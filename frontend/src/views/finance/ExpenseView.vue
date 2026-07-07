<template>
  <div>
    <el-card>
      <template #header>
        <div class="header">
          <span>费用支出 / 其他收入记账</span>
          <el-button size="small" @click="categoryVisible = true">管理分类</el-button>
        </div>
      </template>
      <el-form :model="form" label-width="100px" style="max-width: 520px">
        <el-form-item label="类型" required>
          <el-radio-group v-model="form.type" @change="onTypeChange">
            <el-radio :value="1">费用支出</el-radio>
            <el-radio :value="2">其他收入</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="form.sCategoryId" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="c in categoryOptions" :key="c.sId" :label="c.sName" :value="c.sId"/>
          </el-select>
        </el-form-item>
        <el-form-item label="金额（元）" required>
          <el-input-number v-model="form.amountYuan" :min="0.01" :precision="2" :step="1" style="width: 100%"/>
        </el-form-item>
        <el-form-item label="发生日期" required>
          <el-date-picker v-model="form.dOccurDate" type="date" value-format="YYYY-MM-DD" style="width: 100%"/>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.sRemark" type="textarea"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">保存记账</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 分类管理 -->
    <el-dialog v-model="categoryVisible" title="费用/收入分类管理" width="560px" @open="loadAllCategories">
      <el-form :inline="true" :model="catForm">
        <el-form-item label="名称">
          <el-input v-model="catForm.sName" placeholder="分类名称"/>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="catForm.iCategoryType" style="width: 100px">
            <el-option v-for="o in CATEGORY_TYPE_OPTIONS" :key="o.value" :label="o.label" :value="o.value"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="addCategory">添加</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="allCategories" border>
        <el-table-column prop="sName" label="名称"/>
        <el-table-column label="类型" width="100">
          <template #default="{ row }">{{ labelOf(CATEGORY_TYPE_OPTIONS, row.iCategoryType) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button size="small" type="danger" @click="removeCategory(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { expenseApi } from '@/api/finance'
import { yuanToFen } from '@/utils/money'
import { CATEGORY_TYPE_OPTIONS, labelOf } from '@/utils/dict'

const form = reactive({
  type: 1, sCategoryId: '', amountYuan: 0,
  dOccurDate: new Date().toISOString().slice(0, 10), sRemark: ''
})
const categoryOptions = ref([])

async function loadCategories() {
  categoryOptions.value = await expenseApi.listCategory(form.type)
  form.sCategoryId = ''
}

function onTypeChange() {
  loadCategories()
}

async function submit() {
  if (!form.sCategoryId) {
    ElMessage.warning('请选择分类')
    return
  }
  if (!form.amountYuan || form.amountYuan <= 0) {
    ElMessage.warning('请填写金额')
    return
  }
  const payload = {
    sCategoryId: form.sCategoryId,
    iAmount: yuanToFen(form.amountYuan),
    dOccurDate: form.dOccurDate,
    sRemark: form.sRemark
  }
  if (form.type === 1) {
    await expenseApi.recordExpense(payload)
  } else {
    await expenseApi.recordIncome(payload)
  }
  ElMessage.success('记账成功')
  form.amountYuan = 0
  form.sRemark = ''
}

// 分类管理
const categoryVisible = ref(false)
const allCategories = ref([])
const catForm = reactive({ sName: '', iCategoryType: 1 })

async function loadAllCategories() {
  allCategories.value = await expenseApi.listCategory()
}

async function addCategory() {
  if (!catForm.sName) {
    ElMessage.warning('请填写分类名称')
    return
  }
  await expenseApi.createCategory({ sName: catForm.sName, iCategoryType: catForm.iCategoryType })
  ElMessage.success('添加成功')
  catForm.sName = ''
  loadAllCategories()
  loadCategories()
}

async function removeCategory(row) {
  await expenseApi.removeCategory(row.sId)
  ElMessage.success('删除成功')
  loadAllCategories()
  loadCategories()
}

onMounted(loadCategories)
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
