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

            # Check if all recent detections are within time window (e.g., 2 seconds)
            times = [t for _, t in self.detection_buffer[person_id]]
            if (max(times) - min(times)) < 2.0:  # All within 2 seconds
                if avg_confidence >= config.MIN_CONFIDENCE:
                    return True, avg_confidence

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
    Hiển thị thông báo thành công to và rõ ràng
    Duration: 2-3 giây
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
        message = f"Employee {action_type.upper()}"
    else:
        message = "CHECK-IN SUCCESS"

    cv2.putText(notification_frame, message, (box_x + 100, box_y + 180),
               cv2.FONT_HERSHEY_SIMPLEX, 1.2, (255, 255, 255), 3)

    # Person name
    cv2.putText(notification_frame, person_name, (box_x + 80, box_y + 240),
               cv2.FONT_HERSHEY_SIMPLEX, 1.5, (255, 255, 255), 4)

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

    # Header
    header_height = 80
    cv2.rectangle(canvas, (width + 10, 10), (width + panel_width - 10, header_height),
                  (50, 50, 50), -1)
    cv2.putText(canvas, "FACE RECOGNITION", (width + 40, 45),
                cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 255, 255), 2)
    cv2.putText(canvas, f"Model: {config.FACE_MODEL}", (width + 60, 70),
                cv2.FONT_HERSHEY_SIMPLEX, 0.5, (200, 200, 200), 1)

    # Person info
    if info_dict.get('person_detected'):
        y_offset = 120
        box_height = 220

        # Info box
        color = config.COLOR_RECOGNIZED if info_dict.get('status') == 'active' else config.COLOR_INACTIVE
        cv2.rectangle(canvas, (width + 20, y_offset),
                     (width + panel_width - 20, y_offset + box_height), (40, 40, 40), -1)
        cv2.rectangle(canvas, (width + 20, y_offset),
                     (width + panel_width - 20, y_offset + box_height), color, 2)

        # Display info
        y_text = y_offset + 35
        line_height = 35

        lines = [
            f"ID: {info_dict.get('id', 'N/A')}",
            f"Name: {info_dict.get('name', 'Unknown')}",
            f"Confidence: {info_dict.get('confidence', 0):.1f}%",
            f"Status: {info_dict.get('status', 'N/A')}",
        ]

        if info_dict.get('is_employee'):
            lines.append(f"Action: Check-{info_dict.get('check_type', 'in')}")

        if info_dict.get('confirmed'):
            lines.append("✓ CONFIRMED")

        for line in lines:
            cv2.putText(canvas, line, (width + 35, y_text),
                       cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 1)
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

    # FPS and stats
    y_stats = height - 120
    cv2.putText(canvas, f"FPS: {info_dict.get('fps', 0):.1f}",
               (width + 30, y_stats), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 0), 1)
    cv2.putText(canvas, f"Latency: {info_dict.get('latency', 0):.0f}ms",
               (width + 30, y_stats + 30), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 0), 1)
    cv2.putText(canvas, f"Threshold: {config.RECOGNITION_THRESHOLD}",
               (width + 30, y_stats + 60), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (200, 200, 200), 1)

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

                    if person_id is not None:
                        # Person recognized
                        detected_persons.add(person_id)
                        name, is_employee, status, check_type = recognizer.get_person_info(person_id)

                        # Temporal smoothing
                        is_confirmed, avg_confidence = recognizer.add_to_buffer(person_id, confidence)

                        # Color based on status
                        if status == 'active':
                            if is_confirmed:
                                color = config.COLOR_RECOGNIZED
                            else:
                                color = config.COLOR_PROCESSING
                        else:
                            color = config.COLOR_INACTIVE

                        # Draw rectangle
                        cv2.rectangle(frame, (x, y), (x+w, y+h), color, 2)
                        cv2.rectangle(frame, (x, y-35), (x+w, y), color, -1)

                        # Label
                        label = f"{name} ({avg_confidence:.0f}%)"
                        if is_confirmed:
                            label += " ✓"

                        cv2.putText(frame, label, (x+5, y-10),
                                   cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 2)

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
                        if is_confirmed and status == 'active':
                            ts = time.time()
                            date = datetime.fromtimestamp(ts).strftime("%d-%m-%Y")
                            timestamp = datetime.fromtimestamp(ts).strftime("%H:%M-%S")

                            # Check cooldown
                            last_log = recognizer.last_log_times.get(person_id, 0)

                            if ts - last_log > config.CHECK_IN_DELAY:
                                action_logged = False
                                action_type = None

                                if is_employee:
                                    # Employee check-in/out
                                    employee_id = int(person_id[4:])
                                    log_employee_access(employee_id, check_type, f"{date} {timestamp}")
                                    speak(f"Employee check {check_type}")
                                    print(f"✓ Employee {name} checked {check_type}")
                                    action_logged = True
                                    action_type = check_type
                                else:
                                    # Member check-in
                                    member_id = int(person_id)
                                    if ts - last_log > config.MEMBER_COOLDOWN:
                                        log_id = log_access(member_id, f"{date} {timestamp}")
                                        recognizer.last_log_ids[person_id] = log_id
                                        speak("Attendance taken")
                                        print(f"✓ Member {name} checked in")
                                        action_logged = True
                                        action_type = "check-in"
                                    else:
                                        # Update existing log
                                        log_id = recognizer.last_log_ids.get(person_id)
                                        if log_id:
                                            update_access_log(log_id, f"{date} {timestamp}")

                                recognizer.last_log_times[person_id] = ts
                                recognizer.clear_buffer(person_id)

                                # Show success notification for 2-3 seconds
                                if action_logged:
                                    # Create notification frame
                                    success_frame = show_success_notification(
                                        frame, name, action_type, is_employee
                                    )
                                    # Add info panel
                                    display_success = create_info_panel(success_frame, info_dict)

                                    # Show for configured duration
                                    notification_duration = config.SUCCESS_NOTIFICATION_DURATION
                                    notification_start = time.time()

                                    while (time.time() - notification_start) < notification_duration:
                                        cv2.imshow("Face Recognition - DeepFace + ArcFace", display_success)
                                        if cv2.waitKey(30) == ord('q'):
                                            break

                                    # Continue after notification
                                    continue

                    else:
                        # Unknown person
                        cv2.rectangle(frame, (x, y), (x+w, y+h), config.COLOR_UNKNOWN, 2)
                        cv2.rectangle(frame, (x, y-35), (x+w, y), config.COLOR_UNKNOWN, -1)
                        cv2.putText(frame, f"Unknown ({distance:.2f})", (x+5, y-10),
                                   cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 2)

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

        cv2.imshow("Face Recognition - DeepFace + ArcFace", display_frame)

        if cv2.waitKey(1) == ord('q'):
            break

    video.release()
    cv2.destroyAllWindows()
    print("\n" + "=" * 60)
    print("SYSTEM STOPPED")
    print("=" * 60)


if __name__ == '__main__':
    main()
