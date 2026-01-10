"""
Face Recognition với DeepFace - MAXIMUM ACCURACY
Sử dụng RetinaFace detector + ArcFace embeddings + Cosine similarity
"""

import cv2
import numpy as np
from deepface import DeepFace
from scipy.spatial.distance import cosine
import time
from datetime import datetime
from collections import deque
from PIL import Image, ImageDraw, ImageFont
import threading

# Import modules
from database import (get_member_status, get_employee_status,
                      log_access, update_access_log,
                      log_employee_access,
                      get_member_name, get_employee_name,
                      get_last_employee_check_type)
from database_embeddings import get_all_embeddings
import config

# Optional: Text-to-speech
try:
    from win32com.client import Dispatch
    def speak(text):
        try:
            speaker = Dispatch("SAPI.SpVoice")
            speaker.Speak(text)
        except:
            pass
    TTS_AVAILABLE = True
except:
    def speak(text):
        pass
    TTS_AVAILABLE = False


def speak_async(text, callback=None):
    """
    Speak trong background thread không block camera
    
    Args:
        text: Text cần nói
        callback: Function gọi sau khi speak xong
    """
    def _speak():
        speak(text)
        if callback:
            callback()
    
    thread = threading.Thread(target=_speak, daemon=True)
    thread.start()


def put_text_vietnamese(img, text, position, font_size=20, color=(255, 255, 255)):
    """
    Vẽ text tiếng Việt lên OpenCV image sử dụng PIL
    
    Args:
        img: OpenCV image (BGR)
        text: Text tiếng Việt có dấu
        position: (x, y) tuple
        font_size: Size của font
        color: (B, G, R) tuple cho OpenCV
    
    Returns:
        img: OpenCV image với text
    """
    # Convert BGR to RGB
    img_pil = Image.fromarray(cv2.cvtColor(img, cv2.COLOR_BGR2RGB))
    draw = ImageDraw.Draw(img_pil)
    
    # Load font (sử dụng font hệ thống hỗ trợ tiếng Việt)
    try:
        # Windows font
        font = ImageFont.truetype("arial.ttf", font_size)
    except:
        # Fallback to default font
        font = ImageFont.load_default()
    
    # Convert OpenCV BGR color to PIL RGB color
    color_rgb = (color[2], color[1], color[0])
    
    # Draw text
    draw.text(position, text, font=font, fill=color_rgb)
    
    # Convert back to BGR
    img = cv2.cvtColor(np.array(img_pil), cv2.COLOR_RGB2BGR)
    
    return img


def draw_processing_overlay(frame, message):
    """
    Draw overlay "Processing check-in, please wait..." on frame
    
    Args:
        frame: OpenCV frame
        message: Message to display
    
    Returns:
        frame với overlay
    """
    height, width = frame.shape[:2]
    overlay = frame.copy()
    
    # Semi-transparent background
    cv2.rectangle(overlay, (0, 0), (width, height), (0, 0, 0), -1)
    cv2.addWeighted(overlay, 0.3, frame, 0.7, 0, frame)
    
    # Message box
    box_width = 500
    box_height = 120
    box_x = (width - box_width) // 2
    box_y = (height - box_height) // 2
    
    # Orange/yellow background for waiting state
    cv2.rectangle(frame, (box_x, box_y), 
                 (box_x + box_width, box_y + box_height), (0, 165, 255), -1)
    cv2.rectangle(frame, (box_x, box_y), 
                 (box_x + box_width, box_y + box_height), (0, 200, 255), 3)
    
    # Message text using PIL for Vietnamese
    frame = put_text_vietnamese(frame, message, (box_x + 60, box_y + 35), 
                               font_size=30, color=(255, 255, 255))
    
    # Animated dots
    dots_count = int((time.time() * 2) % 4)
    dots = "." * dots_count
    frame = put_text_vietnamese(frame, dots, (box_x + box_width - 80, box_y + 35),
                               font_size=30, color=(255, 255, 255))
    
    return frame


