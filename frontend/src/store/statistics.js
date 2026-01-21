import { defineStore } from 'pinia'
import { getDashboard } from '@/api/statistics'

export const useStatisticsStore = defineStore('statistics', {
    state: () => ({
        dashboard: null
    }),

    actions: {
        async fetchDashboard() {
            const res = await getDashboard()
            this.dashboard = res.data
            return this.dashboard
        }
    }
})
