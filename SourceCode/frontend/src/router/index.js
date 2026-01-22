import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'locker',
        name: 'Locker',
        redirect: '/locker/list',
        meta: { title: '快递柜管理', icon: 'Box' },
        children: [
          {
            path: 'list',
            name: 'LockerList',
            component: () => import('@/views/locker/list.vue'),
            meta: { title: '快递柜列表' }
          },
          {
            path: 'compartment',
            name: 'Compartment',
            component: () => import('@/views/locker/compartment.vue'),
            meta: { title: '仓门管理' }
          }
        ]
      },
      {
        path: 'express',
        name: 'Express',
        redirect: '/express/list',
        meta: { title: '快递管理', icon: 'Postcard' },
        children: [
          {
            path: 'list',
            name: 'ExpressList',
            component: () => import('@/views/express/list.vue'),
            meta: { title: '快递列表' }
          },
          {
            path: 'storage',
            name: 'StorageList',
            component: () => import('@/views/express/storage.vue'),
            meta: { title: '寄存记录' }
          }
        ]
      },
      {
        path: 'user',
        name: 'User',
        redirect: '/user/list',
        meta: { title: '用户管理', icon: 'User' },
        children: [
          {
            path: 'list',
            name: 'UserList',
            component: () => import('@/views/user/list.vue'),
            meta: { title: '普通用户' }
          },
          {
            path: 'courier',
            name: 'CourierList',
            component: () => import('@/views/user/courier.vue'),
            meta: { title: '快递员管理' }
          }
        ]
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/statistics/index.vue'),
        meta: { title: '统计分析', icon: 'DataAnalysis' }
      },
      {
        path: 'system',
        name: 'System',
        redirect: '/system/config',
        meta: { title: '系统设置', icon: 'Setting' },
        children: [
          {
            path: 'config',
            name: 'SystemConfig',
            component: () => import('@/views/system/config.vue'),
            meta: { title: '系统配置' }
          }
        ]
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title} - 快递柜管理系统`
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
