"""
Sync members data từ MySQL backend vào SQLite face recognition database
"""

import sqlite3
import mysql.connector
from datetime import datetime

# MySQL Configuration (adjust these to match your MySQL setup)
MYSQL_CONFIG = {
    'host': 'localhost',
    'user': 'root',  # Change to your MySQL username
    'password': '',  # Change to your MySQL password
    'database': 'gymsystem'
}

SQLITE_DB_PATH = 'data/face_recognition.db'

def connect_mysql():
    """Connect to MySQL database"""
    try:
        conn = mysql.connector.connect(**MYSQL_CONFIG)
        print("✅ Connected to MySQL")
        return conn
    except mysql.connector.Error as err:
        print(f"❌ MySQL connection error: {err}")
        return None

def sync_members():
    """
    Sync members từ MySQL vào SQLite

    MySQL tables involved:
    - users (id, full_name, email, phone, dob, gender, role)
    - members (id, card_id, face_id, join_date, membership, status)
    """

    # Connect to both databases
    mysql_conn = connect_mysql()
    if not mysql_conn:
        return

    sqlite_conn = sqlite3.connect(SQLITE_DB_PATH)

    mysql_cursor = mysql_conn.cursor(dictionary=True)
    sqlite_cursor = sqlite_conn.cursor()

    print("\n" + "="*60)
    print("SYNCING MEMBERS FROM MYSQL TO SQLITE")
    print("="*60)

    # Query members from MySQL
    query = """
        SELECT
            u.id,
            u.full_name,
            u.email,
            u.phone,
            u.dob,
            u.gender,
            u.role,
            m.card_id,
            m.face_id,
            m.join_date,
            m.membership,
            m.status
        FROM users u
        INNER JOIN members m ON u.id = m.id
        WHERE u.role = 'MEMBER' AND u.is_deleted = 0
    """

    try:
        mysql_cursor.execute(query)
        members = mysql_cursor.fetchall()

        print(f"\n📊 Found {len(members)} members in MySQL")

        synced_count = 0
        updated_count = 0

        for member in members:
            # Format dates
            dob_str = member['dob'].strftime('%Y-%m-%d') if member['dob'] else None
            join_date_str = member['join_date'].strftime('%Y-%m-%d %H:%M:%S') if member['join_date'] else None

            # Check if member exists in SQLite
            sqlite_cursor.execute('SELECT id FROM Members WHERE id = ?', (member['id'],))
            exists = sqlite_cursor.fetchone()

            if exists:
                # Update existing member
                sqlite_cursor.execute('''
                    UPDATE Members SET
                        full_name = ?,
                        email = ?,
                        phone = ?,
                        card_id = ?,
                        face_id = ?,
                        membership = ?,
                        join_date = ?,
                        status = ?,
                        dob = ?,
                        gender = ?,
                        role = ?
                    WHERE id = ?
                ''', (
                    member['full_name'],
                    member['email'],
                    member['phone'],
                    member['card_id'],
                    member['face_id'],
                    member['membership'],
                    join_date_str,
                    member['status'],
                    dob_str,
                    member['gender'],
                    member['role'],
                    member['id']
                ))
                updated_count += 1
                print(f"   ♻️  Updated: {member['full_name']} (ID: {member['id']})")
            else:
                # Insert new member
                sqlite_cursor.execute('''
                    INSERT INTO Members (
                        id, full_name, email, phone, card_id, face_id,
                        membership, join_date, status, dob, gender, role
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ''', (
                    member['id'],
                    member['full_name'],
                    member['email'],
                    member['phone'],
                    member['card_id'],
                    member['face_id'],
                    member['membership'],
                    join_date_str,
                    member['status'],
                    dob_str,
                    member['gender'],
                    member['role']
                ))
                synced_count += 1
                print(f"   ✅ Synced: {member['full_name']} (ID: {member['id']})")

        sqlite_conn.commit()

        print("\n" + "="*60)
        print(f"✅ SYNC COMPLETE!")
        print(f"   • New members: {synced_count}")
        print(f"   • Updated members: {updated_count}")
        print(f"   • Total: {synced_count + updated_count}")
        print("="*60)

        # Show summary
        sqlite_cursor.execute('SELECT COUNT(*) FROM Members')
        total_members = sqlite_cursor.fetchone()[0]
        print(f"\n📊 Total members in SQLite: {total_members}")

        sqlite_cursor.execute('''
            SELECT m.full_name, m.status,
                   CASE WHEN e.member_id IS NOT NULL THEN 'Yes' ELSE 'No' END as has_face_data
            FROM Members m
            LEFT JOIN MemberEmbeddings e ON m.id = e.member_id
            ORDER BY m.id
        ''')
        members_status = sqlite_cursor.fetchall()

        print("\n📋 Members status:")
        print(f"{'Name':<30} {'Status':<15} {'Face Data':<10}")
        print("-" * 55)
        for m in members_status:
            print(f"{m[0]:<30} {m[1]:<15} {m[2]:<10}")

    except mysql.connector.Error as err:
        print(f"❌ MySQL error: {err}")
    except sqlite3.Error as err:
        print(f"❌ SQLite error: {err}")
    finally:
        mysql_cursor.close()
        mysql_conn.close()
        sqlite_cursor.close()
        sqlite_conn.close()
        print("\n✅ Database connections closed")

