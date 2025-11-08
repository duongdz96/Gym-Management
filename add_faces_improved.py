"""
Thu thập face embeddings với DeepFace - ACCURACY FOCUS
Sử dụng RetinaFace detector + ArcFace model
Có quality control nghiêm ngặt
"""

import cv2
import numpy as np
from deepface import DeepFace
from scipy.spatial.distance import cosine
import time

# Import từ modules khác
from database import get_all_active_members, get_all_active_employees
from database_embeddings import save_member_embeddings, save_employee_embeddings
import config


def is_good_quality_face(image):
    """
    Kiểm tra chất lượng face image
    Returns: (is_good, reason)
    """
    try:
        # Convert to grayscale nếu cần
        if len(image.shape) == 3:
            gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        else:
            gray = image

        # 1. Blur detection
        blur_score = cv2.Laplacian(gray, cv2.CV_64F).var()
        if blur_score < config.BLUR_THRESHOLD:
            return False, f"Too blurry ({blur_score:.0f})"

        # 2. Brightness check
        brightness = np.mean(gray)
        if brightness < config.MIN_BRIGHTNESS:
            return False, f"Too dark ({brightness:.0f})"
        if brightness > config.MAX_BRIGHTNESS:
            return False, f"Too bright ({brightness:.0f})"

        # 3. Face size check
        if image.shape[0] < config.MIN_FACE_SIZE or image.shape[1] < config.MIN_FACE_SIZE:
            return False, f"Face too small"

        # 4. Contrast check
        contrast = gray.std()
        if contrast < config.MIN_CONTRAST:
            return False, f"Low contrast ({contrast:.0f})"

        return True, "Good quality"

    except Exception as e:
        return False, f"Error: {str(e)}"


def select_person(person_type='member'):
    """
    Chọn member hoặc employee để thu thập face data

    Args:
        person_type: 'member' hoặc 'employee'
    """
    if person_type == 'member':
        persons = get_all_active_members()
        title = "Members"
    else:
        persons = get_all_active_employees()
        title = "Employees"

    if not persons:
        print(f"No active {person_type}s found.")
        return None, None

    search_term = input(f"Search {person_type} by name (leave empty for all): ").lower()
    filtered = [(id, name) for id, name in persons if search_term in name.lower()] if search_term else persons

    if not filtered:
        print("No matches found.")
        return None, None

    print(f"\nMatching {title}:")
    for i, (id, name) in enumerate(filtered):
        print(f"{i+1}. {name} (ID: {id})")

    while True:
        try:
            choice = int(input(f"\nEnter the number of the {person_type}: ")) - 1
            if 0 <= choice < len(filtered):
                person_id, person_name = filtered[choice]
                return person_id, person_name
            else:
                print("Invalid choice. Try again.")
        except ValueError:
            print("Please enter a number.")


