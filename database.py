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
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS Employees (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT,
            status TEXT DEFAULT 'active',
            face_data BLOB
        )
    ''')
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS EmployeeLogs (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            employee_id INTEGER,
            check_type TEXT,  -- 'in' or 'out'
            log_time TEXT,
            FOREIGN KEY (employee_id) REFERENCES Employees(id)
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

def add_member_without_face_data(name):
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        INSERT INTO Members (name, status, face_data)
        VALUES (?, ?, NULL)
    ''', (name, 'active'))
    member_id = cursor.lastrowid
    conn.commit()
    conn.close()
    return member_id

def get_all_faces_and_labels():
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('SELECT id, face_data FROM Members WHERE status = "active" AND face_data IS NOT NULL')
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
    log_id = cursor.lastrowid
    conn.commit()
    conn.close()
    return log_id

def update_access_log(log_id, new_access_time):
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        UPDATE AccessLogs SET access_time = ? WHERE id = ?
    ''', (new_access_time, log_id))
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

def get_employee_logs_for_date(date):
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        SELECT employee_id, check_type, log_time FROM EmployeeLogs
        WHERE log_time LIKE ?
    ''', (f"%{date}%",))
    rows = cursor.fetchall()
    conn.close()
    return rows

def get_all_logs_for_date(date):
    member_logs = get_logs_for_date(date)
    employee_logs = get_employee_logs_for_date(date)
    # Format: [(id, time, type), ...]
    all_logs = []
    for mid, time in member_logs:
        all_logs.append((mid, time, 'Member Check-in'))
    for eid, check_type, time in employee_logs:
        all_logs.append((eid, time, f'Employee {check_type.capitalize()}'))
    # Sort by time
    all_logs.sort(key=lambda x: x[1])
    return all_logs

def get_member_name(member_id):
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('SELECT name FROM Members WHERE id = ?', (int(member_id),))
    result = cursor.fetchone()
    conn.close()
    return result[0] if result else 'Unknown'

def get_all_active_members():
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('SELECT id, name FROM Members WHERE status = "active"')
    rows = cursor.fetchall()
    conn.close()
    return rows

def update_member_face_data(member_id, face_data):
    serialized_face_data = pickle.dumps(face_data)
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        UPDATE Members SET face_data = ? WHERE id = ?
    ''', (serialized_face_data, member_id))
    conn.commit()
    conn.close()

def add_employee(name, face_data):
    serialized_face_data = pickle.dumps(face_data)
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        INSERT INTO Employees (name, status, face_data)
        VALUES (?, ?, ?)
    ''', (name, 'active', serialized_face_data))
    employee_id = cursor.lastrowid
    conn.commit()
    conn.close()
    return employee_id

def get_all_employee_faces_and_labels():
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('SELECT id, face_data FROM Employees WHERE status = "active" AND face_data IS NOT NULL')
    rows = cursor.fetchall()
    labels = []
    faces = []
    for row in rows:
        employee_id, serialized_face_data = row
        face_data = pickle.loads(serialized_face_data)
        labels.extend([f"emp_{employee_id}"] * face_data.shape[0])  # Prefix to distinguish
        faces.append(face_data)
    conn.close()
    if faces:
        return np.concatenate(faces), labels
    return np.array([]), []

def get_employee_status(employee_id):
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('SELECT status FROM Employees WHERE id = ?', (int(employee_id),))
    result = cursor.fetchone()
    conn.close()
    return result[0] if result else None

def log_employee_access(employee_id, check_type, log_time):
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        INSERT INTO EmployeeLogs (employee_id, check_type, log_time)
        VALUES (?, ?, ?)
    ''', (int(employee_id), check_type, log_time))
    log_id = cursor.lastrowid
    conn.commit()
    conn.close()
    return log_id

def get_employee_name(employee_id):
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('SELECT name FROM Employees WHERE id = ?', (int(employee_id),))
    result = cursor.fetchone()
    conn.close()
    return result[0] if result else 'Unknown'

def get_last_employee_check_type(employee_id):
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('SELECT check_type FROM EmployeeLogs WHERE employee_id = ? ORDER BY id DESC LIMIT 1', (int(employee_id),))
    result = cursor.fetchone()
    conn.close()
    return result[0] if result else None

def get_all_active_employees():
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('SELECT id, name FROM Employees WHERE status = "active"')
    rows = cursor.fetchall()
    conn.close()
    return rows

def update_employee_face_data(employee_id, face_data):
    serialized_face_data = pickle.dumps(face_data)
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        UPDATE Employees SET face_data = ? WHERE id = ?
    ''', (serialized_face_data, employee_id))
    conn.commit()
    conn.close()

def add_employee_without_face_data(name):
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        INSERT INTO Employees (name, status, face_data)
        VALUES (?, ?, NULL)
    ''', (name, 'active'))
    employee_id = cursor.lastrowid
    conn.commit()
    conn.close()
    return employee_id

# Initialize the database
init_db()