class FaceRecognizer:
    """
    Face recognizer với maximum accuracy
    """

    def __init__(self):
        print("=" * 60)
        print("INITIALIZING FACE RECOGNIZER")
        print("=" * 60)

        # Load all embeddings from database
        print("Loading embeddings from database...")
        self.embeddings_db = get_all_embeddings()

        if not self.embeddings_db:
            print("⚠️ No embeddings found in database!")
            print("   Please run add_faces_improved.py first to collect face data")
        else:
            total_embeddings = sum(len(embs) for embs in self.embeddings_db.values())
            print(f"✅ Loaded {len(self.embeddings_db)} persons")
            print(f"   Total embeddings: {total_embeddings}")

        # Configuration
        self.threshold = config.RECOGNITION_THRESHOLD
        self.model_name = config.FACE_MODEL
        self.detector = config.FACE_DETECTOR
        self.distance_metric = config.DISTANCE_METRIC

        # Temporal smoothing - track detections over time
        self.detection_buffer = {}  # person_id -> deque of (confidence, timestamp)
        self.buffer_size = config.CONFIDENCE_BUFFER_SIZE

        # Tracking for check-in/out logic
        self.last_log_times = {}  # person_id -> timestamp
        self.last_log_ids = {}    # person_id -> log_id (for members)
        self.last_unknown_time = 0
        
        # Flag để tạm dừng nhận diện khi đang process check-in
        self.is_processing_checkin = False
        self.processing_message = ""
        self.processing_start_time = 0

        print(f"\nConfiguration:")
        print(f"  Model: {self.model_name}")
        print(f"  Detector: {self.detector}")
        print(f"  Threshold: {self.threshold}")
        print(f"  Distance metric: {self.distance_metric}")
        print(f"  Temporal smoothing: {self.buffer_size} frames")
        print("=" * 60)

    def compute_distance(self, emb1, emb2):
        """Compute distance between two embeddings"""
        if self.distance_metric == 'cosine':
            return cosine(emb1, emb2)
        elif self.distance_metric == 'euclidean':
            return np.linalg.norm(emb1 - emb2)
        elif self.distance_metric == 'euclidean_l2':
            return np.linalg.norm(emb1 - emb2) / np.sqrt(len(emb1))
        else:
            return cosine(emb1, emb2)

    def recognize_face(self, face_img):
        """
        Recognize a face image

        Returns:
            (person_id, confidence, distance) or (None, 0, inf)
        """
        try:
            # Extract embedding
            embedding_obj = DeepFace.represent(
                face_img,
                model_name=self.model_name,
                detector_backend='skip',  # Already detected
                enforce_detection=False
            )

            embedding = np.array(embedding_obj[0]['embedding'])

            # Find best match
            best_person_id = None
            best_distance = float('inf')

            for person_id, stored_embeddings in self.embeddings_db.items():
                # Compare with all stored embeddings of this person
                distances = []
                for stored_emb in stored_embeddings:
                    dist = self.compute_distance(embedding, stored_emb)
                    distances.append(dist)

                # Take minimum distance (best match)
                min_dist = min(distances)

                if min_dist < best_distance:
                    best_distance = min_dist
                    best_person_id = person_id

            # Decision based on threshold
            if best_distance < self.threshold:
                # Convert distance to confidence (0-100%)
                if self.distance_metric == 'cosine':
                    # Cosine distance: 0 = identical, 2 = opposite
                    # Convert to similarity: 1 - distance
                    confidence = (1 - best_distance) * 100
                else:
                    # For Euclidean, use inverse
                    confidence = max(0, (1 - best_distance / self.threshold) * 100)

                return best_person_id, confidence, best_distance
            else:
                return None, 0, best_distance

        except Exception as e:
            print(f"Recognition error: {e}")
            return None, 0, float('inf')

    def add_to_buffer(self, person_id, confidence):
        """
        Add detection to temporal buffer

        Returns:
            (is_confirmed, avg_confidence)
        """
        current_time = time.time()

        if person_id not in self.detection_buffer:
            self.detection_buffer[person_id] = deque(maxlen=self.buffer_size)

        self.detection_buffer[person_id].append((confidence, current_time))

        # Check if we have enough consecutive detections
        if len(self.detection_buffer[person_id]) >= self.buffer_size:
            # Calculate average confidence
            confidences = [conf for conf, _ in self.detection_buffer[person_id]]
            avg_confidence = np.mean(confidences)

            # Check if all recent detections are within time window
            times = [t for _, t in self.detection_buffer[person_id]]
            time_span = max(times) - min(times)
            
            # Time window: 5 giây (phù hợp với FPS thấp + frame skip)
            if time_span < 5.0:
                if avg_confidence >= config.MIN_CONFIDENCE:
                    if config.VERBOSE:
                        print(f"[TEMPORAL] ✓ Time span: {time_span:.2f}s < 5.0s | Avg conf: {avg_confidence:.1f}% >= {config.MIN_CONFIDENCE}%")
                    return True, avg_confidence
                else:
                    if config.VERBOSE:
                        print(f"[TEMPORAL] ✗ Confidence too low: {avg_confidence:.1f}% < {config.MIN_CONFIDENCE}%")
            else:
                if config.VERBOSE:
                    print(f"[TEMPORAL] ✗ Time span too long: {time_span:.2f}s >= 5.0s")

        return False, confidence

    def clear_buffer(self, person_id):
        """Clear buffer for a person"""
        if person_id in self.detection_buffer:
            self.detection_buffer[person_id].clear()

    def get_person_info(self, person_id):
        """
        Get person information

        Returns:
            (name, is_employee, status, check_type)
        """
        if person_id.startswith('emp_'):
            # Employee
            employee_id = int(person_id[4:])
            name = get_employee_name(employee_id)
            status = get_employee_status(employee_id)
            last_check = get_last_employee_check_type(employee_id)

            if last_check == 'in':
                check_type = 'out'
            else:
                check_type = 'in'

            return name, True, status, check_type
        else:
            # Member
            member_id = int(person_id)
            name = get_member_name(member_id)
            status = get_member_status(member_id)

            return name, False, status, None


