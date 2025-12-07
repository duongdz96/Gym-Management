// Mock API for Gym Class Management System
// Based on mockData2.js structure (matching backend)
// Can be switched to real API by setting USE_REAL_API = true
import {
  users,
  teachers,
  members,
  rooms,
  fitnessClasses,
  schedulePatterns,
  classSchedules,
  classRegistrations,
  memberRegistrations
} from './mockData2.js';
import apiService from './apiService.js';

// Switch between mock and real API
const USE_REAL_API = false; // Set to true to use real backend API

// ==================== DATA STORAGE ====================
// These will be mutable copies for CRUD operations
let fitnessClassesData = [...fitnessClasses];
let schedulePatternsData = [...schedulePatterns];
let classSchedulesData = [...classSchedules];
let classRegistrationsData = [...classRegistrations];
let memberRegistrationsData = [...memberRegistrations];

// ID counters
let nextFitnessClassId = Math.max(...fitnessClasses.map(fc => fc.id), 0) + 1;
let nextSchedulePatternId = Math.max(...schedulePatterns.map(sp => sp.id), 0) + 1;
let nextClassScheduleId = Math.max(...classSchedules.map(cs => cs.id), 0) + 1;
let nextClassRegistrationId = Math.max(...classRegistrations.map(cr => cr.id), 0) + 1;
let nextMemberRegistrationId = Math.max(...memberRegistrations.map(mr => mr.id), 0) + 1;

// ==================== HELPER FUNCTIONS ====================

// Get teacher by ID (combine user + teacher data)
function getTeacher(teacherId) {
  const user = users.find(u => u.id === teacherId && u.role === 'TEACHER');
  const teacher = teachers.find(t => t.id === teacherId);
  if (!user || !teacher) return null;
  return {
    id: teacherId,
    name: user.fullName,
    email: user.email,
    specialties: teacher.specialize ? teacher.specialize.split(',').map(s => s.trim()) : [],
    bio: `${teacher.position} - ${teacher.specialize}`,
    avatar: '👨‍🏫'
  };
}

// Get member by ID (combine user + member data)
function getMember(memberId) {
  const user = users.find(u => u.id === memberId && u.role === 'MEMBER');
  const member = members.find(m => m.id === memberId);
  if (!user || !member) return null;
  return {
    id: memberId,
    name: user.fullName,
    email: user.email,
    membershipTier: member.membership,
    joinDate: member.joinDate
  };
}

// Get fitness class with teacher info
function getFitnessClassWithTeacher(fitnessClassId) {
  const fc = fitnessClassesData.find(f => f.id === fitnessClassId);
  if (!fc) return null;
  
  const registration = classRegistrationsData.find(cr => cr.fitnessClassId === fitnessClassId);
  const teacher = registration ? getTeacher(registration.staffId) : null;
  
  // Get schedule pattern for this class
  const pattern = schedulePatternsData.find(sp => {
    // Find pattern that matches this class (we'll link them via classSchedules)
    const schedule = classSchedulesData.find(cs => cs.fitnessClassId === fitnessClassId);
    return schedule && schedule.schedulePatternId === sp.id;
  });
  
  // Get room from first schedule
  const firstSchedule = classSchedulesData.find(cs => cs.fitnessClassId === fitnessClassId);
  const room = firstSchedule ? rooms.find(r => r.id === firstSchedule.roomId) : null;
  
  // Get all schedules for this class
  const schedules = classSchedulesData.filter(cs => cs.fitnessClassId === fitnessClassId);
  
  // Determine status based on teacher and schedules
  let status = 'pending_teacher';
  if (registration) {
    status = 'ready_for_students';
  } else {
    // Check if there are pending applications
    status = 'pending_teacher';
  }
  
  // Calculate max students from capacity of schedules
  const maxStudents = schedules.length > 0 ? Math.max(...schedules.map(s => s.capacity)) : 20;
  
  // Get time and days from pattern
  const startTime = pattern ? pattern.timeStart.substring(0, 5) : '07:00';
  const endTime = pattern ? pattern.timeEnd.substring(0, 5) : '08:30';
  const daysOfWeek = pattern ? pattern.daysOfWeek.split(',').map(d => {
    const dayMap = { 'MONDAY': 1, 'TUESDAY': 2, 'WEDNESDAY': 3, 'THURSDAY': 4, 'FRIDAY': 5, 'SATURDAY': 6, 'SUNDAY': 0 };
    return dayMap[d.trim()];
  }).filter(d => d !== undefined) : [];
  const startDate = pattern ? pattern.classStartDate : '';
  const endDate = pattern ? pattern.classEndDate : '';
  
  return {
    id: fc.id,
    name: fc.name,
    description: fc.description,
    difficulty: fc.difficultyLevel,
    maxStudents: maxStudents,
    status: status,
    roomId: room ? room.id : null,
    teacherId: teacher ? teacher.id : null,
    patternType: 'weekly', // Default to weekly
    startTime: startTime,
    endTime: endTime,
    daysOfWeek: daysOfWeek,
    startDate: startDate,
    endDate: endDate,
    createdBy: 'manager',
    createdAt: new Date().toISOString()
  };
}

