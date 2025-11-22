# DATABASE MIGRATION GUIDE

## Mục đích

Đồng bộ SQLite face recognition database với MySQL backend của gym system để:
1. **Không duplicate data** - Members data chỉ quản lý ở MySQL
2. **Sync real-time** - Face recognition system luôn có data mới nhất
3. **Compatible schema** - AccessLogs có thể sync với MySQL nếu cần
4. **Production-ready** - Chuẩn bị cho deploy thật

## Schema Changes

### BEFORE (Old SQLite):
```sql
Members:
  - id (AUTOINCREMENT)  ❌ Tự sinh ID → duplicate với MySQL
  - name                ❌ Thiếu info (email, phone, etc.)
  - status
  - face_data (BLOB)    ❌ Mixed data structure

AccessLogs:
  - id
  - member_id
  - access_time         ❌ Thiếu metadata (method, device, result)
```

### AFTER (New SQLite - MySQL-compatible):
```sql
Members:
  - id (NOT AUTOINCREMENT) ✅ Sync từ MySQL users.id
  - full_name
  - email, phone, dob, gender, role
  - card_id, face_id    ✅ Từ MySQL members table
  - membership, join_date, status

MemberEmbeddings:       ✅ Tách riêng face data
  - id (AUTOINCREMENT)
  - member_id (FK)
  - embeddings (BLOB)
  - model_name
  - created_at

AccessLogs:
  - id
  - member_id
  - access_time
  - access_method       ✅ FACE, CARD, MANUAL
  - device_id           ✅ Device tracking
  - location_type       ✅ MAIN_ENTRANCE, GYM_FLOOR, etc.
  - result              ✅ SUCCESS, FAIL
  - reason              ✅ Reason if fail
```

## Migration Steps

### ⚠️ QUAN TRỌNG: Backup trước!

```bash
# Backup MySQL
mysqldump -u root -p gymsystem > backup_mysql_$(date +%Y%m%d).sql

# Backup SQLite (tự động trong script)
# File: data/face_recognition_backup_YYYYMMDD_HHMMSS.db
```

### Step 1: Run Migration Script

```bash
python migrate_to_mysql_schema.py
```

**Điều gì xảy ra:**
- Backup SQLite hiện tại tự động
- Tạo tables mới với MySQL-compatible schema
- Giữ nguyên data cũ (nếu có)
- Tạo indexes cho performance

**Output mẫu:**
```
[Step 1] Checking current database...
[Step 2] Creating backup...
✅ Backup created: data/face_recognition_backup_20250109_143052.db

[Step 3] Running migration...
[1] Checking existing tables...
[2] Creating Members table (MySQL-compatible schema)...
    ✅ Members table created/verified
[3] Creating MemberEmbeddings table...
    ✅ MemberEmbeddings table created/verified
...
✅ MIGRATION COMPLETE!
```

### Step 2: Configure MySQL Connection

Mở file `sync_members_from_mysql.py` và update MySQL config:

```python
MYSQL_CONFIG = {
    'host': 'localhost',
    'user': 'root',          # ← Change this
    'password': 'your_pass',  # ← Change this
    'database': 'gymsystem'
}
```

### Step 3: Sync Members từ MySQL

```bash
python sync_members_from_mysql.py
```

**Điều gì xảy ra:**
- Connect MySQL backend
- Query members từ `users` + `members` tables
- Insert/Update vào SQLite
- Sync employees (staffs) nếu muốn

**Output mẫu:**
```
✅ Connected to MySQL

SYNCING MEMBERS FROM MYSQL TO SQLITE

📊 Found 5 members in MySQL
   ✅ Synced: Nguyen Van A (ID: 1)
   ✅ Synced: Tran Thi B (ID: 7)
   ✅ Synced: Le Van C (ID: 8)
   ✅ Synced: Pham Thi D (ID: 9)
   ✅ Synced: Do Van E (ID: 10)

✅ SYNC COMPLETE!
   • New members: 5
   • Updated members: 0
   • Total: 5

📊 Total members in SQLite: 5

📋 Members status:
Name                           Status          Face Data
-------------------------------------------------------
Nguyen Van A                   Active          No
Tran Thi B                     Inactive        No
Le Van C                       Active          No
Pham Thi D                     Suspended       No
Do Van E                       Active          No
```

### Step 4: Collect Face Data

Bây giờ members đã có trong SQLite, thu thập face data:

```bash
python add_faces_improved.py
```

**Chọn member từ danh sách:**
```
Matching Members:
1. Nguyen Van A (ID: 1)
2. Le Van C (ID: 8)
3. Do Van E (ID: 10)

Enter the number of the member: 1
```

**Thu thập 10 samples** với RetinaFace + ArcFace (accuracy mode)

**Điều gì xảy ra:**
- Face data được lưu vào `MemberEmbeddings` table
- `member_id` link với `Members.id` (từ MySQL)
- Model name được lưu (ArcFace, Facenet, etc.)

### Step 5: Test Face Recognition

```bash
python test_improved.py
```

**Điều gì xảy ra:**
- Load members từ SQLite (đã sync từ MySQL)
- Load face embeddings từ `MemberEmbeddings`
- Recognize faces real-time
- Log check-ins vào `AccessLogs` với đầy đủ metadata

**AccessLogs format mới:**
```
ID  Member  Time                 Method  Device                       Location        Result
1   1       09-01-2025 14:30:25  FACE    FACE_RECOGNITION_DEVICE_01  MAIN_ENTRANCE   SUCCESS
2   8       09-01-2025 14:32:18  FACE    FACE_RECOGNITION_DEVICE_01  MAIN_ENTRANCE   SUCCESS
3   1       09-01-2025 14:35:00  FACE    FACE_RECOGNITION_DEVICE_01  MAIN_ENTRANCE   SUCCESS (Updated)
```

