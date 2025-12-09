"""
Migrate data from SQLite to MySQL for Gym Management System

This script migrates face recognition data from SQLite database to MySQL backend.
It handles:
- Members data (users + members tables)
- Staffs data (users + staffs tables)
- Face embeddings (MemberEmbeddings, EmployeeEmbeddings)
- Access logs (AccessLogs, EmployeeLogs)

Requirements:
- mysql-connector-python
- Existing MySQL database with schema from Dump20251118.sql
"""

import sqlite3
import mysql.connector
from mysql.connector import Error
import pickle
import os
from datetime import datetime

# Database configurations
SQLITE_DB = 'data/face_recognition.db'

MYSQL_CONFIG = {
    'host': 'localhost',  # Change if running in Docker
    'port': 3306,         # Default MySQL port
    'user': 'root',       # Your MySQL username
    'password': 'password',  # Your MySQL password
    'database': 'gymsystem'  # Your database name
}

def connect_mysql():
    """Connect to MySQL database"""
    try:
        conn = mysql.connector.connect(**MYSQL_CONFIG)
        if conn.is_connected():
            print("✅ Connected to MySQL database")
            return conn
    except Error as e:
        print(f"❌ Error connecting to MySQL: {e}")
        return None

def connect_sqlite():
    """Connect to SQLite database"""
    try:
        conn = sqlite3.connect(SQLITE_DB)
        print("✅ Connected to SQLite database")
        return conn
    except Error as e:
        print(f"❌ Error connecting to SQLite: {e}")
        return None

def migrate_users_members():
    """Migrate Members data from SQLite to MySQL (users + members tables)"""
    sqlite_conn = connect_sqlite()
    mysql_conn = connect_mysql()

    if not sqlite_conn or not mysql_conn:
        return False

    try:
        sqlite_cursor = sqlite_conn.cursor()
        mysql_cursor = mysql_conn.cursor()

        # Get all members from SQLite
        sqlite_cursor.execute("""
            SELECT id, full_name, email, phone, card_id, face_id,
                   membership, join_date, status, dob, gender, role
            FROM Members
            WHERE status = 'Active'
        """)

        members = sqlite_cursor.fetchall()
        print(f"📊 Found {len(members)} members in SQLite")

        for member in members:
            (member_id, full_name, email, phone, card_id, face_id,
             membership, join_date, status, dob, gender, role) = member

            # Insert into users table
            mysql_cursor.execute("""
                INSERT INTO users (id, full_name, email, phone, dob, gender, password, role, is_deleted)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON DUPLICATE KEY UPDATE
                full_name = VALUES(full_name),
                email = VALUES(email),
                phone = VALUES(phone),
                dob = VALUES(dob),
                gender = VALUES(gender),
                role = VALUES(role)
            """, (
                member_id, full_name, email, phone,
                dob, gender.upper() if gender else 'MALE',
                '$2a$10$defaultpassword',  # Default password hash
                role or 'MEMBER', 0
            ))

            # Insert into members table
            mysql_cursor.execute("""
                INSERT INTO members (id, card_id, face_id, join_date, membership, status)
                VALUES (%s, %s, %s, %s, %s, %s)
                ON DUPLICATE KEY UPDATE
                card_id = VALUES(card_id),
                face_id = VALUES(face_id),
                join_date = VALUES(join_date),
                membership = VALUES(membership),
                status = VALUES(status)
            """, (
                member_id, card_id, face_id, join_date, membership, status
            ))

        mysql_conn.commit()
        print(f"✅ Migrated {len(members)} members to MySQL")

        return True

    except Error as e:
        print(f"❌ Error migrating members: {e}")
        mysql_conn.rollback()
        return False
    finally:
        if sqlite_conn:
            sqlite_conn.close()
        if mysql_conn:
            mysql_conn.close()

