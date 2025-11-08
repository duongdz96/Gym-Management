import cv2
import numpy as np
from database import get_all_active_members, update_member_face_data

def select_member():
    members = get_all_active_members()
    if not members:
        print("No active members found.")
        return None

    search_term = input("Search by name (leave empty for all): ").lower()
    filtered_members = [(id, name) for id, name in members if search_term in name.lower()] if search_term else members

    if not filtered_members:
        print("No members match the search.")
        return None

    print("Matching Members:")
    for i, (id, name) in enumerate(filtered_members):
        print(f"{i+1}. {name} (ID: {id})")

    while True:
        try:
            choice = int(input("Enter the number of the member to add face data: ")) - 1
            if 0 <= choice < len(filtered_members):
                return filtered_members[choice][0]  # Return member_id
            else:
                print("Invalid choice. Try again.")
        except ValueError:
            print("Please enter a number.")

video = cv2.VideoCapture(0, cv2.CAP_DSHOW)  # DirectShow backend for Windows
video.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
video.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)
video.set(cv2.CAP_PROP_FPS, 30)

facedetect=cv2.CascadeClassifier('data/haarcascade_frontalface_default.xml')

member_id = select_member()
if member_id is None:
    exit()

faces_data=[]

i=0

print(f"Starting face capture for member ID: {member_id}")
print("Press 'q' to quit or wait for 100 faces.")

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
    for (x,y,w,h) in faces:
        crop_img=frame[y:y+h, x:x+w, :]
        resized_img=cv2.resize(crop_img, (50,50))
        if len(faces_data)<100 and i%10==0:
            faces_data.append(resized_img)
        i=i+1
        count = len(faces_data)
        if count > 0:
            cv2.putText(frame, str(count), (50,50), cv2.FONT_HERSHEY_COMPLEX, 1, (0,255,0), 1)
        cv2.rectangle(frame, (x,y), (x+w, y+h), (0,255,0), 1)
    cv2.imshow("Frame",frame)
    k=cv2.waitKey(1)
    if k==ord('q') or len(faces_data)==100:
        break
video.release()
cv2.destroyAllWindows()

if len(faces_data) == 100:
    faces_data=np.asarray(faces_data)
    faces_data=faces_data.reshape(len(faces_data), -1)
    update_member_face_data(member_id, faces_data)
    print(f"Face data added for member ID: {member_id}")
else:
    print("Addition cancelled, less than 100 faces collected.")