// Convert classSchedule to session format
function classScheduleToSession(cs) {
  const fc = fitnessClassesData.find(f => f.id === cs.fitnessClassId);
  const room = rooms.find(r => r.id === cs.roomId);
  const registration = classRegistrationsData.find(cr => cr.fitnessClassId === cs.fitnessClassId);
  const teacher = registration ? getTeacher(registration.staffId) : null;
  
  const startDate = new Date(cs.startTime);
  const dateStr = startDate.toISOString().split('T')[0];
  const startTimeStr = startDate.toTimeString().substring(0, 5);
  const endDate = new Date(cs.endTime);
  const endTimeStr = endDate.toTimeString().substring(0, 5);
  
  return {
    id: cs.id,
    classId: cs.fitnessClassId,
    date: dateStr,
    startTime: startTimeStr,
    endTime: endTimeStr,
    roomId: cs.roomId,
    room: room ? { id: room.id, name: room.name, note: room.note, location: room.location } : null,
    teacherId: teacher ? teacher.id : null,
    status: cs.status,
    capacity: cs.capacity,
    className: fc ? fc.name : 'Unknown',
    roomName: room ? room.name : 'Unknown',
    teacherName: teacher ? teacher.name : 'Unknown'
  };
}

// Generate sessions from fitness class and pattern
function generateSessionsFromPattern(fitnessClassId, pattern, roomId) {
  const sessions = [];
  const start = new Date(pattern.classStartDate);
  const end = new Date(pattern.classEndDate);
  const daysOfWeek = pattern.daysOfWeek.split(',').map(d => {
    const dayMap = { 'MONDAY': 1, 'TUESDAY': 2, 'WEDNESDAY': 3, 'THURSDAY': 4, 'FRIDAY': 5, 'SATURDAY': 6, 'SUNDAY': 0 };
    return dayMap[d.trim()];
  }).filter(d => d !== undefined);
  
  const [startHour, startMin] = pattern.timeStart.split(':').map(Number);
  const [endHour, endMin] = pattern.timeEnd.split(':').map(Number);
  
  let currentDate = new Date(start);
  while (currentDate <= end) {
    const dayOfWeek = currentDate.getDay();
    if (daysOfWeek.includes(dayOfWeek)) {
      const startTime = new Date(currentDate);
      startTime.setHours(startHour, startMin, 0, 0);
      const endTime = new Date(currentDate);
      endTime.setHours(endHour, endMin, 0, 0);
      
      sessions.push({
        id: nextClassScheduleId++,
        startTime: startTime.toISOString(),
        endTime: endTime.toISOString(),
        capacity: 20, // Default capacity
        status: 'OPEN',
        fitnessClassId: fitnessClassId,
        schedulePatternId: pattern.id,
        roomId: roomId
      });
    }
    currentDate.setDate(currentDate.getDate() + 1);
  }
  
  return sessions;
}

