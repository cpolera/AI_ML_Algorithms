import { createRouter, createWebHistory } from 'vue-router'
import { navigationGuard } from '@okta/okta-vue'
import Home from '@/components/Home';
import QAP from '@/components/QAP';
import { LoginCallback } from '@okta/okta-vue'

const routes = [
  {
    path: '/',
    component: Home
  },
  { 
    path: '/callback', 
    component: LoginCallback 
  },
  {
    path: '/qap',
    component: QAP,
    meta: {
      requiresAuth: false
    }
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
})

router.beforeEach(navigationGuard)

export default router
