// Unified API Service - Can switch between mock and real backend
// Set USE_REAL_API = true to use real backend, false for mock data
import apiService from '../views/Test/apiService';
import { formatDate } from '../views/Test/dateUtils';
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
} from '@/views/Test/mockData2.js';

const USE_REAL_API = true; // Set to false to use mock data instead

// ==================== MOCK DATA STORAGE ====================
let fitnessClassesData = [...fitnessClasses];
let schedulePatternsData = [...schedulePatterns];
let classSchedulesData = [...classSchedules];
let classRegistrationsData = [...classRegistrations];
let memberRegistrationsData = [...memberRegistrations];

let nextFitnessClassId = Math.max(...fitnessClasses.map(fc => fc.id), 0) + 1;
let nextSchedulePatternId = Math.max(...schedulePatterns.map(sp => sp.id), 0) + 1;
let nextClassScheduleId = Math.max(...classSchedules.map(cs => cs.id), 0) + 1;
let nextClassRegistrationId = Math.max(...classRegistrations.map(cr => cr.id), 0) + 1;
let nextMemberRegistrationId = Math.max(...memberRegistrations.map(mr => mr.id), 0) + 1;

// ==================== HELPER FUNCTIONS ====================
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

function getFitnessClassWithTeacher(fitnessClassId) {
  const fc = fitnessClassesData.find(f => f.id === fitnessClassId);
  if (!fc) return null;

  const registration = classRegistrationsData.find(cr => cr.fitnessClassId === fitnessClassId);
  const teacher = registration ? getTeacher(registration.staffId) : null;

  const pattern = schedulePatternsData.find(sp => {
    const schedule = classSchedulesData.find(cs => cs.fitnessClassId === fitnessClassId);
    return schedule && schedule.schedulePatternId === sp.id;
  });

  const firstSchedule = classSchedulesData.find(cs => cs.fitnessClassId === fitnessClassId);
  const room = firstSchedule ? rooms.find(r => r.id === firstSchedule.roomId) : null;

  const schedules = classSchedulesData.filter(cs => cs.fitnessClassId === fitnessClassId);

  let status = 'pending_teacher';
  if (registration) {
    status = 'ready_for_students';
  }

  const maxStudents = schedules.length > 0 ? Math.max(...schedules.map(s => s.capacity)) : 20;

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
    patternType: 'weekly',
    startTime: startTime,
    endTime: endTime,
    daysOfWeek: daysOfWeek,
    startDate: startDate,
    endDate: endDate,
    createdBy: 'manager',
    createdAt: new Date().toISOString()
  };
}

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
    startTime: cs.startTime, // Keep ISO string
    endTime: cs.endTime, // Keep ISO string
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

