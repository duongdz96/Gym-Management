"""
Debug script để tìm lỗi check-in
"""

import config

print("=" * 70)
print("CHECK-IN DEBUG ANALYZER")
print("=" * 70)

print("\n📋 ĐIỀU KIỆN ĐỂ CHECK-IN THÀNH CÔNG:\n")

print("BƯỚC 1: NHẬN DIỆN (Recognition)")
print("-" * 70)
print(f"✅ Detector: {config.FACE_DETECTOR_REALTIME}")
print(f"✅ Model: {config.FACE_MODEL}")
print(f"✅ Threshold: {config.RECOGNITION_THRESHOLD}")
print("   → person_id phải != None")
print("   → distance < threshold ({})".format(config.RECOGNITION_THRESHOLD))

print("\nBƯỚC 2: TEMPORAL SMOOTHING (Confirmation)")
print("-" * 70)
print(f"✅ Buffer size: {config.CONFIDENCE_BUFFER_SIZE} frames")
print(f"✅ Min confidence: {config.MIN_CONFIDENCE}%")
print(f"✅ Time window: 2.0 seconds")
print("   → Cần ít nhất {} frames liên tiếp".format(config.CONFIDENCE_BUFFER_SIZE))
print("   → Tất cả trong vòng 2 giây")
print("   → Average confidence >= {}%".format(config.MIN_CONFIDENCE))
print("   → is_confirmed = True")

print("\nBƯỚC 3: STATUS CHECK")
print("-" * 70)
print("✅ Status phải = 'active'")
print("   → Kiểm tra trong database Members table")

print("\nBƯỚC 4: COOLDOWN CHECK")
print("-" * 70)
print(f"✅ CHECK_IN_DELAY: {config.CHECK_IN_DELAY}s")
print(f"✅ MEMBER_COOLDOWN: {config.MEMBER_COOLDOWN}s")
print("   → Lần đầu check-in: phải đợi {} giây sau khi confirmed".format(config.CHECK_IN_DELAY))
print("   → Check-in tiếp: phải đợi {} giây sau lần trước".format(config.MEMBER_COOLDOWN))

print("\nBƯỚC 5: TẠO LOG")
print("-" * 70)
print("✅ Nếu tất cả điều kiện trên OK:")
print("   → Gọi log_access(member_id, timestamp)")
print("   → Console phải print: '✓ Member {name} checked in'")
print("   → Hiện notification 2.5 giây")
print("   → Ghi vào AccessLogs table")

print("\n" + "=" * 70)
print("POSSIBLE ISSUES:")
print("=" * 70)

issues = []

# Check confidence threshold
if config.MIN_CONFIDENCE >= 85:
    issues.append(f"⚠️  MIN_CONFIDENCE quá cao ({config.MIN_CONFIDENCE}%) - khó đạt được")
    print(f"\n⚠️  ISSUE 1: MIN_CONFIDENCE = {config.MIN_CONFIDENCE}%")
    print("   Có thể quá cao, làm is_confirmed = False")
    print("   Khuyến nghị: Giảm xuống 70-75%")

# Check buffer size
if config.CONFIDENCE_BUFFER_SIZE >= 5:
    issues.append(f"⚠️  CONFIDENCE_BUFFER_SIZE lớn ({config.CONFIDENCE_BUFFER_SIZE} frames)")
    print(f"\n⚠️  ISSUE 2: CONFIDENCE_BUFFER_SIZE = {config.CONFIDENCE_BUFFER_SIZE}")
    print("   Cần 5 frames liên tiếp là khá khó với detector chậm")
    print("   Khuyến nghị: Giảm xuống 3 frames")

# Check threshold
if config.RECOGNITION_THRESHOLD >= 0.40:
    issues.append(f"⚠️  RECOGNITION_THRESHOLD cao ({config.RECOGNITION_THRESHOLD})")
    print(f"\n⚠️  ISSUE 3: RECOGNITION_THRESHOLD = {config.RECOGNITION_THRESHOLD}")
    print("   Threshold cao = khó nhận diện")
    print("   Khuyến nghị: Giảm xuống 0.35-0.38")

if not issues:
    print("\n✅ Không phát hiện vấn đề về config")

print("\n" + "=" * 70)
print("DEBUG CHECKLIST:")
print("=" * 70)

print("""
Khi chạy test_improved.py, quan sát:

1. □ Có thấy bounding box màu VÀNG (đang nhận diện) không?
   → Nếu KHÔNG: Detector không phát hiện mặt

2. □ Có thấy tên + confidence (xx%) không?
   → Nếu KHÔNG: Không nhận diện được (distance > threshold)
   → Nếu CÓ: Ghi lại confidence là bao nhiêu %?

3. □ Có thấy dấu ✓ (checkmark) sau tên không?
   → Nếu KHÔNG: is_confirmed = False (chưa đủ 5 frames hoặc confidence thấp)
   → Nếu CÓ: is_confirmed = True → Sắp check-in

4. □ Sau khi thấy ✓, có thấy notification màu xanh 2.5s không?
   → Nếu KHÔNG: Cooldown chưa hết hoặc status != 'active'
   → Nếu CÓ: Đã check-in thành công!

5. □ Console có print "✓ Member {name} checked in" không?
   → Nếu KHÔNG: Không gọi log_access()
   → Nếu CÓ: Phải có log trong database

6. □ Database có log không?
   → Chạy: python check_database.py
""")

print("=" * 70)
print("\nKHUYẾN NGHỊ FIX:")
print("=" * 70)

print("""
Thử giảm các threshold để dễ check-in hơn:

1. Giảm MIN_CONFIDENCE:
   config.py: MIN_CONFIDENCE = 70  # Từ 80 → 70

2. Giảm CONFIDENCE_BUFFER_SIZE:
   config.py: CONFIDENCE_BUFFER_SIZE = 3  # Từ 5 → 3

3. Giảm RECOGNITION_THRESHOLD:
   config.py: RECOGNITION_THRESHOLD = 0.35  # Từ 0.40 → 0.35

4. Kiểm tra status:
   Chạy: python check_database.py
   Đảm bảo Members có status = 'active'
""")

print("=" * 70)