def show_success_notification(frame, person_name, action_type, is_employee):
    """
    Display success notification large and clear
    Duration: 2-3 seconds
    """
    height, width = frame.shape[:2]
    notification_frame = frame.copy()

    # Semi-transparent overlay
    overlay = notification_frame.copy()
    cv2.rectangle(overlay, (0, 0), (width, height), (0, 0, 0), -1)
    cv2.addWeighted(overlay, 0.6, notification_frame, 0.4, 0, notification_frame)

    # Success box
    box_width = 600
    box_height = 300
    box_x = (width - box_width) // 2
    box_y = (height - box_height) // 2

    # Green background
    cv2.rectangle(notification_frame, (box_x, box_y),
                 (box_x + box_width, box_y + box_height), (0, 200, 0), -1)
    cv2.rectangle(notification_frame, (box_x, box_y),
                 (box_x + box_width, box_y + box_height), (0, 255, 0), 5)

    # Success icon (checkmark)
    center_x = width // 2
    center_y = box_y + 80
    cv2.circle(notification_frame, (center_x, center_y), 50, (255, 255, 255), -1)
    cv2.putText(notification_frame, "✓", (center_x - 30, center_y + 20),
               cv2.FONT_HERSHEY_SIMPLEX, 3, (0, 200, 0), 8)

    # Success message
    if is_employee:
        action_text = "IN" if action_type == 'in' else "OUT"
        message = f"EMPLOYEE {action_text}"
    else:
        # Member check-in
        message = "CHECK-IN SUCCESS"

    cv2.putText(notification_frame, message, (box_x + 90, box_y + 180),
               cv2.FONT_HERSHEY_SIMPLEX, 1.1, (255, 255, 255), 3)

    # Person name - sử dụng PIL để vẽ tiếng Việt
    notification_frame = put_text_vietnamese(notification_frame, person_name, (box_x + 80, box_y + 210), 
                                            font_size=40, color=(255, 255, 255))

    return notification_frame


