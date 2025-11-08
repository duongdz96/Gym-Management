"""
Script để kiểm tra database và debug vấn đề check-in
"""

import sqlite3
from datetime import datetime

DB_PATH = 'data/face_recognition.db'

def check_database():
    """Kiểm tra toàn bộ database"""
    print("\n" + "=" * 70)
    print("DATABASE DIAGNOSTIC TOOL")
    print("=" * 70)

    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()

    # 1. Check Members
    print("\n📊 MEMBERS:")
    print("-" * 70)
    cursor.execute('SELECT id, name, status FROM Members')
    members = cursor.fetchall()
    if members:
        for member_id, name, status in members:
            print(f"  ID {member_id}: {name} ({status})")
    else:
        print("  ⚠️  No members found")

    # 2. Check Employees
    print("\n📊 EMPLOYEES:")
    print("-" * 70)
    cursor.execute('SELECT id, name, status FROM Employees')
    employees = cursor.fetchall()
    if employees:
        for emp_id, name, status in employees:
            print(f"  ID {emp_id}: {name} ({status})")
    else:
        print("  ⚠️  No employees found")

    # 3. Check MemberEmbeddings
    print("\n📊 MEMBER EMBEDDINGS:")
    print("-" * 70)
    cursor.execute('SELECT member_id, COUNT(*) FROM MemberEmbeddings GROUP BY member_id')
    embeddings = cursor.fetchall()
    if embeddings:
        for member_id, count in embeddings:
            cursor.execute('SELECT name FROM Members WHERE id = ?', (member_id,))
            name = cursor.fetchone()[0]
            print(f"  Member {member_id} ({name}): {count} embeddings")
    else:
        print("  ⚠️  No member embeddings found")

    # 4. Check EmployeeEmbeddings
    print("\n📊 EMPLOYEE EMBEDDINGS:")
    print("-" * 70)
    cursor.execute('SELECT employee_id, COUNT(*) FROM EmployeeEmbeddings GROUP BY employee_id')
    embeddings = cursor.fetchall()
    if embeddings:
        for emp_id, count in embeddings:
            cursor.execute('SELECT name FROM Employees WHERE id = ?', (emp_id,))
            name = cursor.fetchone()[0]
            print(f"  Employee {emp_id} ({name}): {count} embeddings")
    else:
        print("  ⚠️  No employee embeddings found")

    # 5. Check AccessLogs (IMPORTANT)
    print("\n📊 ACCESS LOGS (Member Check-ins):")
    print("-" * 70)
    cursor.execute('SELECT COUNT(*) FROM AccessLogs')
    total_logs = cursor.fetchone()[0]
    print(f"  Total logs: {total_logs}")

    if total_logs > 0:
        cursor.execute('''
            SELECT al.id, al.member_id, m.name, al.access_time
            FROM AccessLogs al
            JOIN Members m ON al.member_id = m.id
            ORDER BY al.id DESC
            LIMIT 10
        ''')
        logs = cursor.fetchall()
        print("\n  Latest 10 check-ins:")
        for log_id, member_id, name, access_time in logs:
            print(f"    #{log_id}: {name} (ID {member_id}) - {access_time}")
    else:
        print("  ⚠️  NO ACCESS LOGS FOUND!")
        print("  ⚠️  This means check-ins are NOT being recorded!")

    # 6. Check EmployeeLogs
    print("\n📊 EMPLOYEE LOGS (Employee Check-in/out):")
    print("-" * 70)
    cursor.execute('SELECT COUNT(*) FROM EmployeeLogs')
    total_logs = cursor.fetchone()[0]
    print(f"  Total logs: {total_logs}")

    if total_logs > 0:
        cursor.execute('''
            SELECT el.id, el.employee_id, e.name, el.check_type, el.log_time
            FROM EmployeeLogs el
            JOIN Employees e ON el.employee_id = e.id
            ORDER BY el.id DESC
            LIMIT 10
        ''')
        logs = cursor.fetchall()
        print("\n  Latest 10 check-in/outs:")
        for log_id, emp_id, name, check_type, log_time in logs:
            print(f"    #{log_id}: {name} (ID {emp_id}) - {check_type.upper()} - {log_time}")
    else:
        print("  ⚠️  No employee logs found")

    # 7. Check today's logs
    print("\n📊 TODAY'S ACTIVITY:")
    print("-" * 70)
    today = datetime.now().strftime("%d-%m-%Y")

    cursor.execute('''
        SELECT COUNT(*) FROM AccessLogs
        WHERE access_time LIKE ?
    ''', (f"%{today}%",))
    today_member_logs = cursor.fetchone()[0]
    print(f"  Member check-ins today: {today_member_logs}")

    cursor.execute('''
        SELECT COUNT(*) FROM EmployeeLogs
        WHERE log_time LIKE ?
    ''', (f"%{today}%",))
    today_employee_logs = cursor.fetchone()[0]
    print(f"  Employee check-in/outs today: {today_employee_logs}")

    conn.close()

    # 8. Summary
    print("\n" + "=" * 70)
    print("DIAGNOSIS:")
    print("=" * 70)

    if total_logs == 0:
        print("❌ PROBLEM FOUND: No access logs in database!")
        print("\n Possible causes:")
        print("  1. MEMBER_COOLDOWN too long (currently 300 seconds = 5 minutes)")
        print("  2. Check-in logic not triggering (temporal smoothing issue)")
        print("  3. Database permissions issue")
        print("  4. Code error when logging")
        print("\n Recommendations:")
        print("  • Reduce MEMBER_COOLDOWN in config.py to 10 seconds for testing")
        print("  • Check console output when running test_improved.py")
        print("  • Verify that '✓ Member {name} checked in' message appears")
    else:
        print("✅ Database is working! Access logs are being recorded.")
        print(f"   Total check-ins: {total_logs}")

    print("=" * 70)


def test_manual_log():
    """Test thử ghi log thủ công"""
    print("\n" + "=" * 70)
    print("MANUAL LOG TEST")
    print("=" * 70)

    from database import log_access, log_employee_access
    from datetime import datetime

    # Test member log
    try:
        test_time = datetime.now().strftime("%d-%m-%Y %H:%M:%S")
        log_id = log_access(1, test_time)  # Assume member_id = 1 exists
        print(f"✅ Test member log created successfully (ID: {log_id})")
        print(f"   Time: {test_time}")
    except Exception as e:
        print(f"❌ Error creating member log: {e}")

    # Test employee log
    try:
        test_time = datetime.now().strftime("%d-%m-%Y %H:%M:%S")
        log_employee_access(1, 'in', test_time)  # Assume employee_id = 1 exists
        print(f"✅ Test employee log created successfully")
        print(f"   Time: {test_time}")
    except Exception as e:
        print(f"❌ Error creating employee log: {e}")

    print("=" * 70)


if __name__ == '__main__':
    check_database()

    print("\n")
    choice = input("Do you want to test manual logging? (y/n): ").lower()
    if choice == 'y':
        test_manual_log()
        print("\nRe-checking database after manual test:")
        check_database()
