import cv2
import pytesseract
import re


# 读取图像
def electricity_readings(image):
    if image is None:
        print("图像读取失败，请检查路径。")
        exit()

    height, width = image.shape[:2]

    # 图像预处理
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    gray = cv2.convertScaleAbs(gray, alpha=1.8, beta=30)  # 增强对比度和亮度
    gray = cv2.medianBlur(gray, 3)  #去噪
    thresh = cv2.adaptiveThreshold(
        gray, 255,
        cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
        cv2.THRESH_BINARY_INV,
        blockSize=15,
        C=10
    )

    # OCR识别
    custom_config = r'--oem 3 --psm 6'
    text_gray = pytesseract.image_to_string(gray, config=custom_config)
    text_thresh = pytesseract.image_to_string(thresh, config=custom_config)
    text = (text_gray + '\n' + text_thresh).lower()

    # print("【OCR识别出的全部文本】：\n")
    # print(text)

    # 提取带单位或上下文信息
    unit_matches = re.findall(r'(\d+\.\d+)\s*(kwh|kw|kw·h|度)', text)
    context_matches = re.findall(r'(电量|总电量|读数|总读数|当前读数)[^\d]{0,5}(\d+\.\d+)', text)
    all_numbers = re.findall(r'\d+\.\d+', text)

    # 候选值提取（含高度和中心距离）
    data = pytesseract.image_to_data(gray, config=custom_config, output_type=pytesseract.Output.DICT)
    candidate_values = []
    for i in range(len(data['text'])):
        word = data['text'][i].strip().lower()
        if re.fullmatch(r'\d+\.\d+', word):
            try:
                val = float(word)
                if 0 < val < 99999:
                    x, y, w, h = data['left'][i], data['top'][i], data['width'][i], data['height'][i]
                    cx, cy = x + w / 2, y + h / 2
                    dist_to_center = ((cx - width / 2) ** 2 + (cy - height / 2) ** 2) ** 0.5
                    candidate_values.append((word, val, dist_to_center, h))
            except:
                continue

    # 结果判断优先级
    final_value = None

    if unit_matches:
        final_value = unit_matches[0][0]

    elif context_matches:
        final_value = context_matches[0][1]

    elif candidate_values:
        # 加权排序：靠中心 + 字体高 + 数值大
        candidate_values.sort(key=lambda x: (x[2] - 0.5 * x[3], -x[1]))
        final_value = candidate_values[0][0]
        strategy = "图像中心+字体高度优先"
    elif all_numbers:
        final_value = max(all_numbers, key=lambda x: float(x))

    # 输出最终识别
    if final_value:
        print(f"最终识别电表数值：{final_value} kWh")
        return final_value
    else:
        print("无法识别出电表读数")
        return None
