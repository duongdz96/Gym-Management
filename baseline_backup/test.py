from sklearn.neighbors import KNeighborsClassifier
import cv2
import pickle
import numpy as np
import os
import csv
import time
from datetime import datetime
from database import (get_all_faces_and_labels, get_member_status, log_access, get_member_name, update_access_log,
                       get_all_employee_faces_and_labels, get_employee_status, log_employee_access, get_employee_name, get_last_employee_check_type)

from win32com.client import Dispatch

def speak(str1):
    speak=Dispatch(("SAPI.SpVoice"))
    speak.Speak(str1)

video=cv2.VideoCapture(0, cv2.CAP_DSHOW)  # DirectShow backend for Windows
video.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
video.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)
video.set(cv2.CAP_PROP_FPS, 30)
facedetect=cv2.CascadeClassifier('data/haarcascade_frontalface_default.xml')

MEMBER_FACES, MEMBER_LABELS = get_all_faces_and_labels()
EMPLOYEE_FACES, EMPLOYEE_LABELS = get_all_employee_faces_and_labels()

ALL_FACES = []
ALL_LABELS = []

if MEMBER_FACES.size > 0:
    ALL_FACES.append(MEMBER_FACES)
    ALL_LABELS.extend(MEMBER_LABELS)

if EMPLOYEE_FACES.size > 0:
    ALL_FACES.append(EMPLOYEE_FACES)
    ALL_LABELS.extend(EMPLOYEE_LABELS)

if ALL_FACES:
    FACES = np.concatenate(ALL_FACES)
    LABELS = ALL_LABELS
    knn = KNeighborsClassifier(n_neighbors=5)
    knn.fit(FACES, LABELS)
    print('Shape of Faces matrix --> ', FACES.shape)
else:
    FACES = np.array([])
    LABELS = []
    print('No active members or employees')

last_log_times = {}  # Dictionary to track last log time per member_id
last_log_ids = {}  # Dictionary to track last log id per member_id
detection_start_times = {}  # Dictionary to track detection start time per member_id
last_unknown_time = 0  # Track last time unknown was detected
DELAY = 1.5  # Delay in seconds before check-in

