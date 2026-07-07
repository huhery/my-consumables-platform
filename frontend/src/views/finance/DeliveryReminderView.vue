<template>
  <div>
    <el-card>
      <template #header>送货提醒（待发货批发单，按期望送达日期排序）</template>
      <el-table :data="list" border v-loading="loading">
        <el-table-column prop="sSaleNo" label="销售单号" width="200"/>
        <el-table-column label="客户">
          <template #default="{ row }">{{ customerName(row.sCustomerId) }}</template>
        </el-table-column>
        <el-table-column label="金额（元）" width="120">
          <template #default="{ row }">{{ fenToYuan(row.iTotalAmount) }}</template>
        </el-table-column>
        <el-table-column label="期望送达" width="140">
          <template #default="{ row }">{{ formatExpect(row.dExpectDelivery) }}</template>
        </el-table-column>
        <el-table-column prop="dtCreateTime" label="下单时间" width="180"/>
      </el-table>
      <el-empty v-if="!loading && list.length === 0" description="暂无待发货单据"/>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { deliveryApi } from '@/api/finance'
import { customerApi } from '@/api/customer'
import { fenToYuan } from '@/utils/money'

const list = ref([])
const loading = ref(false)
const customers = ref([])

function customerName(id) {
  const c = customers.value.find((x) => x.sId === id)
  return c ? c.sName : id
}

// 期望送达日期为占位日期（1970）时显示未填
function formatExpect(date) {
  if (!date) {
    return '未填'
  }
  const d = String(date)
  return d.startsWith('1970') ? '未填' : d.slice(0, 10)
}

async function loadData() {
  loading.value = true
  try {
    customers.value = (await customerApi.page({ pageNum: 1, pageSize: 1000 })).list
    list.value = await deliveryApi.list()
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
