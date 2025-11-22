"""
Migration script để đồng bộ SQLite database với MySQL schema
Đảm bảo face recognition system tương thích với backend gym system
"""

import sqlite3
import os
from datetime import datetime

DB_PATH = 'data/face_recognition.db'
BACKUP_PATH = f'data/face_recognition_backup_{datetime.now().strftime("%Y%m%d_%H%M%S")}.db'

def backup_database():
    """Backup database hiện tại trước khi migrate"""
    if os.path.exists(DB_PATH):
        import shutil
        shutil.copy2(DB_PATH, BACKUP_PATH)
        print(f"✅ Backup created: {BACKUP_PATH}")
        return True
    else:
        print("⚠️  No existing database to backup")
        return False

def migrate_database():
    """
    Migrate SQLite schema để khớp với MySQL backend

    Changes:
    1. Members table: Thêm các fields từ MySQL (email, phone, card_id, face_id, membership, join_date)
    2. AccessLogs table: Thêm access_method, device_id, location_type, result, reason
    3. Không còn tự quản lý member data, sync từ MySQL
    """

    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()

    print("\n" + "="*60)
    print("DATABASE MIGRATION - MySQL Schema Sync")
    print("="*60)

    # 1. DROP old tables (nếu muốn reset hoàn toàn)
    # Nếu muốn giữ data cũ, comment phần này
    print("\n[1] Checking existing tables...")
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table'")
    existing_tables = [row[0] for row in cursor.fetchall()]
    print(f"    Existing tables: {existing_tables}")

    # 2. CREATE new Members table (sync với MySQL schema)
    print("\n[2] Creating Members table (MySQL-compatible schema)...")
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS Members (
            id INTEGER PRIMARY KEY,  -- Sync với MySQL users.id
            full_name TEXT NOT NULL,
            email TEXT,
            phone TEXT,
            card_id TEXT,  -- Từ MySQL members.card_id
            face_id TEXT,  -- Từ MySQL members.face_id (nếu có)
            membership TEXT,  -- Premium, Standard, Gold, Basic, VIP
            join_date TEXT,  -- ISO format: YYYY-MM-DD HH:MM:SS
            status TEXT DEFAULT 'Active',  -- Active, Inactive, Suspended
            dob TEXT,
            gender TEXT,
            role TEXT DEFAULT 'MEMBER'
        )
    ''')
    print("    ✅ Members table created/verified")

    # 3. CREATE MemberEmbeddings table (riêng cho face data)
    print("\n[3] Creating MemberEmbeddings table...")
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS MemberEmbeddings (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            member_id INTEGER NOT NULL,  -- FK to Members(id)
            embeddings BLOB NOT NULL,  -- Face embeddings (pickled numpy array)
            model_name TEXT NOT NULL,  -- 'ArcFace', 'Facenet', etc.
            created_at TEXT DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (member_id) REFERENCES Members(id) ON DELETE CASCADE
        )
    ''')
    print("    ✅ MemberEmbeddings table created/verified")

    # 4. CREATE AccessLogs table (sync với MySQL schema)
    print("\n[4] Creating AccessLogs table (MySQL-compatible schema)...")
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS AccessLogs (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            member_id INTEGER NOT NULL,
            access_time TEXT NOT NULL,  -- ISO format: DD-MM-YYYY HH:MM:SS
            access_method TEXT DEFAULT 'FACE',  -- FACE, CARD, MANUAL
            device_id TEXT DEFAULT 'FACE_RECOGNITION_DEVICE_01',
            location_type TEXT DEFAULT 'MAIN_ENTRANCE',
            result TEXT DEFAULT 'SUCCESS',  -- SUCCESS, FAIL
            reason TEXT,  -- Reason if FAIL (e.g., "Membership expired")
            FOREIGN KEY (member_id) REFERENCES Members(id)
        )
    ''')
    print("    ✅ AccessLogs table created/verified")

    # 5. CREATE Employees table (nếu cần)
    print("\n[5] Creating Employees table...")
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS Employees (
            id INTEGER PRIMARY KEY,  -- Sync với MySQL staffs.id
            full_name TEXT NOT NULL,
            email TEXT,
            phone TEXT,
            position TEXT,  -- Personal Trainer, Yoga Instructor, etc.
            hire_price TEXT,
            specialize TEXT,
            status TEXT DEFAULT 'Active',
            dob TEXT,
            gender TEXT,
            role TEXT DEFAULT 'STAFF'
        )
    ''')
    print("    ✅ Employees table created/verified")

    # 6. CREATE EmployeeEmbeddings table
    print("\n[6] Creating EmployeeEmbeddings table...")
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS EmployeeEmbeddings (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            employee_id INTEGER NOT NULL,
            embeddings BLOB NOT NULL,
            model_name TEXT NOT NULL,
            created_at TEXT DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (employee_id) REFERENCES Employees(id) ON DELETE CASCADE
        )
    ''')
    print("    ✅ EmployeeEmbeddings table created/verified")

    # 7. CREATE EmployeeLogs table
    print("\n[7] Creating EmployeeLogs table...")
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS EmployeeLogs (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            employee_id INTEGER NOT NULL,
            check_type TEXT NOT NULL,  -- 'in' or 'out'
            log_time TEXT NOT NULL,
            access_method TEXT DEFAULT 'FACE',
            device_id TEXT DEFAULT 'FACE_RECOGNITION_DEVICE_01',
            location_type TEXT DEFAULT 'STAFF_ENTRANCE',
            FOREIGN KEY (employee_id) REFERENCES Employees(id)
        )
    ''')
    print("    ✅ EmployeeLogs table created/verified")

    # 8. CREATE indexes for performance
    print("\n[8] Creating indexes...")
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_member_embeddings_member_id ON MemberEmbeddings(member_id)')
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_employee_embeddings_employee_id ON EmployeeEmbeddings(employee_id)')
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_access_logs_member_id ON AccessLogs(member_id)')
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_access_logs_time ON AccessLogs(access_time)')
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_employee_logs_employee_id ON EmployeeLogs(employee_id)')
    print("    ✅ Indexes created")

    conn.commit()

    # 9. Verify schema
    print("\n[9] Verifying new schema...")
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name")
    tables = cursor.fetchall()
    print("\n    Tables:")
    for table in tables:
        print(f"      • {table[0]}")
        cursor.execute(f"PRAGMA table_info({table[0]})")
        columns = cursor.fetchall()
        for col in columns:
            print(f"        - {col[1]} ({col[2]})")

    conn.close()

    print("\n" + "="*60)
    print("✅ MIGRATION COMPLETE!")
    print("="*60)
    print("\nNext steps:")
    print("1. Run sync_members_from_mysql.py to import members from MySQL")
    print("2. Run add_faces_improved.py to collect face data for members")
    print("3. Run test_improved.py for face recognition check-in")
    print("="*60)

