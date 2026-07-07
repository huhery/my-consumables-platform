<template>
  <div>
    <el-form :inline="true" :model="query">
      <el-form-item label="名称">
        <el-input v-model="query.sName" placeholder="地点名称" clearable/>
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="query.iLocationType" placeholder="全部" clearable style="width: 120px">
          <el-option v-for="o in LOCATION_TYPE_OPTIONS" :key="o.value" :label="o.label" :value="o.value"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openCreate">新增地点</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" border v-loading="loading">
      <el-table-column prop="sCode" label="编码" width="140"/>
      <el-table-column prop="sName" label="名称"/>
      <el-table-column label="类型" width="120">
        <template #default="{ row }">{{ labelOf(LOCATION_TYPE_OPTIONS, row.iLocationType) }}</template>
      </el-table-column>
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

    <el-dialog v-model="dialogVisible" :title="form.sId ? '编辑地点' : '新增地点'" width="460px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="地点编码" required><el-input v-model="form.sCode"/></el-form-item>
        <el-form-item label="地点名称" required><el-input v-model="form.sName"/></el-form-item>
        <el-form-item label="地点类型" required>
          <el-select v-model="form.iLocationType" placeholder="请选择">
            <el-option v-for="o in LOCATION_TYPE_OPTIONS" :key="o.value" :label="o.label" :value="o.value"/>
          </el-select>
        </el-form-item>
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
import { warehouseApi } from '@/api/warehouse'
import { LOCATION_TYPE_OPTIONS, labelOf } from '@/utils/dict'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ sName: '', iLocationType: null, pageNum: 1, pageSize: 10 })

async function loadData() {
  loading.value = true
  try {
    const res = await warehouseApi.page(query)
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
const form = reactive({ sId: '', sCode: '', sName: '', iLocationType: 1 })

function openCreate() {
  Object.assign(form, { sId: '', sCode: '', sName: '', iLocationType: 1 })
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSave() {
  if (form.sId) {
    await warehouseApi.update(form.sId, form)
  } else {
    await warehouseApi.create(form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除「${row.sName}」？`, '提示', { type: 'warning' })
  await warehouseApi.remove(row.sId)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.pagination { margin-top: 16px; }
</style>