## Lợi ích của Migration

### 1. Single Source of Truth
```
Before:
  MySQL: Nguyen Van A (ID: 1, membership: Premium)
  SQLite: Nguyen Van A (ID: 5, status: active)  ❌ Duplicate!

After:
  MySQL: Nguyen Van A (ID: 1, membership: Premium)
  SQLite: Nguyen Van A (ID: 1) ✅ References MySQL
          + Face embeddings in MemberEmbeddings
```

### 2. Easy Sync
```bash
# Khi có member mới trong MySQL:
python sync_members_from_mysql.py  # Chỉ update/insert mới
python add_faces_improved.py       # Thu thập face cho member mới
```

### 3. Rich Metadata
```sql
-- Old AccessLogs:
member_id | access_time
1         | 09-01-2025 14:30:25

-- New AccessLogs:
member_id | access_time         | method | device                    | location       | result
1         | 09-01-2025 14:30:25 | FACE   | FACE_RECOGNITION_DEVICE_01| MAIN_ENTRANCE  | SUCCESS
```

### 4. Production Ready

Có thể push AccessLogs về MySQL backend:

```python
# Future feature: Sync logs back to MySQL
def sync_logs_to_mysql():
    """
    Push SQLite AccessLogs → MySQL access_logs table
    For centralized reporting và analytics
    """
    # Query new logs from SQLite
    # Insert into MySQL access_logs
    # Update sync status
```

## Data Flow Architecture

```
┌─────────────────────────────────────────────────────────┐
│              MYSQL BACKEND (Spring Boot)                │
│                                                          │
│  users table        members table       access_logs     │
│  ├─ id             ├─ id (FK)          ├─ id           │
│  ├─ full_name      ├─ card_id          ├─ member_id    │
│  ├─ email          ├─ face_id          ├─ access_time  │
│  └─ ...            └─ ...              └─ ...          │
│                                                          │
│  [Web Dashboard] [Mobile App] [Reports] [Analytics]     │
└─────────────────────────────────────────────────────────┘
                              ↕
                      sync_members_from_mysql.py
                      (One-way: MySQL → SQLite)
                              ↓
┌─────────────────────────────────────────────────────────┐
│           SQLITE (Face Recognition System)              │
│                                                          │
│  Members                MemberEmbeddings                │
│  ├─ id (from MySQL)    ├─ member_id (FK)               │
│  ├─ full_name          ├─ embeddings (BLOB)            │
│  ├─ email              ├─ model_name                    │
│  ├─ status             └─ created_at                    │
│  └─ ...                                                 │
│                                                          │
│  AccessLogs (MySQL-compatible)                          │
│  ├─ member_id                                           │
│  ├─ access_time                                         │
│  ├─ access_method (FACE)                                │
│  └─ ...                                                 │
│                                                          │
│  [Face Detection] [Face Recognition] [Check-in]         │
└─────────────────────────────────────────────────────────┘
                              ↕
                      Camera + DeepFace
                              ↓
                         Real-time Recognition
```

## Troubleshooting

### Issue 1: MySQL connection error

```bash
❌ MySQL connection error: Access denied for user 'root'@'localhost'
```

**Fix:**
- Check MySQL credentials trong `sync_members_from_mysql.py`
- Test connection: `mysql -u root -p gymsystem`

### Issue 2: Table already exists

```bash
❌ Table 'Members' already exists
```

**Fix:**
- Migration script sử dụng `CREATE TABLE IF NOT EXISTS` → safe to run multiple times
- Nếu muốn reset hoàn toàn:
  ```bash
  rm data/face_recognition.db
  python migrate_to_mysql_schema.py
  ```

### Issue 3: No members found

```bash
📊 Found 0 members in MySQL
```

**Fix:**
- Check MySQL có data: `SELECT * FROM members;`
- Check `is_deleted = 0` filter
- Check `role = 'MEMBER'`

### Issue 4: Face recognition không hoạt động

**Possible causes:**
1. Chưa thu thập face data → Run `add_faces_improved.py`
2. Member status không phải 'Active' → Update trong MySQL
3. Embeddings table empty → Check `MemberEmbeddings`

**Debug:**
```bash
python check_database.py  # Xem database status
```

## Next Steps

Sau khi migration xong:

1. ✅ **Test check-in:** Run `test_improved.py` và verify logs
2. ✅ **Verify data:** Check `check_database.py` output
3. ✅ **Add more members:** Sync from MySQL periodically
4. ✅ **Backup regularly:** Keep backups of both MySQL and SQLite

## Rollback (Nếu cần)

```bash
# Restore from backup
cp data/face_recognition_backup_YYYYMMDD_HHMMSS.db data/face_recognition.db

# Hoặc reset hoàn toàn
rm data/face_recognition.db
python database.py  # Sử dụng schema cũ
```

## Summary

**Migration này giúp:**
- ✅ Đồng bộ members từ MySQL backend
- ✅ Schema tương thích với production
- ✅ Metadata đầy đủ cho AccessLogs
- ✅ Dễ maintain và scale
- ✅ Ready để integrate với backend

**Không mất:**
- ❌ Không mất data cũ (có backup)
- ❌ Không ảnh hưởng MySQL
- ❌ Có thể rollback bất cứ lúc nào

---

Generated: 2025-01-09
For: DATN - Gym Management Face Recognition System