while True:
    ret,frame=video.read()
    if not ret:
        continue
    frame = cv2.flip(frame, 1)  # Flip horizontally to correct mirroring
    # Ensure frame is in color (BGR format)
    if len(frame.shape) == 3 and frame.shape[2] == 3:
        # Frame is already in color, continue normally
        pass
    else:
        # Convert to color if somehow it's grayscale
        frame = cv2.cvtColor(frame, cv2.COLOR_GRAY2BGR)
    gray=cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    faces=facedetect.detectMultiScale(gray, 1.3 ,5)
    height, width = frame.shape[:2]
    expanded_width = width + 300  # Add 300 pixels on the right for info panel
    expanded_frame = np.zeros((height, expanded_width, 3), dtype=np.uint8)
    expanded_frame[:, :width] = frame  # Copy original frame to left side
    detected_members = set()  # Track detected members in this frame
    for (x,y,w,h) in faces:
        # Check if entire face is within the original frame (left side)
        if x > 0 and y > 0 and (x + w) < width and (y + h) < height:
            crop_img=frame[y:y+h, x:x+w, :]  # Crop from original color frame
            resized_img=cv2.resize(crop_img, (50,50)).flatten().reshape(1,-1)
            if FACES.size > 0:
                output = knn.predict(resized_img)
                label = output[0]
                if label.startswith('emp_'):
                    employee_id = int(label[4:])  # Remove 'emp_' prefix
                    display_text = get_employee_name(employee_id)
                    is_employee = True
                else:
                    member_id = int(label)
                    display_text = get_member_name(member_id)
                    is_employee = False
            else:
                display_text = 'Unknown'
                is_employee = False
            ts=time.time()
            date=datetime.fromtimestamp(ts).strftime("%d-%m-%Y")
            timestamp=datetime.fromtimestamp(ts).strftime("%H:%M-%S")
            
            # Determine color based on recognition
            if display_text == 'Unknown':
                color = (0, 0, 255)  # Red for unknown
            else:
                color = (0, 255, 0)  # Green for recognized
            
            cv2.rectangle(expanded_frame, (x,y), (x+w, y+h), color, 1)
            cv2.rectangle(expanded_frame,(x,y),(x+w,y+h), color, 2)
            cv2.rectangle(expanded_frame,(x,y-40),(x+w,y), color, -1)
            cv2.putText(expanded_frame, display_text, (x,y-15), cv2.FONT_HERSHEY_COMPLEX, 1, (255,255,255), 1)
            cv2.rectangle(expanded_frame, (x,y), (x+w, y+h), color, 1)

            if display_text != 'Unknown':
                if is_employee:
                    detected_members.add(f"emp_{employee_id}")
                    status = get_employee_status(employee_id)
                    last_check = get_last_employee_check_type(employee_id)
                    if last_check == 'in':
                        check_type = 'out'
                        action = "Check-Out"
                    else:
                        check_type = 'in'
                        action = "Check-In"
                    info_lines = [
                        f"Employee ID: {employee_id}",
                        f"Name: {display_text}",
                        f"Time: {timestamp}",
                        f"Status: {status}",
                        f"Action: {action}"
                    ]
                else:
                    detected_members.add(member_id)
                    status = get_member_status(member_id)
                    check_type = None
                    info_lines = [
                        f"Member ID: {member_id}",
                        f"Name: {display_text}",
                        f"Time: {timestamp}",
                        f"Status: {status}"
                    ]
                # Prepare overlay with info panel on the right
                overlay_frame = expanded_frame.copy()
                id_to_show = employee_id if is_employee else member_id
                info_lines = [
                    f"ID: {id_to_show}",
                    f"Name: {display_text}",
                    f"Time: {timestamp}",
                    f"Status: {status}"
                ]
                box_x = width + 20  # Position rectangle on the right panel
                box_y = 50
                box_width = 250
                box_height = len(info_lines) * 30 + 20
                cv2.rectangle(overlay_frame, (box_x, box_y), (box_x + box_width, box_y + box_height), (0, 255, 0), 2)
                text_y = box_y + 25
                for line in info_lines:
                    cv2.putText(overlay_frame, line, (box_x + 10, text_y), cv2.FONT_HERSHEY_COMPLEX, 0.7, (255, 255, 255), 1)
                    text_y += 30

                if status == 'active':
                    key = member_id if not is_employee else f"emp_{employee_id}"
                    if key not in detection_start_times:
                        detection_start_times[key] = ts
                    elif ts - detection_start_times[key] >= DELAY:
                        if is_employee:
                            # Employees always log check-in/out
                            log_employee_access(employee_id, check_type, f"{date} {timestamp}")
                            speak(f"Employee {action}..")
                        else:
                            # Members: update within 5 min
                            last_log = last_log_times.get(member_id, 0)
                            if ts - last_log > 300:
                                log_id = log_access(member_id, f"{date} {timestamp}")
                                last_log_ids[member_id] = log_id
                            else:
                                log_id = last_log_ids.get(member_id)
                                if log_id:
                                    update_access_log(log_id, f"{date} {timestamp}")
                            last_log_times[member_id] = ts
                            speak("Attendance Taken..")
                        del detection_start_times[key]  # Reset after log

                        # Display overlay for 2 seconds
                        start_overlay = time.time()
                        while time.time() - start_overlay < 2:
                            cv2.imshow("Frame", overlay_frame)
                            if cv2.waitKey(1) == ord('q'):
                                break
                        continue  # Skip to next loop iteration after overlay
                else:
                    user_type = "Employee" if is_employee else "Member"
                    speak(f"Access denied. {user_type} is inactive.")

                    # Display overlay for inactive status for 2 seconds
                    start_overlay = time.time()
                    while time.time() - start_overlay < 2:
                        cv2.imshow("Frame", overlay_frame)
                        if cv2.waitKey(1) == ord('q'):
                                    break
                    time.sleep(1)
                    continue
            elif display_text == 'Unknown':
                if ts - last_unknown_time >= 3:  # Only show overlay if 3 seconds have passed since last unknown
                    # For unknown faces, display a red info panel
                    overlay_frame = expanded_frame.copy()
                    info_lines = [
                        "Unknown",
                        f"Time: {timestamp}"
                    ]
                    box_x = width + 20  # Position rectangle on the right panel
                    box_y = 50
                    box_width = 250
                    box_height = len(info_lines) * 30 + 20
                    cv2.rectangle(overlay_frame, (box_x, box_y), (box_x + box_width, box_y + box_height), (0, 0, 255), 2)
                    text_y = box_y + 25
                    for line in info_lines:
                        cv2.putText(overlay_frame, line, (box_x + 10, text_y), cv2.FONT_HERSHEY_COMPLEX, 0.7, (0, 0, 255), 1)
                        text_y += 30
                    
                    # Display overlay for unknown for 0.5 seconds
                    start_overlay = time.time()
                    while time.time() - start_overlay < 0.5:
                        cv2.imshow("Frame", overlay_frame)
                        if cv2.waitKey(1) == ord('q'):
                            break
                    last_unknown_time = ts  # Update last unknown time
                    continue  # Skip to next loop iteration after overlay
                # If not enough time has passed, just draw the red box and continue without overlay
        else:
            # Face not fully visible, skip processing
            pass

    # Reset detection start for members not detected in this frame
    for mid in list(detection_start_times.keys()):
        if mid not in detected_members:
            del detection_start_times[mid]

    cv2.imshow("Frame", expanded_frame)
    k=cv2.waitKey(1)
    if k==ord('q'):
        break
video.release()
cv2.destroyAllWindows()

