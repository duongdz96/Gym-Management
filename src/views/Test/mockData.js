// Mock Data for Gym Class Management System
// This file contains all mock data and helper functions for CRUD operations

// ==================== INITIAL DATA ====================

export const rooms = [
    { id: 1, name: 'Phòng Yoga A', capacity: 20, description: 'Phòng tập Yoga với gương lớn' },
    { id: 2, name: 'Phòng Cardio B', capacity: 15, description: 'Phòng tập Cardio với máy chạy bộ' },
    { id: 3, name: 'Phòng Dance C', capacity: 25, description: 'Phòng tập Dance rộng rãi' },
    { id: 4, name: 'Phòng Gym D', capacity: 30, description: 'Phòng tập Gym với đầy đủ thiết bị' },
    { id: 5, name: 'Phòng Pilates E', capacity: 12, description: 'Phòng tập Pilates nhỏ, yên tĩnh' }
];

export const teachers = [
    {
        id: 1,
        name: 'Nguyễn Văn An',
        email: 'an.nguyen@gym.com',
        specialties: ['Yoga', 'Pilates'],
        bio: 'Huấn luyện viên Yoga với 5 năm kinh nghiệm',
        avatar: '👨‍🏫'
    },
    {
        id: 2,
        name: 'Trần Thị Bình',
        email: 'binh.tran@gym.com',
        specialties: ['Dance', 'Aerobic'],
        bio: 'Chuyên gia Dance và Aerobic, từng đạt giải quốc gia',
        avatar: '👩‍🏫'
    },
    {
        id: 3,
        name: 'Lê Minh Châu',
        email: 'chau.le@gym.com',
        specialties: ['Gym', 'Strength Training'],
        bio: 'Huấn luyện viên thể hình chuyên nghiệp',
        avatar: '💪'
    },
    {
        id: 4,
        name: 'Phạm Thu Dung',
        email: 'dung.pham@gym.com',
        specialties: ['Yoga', 'Meditation'],
        bio: 'Chuyên gia Yoga trị liệu và thiền định',
        avatar: '🧘‍♀️'
    }
];

export const students = [
    {
        id: 1,
        name: 'Hoàng Minh Tuấn',
        email: 'tuan@student.com',
        membershipTier: 'VIP',
        joinDate: '2024-01-15'
    },
    {
        id: 2,
        name: 'Vũ Thị Hoa',
        email: 'hoa@student.com',
        membershipTier: 'PREMIUM',
        joinDate: '2024-03-20'
    },
    {
        id: 3,
        name: 'Đỗ Quang Huy',
        email: 'huy@student.com',
        membershipTier: 'BASIC',
        joinDate: '2024-06-10'
    },
    {
        id: 4,
        name: 'Ngô Lan Anh',
        email: 'anh@student.com',
        membershipTier: 'VIP',
        joinDate: '2024-02-28'
    }
];

// Class statuses:
// - draft: Lớp mới tạo, chưa hoàn thiện
// - pending_teacher: Đang chờ giáo viên đăng ký
// - waiting_approval: Có giáo viên đăng ký, chờ Manager duyệt
// - ready_for_students: Đã có giáo viên được duyệt, mở đăng ký cho học viên
// - cancelled: Lớp bị hủy

