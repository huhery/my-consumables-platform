<template>
  <div class="login-wrap">
    <el-card class="login-card">
      <div class="title">超市耗材供应商平台</div>
      <el-form :model="form" @keyup.enter="submit">
        <el-form-item>
          <el-input v-model="form.loginName" placeholder="登录名" size="large" :prefix-icon="User"/>
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" size="large"
                    :prefix-icon="Lock" show-password/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="submit">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { authApi } from '@/api/auth'
import { useAuthStore, ROLE_PLATFORM_ADMIN } from '@/store/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const form = reactive({ loginName: '', password: '' })

async function submit() {
  if (!form.loginName || !form.password) {
    ElMessage.warning('请输入登录名与密码')
    return
  }
  loading.value = true
  try {
    const result = await authApi.login(form)
    authStore.setAuth(result)
    ElMessage.success('登录成功')
    // 按角色分流：平台管理员进平台后台，商家进业务界面
    if (result.role === ROLE_PLATFORM_ADMIN) {
      router.push('/platform/tenant')
    } else {
      router.push('/stock')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrap {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1f2d3d, #409eff);
}

.login-card {
  width: 380px;
  padding: 20px 10px;
}

.title {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 24px;
  color: #303133;
}
</style>
