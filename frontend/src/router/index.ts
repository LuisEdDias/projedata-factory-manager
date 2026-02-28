import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/raw-materials',
      name: 'rawMaterials',
      component: () => import('../views/RawMaterialsView.vue'),
    },
    {
      path: '/products',
      name: 'products',
      component: () => import('../views/ProductsView.vue'),
    },
    {
      path: '/products/:code',
      name: 'product-details',
      component: () => import('@/views/ProductDetailsView.vue'),
    },
  ],
})

export default router
