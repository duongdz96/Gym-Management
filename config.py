"""
Configuration cho Face Recognition System
Tối ưu cho ACCURACY cao nhất
"""

# ============================================================
# FACE DETECTION SETTINGS
# ============================================================

# Face detector backend
# Options: 'opencv', 'ssd', 'dlib', 'mtcnn', 'retinaface', 'mediapipe'
# retinaface = Best accuracy (96-99%)
FACE_DETECTOR = 'retinaface'

# Enforce detection (raise error if no face found)
ENFORCE_DETECTION = True

# Face alignment (rotate face to align eyes horizontally)
ALIGN_FACE = True

# ============================================================
# FACE RECOGNITION MODEL SETTINGS
# ============================================================

# Model cho face embeddings
# Options: 'VGG-Face', 'Facenet', 'Facenet512', 'OpenFace', 'DeepFace', 'DeepID', 'ArcFace', 'Dlib', 'SFace'
# ArcFace = Best accuracy (95-99%)
FACE_MODEL = 'ArcFace'

# Distance metric
# Options: 'cosine', 'euclidean', 'euclidean_l2'
# cosine = Best for normalized embeddings
DISTANCE_METRIC = 'cosine'

# Recognition threshold
# Lower = stricter (less false accepts, more false rejects)
# Higher = looser (more false accepts, less false rejects)
# Optimal range for ArcFace + cosine: 0.35 - 0.45
RECOGNITION_THRESHOLD = 0.38  # Giảm từ 0.40 → 0.38 để dễ nhận diện hơn

# ============================================================
# DATA COLLECTION SETTINGS
# ============================================================

# Số lượng face samples cần thu thập
NUM_SAMPLES = 50  # Đủ cho accuracy cao, không quá nhiều

# Camera resolution
CAMERA_WIDTH = 1280  # Higher resolution = better quality
CAMERA_HEIGHT = 720

# FPS
CAMERA_FPS = 30

# Frame skip (chỉ process mỗi N frames)
# Giảm load, tránh thu quá nhiều ảnh giống nhau
FRAME_SKIP = 10

# ============================================================
# QUALITY CONTROL SETTINGS
# ============================================================

# Blur detection threshold (Laplacian variance)
# Higher = less blurry required
BLUR_THRESHOLD = 100

# Brightness range (0-255)
MIN_BRIGHTNESS = 40
MAX_BRIGHTNESS = 220

# Minimum face size (pixels)
MIN_FACE_SIZE = 80

# Contrast threshold (standard deviation)
MIN_CONTRAST = 30

# Maximum similarity between consecutive samples
# Đảm bảo diversity trong training data
MAX_SAMPLE_SIMILARITY = 0.95

# ============================================================
# RECOGNITION SETTINGS
# ============================================================

# Temporal smoothing - số frames liên tiếp để confirm
# Giảm false positives
# 5 frames quá khó với MTCNN, giảm xuống 3 để dễ trigger
CONFIDENCE_BUFFER_SIZE = 3  # Giảm từ 5 → 3 để dễ check-in

# Minimum average confidence để trigger action
MIN_CONFIDENCE = 70  # Giảm từ 80 → 70 để dễ check-in

# Delay trước khi check-in/out (seconds)
# Tránh check-in nhiều lần liên tiếp
CHECK_IN_DELAY = 1.5

# Cooldown period giữa các check-ins của cùng 1 người (seconds)
# Members: 10 giây cho testing (production: 300s = 5 phút)
MEMBER_COOLDOWN = 10  # Giảm xuống 10s để test dễ hơn

# Unknown face display cooldown (seconds)
# Tránh spam "Unknown" message
UNKNOWN_COOLDOWN = 3

# Success notification display duration (seconds)
# Hiển thị thông báo thành công trong bao lâu
SUCCESS_NOTIFICATION_DURATION = 2.5  # 2-3 giây để user thấy rõ

# ============================================================
# UI SETTINGS
# ============================================================

# Info panel width (pixels)
INFO_PANEL_WIDTH = 400

# Colors (BGR format)
COLOR_RECOGNIZED = (0, 255, 0)      # Green
COLOR_UNKNOWN = (0, 0, 255)         # Red
COLOR_PROCESSING = (0, 255, 255)    # Yellow
COLOR_INACTIVE = (128, 128, 128)    # Gray

# Font settings
FONT = 0  # cv2.FONT_HERSHEY_SIMPLEX
FONT_SCALE = 0.7
FONT_THICKNESS = 2

