import api from './api'

// ==================== EXERCISE APIs ====================
export const exerciseApi = {
    // Lấy tất cả bài tập
    getAllExercises() {
        return api.get('/exercises')
    },

    // Lấy bài tập theo ID
    getExerciseById(id) {
        return api.get(`/exercises/${id}`)
    },

    // Tìm kiếm bài tập theo tên
    searchExercise(name) {
        return api.get('/exercises/search', { params: { name } })
    },

    // Lấy bài tập của user (cá nhân + hệ thống)
    getMyExercises(userId) {
        return api.get(`/exercises/user/${userId}`)
    },

    // Tạo bài tập mới
    createExercise(exerciseData) {
        return api.post('/exercises', exerciseData)
    },

    // Cập nhật bài tập
    updateExercise(id, exerciseData) {
        return api.put(`/exercises/${id}`, exerciseData)
    },

    // Xóa bài tập
    deleteExercise(id) {
        return api.delete(`/exercises/${id}`)
    }
}

// ==================== WORKOUT ROUTINE APIs ====================
export const workoutRoutineApi = {
    // Lấy tất cả mẫu lịch tập
    getAllRoutines() {
        return api.get('/workout-routines')
    },

    // Lấy mẫu visible cho user (public + của user)
    getVisibleRoutines(userId) {
        return api.get('/workout-routines', { params: { userId } })
    },

    // Lấy mẫu theo ID
    getRoutineById(id) {
        return api.get(`/workout-routines/${id}`)
    },

    // Tìm kiếm theo tên
    searchRoutines(name, userId = null) {
        const params = { name }
        if (userId) params.userId = userId
        return api.get('/workout-routines/search', { params })
    },

    // Lọc theo nhóm cơ
    filterByMuscleGroup(muscleGroup) {
        return api.get('/workout-routines/filter', { params: { muscle: muscleGroup } })
    },

    // Tạo mẫu mới
    createRoutine(routineData) {
        return api.post('/workout-routines', routineData)
    },

    // Cập nhật mẫu
    updateRoutine(id, routineData) {
        return api.put(`/workout-routines/${id}`, routineData)
    },

    // Xóa mẫu
    deleteRoutine(id) {
        return api.delete(`/workout-routines/${id}`)
    }
}

// ==================== TRAINING PLAN APIs ====================
export const trainingPlanApi = {
    // Tạo lịch tập từ mẫu (cho bản thân)
    generatePlanFromRoutine(data) {
        // data: { routineId, memberId, date }
        return api.post('/training-plans/generate', data)
    },

    // Gán lịch tập cho member khác
    assignPlanToMember(data) {
        // data: { routineId, memberId, date, assignedBy }
        return api.post('/training-plans/generate', data)
    },

    // Lấy lịch tập của mình
    getMyPlans(memberId) {
        return api.get(`/training-plans/history/${memberId}`)
    },

    // Lấy lịch tập hôm nay
    getTodayPlan(memberId) {
        return api.get(`/training-plans/today/${memberId}`)
    },

    // Lấy chi tiết lịch tập
    getPlanById(planId) {
        return api.get(`/training-plans/${planId}`)
    },

    // Đánh dấu exercise hoàn thành
    completeExercise(planId, exerciseId) {
        return api.patch(`/training-plans/${planId}/exercise/${exerciseId}/complete`)
    },

    // Hoàn thành toàn bộ plan
    completePlan(planId) {
        return api.patch(`/training-plans/${planId}/complete`)
    },

    // Cập nhật trạng thái
    updateStatus(planId, status) {
        return api.patch(`/training-plans/${planId}/status`, null, { params: { status } })
    }
}

// ==================== MEMBER APIs (for assignment) ====================
export const memberApi = {
    // Lấy danh sách members (để gán)
    getAllMembers() {
        return api.get('/members')
    },

    // Tìm kiếm member
    searchMembers(query) {
        return api.get('/members/search', { params: { q: query } })
    }
}