def sync_employees():
    """Sync employees (staffs) từ MySQL vào SQLite"""

    mysql_conn = connect_mysql()
    if not mysql_conn:
        return

    sqlite_conn = sqlite3.connect(SQLITE_DB_PATH)

    mysql_cursor = mysql_conn.cursor(dictionary=True)
    sqlite_cursor = sqlite_conn.cursor()

    print("\n" + "="*60)
    print("SYNCING EMPLOYEES FROM MYSQL TO SQLITE")
    print("="*60)

    query = """
        SELECT
            u.id,
            u.full_name,
            u.email,
            u.phone,
            u.dob,
            u.gender,
            u.role,
            s.position,
            s.hire_price,
            s.specialize
        FROM users u
        INNER JOIN staffs s ON u.id = s.id
        WHERE u.role = 'STAFF' AND u.is_deleted = 0
    """

    try:
        mysql_cursor.execute(query)
        employees = mysql_cursor.fetchall()

        print(f"\n📊 Found {len(employees)} employees in MySQL")

        synced_count = 0
        updated_count = 0

        for emp in employees:
            dob_str = emp['dob'].strftime('%Y-%m-%d') if emp['dob'] else None

            sqlite_cursor.execute('SELECT id FROM Employees WHERE id = ?', (emp['id'],))
            exists = sqlite_cursor.fetchone()

            if exists:
                sqlite_cursor.execute('''
                    UPDATE Employees SET
                        full_name = ?,
                        email = ?,
                        phone = ?,
                        position = ?,
                        hire_price = ?,
                        specialize = ?,
                        status = 'Active',
                        dob = ?,
                        gender = ?,
                        role = ?
                    WHERE id = ?
                ''', (
                    emp['full_name'],
                    emp['email'],
                    emp['phone'],
                    emp['position'],
                    emp['hire_price'],
                    emp['specialize'],
                    dob_str,
                    emp['gender'],
                    emp['role'],
                    emp['id']
                ))
                updated_count += 1
                print(f"   ♻️  Updated: {emp['full_name']} (ID: {emp['id']})")
            else:
                sqlite_cursor.execute('''
                    INSERT INTO Employees (
                        id, full_name, email, phone, position, hire_price,
                        specialize, status, dob, gender, role
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, 'Active', ?, ?, ?)
                ''', (
                    emp['id'],
                    emp['full_name'],
                    emp['email'],
                    emp['phone'],
                    emp['position'],
                    emp['hire_price'],
                    emp['specialize'],
                    dob_str,
                    emp['gender'],
                    emp['role']
                ))
                synced_count += 1
                print(f"   ✅ Synced: {emp['full_name']} (ID: {emp['id']})")

        sqlite_conn.commit()

        print("\n" + "="*60)
        print(f"✅ SYNC COMPLETE!")
        print(f"   • New employees: {synced_count}")
        print(f"   • Updated employees: {updated_count}")
        print("="*60)

    except Exception as err:
        print(f"❌ Error: {err}")
    finally:
        mysql_cursor.close()
        mysql_conn.close()
        sqlite_cursor.close()
        sqlite_conn.close()

if __name__ == '__main__':
    print("\n" + "="*60)
    print("MySQL → SQLite Sync Tool")
    print("="*60)
    print("\n⚠️  Make sure:")
    print("   1. MySQL server is running")
    print("   2. Database 'gymsystem' exists")
    print("   3. You've run migrate_to_mysql_schema.py first")
    print("   4. Update MYSQL_CONFIG in this file with correct credentials")

    choice = input("\nProceed? (yes/no): ").lower()

    if choice == 'yes':
        sync_members()

        sync_emp = input("\nSync employees too? (yes/no): ").lower()
        if sync_emp == 'yes':
            sync_employees()

        print("\n✅ All done! Next step:")
        print("   Run: python add_faces_improved.py")
        print("   to collect face data for members/employees")
    else:
        print("\n❌ Sync cancelled")
