# Baseline Version - Backup

Các files trong folder này là **baseline version** (Haar Cascade + KNN).

## 📁 Files:

- `test.py` - Recognition với KNN (62-68% accuracy)
- `add_faces.py` - Thu thập faces với Haar Cascade
- `add_face_ui.py` - UI Tkinter cho baseline
- `add_test_users.py` - Helper thêm test users
- `add_test_employees.py` - Helper thêm test employees
- `app.py` - Streamlit web app
- `reset_db.py` - Reset database utility

## ⚠️ Lưu ý:

Project hiện tại **CHỈ SỬ DỤNG Improved Version** (DeepFace + ArcFace).

Baseline files được giữ lại ở đây để:
- Backup phòng khi cần
- Tham khảo code cũ
- So sánh nếu cần thiết

## 🔄 Khôi phục:

Nếu cần dùng lại baseline:
```bash
# Copy files từ backup ra root
cp baseline_backup/*.py .
```

## 📊 Performance:

Baseline version:
- Accuracy: 62-68%
- Technology: Haar Cascade + KNN
- Face data: 100 ảnh 50x50 pixels/person

Improved version (đang dùng):
- Accuracy: 95-98%
- Technology: RetinaFace + ArcFace
- Embeddings: 50 vectors 512D/person
