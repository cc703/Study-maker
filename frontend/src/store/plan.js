import { defineStore } from 'pinia'
import { getPlanList, getActivePlanList } from '@/api/plan'

export const usePlanStore = defineStore('plan', {
    state: () => ({
        planList: [],
        activePlanList: []
    }),

    actions: {
        async fetchPlanList() {
            const res = await getPlanList()
            this.planList = res.data || []
            return this.planList
        },

        async fetchActivePlanList() {
            const res = await getActivePlanList()
            this.activePlanList = res.data || []
            return this.activePlanList
        }
    }
})