// ==================== UNIFIED API ====================
export const unifiedApi = {
  // Fitness Classes
  getClasses: async () => {
    if (USE_REAL_API) {
      const data = await apiService.fitnessClass.getAll();
      const classes = [];

      for (const fc of data) {
        // Get APPROVED teacher registration for this class
        let teacherId = null;
        try {
          const registrations = await apiService.classRegistration.getByFitnessClass(fc.id);
          // Only set teacherId if there's an APPROVED registration
          const approvedReg = registrations.find(r => r.status === 'APPROVED' || r.status === 'approved');
          if (approvedReg) {
            teacherId = approvedReg.teacher?.id || approvedReg.staffId;
          }
        } catch (e) {
          // No teacher registered yet
        }

        // Get schedules to determine maxStudents and schedule info
        let maxStudents = 20;
        let startTime = '07:00';
        let endTime = '08:30';
        let daysOfWeek = [];
        let startDate = '';
        let endDate = '';
        let roomId = null;
        let patternType = 'weekly'; // Default

        try {
          const schedules = await apiService.classSchedule.getByFitnessClass(fc.id);
          if (schedules.length > 0) {
            maxStudents = Math.max(...schedules.map(s => s.capacity || 20));
            const firstSchedule = schedules[0];
            roomId = firstSchedule.room?.id || firstSchedule.roomId;

            // Get schedule pattern info if available
            if (firstSchedule.schedulePattern) {
              const pattern = firstSchedule.schedulePattern;
              startTime = pattern.timeStart?.substring(0, 5) || '07:00';
              endTime = pattern.timeEnd?.substring(0, 5) || '08:30';
              startDate = pattern.classStartDate || '';
              endDate = pattern.classEndDate || '';

              if (pattern.daysOfWeek) {
                const dayMap = { 'MONDAY': 1, 'TUESDAY': 2, 'WEDNESDAY': 3, 'THURSDAY': 4, 'FRIDAY': 5, 'SATURDAY': 6, 'SUNDAY': 0 };
                daysOfWeek = pattern.daysOfWeek.split(',').map(d => dayMap[d.trim()]).filter(d => d !== undefined);
              }
            }

            // Determine pattern type based on schedules
            // If we have specific schedules but no repeating pattern (or empty daysOfWeek), it's custom dates
            if (schedules.length > 0 && daysOfWeek.length === 0) {
              patternType = 'no_repeat';
              // For no_repeat, calculate startDate and endDate from actual schedules
              const scheduleDates = schedules.map(s => s.startTime.split('T')[0]).sort();
              if (scheduleDates.length > 0) {
                startDate = scheduleDates[0];
                endDate = scheduleDates[scheduleDates.length - 1];
              }
            } else if (daysOfWeek.length > 0) {
              patternType = 'weekly';
            }
          }
        } catch (e) {
          // No schedules yet
        }

        classes.push({
          id: fc.id,
          name: fc.name,
          description: fc.description,
          difficulty: fc.difficultyLevel,
          maxStudents: maxStudents,
          status: teacherId ? 'ready_for_students' : 'pending_teacher',
          roomId: roomId,
          teacherId: teacherId,
          patternType: patternType,
          startTime: startTime,
          endTime: endTime,
          daysOfWeek: daysOfWeek,
          startDate: startDate,
          endDate: endDate,
          createdBy: 'manager',
          createdAt: fc.createdAt || new Date().toISOString()
        });
      }

      return classes;
    } else {
      return fitnessClassesData.map(fc => getFitnessClassWithTeacher(fc.id)).filter(c => c !== null);
    }
  },

  getClass: async (id) => {
    if (USE_REAL_API) {
      const fc = await apiService.fitnessClass.getById(id);
      return {
        id: fc.id,
        name: fc.name,
        description: fc.description,
        difficulty: fc.difficultyLevel,
        maxStudents: 20,
        status: 'ready_for_students',
        roomId: null,
        teacherId: null,
        patternType: 'weekly',
        startTime: '07:00',
        endTime: '08:30',
        daysOfWeek: [],
        startDate: '',
        endDate: '',
        createdBy: 'manager',
        createdAt: fc.createdAt || new Date().toISOString()
      };
    } else {
      return getFitnessClassWithTeacher(id);
    }
  },

  createClass: async (classData) => {
    if (USE_REAL_API) {
      const data = {
        name: classData.name,
        description: classData.description,
        difficultyLevel: classData.difficulty,
        status: 'ACTIVE'
      };
      return await apiService.fitnessClass.create(data);
    } else {
      // Mock implementation
      const newFitnessClass = {
        id: nextFitnessClassId++,
        name: classData.name,
        description: classData.description,
        difficultyLevel: classData.difficulty,
        status: 'Active'
      };
      fitnessClassesData.push(newFitnessClass);
      return getFitnessClassWithTeacher(newFitnessClass.id);
    }
  },

  updateClass: async (id, updates) => {
    if (USE_REAL_API) {
      return await apiService.fitnessClass.update(id, updates);
    } else {
      const index = fitnessClassesData.findIndex(fc => fc.id === id);
      if (index !== -1) {
        fitnessClassesData[index] = { ...fitnessClassesData[index], ...updates };
        return getFitnessClassWithTeacher(id);
      }
      throw new Error('Class not found');
    }
  },

  deleteClass: async (id) => {
    if (USE_REAL_API) {
      return await apiService.fitnessClass.delete(id);
    } else {
      const index = fitnessClassesData.findIndex(fc => fc.id === id);
      if (index !== -1) {
        fitnessClassesData.splice(index, 1);
        classSchedulesData = classSchedulesData.filter(cs => cs.fitnessClassId !== id);
        classRegistrationsData = classRegistrationsData.filter(cr => cr.fitnessClassId !== id);
        return true;
      }
      throw new Error('Class not found');
    }
  },

  // Class Schedules
  getSessions: async (fitnessClassId) => {
    if (USE_REAL_API) {
      const schedules = await apiService.classSchedule.getByFitnessClass(fitnessClassId);
      return schedules.map(cs => {
        // Extract date directly from LocalDateTime string to avoid timezone issues
        const dateStr = cs.startTime ? cs.startTime.split('T')[0] : null;
        // Parse time from LocalDateTime for display
        const startDateTime = new Date(cs.startTime);
        const endDateTime = new Date(cs.endTime);
        const startTimeStr = startDateTime.toTimeString().substring(0, 5);
        const endTimeStr = endDateTime.toTimeString().substring(0, 5);

        return {
          id: cs.id,
          classId: cs.fitnessClass?.id || fitnessClassId,
          date: dateStr,
          startTime: cs.startTime, // Keep full ISO string for compatibility
          endTime: cs.endTime, // Keep full ISO string for compatibility
          roomId: cs.room?.id || cs.roomId,
          room: cs.room,
          status: cs.status,
          capacity: cs.capacity,
          className: cs.fitnessClass?.name || 'Unknown',
          roomName: cs.room?.name || 'Unknown',
          teacherName: 'Unknown',
          note: cs.note || null,
          fitnessClass: cs.fitnessClass,
          schedulePattern: cs.schedulePattern
        };
      });
    } else {
      const schedules = classSchedulesData.filter(cs => cs.fitnessClassId === fitnessClassId);
      return schedules.map(cs => classScheduleToSession(cs));
    }
  },

  // Teacher Registrations
  applyToTeach: async (fitnessClassId, teacherId) => {
    if (USE_REAL_API) {
      const data = {
        teacher: { id: teacherId },
        fitnessClass: { id: fitnessClassId },
        description: 'Pending approval'
      };
      // Backend expects staffId in request body along with ClassRegistration
      const result = await apiService.classRegistration.registerTeaching(teacherId, data);
      return {
        id: result.id,
        classId: fitnessClassId,
        teacherId: teacherId,
        status: 'pending',
        appliedAt: new Date().toISOString(),
        reviewedAt: null,
        reviewedBy: null,
        rejectionReason: null
      };
    } else {
      const existing = classRegistrationsData.find(cr =>
        cr.fitnessClassId === fitnessClassId && cr.staffId === teacherId
      );
      if (existing) {
        throw new Error('Already applied to teach this class');
      }
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
    }
  },

  getApplications: async () => {
    if (USE_REAL_API) {
      // Get all classes and their registrations
      const classes = await apiService.fitnessClass.getAll();
      const applications = [];
      for (const cls of classes) {
        try {
          const regs = await apiService.classRegistration.getByFitnessClass(cls.id);
          regs.forEach(reg => {
            applications.push({
              id: reg.id,
              classId: cls.id,
              teacherId: reg.teacher?.id || reg.staffId,
              status: (reg.status || 'PENDING').toLowerCase(), // Use actual status from backend
              appliedAt: new Date().toISOString(),
              reviewedAt: reg.status === 'APPROVED' || reg.status === 'REJECTED' ? new Date().toISOString() : null,
              reviewedBy: reg.status === 'APPROVED' || reg.status === 'REJECTED' ? 'manager' : null,
              rejectionReason: reg.status === 'REJECTED' ? reg.description : null
            });
          });
        } catch (e) {
          // No registrations for this class
        }
      }
      return applications;
    } else {
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
    }
  },

  approveTeacher: async (applicationId, managerId) => {
    if (USE_REAL_API) {
      try {
        // Approve the registration - backend returns the updated registration
        const updatedRegistration = await apiService.classRegistration.approve(applicationId);

        return {
          id: applicationId,
          status: 'approved',
          reviewedAt: new Date().toISOString(),
          reviewedBy: managerId
        };
      } catch (error) {
        console.error('Error approving teacher:', error);
        throw error;
      }
    } else {
      const registration = classRegistrationsData.find(cr => cr.id === applicationId);
      if (!registration) throw new Error('Application not found');
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
    }
  },

  rejectTeacher: async (applicationId, managerId, reason) => {
    if (USE_REAL_API) {
      try {
        // Reject the registration with reason
        await apiService.classRegistration.reject(applicationId, reason);

        return {
          id: applicationId,
          status: 'rejected',
          reviewedAt: new Date().toISOString(),
          reviewedBy: managerId,
          rejectionReason: reason
        };
      } catch (error) {
        console.error('Error rejecting teacher:', error);
        throw error;
      }
    } else {
      const registration = classRegistrationsData.find(cr => cr.id === applicationId);
      if (!registration) throw new Error('Application not found');
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
    }
  },

  // Member Registrations
  registerBulkSchedules: async (memberId, scheduleIds) => {
    console.log(`--- Bulk Registration Started ---`);
    console.log(`Member ID: ${memberId}`);
    console.log(`Schedule IDs to process:`, scheduleIds);
    if (USE_REAL_API) {
      return await apiService.memberRegistration.registerBulk(memberId, scheduleIds);
    } else {
      const results = [];
      for (const scheduleId of scheduleIds) {
        const schedule = classSchedulesData.find(cs => cs.id === scheduleId);
        if (!schedule) throw new Error(`Schedule ${scheduleId} not found`);

        const existing = memberRegistrationsData.find(mr =>
          mr.classScheduleId === scheduleId && mr.memberId === memberId
        );
        if (existing) continue;

        const currentCount = memberRegistrationsData.filter(mr =>
          mr.classScheduleId === scheduleId
        ).length;
        if (currentCount >= schedule.capacity) {
          throw new Error(`Schedule ${scheduleId} is full`);
        }
        if (schedule.status !== 'OPEN') {
          throw new Error(`Schedule ${scheduleId} is not open for registration`);
        }

        const registration = {
          id: nextMemberRegistrationId++,
          followDate: new Date().toISOString(),
          classScheduleId: scheduleId,
          memberId: memberId
        };
        memberRegistrationsData.push(registration);
        results.push(registration);
      }
      return results;
    }
  },

  getStudentRegistrations: async (studentId) => {
    if (USE_REAL_API) {
      const registrations = await apiService.memberRegistration.getByMember(studentId);
      return registrations.map(mr => ({
        id: mr.id,
        classId: mr.classSchedule?.fitnessClass?.id || null,
        scheduleId: mr.classSchedule?.id || null,
        studentId: studentId,
        registeredAt: mr.followDate,
        status: 'active'
      }));
    } else {
      const registrations = memberRegistrationsData.filter(mr => mr.memberId === studentId);
      return registrations.map(mr => {
        const schedule = classSchedulesData.find(cs => cs.id === mr.classScheduleId);
        return {
          id: mr.id,
          classId: schedule ? schedule.fitnessClassId : null,
          scheduleId: mr.classScheduleId,
          studentId: studentId,
          registeredAt: mr.followDate,
          status: 'active'
        };
      });
    }
  },

  cancelRegistration: async (fitnessClassId, studentId) => {
    if (USE_REAL_API) {
      // Get all registrations for this student
      const registrations = await apiService.memberRegistration.getByMember(studentId);
      // Find registration for schedules of this class
      const schedules = await apiService.classSchedule.getByFitnessClass(fitnessClassId);
      const scheduleIds = schedules.map(s => s.id);
      const registration = registrations.find(r =>
        scheduleIds.includes(r.classSchedule?.id)
      );
      if (registration) {
        return await apiService.memberRegistration.cancel(registration.id, studentId);
      }
      throw new Error('Registration not found');
    } else {
      const schedules = classSchedulesData.filter(cs => cs.fitnessClassId === fitnessClassId);
      const registration = memberRegistrationsData.find(mr =>
        schedules.some(cs => cs.id === mr.classScheduleId) &&
        mr.memberId === studentId
      );
      if (!registration) throw new Error('Registration not found');
      const index = memberRegistrationsData.findIndex(mr => mr.id === registration.id);
      if (index !== -1) {
        memberRegistrationsData.splice(index, 1);
      }
      return registration;
    }
  },

  // Rooms, Teachers, Students
  getRooms: async () => {
    if (USE_REAL_API) {
      const data = await apiService.room.getAll();
      return data.map(r => ({
        id: r.id,
        name: r.name,
        capacity: r.capacity || 20, // Use capacity from backend if available
        description: r.note || r.location || r.description
      }));
    } else {
      return rooms.map(r => ({
        id: r.id,
        name: r.name,
        capacity: 20,
        description: r.note || r.location
      }));
    }
  },

  getAvailableRoomsForPattern: async (pattern) => {
    if (USE_REAL_API) {
      // pattern should have: daysOfWeek, timeStart, timeEnd, classStartDate, classEndDate
      const data = await apiService.room.getAvailableForPattern(pattern);
      return data.map(r => ({
        id: r.id,
        name: r.name,
        capacity: r.capacity || 20,
        description: r.note || r.location || r.description
      }));
    } else {
      // For mock, return all rooms
      return rooms.map(r => ({
        id: r.id,
        name: r.name,
        capacity: 20,
        description: r.note || r.location
      }));
    }
  },

  getTeachers: async () => {
    if (USE_REAL_API) {
      const data = await apiService.teacher.getAll();
      return data.map(t => ({
        id: t.id,
        name: t.fullName,
        email: t.email,
        specialties: t.specialize ? t.specialize.split(',').map(s => s.trim()) : [],
        bio: `${t.position} - ${t.specialize}`,
        avatar: '👨‍🏫'
      }));
    } else {
      return teachers.map(t => getTeacher(t.id)).filter(t => t !== null);
    }
  },

  // Get teachers by fitness class ID (from ClassRegistration)
  getTeachersByFitnessClass: async (fitnessClassId) => {
    if (USE_REAL_API) {
      try {
        const registrations = await apiService.classRegistration.getByFitnessClass(fitnessClassId);
        return registrations;
      } catch (error) {
        console.error(`Error getting teachers for fitness class ${fitnessClassId}:`, error);
        return [];
      }
    } else {
      // Mock data version
      const registrations = classRegistrationsData.filter(cr => cr.fitnessClassId === fitnessClassId);
      return registrations.map(reg => {
        const teacher = getTeacher(reg.staffId);
        return {
          id: reg.id,
          teacher: teacher,
          status: reg.status,
          fitnessClass: { id: fitnessClassId }
        };
      });
    }
  },

  getStudents: async () => {
    if (USE_REAL_API) {
      const data = await apiService.member.getAll();
      return data.map(m => ({
        id: m.id,
        name: m.fullName,
        email: m.email,
        membershipTier: m.membership,
        joinDate: m.joinDate
      }));
    } else {
      return members.map(m => getMember(m.id)).filter(m => m !== null);
    }
  },

  // Schedules
  getStudentSchedule: async (studentId, startDate, endDate) => {
    if (USE_REAL_API) {
      const registrations = await apiService.memberRegistration.getByMember(studentId);
      const schedules = [];
      for (const reg of registrations) {
        if (reg.classSchedule) {
          // Extract date directly from LocalDateTime string to avoid timezone issues
          const dateStr = reg.classSchedule.startTime ? reg.classSchedule.startTime.split('T')[0] : null;
          if (!dateStr) continue;

          // Compare dates as strings (YYYY-MM-DD format)
          if (dateStr >= startDate && dateStr <= endDate) {
            // Parse time from LocalDateTime for display
            const startDateTime = new Date(reg.classSchedule.startTime);
            const endDateTime = new Date(reg.classSchedule.endTime);
            const startTimeStr = startDateTime.toTimeString().substring(0, 5);
            const endTimeStr = endDateTime.toTimeString().substring(0, 5);

            // Get className - try multiple sources
            let className = 'Unknown';

            if (reg.fitnessClass?.name) {
              className = reg.fitnessClass.name;
            } else if (reg.classSchedule.fitnessClass?.name) {
              className = reg.classSchedule.fitnessClass.name;
            } else if (reg.classSchedule.fitnessClass?.id) {
              // Fetch class details if we have the ID but not the name
              try {
                const classDetails = await apiService.fitnessClass.getById(reg.classSchedule.fitnessClass.id);
                className = classDetails.name || 'Unknown';
              } catch (e) {
                console.warn('❌ Could not fetch class details:', e);
              }
            } else {
              console.warn('⚠️ No fitnessClass data available');
            }

            schedules.push({
              id: reg.classSchedule.id,
              classId: reg.classSchedule.fitnessClass?.id,
              date: dateStr,
              startTime: startTimeStr,
              endTime: endTimeStr,
              roomId: reg.classSchedule.room?.id,
              room: reg.classSchedule.room,
              status: reg.classSchedule.status,
              capacity: reg.classSchedule.capacity,
              className: className,
              roomName: reg.classSchedule.room?.name || 'Unknown',
              teacherName: reg.classSchedule.teacher?.fullName || 'Unknown'
            });
          }
        }
      }
      return schedules;
    } else {
      const start = new Date(startDate);
      const end = new Date(endDate);
      const myRegistrations = memberRegistrationsData.filter(mr => mr.memberId === studentId);
      const myScheduleIds = myRegistrations.map(mr => mr.classScheduleId);
      const mySchedules = classSchedulesData.filter(cs => {
        const scheduleDate = new Date(cs.startTime);
        return myScheduleIds.includes(cs.id) &&
          scheduleDate >= start &&
          scheduleDate <= end;
      });
      return mySchedules.map(cs => classScheduleToSession(cs));
    }
  },

  getTeacherSchedule: async (teacherId, startDate, endDate) => {
    if (USE_REAL_API) {
      const registrations = await apiService.classRegistration.getByTeacher(teacherId);
      // Only show APPROVED classes
      const approvedRegistrations = registrations.filter(r => r.status === 'APPROVED');
      const schedules = [];
      for (const reg of approvedRegistrations) {
        const fitnessClassDetails = reg.fitnessClass;
        const classSchedules = await apiService.classSchedule.getByFitnessClass(reg.fitnessClass?.id);
        for (const cs of classSchedules) {
          // Extract date directly from LocalDateTime string to avoid timezone issues
          // cs.startTime is in format "2024-01-15T00:00:00" (LocalDateTime, no timezone)
          const dateStr = cs.startTime ? cs.startTime.split('T')[0] : null;
          if (!dateStr) continue;

          // Compare dates as strings (YYYY-MM-DD format)
          if (dateStr >= startDate && dateStr <= endDate) {
            // Parse time from LocalDateTime for display
            const startDateTime = new Date(cs.startTime);
            const endDateTime = new Date(cs.endTime);
            const startTimeStr = startDateTime.toTimeString().substring(0, 5);
            const endTimeStr = endDateTime.toTimeString().substring(0, 5);

            schedules.push({
              id: cs.id,
              classId: cs.fitnessClass?.id,
              date: dateStr,
              startTime: startTimeStr,
              endTime: endTimeStr,
              roomId: cs.room?.id,
              room: cs.room,
              status: cs.status,
              capacity: cs.capacity,
              className: fitnessClassDetails?.name || cs.fitnessClass?.name || 'Unknown',
              roomName: cs.room?.name || 'Unknown',
              teacherName: reg.teacher?.fullName || 'Unknown'
            });
          }
        }
      }
      return schedules;
    } else {
      const start = new Date(startDate);
      const end = new Date(endDate);
      const teacherRegistrations = classRegistrationsData.filter(cr => cr.staffId === teacherId);
      const teacherFitnessClassIds = teacherRegistrations.map(cr => cr.fitnessClassId);
      const mySchedules = classSchedulesData.filter(cs => {
        const scheduleDate = new Date(cs.startTime);
        return teacherFitnessClassIds.includes(cs.fitnessClassId) &&
          scheduleDate >= start &&
          scheduleDate <= end;
      });
      return mySchedules.map(cs => classScheduleToSession(cs));
    }
  },

  // Conflict checking
  checkRoomConflicts: async (roomId, newSessions, excludeFitnessClassId = null) => {
    try {
      // Get all schedules for this room
      const allSchedules = await apiService.classSchedule.getAll();
      const roomSchedules = allSchedules.filter(s => s.room?.id === roomId);


      // Fetch all fitness classes and build a schedule-to-class map
      const allClasses = await apiService.fitnessClass.getAll();
      const scheduleToClassMap = {};

      for (const cls of allClasses) {
        try {
          const classSchedules = await apiService.classSchedule.getByFitnessClass(cls.id);
          classSchedules.forEach(schedule => {
            scheduleToClassMap[schedule.id] = cls.name;
          });
        } catch (e) {
          console.error(`Error fetching schedules for class ${cls.id}:`, e);
        }
      }


      const conflicts = [];

      for (const newSession of newSessions) {
        const newStart = new Date(newSession.startTime);
        const newEnd = new Date(newSession.endTime);

        for (const existing of roomSchedules) {
          // Skip if it's from the class we're editing
          if (excludeFitnessClassId && existing.fitnessClass?.id === excludeFitnessClassId) {
            continue;
          }

          const existingStart = new Date(existing.startTime);
          const existingEnd = new Date(existing.endTime);

          // Check if time ranges overlap
          if (newStart < existingEnd && newEnd > existingStart) {
            const dateStr = existingStart.toISOString().split('T')[0];
            const timeStr = `${existingStart.toTimeString().substring(0, 5)} - ${existingEnd.toTimeString().substring(0, 5)}`;


            // Get class name from schedule-to-class map
            const className = scheduleToClassMap[existing.id] || 'Unknown Class';

            conflicts.push({
              date: formatDate(dateStr),
              time: timeStr,
              className: className
            });
          }
        }
      }

      return conflicts;
    } catch (error) {
      console.error('Error checking room conflicts:', error);
      return [];
    }
  },


  checkTeacherConflicts: (teacherId, newSessions, excludeFitnessClassId = null) => {
    // This would need real implementation for real API
    return [];
  }
};

export default unifiedApi;


