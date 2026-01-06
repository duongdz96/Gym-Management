# Gym Management - Face Recognition System

Hệ thống nhận diện khuôn mặt cho phòng gym sử dụng DeepFace + ArcFace với độ chính xác 95-98%.

## Cài đặt

1. **Import database:**
   ```bash
   mysql -u root -p < Dump20260105.sql
   ```

2. **Cài đặt dependencies:**
   ```bash
   pip install -r requirements.txt
   ```

3. **Cấu hình MySQL:**
   Sửa `database.py`:
   ```python
   MYSQL_CONFIG = {
       'host': 'localhost',
       'user': 'user',
       'password': 'password',  # Thay đổi password
       'database': 'gympool'
   }
   ```

## Sử dụng

1. **Test connection:**
   ```bash
   python setup_mysql.py
   ```

2. **Thu thập face data:**
   ```bash
   python add_faces.py
   ```
   - Chọn member hoặc employee
   - Thu thập 50 samples với quality checks

3. **Chạy nhận diện:**
   ```bash
   python face_recognition.py
   ```
   - Real-time recognition
   - Auto check-in/out
   - Press 'q' để thoát

## Công nghệ

- **Face Detection:** RetinaFace (96-99% accuracy)
- **Face Recognition:** ArcFace (512D embeddings)
- **Similarity:** Cosine distance (threshold 0.38)
- **Database:** MySQL
- **Framework:** DeepFace, OpenCV

## Performance

- Accuracy: 95-98%
- FPS: 6-10 (CPU), 15-20 (GPU)
- Storage: ~200KB per person
- Support: 50-500 users

## Files

- `config.py` - Cấu hình hệ thống
- `database.py` - MySQL operations
- `database_embeddings.py` - Face embeddings storage
- `add_faces.py` - Thu thập face data
- `face_recognition.py` - Real-time recognition
- `setup_mysql.py` - Test connection
- `requirements.txt` - Dependencies
