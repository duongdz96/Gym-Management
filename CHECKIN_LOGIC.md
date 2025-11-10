# MEMBER CHECK-IN LOGIC

## Tổng quan

Hệ thống cho phép members check-in BẤT CỨ LÚC NÀO, không có giới hạn số lần.
Tuy nhiên, để tránh spam logs, hệ thống sử dụng **sliding window 5 phút**.

## Logic hoạt động

### Case 1: Check-in lần đầu
```
Member A vào gym lúc 10:00:00
→ Tạo AccessLog mới (log_id = 1)
→ Timestamp: 10:00:00
→ Notification: "CHECK-IN SUCCESS"
→ Console: "✓ Member A checked in (NEW)"
```

### Case 2: Check-in lại TRONG 5 phút
```
Member A check-in lúc 10:00:00
Member A ra ngoài, vào lại lúc 10:02:30 (2.5 phút sau)

→ UPDATE log_id = 1
→ Timestamp: 10:02:30 (ghi đè timestamp cũ)
→ Notification: "CHECK-IN UPDATED"
→ Console: "✓ Member A check-in updated (150s ago)"
→ Speak: "Check-in updated"

Kết quả: Chỉ có 1 log duy nhất với timestamp 10:02:30
```

### Case 3: Check-in lại SAU 5 phút
```
Member A check-in lúc 10:00:00
Member A check-in lúc 10:06:00 (6 phút sau)

→ Tạo AccessLog MỚI (log_id = 2)
→ Timestamp: 10:06:00
→ Notification: "CHECK-IN SUCCESS"
→ Console: "✓ Member A checked in (NEW)"

Kết quả: 2 logs riêng biệt (10:00:00 và 10:06:00)
```

### Case 4: Nhiều lần check-in trong 5 phút
```
10:00:00 → Check-in lần 1 → Tạo log_id = 1 (timestamp: 10:00:00)
10:01:30 → Check-in lần 2 → Update log_id = 1 (timestamp: 10:01:30)
10:03:00 → Check-in lần 3 → Update log_id = 1 (timestamp: 10:03:00)
10:04:45 → Check-in lần 4 → Update log_id = 1 (timestamp: 10:04:45)

Kết quả cuối cùng: Chỉ có 1 log với timestamp cuối cùng: 10:04:45
```

### Case 5: Chuỗi check-in dài
```
10:00:00 → Check-in → log_id = 1 (10:00:00)
10:02:00 → Check-in → Update log_id = 1 (10:02:00)
10:04:00 → Check-in → Update log_id = 1 (10:04:00)
10:07:00 → Check-in → log_id = 2 (10:07:00) - NEW vì > 5 phút từ 10:04:00
10:08:00 → Check-in → Update log_id = 2 (10:08:00)
10:15:00 → Check-in → log_id = 3 (10:15:00) - NEW vì > 5 phút từ 10:08:00

Database cuối cùng:
- Log 1: 10:04:00
- Log 2: 10:08:00
- Log 3: 10:15:00
```

## Cấu hình

### config.py

```python
# Confidence threshold - Tăng để giảm false positives
MIN_CONFIDENCE = 75  # 75% confidence required

# Sliding window size - 5 phút
MEMBER_COOLDOWN = 300  # seconds (5 minutes)

# Delay sau khi confirmed trước khi cho phép check-in
CHECK_IN_DELAY = 1.5  # seconds
```

## Lợi ích

### 1. Linh hoạt cho members
- Member có thể vào/ra tự do
- Không bị block vì "check-in quá sớm"
- Phù hợp với thực tế gym (quên đồ, đi mua nước, etc.)

### 2. Database sạch
- Không spam logs khi member vào/ra nhiều lần
- Mỗi session gym ~1 log (với timestamp cuối cùng)
- Dễ tracking và báo cáo

### 3. User Experience tốt
- Luôn nhận diện và cho feedback
- Notification rõ ràng: "SUCCESS" vs "UPDATED"
- Console log chi tiết

## Testing

### Test 1: Basic check-in
```bash
1. Run: python test_improved.py
2. Member đứng trước camera
3. Xác nhận: "CHECK-IN SUCCESS"
4. Check database: python check_database.py
```

### Test 2: Update trong 5 phút
```bash
1. Check-in lần 1
2. Đợi 2 phút
3. Check-in lần 2
4. Xác nhận: "CHECK-IN UPDATED"
5. Check database: Chỉ có 1 log với timestamp mới
```

### Test 3: New check-in sau 5 phút
```bash
1. Check-in lần 1
2. Đợi 6 phút (có thể dùng Ctrl+C và chạy lại)
3. Check-in lần 2
4. Xác nhận: "CHECK-IN SUCCESS" (new)
5. Check database: Có 2 logs riêng biệt
```

## Database Schema

### AccessLogs table
```sql
CREATE TABLE AccessLogs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    member_id INTEGER,
    access_time TEXT,  -- Format: "DD-MM-YYYY HH:MM:SS"
    FOREIGN KEY (member_id) REFERENCES Members(id)
)
```

### Update query (trong database.py)
```python
def update_access_log(log_id, new_timestamp):
    """Update existing log with new timestamp"""
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        UPDATE AccessLogs
        SET access_time = ?
        WHERE id = ?
    ''', (new_timestamp, log_id))
    conn.commit()
    conn.close()
```

## Troubleshooting

### Issue 1: Không check-in được
```
Possible causes:
1. MIN_CONFIDENCE = 75% - Có thể cần giảm xuống 70% nếu lighting kém
2. Status != 'active' - Check database member status
3. CONFIDENCE_BUFFER_SIZE = 3 - Cần 3 frames liên tiếp

Fix: Xem console log và check confidence %
```

### Issue 2: Không update log được
```
Possible cause: recognizer.last_log_ids[person_id] bị clear
Debug: Check console log xem có "No previous log found" không
```

### Issue 3: Tạo log mới thay vì update
```
Possible cause: MEMBER_COOLDOWN quá ngắn
Check: In ra (ts - last_log) trong console log
```

## Summary

**Before:**
- Members có cooldown 5 phút
- Trong cooldown: update log NHƯNG KHÔNG hiện notification
- Trải nghiệm kém cho member

**After:**
- Members LUÔN được check-in
- Trong 5 phút: update log + show "UPDATED" notification
- Sau 5 phút: tạo log mới + show "SUCCESS" notification
- Trải nghiệm tốt hơn, database vẫn sạch

---

Generated: 2025-01-09
Version: 2.0
