// Date utility functions for class scheduling

export function formatDate(dateString) {
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
}

export function formatTime(timeString) {
    return timeString;
}

export function formatDateTime(dateTimeString) {
    const date = new Date(dateTimeString);
    return `${formatDate(date.toISOString().split('T')[0])} ${formatTime(date.toTimeString().slice(0, 5))}`;
}

export function getDayName(dayNumber) {
    const days = ['Chủ nhật', 'Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7'];
    return days[dayNumber];
}

export function getDayShortName(dayNumber) {
    const days = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'];
    return days[dayNumber];
}

export function getWeeksUntilStart(startDate) {
    const start = new Date(startDate);
    const now = new Date();
    const diffTime = start - now;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return Math.floor(diffDays / 7);
}

// VIP can register 2-4 weeks before class starts
export function canVIPRegister(startDate) {
    const weeksUntil = getWeeksUntilStart(startDate);
    return weeksUntil >= 2 && weeksUntil <= 4;
}

// All students can register less than 2 weeks before class starts
export function canAllRegister(startDate) {
    const weeksUntil = getWeeksUntilStart(startDate);
    return weeksUntil < 2;
}

export function isDateInRange(date, startDate, endDate) {
    const d = new Date(date);
    const start = new Date(startDate);
    const end = new Date(endDate);
    return d >= start && d <= end;
}

export function addDays(dateString, days) {
    const date = new Date(dateString);
    date.setDate(date.getDate() + days);
    return date.toISOString().split('T')[0];
}

export function addWeeks(dateString, weeks) {
    return addDays(dateString, weeks * 7);
}

export function addMonths(dateString, months) {
    const date = new Date(dateString);
    date.setMonth(date.getMonth() + months);
    return date.toISOString().split('T')[0];
}

export function getDatesBetween(startDate, endDate) {
    const dates = [];
    let currentDate = new Date(startDate);
    const end = new Date(endDate);

    while (currentDate <= end) {
        dates.push(currentDate.toISOString().split('T')[0]);
        currentDate.setDate(currentDate.getDate() + 1);
    }

    return dates;
}

export function filterByDaysOfWeek(dates, daysOfWeek) {
    return dates.filter(dateString => {
        const date = new Date(dateString);
        return daysOfWeek.includes(date.getDay());
    });
}

export function getTodayString() {
    return new Date().toISOString().split('T')[0];
}

export function compareDates(date1, date2) {
    const d1 = new Date(date1);
    const d2 = new Date(date2);
    return d1 - d2;
}

export function isSameDate(date1, date2) {
    return date1 === date2;
}

export function isBeforeToday(dateString) {
    const date = new Date(dateString);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return date < today;
}

export function isAfterToday(dateString) {
    const date = new Date(dateString);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return date > today;
}

export function getMonthName(monthNumber) {
    const months = [
        'Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6',
        'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12'
    ];
    return months[monthNumber];
}

export function getCalendarWeeks(year, month) {
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);

    const weeks = [];
    let currentWeek = [];

    // Fill in days before first day of month
    const firstDayOfWeek = firstDay.getDay();
    for (let i = 0; i < firstDayOfWeek; i++) {
        currentWeek.push(null);
    }

    // Fill in days of month
    for (let day = 1; day <= lastDay.getDate(); day++) {
        currentWeek.push(new Date(year, month, day));

        if (currentWeek.length === 7) {
            weeks.push(currentWeek);
            currentWeek = [];
        }
    }

    // Fill in remaining days
    while (currentWeek.length < 7 && currentWeek.length > 0) {
        currentWeek.push(null);
    }

    if (currentWeek.length > 0) {
        weeks.push(currentWeek);
    }

    return weeks;
}