export const classes = [
    {
        id: 1,
        name: 'Yoga Buổi Sáng',
        description: 'Lớp Yoga nhẹ nhàng cho buổi sáng, phù hợp mọi lứa tuổi',
        difficulty: 'Beginner',
        maxStudents: 20,
        status: 'ready_for_students',
        roomId: 1,
        teacherId: 1,
        patternType: 'weekly', // weekly, monthly, custom_range, no_repeat
        startTime: '07:00',
        endTime: '08:30',
        daysOfWeek: [1, 3, 5], // Monday, Wednesday, Friday
        startDate: '2025-12-02',
        endDate: '2026-01-31',
        createdBy: 'manager',
        createdAt: '2025-11-20T10:00:00'
    },
    {
        id: 2,
        name: 'Dance Fitness',
        description: 'Lớp nhảy giảm cân với âm nhạc sôi động',
        difficulty: 'Intermediate',
        maxStudents: 25,
        status: 'pending_teacher',
        roomId: 3,
        teacherId: null,
        patternType: 'weekly',
        startTime: '18:00',
        endTime: '19:00',
        daysOfWeek: [2, 4], // Tuesday, Thursday
        startDate: '2025-12-05',
        endDate: '2026-02-28',
        createdBy: 'manager',
        createdAt: '2025-11-25T14:30:00'
    },
    {
        id: 3,
        name: 'Strength Training',
        description: 'Lớp tập gym tăng cơ cho nam',
        difficulty: 'Advanced',
        maxStudents: 15,
        status: 'waiting_approval',
        roomId: 4,
        teacherId: null,
        pendingTeacherId: 3, // Teacher đã apply, chờ duyệt
        patternType: 'weekly',
        startTime: '19:00',
        endTime: '20:30',
        daysOfWeek: [1, 3, 5],
        startDate: '2025-12-10',
        endDate: '2026-03-31',
        createdBy: 'manager',
        createdAt: '2025-11-28T09:15:00'
    },
    {
        id: 4,
        name: 'Pilates Nâng Cao',
        description: 'Lớp Pilates nâng cao cho người có kinh nghiệm',
        difficulty: 'Advanced',
        maxStudents: 12,
        status: 'ready_for_students',
        roomId: 5,
        teacherId: 4,
        patternType: 'weekly',
        startTime: '06:00',
        endTime: '07:00',
        daysOfWeek: [2, 4, 6],
        startDate: '2025-12-18', // 2 tuần sau (14 ngày) - VIP early access
        endDate: '2026-02-28',
        createdBy: 'manager',
        createdAt: '2025-11-28T15:00:00'
    },
    {
        id: 5,
        name: 'Cardio Buổi Tối',
        description: 'Lớp Cardio cường độ cao giúp đốt cháy mỡ thừa',
        difficulty: 'Intermediate',
        maxStudents: 20,
        status: 'ready_for_students',
        roomId: 2,
        teacherId: 2,
        patternType: 'weekly',
        startTime: '19:30',
        endTime: '20:30',
        daysOfWeek: [1, 3, 5],
        startDate: '2025-12-25', // 3 tuần sau (21 ngày) - VIP early access
        endDate: '2026-03-31',
        createdBy: 'manager',
        createdAt: '2025-11-29T10:00:00'
    },
    {
        id: 6,
        name: 'Zumba Dance Party',
        description: 'Lớp Zumba vui nhộn với âm nhạc Latin sôi động',
        difficulty: 'Beginner',
        maxStudents: 25,
        status: 'ready_for_students',
        roomId: 3,
        teacherId: 2,
        patternType: 'weekly',
        startTime: '17:00',
        endTime: '18:00',
        daysOfWeek: [2, 4],
        startDate: '2025-12-11', // 1 tuần sau (7 ngày) - All can register
        endDate: '2026-03-31',
        createdBy: 'manager',
        createdAt: '2025-11-29T11:00:00'
    }
];

// Teacher applications
export const teacherApplications = [
    {
        id: 1,
        classId: 3,
        teacherId: 3,
        status: 'pending', // pending, approved, rejected
        appliedAt: '2025-11-29T10:00:00',
        reviewedAt: null,
        reviewedBy: null,
        rejectionReason: null
    },
    {
        id: 2,
        classId: 3, // Same class - Strength Training
        teacherId: 1, // Different teacher - Nguyễn Văn An
        status: 'pending',
        appliedAt: '2025-11-29T14:30:00',
        reviewedAt: null,
        reviewedBy: null,
        rejectionReason: null
    }
];

// Sessions - generated from class patterns
export const sessions = [];

// Student registrations
export const studentRegistrations = [
    {
        id: 1,
        classId: 1,
        studentId: 1,
        registeredAt: '2025-11-22T08:00:00',
        status: 'active'
    },
    {
        id: 2,
        classId: 1,
        studentId: 2,
        registeredAt: '2025-11-23T10:30:00',
        status: 'active'
    }
];

// ==================== HELPER FUNCTIONS ====================

let nextClassId = 4;
let nextSessionId = 1;
let nextApplicationId = 3; // Updated since we have 2 applications now
let nextRegistrationId = 3;

