<template>
  <div>
    <el-form :inline="true" :model="query">
      <el-form-item label="类型">
        <el-select v-model="query.iFlowType" placeholder="全部" clearable style="width: 140px">
          <el-option v-for="o in FUND_FLOW_TYPE_OPTIONS" :key="o.value" :label="o.label" :value="o.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="日期">
        <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD"
                        start-placeholder="开始" end-placeholder="结束"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" border v-loading="loading">
      <el-table-column label="类型" width="120">
        <template #default="{ row }">
          <el-tag :type="tagTypeOf(FUND_FLOW_TYPE_OPTIONS, row.iFlowType)">
            {{ labelOf(FUND_FLOW_TYPE_OPTIONS, row.iFlowType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="方向" width="80">
        <template #default="{ row }">
          <span :class="row.iDirection === 1 ? 'income' : 'expense'">
            {{ row.iDirection === 1 ? '收入' : '支出' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="金额（元）" width="120">
        <template #default="{ row }">{{ fenToYuan(row.iAmount) }}</template>
      </el-table-column>
      <el-table-column prop="dOccurDate" label="发生日期" width="140"/>
      <el-table-column prop="sRemark" label="备注"/>
    </el-table>

    <el-pagination class="pagination" background layout="total, prev, pager, next"
                   :total="total" :page-size="query.pageSize" :current-page="query.pageNum"
                   @current-change="onPageChange"/>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { fundApi } from '@/api/finance'
import { fenToYuan } from '@/utils/money'
import { FUND_FLOW_TYPE_OPTIONS, labelOf, tagTypeOf } from '@/utils/dict'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const dateRange = ref([])
const query = reactive({ iFlowType: null, pageNum: 1, pageSize: 10 })

async function loadData() {
  loading.value = true
  try {
    const params = { ...query }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await fundApi.page(params)
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

onMounted(loadData)
</script>

<style scoped>
.pagination { margin-top: 16px; }
.income { color: #67c23a; }
.expense { color: #f56c6c; }
</style>
