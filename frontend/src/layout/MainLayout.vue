<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="layout-aside">
      <div class="logo">超市耗材供应商系统</div>
      <el-menu
          :default-active="activeMenu"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff">
        <!-- 平台管理员：仅显示平台管理 -->
        <el-menu-item v-if="isPlatformAdmin" index="/platform/tenant">
          <el-icon><OfficeBuilding/></el-icon><span>商家管理</span>
        </el-menu-item>

        <template v-if="!isPlatformAdmin">
        <el-sub-menu index="basedata">
          <template #title><el-icon><Files/></el-icon><span>基础数据</span></template>
          <el-menu-item index="/goods">商品管理</el-menu-item>
          <el-menu-item index="/warehouse">库存地点</el-menu-item>
          <el-menu-item index="/customer">客户管理</el-menu-item>
          <el-menu-item index="/supplier">供应商管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="stock">
          <template #title><el-icon><Box/></el-icon><span>库存管理</span></template>
          <el-menu-item index="/stock">库存查询</el-menu-item>
          <el-menu-item index="/stock-flow">出入库流水</el-menu-item>
          <el-menu-item index="/stock-adjust">库存调整</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="purchase">
          <template #title><el-icon><ShoppingCart/></el-icon><span>进货管理</span></template>
          <el-menu-item index="/purchase">进货单列表</el-menu-item>
          <el-menu-item index="/purchase-create">新建进货单</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="sale">
          <template #title><el-icon><Sell/></el-icon><span>出货管理</span></template>
          <el-menu-item index="/sale-wholesale">批发出货</el-menu-item>
          <el-menu-item index="/sale-store">门店散卖</el-menu-item>
          <el-menu-item index="/sale">销售单列表</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="finance">
          <template #title><el-icon><Wallet/></el-icon><span>财务管理</span></template>
          <el-menu-item index="/receivable">销售收款</el-menu-item>
          <el-menu-item index="/payable">采购付款</el-menu-item>
          <el-menu-item index="/expense">费用收入</el-menu-item>
          <el-menu-item index="/fund-flow">资金流水</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/delivery-reminder">
          <el-icon><Van/></el-icon><span>送货提醒</span>
        </el-menu-item>
        <el-menu-item v-if="aiEnabled" index="/ai-assistant">
          <el-icon><ChatDotRound/></el-icon><span>智能问数</span>
        </el-menu-item>
        <el-sub-menu index="report">
          <template #title><el-icon><DataAnalysis/></el-icon><span>数据报表</span></template>
          <el-menu-item index="/report-fund">资金汇总</el-menu-item>
          <el-menu-item index="/report-expense">费用分类汇总</el-menu-item>
        </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="layout-header">
        <span class="header-title">耗材供应商管理后台</span>
        <div class="header-right">
          <span class="user-name">{{ loginName }}</span>
          <el-button link type="primary" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/store/auth'
import { authApi } from '@/api/auth'
import { aiApi } from '@/api/ai'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 当前激活菜单跟随路由
const activeMenu = computed(() => route.path)
// 是否平台管理员（控制菜单显示）
const isPlatformAdmin = computed(() => authStore.isPlatformAdmin)
// 当前登录名
const loginName = computed(() => authStore.loginName)

// 当前商家是否开通 AI（控制智能问数入口显隐）
const aiEnabled = ref(false)

// 商家登录后查询 AI 开通状态
onMounted(async () => {
  if (!authStore.isPlatformAdmin) {
    try {
      const res = await aiApi.status()
      aiEnabled.value = !!(res && res.enabled)
    } catch (e) {
      aiEnabled.value = false
    }
  }
})

// 退出登录
async function handleLogout() {
  await ElMessageBox.confirm('确认退出登录？', '提示', { type: 'warning' })
  try {
    await authApi.logout()
  } catch (e) {
    // 忽略退出接口异常，前端照常清理
  }
  authStore.clear()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.layout-aside {
  background-color: #304156;
}

.logo {
  height: 56px;
  line-height: 56px;
  color: #fff;
  text-align: center;
  font-size: 15px;
  font-weight: bold;
  background-color: #2b3a4b;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
}

.header-title {
  font-size: 16px;
  font-weight: bold;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-name {
  color: #606266;
  font-size: 14px;
}
</style>
