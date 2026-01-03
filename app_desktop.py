"""
Desktop Application for Face Recognition System
Ứng dụng quản lý nhận diện khuôn mặt cho Gym
"""

import tkinter as tk
from tkinter import ttk, messagebox
import cv2
from PIL import Image, ImageTk
import threading
import hashlib
from datetime import datetime
import numpy as np

# Import các module cần thiết
from database import (
    get_connection, 
    get_all_active_members, 
    get_all_active_employees,
    log_access,
    log_employee_access,
    verify_user_login
)
from database_embeddings import get_all_embeddings
from deepface import DeepFace
from scipy.spatial.distance import cosine
import config


class LoginWindow:
    """Màn hình đăng nhập"""
    
    def __init__(self):
        self.root = tk.Tk()
        self.root.title("Face Recognition System - Login")
        self.root.geometry("500x400")
        self.root.resizable(False, False)
        
        # Center window
        self.center_window()
        
        # Biến lưu user info
        self.logged_user = None
        
        self.setup_ui()
        
    def center_window(self):
        """Căn giữa cửa sổ"""
        self.root.update_idletasks()
        width = self.root.winfo_width()
        height = self.root.winfo_height()
        x = (self.root.winfo_screenwidth() // 2) - (width // 2)
        y = (self.root.winfo_screenheight() // 2) - (height // 2)
        self.root.geometry(f'{width}x{height}+{x}+{y}')
        
    def setup_ui(self):
        """Thiết lập giao diện"""
        # Title
        title_frame = tk.Frame(self.root, bg="#2196F3", height=80)
        title_frame.pack(fill=tk.X)
        
        title_label = tk.Label(
            title_frame,
            text="🏋️ GYM MANAGEMENT SYSTEM",
            font=("Arial", 20, "bold"),
            bg="#2196F3",
            fg="white"
        )
        title_label.pack(pady=20)
        
        # Login form
        form_frame = tk.Frame(self.root, bg="white")
        form_frame.pack(expand=True, fill=tk.BOTH, padx=50, pady=30)
        
        # Email
        tk.Label(
            form_frame,
            text="Email:",
            font=("Arial", 12),
            bg="white"
        ).grid(row=0, column=0, sticky="w", pady=10)
        
        self.email_entry = tk.Entry(
            form_frame,
            font=("Arial", 12),
            width=30
        )
        self.email_entry.grid(row=0, column=1, pady=10, padx=10)
        
        # Password
        tk.Label(
            form_frame,
            text="Password:",
            font=("Arial", 12),
            bg="white"
        ).grid(row=1, column=0, sticky="w", pady=10)
        
        self.password_entry = tk.Entry(
            form_frame,
            font=("Arial", 12),
            width=30,
            show="●"
        )
        self.password_entry.grid(row=1, column=1, pady=10, padx=10)
        
        # Login button
        login_btn = tk.Button(
            form_frame,
            text="LOGIN",
            font=("Arial", 14, "bold"),
            bg="#4CAF50",
            fg="white",
            width=20,
            height=2,
            cursor="hand2",
            command=self.handle_login
        )
        login_btn.grid(row=2, column=0, columnspan=2, pady=30)
        
        # Bind Enter key
        self.root.bind('<Return>', lambda e: self.handle_login())
        
        # Info label
        info_label = tk.Label(
            self.root,
            text="Only RECEPTIONIST or MANAGER can login",
            font=("Arial", 9, "italic"),
            fg="gray"
        )
        info_label.pack(side=tk.BOTTOM, pady=10)
        
    def handle_login(self):
        """Xử lý đăng nhập"""
        email = self.email_entry.get().strip()
        password = self.password_entry.get().strip()
        
        if not email or not password:
            messagebox.showerror("Error", "Please enter both email and password")
            return
        
        # Verify login
        user = verify_user_login(email, password)
        
        if user:
            user_id, full_name, role = user
            
            # Chỉ cho RECEPTIONIST và MANAGER đăng nhập
            if role in ['RECEPTIONIST', 'MANAGER']:
                self.logged_user = {
                    'id': user_id,
                    'name': full_name,
                    'role': role
                }
                
                messagebox.showinfo(
                    "Success", 
                    f"Welcome {full_name}!\nRole: {role}"
                )
                
                # Mở màn hình chính
                self.root.destroy()
                MainWindow(self.logged_user)
            else:
                messagebox.showerror(
                    "Access Denied",
                    f"Only RECEPTIONIST or MANAGER can access.\nYour role: {role}"
                )
        else:
            messagebox.showerror(
                "Login Failed",
                "Invalid email or password"
            )
    
    def run(self):
        """Chạy ứng dụng"""
        self.root.mainloop()
        return self.logged_user


class MainWindow:
    """Màn hình chính - Chọn Member hoặc Employee"""
    
    def __init__(self, user_info):
        self.root = tk.Tk()
        self.root.title("Face Recognition - Main Menu")
        self.root.geometry("600x500")
        self.root.resizable(False, False)
        
        self.user_info = user_info
        self.center_window()
        self.setup_ui()
        self.root.mainloop()
        
    def center_window(self):
        """Căn giữa cửa sổ"""
        self.root.update_idletasks()
        width = self.root.winfo_width()
        height = self.root.winfo_height()
        x = (self.root.winfo_screenwidth() // 2) - (width // 2)
        y = (self.root.winfo_screenheight() // 2) - (height // 2)
        self.root.geometry(f'{width}x{height}+{x}+{y}')
        
    def setup_ui(self):
        """Thiết lập giao diện"""
        # Header
        header_frame = tk.Frame(self.root, bg="#2196F3", height=100)
        header_frame.pack(fill=tk.X)
        
        tk.Label(
            header_frame,
            text="FACE RECOGNITION SYSTEM",
            font=("Arial", 24, "bold"),
            bg="#2196F3",
            fg="white"
        ).pack(pady=15)
        
        tk.Label(
            header_frame,
            text=f"Logged in as: {self.user_info['name']} ({self.user_info['role']})",
            font=("Arial", 11),
            bg="#2196F3",
            fg="white"
        ).pack()
        
        # Main content
        content_frame = tk.Frame(self.root, bg="white")
        content_frame.pack(expand=True, fill=tk.BOTH, padx=50, pady=50)
        
        tk.Label(
            content_frame,
            text="Select User Type:",
            font=("Arial", 18, "bold"),
            bg="white"
        ).pack(pady=20)
        
        # Member button
        member_btn = tk.Button(
            content_frame,
            text="👥 MEMBERS\n(Check-in)",
            font=("Arial", 16, "bold"),
            bg="#4CAF50",
            fg="white",
            width=20,
            height=4,
            cursor="hand2",
            command=lambda: self.open_selection('member')
        )
        member_btn.pack(pady=15)
        
        # Employee button
        employee_btn = tk.Button(
            content_frame,
            text="👔 EMPLOYEES\n(Check-in/out)",
            font=("Arial", 16, "bold"),
            bg="#FF9800",
            fg="white",
            width=20,
            height=4,
            cursor="hand2",
            command=lambda: self.open_selection('employee')
        )
        employee_btn.pack(pady=15)
        
        # Logout button
        logout_btn = tk.Button(
            self.root,
            text="Logout",
            font=("Arial", 10),
            bg="#f44336",
            fg="white",
            width=10,
            cursor="hand2",
            command=self.logout
        )
        logout_btn.pack(side=tk.BOTTOM, pady=10)
        
    def open_selection(self, user_type):
        """Mở màn hình chọn người"""
        self.root.destroy()
        SelectPersonWindow(self.user_info, user_type)
        
    def logout(self):
        """Đăng xuất"""
        if messagebox.askyesno("Logout", "Are you sure you want to logout?"):
            self.root.destroy()
            LoginWindow().run()


class SelectPersonWindow:
    """Màn hình chọn người từ danh sách"""
    
    def __init__(self, user_info, user_type):
        self.root = tk.Tk()
        self.root.title(f"Select {user_type.capitalize()}")
        self.root.geometry("800x600")
        
        self.user_info = user_info
        self.user_type = user_type  # 'member' or 'employee'
        self.all_persons = []
        self.filtered_persons = []
        
        self.center_window()
        self.load_data()
        self.setup_ui()
        self.root.mainloop()
        
    def center_window(self):
        """Căn giữa cửa sổ"""
        self.root.update_idletasks()
        width = self.root.winfo_width()
        height = self.root.winfo_height()
        x = (self.root.winfo_screenwidth() // 2) - (width // 2)
        y = (self.root.winfo_screenheight() // 2) - (height // 2)
        self.root.geometry(f'{width}x{height}+{x}+{y}')
        
    def load_data(self):
        """Load danh sách từ database"""
        if self.user_type == 'member':
            self.all_persons = get_all_active_members()
        else:
            self.all_persons = get_all_active_employees()
        
        self.filtered_persons = self.all_persons.copy()
        
    def setup_ui(self):
        """Thiết lập giao diện"""
        # Header
        header_frame = tk.Frame(self.root, bg="#2196F3", height=80)
        header_frame.pack(fill=tk.X)
        
        title = "Select Member" if self.user_type == 'member' else "Select Employee"
        tk.Label(
            header_frame,
            text=title,
            font=("Arial", 20, "bold"),
            bg="#2196F3",
            fg="white"
        ).pack(pady=20)
        
        # Search bar
        search_frame = tk.Frame(self.root, bg="white")
        search_frame.pack(fill=tk.X, padx=20, pady=15)
        
        tk.Label(
            search_frame,
            text="🔍 Search:",
            font=("Arial", 12),
            bg="white"
        ).pack(side=tk.LEFT, padx=5)
        
        self.search_var = tk.StringVar()
        self.search_var.trace('w', self.filter_list)
        
        search_entry = tk.Entry(
            search_frame,
            textvariable=self.search_var,
            font=("Arial", 12),
            width=40
        )
        search_entry.pack(side=tk.LEFT, padx=10)
        search_entry.focus()
        
        # List frame
        list_frame = tk.Frame(self.root)
        list_frame.pack(expand=True, fill=tk.BOTH, padx=20, pady=10)
        
        # Scrollbar
        scrollbar = tk.Scrollbar(list_frame)
        scrollbar.pack(side=tk.RIGHT, fill=tk.Y)
        
        # Listbox
        self.listbox = tk.Listbox(
            list_frame,
            font=("Arial", 12),
            yscrollcommand=scrollbar.set,
            selectmode=tk.SINGLE
        )
        self.listbox.pack(side=tk.LEFT, expand=True, fill=tk.BOTH)
        scrollbar.config(command=self.listbox.yview)
        
        # Double click to select
        self.listbox.bind('<Double-Button-1>', lambda e: self.select_person())
        
        # Populate list
        self.populate_list()
        
        # Buttons
        btn_frame = tk.Frame(self.root, bg="white")
        btn_frame.pack(fill=tk.X, padx=20, pady=15)
        
        tk.Button(
            btn_frame,
            text="← Back",
            font=("Arial", 11),
            bg="#757575",
            fg="white",
            width=15,
            cursor="hand2",
            command=self.go_back
        ).pack(side=tk.LEFT, padx=5)
        
        tk.Button(
            btn_frame,
            text="Start Face Recognition →",
            font=("Arial", 11, "bold"),
            bg="#4CAF50",
            fg="white",
            width=25,
            cursor="hand2",
            command=self.select_person
        ).pack(side=tk.RIGHT, padx=5)
        
        # Status
        self.status_label = tk.Label(
            self.root,
            text=f"Total: {len(self.all_persons)} persons",
            font=("Arial", 9),
            fg="gray"
        )
        self.status_label.pack(pady=5)
        
    def populate_list(self):
        """Điền danh sách vào listbox"""
        self.listbox.delete(0, tk.END)
        
        for person_id, name in self.filtered_persons[:50]:  # Hiển thị tối đa 50
            display_text = f"ID: {person_id:3d} | {name}"
            self.listbox.insert(tk.END, display_text)
            
    def filter_list(self, *args):
        """Lọc danh sách theo từ khóa search"""
        keyword = self.search_var.get().lower()
        
        if keyword:
            self.filtered_persons = [
                (pid, name) for pid, name in self.all_persons
                if keyword in name.lower() or keyword in str(pid)
            ]
        else:
            self.filtered_persons = self.all_persons.copy()
        
        self.populate_list()
        self.status_label.config(
            text=f"Showing: {len(self.filtered_persons)} / {len(self.all_persons)} persons"
        )
        
    def select_person(self):
        """Chọn người và bắt đầu nhận diện"""
        selection = self.listbox.curselection()
        
        if not selection:
            messagebox.showwarning("No Selection", "Please select a person from the list")
            return
        
        index = selection[0]
        person_id, person_name = self.filtered_persons[index]
        
        # Mở cửa sổ nhận diện khuôn mặt
        self.root.destroy()
        FaceRecognitionWindow(
            self.user_info,
            self.user_type,
            person_id,
            person_name
        )
        
    def go_back(self):
        """Quay lại màn hình chính"""
        self.root.destroy()
        MainWindow(self.user_info)


class FaceRecognitionWindow:
    """Màn hình quét khuôn mặt"""
    
    def __init__(self, user_info, user_type, person_id, person_name):
        self.root = tk.Tk()
        self.root.title("Face Recognition")
        self.root.geometry("1000x700")
        
        self.user_info = user_info
        self.user_type = user_type
        self.person_id = person_id
        self.person_name = person_name
        
        # Camera & Recognition
        self.cap = None
        self.is_running = False
        self.embeddings_db = {}
        self.target_embedding = None
        
        # Recognition state
        self.match_count = 0
        self.required_matches = 3  # 3 frames liên tiếp
        
        self.center_window()
        self.setup_ui()          # ← Setup UI trước (tạo status_text)
        self.load_embeddings()   # ← Load embeddings sau (dùng status_text)
        self.start_camera()
        
        self.root.protocol("WM_DELETE_WINDOW", self.on_closing)
        self.root.mainloop()
        
    def center_window(self):
        """Căn giữa cửa sổ"""
        self.root.update_idletasks()
        width = self.root.winfo_width()
        height = self.root.winfo_height()
        x = (self.root.winfo_screenwidth() // 2) - (width // 2)
        y = (self.root.winfo_screenheight() // 2) - (height // 2)
        self.root.geometry(f'{width}x{height}+{x}+{y}')
        
    def load_embeddings(self):
        """Load embeddings từ database"""
        try:
            embeddings_dict = get_all_embeddings(self.user_type)
            
            if not embeddings_dict:
                messagebox.showwarning(
                    "No Data",
                    f"No face data found for {self.user_type}s.\n"
                    "Please collect face data first using add_faces_improved.py"
                )
                self.go_back()
                return
            
            # Lấy embedding của người được chọn
            if self.person_id in embeddings_dict:
                self.target_embedding = embeddings_dict[self.person_id]
            else:
                messagebox.showerror(
                    "Error",
                    f"No face data found for {self.person_name} (ID: {self.person_id})\n"
                    "Please collect face data first."
                )
                self.go_back()
                
        except Exception as e:
            messagebox.showerror("Error", f"Failed to load embeddings: {str(e)}")
            self.go_back()
            
    def setup_ui(self):
        """Thiết lập giao diện"""
        # Header
        header_frame = tk.Frame(self.root, bg="#2196F3")
        header_frame.pack(fill=tk.X)
        
        tk.Label(
            header_frame,
            text=f"Verifying: {self.person_name}",
            font=("Arial", 18, "bold"),
            bg="#2196F3",
            fg="white"
        ).pack(pady=15)
        
        # Main content
        content_frame = tk.Frame(self.root)
        content_frame.pack(expand=True, fill=tk.BOTH, padx=20, pady=20)
        
        # Left: Camera
        camera_frame = tk.LabelFrame(
            content_frame,
            text="Camera Feed",
            font=("Arial", 12, "bold"),
            fg="#2196F3"
        )
        camera_frame.pack(side=tk.LEFT, padx=10)
        
        self.camera_label = tk.Label(camera_frame, bg="black")
        self.camera_label.pack(padx=10, pady=10)
        
        # Right: Info
        info_frame = tk.Frame(content_frame)
        info_frame.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True, padx=10)
        
        # Person info
        info_box = tk.LabelFrame(
            info_frame,
            text="Person Information",
            font=("Arial", 12, "bold"),
            fg="#2196F3"
        )
        info_box.pack(fill=tk.X, pady=10)
        
        tk.Label(
            info_box,
            text=f"ID: {self.person_id}",
            font=("Arial", 14),
            anchor="w"
        ).pack(fill=tk.X, padx=20, pady=5)
        
        tk.Label(
            info_box,
            text=f"Name: {self.person_name}",
            font=("Arial", 14),
            anchor="w"
        ).pack(fill=tk.X, padx=20, pady=5)
        
        tk.Label(
            info_box,
            text=f"Type: {self.user_type.upper()}",
            font=("Arial", 14),
            anchor="w"
        ).pack(fill=tk.X, padx=20, pady=5)
        
        # Status
        status_box = tk.LabelFrame(
            info_frame,
            text="Recognition Status",
            font=("Arial", 12, "bold"),
            fg="#2196F3"
        )
        status_box.pack(fill=tk.BOTH, expand=True, pady=10)
        
        self.status_text = tk.Text(
            status_box,
            font=("Courier", 11),
            height=15,
            wrap=tk.WORD,
            state=tk.DISABLED
        )
        self.status_text.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        
        # Buttons
        btn_frame = tk.Frame(self.root)
        btn_frame.pack(fill=tk.X, padx=20, pady=10)
        
        tk.Button(
            btn_frame,
            text="← Back to List",
            font=("Arial", 11),
            bg="#757575",
            fg="white",
            width=15,
            cursor="hand2",
            command=self.go_back
        ).pack(side=tk.LEFT, padx=5)
        
        tk.Button(
            btn_frame,
            text="Stop Camera",
            font=("Arial", 11),
            bg="#f44336",
            fg="white",
            width=15,
            cursor="hand2",
            command=self.stop_camera
        ).pack(side=tk.RIGHT, padx=5)
        
    def log_status(self, message):
        """Ghi log vào status text"""
        self.status_text.config(state=tk.NORMAL)
        timestamp = datetime.now().strftime("%H:%M:%S")
        self.status_text.insert(tk.END, f"[{timestamp}] {message}\n")
        self.status_text.see(tk.END)
        self.status_text.config(state=tk.DISABLED)
        
    def start_camera(self):
        """Bắt đầu camera"""
        try:
            self.cap = cv2.VideoCapture(0)
            self.cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
            self.cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)
            
            if not self.cap.isOpened():
                raise Exception("Cannot open camera")
            
            self.is_running = True
            self.log_status("✓ Camera started")
            self.log_status("Please look at the camera...")
            self.update_frame()
            
        except Exception as e:
            messagebox.showerror("Error", f"Failed to start camera: {str(e)}")
            self.go_back()
            
    def update_frame(self):
        """Cập nhật frame từ camera"""
        if not self.is_running:
            return
        
        ret, frame = self.cap.read()
        
        if ret:
            # Flip frame
            frame = cv2.flip(frame, 1)
            
            # Detect and recognize face
            self.process_frame(frame)
            
            # Convert to PhotoImage
            frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            img = Image.fromarray(frame_rgb)
            img = img.resize((640, 480))
            imgtk = ImageTk.PhotoImage(image=img)
            
            self.camera_label.imgtk = imgtk
            self.camera_label.configure(image=imgtk)
        
        # Schedule next update
        self.root.after(30, self.update_frame)
        
    def process_frame(self, frame):
        """Xử lý frame - detect và recognize"""
        try:
            # Detect face
            faces = DeepFace.extract_faces(
                frame,
                detector_backend='retinaface',
                enforce_detection=False
            )
            
            if not faces or faces[0]['confidence'] < 0.9:
                self.match_count = 0
                return
            
            face = faces[0]
            x, y, w, h = face['facial_area']['x'], face['facial_area']['y'], \
                         face['facial_area']['w'], face['facial_area']['h']
            
            # Extract embedding
            embedding = DeepFace.represent(
                frame,
                model_name='ArcFace',
                detector_backend='retinaface',
                enforce_detection=False
            )
            
            if not embedding:
                return
            
            test_embedding = np.array(embedding[0]['embedding'])
            
            # Compare with target
            distances = [
                cosine(test_embedding, stored_emb) 
                for stored_emb in self.target_embedding
            ]
            min_distance = min(distances)
            
            # Draw rectangle
            if min_distance < config.RECOGNITION_THRESHOLD:
                color = (0, 255, 0)  # Green - Match
                self.match_count += 1
                
                cv2.rectangle(frame, (x, y), (x+w, y+h), color, 3)
                cv2.putText(
                    frame,
                    f"MATCH ({self.match_count}/{self.required_matches})",
                    (x, y-10),
                    cv2.FONT_HERSHEY_SIMPLEX,
                    0.7,
                    color,
                    2
                )
                
                # Success after required matches
                if self.match_count >= self.required_matches:
                    self.on_recognition_success()
            else:
                color = (0, 0, 255)  # Red - No match
                self.match_count = 0
                
                cv2.rectangle(frame, (x, y), (x+w, y+h), color, 3)
                cv2.putText(
                    frame,
                    "NOT MATCH",
                    (x, y-10),
                    cv2.FONT_HERSHEY_SIMPLEX,
                    0.7,
                    color,
                    2
                )
                
        except Exception as e:
            pass  # Skip error frames
            
    def on_recognition_success(self):
        """Xử lý khi nhận diện thành công"""
        self.stop_camera()
        
        try:
            # Log to database
            if self.user_type == 'member':
                log_access(self.person_id)
                msg = "✓ Check-in successful!"
            else:
                log_employee_access(self.person_id, 'in')
                msg = "✓ Check-in successful!"
            
            self.log_status(msg)
            
            messagebox.showinfo(
                "Success",
                f"{msg}\n\n"
                f"Name: {self.person_name}\n"
                f"ID: {self.person_id}\n"
                f"Time: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}"
            )
            
            self.go_back()
            
        except Exception as e:
            messagebox.showerror("Error", f"Failed to log attendance: {str(e)}")
            
    def stop_camera(self):
        """Dừng camera"""
        self.is_running = False
        if self.cap:
            self.cap.release()
        self.log_status("✗ Camera stopped")
        
    def go_back(self):
        """Quay lại màn hình chọn người"""
        self.stop_camera()
        self.root.destroy()
        SelectPersonWindow(self.user_info, self.user_type)
        
    def on_closing(self):
        """Xử lý khi đóng cửa sổ"""
        self.stop_camera()
        self.root.destroy()


# Thêm hàm verify_user_login vào database.py nếu chưa có
def verify_user_login(email, password):
    """
    Verify user login credentials
    Returns: (user_id, full_name, role) if success, None if failed
    """
    conn = get_connection()
    if not conn:
        return None
    
    try:
        cursor = conn.cursor()
        
        # Get user by email
        query = """
            SELECT id, full_name, password, role 
            FROM users 
            WHERE email = %s AND is_deleted = 0
        """
        cursor.execute(query, (email,))
        result = cursor.fetchone()
        
        if result:
            user_id, full_name, stored_password, role = result
            
            # BCrypt password verification
            import bcrypt
            
            # Verify password
            if bcrypt.checkpw(password.encode('utf-8'), stored_password.encode('utf-8')):
                return (user_id, full_name, role)
        
        return None
        
    except Exception as e:
        print(f"Error verifying login: {e}")
        return None
    finally:
        if conn:
            conn.close()


if __name__ == "__main__":
    # Chạy ứng dụng
    logged_user = LoginWindow().run()
    
    if logged_user:
        MainWindow(logged_user)