// Check teacher conflicts
function checkTeacherConflicts(teacherId, newSessions, excludeFitnessClassId = null) {
  const conflicts = [];
  
  // Get teacher's registered fitness classes
  const teacherRegistrations = classRegistrationsData.filter(cr => cr.staffId === teacherId);
  const teacherFitnessClassIds = teacherRegistrations.map(cr => cr.fitnessClassId);
  
  // Get all schedules for teacher's classes
  const teacherSchedules = classSchedulesData.filter(cs => 
    teacherFitnessClassIds.includes(cs.fitnessClassId) &&
    (excludeFitnessClassId === null || cs.fitnessClassId !== excludeFitnessClassId)
  );
  
  for (const newSession of newSessions) {
    // Handle both ISO string and object format
    const newStart = typeof newSession.startTime === 'string' 
      ? new Date(newSession.startTime) 
      : new Date(newSession.startTime);
    const newEnd = typeof newSession.endTime === 'string'
      ? new Date(newSession.endTime)
      : new Date(newSession.endTime);
    
    for (const existing of teacherSchedules) {
      const existingStart = new Date(existing.startTime);
      const existingEnd = new Date(existing.endTime);
      
      // Check if same date and time overlap
      const newDate = newStart.toISOString().split('T')[0];
      const existingDate = existingStart.toISOString().split('T')[0];
      
      if (newDate === existingDate) {
        // Check time overlap
        if (newStart < existingEnd && newEnd > existingStart) {
          const fc = fitnessClassesData.find(f => f.id === existing.fitnessClassId);
          conflicts.push({
            date: newDate,
            time: `${existingStart.toTimeString().substring(0, 5)} - ${existingEnd.toTimeString().substring(0, 5)}`,
            className: fc ? fc.name : 'Unknown Class',
            classId: existing.fitnessClassId
          });
        }
      }
    }
  }
  
  return conflicts;
}

// Check room conflicts
function checkRoomConflicts(roomId, newSessions, excludeFitnessClassId = null) {
  const conflicts = [];
  
  for (const newSession of newSessions) {
    // Handle both ISO string and object format
    const newStart = typeof newSession.startTime === 'string' 
      ? new Date(newSession.startTime) 
      : new Date(newSession.startTime);
    const newEnd = typeof newSession.endTime === 'string'
      ? new Date(newSession.endTime)
      : new Date(newSession.endTime);
    const newDate = newStart.toISOString().split('T')[0];
    
    // Find all schedules in the same room on the same date
    const existingSchedules = classSchedulesData.filter(cs =>
      cs.roomId === roomId &&
      (excludeFitnessClassId === null || cs.fitnessClassId !== excludeFitnessClassId)
    );
    
    for (const existing of existingSchedules) {
      const existingStart = new Date(existing.startTime);
      const existingEnd = new Date(existing.endTime);
      const existingDate = existingStart.toISOString().split('T')[0];
      
      if (newDate === existingDate) {
        // Check time overlap
        if (newStart < existingEnd && newEnd > existingStart) {
          const fc = fitnessClassesData.find(f => f.id === existing.fitnessClassId);
          conflicts.push({
            date: newDate,
            time: `${existingStart.toTimeString().substring(0, 5)} - ${existingEnd.toTimeString().substring(0, 5)}`,
            className: fc ? fc.name : 'Unknown Class',
            classId: existing.fitnessClassId
          });
        }
      }
    }
  }
  
  return conflicts;
}

// ==================== MOCK API ====================

