"""
Script helper để setup database và thêm members/employees
Dùng cho lần đầu tiên chạy project
"""

from database import (add_member_without_face_data, add_employee_without_face_data,
                      get_all_active_members, get_all_active_employees)
from database_embeddings import get_stats


def show_current_data():
    """Hiển thị data hiện tại trong database"""
    print("\n" + "=" * 60)
    print("DATABASE CURRENT STATUS")
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


def add_members_interactive():
    """Thêm members interactively"""
    print("\n" + "=" * 60)
    print("THÊM MEMBERS (Khách hàng)")
    print("=" * 60)
    print("Nhập tên members, mỗi dòng 1 tên.")
    print("Nhập 'done' khi hoàn thành.\n")

    count = 0
    while True:
        name = input(f"Member {count + 1} (hoặc 'done'): ").strip()

        if name.lower() == 'done':
            break

        if not name:
            print("  ⚠️ Tên không được để trống")
            continue

        try:
            member_id = add_member_without_face_data(name)
            print(f"  ✅ Đã thêm: {name} (ID: {member_id})")
            count += 1
        except Exception as e:
            print(f"  ❌ Lỗi: {e}")

    print(f"\n✅ Đã thêm {count} members")


def add_employees_interactive():
    """Thêm employees interactively"""
    print("\n" + "=" * 60)
    print("THÊM EMPLOYEES (Nhân viên)")
    print("=" * 60)
    print("Nhập tên employees, mỗi dòng 1 tên.")
    print("Nhập 'done' khi hoàn thành.\n")

    count = 0
    while True:
        name = input(f"Employee {count + 1} (hoặc 'done'): ").strip()

        if name.lower() == 'done':
            break

        if not name:
            print("  ⚠️ Tên không được để trống")
            continue

        try:
            employee_id = add_employee_without_face_data(name)
            print(f"  ✅ Đã thêm: {name} (ID: {employee_id})")
            count += 1
        except Exception as e:
            print(f"  ❌ Lỗi: {e}")

    print(f"\n✅ Đã thêm {count} employees")


def add_sample_data():
    """Thêm sample data để test nhanh"""
    print("\n" + "=" * 60)
    print("THÊM SAMPLE DATA (Để test)")
    print("=" * 60)

    confirm = input("Thêm sample data? (y/n): ").strip().lower()

    if confirm != 'y':
        print("Hủy bỏ.")
        return

    sample_members = [
        "Nguyen Van A",
        "Tran Thi B",
        "Le Van C",
        "Pham Thi D"
    ]

    sample_employees = [
        "Nguyen Van E - NV",
        "Tran Thi F - NV"
    ]

    print("\nThêm members...")
    for name in sample_members:
        try:
            member_id = add_member_without_face_data(name)
            print(f"  ✅ {name} (ID: {member_id})")
        except Exception as e:
            print(f"  ❌ {name}: {e}")

    print("\nThêm employees...")
    for name in sample_employees:
        try:
            employee_id = add_employee_without_face_data(name)
            print(f"  ✅ {name} (ID: {employee_id})")
        except Exception as e:
            print(f"  ❌ {name}: {e}")

    print("\n✅ Hoàn thành thêm sample data")


def main():
    """Main menu"""
    while True:
        print("\n" + "=" * 60)
        print("DATABASE SETUP HELPER")
        print("=" * 60)
        print("\nChọn chức năng:")
        print("  1. Xem data hiện tại")
        print("  2. Thêm Members (thủ công)")
        print("  3. Thêm Employees (thủ công)")
        print("  4. Thêm sample data (nhanh)")
        print("  0. Thoát")
        print("=" * 60)

        try:
            choice = input("\nNhập lựa chọn (0-4): ").strip()

            if choice == '0':
                print("\nThoát chương trình.")
                break
            elif choice == '1':
                show_current_data()
            elif choice == '2':
                add_members_interactive()
                show_current_data()
            elif choice == '3':
                add_employees_interactive()
                show_current_data()
            elif choice == '4':
                add_sample_data()
                show_current_data()
            else:
                print("⚠️ Lựa chọn không hợp lệ")

        except KeyboardInterrupt:
            print("\n\nĐã dừng bởi người dùng.")
            break
        except Exception as e:
            print(f"\n❌ Lỗi: {e}")

    print("\n" + "=" * 60)
    print("NEXT STEPS:")
    print("=" * 60)
    print("\n1. Chạy để thu thập face data:")
    print("   python add_faces_improved.py")
    print("\n2. Sau đó chạy nhận diện:")
    print("   python test_improved.py")
    print("\n" + "=" * 60)


if __name__ == '__main__':
    # Show initial status
    show_current_data()

    # Main menu
    main()