# ============================================================
# PERFORMANCE SETTINGS (Tối ưu cho real-time)
# ============================================================

# Enable GPU if available
USE_GPU = True

# FRAME SKIPPING - Chỉ process mỗi N frames
# 1 = process mọi frame (smooth, accuracy cao) ⭐ OPTION 1
# 2 = process 1/2 frames (cân bằng tốt hơn)
# 3 = process 1/3 frames (nhanh hơn)
FRAME_SKIP_REALTIME = 1  # Option 1: Process every frame (accuracy first)

# Resolution cho real-time recognition
# Full resolution = accuracy cao
RECOGNITION_WIDTH = 1280  # Option 1: Full resolution
RECOGNITION_HEIGHT = 720  # Option 1: Full resolution

# Detector cho real-time (options: 'opencv', 'mtcnn', 'retinaface')
# opencv = nhanh nhất (~10x), accuracy ~85-90%
# mtcnn = cân bằng (~3x nhanh hơn RetinaFace), accuracy ~95% ⭐ OPTION 1
# retinaface = chậm nhất (2-3 FPS), accuracy cao nhất ~97-99%
FACE_DETECTOR_REALTIME = 'mtcnn'  # Option 1: MTCNN + Full res + No skip

# Disable quality check trong recognition (không cần thiết)
SKIP_QUALITY_CHECK_REALTIME = True

# Batch processing (nếu recognize nhiều faces cùng lúc)
BATCH_SIZE = 4

# Cache embeddings in memory (faster but uses more RAM)
CACHE_EMBEDDINGS = True

# ============================================================
# LOGGING & DEBUG
# ============================================================

# Enable verbose logging
VERBOSE = True

# Save failed recognitions for analysis
SAVE_UNKNOWNS = False

# Log directory
LOG_DIR = 'logs/'

# ============================================================
# THRESHOLDS FOR DIFFERENT MODELS (Reference)
# ============================================================

# Nếu sau này muốn đổi model, dùng threshold tương ứng
MODEL_THRESHOLDS = {
    'VGG-Face': {
        'cosine': 0.40,
        'euclidean': 0.60,
        'euclidean_l2': 0.86
    },
    'Facenet': {
        'cosine': 0.40,
        'euclidean': 10,
        'euclidean_l2': 0.80
    },
    'Facenet512': {
        'cosine': 0.30,
        'euclidean': 23.56,
        'euclidean_l2': 1.04
    },
    'ArcFace': {
        'cosine': 0.40,  # ⭐ Đang dùng
        'euclidean': 4.15,
        'euclidean_l2': 1.13
    },
    'OpenFace': {
        'cosine': 0.10,
        'euclidean': 0.55,
        'euclidean_l2': 0.55
    }
}

# ============================================================
# HELPER FUNCTIONS
# ============================================================

def get_optimal_threshold(model_name=FACE_MODEL, metric=DISTANCE_METRIC):
    """
    Lấy threshold tối ưu cho model và metric
    """
    if model_name in MODEL_THRESHOLDS and metric in MODEL_THRESHOLDS[model_name]:
        return MODEL_THRESHOLDS[model_name][metric]
    return RECOGNITION_THRESHOLD


def print_config():
    """
    In ra configuration hiện tại
    """
    print("=" * 60)
    print("FACE RECOGNITION CONFIGURATION")
    print("=" * 60)
    print(f"Face Detector       : {FACE_DETECTOR}")
    print(f"Face Model          : {FACE_MODEL}")
    print(f"Distance Metric     : {DISTANCE_METRIC}")
    print(f"Threshold           : {RECOGNITION_THRESHOLD}")
    print(f"Samples per person  : {NUM_SAMPLES}")
    print(f"Camera Resolution   : {CAMERA_WIDTH}x{CAMERA_HEIGHT}")
    print(f"Quality Control     : Enabled")
    print(f"  - Blur threshold  : {BLUR_THRESHOLD}")
    print(f"  - Brightness      : {MIN_BRIGHTNESS}-{MAX_BRIGHTNESS}")
    print(f"  - Min face size   : {MIN_FACE_SIZE}px")
    print(f"Temporal Smoothing  : {CONFIDENCE_BUFFER_SIZE} frames")
    print(f"GPU                 : {'Enabled' if USE_GPU else 'Disabled'}")
    print("=" * 60)


if __name__ == '__main__':
    print_config()
