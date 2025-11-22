# 🔄 DATABASE MIGRATION - Quick Start

## Tại sao cần Migration?

Bạn có 2 databases đang chạy riêng lẻ:
- **MySQL (Backend)**: Quản lý members, employees, bills, classes (Spring Boot)
- **SQLite (Face Recognition)**: Quản lý face data và check-in logs (Python)

❌ **Vấn đề:** Data duplicate, không đồng bộ, khó maintain

✅ **Giải pháp:** Sync SQLite với MySQL, sử dụng MySQL làm single source of truth

## 📋 Checklist

### Trước khi bắt đầu:

- [ ] MySQL server đang chạy
- [ ] Database `gymsystem` có data (members, employees)
- [ ] Python dependencies đã cài (`pip install mysql-connector-python`)
- [ ] Backup data quan trọng

### Migration Process (5 phút):

```bash
# Step 1: Migrate schema (1 phút)
python migrate_to_mysql_schema.py
# → Tạo tables mới, tự động backup

# Step 2: Update MySQL credentials (30 giây)
# Edit sync_members_from_mysql.py:
# MYSQL_CONFIG = {'user': 'root', 'password': 'your_password'}

# Step 3: Sync members (1 phút)
python sync_members_from_mysql.py
# → Import 5 members từ MySQL vào SQLite

# Step 4: Collect face data (2-3 phút/member)
python add_faces_improved.py
# → Thu thập 10 samples cho mỗi member

# Step 5: Test (30 giây)
python test_improved.py
# → Face recognition với data mới
```

## 🎯 Kết quả

### Before Migration:

```
SQLite Members:
  ID: 1, Name: "Test Member"  ❌ Không có email, phone
  ID: 2, Name: "John Doe"     ❌ Duplicate với MySQL

MySQL members:
  ID: 1, Name: "Nguyen Van A" ✅ Full info
  ID: 7, Name: "Tran Thi B"   ✅ Full info
```

### After Migration:

```
SQLite Members (synced từ MySQL):
  ID: 1, Name: "Nguyen Van A", Email, Phone, Membership ✅
  ID: 7, Name: "Tran Thi B", Email, Phone, Membership ✅
  + Face embeddings in MemberEmbeddings table

AccessLogs (MySQL-compatible):
  ID, Member, Time, Method (FACE), Device, Location, Result ✅
```

## 📊 So sánh Tables

| Feature | Old SQLite | New SQLite (After Migration) | MySQL Backend |
|---------|-----------|------------------------------|---------------|
| **Member ID** | Auto-increment ❌ | From MySQL ✅ | Primary source ✅ |
| **Member Info** | Name only ❌ | Full info ✅ | Full info ✅ |
| **Face Data** | Mixed BLOB ❌ | Separate table ✅ | Not stored |
| **AccessLogs** | Basic ❌ | Rich metadata ✅ | Compatible ✅ |
| **Sync** | Manual ❌ | One command ✅ | Real-time ✅ |

## 🚀 Files Created

1. **migrate_to_mysql_schema.py** - Schema migration script
2. **sync_members_from_mysql.py** - Data sync từ MySQL
3. **MIGRATION_GUIDE.md** - Chi tiết đầy đủ
4. **README_MIGRATION.md** - Quick start này

## ⚙️ New Schema

### Members Table:
```sql
CREATE TABLE Members (
    id INTEGER PRIMARY KEY,  -- From MySQL users.id
    full_name TEXT NOT NULL,
    email TEXT,
    phone TEXT,
    card_id TEXT,
    face_id TEXT,
    membership TEXT,  -- Premium, Gold, Silver, Basic
    join_date TEXT,
    status TEXT,      -- Active, Inactive, Suspended
    dob TEXT,
    gender TEXT,
    role TEXT DEFAULT 'MEMBER'
)
```

### MemberEmbeddings Table:
```sql
CREATE TABLE MemberEmbeddings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    member_id INTEGER NOT NULL,  -- FK to Members
    embeddings BLOB NOT NULL,     -- ArcFace 512D vectors
    model_name TEXT NOT NULL,     -- 'ArcFace', 'Facenet', etc.
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES Members(id)
)
```

### AccessLogs Table:
```sql
CREATE TABLE AccessLogs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    member_id INTEGER NOT NULL,
    access_time TEXT NOT NULL,
    access_method TEXT DEFAULT 'FACE',  -- FACE, CARD, MANUAL
    device_id TEXT DEFAULT 'FACE_RECOGNITION_DEVICE_01',
    location_type TEXT DEFAULT 'MAIN_ENTRANCE',
    result TEXT DEFAULT 'SUCCESS',  -- SUCCESS, FAIL
    reason TEXT,  -- Reason if FAIL
    FOREIGN KEY (member_id) REFERENCES Members(id)
)
```

## 🔧 Configuration

