import Vue from 'vue'
import Router from 'vue-router'
import Home from '@/components/Home.vue'
import Login from '@/components/Login.vue'
import Organization from '@/components/Organization.vue'

Vue.use(Router)

const isAuthenticated = (to, from, next) => {
  if (localStorage.getItem('token') != null) {
    next()
    return
  }
  next('/login')
}

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home
    },
    {
      path: '/login',
      name: 'Login',
      component: Login,
      meta: { 
        guest: true
      }
    },
    {
      path: '/organization',
      name: 'Organization',
      component: Organization,
      beforeEnter: isAuthenticated,
      meta: {
        requiresAuth: true
      }
    }
  ]
})