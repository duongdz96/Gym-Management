// API Service for Test views - connects to real backend
import api from '@/services/api';

// ==================== FITNESS CLASS ====================
export const fitnessClassApi = {
  getAll: async () => {
    const res = await api.get('/fitness_class');
    return res.data;
  },
  
  getById: async (id) => {
    const res = await api.get(`/fitness_class/${id}`);
    return res.data;
  },
  
  create: async (data) => {
    const res = await api.post('/fitness_class', data);
    return res.data;
  },
  
  update: async (id, data) => {
    const res = await api.put(`/fitness_class/${id}`, data);
    return res.data;
  },
  
  delete: async (id) => {
    const res = await api.delete(`/fitness_class/${id}`);
    return res.data;
  }
};

// ==================== CLASS SCHEDULE ====================
export const classScheduleApi = {
  getByFitnessClass: async (fitnessClassId) => {
    const res = await api.get(`/classschedule/by-fitness_class/${fitnessClassId}`);
    return res.data;
  },
  
  generate: async (data) => {
    // data should have: { fitnessClass: {id}, schedulePattern: {id}, room: {id} }
    const res = await api.post('/classschedule/generate', data);
    return res.data;
  },
  
  create: async (data) => {
    const res = await api.post('/classschedule', data);
    return res.data;
  },
  
  update: async (id, data) => {
    const res = await api.put(`/classschedule/${id}`, data);
    return res.data;
  },
  
  delete: async (id) => {
    const res = await api.delete(`/classschedule/${id}`);
    return res.data;
  }
};

// ==================== CLASS REGISTRATION (Teacher) ====================
export const classRegistrationApi = {
  getByTeacher: async (staffId) => {
    const res = await api.get(`/class-registrations/teacher/${staffId}`);
    return res.data;
  },
  
  getByFitnessClass: async (fitnessClassId) => {
    const res = await api.get(`/class-registrations/fitness_class/${fitnessClassId}`);
    return res.data;
  },
  
  registerTeaching: async (staffId, data) => {
    
    const classRegistrationBody = {
        teacher: { 
            id: staffId 
        }, 
        fitnessClass: { 
            id: data.fitnessClass.id
        },
        description: data.description || 'Phân công giảng dạy'
    };
    const res = await api.post('/class-registrations', classRegistrationBody);
    return res.data;
},
  
  unregisterTeaching: async (staffId, registrationId) => {
    const res = await api.delete(`/class-registrations/${registrationId}`, {
      params: { staffId }
    });
    return res.data;
  }
};

// ==================== MEMBER REGISTRATION ====================
export const memberRegistrationApi = {
  getByMember: async (memberId) => {
    const res = await api.get(`/member-registrations/member/${memberId}`);
    return res.data;
  },
  
  getBySchedule: async (scheduleId) => {
    const res = await api.get(`/member-registrations/schedule/${scheduleId}`);
    return res.data;
  },
  
  register: async (data) => {
    // data should have: { member: {id}, classSchedule: {id} }
    const res = await api.post('/member-registrations', data);
    return res.data;
  },
  
  registerBulk: async (memberId, scheduleIds) => {
    // scheduleIds is array of Long
    const res = await api.post('/member-registrations/bulk-register', {
      memberId,
      scheduleIds
    });
    return res.data;
  },
  
  cancel: async (registrationId, memberId) => {
    const res = await api.delete(`/member-registrations/${registrationId}`, {
      params: { memberId }
    });
    return res.data;
  },
  
  cancelBulk: async (registrationIds, memberId) => {
    const res = await api.delete('/member-registrations/bulk-cancel', {
      data: { registrationIds, memberId }
    });
    return res.data;
  }
};

// ==================== ROOMS ====================
export const roomApi = {
  getAll: async () => {
    const res = await api.get('/room');
    return res.data;
  },
  
  getById: async (id) => {
    const res = await api.get(`/room/${id}`);
    return res.data;
  },
  
  getAvailableForPattern: async (pattern) => {
    // POST /api/room/available-for-pattern
    // pattern should have: daysOfWeek, timeStart, timeEnd, classStartDate, classEndDate
    const res = await api.post('/room/available-for-pattern', pattern);
    return res.data;
  }
};

// ==================== TEACHERS ====================
export const teacherApi = {
  getAll: async () => {
    const res = await api.get('/teachers');
    return res.data;
  },
  
  getById: async (id) => {
    const res = await api.get(`/teachers/${id}`);
    return res.data;
  }
};

// ==================== MEMBERS ====================
export const memberApi = {
  getAll: async () => {
    const res = await api.get('/members');
    return res.data;
  },
  
  getById: async (id) => {
    const res = await api.get(`/members/${id}`);
    return res.data;
  }
};

// ==================== SCHEDULE PATTERN ====================
export const schedulePatternApi = {
  getAll: async () => {
    const res = await api.get('/schedule_pattern');
    return res.data;
  },
  
  getById: async (id) => {
    const res = await api.get(`/schedule_pattern/${id}`);
    return res.data;
  },
  
  create: async (data) => {
    const res = await api.post('/schedule_pattern', data);
    return res.data;
  },
  
  update: async (id, data) => {
    const res = await api.put(`/schedule_pattern/${id}`, data);
    return res.data;
  },
  
  delete: async (id) => {
    const res = await api.delete(`/schedule_pattern/${id}`);
    return res.data;
  }
};

export default {
  fitnessClass: fitnessClassApi,
  classSchedule: classScheduleApi,
  classRegistration: classRegistrationApi,
  memberRegistration: memberRegistrationApi,
  room: roomApi,
  teacher: teacherApi,
  member: memberApi,
  schedulePattern: schedulePatternApi
};