### MySQL Connection (sync_members_from_mysql.py):

```python
MYSQL_CONFIG = {
    'host': 'localhost',      # MySQL server
    'user': 'root',           # ← CHANGE THIS
    'password': 'your_pass',  # ← CHANGE THIS
    'database': 'gymsystem'   # Database name
}
```

## 📝 Usage Examples

### Sync members mới từ MySQL:

```bash
# Chạy khi có members mới trong MySQL
python sync_members_from_mysql.py

# Output:
# ✅ Synced: New Member Name (ID: 15)
# ♻️  Updated: Existing Member (ID: 1)
```

### Kiểm tra database:

```bash
python check_database.py

# Output hiện tại:
📊 MEMBERS:
  Total members: 5
  Active: 3
  With face data: 2

📊 ACCESS LOGS (Member Check-ins):
  Total logs: 12
  Today: 3
```

### Thu thập face data:

```bash
python add_faces_improved.py

# Chọn member từ list (đã sync từ MySQL)
# Thu thập 10 samples với RetinaFace + ArcFace
# Lưu vào MemberEmbeddings table
```

## 🐛 Troubleshooting

### "MySQL connection error"

```bash
❌ ERROR: Access denied for user 'root'@'localhost'
```

**Fix:**
1. Check MySQL đang chạy: `mysql -u root -p`
2. Update password trong `sync_members_from_mysql.py`
3. Test connection: `python -c "import mysql.connector; mysql.connector.connect(host='localhost', user='root', password='your_pass', database='gymsystem')"`

### "No members found in MySQL"

```bash
📊 Found 0 members in MySQL
```

**Fix:**
1. Check MySQL data: `SELECT * FROM members;`
2. Check users table: `SELECT * FROM users WHERE role='MEMBER';`
3. Verify join condition: `users.id = members.id`

### "Table already exists"

```bash
❌ Table 'Members' already exists
```

**Fix:** Script sử dụng `IF NOT EXISTS` → Safe to run multiple times. Nếu muốn reset:
```bash
rm data/face_recognition.db
python migrate_to_mysql_schema.py
```

## 📖 Detailed Docs

- **[MIGRATION_GUIDE.md](./MIGRATION_GUIDE.md)** - Chi tiết đầy đủ về migration
- **[CHECKIN_LOGIC.md](./CHECKIN_LOGIC.md)** - Logic check-in mới
- **[ADD_FACES_ACCURACY_MODE.md](./ADD_FACES_ACCURACY_MODE.md)** - Face collection accuracy mode

## ✅ Verification

Sau khi migration, verify:

```bash
# 1. Check schema
python migrate_to_mysql_schema.py  # Chạy lại để xem schema

# 2. Check data sync
python -c "import sqlite3; conn = sqlite3.connect('data/face_recognition.db');
print('Members:', conn.execute('SELECT COUNT(*) FROM Members').fetchone()[0])"

# 3. Check embeddings
python check_database.py

# 4. Test recognition
python test_improved.py
```

Expected output:
```
✅ Members: 5 (from MySQL)
✅ MemberEmbeddings: 2 (face data collected)
✅ AccessLogs: Compatible with MySQL schema
✅ Face recognition: Working with new schema
```

## 🎓 Cho DATN

**Điểm mạnh để trình bày:**

1. ✅ **System Integration:** SQLite sync với MySQL backend
2. ✅ **Data Architecture:** Separate concerns (face data vs member data)
3. ✅ **Scalability:** Dễ add members mới (chỉ cần sync)
4. ✅ **Production-ready:** Schema tương thích với real gym system
5. ✅ **Maintainability:** Single source of truth (MySQL)

**Demo flow:**

```
1. Show MySQL members (web dashboard)
   → 5 members trong database

2. Run sync script
   → Import vào SQLite

3. Collect face data
   → Thu thập 10 samples cho 1 member

4. Run face recognition
   → Check-in thành công
   → Log có đầy đủ metadata (method, device, location)

5. Show AccessLogs
   → Compatible với MySQL schema
   → Có thể sync về backend sau này
```

## 📅 Maintenance

### Daily:
- Run face recognition: `python test_improved.py`
- Check logs: `python check_database.py`

### Weekly:
- Sync new members: `python sync_members_from_mysql.py`
- Backup databases:
  ```bash
  cp data/face_recognition.db backups/face_recognition_$(date +%Y%m%d).db
  mysqldump -u root -p gymsystem > backups/mysql_$(date +%Y%m%d).sql
  ```

### Monthly:
- Review accuracy metrics
- Update thresholds nếu cần (MIN_CONFIDENCE, RECOGNITION_THRESHOLD)
- Re-collect face data cho members có accuracy thấp

---

**Tạo bởi:** Claude Code
**Ngày:** 2025-01-09
**Version:** 1.0
**Cho:** DATN - Gym Management Face Recognition System
