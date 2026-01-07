"""
MySQL Database operations cho face embeddings
Lưu trữ ArcFace embeddings (512D) vào MySQL
"""

import mysql.connector
from mysql.connector import Error
import pickle
import numpy as np

# Import MySQL config
from database import get_connection


# ============================================================
# MEMBER EMBEDDINGS OPERATIONS
# ============================================================

def save_member_embeddings(member_id, embeddings, model_name='ArcFace'):
    """
    Lưu face embeddings cho member vào MySQL
    
    Args:
        member_id: ID của member
        embeddings: numpy array shape (N, 512) - N embeddings, mỗi cái 512D
        model_name: Tên model (ArcFace, Facenet512, etc.)
    
    Returns:
        True/False
    """
    conn = get_connection()
    if not conn:
        return False
    
    try:
        cursor = conn.cursor()
        
        # Serialize embeddings
        serialized = pickle.dumps(embeddings)
        
        # Delete old embeddings if exist (REPLACE INTO)
        cursor.execute('''
            REPLACE INTO member_face_embeddings (member_id, embeddings, model_name)
            VALUES (%s, %s, %s)
        ''', (member_id, serialized, model_name))
        
        conn.commit()
        print(f"✅ Saved {len(embeddings)} embeddings for member {member_id}")
        return True
        
    except Error as e:
        print(f"❌ Error saving member embeddings: {e}")
        return False
    finally:
        cursor.close()
        conn.close()


def get_member_embeddings(member_id):
    """
    Lấy embeddings của 1 member cụ thể
    
    Returns:
        numpy array hoặc None
    """
    conn = get_connection()
    if not conn:
        return None
    
    try:
        cursor = conn.cursor()
        cursor.execute('''
            SELECT embeddings 
            FROM member_face_embeddings
            WHERE member_id = %s
        ''', (member_id,))
        
        result = cursor.fetchone()
        if result:
            return pickle.loads(result[0])
        return None
        
    except Error as e:
        print(f"Error: {e}")
        return None
    finally:
        cursor.close()
        conn.close()


def get_all_member_embeddings():
    """
    Load tất cả member embeddings từ MySQL
    
    Returns:
        dict: {member_id: embeddings_array, ...}
    """
    conn = get_connection()
    if not conn:
        return {}
    
    try:
        cursor = conn.cursor()
        
        # Chỉ lấy members có status active
        cursor.execute('''
            SELECT me.member_id, me.embeddings
            FROM member_face_embeddings me
            JOIN members m ON me.member_id = m.id
            WHERE m.status = 'Active'
        ''')
        
        rows = cursor.fetchall()
        
        embeddings_dict = {}
        for member_id, serialized in rows:
            try:
                embeddings = pickle.loads(serialized)
                embeddings_dict[str(member_id)] = embeddings
            except Exception as e:
                print(f"⚠️ Warning: Failed to load embeddings for member {member_id}: {e}")
                print(f"   → Record with member_id={member_id} has corrupt data. Please delete and re-collect faces.")
                # Bỏ qua record lỗi, tiếp tục với records khác
                continue
        
        return embeddings_dict
        
    except Error as e:
        print(f"Error loading member embeddings: {e}")
        return {}
    finally:
        cursor.close()
        conn.close()


def delete_member_embeddings(member_id):
    """Xóa embeddings của member"""
    conn = get_connection()
    if not conn:
        return False
    
    try:
        cursor = conn.cursor()
        cursor.execute('DELETE FROM member_face_embeddings WHERE member_id = %s', (member_id,))
        conn.commit()
        return True
    except Error as e:
        print(f"Error: {e}")
        return False
    finally:
        cursor.close()
        conn.close()


# ============================================================
# EMPLOYEE EMBEDDINGS OPERATIONS
# ============================================================

def save_employee_embeddings(user_id, embeddings, model_name='ArcFace'):
    """
    Lưu face embeddings cho employee (user) vào MySQL
    
    Args:
        user_id: ID của user (employee)
        embeddings: numpy array shape (N, 512)
        model_name: Tên model
    
    Returns:
        True/False
    """
    conn = get_connection()
    if not conn:
        return False
    
    try:
        cursor = conn.cursor()
        
        serialized = pickle.dumps(embeddings)
        
        cursor.execute('''
            REPLACE INTO employee_face_embeddings (user_id, embeddings, model_name)
            VALUES (%s, %s, %s)
        ''', (user_id, serialized, model_name))
        
        conn.commit()
        print(f"✅ Saved {len(embeddings)} embeddings for employee {user_id}")
        return True
        
    except Error as e:
        print(f"❌ Error saving employee embeddings: {e}")
        return False
    finally:
        cursor.close()
        conn.close()


