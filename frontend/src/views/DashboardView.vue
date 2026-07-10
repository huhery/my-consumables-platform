<template>
  <div class="dashboard" v-loading="loading">
    <!-- 顶部：今日收支 -->
    <el-row :gutter="16">
      <el-col :span="12">
        <el-card class="stat-card clickable" @click="goto('/fund-flow')">
          <div class="stat-label">今天收入</div>
          <div class="stat-value income">¥{{ yuan(data.todayIncome) }}</div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="stat-card clickable" @click="goto('/fund-flow')">
          <div class="stat-label">今天支出</div>
          <div class="stat-value expense">¥{{ yuan(data.todayExpense) }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 中部：欠款 / 应付 -->
    <el-row :gutter="16" class="row-gap">
      <el-col :span="12">
        <el-card class="clickable" @click="goto('/receivable')">
          <div class="card-title">别人欠我的钱</div>
          <div class="stat-value expense">¥{{ yuan(data.totalReceivable) }}</div>
          <div class="sub" v-if="data.topDebtor">
            欠最多：{{ data.topDebtor.name }}（¥{{ yuan(data.topDebtor.amount) }}）
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="clickable" @click="goto('/payable')">
          <div class="card-title">我欠供应商的钱</div>
          <div class="stat-value expense">¥{{ yuan(data.totalPayable) }}</div>
          <div class="sub" v-if="data.topCreditor">
            欠最多：{{ data.topCreditor.name }}（¥{{ yuan(data.topCreditor.amount) }}）
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 下部：补货 / 送货 -->
    <el-row :gutter="16" class="row-gap">
      <el-col :span="12">
        <el-card class="clickable" @click="goto('/stock')">
          <div class="card-title">要补货的商品（{{ data.lowStockCount || 0 }} 种）</div>
          <ul class="list" v-if="data.lowStockItems && data.lowStockItems.length">
            <li v-for="(it, i) in data.lowStockItems" :key="i">
              {{ it.goodsName }}　<span class="expense">剩 {{ it.qty }}</span>
            </li>
          </ul>
          <div class="empty" v-else>暂无库存不足的商品</div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="clickable" @click="goto('/delivery-reminder')">
          <div class="card-title">近几天要送的货（{{ data.deliveryCount || 0 }} 单）</div>
          <ul class="list" v-if="data.deliveryItems && data.deliveryItems.length">
            <li v-for="(it, i) in data.deliveryItems" :key="i">
              单号 {{ it.saleNo }}　{{ it.expectDelivery || '未指定日期' }}
            </li>
          </ul>
          <div class="empty" v-else>暂无待送货的订单</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部：本月销售额 -->
    <el-row class="row-gap">
      <el-col :span="24">
        <el-card class="stat-card">
          <div class="stat-label">本月卖了</div>
          <div class="stat-value income big">¥{{ yuan(data.monthSales) }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { dashboardApi } from '@/api/dashboard'
import { fenToYuan } from '@/utils/money'

const router = useRouter()
const loading = ref(false)
const data = ref({})

function yuan(fen) {
  return fenToYuan(fen)
}

function goto(path) {
  router.push(path)
}

async function load() {
  loading.value = true
  try {
    data.value = await dashboardApi.summary()
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.dashboard {
  max-width: 1000px;
  margin: 0 auto;
}

.row-gap {
  margin-top: 16px;
}

.stat-card {
  text-align: center;
}

.stat-label {
  font-size: 16px;
  color: #606266;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
}

.stat-value.big {
  font-size: 40px;
}

.income {
  color: #67c23a;
}

.expense {
  color: #f56c6c;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
}

.sub {
  margin-top: 8px;
  color: #909399;
  font-size: 14px;
}

.list {
  list-style: none;
  padding: 0;
  margin: 0;
  font-size: 15px;
  line-height: 2;
}

.empty {
  color: #909399;
  font-size: 14px;
}

.clickable {
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.clickable:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.12);
}
</style>