def check_current_schema():
    """Hiển thị schema hiện tại"""
    if not os.path.exists(DB_PATH):
        print("⚠️  Database does not exist yet")
        return

    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()

    print("\n" + "="*60)
    print("CURRENT DATABASE SCHEMA")
    print("="*60)

    cursor.execute("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name")
    tables = cursor.fetchall()

    for table in tables:
        table_name = table[0]
        print(f"\n📋 Table: {table_name}")
        cursor.execute(f"PRAGMA table_info({table_name})")
        columns = cursor.fetchall()
        for col in columns:
            pk = " [PK]" if col[5] else ""
            notnull = " NOT NULL" if col[3] else ""
            default = f" DEFAULT {col[4]}" if col[4] else ""
            print(f"   • {col[1]:<20} {col[2]:<15}{pk}{notnull}{default}")

        # Count rows
        cursor.execute(f"SELECT COUNT(*) FROM {table_name}")
        count = cursor.fetchone()[0]
        print(f"   → Total rows: {count}")

    conn.close()
    print("="*60)

if __name__ == '__main__':
    print("\n" + "="*60)
    print("SQLite → MySQL Schema Migration Tool")
    print("="*60)

    # Show current schema
    print("\n[Step 1] Checking current database...")
    check_current_schema()

    # Ask for confirmation
    print("\n⚠️  This will modify your database structure!")
    print("   A backup will be created automatically.")
    choice = input("\nProceed with migration? (yes/no): ").lower()

    if choice == 'yes':
        print("\n[Step 2] Creating backup...")
        backup_database()

        print("\n[Step 3] Running migration...")
        migrate_database()

        print("\n[Step 4] Verifying migration...")
        check_current_schema()
    else:
        print("\n❌ Migration cancelled")
