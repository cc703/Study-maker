import { defineStore } from 'pinia'
import { getStageList } from '@/api/stage'

export const useStageStore = defineStore('stage', {
    state: () => ({
        stageList: []
    }),

    actions: {
        async fetchStageList() {
            const res = await getStageList()
            this.stageList = res.data || []
            return this.stageList
        }
    }
})
