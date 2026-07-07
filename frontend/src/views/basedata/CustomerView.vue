<template>
  <div>
    <el-form :inline="true" :model="query">
      <el-form-item label="名称">
        <el-input v-model="query.sName" placeholder="客户名称" clearable/>
      </el-form-item>
      <el-form-item label="联系人">
        <el-input v-model="query.sContact" placeholder="联系人" clearable/>
      </el-form-item>
      <el-form-item label="电话">
        <el-input v-model="query.sPhone" placeholder="电话" clearable/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openCreate">新增客户</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" border v-loading="loading">
      <el-table-column prop="sName" label="客户名称"/>
      <el-table-column prop="sContact" label="联系人" width="120"/>
      <el-table-column prop="sPhone" label="电话" width="140"/>
      <el-table-column prop="sAddress" label="收货地址"/>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pagination" background layout="total, prev, pager, next"
                   :total="total" :page-size="query.pageSize" :current-page="query.pageNum"
                   @current-change="onPageChange"/>

    <el-dialog v-model="dialogVisible" :title="form.sId ? '编辑客户' : '新增客户'" width="460px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="客户名称" required><el-input v-model="form.sName"/></el-form-item>
        <el-form-item label="联系人"><el-input v-model="form.sContact"/></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.sPhone"/></el-form-item>
        <el-form-item label="收货地址"><el-input v-model="form.sAddress" type="textarea"/></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { customerApi } from '@/api/customer'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ sName: '', sContact: '', sPhone: '', pageNum: 1, pageSize: 10 })

async function loadData() {
  loading.value = true
  try {
    const res = await customerApi.page(query)
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
const form = reactive({ sId: '', sName: '', sContact: '', sPhone: '', sAddress: '' })

function openCreate() {
  Object.assign(form, { sId: '', sName: '', sContact: '', sPhone: '', sAddress: '' })
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSave() {
  if (form.sId) {
    await customerApi.update(form.sId, form)
  } else {
    await customerApi.create(form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除客户「${row.sName}」？`, '提示', { type: 'warning' })
  await customerApi.remove(row.sId)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.pagination { margin-top: 16px; }
</style>
