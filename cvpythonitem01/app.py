import os

from flask import Flask, request, jsonify
from werkzeug.utils import secure_filename

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
        # 方法一：使用 OCR + 指针简化识别
        digital_result, pointer_result, _ = read_water_meter(filepath)

        # 将 pointer_result 转换为可序列化格式
        serializable_pointer_result = {}
        if pointer_result:
            for k, v in pointer_result.items():
                if isinstance(k, tuple):
                    # 将元组键转换为字符串
                    key = f"表盘_{k[0]}_{k[1]}"
                else:
                    key = str(k)
                serializable_pointer_result[key] = v

        if digital_result:
            return jsonify({
                "code": 200,
                "data": {
                    "method": "ocr_and_simple_pointer",
                    "digital_result": digital_result,
                    "pointer_readings": serializable_pointer_result
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


if __name__ == '__main__':
    app.run(debug=True)
