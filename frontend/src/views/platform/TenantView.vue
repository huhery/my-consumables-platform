<template>
  <div>
    <el-form :inline="true" :model="query">
      <el-form-item label="商家名称">
        <el-input v-model="query.sName" placeholder="商家名称" clearable/>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.iStatus" placeholder="全部" clearable style="width: 120px">
          <el-option :value="1" label="启用"/>
          <el-option :value="2" label="停用"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openDialog">开通商家</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" border v-loading="loading">
      <el-table-column prop="sName" label="商家名称"/>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.iStatus === 1 ? 'success' : 'info'">
            {{ row.iStatus === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="dtOpenTime" label="开通时间" width="180"/>
      <el-table-column label="到期日期" width="140">
        <template #default="{ row }">
          <span :class="isExpired(row) ? 'expired' : ''">{{ formatDate(row.dExpireDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="AI 问数" width="110">
        <template #default="{ row }">
          <el-switch
              :model-value="row.iAiEnabled === 1"
              @change="(val) => toggleAi(row, val)"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button v-if="row.iStatus === 1" size="small" type="warning" @click="toggle(row, false)">停用</el-button>
          <el-button v-else size="small" type="success" @click="toggle(row, true)">启用</el-button>
          <el-button size="small" type="primary" @click="handleRenew(row)">续期</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pagination" background layout="total, prev, pager, next"
                   :total="total" :page-size="query.pageSize" :current-page="query.pageNum"
                   @current-change="onPageChange"/>

    <!-- 开通商家 -->
    <el-dialog v-model="dialogVisible" title="开通商家" width="460px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="商家名称" required>
          <el-input v-model="form.name"/>
        </el-form-item>
        <el-form-item label="初始登录名" required>
          <el-input v-model="form.loginName"/>
        </el-form-item>
        <el-form-item label="初始密码" required>
          <el-input v-model="form.password" type="password" show-password/>
        </el-form-item>
        <el-form-item label="有效期（年）">
          <el-input-number v-model="form.expireYears" :min="1" :max="10"/>
        </el-form-item>
        <el-form-item label="开通 AI 问数">
          <el-switch v-model="form.aiEnabled"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">开通</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { platformApi } from '@/api/auth'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ sName: '', iStatus: null, pageNum: 1, pageSize: 10 })

async function loadData() {
  loading.value = true
  try {
    const res = await platformApi.page(query)
    list.value = res.list
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function onPageChange(page) {
  query.pageNum = page
  loadData()
}

const dialogVisible = ref(false)
const form = reactive({ name: '', loginName: '', password: '', expireYears: 1, aiEnabled: false })

function openDialog() {
  Object.assign(form, { name: '', loginName: '', password: '', expireYears: 1, aiEnabled: false })
  dialogVisible.value = true
}

async function submit() {
  if (!form.name || !form.loginName || !form.password) {
    ElMessage.warning('请完整填写')
    return
  }
  await platformApi.openTenant(form)
  ElMessage.success('开通成功')
  dialogVisible.value = false
  loadData()
}

async function toggle(row, enable) {
  const action = enable ? '启用' : '停用'
  await ElMessageBox.confirm(`确认${action}商家「${row.sName}」？`, '提示', { type: 'warning' })
  if (enable) {
    await platformApi.enable(row.sId)
  } else {
    await platformApi.disable(row.sId)
  }
  ElMessage.success(`${action}成功`)
  loadData()
}

async function toggleAi(row, val) {
  try {
    await platformApi.setAi(row.sId, val)
    row.iAiEnabled = val ? 1 : 0
    ElMessage.success(val ? '已开通 AI 问数' : '已关闭 AI 问数')
  } catch (e) {
    // 失败时刷新以恢复真实状态
    loadData()
  }
}

async function handleRenew(row) {
  const { value } = await ElMessageBox.prompt('续期年数', '续期', {
    inputValue: '1', inputPattern: /^[1-9]\d*$/, inputErrorMessage: '请输入正整数',
    confirmButtonText: '确认续期', cancelButtonText: '取消'
  })
  await platformApi.renew(row.sId, parseInt(value))
  ElMessage.success('续期成功')
  loadData()
}

function formatDate(d) {
  if (!d) return '-'
  return String(d).slice(0, 10)
}

function isExpired(row) {
  if (!row.dExpireDate) return false
  return new Date(row.dExpireDate) < new Date()
}

onMounted(loadData)
</script>

<style scoped>
.pagination { margin-top: 16px; }
.expired { color: #f56c6c; font-weight: bold; }
</style>
