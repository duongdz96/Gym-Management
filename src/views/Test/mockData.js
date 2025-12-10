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
    },
    {
        id: 7,
        name: 'Morning Yoga Flow',
        description: 'Lớp Yoga buổi sáng giúp khởi động ngày mới tràn đầy năng lượng',
        difficulty: 'Beginner',
        maxStudents: 15,
        status: 'ready_for_students',
        roomId: 1,
        teacherId: 1, // Teacher ID 1
        patternType: 'weekly',
        startTime: '08:00',
        endTime: '09:30',
        daysOfWeek: [0, 6], // Sunday (0) and Saturday (6) - Hôm nay là thứ 7
        startDate: '2025-12-06', // Bắt đầu hôm nay
        endDate: '2026-03-31',
        createdBy: 'manager',
        createdAt: '2025-12-01T10:00:00'
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
    },
    {
        id: 3,
        classId: 7,
        studentId: 1,
        registeredAt: '2025-11-22T08:00:00',
        status: 'active'
    },
];

// Student Attendance Records
export const studentAttendances = [
    // Will be populated as sessions occur and attendance is marked
    // Example structure:
    // {
    //     id: 1,
    //     sessionId: 1,
    //     studentId: 1,
    //     status: 'PRESENT', // REGISTERED, PRESENT, ABSENT, EXCUSED, LATE
    //     checkedInAt: '2025-12-03T07:05:00',
    //     checkedInBy: 1, // Teacher ID
    //     notes: null
    // }
];

// ==================== HELPER FUNCTIONS ====================

let nextClassId = 8;
let nextSessionId = 1;
let nextApplicationId = 3; // Updated since we have 2 applications now
let nextRegistrationId = 3;
let nextAttendanceId = 1;

// Helper to create session object with all fields
function createSessionObject(classData, date) {
    const sessionDate = new Date(date);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    sessionDate.setHours(0, 0, 0, 0);

    // Determine initial status based on date
    let status = 'SCHEDULED';
    if (sessionDate < today) {
        status = 'COMPLETED'; // Past sessions are auto-completed
    }

    return {
        id: nextSessionId++,
        classId: classData.id,
        date: typeof date === 'string' ? date : date.toISOString().split('T')[0],
        startTime: classData.startTime,
        endTime: classData.endTime,
        roomId: classData.roomId,
        teacherId: classData.teacherId,

        // Session Status
        status: status, // SCHEDULED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, TEACHER_ABSENT

        // Cancellation
        cancellationReason: null, // Reason for cancellation
        cancelledBy: null, // Manager ID who cancelled
        cancelledAt: null,

        // Manager Note
        managerNote: null, // Note from manager (e.g., "Teacher B will substitute")
        managerNoteBy: null, // Manager ID who added note
        managerNoteAt: null,

        // Teacher Management
        teacherConfirmed: false,
        teacherConfirmedAt: null,
        teacherLeaveRequest: false,
        teacherLeaveReason: null,
        teacherLeaveRequestedAt: null,
        teacherLeaveApprovedBy: null,
        teacherLeaveApprovedAt: null,

        // Metadata
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
    };
}

