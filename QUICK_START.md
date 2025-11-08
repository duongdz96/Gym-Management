# ⚡ QUICK START - 5 PHÚT

## Chạy project nhanh nhất có thể

### 1️⃣ Cài đặt (2 phút)

```bash
pip install deepface scipy opencv-python
```

### 2️⃣ Thêm người vào database (1 phút)

```bash
python setup_database.py
```

**Trong menu:**
- Chọn `4` - Thêm sample data
- Hoặc chọn `2` và `3` để thêm tên thật

### 3️⃣ Thu thập face data (5-10 phút/người)

```bash
python add_faces_improved.py
```

**Làm theo:**
1. Chọn `1` (Member) hoặc `2` (Employee)
2. Chọn người từ list
3. Nhìn vào camera, đợi thu 50 samples
4. Lặp lại cho 2-3 người nữa

**Tips:**
- ✓ Ánh sáng tốt
- ✓ Nhìn thẳng camera
- ✓ Giữ đầu yên

### 4️⃣ Chạy nhận diện (ngay lập tức)

```bash
python test_improved.py
```

**Xong!** Hệ thống đang chạy.

Press `q` để thoát.

---

## 🎯 Kết quả mong đợi

- ✅ Nhận diện chính xác 95-98%
- ✅ FPS: 6-20 (tùy CPU/GPU)
- ✅ Auto check-in/out
- ✅ Hiển thị confidence score

---

## ❌ Nếu có lỗi

Xem file [HUONG_DAN_CHAY.md](HUONG_DAN_CHAY.md) để troubleshooting chi tiết.

Hoặc chạy:
```bash
python check_gpu.py
```

---

## 📊 Files quan trọng

| File | Mục đích |
|------|----------|
| `setup_database.py` | Thêm members/employees |
| `add_faces_improved.py` | Thu thập face data |
| `test_improved.py` | Nhận diện + check-in/out |
| `config.py` | Settings (threshold, etc.) |

---

## 🔧 Fine-tuning

Nếu nhận diện không chính xác, sửa trong `config.py`:

```python
# Bị từ chối nhiều → Tăng lên
RECOGNITION_THRESHOLD = 0.45

# Nhận sai người → Giảm xuống
RECOGNITION_THRESHOLD = 0.35
```

Sau đó chạy lại `test_improved.py`.

---

**That's it! Enjoy! 🎉**
