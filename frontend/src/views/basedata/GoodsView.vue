<template>
  <div>
    <!-- 筛选区 -->
    <el-form :inline="true" :model="query">
      <el-form-item label="商品编码">
        <el-input v-model="query.sCode" placeholder="编码" clearable/>
      </el-form-item>
      <el-form-item label="商品名称">
        <el-input v-model="query.sName" placeholder="名称" clearable/>
      </el-form-item>
      <el-form-item label="分类">
        <el-input v-model="query.sCategory" placeholder="分类" clearable/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openCreate">新增商品</el-button>
      </el-form-item>
    </el-form>

    <!-- 列表 -->
    <el-table :data="list" border v-loading="loading">
      <el-table-column prop="sCode" label="编码" width="140"/>
      <el-table-column prop="sName" label="名称"/>
      <el-table-column prop="sCategory" label="分类" width="120"/>
      <el-table-column prop="sSpec" label="规格" width="120"/>
      <el-table-column prop="sBaseUnit" label="基本单位" width="100"/>
      <el-table-column label="操作" width="240">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="warning" @click="openUnits(row)">包装单位</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        class="pagination"
        background
        layout="total, prev, pager, next"
        :total="total"
        :page-size="query.pageSize"
        :current-page="query.pageNum"
        @current-change="onPageChange"/>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.sId ? '编辑商品' : '新增商品'" width="500px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="商品编码" required>
          <el-input v-model="form.sCode"/>
        </el-form-item>
        <el-form-item label="商品名称" required>
          <el-input v-model="form.sName"/>
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="form.sCategory"/>
        </el-form-item>
        <el-form-item label="规格">
          <el-input v-model="form.sSpec"/>
        </el-form-item>
        <el-form-item label="基本单位" required>
          <el-input v-model="form.sBaseUnit" placeholder="如：个、卷"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 包装单位弹窗 -->
    <el-dialog v-model="unitDialogVisible" title="包装单位配置" width="560px">
      <el-form :inline="true" :model="unitForm">
        <el-form-item label="单位名称">
          <el-input v-model="unitForm.sUnitName" placeholder="如：箱"/>
        </el-form-item>
        <el-form-item label="换算率">
          <el-input-number v-model="unitForm.iConvertRate" :min="1"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleAddUnit">添加</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="unitList" border>
        <el-table-column prop="sUnitName" label="包装单位"/>
        <el-table-column label="换算关系">
          <template #default="{ row }">
            1 {{ row.sUnitName }} = {{ row.iConvertRate }} {{ currentGoods.sBaseUnit }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="handleDeleteUnit(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { goodsApi } from '@/api/goods'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ sCode: '', sName: '', sCategory: '', pageNum: 1, pageSize: 10 })

// 加载列表
async function loadData() {
  loading.value = true
  try {
    const res = await goodsApi.page(query)
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

// 新增/编辑表单
const dialogVisible = ref(false)
const form = reactive({ sId: '', sCode: '', sName: '', sCategory: '', sSpec: '', sBaseUnit: '' })

function resetForm() {
  Object.assign(form, { sId: '', sCode: '', sName: '', sCategory: '', sSpec: '', sBaseUnit: '' })
}

function openCreate() {
  resetForm()
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSave() {
  if (form.sId) {
    await goodsApi.update(form.sId, form)
  } else {
    await goodsApi.create(form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除商品「${row.sName}」？`, '提示', { type: 'warning' })
  await goodsApi.remove(row.sId)
  ElMessage.success('删除成功')
  loadData()
}

// 包装单位
const unitDialogVisible = ref(false)
const currentGoods = reactive({ sId: '', sBaseUnit: '' })
const unitList = ref([])
const unitForm = reactive({ sUnitName: '', iConvertRate: 1 })

async function openUnits(row) {
  Object.assign(currentGoods, row)
  await loadUnits()
  unitDialogVisible.value = true
}

async function loadUnits() {
  unitList.value = await goodsApi.listUnits(currentGoods.sId)
}

async function handleAddUnit() {
  if (!unitForm.sUnitName) {
    ElMessage.warning('请填写单位名称')
    return
  }
  await goodsApi.addUnit(currentGoods.sId, unitForm)
  ElMessage.success('添加成功')
  unitForm.sUnitName = ''
  unitForm.iConvertRate = 1
  loadUnits()
}

async function handleDeleteUnit(row) {
  await goodsApi.removeUnit(row.sId)
  ElMessage.success('删除成功')
  loadUnits()
}

onMounted(loadData)
</script>

<style scoped>
.pagination {
  margin-top: 16px;
}
</style>