def create_info_panel(frame, info_dict):
    """
    Create enhanced display frame with info panel
    """
    height, width = frame.shape[:2]

    # Expand frame
    panel_width = config.INFO_PANEL_WIDTH
    canvas = np.zeros((height, width + panel_width, 3), dtype=np.uint8)
    canvas[:, :width] = frame

    # Panel background
    cv2.rectangle(canvas, (width, 0), (width + panel_width, height), (30, 30, 30), -1)

    # Header with gradient background (blue theme)
    header_height = 80
    cv2.rectangle(canvas, (width + 10, 10), (width + panel_width - 10, header_height),
                  (100, 50, 0), -1)  # Blue gradient
    cv2.putText(canvas, "FACE RECOGNITION", (width + 30, 45),
                cv2.FONT_HERSHEY_SIMPLEX, 0.75, (255, 255, 255), 2)
    cv2.putText(canvas, f"Model: {config.FACE_MODEL}", (width + 50, 70),
                cv2.FONT_HERSHEY_SIMPLEX, 0.5, (200, 255, 255), 1)

    # Person info
    if info_dict.get('person_detected'):
        y_offset = 120
        box_height = 220

        # Info box with colored background
        if info_dict.get('status') == 'active':
            color = config.COLOR_RECOGNIZED
            bg_color = (20, 50, 20)  # Dark green background
        else:
            color = config.COLOR_INACTIVE
            bg_color = (40, 40, 40)  # Dark gray background
        
        cv2.rectangle(canvas, (width + 20, y_offset),
                     (width + panel_width - 20, y_offset + box_height), bg_color, -1)
        cv2.rectangle(canvas, (width + 20, y_offset),
                     (width + panel_width - 20, y_offset + box_height), color, 3)

        # Display info
        y_text = y_offset + 35
        line_height = 35

        lines = [
            f"ID: {info_dict.get('id', 'N/A')}",
            f"Name: {info_dict.get('name', 'Unknown')}",
            f"Status: {info_dict.get('status', 'N/A')}",
        ]

        if info_dict.get('is_employee'):
            action_text = "In" if info_dict.get('check_type') == 'in' else "Out"
            lines.append(f"Action: {action_text}")

        if info_dict.get('confirmed'):
            lines.append("[CONFIRMED]")

        # Text color based on status
        text_color = (100, 255, 100) if info_dict.get('status') == 'active' else (255, 255, 255)
        
        for line in lines:
            canvas = put_text_vietnamese(canvas, line, (width + 35, y_text - 20),
                                        font_size=18, color=text_color)
            y_text += line_height

    elif info_dict.get('unknown'):
        # Unknown person
        y_offset = 120
        cv2.rectangle(canvas, (width + 20, y_offset),
                     (width + panel_width - 20, y_offset + 100),
                     (0, 0, 255), 2)
        cv2.putText(canvas, "UNKNOWN", (width + 100, y_offset + 45),
                   cv2.FONT_HERSHEY_SIMPLEX, 0.9, (0, 0, 255), 2)
        cv2.putText(canvas, info_dict.get('time', ''), (width + 60, y_offset + 80),
                   cv2.FONT_HERSHEY_SIMPLEX, 0.5, (200, 200, 200), 1)

    # FPS and stats with cyan color
    y_stats = height - 120
    cv2.putText(canvas, f"FPS: {info_dict.get('fps', 0):.1f}",
               (width + 30, y_stats), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 100), 2)
    cv2.putText(canvas, f"Latency: {info_dict.get('latency', 0):.0f}ms",
               (width + 30, y_stats + 30), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 100), 2)
    cv2.putText(canvas, f"Threshold: {config.RECOGNITION_THRESHOLD}",
               (width + 30, y_stats + 60), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (150, 255, 255), 1)

    # Instructions
    cv2.putText(canvas, "Press 'q' to quit",
               (width + 30, height - 20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (150, 150, 150), 1)

    return canvas


def main():
    """Main recognition loop"""
    print("\n" + "=" * 60)
    print("STARTING FACE RECOGNITION SYSTEM")
    print("=" * 60)

    # Initialize recognizer
    recognizer = FaceRecognizer()

    if not recognizer.embeddings_db:
        print("\n❌ Cannot start - no face data available")
        print("   Run add_faces_improved.py first to collect faces")
        return

    # Open camera
    print("\nOpening camera...")
    video = cv2.VideoCapture(0, cv2.CAP_DSHOW)
    video.set(cv2.CAP_PROP_FRAME_WIDTH, config.CAMERA_WIDTH)
    video.set(cv2.CAP_PROP_FRAME_HEIGHT, config.CAMERA_HEIGHT)
    video.set(cv2.CAP_PROP_FPS, config.CAMERA_FPS)

    if not video.isOpened():
        print("❌ Failed to open camera")
        return

    print("✅ Camera opened successfully")
    print("\n" + "=" * 60)
    print("SYSTEM RUNNING - Press 'q' to quit")
    print("=" * 60)

    # FPS calculation
    fps_counter = deque(maxlen=30)
    frame_count = 0
    detected_persons = set()  # Track detected persons in current frame
    last_recognition_result = None  # Cache last recognition result

    while True:
        start_time = time.time()

        ret, frame = video.read()
        if not ret:
            print("Failed to read frame")
            continue

        frame = cv2.flip(frame, 1)
        frame_count += 1

        # For info panel
        info_dict = {
            'person_detected': False,
            'unknown': False,
            'fps': 0,
            'latency': 0
        }

        detected_persons.clear()
        
        # Nếu đang process check-in, vẽ overlay và skip nhận diện
        if recognizer.is_processing_checkin:
            # Vẽ overlay thông báo
            frame = draw_processing_overlay(frame, recognizer.processing_message)
            
            # Tính elapsed time (tự động clear flag sau 5 giây nếu callback fail)
            if time.time() - recognizer.processing_start_time > 5.0:
                recognizer.is_processing_checkin = False
                if config.VERBOSE:
                    print("[TIMEOUT] Auto-cleared processing flag after 5s")
        else:
            # PERFORMANCE OPTIMIZATION: Frame skipping
            # Chỉ process mỗi N frames, các frame khác dùng kết quả cũ
            should_process = (frame_count % config.FRAME_SKIP_REALTIME == 0)

            if should_process:
                # Resize frame để process nhanh hơn
                process_frame = cv2.resize(frame,
                                          (config.RECOGNITION_WIDTH, config.RECOGNITION_HEIGHT))

                try:
                    # Detect faces với detector nhẹ hơn
                    face_objs = DeepFace.extract_faces(
                        process_frame,
                        detector_backend=config.FACE_DETECTOR_REALTIME,  # opencv thay vì retinaface
                        enforce_detection=False,
                        align=config.ALIGN_FACE
                    )

                    if config.VERBOSE:
                        print(f"\n[DETECT] Found {len(face_objs)} face(s)")

                    # Scale lại tọa độ về frame gốc
                    scale_x = frame.shape[1] / config.RECOGNITION_WIDTH
                    scale_y = frame.shape[0] / config.RECOGNITION_HEIGHT

                    for face_obj in face_objs:
                        facial_area = face_obj['facial_area']
                        # Scale coordinates về frame gốc
                        x = int(facial_area['x'] * scale_x)
                        y = int(facial_area['y'] * scale_y)
                        w = int(facial_area['w'] * scale_x)
                        h = int(facial_area['h'] * scale_y)

                        # Get face image
                        face_img = face_obj['face']
                        if face_img.dtype in [np.float32, np.float64]:
                            face_img = (face_img * 255).astype(np.uint8)

                        # PERFORMANCE: Bỏ quality check trong real-time recognition
                        # (Quality đã được check khi collect data)

                        # Recognize
                        person_id, confidence, distance = recognizer.recognize_face(face_img)

                        if config.VERBOSE:
                            if person_id:
                                print(f"[RECOGNIZE] ✓ Match: {person_id} | Confidence: {confidence:.1f}% | Distance: {distance:.4f}")
                            else:
                                print(f"[RECOGNIZE] ✗ No match | Best distance: {distance:.4f} | Threshold: {recognizer.threshold}")

                        if person_id is not None:
                            # Person recognized
                            detected_persons.add(person_id)
                            name, is_employee, status, check_type = recognizer.get_person_info(person_id)

                            # Temporal smoothing
                            is_confirmed, avg_confidence = recognizer.add_to_buffer(person_id, confidence)

                            if config.VERBOSE:
                                buffer_len = len(recognizer.detection_buffer.get(person_id, []))
                                print(f"[BUFFER] {name} | Buffer: {buffer_len}/{recognizer.buffer_size} frames | Avg confidence: {avg_confidence:.1f}% | Confirmed: {is_confirmed}")

                            # Color based on status - màu xanh lá cho cả đang nhận diện và thành công
                            if status == 'active':
                                color = config.COLOR_RECOGNIZED  # Màu xanh lá cho cả 2 trường hợp
                            else:
                                color = config.COLOR_INACTIVE

                            # Draw rectangle with thicker border for better visibility
                            cv2.rectangle(frame, (x, y), (x+w, y+h), color, 3)
                            cv2.rectangle(frame, (x, y-40), (x+w, y), color, -1)

                            # Label - chỉ hiển thị tên, không hiển thị %
                            label = f"{name}"
                            if is_confirmed:
                                label += " [OK]"

                            # Sử dụng PIL để vẽ text tiếng Việt
                            frame = put_text_vietnamese(frame, label, (x+5, y-35), font_size=20, color=(255, 255, 255))

                            # Update info dict
                            info_dict.update({
                                'person_detected': True,
                                'id': person_id,
                                'name': name,
                                'confidence': avg_confidence,
                                'status': status,
                                'is_employee': is_employee,
                                'check_type': check_type if is_employee else None,
                                'confirmed': is_confirmed
                            })

                            # Trigger action if confirmed
                            if config.VERBOSE:
                                print(f"[CHECK] is_confirmed={is_confirmed} | status={status} | active={status.lower() == 'active'}")
                        
                            if is_confirmed and status.lower() == 'active':
                                ts = time.time()
                                date = datetime.fromtimestamp(ts).strftime("%d-%m-%Y")
                                timestamp = datetime.fromtimestamp(ts).strftime("%H:%M-%S")

                                # Check cooldown
                                last_log = recognizer.last_log_times.get(person_id, 0)
                                cooldown_elapsed = ts - last_log

                                if config.VERBOSE:
                                    print(f"[COOLDOWN] Last log: {cooldown_elapsed:.1f}s ago | Required: {config.CHECK_IN_DELAY}s | Pass: {cooldown_elapsed > config.CHECK_IN_DELAY}")

                                if ts - last_log > config.CHECK_IN_DELAY:
                                    action_logged = False
                                    action_type = None
                                    speech_text = ""

                                    if is_employee:
                                        # Employee check-in/out
                                        employee_id = int(person_id[4:])
                                        
                                        # Nếu là checkout, check thời gian tối thiểu từ check-in
                                        if check_type == 'out':
                                            time_since_checkin = ts - last_log
                                            if time_since_checkin < config.EMPLOYEE_CHECKOUT_MIN_DURATION:
                                                if config.VERBOSE:
                                                    print(f"[SKIP] Employee {employee_id} | Checkout too soon: {time_since_checkin:.1f}s < {config.EMPLOYEE_CHECKOUT_MIN_DURATION}s")
                                                # Skip checkout, continue recognition
                                                continue
                                        
                                        if config.VERBOSE:
                                            print(f"[LOG] Employee {employee_id} | Action: {check_type} | Time: {date} {timestamp}")
                                        log_employee_access(employee_id, check_type, f"{date} {timestamp}")
                                        speech_text = f"Employee check {check_type}"
                                        print(f"✓ Employee {name} checked {check_type}")
                                        action_logged = True
                                        action_type = check_type
                                    else:
                                        # Member check-in: LUÔN tạo log mới
                                        # Ghi hết tất cả lần check-in, không update log cũ
                                        member_id = int(person_id)
                                        
                                        if config.VERBOSE:
                                            print(f"[LOG] Member {member_id} | NEW check-in | Time: {date} {timestamp}")
                                        
                                        log_id = log_access(member_id, f"{date} {timestamp}")
                                        recognizer.last_log_ids[person_id] = log_id
                                        speech_text = "Attendance taken"
                                        print(f"✓ Member {name} checked in")
                                        action_logged = True
                                        action_type = "check-in"

                                    recognizer.last_log_times[person_id] = ts
                                    recognizer.clear_buffer(person_id)

                                    if config.VERBOSE:
                                        print(f"[SUCCESS] Logged to database | Buffer cleared | Cooldown timer reset\n")

                                    # Set flag và message
                                    if action_logged:
                                        recognizer.is_processing_checkin = True
                                        recognizer.processing_message = "Processing check-in, please wait"
                                        recognizer.processing_start_time = time.time()
                                    
                                        # Callback để clear flag sau khi speak xong
                                        def on_speak_done():
                                            recognizer.is_processing_checkin = False
                                    
                                        # Speak async không block camera
                                        speak_async(speech_text, on_speak_done)
                                    
                                        # Show success notification trong 2 giây
                                        success_frame = show_success_notification(
                                            frame, name, action_type, is_employee
                                        )
                                        display_success = create_info_panel(success_frame, info_dict)
                                    
                                        notification_duration = config.SUCCESS_NOTIFICATION_DURATION
                                        notification_start = time.time()
                                    
                                        while (time.time() - notification_start) < notification_duration:
                                            cv2.imshow("Nhan dien khuon mat - DeepFace + ArcFace", display_success)
                                            if cv2.waitKey(30) == ord('q'):
                                                recognizer.is_processing_checkin = False
                                                break
                                    
                                        continue

                        else:
                            # Unknown person - chỉ hiển thị Unknown
                            cv2.rectangle(frame, (x, y), (x+w, y+h), config.COLOR_UNKNOWN, 3)
                            cv2.rectangle(frame, (x, y-40), (x+w, y), config.COLOR_UNKNOWN, -1)
                            frame = put_text_vietnamese(frame, "Khong xac dinh", (x+5, y-35), font_size=20, color=(255, 255, 255))

                            # Show unknown info (with cooldown)
                            ts = time.time()
                            if ts - recognizer.last_unknown_time > config.UNKNOWN_COOLDOWN:
                                info_dict.update({
                                    'unknown': True,
                                    'time': datetime.fromtimestamp(ts).strftime("%H:%M:%S")
                                })
                                recognizer.last_unknown_time = ts

                except Exception as e:
                    if config.VERBOSE and "Face could not be detected" not in str(e):
                        print(f"Error: {e}")

        # Clear buffers for persons not detected in this frame
        for person_id in list(recognizer.detection_buffer.keys()):
            if person_id not in detected_persons:
                recognizer.detection_buffer[person_id].clear()

        # Calculate FPS
        end_time = time.time()
        frame_time = end_time - start_time
        fps = 1 / frame_time if frame_time > 0 else 0
        fps_counter.append(fps)
        avg_fps = np.mean(fps_counter)

        info_dict['fps'] = avg_fps
        info_dict['latency'] = frame_time * 1000

        # Create display frame
        display_frame = create_info_panel(frame, info_dict)

        cv2.imshow("Nhan dien khuon mat - DeepFace + ArcFace", display_frame)

        if cv2.waitKey(1) == ord('q'):
            break

    video.release()
    cv2.destroyAllWindows()
    print("\n" + "=" * 60)
    print("SYSTEM STOPPED")
    print("=" * 60)


if __name__ == '__main__':
    main()
