"""
Database operations cho face embeddings (DeepFace version)
Mở rộng database.py để support 512D embeddings thay vì raw pixels
"""

import sqlite3
import pickle
import numpy as np

DB_PATH = 'data/face_recognition.db'

def init_embeddings_tables():
    """
    Tạo tables mới cho embeddings
    Giữ nguyên tables cũ để có thể so sánh baseline vs improved
    """
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()

    # Table cho member embeddings
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS MemberEmbeddings (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            member_id INTEGER,
            embeddings BLOB,
            model_name TEXT DEFAULT 'ArcFace',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (member_id) REFERENCES Members(id)
        )
    ''')

    # Table cho employee embeddings
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS EmployeeEmbeddings (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            employee_id INTEGER,
            embeddings BLOB,
            model_name TEXT DEFAULT 'ArcFace',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (employee_id) REFERENCES Employees(id)
        )
    ''')

    conn.commit()
    conn.close()
    print("✅ Embeddings tables initialized")


def save_member_embeddings(member_id, embeddings, model_name='ArcFace'):
    """
    Lưu face embeddings cho member

    Args:
        member_id: ID của member
        embeddings: numpy array shape (N, 512) - N embeddings, mỗi cái 512D
        model_name: Tên model (ArcFace, Facenet512, etc.)
    """
    # Delete old embeddings if exist
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('DELETE FROM MemberEmbeddings WHERE member_id = ?', (member_id,))

    # Serialize embeddings
    serialized = pickle.dumps(embeddings)

    # Insert new embeddings
    cursor.execute('''
        INSERT INTO MemberEmbeddings (member_id, embeddings, model_name)
        VALUES (?, ?, ?)
    ''', (member_id, serialized, model_name))

    conn.commit()
    conn.close()
    print(f"✅ Saved {len(embeddings)} embeddings for member {member_id}")


def save_employee_embeddings(employee_id, embeddings, model_name='ArcFace'):
    """
    Lưu face embeddings cho employee

    Args:
        employee_id: ID của employee
        embeddings: numpy array shape (N, 512)
        model_name: Tên model
    """
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('DELETE FROM EmployeeEmbeddings WHERE employee_id = ?', (employee_id,))

    serialized = pickle.dumps(embeddings)

    cursor.execute('''
        INSERT INTO EmployeeEmbeddings (employee_id, embeddings, model_name)
        VALUES (?, ?, ?)
    ''', (employee_id, serialized, model_name))

    conn.commit()
    conn.close()
    print(f"✅ Saved {len(embeddings)} embeddings for employee {employee_id}")


def get_all_member_embeddings():
    """
    Load tất cả member embeddings từ database

    Returns:
        dict: {member_id: embeddings_array, ...}
    """
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()

    # Chỉ lấy members có status active
    cursor.execute('''
        SELECT me.member_id, me.embeddings
        FROM MemberEmbeddings me
        JOIN Members m ON me.member_id = m.id
        WHERE m.status = 'active'
    ''')

    rows = cursor.fetchall()
    conn.close()

    embeddings_dict = {}
    for member_id, serialized in rows:
        embeddings = pickle.loads(serialized)
        embeddings_dict[member_id] = embeddings

    return embeddings_dict


def get_all_employee_embeddings():
    """
    Load tất cả employee embeddings từ database

    Returns:
        dict: {employee_id: embeddings_array, ...}
    """
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()

    cursor.execute('''
        SELECT ee.employee_id, ee.embeddings
        FROM EmployeeEmbeddings ee
        JOIN Employees e ON ee.employee_id = e.id
        WHERE e.status = 'active'
    ''')

    rows = cursor.fetchall()
    conn.close()

    embeddings_dict = {}
    for employee_id, serialized in rows:
        embeddings = pickle.loads(serialized)
        embeddings_dict[f"emp_{employee_id}"] = embeddings  # Prefix để phân biệt

    return embeddings_dict


def get_all_embeddings():
    """
    Load TẤT CẢ embeddings (cả members và employees)

    Returns:
        dict: {
            '123': embeddings,           # member_id
            'emp_45': embeddings,         # employee_id với prefix
            ...
        }
    """
    member_embs = get_all_member_embeddings()
    employee_embs = get_all_employee_embeddings()

    # Merge hai dicts
    all_embeddings = {}

    # Add member embeddings với key là member_id
    for member_id, embs in member_embs.items():
        all_embeddings[str(member_id)] = embs

    # Add employee embeddings với key là "emp_{employee_id}"
    all_embeddings.update(employee_embs)

    print(f"✅ Loaded {len(member_embs)} members + {len(employee_embs)} employees")
    return all_embeddings


def get_member_embedding(member_id):
    """
    Lấy embeddings của 1 member cụ thể

    Returns:
        numpy array hoặc None
    """
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()

    cursor.execute('''
        SELECT embeddings FROM MemberEmbeddings
        WHERE member_id = ?
    ''', (member_id,))

    result = cursor.fetchone()
    conn.close()

    if result:
        return pickle.loads(result[0])
    return None


def delete_member_embeddings(member_id):
    """Xóa embeddings của member"""
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('DELETE FROM MemberEmbeddings WHERE member_id = ?', (member_id,))
    conn.commit()
    conn.close()


def delete_employee_embeddings(employee_id):
    """Xóa embeddings của employee"""
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('DELETE FROM EmployeeEmbeddings WHERE employee_id = ?', (employee_id,))
    conn.commit()
    conn.close()


def get_stats():
    """
    Thống kê embeddings trong database
    """
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()

    cursor.execute('SELECT COUNT(*) FROM MemberEmbeddings')
    member_count = cursor.fetchone()[0]

    cursor.execute('SELECT COUNT(*) FROM EmployeeEmbeddings')
    employee_count = cursor.fetchone()[0]

    conn.close()

    return {
        'members': member_count,
        'employees': employee_count,
        'total': member_count + employee_count
    }


# Initialize tables khi import module
init_embeddings_tables()
