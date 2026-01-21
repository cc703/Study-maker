import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/layout/Layout.vue'

const routes = [
    {
        path: '/welcome',
        name: 'Welcome',
        component: () => import('@/views/welcome/index.vue'),
        meta: { title: '欢迎页' }
    },
    {
        path: '/',
        component: Layout,
        redirect: '/dashboard',
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('@/views/dashboard/index.vue'),
                meta: { title: '仪表盘', icon: 'Odometer' }
            },
            {
                path: 'plan',
                name: 'Plan',
                component: () => import('@/views/plan/index.vue'),
                meta: { title: '学习方向', icon: 'Flag' }
            },
            {
                path: 'stage',
                name: 'Stage',
                component: () => import('@/views/stage/index.vue'),
                meta: { title: '阶段目标', icon: 'Medal' }
            },
            {
                path: 'task',
                name: 'Task',
                component: () => import('@/views/task/index.vue'),
                meta: { title: '任务管理', icon: 'List' }
            },
            {
                path: 'statistics',
                name: 'Statistics',
                component: () => import('@/views/statistics/index.vue'),
                meta: { title: '统计复盘', icon: 'DataAnalysis' }
            },
            {
                path: 'memo',
                name: 'Memo',
                component: () => import('@/views/memo/index.vue'),
                meta: { title: '备忘录', icon: 'Document' }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