// Generate sessions from class pattern
export function generateSessions(classData) {
    const sessions = [];
    const start = new Date(classData.startDate);
    const end = new Date(classData.endDate);

    if (classData.patternType === 'no_repeat') {
        // Multiple sessions from selected dates
        if (classData.selectedDates && classData.selectedDates.length > 0) {
            classData.selectedDates.forEach(date => {
                sessions.push(createSessionObject(classData, date));
            });
        } else {
            // Fallback to single session if no selectedDates
            sessions.push(createSessionObject(classData, classData.startDate));
        }
    } else if (classData.patternType === 'weekly') {
        // Weekly pattern
        let currentDate = new Date(start);
        while (currentDate <= end) {
            const dayOfWeek = currentDate.getDay();
            if (classData.daysOfWeek.includes(dayOfWeek)) {
                sessions.push(createSessionObject(classData, currentDate));
            }
            currentDate.setDate(currentDate.getDate() + 1);
        }
    } else if (classData.patternType === 'monthly') {
        // Monthly pattern - repeat on selected days of week each month
        let currentDate = new Date(start);
        while (currentDate <= end) {
            const dayOfWeek = currentDate.getDay();
            if (classData.daysOfWeek.includes(dayOfWeek)) {
                sessions.push(createSessionObject(classData, currentDate));
            }
            currentDate.setDate(currentDate.getDate() + 1);
        }
    } else if (classData.patternType === 'custom_range') {
        // Custom range - generate based on daysOfWeek within range
        let currentDate = new Date(start);
        while (currentDate <= end) {
            const dayOfWeek = currentDate.getDay();
            if (classData.daysOfWeek.includes(dayOfWeek)) {
                sessions.push(createSessionObject(classData, currentDate));
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

// ==================== CRUD OPERATIONS ====================
export const mockApi = {
    // Classes
    getClasses: () => Promise.resolve([...classes]),
    getClass: (id) => Promise.resolve(classes.find(c => c.id === id)),
    getSessions: () => {
        console.log('getSessions called, sessions.length:', sessions.length);
        console.log('sessions array:', sessions);
        return Promise.resolve([...sessions]);
    },
    getRooms: () => Promise.resolve([...rooms]),
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
    checkTeacherConflicts,

    // Schedules
    getStudentSchedule: (studentId, startDate, endDate) => {
        const start = new Date(startDate);
        const end = new Date(endDate);

        // Get student's active registrations
        const myRegistrations = studentRegistrations.filter(r =>
            r.studentId === studentId && r.status === 'active'
        );
        const myClassIds = myRegistrations.map(r => r.classId);

        // Filter sessions
        const mySessions = sessions.filter(s => {
            const date = new Date(s.date);
            return myClassIds.includes(s.classId) &&
                date >= start &&
                date <= end;
        });

        // Enrich with details
        return Promise.resolve(mySessions.map(s => {
            const cls = classes.find(c => c.id === s.classId);
            const room = rooms.find(r => r.id === s.roomId);
            const teacher = teachers.find(t => t.id === s.teacherId);
            return {
                ...s,
                className: cls ? cls.name : 'Unknown Class',
                roomName: room ? room.name : 'Unknown Room',
                teacherName: teacher ? teacher.name : 'Unknown Teacher'
            };
        }));
    },

    getTeacherSchedule: (teacherId, startDate, endDate) => {
        const start = new Date(startDate);
        const end = new Date(endDate);

        // Get teacher's classes
        const myClasses = classes.filter(c => c.teacherId === teacherId);
        const myClassIds = myClasses.map(c => c.id);

        // Filter sessions
        const mySessions = sessions.filter(s => {
            const date = new Date(s.date);
            return myClassIds.includes(s.classId) &&
                date >= start &&
                date <= end;
        });

        // Enrich with details
        return Promise.resolve(mySessions.map(s => {
            const cls = classes.find(c => c.id === s.classId);
            const room = rooms.find(r => r.id === s.roomId);
            const teacher = teachers.find(t => t.id === teacherId); // Self
            return {
                ...s,
                className: cls ? cls.name : 'Unknown Class',
                roomName: room ? room.name : 'Unknown Room',
                teacherName: teacher ? teacher.name : 'Me'
            };
        }));
    },

    // ==================== SESSION MANAGEMENT ====================

    // Get session by ID
    getSession: (sessionId) => {
        const session = sessions.find(s => s.id === sessionId);
        if (!session) return Promise.reject(new Error('Session not found'));
        return Promise.resolve(session);
    },

    // Get sessions for a specific date range
    getSessionsByDateRange: (startDate, endDate) => {
        const start = new Date(startDate);
        const end = new Date(endDate);
        return Promise.resolve(sessions.filter(s => {
            const date = new Date(s.date);
            return date >= start && date <= end;
        }));
    },

    // Teacher confirms attendance for a session
    confirmTeacherAttendance: (sessionId, teacherId) => {
        const session = sessions.find(s => s.id === sessionId);
        if (!session) return Promise.reject(new Error('Session not found'));
        if (session.teacherId !== teacherId) {
            return Promise.reject(new Error('Not authorized'));
        }

        session.teacherConfirmed = true;
        session.teacherConfirmedAt = new Date().toISOString();
        session.status = 'CONFIRMED';
        session.updatedAt = new Date().toISOString();

        return Promise.resolve(session);
    },

    // Teacher requests leave for a session
    requestTeacherLeave: (sessionId, teacherId, reason) => {
        const session = sessions.find(s => s.id === sessionId);
        if (!session) return Promise.reject(new Error('Session not found'));
        if (session.teacherId !== teacherId) {
            return Promise.reject(new Error('Not authorized'));
        }

        session.teacherLeaveRequest = true;
        session.teacherLeaveReason = reason;
        session.teacherLeaveRequestedAt = new Date().toISOString();
        session.updatedAt = new Date().toISOString();

        return Promise.resolve(session);
    },

    // Manager approves/rejects teacher leave
    approveTeacherLeave: (sessionId, managerId, approved) => {
        const session = sessions.find(s => s.id === sessionId);
        if (!session) return Promise.reject(new Error('Session not found'));
        if (!session.teacherLeaveRequest) {
            return Promise.reject(new Error('No leave request found'));
        }

        if (approved) {
            // Just approve the leave request, don't auto-cancel
            // Manager will decide whether to cancel or find substitute
            session.teacherLeaveApprovedBy = managerId;
            session.teacherLeaveApprovedAt = new Date().toISOString();
        } else {
            // Reject leave request
            session.teacherLeaveRequest = false;
            session.teacherLeaveReason = null;
            session.teacherLeaveRequestedAt = null;
        }
        session.updatedAt = new Date().toISOString();

        return Promise.resolve(session);
    },

    // Manager cancels a session with reason
    cancelSession: (sessionId, managerId, reason) => {
        const session = sessions.find(s => s.id === sessionId);
        if (!session) return Promise.reject(new Error('Session not found'));

        session.status = 'CANCELLED';
        session.cancellationReason = reason;
        session.cancelledBy = managerId;
        session.cancelledAt = new Date().toISOString();
        session.updatedAt = new Date().toISOString();

        return Promise.resolve(session);
    },

    // Manager adds note to session
    addSessionNote: (sessionId, managerId, note) => {
        const session = sessions.find(s => s.id === sessionId);
        if (!session) return Promise.reject(new Error('Session not found'));

        session.managerNote = note;
        session.managerNoteBy = managerId;
        session.managerNoteAt = new Date().toISOString();
        session.updatedAt = new Date().toISOString();

        return Promise.resolve(session);
    },

    // Update session status
    updateSessionStatus: (sessionId, status) => {
        const session = sessions.find(s => s.id === sessionId);
        if (!session) return Promise.reject(new Error('Session not found'));

        session.status = status;
        session.updatedAt = new Date().toISOString();

        return Promise.resolve(session);
    },

    // ==================== ATTENDANCE TRACKING ====================

    // Mark student attendance
    markStudentAttendance: (sessionId, studentId, status, checkedInBy, notes = null) => {
        // Check if attendance record already exists
        let attendance = studentAttendances.find(a =>
            a.sessionId === sessionId && a.studentId === studentId
        );

        if (attendance) {
            // Update existing
            attendance.status = status;
            attendance.checkedInAt = new Date().toISOString();
            attendance.checkedInBy = checkedInBy;
            attendance.notes = notes;
        } else {
            // Create new
            attendance = {
                id: nextAttendanceId++,
                sessionId,
                studentId,
                status,
                checkedInAt: new Date().toISOString(),
                checkedInBy,
                notes
            };
            studentAttendances.push(attendance);
        }

        return Promise.resolve(attendance);
    },

    // Get all attendance for a session
    getSessionAttendance: (sessionId) => {
        const attendances = studentAttendances.filter(a => a.sessionId === sessionId);

        // Enrich with student info
        return Promise.resolve(attendances.map(a => {
            const student = students.find(s => s.id === a.studentId);
            return {
                ...a,
                studentName: student ? student.name : 'Unknown',
                studentEmail: student ? student.email : ''
            };
        }));
    },

    // Get student's attendance history
    getStudentAttendanceHistory: (studentId, startDate, endDate) => {
        const start = new Date(startDate);
        const end = new Date(endDate);

        const attendances = studentAttendances.filter(a => {
            if (a.studentId !== studentId) return false;
            const session = sessions.find(s => s.id === a.sessionId);
            if (!session) return false;
            const date = new Date(session.date);
            return date >= start && date <= end;
        });

        // Enrich with session and class info
        return Promise.resolve(attendances.map(a => {
            const session = sessions.find(s => s.id === a.sessionId);
            const cls = session ? classes.find(c => c.id === session.classId) : null;
            return {
                ...a,
                sessionDate: session ? session.date : null,
                sessionTime: session ? `${session.startTime} - ${session.endTime}` : null,
                className: cls ? cls.name : 'Unknown'
            };
        }));
    },

    // Initialize attendance records for registered students when session is created
    initializeSessionAttendance: (sessionId) => {
        const session = sessions.find(s => s.id === sessionId);
        if (!session) return Promise.reject(new Error('Session not found'));

        // Find all students registered for this class
        const registeredStudents = studentRegistrations.filter(r =>
            r.classId === session.classId && r.status === 'active'
        );

        // Create REGISTERED attendance records for each student
        registeredStudents.forEach(reg => {
            const existing = studentAttendances.find(a =>
                a.sessionId === sessionId && a.studentId === reg.studentId
            );

            if (!existing) {
                studentAttendances.push({
                    id: nextAttendanceId++,
                    sessionId,
                    studentId: reg.studentId,
                    status: 'REGISTERED',
                    checkedInAt: null,
                    checkedInBy: null,
                    notes: null
                });
            }
        });

        return Promise.resolve(true);
    }
};

export default mockApi;
