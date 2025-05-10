import math

import cv2
import matplotlib.pyplot as plt
import numpy as np
import pytesseract

# --- 数字盘识别参数 ---
GAUSSIAN_BLUR_KERNEL_SIZE = (5, 5)
ADAPTIVE_THRESH_BLOCK_SIZE = 11
ADAPTIVE_THRESH_C = 40
MORPH_KERNEL_WIDTH = 15
MORPH_KERNEL_HEIGHT = 5
MIN_CONTOUR_AREA = 800
MAX_CONTOUR_AREA = 2000
MIN_ASPECT_RATIO = 6.5
MAX_ASPECT_RATIO = 8.5


def recognize_digital_display(image):
    """
    识别水表上的数字显示部分

    Args:
        image: 输入图像

    Returns:
        tuple: (ocr_result, output_image) - OCR结果和标记后的图像
    """
    original_image = image.copy()
    output_image = original_image.copy()
    output_image_debug = original_image.copy()

    # 预处理
    gray_image = cv2.cvtColor(original_image, cv2.COLOR_BGR2GRAY)
    blurred_image = cv2.GaussianBlur(gray_image, GAUSSIAN_BLUR_KERNEL_SIZE, 0)

    # 自适应阈值处理
    thresh_image = cv2.adaptiveThreshold(blurred_image, 255,
                                         cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
                                         cv2.THRESH_BINARY_INV,
                                         ADAPTIVE_THRESH_BLOCK_SIZE,
                                         ADAPTIVE_THRESH_C)

    # 形态学闭运算
    kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (MORPH_KERNEL_WIDTH, MORPH_KERNEL_HEIGHT))
    closed_image = cv2.morphologyEx(thresh_image, cv2.MORPH_CLOSE, kernel)

    # 查找轮廓
    contours, hierarchy = cv2.findContours(closed_image, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    print(f"形态学处理后找到 {len(contours)} 个轮廓。")

    # 筛选轮廓
    found_target = False
    target_bbox = None

    for i, contour in enumerate(contours):
        area = cv2.contourArea(contour)
        (x, y, w, h) = cv2.boundingRect(contour)
        aspect_ratio = float(w) / h if h > 0 else 0

        # 在调试图像上绘制轮廓
        color = (np.random.randint(50, 200), np.random.randint(50, 200), np.random.randint(50, 200))
        cv2.rectangle(output_image_debug, (x, y), (x + w, y + h), color, 1)
        cv2.putText(output_image_debug, f"{i}", (x, y - 2), cv2.FONT_HERSHEY_SIMPLEX, 0.4, color, 1)

        # 应用筛选条件
        if MIN_CONTOUR_AREA <= area <= MAX_CONTOUR_AREA:
            if MIN_ASPECT_RATIO <= aspect_ratio <= MAX_ASPECT_RATIO:
                print(
                    f"轮廓 {i}: 面积={area:.1f}, 位置=({x},{y}), 尺寸=({w}x{h}), 长宽比={aspect_ratio:.2f} --> 符合条件!")
                if not found_target:
                    target_bbox = (x, y, w, h)
                    cv2.rectangle(output_image_debug, (x, y), (x + w, y + h), (0, 255, 0), 3)
                found_target = True

    # OCR识别
    ocr_result = None
    if found_target and target_bbox:
        print("\n基于合并数字区域找到目标，准备进行 OCR。")
        (x, y, w, h) = target_bbox

        # 裁剪ROI
        padding = 1
        roi_x = max(0, x - padding)
        roi_y = max(0, y - padding)
        roi_w = w + (2 * padding)
        roi_h = h + (2 * padding)

        # 确保坐标有效
        img_h, img_w = gray_image.shape[:2]
        if roi_x + roi_w > img_w: roi_w = img_w - roi_x
        if roi_y + roi_h > img_h: roi_h = img_h - roi_y

        if roi_w > 0 and roi_h > 0:
            roi = gray_image[roi_y:roi_y + roi_h, roi_x:roi_x + roi_w]

            # 预处理ROI
            scale_factor = 3
            if roi.shape[0] > 0 and roi.shape[1] > 0:
                roi_resized = cv2.resize(roi, (roi_w * scale_factor, roi_h * scale_factor),
                                         interpolation=cv2.INTER_CUBIC)
                _, roi_thresh = cv2.threshold(roi_resized, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)
                roi_processed = roi_thresh

                # 调用Tesseract
                custom_config = r'--oem 3 --psm 8 -c tessedit_char_whitelist=0123456789'
                try:
                    text = pytesseract.image_to_string(roi_processed, config=custom_config)
                    ocr_result = "".join(filter(str.isdigit, text))
                    print(f"\nOCR 识别结果: '{ocr_result}' (原始输出: '{text.strip()}')")

                    # 在输出图像上绘制结果
                    padding = 3
                    cv2.rectangle(output_image,
                                  (x - padding, y - padding),
                                  (x + w + padding, y + h + padding),
                                  (0, 255, 0), 2)
                    cv2.putText(output_image, f"OCR: {ocr_result}", (x, y - 10),
                                cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)

                except Exception as e:
                    print(f"\nOCR 识别时发生错误: {e}")

    return ocr_result, output_image


def detect_dial_readings(image):
    """
    检测表盘指针并读取数值

    Args:
        image: 输入图像

    Returns:
        tuple: (readings, output_image) - 检测到的读数和标记后的图像
    """
    output_image = image.copy()
    readings = {}

    # 转为灰度图
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    gray_blurred = cv2.GaussianBlur(gray, (9, 9), 2)

    # 圆形检测
    circles = cv2.HoughCircles(
        gray_blurred,
        cv2.HOUGH_GRADIENT,
        dp=1.2,
        minDist=50,
        param1=100,
        param2=35,
        minRadius=20,
        maxRadius=45
    )

    if circles is None:
        print("未检测到圆形表盘。请调整 HoughCircles 参数。")
        return None, output_image

    circles = np.uint16(np.around(circles[0, :]))
    print(f"检测到 {len(circles)} 个候选圆。")

    # 处理每个检测到的圆
    dial_count = 0
    for i, (cx, cy, r) in enumerate(circles):
        dial_count += 1
        print(f"\n--- 处理表盘 {dial_count} (Center: ({cx},{cy}), Radius: {r}) ---")

        # ��取ROI
        roi_margin = int(r * 0.1)
        x_start = max(0, cx - r - roi_margin)
        y_start = max(0, cy - r - roi_margin)
        x_end = min(image.shape[1], cx + r + roi_margin)
        y_end = min(image.shape[0], cy + r + roi_margin)

        dial_roi = image[y_start:y_end, x_start:x_end]
        if dial_roi.size == 0:
            print("  错误：无法提取有效的 ROI。")
            continue

        # ROI中心坐标
        roi_center_x = cx - x_start
        roi_center_y = cy - y_start
        roi_center = (roi_center_x, roi_center_y)

        # 检测指针（红色阈值）
        hsv_roi = cv2.cvtColor(dial_roi, cv2.COLOR_BGR2HSV)

        # HSV红色范围
        lower_red1 = np.array([0, 100, 70])
        upper_red1 = np.array([10, 255, 255])
        lower_red2 = np.array([170, 100, 70])
        upper_red2 = np.array([180, 255, 255])

        mask1 = cv2.inRange(hsv_roi, lower_red1, upper_red1)
        mask2 = cv2.inRange(hsv_roi, lower_red2, upper_red2)
        red_mask = cv2.bitwise_or(mask1, mask2)

        # 形态学处理
        kernel = np.ones((3, 3), np.uint8)
        red_mask_cleaned = cv2.morphologyEx(red_mask, cv2.MORPH_OPEN, kernel, iterations=1)
        red_mask_cleaned = cv2.morphologyEx(red_mask_cleaned, cv2.MORPH_CLOSE, kernel, iterations=1)

        # 查找指针轮廓
        contours, _ = cv2.findContours(red_mask_cleaned, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

        best_pointer_contour = None
        max_area = 0
        min_pointer_area = 10

        if not contours:
            print("  在 ROI 中未找到红色轮廓。")
        else:
            for cnt in contours:
                area = cv2.contourArea(cnt)
                if area > max_area and area > min_pointer_area:
                    max_area = area
                    best_pointer_contour = cnt

        if best_pointer_contour is None:
            print("  未找到合适的指针轮廓。")
            cv2.circle(output_image, (cx, cy), r, (255, 0, 255), 2)
            cv2.putText(output_image, "?", (cx - r // 2, cy + r // 2), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 0, 255), 2)
            continue

        # 查找指针尖端
        max_dist_sq = 0
        pointer_tip = None
        for point in best_pointer_contour.reshape(-1, 2):
            dist_sq = (point[0] - roi_center_x) ** 2 + (point[1] - roi_center_y) ** 2
            if dist_sq > max_dist_sq:
                max_dist_sq = dist_sq
                pointer_tip = tuple(point)

        if pointer_tip is None:
            print("  无法确定指针尖端。")
            cv2.circle(output_image, (cx, cy), r, (255, 0, 255), 2)
            cv2.putText(output_image, "?", (cx - r // 2, cy + r // 2), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 0, 255), 2)
            continue

        # 计算角度
        delta_x = pointer_tip[0] - roi_center_x
        delta_y = pointer_tip[1] - roi_center_y

        angle_rad = math.atan2(-delta_y, delta_x)
        angle_deg = math.degrees(angle_rad)
        angle_normalized = (90 - angle_deg) % 360
        if angle_normalized < 0:
            angle_normalized += 360

        print(f"  计算得到的指针角度 (0=上, 顺时针): {angle_normalized:.2f} 度")

        # 映射角度到数字
        sector_angle = 360 / 10
        angle_tolerance = 15

        reading = 0

        if angle_normalized > angle_tolerance and angle_normalized < (360 - angle_tolerance):
            reading = int(math.floor(angle_normalized / sector_angle))

        print(f"  角度: {angle_normalized:.2f}, 公差调整后的读数: {reading}")

        # 存储读数
        readings[(cx, cy)] = reading

        # 在输出图像上绘制结果
        cv2.circle(output_image, (cx, cy), r, (0, 255, 0), 2)
        line_length = r * 0.9
        end_x = int(cx + line_length * math.sin(math.radians(angle_normalized)))
        end_y = int(cy - line_length * math.cos(math.radians(angle_normalized)))
        cv2.line(output_image, (cx, cy), (end_x, end_y), (255, 0, 0), 2)
        cv2.putText(output_image, str(reading), (cx + r // 3, cy + r // 2), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255),
                    2)

    return readings, output_image


def read_water_meter(image_path):
    """
    读取水表图片，识别数字显示部分和指针刻度，返回组合后的最终读数

    Args:
        image_path: 图像路径

    Returns:
        tuple: (final_reading, final_image)
            - final_reading: 组合后的最终读数（字符串格式，如"123.4567"）
            - final_image: 标记了识别结果的输出图像
    """
    # 加载图像
    image = cv2.imread(image_path)
    if image is None:
        print(f"错误：无法加载图片 {image_path}")
        return None, None

    final_image = image.copy()

    # 1. 识别数字显示部分（整数部分）
    print("\n=== 开始识别数字显示部分 ===")
    digital_result, digital_image = recognize_digital_display(image)

    # 2. 识别指针刻度（小数部分）
    print("\n=== 开始识别指针刻度 ===")
    pointer_readings, pointer_image = detect_dial_readings(image)

    # 3. 合并结果到最终图像
    h, w = image.shape[:2]
    final_image[:h // 2, :] = digital_image[:h // 2, :]
    final_image[h // 2:, :] = pointer_image[h // 2:, :]

    # 4. 组合最终读数
    final_reading = None
    integer_part = digital_result if digital_result else "0"

    decimal_part = ""
    decimal_value = 0

    if pointer_readings:
        # 按X坐标排序指针读数 (从右到左)
        sorted_centers = sorted(pointer_readings.keys(), key=lambda k: k[0], reverse=True)

        # 从指针读数获取小数部分
        for i, center in enumerate(sorted_centers):
            reading = pointer_readings[center]
            decimal_part += str(reading)

        # 计算小数部分（使用加权值更精确）
        weights = [0.1, 0.01, 0.001, 0.0001]
        for i, center in enumerate(sorted_centers):
            if i < len(weights):
                decimal_value += pointer_readings[center] * weights[i]

    # 5. 组合整数和小数部分
    if decimal_part:
        final_reading = f"{integer_part}.{decimal_part}"
    else:
        final_reading = integer_part

    # 计算最终数值（整数 + 小数值）
    final_numeric_value = float(integer_part) + decimal_value

    # 6. 在图像上添加最终结果
    cv2.putText(final_image, f"数字显示读数: {integer_part}",
                (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)

    if decimal_part:
        cv2.putText(final_image, f"指针读数: 0.{decimal_part}",
                    (10, 60), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 0, 0), 2)
    else:
        cv2.putText(final_image, "指针读数: 未检测到",
                    (10, 60), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 0, 0), 2)

    # 添加最终水表读数
    cv2.putText(final_image, f"最终水表读数: {final_numeric_value:.4f}",
                (10, 90), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0, 255, 0), 2)

    return final_reading, final_image, digital_result, decimal_part


if __name__ == "__main__":
    image_file = 'img/25.png'  # 请确保路径正确

    # 读取水表
    digital_result, pointer_readings, final_image = read_water_meter(image_file)

    if final_image is not None:
        # 显示读数结果
        print("\n=== 最终读数结果 ===")
        print(f"数字显示部分: {digital_result if digital_result else '未检测到'}")

        if pointer_readings:
            # 从右到左排序 (X坐标降序)
            sorted_centers = sorted(pointer_readings.keys(), key=lambda k: k[0], reverse=True)
            pointer_result_str = ""
            print("指针读数 (按从右到左排序):")

            # 设置小数位权重 (从右到左: 0.1, 0.01, 0.001, 0.0001)
            weights = [0.1, 0.01, 0.001, 0.0001]
            pointer_result_weighted_sum = 0

            for i, center in enumerate(sorted_centers):
                reading = pointer_readings[center]

                # 获取当前位置的权重，确保不超出列表范围
                weight = weights[i] if i < len(weights) else 0
                weighted_value = reading * weight

                print(f"  位置 ({center[0]}, {center[1]}): {reading} × {weight} = {weighted_value:.4f}")
                pointer_result_str += str(reading)

                # 累加加权值
                pointer_result_weighted_sum += weighted_value

            print(f"指针读数(从右到左): {pointer_result_str}")
            print(f"指针总读数(加权和): {pointer_result_weighted_sum:.4f}")
        else:
            print("未检测到有效的指针读数。")

        # 显示最终图像
        plt.figure(figsize=(12, 10))
        plt.imshow(cv2.cvtColor(final_image, cv2.COLOR_BGR2RGB))
        plt.title('水表读数识别结果')
        plt.axis('off')
        plt.tight_layout()
        plt.show()

        # 也可以使用OpenCV显示
        cv2.imshow('水表读数识别结果', final_image)
        cv2.waitKey(0)
        cv2.destroyAllWindows()
    else:
        print("处理失败，请检查输入图像路径。")
