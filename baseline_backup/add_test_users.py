from database import add_member_without_face_data

# List of test users to add
test_users = [
    "Nguyen Van A",
    "Tran Thi B",
    "Le Van C",
    "Pham Thi D",
    "Hoang Van E"
]

print("Adding test users to database...")
for name in test_users:
    member_id = add_member_without_face_data(name)
    print(f"Added: {name} with ID: {member_id}")

print("All test users added successfully!")