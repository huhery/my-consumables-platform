import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layout/MainLayout.vue'
import { useAuthStore, ROLE_PLATFORM_ADMIN } from '@/store/auth'

const routes = [
  {
    path: '/login',
    component: () => import('@/views/LoginView.vue'),
    meta: { anonymous: true }
  },
  {
    path: '/',
    component: MainLayout,
    redirect: '/stock',
    children: [
      // 平台管理（平台管理员）
      { path: 'platform/tenant', component: () => import('@/views/platform/TenantView.vue'), meta: { platformOnly: true } },
      // 基础数据
      { path: 'goods', component: () => import('@/views/basedata/GoodsView.vue') },
      { path: 'warehouse', component: () => import('@/views/basedata/WarehouseView.vue') },
      { path: 'customer', component: () => import('@/views/basedata/CustomerView.vue') },
      { path: 'supplier', component: () => import('@/views/basedata/SupplierView.vue') },
      { path: 'stock', component: () => import('@/views/stock/StockView.vue') },
      { path: 'stock-flow', component: () => import('@/views/stock/StockFlowView.vue') },
      { path: 'stock-adjust', component: () => import('@/views/stock/StockAdjustView.vue') },
      { path: 'purchase', component: () => import('@/views/purchase/PurchaseListView.vue') },
      { path: 'purchase-create', component: () => import('@/views/purchase/PurchaseCreateView.vue') },
      { path: 'sale-wholesale', component: () => import('@/views/sale/WholesaleView.vue') },
      { path: 'sale-store', component: () => import('@/views/sale/StoreSaleView.vue') },
      { path: 'sale', component: () => import('@/views/sale/SaleListView.vue') },
      { path: 'receivable', component: () => import('@/views/finance/ReceivableView.vue') },
      { path: 'payable', component: () => import('@/views/finance/PayableView.vue') },
      { path: 'expense', component: () => import('@/views/finance/ExpenseView.vue') },
      { path: 'fund-flow', component: () => import('@/views/finance/FundFlowView.vue') },
      { path: 'delivery-reminder', component: () => import('@/views/finance/DeliveryReminderView.vue') },
      { path: 'report-fund', component: () => import('@/views/report/FundSummaryView.vue') },
      { path: 'report-expense', component: () => import('@/views/report/ExpenseSummaryView.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录跳登录页；平台专属页仅平台管理员可进
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  if (to.meta.anonymous) {
    next()
    return
  }
  if (!authStore.isLoggedIn) {
    next('/login')
    return
  }
  if (to.meta.platformOnly && !authStore.isPlatformAdmin) {
    next('/stock')
    return
  }
  next()
})

export default router
