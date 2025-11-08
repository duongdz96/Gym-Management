import tkinter as tk
from tkinter import messagebox
import cv2
import numpy as np
from database import (get_all_active_members, update_member_face_data,
                      get_all_active_employees, update_employee_face_data)
import threading

class FaceCaptureApp:
    def __init__(self, root):
        self.root = root
        self.root.title("Add Face Data")
        self.root.geometry("400x450")

        self.user_type = tk.StringVar(value="member")  # Default to member
        self.selected_id = None
        self.users = []

        # Radio buttons for user type
        tk.Label(root, text="Select User Type:").pack(pady=5)
        tk.Radiobutton(root, text="Member", variable=self.user_type, value="member", command=self.load_users).pack()
        tk.Radiobutton(root, text="Employee", variable=self.user_type, value="employee", command=self.load_users).pack()

        # Search entry
        tk.Label(root, text="Search by name:").pack(pady=5)
        self.search_var = tk.StringVar()
        self.search_entry = tk.Entry(root, textvariable=self.search_var)
        self.search_entry.pack(pady=5)
        self.search_entry.bind("<KeyRelease>", self.filter_users)

        # Listbox for users
        self.listbox = tk.Listbox(root, height=10)
        self.listbox.pack(pady=5, fill=tk.BOTH, expand=True)

        # Select button
        tk.Button(root, text="Select User", command=self.select_user).pack(pady=5)

        # Start capture button
        self.capture_button = tk.Button(root, text="Start Face Capture", command=self.start_capture, state=tk.DISABLED)
        self.capture_button.pack(pady=5)

        self.load_users()

    def load_users(self):
        if self.user_type.get() == "member":
            self.users = get_all_active_members()
        else:
            self.users = get_all_active_employees()
        self.update_listbox()

    def update_listbox(self):
        self.listbox.delete(0, tk.END)
        for user_id, name in self.users:
            self.listbox.insert(tk.END, f"{name} (ID: {user_id})")

    def filter_users(self, event=None):
        search_term = self.search_var.get().lower()
        self.listbox.delete(0, tk.END)
        for user_id, name in self.users:
            if search_term in name.lower():
                self.listbox.insert(tk.END, f"{name} (ID: {user_id})")

    def select_user(self):
        selection = self.listbox.curselection()
        if selection:
            selected_text = self.listbox.get(selection[0])
            # Extract ID from text
            user_id = int(selected_text.split("(ID: ")[1].rstrip(")"))
            self.selected_id = user_id
            user_type = self.user_type.get()
            messagebox.showinfo("Selected", f"Selected {user_type} ID: {user_id}")
            self.capture_button.config(state=tk.NORMAL)
        else:
            messagebox.showwarning("No Selection", "Please select a user first.")

    def start_capture(self):
        if self.selected_id:
            user_type = self.user_type.get()
            threading.Thread(target=self.capture_faces, args=(self.selected_id, user_type)).start()
        else:
            messagebox.showerror("Error", "No user selected.")

    def capture_faces(self, user_id, user_type):
        video = cv2.VideoCapture(0, cv2.CAP_DSHOW)
        video.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
        video.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)
        video.set(cv2.CAP_PROP_FPS, 30)

        facedetect = cv2.CascadeClassifier('data/haarcascade_frontalface_default.xml')
        faces_data = []
        i = 0

        window_title = f"{user_type.capitalize()} Face Capture"
        while True:
            ret, frame = video.read()
            if not ret:
                continue
            frame = cv2.flip(frame, 1)
            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            faces = facedetect.detectMultiScale(gray, 1.3, 5)

            for (x, y, w, h) in faces:
                crop_img = frame[y:y+h, x:x+w, :]
                resized_img = cv2.resize(crop_img, (50, 50))
                if len(faces_data) < 100 and i % 10 == 0:
                    faces_data.append(resized_img)
                i += 1
                count = len(faces_data)
                cv2.putText(frame, str(count), (50, 50), cv2.FONT_HERSHEY_COMPLEX, 1, (0, 255, 0), 1)
                cv2.rectangle(frame, (x, y), (x+w, y+h), (0, 255, 0), 1)

            cv2.imshow(window_title, frame)
            k = cv2.waitKey(1)
            if k == ord('q') or len(faces_data) == 100:
                break

        video.release()
        cv2.destroyAllWindows()

        if len(faces_data) == 100:
            faces_data = np.asarray(faces_data)
            faces_data = faces_data.reshape(len(faces_data), -1)
            if user_type == "member":
                update_member_face_data(user_id, faces_data)
            else:
                update_employee_face_data(user_id, faces_data)
            messagebox.showinfo("Success", f"Face data added for {user_type} ID: {user_id}")
        else:
            messagebox.showwarning("Cancelled", "Less than 100 faces collected.")

if __name__ == "__main__":
    root = tk.Tk()
    app = FaceCaptureApp(root)
    root.mainloop()