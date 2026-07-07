<template>
  <div>
    <el-form :inline="true">
      <el-form-item label="日期范围">
        <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD"
                        start-placeholder="开始" end-placeholder="结束"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="16" v-loading="loading">
      <el-col :span="8">
        <el-card><div class="label">收入合计</div><div class="value income">{{ fenToYuan(data.incomeTotal) }} 元</div></el-card>
      </el-col>
      <el-col :span="8">
        <el-card><div class="label">支出合计</div><div class="value expense">{{ fenToYuan(data.expenseTotal) }} 元</div></el-card>
      </el-col>
      <el-col :span="8">
        <el-card><div class="label">净额</div><div class="value">{{ fenToYuan(data.netAmount) }} 元</div></el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" style="margin-top: 16px" v-loading="loading">
      <el-col :span="12">
        <el-card><div class="label">应收未收（欠款）</div><div class="value debt">{{ fenToYuan(data.unreceivedTotal) }} 元</div></el-card>
      </el-col>
      <el-col :span="12">
        <el-card><div class="label">应付未付</div><div class="value debt">{{ fenToYuan(data.unpaidTotal) }} 元</div></el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { reportApi } from '@/api/finance'
import { fenToYuan } from '@/utils/money'

const dateRange = ref([])
const loading = ref(false)
const data = reactive({ incomeTotal: 0, expenseTotal: 0, netAmount: 0, unreceivedTotal: 0, unpaidTotal: 0 })

async function loadData() {
  loading.value = true
  try {
    const params = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await reportApi.fundSummary(params)
    Object.assign(data, res)
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.label { color: #909399; font-size: 14px; }
.value { font-size: 24px; font-weight: bold; margin-top: 8px; }
.income { color: #67c23a; }
.expense { color: #e6a23c; }
.debt { color: #f56c6c; }
</style>
