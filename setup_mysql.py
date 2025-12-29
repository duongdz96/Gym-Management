"""
Script helper để test MySQL connection và setup
"""

from database import test_connection, get_all_active_members, get_all_active_employees
from database_embeddings import get_stats


def show_current_data():
    """Hiển thị data hiện tại trong MySQL database"""
    print("\n" + "=" * 60)
    print("MYSQL DATABASE CURRENT STATUS")
    print("=" * 60)

    # Members
    members = get_all_active_members()
    print(f"\nMembers ({len(members)}):")
    if members:
        for id, name in members:
            print(f"  {id}. {name}")
    else:
        print("  (empty)")

    # Employees  
    employees = get_all_active_employees()
    print(f"\nEmployees ({len(employees)}):")
    if employees:
        for id, name in employees:
            print(f"  {id}. {name}")
    else:
        print("  (empty)")

    # Embeddings stats
    stats = get_stats()
    print(f"\nEmbeddings:")
    print(f"  Members with face data: {stats['members']}")
    print(f"  Employees with face data: {stats['employees']}")

    print("=" * 60)


def main():
    """Main function"""
    print("\n" + "=" * 60)
    print("MYSQL SETUP & CONNECTION TEST")
    print("=" * 60)
    
    # Test connection
    print("\n1. Testing MySQL connection...")
    if not test_connection():
        print("\n❌ MySQL connection failed!")
        print("   Please check:")
        print("   - MySQL server is running")
        print("   - Database 'gympool' exists")
        print("   - Username/password in database_mysql.py")
        return
    
    # Show current data
    print("\n2. Loading current data...")
    show_current_data()
    
    print("\n✅ Setup complete!")
    print("\nNext steps:")
    print("  1. Run: python add_faces_improved.py  (to collect face data)")
    print("  2. Run: python test_improved.py       (to start recognition)")


if __name__ == '__main__':
    main()