// Generate sessions from class pattern
export function generateSessions(classData) {
    const sessions = [];
    const start = new Date(classData.startDate);
    const end = new Date(classData.endDate);

    if (classData.patternType === 'no_repeat') {
        // Multiple sessions from selected dates
        if (classData.selectedDates && classData.selectedDates.length > 0) {
            classData.selectedDates.forEach(date => {
                sessions.push({
                    id: nextSessionId++,
                    classId: classData.id,
                    date: date,
                    startTime: classData.startTime,
                    endTime: classData.endTime,
                    roomId: classData.roomId,
                    teacherId: classData.teacherId,
                    status: 'scheduled'
                });
            });
        } else {
            // Fallback to single session if no selectedDates
            sessions.push({
                id: nextSessionId++,
                classId: classData.id,
                date: classData.startDate,
                startTime: classData.startTime,
                endTime: classData.endTime,
                roomId: classData.roomId,
                teacherId: classData.teacherId,
                status: 'scheduled'
            });
        }
    } else if (classData.patternType === 'weekly') {
        // Weekly pattern
        let currentDate = new Date(start);
        while (currentDate <= end) {
            const dayOfWeek = currentDate.getDay();
            if (classData.daysOfWeek.includes(dayOfWeek)) {
                sessions.push({
                    id: nextSessionId++,
                    classId: classData.id,
                    date: currentDate.toISOString().split('T')[0],
                    startTime: classData.startTime,
                    endTime: classData.endTime,
                    roomId: classData.roomId,
                    teacherId: classData.teacherId,
                    status: 'scheduled'
                });
            }
            currentDate.setDate(currentDate.getDate() + 1);
        }
    } else if (classData.patternType === 'monthly') {
        // Monthly pattern - repeat on selected days of week each month
        let currentDate = new Date(start);
        while (currentDate <= end) {
            const dayOfWeek = currentDate.getDay();
            if (classData.daysOfWeek.includes(dayOfWeek)) {
                sessions.push({
                    id: nextSessionId++,
                    classId: classData.id,
                    date: currentDate.toISOString().split('T')[0],
                    startTime: classData.startTime,
                    endTime: classData.endTime,
                    roomId: classData.roomId,
                    teacherId: classData.teacherId,
                    status: 'scheduled'
                });
            }
            currentDate.setDate(currentDate.getDate() + 1);
        }
    } else if (classData.patternType === 'custom_range') {
        // Custom range - generate based on daysOfWeek within range
        let currentDate = new Date(start);
        while (currentDate <= end) {
            const dayOfWeek = currentDate.getDay();
            if (classData.daysOfWeek.includes(dayOfWeek)) {
                sessions.push({
                    id: nextSessionId++,
                    classId: classData.id,
                    date: currentDate.toISOString().split('T')[0],
                    startTime: classData.startTime,
                    endTime: classData.endTime,
                    roomId: classData.roomId,
                    teacherId: classData.teacherId,
                    status: 'scheduled'
                });
            }
            currentDate.setDate(currentDate.getDate() + 1);
        }
    }

    return sessions;
}

// Check room conflicts
export function checkRoomConflicts(roomId, newSessions, excludeClassId = null) {
    const conflicts = [];

    for (const newSession of newSessions) {
        // Find all sessions in the same room on the same date
        const existingSessions = sessions.filter(s =>
            s.roomId === roomId &&
            s.date === newSession.date &&
            (excludeClassId === null || s.classId !== excludeClassId)
        );

        for (const existing of existingSessions) {
            // Check time overlap
            if (timeOverlap(newSession.startTime, newSession.endTime, existing.startTime, existing.endTime)) {
                const conflictClass = classes.find(c => c.id === existing.classId);
                conflicts.push({
                    date: newSession.date,
                    time: `${existing.startTime} - ${existing.endTime}`,
                    className: conflictClass?.name || 'Unknown Class',
                    classId: existing.classId
                });
            }
        }
    }

    return conflicts;
}

// Check teacher schedule conflicts
export function checkTeacherConflicts(teacherId, newSessions, excludeClassId = null) {
    const conflicts = [];

    for (const newSession of newSessions) {
        // Find all sessions with the same teacher on the same date
        const existingSessions = sessions.filter(s =>
            s.teacherId === teacherId &&
            s.date === newSession.date &&
            (excludeClassId === null || s.classId !== excludeClassId)
        );

        for (const existing of existingSessions) {
            // Check time overlap
            if (timeOverlap(newSession.startTime, newSession.endTime, existing.startTime, existing.endTime)) {
                const conflictClass = classes.find(c => c.id === existing.classId);
                conflicts.push({
                    date: newSession.date,
                    time: `${existing.startTime} - ${existing.endTime}`,
                    className: conflictClass?.name || 'Unknown Class',
                    classId: existing.classId
                });
            }
        }
    }

    return conflicts;
}

// Helper: Check if two time ranges overlap
function timeOverlap(start1, end1, start2, end2) {
    return start1 < end2 && end1 > start2;
}

