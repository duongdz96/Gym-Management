"""
MySQL Database operations cho Face Recognition System
Thay thế SQLite bằng MySQL
"""

import mysql.connector
from mysql.connector import Error
import pickle
import numpy as np
from datetime import datetime

# MySQL Configuration
MYSQL_CONFIG = {
    'host': 'localhost',      # Python trên Windows → MySQL trong Docker
    'database': 'gympool',
    'user': 'user',           # ⚠️ Match với docker-compose
    'password': 'password',   # ⚠️ Match với docker-compose
    'port': 3306,
    'charset': 'utf8mb4',
    'collation': 'utf8mb4_0900_ai_ci'
}


def get_connection():
    """Tạo MySQL connection"""
    try:
        conn = mysql.connector.connect(**MYSQL_CONFIG)
        return conn
    except Error as e:
        print(f"Error connecting to MySQL: {e}")
        return None


# ============================================================
# MEMBER OPERATIONS
# ============================================================

def get_all_active_members():
    """
    Lấy tất cả members đang active
    
    Returns:
        list: [(id, name), ...]
    """
    conn = get_connection()
    if not conn:
        return []
    
    try:
        cursor = conn.cursor()
        # Join members với users để lấy full_name
        cursor.execute('''
            SELECT m.id, u.full_name 
            FROM members m
            JOIN users u ON m.id = u.id
            WHERE m.status = 'Active' AND u.is_deleted = 0
            ORDER BY u.full_name
        ''')
        rows = cursor.fetchall()
        return rows
    except Error as e:
        print(f"Error fetching members: {e}")
        return []
    finally:
        cursor.close()
        conn.close()


def get_member_name(member_id):
    """Lấy tên member"""
    conn = get_connection()
    if not conn:
        return 'Unknown'
    
    try:
        cursor = conn.cursor()
        cursor.execute('''
            SELECT u.full_name 
            FROM members m
            JOIN users u ON m.id = u.id
            WHERE m.id = %s
        ''', (member_id,))
        result = cursor.fetchone()
        return result[0] if result else 'Unknown'
    except Error as e:
        print(f"Error: {e}")
        return 'Unknown'
    finally:
        cursor.close()
        conn.close()


def get_member_status(member_id):
    """Lấy status của member"""
    conn = get_connection()
    if not conn:
        return None
    
    try:
        cursor = conn.cursor()
        cursor.execute('SELECT status FROM members WHERE id = %s', (member_id,))
        result = cursor.fetchone()
        return result[0] if result else None
    except Error as e:
        print(f"Error: {e}")
        return None
    finally:
        cursor.close()
        conn.close()


def log_access(member_id, access_time):
    """
    Log member check-in vào access_logs
    
    Args:
        member_id: ID của member
        access_time: datetime string (YYYY-MM-DD HH:MM:SS)
    
    Returns:
        log_id hoặc None
    """
    conn = get_connection()
    if not conn:
        return None
    
    try:
        cursor = conn.cursor()
        # Convert từ format "DD-MM-YYYY HH:MM-SS" sang MySQL datetime
        dt = datetime.strptime(access_time, "%d-%m-%Y %H:%M-%S")
        mysql_datetime = dt.strftime("%Y-%m-%d %H:%M:%S")
        
        cursor.execute('''
            INSERT INTO access_logs (member_id, access_time)
            VALUES (%s, %s)
        ''', (member_id, mysql_datetime))
        conn.commit()
        return cursor.lastrowid
    except Error as e:
        print(f"Error logging access: {e}")
        return None
    finally:
        cursor.close()
        conn.close()


def update_access_log(log_id, new_access_time):
    """Update access log time"""
    conn = get_connection()
    if not conn:
        return False
    
    try:
        cursor = conn.cursor()
        dt = datetime.strptime(new_access_time, "%d-%m-%Y %H:%M-%S")
        mysql_datetime = dt.strftime("%Y-%m-%d %H:%M:%S")
        
        cursor.execute('''
            UPDATE access_logs 
            SET access_time = %s 
            WHERE id = %s
        ''', (mysql_datetime, log_id))
        conn.commit()
        return True
    except Error as e:
        print(f"Error updating access log: {e}")
        return False
    finally:
        cursor.close()
        conn.close()


# ============================================================
# EMPLOYEE OPERATIONS (Users with role != MEMBER)
# ============================================================

def get_all_active_employees():
    """
    Lấy tất cả employees (users có role là MANAGER, RECEPTIONIST, PT, TEACHER)
    
    Returns:
        list: [(id, name), ...]
    """
    conn = get_connection()
    if not conn:
        return []
    
    try:
        cursor = conn.cursor()
        cursor.execute('''
            SELECT id, full_name 
            FROM users
            WHERE role IN ('MANAGER', 'RECEPTIONIST', 'PT', 'TEACHER')
              AND is_deleted = 0
            ORDER BY full_name
        ''')
        rows = cursor.fetchall()
        return rows
    except Error as e:
        print(f"Error fetching employees: {e}")
        return []
    finally:
        cursor.close()
        conn.close()


