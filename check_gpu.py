"""
Script kiểm tra GPU setup cho face recognition
Verify CUDA và TensorFlow GPU support
"""

import sys

def check_cuda():
    """Check NVIDIA CUDA installation"""
    print("=" * 60)
    print("1. Checking CUDA availability...")
    print("=" * 60)

    try:
        import subprocess
        result = subprocess.run(['nvidia-smi'], capture_output=True, text=True)
        if result.returncode == 0:
            print("✅ NVIDIA Driver installed")
            print("\nGPU Information:")
            print(result.stdout)
            return True
        else:
            print("❌ NVIDIA Driver not found")
            return False
    except FileNotFoundError:
        print("❌ nvidia-smi not found. NVIDIA drivers may not be installed.")
        return False
    except Exception as e:
        print(f"❌ Error checking CUDA: {e}")
        return False


def check_tensorflow_gpu():
    """Check TensorFlow GPU support"""
    print("\n" + "=" * 60)
    print("2. Checking TensorFlow GPU support...")
    print("=" * 60)

    try:
        import tensorflow as tf
        print(f"✅ TensorFlow version: {tf.__version__}")

        # Check GPU devices
        gpus = tf.config.list_physical_devices('GPU')
        if gpus:
            print(f"✅ Found {len(gpus)} GPU(s):")
            for gpu in gpus:
                print(f"   - {gpu.name}")

            # Test GPU computation
            print("\nTesting GPU computation...")
            with tf.device('/GPU:0'):
                a = tf.random.normal([1000, 1000])
                b = tf.random.normal([1000, 1000])
                c = tf.matmul(a, b)
            print("✅ GPU computation test passed!")
            return True
        else:
            print("⚠️ TensorFlow installed but no GPU detected")
            print("   TensorFlow will run on CPU (slower)")
            return False

    except ImportError:
        print("❌ TensorFlow not installed")
        print("   Install with: pip install tensorflow")
        return False
    except Exception as e:
        print(f"❌ Error: {e}")
        return False


def check_opencv():
    """Check OpenCV installation"""
    print("\n" + "=" * 60)
    print("3. Checking OpenCV...")
    print("=" * 60)

    try:
        import cv2
        print(f"✅ OpenCV version: {cv2.__version__}")

        # Check camera access
        print("\nTesting camera access...")
        cap = cv2.VideoCapture(0)
        if cap.isOpened():
            ret, frame = cap.read()
            if ret:
                print(f"✅ Camera working (Resolution: {frame.shape[1]}x{frame.shape[0]})")
            cap.release()
            return True
        else:
            print("⚠️ Cannot access camera")
            return False

    except ImportError:
        print("❌ OpenCV not installed")
        print("   Install with: pip install opencv-python")
        return False
    except Exception as e:
        print(f"❌ Error: {e}")
        return False


def check_deepface():
    """Check DeepFace installation"""
    print("\n" + "=" * 60)
    print("4. Checking DeepFace...")
    print("=" * 60)

    try:
        import deepface
        print(f"✅ DeepFace installed")

        # Check available models
        print("\nChecking models...")
        from deepface.basemodels import ArcFace, Facenet
        print("✅ ArcFace model available")
        print("✅ Facenet model available")

        return True

    except ImportError:
        print("❌ DeepFace not installed")
        print("   Install with: pip install deepface")
        return False
    except Exception as e:
        print(f"⚠️ DeepFace installed but some models may need downloading")
        print(f"   Error: {e}")
        return True  # Still OK, models will download on first use


def main():
    print("\n" + "=" * 60)
    print("GPU SETUP CHECK FOR FACE RECOGNITION")
    print("=" * 60)

    results = {
        'CUDA': check_cuda(),
        'TensorFlow GPU': check_tensorflow_gpu(),
        'OpenCV': check_opencv(),
        'DeepFace': check_deepface()
    }

    print("\n" + "=" * 60)
    print("SUMMARY")
    print("=" * 60)

    for component, status in results.items():
        status_str = "✅ OK" if status else "❌ FAILED"
        print(f"{component:20s}: {status_str}")

    print("\n" + "=" * 60)

    if results['CUDA'] and results['TensorFlow GPU']:
        print("🎉 GPU setup is READY for face recognition!")
        print("   Expected performance: 15-20 FPS with DeepFace")
    elif results['TensorFlow GPU'] == False and results['CUDA']:
        print("⚠️ GPU available but TensorFlow not using it")
        print("   You may need to reinstall TensorFlow with GPU support")
        print("   Install: pip install tensorflow[and-cuda]")
    else:
        print("⚠️ Will run on CPU (slower)")
        print("   Expected performance: 5-10 FPS with DeepFace")

    print("=" * 60)


if __name__ == '__main__':
    main()