def collect_face_embeddings(person_id, person_name, person_type='member'):
    """
    Thu thập face embeddings với quality control

    Args:
        person_id: ID của person
        person_name: Tên person
        person_type: 'member' hoặc 'employee'

    Returns:
        embeddings array hoặc None
    """
    print("\n" + "=" * 60)
    print(f"COLLECTING FACES FOR: {person_name} (ID: {person_id})")
    print("=" * 60)
    print(f"Model: {config.FACE_MODEL}")
    print(f"Detector: {config.FACE_DETECTOR}")
    print(f"Target: {config.NUM_SAMPLES} high-quality samples")
    print("\nTIPS FOR BEST RESULTS:")
    print("  ✓ Look straight at camera")
    print("  ✓ Good lighting (not too dark/bright)")
    print("  ✓ Keep face still when capturing")
    print("  ✓ Remove glasses if possible (or collect both with/without)")
    print("  ✓ Neutral expression")
    print("\nPress 'q' to quit")
    print("=" * 60)

    # Open camera
    video = cv2.VideoCapture(0, cv2.CAP_DSHOW)
    video.set(cv2.CAP_PROP_FRAME_WIDTH, config.CAMERA_WIDTH)
    video.set(cv2.CAP_PROP_FRAME_HEIGHT, config.CAMERA_HEIGHT)
    video.set(cv2.CAP_PROP_FPS, config.CAMERA_FPS)

    embeddings = []
    frame_count = 0
    last_capture_time = 0
    capture_cooldown = 0.5  # seconds between captures

    # For smooth preview
    last_face_bbox = None  # Cache last detected face position

    while len(embeddings) < config.NUM_SAMPLES:
        ret, frame = video.read()
        if not ret:
            print("Failed to read frame")
            continue

        frame = cv2.flip(frame, 1)  # Mirror
        frame_count += 1
        current_time = time.time()

        # Create display frame
        display_frame = frame.copy()

        # Progress bar
        progress = len(embeddings) / config.NUM_SAMPLES
        bar_width = 400
        bar_height = 30
        bar_x = 50
        bar_y = 50

        # Background
        cv2.rectangle(display_frame, (bar_x, bar_y), (bar_x + bar_width, bar_y + bar_height), (50, 50, 50), -1)
        # Progress
        filled_width = int(bar_width * progress)
        cv2.rectangle(display_frame, (bar_x, bar_y), (bar_x + filled_width, bar_y + bar_height), (0, 255, 0), -1)
        # Border
        cv2.rectangle(display_frame, (bar_x, bar_y), (bar_x + bar_width, bar_y + bar_height), (255, 255, 255), 2)
        # Text
        progress_text = f"{len(embeddings)}/{config.NUM_SAMPLES} ({progress*100:.1f}%)"
        cv2.putText(display_frame, progress_text, (bar_x + 120, bar_y + 20),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 2)

        # Instructions
        cv2.putText(display_frame, "Position your face in the center", (50, 100),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 255, 255), 2)

        # PERFORMANCE: Preview with lightweight detector (every frame for smooth display)
        try:
            # Use OpenCV for fast preview
            preview_faces = DeepFace.extract_faces(
                frame,
                detector_backend='opencv',  # Fast detector for preview
                enforce_detection=False,
                align=False  # Don't align for preview (faster)
            )

            if len(preview_faces) > 0:
                # Draw preview rectangle (yellow = preview mode)
                face_area = preview_faces[0]['facial_area']
                px, py, pw, ph = face_area['x'], face_area['y'], face_area['w'], face_area['h']
                cv2.rectangle(display_frame, (px, py), (px+pw, py+ph), (0, 255, 255), 2)
                last_face_bbox = (px, py, pw, ph)

                if len(preview_faces) > 1:
                    cv2.putText(display_frame, "Multiple faces! Show only yours", (50, 150),
                                cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 165, 255), 2)
            else:
                cv2.putText(display_frame, "No face detected", (50, 150),
                            cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)
                last_face_bbox = None

        except Exception as e:
            pass  # Ignore preview errors

        # CAPTURE: Only process every N frames with high-quality detector
        if frame_count % config.FRAME_SKIP == 0 and (current_time - last_capture_time) > capture_cooldown:
            # Only try to capture if preview detected a single face
            if last_face_bbox is not None and len(preview_faces) == 1:
                try:
                    # Now use RetinaFace for high-quality capture
                    face_objs = DeepFace.extract_faces(
                        frame,
                        detector_backend=config.FACE_DETECTOR,  # RetinaFace for accuracy
                        enforce_detection=False,
                        align=config.ALIGN_FACE
                    )

                    if len(face_objs) == 0:
                        continue

                    # Get face
                    face_obj = face_objs[0]
                    facial_area = face_obj['facial_area']
                    x, y, w, h = facial_area['x'], facial_area['y'], facial_area['w'], facial_area['h']

                    # Draw high-quality detection rectangle (green)
                    cv2.rectangle(display_frame, (x, y), (x+w, y+h), (0, 255, 0), 3)

                    # Get face image
                    face_img = face_obj['face']
                    # DeepFace returns normalized float, convert to uint8
                    if face_img.dtype == np.float32 or face_img.dtype == np.float64:
                        face_img = (face_img * 255).astype(np.uint8)

                    # Quality check
                    is_good, reason = is_good_quality_face(face_img)

                    if not is_good:
                        cv2.putText(display_frame, f"Quality: {reason}", (x, y - 10),
                                    cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 0, 255), 2)
                        cv2.imshow("Collecting Faces", display_frame)
                        cv2.waitKey(1)
                        continue

                    # Extract embedding
                    embedding_obj = DeepFace.represent(
                        face_img,
                        model_name=config.FACE_MODEL,
                        detector_backend='skip',  # Already detected
                        enforce_detection=False
                    )

                    embedding = np.array(embedding_obj[0]['embedding'])

                    # Diversity check
                    if len(embeddings) > 0:
                        last_embedding = embeddings[-1]
                        similarity = 1 - cosine(embedding, last_embedding)
                        if similarity > config.MAX_SAMPLE_SIMILARITY:
                            cv2.putText(display_frame, "Too similar to last sample, move slightly",
                                        (50, 150), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 165, 255), 2)
                            cv2.imshow("Collecting Faces", display_frame)
                            cv2.waitKey(1)
                            continue

                    # SUCCESS! Save embedding
                    embeddings.append(embedding)
                    last_capture_time = current_time

                    # Visual feedback
                    cv2.rectangle(display_frame, (x, y), (x+w, y+h), (0, 255, 0), 3)
                    cv2.putText(display_frame, "✓ CAPTURED", (x, y - 10),
                                cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0, 255, 0), 2)

                    # Flash effect
                    flash = display_frame.copy()
                    cv2.addWeighted(flash, 0.7, np.ones_like(flash) * 255, 0.3, 0, flash)
                    cv2.imshow("Collecting Faces", flash)
                    cv2.waitKey(100)

                except Exception as e:
                    error_msg = str(e)
                    if "Face could not be detected" not in error_msg:
                        print(f"Capture error: {e}")

        cv2.imshow("Collecting Faces", display_frame)

        key = cv2.waitKey(1)
        if key == ord('q'):
            print("\nCollection cancelled by user")
            break

    video.release()
    cv2.destroyAllWindows()

    # Save if enough samples
    if len(embeddings) >= config.NUM_SAMPLES:
        embeddings_array = np.array(embeddings)
        print(f"\n✅ Successfully collected {len(embeddings)} embeddings")
        print(f"   Shape: {embeddings_array.shape}")

        # Save to database
        if person_type == 'member':
            save_member_embeddings(person_id, embeddings_array, config.FACE_MODEL)
        else:
            save_employee_embeddings(person_id, embeddings_array, config.FACE_MODEL)

        print(f"✅ Saved embeddings for {person_name}")
        return embeddings_array
    else:
        print(f"\n❌ Only collected {len(embeddings)} samples (need {config.NUM_SAMPLES})")
        print("   Data not saved.")
        return None


