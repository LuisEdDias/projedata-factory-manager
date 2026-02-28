import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/raw-materials',
      name: 'rawMaterials',
      component: () => import('../views/RawMaterialsView.vue'),
    },
  ],
})

export default router
