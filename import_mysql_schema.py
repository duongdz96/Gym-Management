"""
Import MySQL schema for face recognition integration

This script imports the create_mysql_tables.sql file into MySQL database.
Run this after setting up MySQL connection.
"""

import os
import subprocess

def import_mysql_schema():
    """Import the MySQL schema file"""

    sql_file = 'create_mysql_tables.sql'

    if not os.path.exists(sql_file):
        print(f"❌ SQL file not found: {sql_file}")
        return False

    print("📋 Importing MySQL schema...")
    print(f"File: {sql_file}")

    # MySQL connection command
    # Adjust these values according to your MySQL setup
    mysql_host = 'localhost'
    mysql_port = '3306'
    mysql_user = 'root'
    mysql_password = 'password'  # Change this
    mysql_database = 'gymsystem'

    # Command to import SQL file
    cmd = [
        'mysql',
        f'--host={mysql_host}',
        f'--port={mysql_port}',
        f'--user={mysql_user}',
        f'--password={mysql_password}',
        mysql_database
    ]

    try:
        # Read SQL file
        with open(sql_file, 'r', encoding='utf-8') as f:
            sql_content = f.read()

        # Execute SQL via mysql command
        process = subprocess.Popen(
            cmd,
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True
        )

        stdout, stderr = process.communicate(input=sql_content)

        if process.returncode == 0:
            print("✅ MySQL schema imported successfully!")
            print("\nCreated tables:")
            print("• users - Common user table")
            print("• members - Customers with face_id")
            print("• staffs - Employees")
            print("• access_logs - Check-in logs")
            print("• MemberEmbeddings - Face embeddings for members")
            print("• EmployeeEmbeddings - Face embeddings for staff (future)")
            return True
        else:
            print(f"❌ Import failed: {stderr}")
            return False

    except Exception as e:
        print(f"❌ Error importing schema: {e}")
        return False

def show_usage():
    """Show usage instructions"""
    print("="*60)
    print("MySQL Schema Import Tool")
    print("="*60)
    print()
    print("This tool imports create_mysql_tables.sql into MySQL")
    print()
    print("Prerequisites:")
    print("1. MySQL server running")
    print("2. Database 'gymsystem' exists")
    print("3. MySQL client installed")
    print()
    print("Usage:")
    print("1. Update MySQL credentials in this script")
    print("2. Run: python import_mysql_schema.py")
    print()
    print("Manual import (alternative):")
    print("mysql -u root -p gymsystem < create_mysql_tables.sql")
    print()
    print("="*60)

if __name__ == '__main__':
    show_usage()

    proceed = input("Proceed with import? (y/n): ").lower()
    if proceed == 'y':
        success = import_mysql_schema()
        if success:
            print("\nNext steps:")
            print("1. Use migrate_sqlite_to_mysql.py to transfer data")
            print("2. Update face recognition code to use MySQL")
            print("3. Test the integration")
    else:
        print("Import cancelled")