// CRUD Operations
export const mockApi = {
    // Classes
    getClasses: () => Promise.resolve([...classes]),
    getClass: (id) => Promise.resolve(classes.find(c => c.id === id)),
    createClass: (classData) => {
        const newClass = {
            ...classData,
            id: nextClassId++,
            createdAt: new Date().toISOString()
        };
        classes.push(newClass);

        // Generate sessions
        const newSessions = generateSessions(newClass);
        sessions.push(...newSessions);

        return Promise.resolve(newClass);
    },
    updateClass: (id, updates) => {
        const index = classes.findIndex(c => c.id === id);
        if (index !== -1) {
            classes[index] = { ...classes[index], ...updates };
            return Promise.resolve(classes[index]);
        }
        return Promise.reject(new Error('Class not found'));
    },
    deleteClass: (id) => {
        const index = classes.findIndex(c => c.id === id);
        if (index !== -1) {
            classes.splice(index, 1);
            // Remove associated sessions
            const sessionIndices = sessions.filter(s => s.classId === id);
            sessionIndices.forEach(s => {
                const idx = sessions.indexOf(s);
                sessions.splice(idx, 1);
            });
            return Promise.resolve(true);
        }
        return Promise.reject(new Error('Class not found'));
    },

    // Teacher Applications
    applyToTeach: (classId, teacherId) => {
        const application = {
            id: nextApplicationId++,
            classId,
            teacherId,
            status: 'pending',
            appliedAt: new Date().toISOString(),
            reviewedAt: null,
            reviewedBy: null,
            rejectionReason: null
        };
        teacherApplications.push(application);

        // Update class status
        const classIndex = classes.findIndex(c => c.id === classId);
        if (classIndex !== -1) {
            classes[classIndex].status = 'waiting_approval';
            classes[classIndex].pendingTeacherId = teacherId;
        }

        return Promise.resolve(application);
    },

    approveTeacher: (applicationId, managerId) => {
        const app = teacherApplications.find(a => a.id === applicationId);
        if (!app) return Promise.reject(new Error('Application not found'));

        app.status = 'approved';
        app.reviewedAt = new Date().toISOString();
        app.reviewedBy = managerId;

        // Update class
        const classIndex = classes.findIndex(c => c.id === app.classId);
        if (classIndex !== -1) {
            classes[classIndex].teacherId = app.teacherId;
            classes[classIndex].status = 'ready_for_students';
            classes[classIndex].pendingTeacherId = null;

            // Update all sessions with teacher
            sessions.forEach(s => {
                if (s.classId === app.classId) {
                    s.teacherId = app.teacherId;
                }
            });
        }

        return Promise.resolve(app);
    },

    rejectTeacher: (applicationId, managerId, reason) => {
        const app = teacherApplications.find(a => a.id === applicationId);
        if (!app) return Promise.reject(new Error('Application not found'));

        app.status = 'rejected';
        app.reviewedAt = new Date().toISOString();
        app.reviewedBy = managerId;
        app.rejectionReason = reason;

        // Update class back to pending_teacher
        const classIndex = classes.findIndex(c => c.id === app.classId);
        if (classIndex !== -1) {
            classes[classIndex].status = 'pending_teacher';
            classes[classIndex].pendingTeacherId = null;
        }

        return Promise.resolve(app);
    },

    getApplications: () => Promise.resolve([...teacherApplications]),

    // Student Registrations
    registerStudent: (classId, studentId) => {
        const classData = classes.find(c => c.id === classId);
        if (!classData) return Promise.reject(new Error('Class not found'));

        // Check capacity
        const currentCount = studentRegistrations.filter(r => r.classId === classId && r.status === 'active').length;
        if (currentCount >= classData.maxStudents) {
            return Promise.reject(new Error('Class is full'));
        }

        // Check if already registered
        const existing = studentRegistrations.find(r =>
            r.classId === classId && r.studentId === studentId && r.status === 'active'
        );
        if (existing) {
            return Promise.reject(new Error('Already registered'));
        }

        const registration = {
            id: nextRegistrationId++,
            classId,
            studentId,
            registeredAt: new Date().toISOString(),
            status: 'active'
        };
        studentRegistrations.push(registration);

        return Promise.resolve(registration);
    },

    getStudentRegistrations: (studentId) => {
        return Promise.resolve(studentRegistrations.filter(r => r.studentId === studentId));
    },

    cancelRegistration: (classId, studentId) => {
        const registration = studentRegistrations.find(r =>
            r.classId === classId && r.studentId === studentId && r.status === 'active'
        );

        if (!registration) {
            return Promise.reject(new Error('Registration not found'));
        }

        registration.status = 'cancelled';
        registration.cancelledAt = new Date().toISOString();

        return Promise.resolve(registration);
    },

    // Rooms & Teachers
    getRooms: () => Promise.resolve([...rooms]),
    getTeachers: () => Promise.resolve([...teachers]),
    getStudents: () => Promise.resolve([...students]),

    // Sessions
    getSessions: (classId) => {
        return Promise.resolve(sessions.filter(s => s.classId === classId));
    },

    getAllSessions: () => Promise.resolve([...sessions]),

    // Conflict checking
    checkRoomConflicts,
    checkTeacherConflicts
};

// Initialize sessions for existing classes
classes.forEach(classData => {
    const classSessions = generateSessions(classData);
    sessions.push(...classSessions);
});

export default mockApi;