export const mockApi = {
  // Fitness Classes (mapped to old "classes")
  getClasses: async () => {
    return fitnessClassesData.map(fc => getFitnessClassWithTeacher(fc.id)).filter(c => c !== null);
  },
  
  getClass: async (id) => {
    return getFitnessClassWithTeacher(id);
  },
  
  createClass: async (classData) => {
    // Create fitness class
    const newFitnessClass = {
      id: nextFitnessClassId++,
      name: classData.name,
      description: classData.description,
      difficultyLevel: classData.difficulty,
      status: 'Active'
    };
    fitnessClassesData.push(newFitnessClass);
    
    // Create schedule pattern
    const daysOfWeekStr = classData.daysOfWeek.map(d => {
      const dayMap = { 0: 'SUNDAY', 1: 'MONDAY', 2: 'TUESDAY', 3: 'WEDNESDAY', 4: 'THURSDAY', 5: 'FRIDAY', 6: 'SATURDAY' };
      return dayMap[d];
    }).filter(d => d).join(',');
    
    const newPattern = {
      id: nextSchedulePatternId++,
      daysOfWeek: daysOfWeekStr,
      timeStart: `${classData.startTime}:00`,
      timeEnd: `${classData.endTime}:00`,
      classStartDate: classData.startDate,
      classEndDate: classData.endDate
    };
    schedulePatternsData.push(newPattern);
    
    // Generate class schedules
    const newSessions = generateSessionsFromPattern(newFitnessClass.id, newPattern, classData.roomId);
    classSchedulesData.push(...newSessions);
    
    return getFitnessClassWithTeacher(newFitnessClass.id);
  },
  
  updateClass: async (id, updates) => {
    const index = fitnessClassesData.findIndex(fc => fc.id === id);
    if (index !== -1) {
      fitnessClassesData[index] = { ...fitnessClassesData[index], ...updates };
      return getFitnessClassWithTeacher(id);
    }
    throw new Error('Class not found');
  },
  
  deleteClass: async (id) => {
    const index = fitnessClassesData.findIndex(fc => fc.id === id);
    if (index !== -1) {
      fitnessClassesData.splice(index, 1);
      // Remove associated schedules
      classSchedulesData = classSchedulesData.filter(cs => cs.fitnessClassId !== id);
      // Remove registrations
      classRegistrationsData = classRegistrationsData.filter(cr => cr.fitnessClassId !== id);
      return true;
    }
    throw new Error('Class not found');
  },
  
  // Teacher Applications (for applying to teach a fitness class)
  applyToTeach: async (fitnessClassId, teacherId) => {
    // Check if already applied
    const existing = classRegistrationsData.find(cr => 
      cr.fitnessClassId === fitnessClassId && cr.staffId === teacherId
    );
    if (existing) {
      throw new Error('Already applied to teach this class');
    }
    
    // Create pending registration (status will be managed separately)
    const registration = {
      id: nextClassRegistrationId++,
      staffId: teacherId,
      fitnessClassId: fitnessClassId,
      description: 'Pending approval'
    };
    classRegistrationsData.push(registration);
    
    return {
      id: registration.id,
      classId: fitnessClassId,
      teacherId: teacherId,
      status: 'pending',
      appliedAt: new Date().toISOString(),
      reviewedAt: null,
      reviewedBy: null,
      rejectionReason: null
    };
  },
  
  approveTeacher: async (applicationId, managerId) => {
    // Find registration by matching teacher and class
    // For now, we'll use a simple mapping
    const registration = classRegistrationsData.find(cr => cr.id === applicationId);
    if (!registration) {
      throw new Error('Application not found');
    }
    
    registration.description = 'Approved';
    
    return {
      id: applicationId,
      classId: registration.fitnessClassId,
      teacherId: registration.staffId,
      status: 'approved',
      appliedAt: new Date().toISOString(),
      reviewedAt: new Date().toISOString(),
      reviewedBy: managerId,
      rejectionReason: null
    };
  },
  
  rejectTeacher: async (applicationId, managerId, reason) => {
    const registration = classRegistrationsData.find(cr => cr.id === applicationId);
    if (!registration) {
      throw new Error('Application not found');
    }
    
    // Remove registration
    const index = classRegistrationsData.findIndex(cr => cr.id === applicationId);
    if (index !== -1) {
      classRegistrationsData.splice(index, 1);
    }
    
    return {
      id: applicationId,
      classId: registration.fitnessClassId,
      teacherId: registration.staffId,
      status: 'rejected',
      appliedAt: new Date().toISOString(),
      reviewedAt: new Date().toISOString(),
      reviewedBy: managerId,
      rejectionReason: reason
    };
  },
  
  getApplications: async () => {
    // Convert classRegistrations to application format
    return classRegistrationsData.map(cr => ({
      id: cr.id,
      classId: cr.fitnessClassId,
      teacherId: cr.staffId,
      status: cr.description === 'Approved' ? 'approved' : 'pending',
      appliedAt: new Date().toISOString(),
      reviewedAt: cr.description === 'Approved' ? new Date().toISOString() : null,
      reviewedBy: cr.description === 'Approved' ? 'manager' : null,
      rejectionReason: null
    }));
  },
  
  // Student/Member Registrations (for registering to specific class schedules)
  registerStudent: async (fitnessClassId, studentId) => {
    // Find an available class schedule for this fitness class
    const availableSchedules = classSchedulesData.filter(cs => 
      cs.fitnessClassId === fitnessClassId &&
      cs.status === 'OPEN'
    );
    
    if (availableSchedules.length === 0) {
      throw new Error('No available schedules for this class');
    }
    
    // Check if already registered to any schedule of this class
    const existing = memberRegistrationsData.find(mr => 
      availableSchedules.some(cs => cs.id === mr.classScheduleId) &&
      mr.memberId === studentId
    );
    if (existing) {
      throw new Error('Already registered to this class');
    }
    
    // Register to the first available schedule (in real app, user would choose)
    const schedule = availableSchedules[0];
    
    // Check capacity
    const currentCount = memberRegistrationsData.filter(mr => 
      mr.classScheduleId === schedule.id
    ).length;
    if (currentCount >= schedule.capacity) {
      throw new Error('Class is full');
    }
    
    const registration = {
      id: nextMemberRegistrationId++,
      followDate: new Date().toISOString(),
      classScheduleId: schedule.id,
      memberId: studentId
    };
    memberRegistrationsData.push(registration);
    
    return {
      id: registration.id,
      classId: fitnessClassId,
      studentId: studentId,
      registeredAt: registration.followDate,
      status: 'active'
    };
  },
  
  getStudentRegistrations: async (studentId) => {
    const registrations = memberRegistrationsData.filter(mr => mr.memberId === studentId);
    return registrations.map(mr => {
      const schedule = classSchedulesData.find(cs => cs.id === mr.classScheduleId);
      return {
        id: mr.id,
        classId: schedule ? schedule.fitnessClassId : null,
        studentId: studentId,
        registeredAt: mr.followDate,
        status: 'active'
      };
    });
  },
  
  cancelRegistration: async (fitnessClassId, studentId) => {
    const schedules = classSchedulesData.filter(cs => cs.fitnessClassId === fitnessClassId);
    const registration = memberRegistrationsData.find(mr => 
      schedules.some(cs => cs.id === mr.classScheduleId) &&
      mr.memberId === studentId
    );
    
    if (!registration) {
      throw new Error('Registration not found');
    }
    
    const index = memberRegistrationsData.findIndex(mr => mr.id === registration.id);
    if (index !== -1) {
      memberRegistrationsData.splice(index, 1);
    }
    
    return registration;
  },
  
  registerBulkSchedules: async (studentId, scheduleIds) => {
    // Register member to multiple class schedules
    const results = [];
    
    for (const scheduleId of scheduleIds) {
      const schedule = classSchedulesData.find(cs => cs.id === scheduleId);
      if (!schedule) {
        throw new Error(`Schedule ${scheduleId} not found`);
      }
      
      // Check if already registered
      const existing = memberRegistrationsData.find(mr => 
        mr.classScheduleId === scheduleId && mr.memberId === studentId
      );
      if (existing) {
        continue; // Skip if already registered
      }
      
      // Check capacity
      const currentCount = memberRegistrationsData.filter(mr => 
        mr.classScheduleId === scheduleId
      ).length;
      if (currentCount >= schedule.capacity) {
        throw new Error(`Schedule ${scheduleId} is full`);
      }
      
      // Check status
      if (schedule.status !== 'OPEN') {
        throw new Error(`Schedule ${scheduleId} is not open for registration`);
      }
      
      const registration = {
        id: nextMemberRegistrationId++,
        followDate: new Date().toISOString(),
        classScheduleId: scheduleId,
        memberId: studentId
      };
      memberRegistrationsData.push(registration);
      results.push(registration);
    }
    
    return results;
  },
  
  // Rooms & Teachers & Students
  getRooms: async () => {
    return rooms.map(r => ({
      id: r.id,
      name: r.name,
      capacity: 20, // Default, can be added to room data
      description: r.note || r.location
    }));
  },
  
  getTeachers: async () => {
    return teachers.map(t => getTeacher(t.id)).filter(t => t !== null);
  },
  
  getStudents: async () => {
    return members.map(m => getMember(m.id)).filter(m => m !== null);
  },
  
  // Sessions
  getSessions: async (fitnessClassId) => {
    const schedules = classSchedulesData.filter(cs => cs.fitnessClassId === fitnessClassId);
    return schedules.map(cs => {
      const session = classScheduleToSession(cs);
      // Ensure startTime and endTime are ISO strings for consistency
      if (cs.startTime && typeof cs.startTime === 'string') {
        session.startTime = cs.startTime; // Keep original ISO string
      }
      if (cs.endTime && typeof cs.endTime === 'string') {
        session.endTime = cs.endTime; // Keep original ISO string
      }
      return session;
    });
  },
  
  getAllSessions: async () => {
    return classSchedulesData.map(cs => classScheduleToSession(cs));
  },
  
  // Schedules
  getStudentSchedule: async (studentId, startDate, endDate) => {
    const start = new Date(startDate);
    const end = new Date(endDate);
    
    // Get student's registrations
    const myRegistrations = memberRegistrationsData.filter(mr => mr.memberId === studentId);
    const myScheduleIds = myRegistrations.map(mr => mr.classScheduleId);
    
    // Filter schedules
    const mySchedules = classSchedulesData.filter(cs => {
      const scheduleDate = new Date(cs.startTime);
      return myScheduleIds.includes(cs.id) &&
        scheduleDate >= start &&
        scheduleDate <= end;
    });
    
    return mySchedules.map(cs => classScheduleToSession(cs));
  },
  
  getTeacherSchedule: async (teacherId, startDate, endDate) => {
    const start = new Date(startDate);
    const end = new Date(endDate);
    
    // Get teacher's registered fitness classes
    const teacherRegistrations = classRegistrationsData.filter(cr => cr.staffId === teacherId);
    const teacherFitnessClassIds = teacherRegistrations.map(cr => cr.fitnessClassId);
    
    // Get all schedules for teacher's classes
    const mySchedules = classSchedulesData.filter(cs => {
      const scheduleDate = new Date(cs.startTime);
      return teacherFitnessClassIds.includes(cs.fitnessClassId) &&
        scheduleDate >= start &&
        scheduleDate <= end;
    });
    
    return mySchedules.map(cs => classScheduleToSession(cs));
  },
  
  // Conflict checking
  checkRoomConflicts,
  checkTeacherConflicts
};

export default mockApi;