def migrate_staffs():
    """Migrate Employees data from SQLite to MySQL (users + staffs tables)"""
    sqlite_conn = connect_sqlite()
    mysql_conn = connect_mysql()

    if not sqlite_conn or not mysql_conn:
        return False

    try:
        sqlite_cursor = sqlite_conn.cursor()
        mysql_cursor = mysql_conn.cursor()

        # Get all employees from SQLite
        sqlite_cursor.execute("""
            SELECT id, full_name, email, phone, position, hire_price, specialize,
                   status, dob, gender, role
            FROM Employees
            WHERE status = 'Active'
        """)

        employees = sqlite_cursor.fetchall()
        print(f"📊 Found {len(employees)} employees in SQLite")

        for employee in employees:
            (emp_id, full_name, email, phone, position, hire_price, specialize,
             status, dob, gender, role) = employee

            # Insert into users table
            mysql_cursor.execute("""
                INSERT INTO users (id, full_name, email, phone, dob, gender, password, role, is_deleted)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON DUPLICATE KEY UPDATE
                full_name = VALUES(full_name),
                email = VALUES(email),
                phone = VALUES(phone),
                dob = VALUES(dob),
                gender = VALUES(gender),
                role = VALUES(role)
            """, (
                emp_id, full_name, email, phone,
                dob, gender.upper() if gender else 'MALE',
                '$2a$10$defaultpassword',  # Default password hash
                role or 'STAFF', 0
            ))

            # Insert into staffs table
            mysql_cursor.execute("""
                INSERT INTO staffs (id, position, hire_price, specialize)
                VALUES (%s, %s, %s, %s)
                ON DUPLICATE KEY UPDATE
                position = VALUES(position),
                hire_price = VALUES(hire_price),
                specialize = VALUES(specialize)
            """, (
                emp_id, position, hire_price, specialize
            ))

        mysql_conn.commit()
        print(f"✅ Migrated {len(employees)} employees to MySQL")

        return True

    except Error as e:
        print(f"❌ Error migrating employees: {e}")
        mysql_conn.rollback()
        return False
    finally:
        if sqlite_conn:
            sqlite_conn.close()
        if mysql_conn:
            mysql_conn.close()

def migrate_face_embeddings():
    """Migrate face embeddings from SQLite to separate storage"""
    sqlite_conn = connect_sqlite()

    if not sqlite_conn:
        return False

    try:
        sqlite_cursor = sqlite_conn.cursor()

        # Create directory for embeddings if not exists
        embeddings_dir = 'data/embeddings'
        os.makedirs(embeddings_dir, exist_ok=True)

        # Migrate member embeddings
        sqlite_cursor.execute("""
            SELECT member_id, embeddings, model_name, created_at
            FROM MemberEmbeddings
            ORDER BY member_id, created_at
        """)

        member_embeddings = sqlite_cursor.fetchall()
        print(f"📊 Found {len(member_embeddings)} member embeddings")

        for embedding in member_embeddings:
            member_id, embeddings_blob, model_name, created_at = embedding

            # Save embeddings to file
            filename = f"{embeddings_dir}/member_{member_id}_{model_name}.pkl"
            with open(filename, 'wb') as f:
                pickle.dump(embeddings_blob, f)

        # Migrate employee embeddings
        sqlite_cursor.execute("""
            SELECT employee_id, embeddings, model_name, created_at
            FROM EmployeeEmbeddings
            ORDER BY employee_id, created_at
        """)

        employee_embeddings = sqlite_cursor.fetchall()
        print(f"📊 Found {len(employee_embeddings)} employee embeddings")

        for embedding in employee_embeddings:
            emp_id, embeddings_blob, model_name, created_at = embedding

            # Save embeddings to file
            filename = f"{embeddings_dir}/employee_{emp_id}_{model_name}.pkl"
            with open(filename, 'wb') as f:
                pickle.dump(embeddings_blob, f)

        print(f"✅ Saved embeddings to {embeddings_dir}/")
        return True

    except Error as e:
        print(f"❌ Error migrating embeddings: {e}")
        return False
    finally:
        if sqlite_conn:
            sqlite_conn.close()

