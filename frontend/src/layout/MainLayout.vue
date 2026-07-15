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
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled/></el-icon><span>工作台</span>
        </el-menu-item>
        <el-menu-item index="/sale-wholesale">
          <el-icon><Sell/></el-icon><span>销售单</span>
        </el-menu-item>
        <el-menu-item index="/receivable">
          <el-icon><Wallet/></el-icon><span>收款</span>
        </el-menu-item>
        <el-menu-item index="/customer">
          <el-icon><User/></el-icon><span>客户</span>
        </el-menu-item>
        <el-menu-item index="/supplier">
          <el-icon><Suitcase/></el-icon><span>供应商</span>
        </el-menu-item>
        <el-menu-item index="/purchase-create">
          <el-icon><ShoppingCart/></el-icon><span>采购单</span>
        </el-menu-item>
        <el-menu-item index="/goods">
          <el-icon><Goods/></el-icon><span>产品</span>
        </el-menu-item>
        <el-menu-item index="/warehouse">
          <el-icon><OfficeBuilding/></el-icon><span>仓库</span>
        </el-menu-item>
        <el-menu-item index="/stock">
          <el-icon><Box/></el-icon><span>库存</span>
        </el-menu-item>
        <el-menu-item index="/expense">
          <el-icon><Money/></el-icon><span>费用收入</span>
        </el-menu-item>
        <el-menu-item index="/fund-flow">
          <el-icon><DataAnalysis/></el-icon><span>账本</span>
        </el-menu-item>
        <el-menu-item index="/delivery-reminder">
          <el-icon><Van/></el-icon><span>送货提醒</span>
        </el-menu-item>
        <el-menu-item v-if="aiEnabled" index="/ai-assistant">
          <el-icon><ChatDotRound/></el-icon><span>智能问数</span>
        </el-menu-item>
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
  try {
    await ElMessageBox.confirm('确认退出登录？', '提示', { type: 'warning' })
  } catch (e) {
    return // 用户点了取消
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
