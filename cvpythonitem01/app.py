import os
import cv2

from flask import Flask, request, jsonify
from werkzeug.utils import secure_filename

from service.electricity_meter_reader import electricity_readings
from service.multi_dial_reader import run_multi_dial_reader
from service.water_meter_reader import read_water_meter

# 导入封装好的函数


app = Flask(__name__)
UPLOAD_FOLDER = 'uploads'
os.makedirs(UPLOAD_FOLDER, exist_ok=True)


@app.route('/api/read_water_meter', methods=['POST'])
def read_water_meter_api():
    if 'image' not in request.files:
        return jsonify({"code": 400, "data": "缺少图像文件", "success": False})

    file = request.files['image']
    filename = secure_filename(file.filename)
    filepath = os.path.join(UPLOAD_FOLDER, filename)
    file.save(filepath)

    try:
        # 调用修改后的水表读数识别函数
        final_reading, final_image, digital_result, pointer_result = read_water_meter(filepath)

        if final_reading:
            if digital_result:  # 有数字部分说明是数字型水表
                integer_part = digital_result
                decimal_part = pointer_result if pointer_result else "0"
                return jsonify({
                    "code": 200,
                    "data": {
                        "method": "combined_meter_reading",
                        "reading": final_reading,
                        "digital_part": integer_part,
                        "pointer_part": decimal_part
                    },
                    "success": True
                })

        # 方法二：fallback 到多指针圆盘识别
        result = run_multi_dial_reader(filepath)
        return jsonify({
            "code": 200,
            "data": {
                "method": "pointer_multi",
                "reading": result
            },
            "success": True
        })
    except Exception as e:
        return jsonify({"code": 500, "data": f"识别失败: {str(e)}", "success": False})


@app.route('/api/read_electricity_meter', methods=['POST'])
def read_electricity_meter_api():
    if 'image' not in request.files:
        return jsonify({"code": 400, "data": "缺少图像文件", "success": False})

    file = request.files['image']
    filename = secure_filename(file.filename)
    filepath = os.path.join(UPLOAD_FOLDER, filename)
    file.save(filepath)

    try:
        image = cv2.imread(filepath)
        if image is None:
            raise ValueError("图像无法加载，可能不是有效的图片文件")
        final_value = electricity_readings(image)
        return jsonify({
            "code": 200,
            "data": {
                "method": "electricity_meter",
                "reading": final_value
            },
            "success": True
        })
    except Exception as e:
        return jsonify({"code": 500, "data": f"识别失败: {str(e)}", "success": False})


if __name__ == '__main__':
    app.run(debug=True)
