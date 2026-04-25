import Vue from 'vue'
import VueRouter from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LayoutVue from '@/views/Layout.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Login.vue')
  },
  {
    path: "/",
    component: LayoutVue,
    children: [
      {
        path: '',
        name: 'home',
        component: HomeView
      },
      {
        path: 'devices',
        name: 'devices',
        component: () => import('../views/Devices.vue')
      },
      {
        path: 'inventory/management',
        name: 'inventoryManagement',
        component: () => import('../views/InventoryManagement.vue')
      },
      {
        path: 'inventory/transaction',
        name: 'inventoryTransaction',
        component: () => import('../views/InventoryTransaction.vue')
      },
      {
        path: 'order/myorder',
        name: 'myorder',
        component: () => import('../views/MyOrder.vue')
      },
      {
        path: 'order/history',
        name: 'historyOrder',
        component: () => import('../views/HistoryOrder.vue')
      },
      {
        path: 'stats/inventory',
        name: 'inventoryStats',
        component: () => import('../views/InventoryStats.vue')
      },
      {
        path: 'stats/order',
        name: 'orderStats',
        component: () => import('../views/OrderStats.vue')
      },
      {
        path: 'emp',
        name: 'Employee',
        component: () => import('../views/Employee.vue')
      },
      {
        path: 'authcode',
        name: 'authcode',
        component: () => import('../views/Authcode.vue')
      },
      {
        path: 'user',
        name: 'user',
        component: () => import('../views/User.vue')
      },
      { path: '*', redirect: '/' },
    ]
  },

]

const router = new VueRouter({
  mode: 'history',
  routes
})

//路由守卫
router.beforeEach((to,from,next) => {
    if(to.path === "/login") {
      next();
    }
    const user = localStorage.getItem("user");
    if(!user && to.path != "/login") {
      return next("/login")
    }
    next();
})




export default router
