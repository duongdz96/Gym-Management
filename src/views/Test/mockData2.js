// Tính từ ngày hiện tại của mock data (giả sử là 2025-12-08)
const TODAY = new Date('2025-12-08');
const YESTERDAY = new Date(TODAY);
YESTERDAY.setDate(TODAY.getDate() - 1);
const NEXT_WEEK = new Date(TODAY);
NEXT_WEEK.setDate(TODAY.getDate() + 7);

// --- 1. USERS, MEMBERS, TEACHERS (Kế thừa từ User) ---

export const users = [
    // Users for Teachers
    { id: 1, email: 'john.doe@gym.com', password: 'hashedpassword', fullName: 'John Doe', dob: '1990-05-15', gender: 'Male', phone: '0901111111', role: 'TEACHER', isDeleted: false },
    { id: 2, email: 'jane.smith@gym.com', password: 'hashedpassword', fullName: 'Jane Smith', dob: '1985-11-20', gender: 'Female', phone: '0902222222', role: 'TEACHER', isDeleted: false },
    
    // Users for Members
    { id: 3, email: 'member.a@gym.com', password: 'hashedpassword', fullName: 'Alice Wonderland', dob: '1998-01-01', gender: 'Female', phone: '0903333333', role: 'MEMBER', isDeleted: false },
    { id: 4, email: 'member.b@gym.com', password: 'hashedpassword', fullName: 'Bob Builder', dob: '2000-10-25', gender: 'Male', phone: '0904444444', role: 'MEMBER', isDeleted: false },
];

export const teachers = [
    { id: 1, position: 'Senior Instructor', specialize: 'Yoga, Pilates' },
    { id: 2, position: 'Junior Coach', specialize: 'Cardio, HIIT' },
];

export const members = [
    { id: 3, membership: 'VIP', joinDate: '2025-10-01', status: 'Active', faceId: 'face_123', cardId: 'card_a1' },
    { id: 4, membership: 'Standard', joinDate: '2025-11-15', status: 'Active', faceId: 'face_456', cardId: 'card_b2' },
];

// --- 2. ROOM ---

export const rooms = [
    { id: 1, name: 'Phòng Yoga A', note: 'Phòng tập Yoga với gương lớn', location: 'Tầng 2' },
    { id: 2, name: 'Phòng Cardio B', note: 'Phòng tập Cardio với máy chạy bộ', location: 'Tầng 1' },
    { id: 3, name: 'Phòng Dance C', note: 'Phòng tập Dance rộng rãi', location: 'Tầng 3' },
    { id: 4, name: 'Phòng Spin D', note: 'Phòng tập Spin rộng rãi', location: 'Tầng 4' },
];

// --- 3. FITNESS CLASS (Template) ---

export const fitnessClasses = [
    { 
        id: 101, 
        name: 'Yoga cơ bản', 
        description: 'Các tư thế Yoga nhập môn cho người mới.', 
        difficultyLevel: 'Beginner', 
        status: 'Active' 
    },
    { 
        id: 102, 
        name: 'HIIT đốt mỡ', 
        description: 'Bài tập cường độ cao giúp đốt cháy calo nhanh chóng.', 
        difficultyLevel: 'Intermediate', 
        status: 'Active' 
    },
    { 
        id: 103, 
        name: 'Zumba Dance', 
        description: 'Lớp nhảy với âm nhạc Latin sôi động.', 
        difficultyLevel: 'All Levels', 
        status: 'Active' 
    },
];

// --- 4. SCHEDULE PATTERN (Tạo lịch tự động) ---

export const schedulePatterns = [
    { 
        id: 201, 
        daysOfWeek: 'MONDAY,WEDNESDAY,FRIDAY', // Thứ 2, 4, 6
        timeStart: '18:00:00', 
        timeEnd: '19:00:00', 
        classStartDate: '2025-12-01', 
        classEndDate: '2026-03-31' 
    },
    { 
        id: 202, 
        daysOfWeek: 'TUESDAY,THURSDAY', // Thứ 3, 5
        timeStart: '07:30:00', 
        timeEnd: '08:30:00', 
        classStartDate: '2025-12-01', 
        classEndDate: '2026-03-31' 
    },
];

// --- 5. CLASS SCHEDULE (Các buổi học cụ thể được generate) ---

export const classSchedules = [
    // Lịch Yoga cơ bản (FC 101) - T2
    { 
        id: 301, 
        startTime: new Date(TODAY.getFullYear(), TODAY.getMonth(), TODAY.getDate(), 18, 0, 0).toISOString(),
        endTime: new Date(TODAY.getFullYear(), TODAY.getMonth(), TODAY.getDate(), 19, 0, 0).toISOString(),
        capacity: 20, 
        status: 'OPEN', 
        fitnessClassId: 101, 
        schedulePatternId: 201, 
        roomId: 1 
    },
    // Lịch HIIT đốt mỡ (FC 102) - T3 (Hôm sau)
    { 
        id: 302, 
        startTime: new Date(NEXT_WEEK.getFullYear(), NEXT_WEEK.getMonth(), NEXT_WEEK.getDate(), 7, 30, 0).toISOString(),
        endTime: new Date(NEXT_WEEK.getFullYear(), NEXT_WEEK.getMonth(), NEXT_WEEK.getDate(), 8, 30, 0).toISOString(),
        capacity: 15, 
        status: 'OPEN', 
        fitnessClassId: 102, 
        schedulePatternId: 202, 
        roomId: 2 
    },
    // Lịch Yoga cơ bản (FC 101) - Lịch đã quá (T2 tuần trước)
    { 
        id: 303, 
        startTime: new Date(YESTERDAY.getFullYear(), YESTERDAY.getMonth(), YESTERDAY.getDate(), 18, 0, 0).toISOString(),
        endTime: new Date(YESTERDAY.getFullYear(), YESTERDAY.getMonth(), YESTERDAY.getDate(), 19, 0, 0).toISOString(),
        capacity: 20, 
        status: 'CLOSED', 
        fitnessClassId: 101, 
        schedulePatternId: 201, 
        roomId: 1 
    },
];

// --- 6. CLASS REGISTRATION (Phân công giáo viên cho Lớp) ---

export const classRegistrations = [
    // John Doe (Teacher 1) dạy Yoga cơ bản (FC 101)
    { id: 401, staffId: 1, fitnessClassId: 101, description: 'Phân công chính thức' },
    // Jane Smith (Teacher 2) dạy HIIT đốt mỡ (FC 102)
    { id: 402, staffId: 2, fitnessClassId: 102, description: 'Phân công chính thức' },
];

// --- 7. MEMBER REGISTRATION (Thành viên đăng ký Lịch cụ thể) ---

export const memberRegistrations = [
    // Alice (Member 3) đăng ký buổi Yoga hôm nay (CS 301)
    { id: 501, followDate: new Date().toISOString(), classScheduleId: 301, memberId: 3 },
    // Bob (Member 4) đăng ký buổi HIIT tuần sau (CS 302)
    { id: 502, followDate: new Date().toISOString(), classScheduleId: 302, memberId: 4 },
    // Alice (Member 3) đã học buổi Yoga tuần trước (CS 303)
    { id: 503, followDate: new Date(YESTERDAY).toISOString(), classScheduleId: 303, memberId: 3 },
];