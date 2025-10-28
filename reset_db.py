import sqlite3

DB_PATH = 'data/face_recognition.db'

def reset_db():
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('DROP TABLE IF EXISTS Members')
    cursor.execute('DROP TABLE IF EXISTS AccessLogs')
    cursor.execute('''
        CREATE TABLE Members (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT,
            status TEXT DEFAULT 'active',
            face_data BLOB
        )
    ''')
    cursor.execute('''
        CREATE TABLE AccessLogs (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            member_id INTEGER,
            access_time TEXT,
            FOREIGN KEY (member_id) REFERENCES Members(id)
        )
    ''')
    conn.commit()
    conn.close()
    print("Database reset successfully.")

reset_db()