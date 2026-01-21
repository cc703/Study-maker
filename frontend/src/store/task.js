import { defineStore } from 'pinia'
import { getTodayTasks } from '@/api/task'

export const useTaskStore = defineStore('task', {
    state: () => ({
        todayTasks: []
    }),

    actions: {
        async fetchTodayTasks() {
            const res = await getTodayTasks()
            this.todayTasks = res.data || []
            return this.todayTasks
        }
    }
})
