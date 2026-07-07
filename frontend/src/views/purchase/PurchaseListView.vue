<template>
  <div>
    <el-form :inline="true" :model="query">
      <el-form-item label="供应商">
        <el-select v-model="query.sSupplierId" placeholder="全部" clearable filterable style="width: 180px">
          <el-option v-for="s in supplierOptions" :key="s.sId" :label="s.sName" :value="s.sId"/>
        </el-select>
      </el-form-item>
      <el-form-item label="入库地点">
        <el-select v-model="query.sWarehouseId" placeholder="全部" clearable style="width: 160px">
          <el-option v-for="w in warehouseOptions" :key="w.sId" :label="w.sName" :value="w.sId"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" border v-loading="loading">
      <el-table-column prop="sPurchaseNo" label="进货单号" width="200"/>
      <el-table-column label="供应商">
        <template #default="{ row }">{{ supplierName(row.sSupplierId) }}</template>
      </el-table-column>
      <el-table-column label="入库地点">
        <template #default="{ row }">{{ warehouseName(row.sWarehouseId) }}</template>
      </el-table-column>
      <el-table-column label="总金额（元）" width="120">
        <template #default="{ row }">{{ fenToYuan(row.iTotalAmount) }}</template>
      </el-table-column>
      <el-table-column prop="dtPurchaseTime" label="进货时间" width="180"/>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button size="small" @click="showDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pagination" background layout="total, prev, pager, next"
                   :total="total" :page-size="query.pageSize" :current-page="query.pageNum"
                   @current-change="onPageChange"/>

    <el-dialog v-model="detailVisible" title="进货单详情" width="640px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="单号">{{ detail.purchase?.sPurchaseNo }}</el-descriptions-item>
        <el-descriptions-item label="总金额（元）">{{ fenToYuan(detail.purchase?.iTotalAmount) }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detail.items" border style="margin-top: 12px">
        <el-table-column label="商品">
          <template #default="{ row }">{{ goodsName(row.sGoodsId) }}</template>
        </el-table-column>
        <el-table-column label="录入" width="120">
          <template #default="{ row }">{{ row.iInputQty }} {{ row.sInputUnit }}</template>
        </el-table-column>
        <el-table-column prop="iQtyBase" label="基本单位数量" width="120"/>
        <el-table-column label="进价（元）" width="100">
          <template #default="{ row }">{{ fenToYuan(row.iPrice) }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { purchaseApi } from '@/api/purchase'
import { supplierApi } from '@/api/supplier'
import { warehouseApi } from '@/api/warehouse'
import { goodsApi } from '@/api/goods'
import { fenToYuan } from '@/utils/money'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ sSupplierId: '', sWarehouseId: '', pageNum: 1, pageSize: 10 })

const supplierOptions = ref([])
const warehouseOptions = ref([])
const goodsOptions = ref([])

function supplierName(id) {
  const s = supplierOptions.value.find((x) => x.sId === id)
  return s ? s.sName : id
}

function warehouseName(id) {
  const w = warehouseOptions.value.find((x) => x.sId === id)
  return w ? w.sName : id
}

function goodsName(id) {
  const g = goodsOptions.value.find((x) => x.sId === id)
  return g ? g.sName : id
}

async function loadOptions() {
  supplierOptions.value = (await supplierApi.page({ pageNum: 1, pageSize: 1000 })).list
  warehouseOptions.value = (await warehouseApi.page({ pageNum: 1, pageSize: 1000 })).list
  goodsOptions.value = (await goodsApi.page({ pageNum: 1, pageSize: 1000 })).list
}

async function loadData() {
  loading.value = true
  try {
    const res = await purchaseApi.page(query)
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

const detailVisible = ref(false)
const detail = reactive({ purchase: null, items: [] })

async function showDetail(row) {
  const res = await purchaseApi.detail(row.sId)
  detail.purchase = res.purchase
  detail.items = res.items
  detailVisible.value = true
}

onMounted(async () => {
  await loadOptions()
  await loadData()
})
</script>

<style scoped>
.pagination { margin-top: 16px; }
</style>
