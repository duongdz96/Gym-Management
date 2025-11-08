"""
Reset Database Script
Xóa toàn bộ dữ liệu và reset database về trạng thái ban đầu
"""

import sqlite3
import os
import shutil
from datetime import datetime

DB_PATH = 'data/face_recognition.db'


def backup_database():
    """Tạo backup của database trước khi reset"""
    if not os.path.exists(DB_PATH):
        print("❌ Database không tồn tại")
        return False

    # Tạo backup folder nếu chưa có
    backup_dir = 'data/backups'
    os.makedirs(backup_dir, exist_ok=True)

    # Tạo backup file với timestamp
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    backup_path = f"{backup_dir}/face_recognition_{timestamp}.db"

    try:
        shutil.copy2(DB_PATH, backup_path)
        print(f"✅ Backup created: {backup_path}")
        return True
    except Exception as e:
        print(f"❌ Backup failed: {e}")
        return False


def get_database_stats():
    """Lấy thống kê database hiện tại"""
    if not os.path.exists(DB_PATH):
        return None

    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()

    stats = {}

    # Tables to check
    tables = [
        'Members',
        'Employees',
        'AccessLogs',
        'EmployeeLogs',
        'MemberEmbeddings',
        'EmployeeEmbeddings'
    ]

    for table in tables:
        try:
            cursor.execute(f'SELECT COUNT(*) FROM {table}')
            count = cursor.fetchone()[0]
            stats[table] = count
        except sqlite3.OperationalError:
            stats[table] = 0  # Table doesn't exist

    conn.close()
    return stats


def print_stats(stats, title="DATABASE STATISTICS"):
    """In ra thống kê database"""
    print("\n" + "=" * 60)
    print(title)
    print("=" * 60)

    if stats is None:
        print("Database does not exist")
    else:
        for table, count in stats.items():
            print(f"  {table:25} : {count:>5} records")

    print("=" * 60)


def reset_database():
    """Reset database về trạng thái ban đầu"""
    if not os.path.exists(DB_PATH):
        print("❌ Database không tồn tại")
        return False

    try:
        conn = sqlite3.connect(DB_PATH)
        cursor = conn.cursor()

        # List of all tables
        tables = [
            'AccessLogs',
            'EmployeeLogs',
            'MemberEmbeddings',
            'EmployeeEmbeddings',
            'Members',
            'Employees'
        ]

        print("\n🗑️  Deleting all data...")

        # Delete data from tables (in order to respect foreign keys)
        for table in tables:
            try:
                cursor.execute(f'DELETE FROM {table}')
                print(f"  ✓ Cleared {table}")
            except sqlite3.OperationalError as e:
                print(f"  ⚠ {table} not found (skipped)")

        # Reset auto-increment counters
        print("\n🔄 Resetting auto-increment counters...")
        try:
            cursor.execute('DELETE FROM sqlite_sequence')
            print("  ✓ Auto-increment counters reset")
        except sqlite3.OperationalError:
            print("  ⚠ No auto-increment counters found")

        conn.commit()
        conn.close()

        print("\n✅ Database reset successfully!")
        return True

    except Exception as e:
        print(f"\n❌ Error resetting database: {e}")
        return False


def confirm_reset():
    """Xác nhận từ user trước khi reset"""
    print("\n⚠️  WARNING: This will DELETE ALL DATA from the database!")
    print("This includes:")
    print("  • All members and employees")
    print("  • All face embeddings")
    print("  • All access logs")
    print("  • All check-in/out records")
    print("\nThis action CANNOT be undone (unless you create a backup).\n")

    response = input("Are you sure you want to continue? (type 'YES' to confirm): ")
    return response == 'YES'


def main():
    """Main function"""
    print("\n" + "=" * 60)
    print("DATABASE RESET UTILITY")
    print("=" * 60)

    # Show current stats
    stats_before = get_database_stats()
    print_stats(stats_before, "CURRENT DATABASE STATUS")

    if stats_before is None:
        print("\n❌ No database found. Nothing to reset.")
        return

    # Check if database is empty
    total_records = sum(stats_before.values())
    if total_records == 0:
        print("\n✅ Database is already empty. Nothing to reset.")
        return

    # Confirm with user
    if not confirm_reset():
        print("\n❌ Reset cancelled by user")
        return

    # Ask about backup
    print("\n📦 Create backup before reset?")
    backup_choice = input("   (y/n, recommended: y): ").lower().strip()

    if backup_choice == 'y':
        if not backup_database():
            print("\n⚠️  Backup failed!")
            proceed = input("Continue without backup? (y/n): ").lower().strip()
            if proceed != 'y':
                print("❌ Reset cancelled")
                return

    # Perform reset
    print("\n" + "=" * 60)
    print("RESETTING DATABASE...")
    print("=" * 60)

    if reset_database():
        # Show stats after reset
        stats_after = get_database_stats()
        print_stats(stats_after, "DATABASE STATUS AFTER RESET")

        print("\n✅ Database has been reset to initial state!")
        print("   You can now add new members/employees using:")
        print("   • python setup_database.py")
        print("   • python add_faces_improved.py")
    else:
        print("\n❌ Reset failed!")


if __name__ == '__main__':
    main()
