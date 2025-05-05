import cv2
import numpy as np
import math
import os


def run_multi_dial_reader(image_path):
    image_filename = image_path
    output_filename = 'detected_all_readings_refined.png'

    if not os.path.exists(image_filename):
        print(f"\u9519\u8bef\uff1a\u627e\u4e0d\u5230\u56fe\u50cf\u6587\u4ef6 '{image_filename}'\u3002")
        return None

    hough_dp = 1.2
    hough_minDist = 40
    hough_param1 = 100
    hough_param2 = 35
    hough_minRadius = 15
    hough_maxRadius = 40

    img = cv2.imread(image_filename)
    if img is None:
        print(f"\u9519\u8bef\uff1a\u65e0\u6cd5\u52a0\u8f7d\u56fe\u50cf '{image_filename}'\u3002")
        return None

    output_image = img.copy()
    height, width = img.shape[:2]
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    blurred = cv2.GaussianBlur(gray, (9, 9), 2)

    circles = cv2.HoughCircles(blurred, cv2.HOUGH_GRADIENT, dp=hough_dp, minDist=hough_minDist,
                               param1=hough_param1, param2=hough_param2,
                               minRadius=hough_minRadius, maxRadius=hough_maxRadius)

    if circles is None:
        print("\u672a\u68c0\u6d4b\u5230\u4efb\u4f55\u5706\u3002")
        return None

    circles = np.uint16(np.around(circles))
    left_side_circles = []
    right_side_circles = []
    for i in circles[0, :]:
        center_x = i[0]
        if center_x < width * 0.45:
            left_side_circles.append(i)
        elif center_x > width * 0.55:
            right_side_circles.append(i)

    left_side_circles.sort(key=lambda c: c[1])
    right_side_circles.sort(key=lambda c: c[1])

    left_dial_labels = ["x10000", "x1000", "x100", "x10"]
    right_dial_labels = ["x0.1", "x0.01", "x0.001", "x0.0001"]

    if len(left_side_circles) != 4:
        left_dial_labels = [f"LDial_{{j}}" for j in range(len(left_side_circles))]

    if len(right_side_circles) != 4:
        right_dial_labels = [f"RDial_{{j}}" for j in range(len(right_side_circles))]

    def process_circle(circle_info, label):
        cx, cy, r = circle_info[0], circle_info[1], circle_info[2]
        roi_margin = int(r * 0.1)
        x1 = max(0, cx - r - roi_margin)
        y1 = max(0, cy - r - roi_margin)
        x2 = min(width, cx + r + roi_margin)
        y2 = min(height, cy + r + roi_margin)
        roi = gray[y1:y2, x1:x2]
        roi_center_x = cx - x1
        roi_center_y = cy - y1
        thresh_roi = cv2.adaptiveThreshold(roi, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
                                           cv2.THRESH_BINARY_INV, blockSize=15, C=0)
        kernel = np.ones((5, 5), np.uint8)
        thresh_roi = cv2.morphologyEx(thresh_roi, cv2.MORPH_OPEN, kernel, iterations=1)
        thresh_roi = cv2.morphologyEx(thresh_roi, cv2.MORPH_CLOSE, kernel, iterations=1)
        contours, _ = cv2.findContours(thresh_roi, cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)
        pointer_contour = None
        pointer_tip = None
        max_contour_length = 0
        for cnt in contours:
            area = cv2.contourArea(cnt)
            if area < 50 or area > (r * r * 0.8): continue
            M = cv2.moments(cnt)
            if M["m00"] == 0: continue
            cnt_cx = int(M["m10"] / M["m00"])
            cnt_cy = int(M["m01"] / M["m00"])
            dist_to_center = math.sqrt((cnt_cx - roi_center_x) ** 2 + (cnt_cy - roi_center_y) ** 2)
            if dist_to_center > r * 0.6: continue
            farthest_point = None
            max_dist_sq = 0
            for point in cnt[:, 0, :]:
                dist_sq = (point[0] - roi_center_x) ** 2 + (point[1] - roi_center_y) ** 2
                if dist_sq > max_dist_sq:
                    max_dist_sq = dist_sq
                    farthest_point = tuple(point)
            current_length = math.sqrt(max_dist_sq)
            if farthest_point is not None and current_length > max_contour_length:
                if current_length > dist_to_center * 1.5:
                    max_contour_length = current_length
                    pointer_contour = cnt
                    pointer_tip = farthest_point

        final_reading = -1
        if pointer_contour is not None and pointer_tip is not None:
            dy = pointer_tip[1] - roi_center_y
            dx = pointer_tip[0] - roi_center_x
            angle_rad = math.atan2(dy, dx)
            angle_deg = math.degrees(angle_rad)
            adjusted_degrees = angle_deg + 90
            if adjusted_degrees < 0:
                adjusted_degrees += 360
            adjusted_degrees %= 360
            degrees_per_unit = 36.0
            final_reading = int(((adjusted_degrees + (degrees_per_unit / 2)) % 360) / degrees_per_unit)
        return final_reading

    all_readings = {}
    for idx, circle_info in enumerate(left_side_circles):
        current_label = left_dial_labels[idx] if idx < len(left_dial_labels) else f"LDial_{{idx}}"
        reading = process_circle(circle_info, current_label)
        all_readings[current_label] = reading

    for idx, circle_info in enumerate(right_side_circles):
        current_label = right_dial_labels[idx] if idx < len(right_dial_labels) else f"RDial_{{idx}}"
        reading = process_circle(circle_info, current_label)
        all_readings[current_label] = reading

    left_decimal_value = 0
    right_decimal_value = 0
    left_success = len(left_side_circles) == 4
    right_success = len(right_side_circles) == 4

    if left_success:
        left_multipliers = [1, 10, 100, 1000]
        for i, label in enumerate(left_dial_labels):
            if all_readings.get(label) is not None:
                left_decimal_value += all_readings[label] * left_multipliers[i]
            else:
                left_success = False
                break

    if right_success:
        right_multipliers = [0.1, 0.01, 0.001, 0.0001]
        for i, label in enumerate(right_dial_labels):
            if all_readings.get(label) is not None:
                right_decimal_value += all_readings[label] * right_multipliers[i]
            else:
                right_success = False
                break

    if left_success and right_success:
        final_reading = left_decimal_value + right_decimal_value
        return final_reading
    else:
        return None
