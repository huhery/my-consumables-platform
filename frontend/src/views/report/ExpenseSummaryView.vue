<template>
  <div>
    <el-form :inline="true">
      <el-form-item label="类型">
        <el-select v-model="categoryType" style="width: 120px" @change="loadData">
          <el-option v-for="o in CATEGORY_TYPE_OPTIONS" :key="o.value" :label="o.label" :value="o.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="日期范围">
        <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD"
                        start-placeholder="开始" end-placeholder="结束"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" border v-loading="loading" show-summary :summary-method="getSummary">
      <el-table-column prop="sCategoryName" label="分类"/>
      <el-table-column label="合计金额（元）">
        <template #default="{ row }">{{ fenToYuan(row.iAmount) }}</template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { reportApi } from '@/api/finance'
import { fenToYuan } from '@/utils/money'
import { CATEGORY_TYPE_OPTIONS } from '@/utils/dict'

const categoryType = ref(1)
const dateRange = ref([])
const list = ref([])
const loading = ref(false)

async function loadData() {
  loading.value = true
  try {
    const params = { categoryType: categoryType.value }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    list.value = await reportApi.expenseSummary(params)
  } finally {
    loading.value = false
  }
}

// 表尾合计行
function getSummary() {
  const total = list.value.reduce((sum, r) => sum + (r.iAmount || 0), 0)
  return ['合计', fenToYuan(total) + ' 元']
}

onMounted(loadData)
</script>