def get_employee_name(user_id):
    """Lấy tên employee từ users table"""
    conn = get_connection()
    if not conn:
        return 'Unknown'
    
    try:
        cursor = conn.cursor()
        cursor.execute('SELECT full_name FROM users WHERE id = %s', (user_id,))
        result = cursor.fetchone()
        return result[0] if result else 'Unknown'
    except Error as e:
        print(f"Error: {e}")
        return 'Unknown'
    finally:
        cursor.close()
        conn.close()


def get_employee_status(user_id):
    """
    Lấy status của employee (active nếu không bị deleted)
    """
    conn = get_connection()
    if not conn:
        return None
    
    try:
        cursor = conn.cursor()
        cursor.execute('SELECT is_deleted FROM users WHERE id = %s', (user_id,))
        result = cursor.fetchone()
        if result:
            return 'active' if result[0] == 0 else 'inactive'
        return None
    except Error as e:
        print(f"Error: {e}")
        return None
    finally:
        cursor.close()
        conn.close()


def log_employee_access(user_id, check_type, log_time):
    """
    Log employee check-in/out vào attendance table
    
    Args:
        user_id: ID của user (employee)
        check_type: 'in' hoặc 'out'
        log_time: datetime string
    
    Returns:
        log_id hoặc None
    """
    conn = get_connection()
    if not conn:
        return None
    
    try:
        cursor = conn.cursor()
        # Convert time format
        dt = datetime.strptime(log_time, "%d-%m-%Y %H:%M-%S")
        mysql_datetime = dt.strftime("%Y-%m-%d %H:%M:%S")
        
        if check_type == 'in':
            # Check-in: Tạo record mới với check_in_time
            cursor.execute('''
                INSERT INTO attendance (user_id, check_in_time, check_out_time)
                VALUES (%s, %s, %s)
            ''', (user_id, mysql_datetime, mysql_datetime))
        else:
            # Check-out: Update record gần nhất chưa có check_out proper
            cursor.execute('''
                UPDATE attendance 
                SET check_out_time = %s
                WHERE user_id = %s
                  AND DATE(check_in_time) = DATE(%s)
                ORDER BY id DESC
                LIMIT 1
            ''', (mysql_datetime, user_id, mysql_datetime))
        
        conn.commit()
        return cursor.lastrowid if check_type == 'in' else cursor.rowcount
    except Error as e:
        print(f"Error logging employee access: {e}")
        return None
    finally:
        cursor.close()
        conn.close()


def get_last_employee_check_type(user_id):
    """
    Lấy loại check cuối cùng của employee (in/out)
    Để xác định lần sau nên check in hay out
    """
    conn = get_connection()
    if not conn:
        return None
    
    try:
        cursor = conn.cursor()
        # Lấy record gần nhất
        cursor.execute('''
            SELECT check_in_time, check_out_time 
            FROM attendance
            WHERE user_id = %s
            ORDER BY id DESC
            LIMIT 1
        ''', (user_id,))
        result = cursor.fetchone()
        
        if not result:
            return None  # Chưa có record nào → lần sau là 'in'
        
        check_in, check_out = result
        # Nếu check_out > check_in đủ nhiều → đã check out → lần sau là 'in'
        # Nếu check_out == check_in hoặc gần bằng → chưa check out → lần sau là 'out'
        if check_out and (check_out - check_in).total_seconds() > 60:
            return 'out'  # Đã check out rồi → lần sau check in
        else:
            return 'in'  # Chưa check out → lần sau check out
            
    except Error as e:
        print(f"Error: {e}")
        return None
    finally:
        cursor.close()
        conn.close()


# ============================================================
# INITIALIZE
# ============================================================

def test_connection():
    """Test MySQL connection"""
    conn = get_connection()
    if conn:
        print("✅ MySQL connection successful!")
        print(f"   Database: {MYSQL_CONFIG['database']}")
        conn.close()
        return True
    else:
        print("❌ MySQL connection failed!")
        return False


def verify_user_login(email, password):
    """
    Verify user login credentials with BCrypt
    Returns: (user_id, full_name, role) if success, None if failed
    """
    conn = get_connection()
    if not conn:
        return None
    
    try:
        cursor = conn.cursor()
        
        # Get user by email
        query = """
            SELECT id, full_name, password, role 
            FROM users 
            WHERE email = %s AND is_deleted = 0
        """
        cursor.execute(query, (email,))
        result = cursor.fetchone()
        
        if result:
            user_id, full_name, stored_password, role = result
            
            # BCrypt password verification
            try:
                import bcrypt
                
                # Verify password (stored_password is already BCrypt hashed in DB)
                if bcrypt.checkpw(password.encode('utf-8'), stored_password.encode('utf-8')):
                    return (user_id, full_name, role)
            except ImportError:
                print("bcrypt not installed. Install: pip install bcrypt")
                return None
        
        return None
        
    except Error as e:
        print(f"Error verifying login: {e}")
        return None
    finally:
        if conn:
            conn.close()


if __name__ == '__main__':
    # Test connection
    test_connection()
    
    # Test queries
    print("\n" + "="*60)
    print("Testing queries...")
    print("="*60)
    
    members = get_all_active_members()
    print(f"\nActive Members: {len(members)}")
    for id, name in members[:5]:
        print(f"  {id}. {name}")
    
    employees = get_all_active_employees()
    print(f"\nActive Employees: {len(employees)}")
    for id, name in employees:
        print(f"  {id}. {name}")