def migrate_access_logs():
    """Migrate access logs from SQLite to MySQL"""
    sqlite_conn = connect_sqlite()
    mysql_conn = connect_mysql()

    if not sqlite_conn or not mysql_conn:
        return False

    try:
        sqlite_cursor = sqlite_conn.cursor()
        mysql_cursor = mysql_conn.cursor()

        # Get all access logs from SQLite
        sqlite_cursor.execute("""
            SELECT member_id, access_time, access_method, device_id,
                   location_type, result, reason
            FROM AccessLogs
            ORDER BY access_time
        """)

        logs = sqlite_cursor.fetchall()
        print(f"📊 Found {len(logs)} access logs in SQLite")

        for log in logs:
            (member_id, access_time, access_method, device_id,
             location_type, result, reason) = log

            # Convert access_time format if needed
            # SQLite: DD-MM-YYYY HH:MM:SS -> MySQL: YYYY-MM-DD HH:MM:SS
            try:
                if access_time:
                    # Parse DD-MM-YYYY format
                    dt = datetime.strptime(access_time, '%d-%m-%Y %H:%M:%S')
                    mysql_time = dt.strftime('%Y-%m-%d %H:%M:%S')
                else:
                    mysql_time = None
            except:
                mysql_time = access_time

            # Insert into MySQL access_logs
            mysql_cursor.execute("""
                INSERT INTO access_logs
                (member_id, access_time, access_method, device_id, location_type, result, reason)
                VALUES (%s, %s, %s, %s, %s, %s, %s)
            """, (
                member_id, mysql_time, access_method or 'FACE',
                device_id or 'FACE_RECOGNITION_DEVICE_01',
                location_type or 'MAIN_ENTRANCE',
                result or 'SUCCESS', reason
            ))

        mysql_conn.commit()
        print(f"✅ Migrated {len(logs)} access logs to MySQL")

        return True

    except Error as e:
        print(f"❌ Error migrating access logs: {e}")
        mysql_conn.rollback()
        return False
    finally:
        if sqlite_conn:
            sqlite_conn.close()
        if mysql_conn:
            mysql_conn.close()

def migrate_employee_logs():
    """Migrate employee logs from SQLite to MySQL (if needed)"""
    # Note: MySQL schema doesn't have employee_logs table
    # You might need to create it or handle differently
    print("⚠️  Employee logs migration not implemented (no corresponding MySQL table)")
    return True

def main():
    print("="*60)
    print("SQLite to MySQL Migration Tool")
    print("="*60)

    # Update MySQL config
    print("\n⚙️  MySQL Configuration:")
    print(f"Host: {MYSQL_CONFIG['host']}:{MYSQL_CONFIG['port']}")
    print(f"Database: {MYSQL_CONFIG['database']}")
    print(f"User: {MYSQL_CONFIG['user']}")

    update_config = input("\nUpdate MySQL config? (y/n): ").lower()
    if update_config == 'y':
        MYSQL_CONFIG['host'] = input("Host: ") or MYSQL_CONFIG['host']
        MYSQL_CONFIG['port'] = int(input("Port: ") or MYSQL_CONFIG['port'])
        MYSQL_CONFIG['user'] = input("Username: ") or MYSQL_CONFIG['user']
        MYSQL_CONFIG['password'] = input("Password: ") or MYSQL_CONFIG['password']
        MYSQL_CONFIG['database'] = input("Database: ") or MYSQL_CONFIG['database']

    # Test connections
    print("\n🔍 Testing connections...")
    sqlite_conn = connect_sqlite()
    mysql_conn = connect_mysql()

    if not sqlite_conn or not mysql_conn:
        print("❌ Cannot proceed without database connections")
        return

    sqlite_conn.close()
    mysql_conn.close()

    # Confirm migration
    print("\n⚠️  This will migrate data from SQLite to MySQL")
    print("   Make sure you have backed up your MySQL database!")
    proceed = input("\nProceed with migration? (yes/no): ").lower()

    if proceed != 'yes':
        print("❌ Migration cancelled")
        return

    # Run migrations
    print("\n🚀 Starting migration...")

    success = True

    if not migrate_users_members():
        success = False

    if not migrate_staffs():
        success = False

    if not migrate_face_embeddings():
        success = False

    if not migrate_access_logs():
        success = False

    migrate_employee_logs()  # Optional

    if success:
        print("\n" + "="*60)
        print("✅ MIGRATION COMPLETED SUCCESSFULLY!")
        print("="*60)
        print("\nNext steps:")
        print("1. Update your face recognition code to use MySQL instead of SQLite")
        print("2. Load embeddings from files in data/embeddings/")
        print("3. Test the system with MySQL backend")
        print("4. Remove SQLite database if no longer needed")
    else:
        print("\n❌ Migration failed. Check errors above.")

if __name__ == '__main__':
    main()