def get_employee_embeddings(user_id):
    """
    Lấy embeddings của 1 employee cụ thể
    
    Returns:
        numpy array hoặc None
    """
    conn = get_connection()
    if not conn:
        return None
    
    try:
        cursor = conn.cursor()
        cursor.execute('''
            SELECT embeddings 
            FROM employee_face_embeddings
            WHERE user_id = %s
        ''', (user_id,))
        
        result = cursor.fetchone()
        if result:
            return pickle.loads(result[0])
        return None
        
    except Error as e:
        print(f"Error: {e}")
        return None
    finally:
        cursor.close()
        conn.close()


def get_all_employee_embeddings():
    """
    Load tất cả employee embeddings từ MySQL
    
    Returns:
        dict: {f"emp_{user_id}": embeddings_array, ...}
    """
    conn = get_connection()
    if not conn:
        return {}
    
    try:
        cursor = conn.cursor()
        
        # Chỉ lấy users có role là staff và không bị deleted
        cursor.execute('''
            SELECT ee.user_id, ee.embeddings
            FROM employee_face_embeddings ee
            JOIN users u ON ee.user_id = u.id
            WHERE u.role IN ('MANAGER', 'RECEPTIONIST', 'PT', 'TEACHER')
              AND u.is_deleted = 0
        ''')
        
        rows = cursor.fetchall()
        
        embeddings_dict = {}
        for user_id, serialized in rows:
            try:
                embeddings = pickle.loads(serialized)
                # Prefix để phân biệt với members
                embeddings_dict[f"emp_{user_id}"] = embeddings
            except Exception as e:
                print(f"⚠️ Warning: Failed to load embeddings for employee {user_id}: {e}")
                print(f"   → Record with user_id={user_id} has corrupt data. Please delete and re-collect faces.")
                # Bỏ qua record lỗi, tiếp tục với records khác
                continue
        
        return embeddings_dict
        
    except Error as e:
        print(f"Error loading employee embeddings: {e}")
        return {}
    finally:
        cursor.close()
        conn.close()


def delete_employee_embeddings(user_id):
    """Xóa embeddings của employee"""
    conn = get_connection()
    if not conn:
        return False
    
    try:
        cursor = conn.cursor()
        cursor.execute('DELETE FROM employee_face_embeddings WHERE user_id = %s', (user_id,))
        conn.commit()
        return True
    except Error as e:
        print(f"Error: {e}")
        return False
    finally:
        cursor.close()
        conn.close()


# ============================================================
# COMBINED OPERATIONS
# ============================================================

def get_all_embeddings(user_type=None):
    """
    Load embeddings theo loại user
    
    Args:
        user_type: 'member', 'employee', hoặc None (load tất cả)
    
    Returns:
        dict: {
            '123': embeddings,           # member_id
            'emp_45': embeddings,         # employee với prefix
            ...
        }
    """
    if user_type == 'member':
        return get_all_member_embeddings()
    elif user_type == 'employee':
        return get_all_employee_embeddings()
    else:
        # Load tất cả
        member_embs = get_all_member_embeddings()
        employee_embs = get_all_employee_embeddings()
        
        # Merge hai dicts
        all_embeddings = {**member_embs, **employee_embs}
        
        print(f"✅ Loaded {len(member_embs)} members + {len(employee_embs)} employees = {len(all_embeddings)} total")
        return all_embeddings


def get_stats():
    """
    Thống kê embeddings trong database
    """
    conn = get_connection()
    if not conn:
        return {'members': 0, 'employees': 0, 'total': 0}
    
    try:
        cursor = conn.cursor()
        
        cursor.execute('SELECT COUNT(*) FROM member_face_embeddings')
        member_count = cursor.fetchone()[0]
        
        cursor.execute('SELECT COUNT(*) FROM employee_face_embeddings')
        employee_count = cursor.fetchone()[0]
        
        return {
            'members': member_count,
            'employees': employee_count,
            'total': member_count + employee_count
        }
        
    except Error as e:
        print(f"Error: {e}")
        return {'members': 0, 'employees': 0, 'total': 0}
    finally:
        cursor.close()
        conn.close()


# ============================================================
# TESTING
# ============================================================

if __name__ == '__main__':
    print("=" * 60)
    print("TESTING EMBEDDINGS OPERATIONS")
    print("=" * 60)
    
    # Test stats
    stats = get_stats()
    print(f"\nEmbeddings Statistics:")
    print(f"  Members with face data: {stats['members']}")
    print(f"  Employees with face data: {stats['employees']}")
    print(f"  Total: {stats['total']}")
    
    # Test load all
    print("\nLoading all embeddings...")
    all_embs = get_all_embeddings()
    
    if all_embs:
        print("\nSample embeddings:")
        for person_id, embs in list(all_embs.items())[:3]:
            print(f"  {person_id}: {embs.shape}")
