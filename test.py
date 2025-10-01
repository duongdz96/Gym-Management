from sklearn.neighbors import KNeighborsClassifier
import cv2
import pickle
import numpy as np
import os
import csv
import time
from datetime import datetime
from database import get_all_faces_and_labels, get_member_status, log_access, get_member_name

from win32com.client import Dispatch

def speak(str1):
    speak=Dispatch(("SAPI.SpVoice"))
    speak.Speak(str1)

video=cv2.VideoCapture(0)
facedetect=cv2.CascadeClassifier('data/haarcascade_frontalface_default.xml')

FACES, LABELS = get_all_faces_and_labels()

print('Shape of Faces matrix --> ', FACES.shape if FACES.size > 0 else 'No active members')

if FACES.size > 0:
    knn=KNeighborsClassifier(n_neighbors=5)
    knn.fit(FACES, LABELS)

last_log_times = {}  # Dictionary to track last log time per member_id
detection_start_times = {}  # Dictionary to track detection start time per member_id
DELAY = 1.5  # Delay in seconds before check-in

while True:
    ret,frame=video.read()
    frame = cv2.flip(frame, 1)  # Flip horizontally to correct mirroring
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
            crop_img=expanded_frame[y:y+h, x:x+w, :]
            resized_img=cv2.resize(crop_img, (50,50)).flatten().reshape(1,-1)
            if FACES.size > 0:
                output=knn.predict(resized_img)
                member_id = output[0]
                display_text = get_member_name(member_id)
            else:
                member_id = None
                display_text = 'Unknown'
            ts=time.time()
            date=datetime.fromtimestamp(ts).strftime("%d-%m-%Y")
            timestamp=datetime.fromtimestamp(ts).strftime("%H:%M-%S")
            cv2.rectangle(expanded_frame, (x,y), (x+w, y+h), (0,0,255), 1)
            cv2.rectangle(expanded_frame,(x,y),(x+w,y+h),(50,50,255),2)
            cv2.rectangle(expanded_frame,(x,y-40),(x+w,y),(50,50,255),-1)
            cv2.putText(expanded_frame, display_text, (x,y-15), cv2.FONT_HERSHEY_COMPLEX, 1, (255,255,255), 1)
            cv2.rectangle(expanded_frame, (x,y), (x+w, y+h), (50,50,255), 1)

            if member_id and display_text != 'Unknown':
                detected_members.add(member_id)
                status = get_member_status(member_id)
                # Prepare overlay with info panel on the right
                overlay_frame = expanded_frame.copy()
                info_lines = [
                    f"ID: {member_id}",
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
                    last_log = last_log_times.get(member_id, 0)
                    if ts - last_log > 300:  # 5 minutes = 300 seconds
                        if member_id not in detection_start_times:
                            detection_start_times[member_id] = ts
                        elif ts - detection_start_times[member_id] >= DELAY:
                            log_access(member_id, f"{date} {timestamp}")
                            last_log_times[member_id] = ts
                            speak("Attendance Taken..")
                            del detection_start_times[member_id]  # Reset after log

                            # Display overlay for 2 seconds
                            start_overlay = time.time()
                            while time.time() - start_overlay < 2:
                                cv2.imshow("Frame", overlay_frame)
                                if cv2.waitKey(1) == ord('q'):
                                    break
                            continue  # Skip to next loop iteration after overlay
                else:
                    speak("Access denied. Member is inactive.")

                    # Display overlay for inactive status for 2 seconds
                    start_overlay = time.time()
                    while time.time() - start_overlay < 2:
                        cv2.imshow("Frame", overlay_frame)
                        if cv2.waitKey(1) == ord('q'):
                            break
                    time.sleep(1)
                    continue
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

