from database import add_employee_without_face_data

# List of test employees to add
test_employees = [
    "Nguyen Van Manager",
    "Tran Thi Staff",
    "Le Van Worker",
    "Pham Thi Admin",
    "Hoang Van Supervisor"
]

print("Adding test employees to database...")
for name in test_employees:
    employee_id = add_employee_without_face_data(name)
    print(f"Added: {name} with ID: {employee_id}")

print("All test employees added successfully!")