def main():
    """Main function"""
    print("\n" + "=" * 60)
    print("FACE EMBEDDINGS COLLECTION - DEEPFACE")
    print("=" * 60)

    # Show config
    config.print_config()

    # Choose person type
    print("\nSelect type:")
    print("1. Member (check-in only)")
    print("2. Employee (check-in/out)")

    while True:
        try:
            choice = int(input("Enter choice (1 or 2): "))
            if choice == 1:
                person_type = 'member'
                break
            elif choice == 2:
                person_type = 'employee'
                break
            else:
                print("Invalid choice")
        except ValueError:
            print("Please enter a number")

    # Select person
    person_id, person_name = select_person(person_type)

    if person_id is None:
        print("No person selected. Exiting.")
        return

    # Collect embeddings
    result = collect_face_embeddings(person_id, person_name, person_type)

    if result is not None:
        print("\n" + "=" * 60)
        print("✅ COLLECTION COMPLETE!")
        print("=" * 60)
        print(f"Person: {person_name}")
        print(f"Samples: {len(result)}")
        print(f"Model: {config.FACE_MODEL}")
        print(f"Embedding dimension: {result.shape[1]}D")
        print("=" * 60)
    else:
        print("\n❌ Collection failed or incomplete")


if __name__ == '__main__':
    main()
