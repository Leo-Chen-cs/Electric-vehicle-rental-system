import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/index/index.vue'),
    meta: {
      title: 'Home',
      icon: 'home',
      showTabBar: true
    }
  },
  {
    path: '/rent/:scooterId',
    name: 'Rent',
    component: () => import('../views/rent/index.vue'),
    meta: {
      title: 'Scooter Rental',
      showTabBar: false
    }
  },
  {
    path: '/records',
    name: 'Records',
    component: () => import('../views/records/index.vue'),
    meta: {
      title: 'Rental Records',
      icon: 'history',
      showTabBar: true
    }
  },
  {
    path: '/my',
    name: 'My',
    component: () => import('../views/my/index.vue'),
    meta: {
      title: 'Profile',
      icon: 'user',
      showTabBar: true
    }
  },
  {
    path: '/my/register',
    name: 'Register',
    component: () => import('../views/my/register.vue'),
    meta: {
      title: 'Register',
      showTabBar: false
    }
  },
  {
    path: '/my/login',
    name: 'Login',
    component: () => import('../views/my/login.vue'),
    meta: {
      title: 'Login',
      showTabBar: false
    }
  },
  {
    path: '/my/feedback',
    name: 'FeedbackList',
    component: () => import('../views/my/feedback.vue'),
    meta: {
      title: 'FeedbackList',
      showTabBar: false
    }
  },
  {
    path: '/my/helpCenter',
    name: 'HelpCenter',
    component: () => import('../views/my/helpCenter.vue'),
    meta: {
      title: 'Help Center',
      showTabBar: false
    }
  },
  {
    path: '/my/aboutUs',
    name: 'AboutUs',
    component: () => import('../views/my/aboutUs.vue'),
    meta: {
      title: 'About Us',
      showTabBar: false
    }
  },
  {
    path: '/feedback',
    name: 'Feedback',
    component: () => import('../views/feedback/index.vue'),
    meta: {
      title: 'Feedback',
      showTabBar: false
    }
  },
  {
    path: '/payment/extend',
    name: 'ExtendPayment',
    component: () => import('../views/payment/extend-payment.vue'),
    meta: {
      title: 'Extend Payment',
      showTabBar: false
    }
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
