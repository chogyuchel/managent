import { createRouter, createWebHistory } from 'vue-router'
import {
  Avatar,
  DataAnalysis,
  Document,
  OfficeBuilding,
  School,
  UserFilled
} from '@element-plus/icons-vue'
import { getToken } from '../utils/auth'

const routes = [
  {
    path: '/login',
    component: () => import('../views/LoginView.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/',
    component: () => import('../layout/AppLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('../views/DashboardView.vue'),
        meta: { title: '数据统计', icon: DataAnalysis }
      },
      {
        path: 'depts',
        component: () => import('../views/DeptView.vue'),
        meta: { title: '部门管理', icon: OfficeBuilding }
      },
      {
        path: 'emps',
        component: () => import('../views/EmpView.vue'),
        meta: { title: '员工管理', icon: UserFilled }
      },
      {
        path: 'clazzs',
        component: () => import('../views/ClazzView.vue'),
        meta: { title: '班级管理', icon: School }
      },
      {
        path: 'students',
        component: () => import('../views/StudentView.vue'),
        meta: { title: '学员管理', icon: Avatar }
      },
      {
        path: 'logs',
        component: () => import('../views/LogView.vue'),
        meta: { title: '操作日志', icon: Document }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  document.title = to.meta.title ? `${to.meta.title} · Tlias 教学管理系统` : 'Tlias 教学管理系统'
  if (!to.meta.public && !getToken()) return { path: '/login' }
  if (to.path === '/login' && getToken()) return { path: '/dashboard' }
  return true
})

export default router