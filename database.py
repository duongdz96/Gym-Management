import sqlite3
import pickle
import numpy as np

DB_PATH = 'data/face_recognition.db'

def init_db():
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS Members (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT,
            status TEXT DEFAULT 'active',
            face_data BLOB
        )
    ''')
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS AccessLogs (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            member_id INTEGER,
            access_time TEXT,
            FOREIGN KEY (member_id) REFERENCES Members(id)
        )
    ''')
    conn.commit()
    conn.close()

def add_member(name, face_data):
    serialized_face_data = pickle.dumps(face_data)
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        INSERT INTO Members (name, status, face_data)
        VALUES (?, ?, ?)
    ''', (name, 'active', serialized_face_data))
    member_id = cursor.lastrowid
    conn.commit()
    conn.close()
    return member_id

def get_all_faces_and_labels():
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('SELECT id, face_data FROM Members WHERE status = "active"')
    rows = cursor.fetchall()
    labels = []
    faces = []
    for row in rows:
        member_id, serialized_face_data = row
        face_data = pickle.loads(serialized_face_data)
        labels.extend([str(member_id)] * face_data.shape[0])
        faces.append(face_data)
    conn.close()
    if faces:
        return np.concatenate(faces), labels
    return np.array([]), []

def get_member_status(member_id):
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('SELECT status FROM Members WHERE id = ?', (int(member_id),))
    result = cursor.fetchone()
    conn.close()
    return result[0] if result else None

def log_access(member_id, access_time):
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        INSERT INTO AccessLogs (member_id, access_time)
        VALUES (?, ?)
    ''', (int(member_id), access_time))
    conn.commit()
    conn.close()

def get_logs_for_date(date):
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        SELECT member_id, access_time FROM AccessLogs
        WHERE access_time LIKE ?
    ''', (f"%{date}%",))
    rows = cursor.fetchall()
    conn.close()
    return rows

def get_member_name(member_id):
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('SELECT name FROM Members WHERE id = ?', (int(member_id),))
    result = cursor.fetchone()
    conn.close()
    return result[0] if result else 'Unknown'

# Initialize the database
init_db()