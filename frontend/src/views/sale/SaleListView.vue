<template>
  <div>
    <el-form :inline="true" :model="query">
      <el-form-item label="类型">
        <el-select v-model="query.iSaleType" placeholder="全部" clearable style="width: 140px">
          <el-option v-for="o in SALE_TYPE_OPTIONS" :key="o.value" :label="o.label" :value="o.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.iStatus" placeholder="全部" clearable style="width: 120px">
          <el-option v-for="o in SALE_STATUS_OPTIONS" :key="o.value" :label="o.label" :value="o.value"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" border v-loading="loading">
      <el-table-column prop="sSaleNo" label="销售单号" width="200"/>
      <el-table-column label="类型" width="120">
        <template #default="{ row }">{{ labelOf(SALE_TYPE_OPTIONS, row.iSaleType) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="tagTypeOf(SALE_STATUS_OPTIONS, row.iStatus)">
            {{ labelOf(SALE_STATUS_OPTIONS, row.iStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="总金额（元）" width="120">
        <template #default="{ row }">{{ fenToYuan(row.iTotalAmount) }}</template>
      </el-table-column>
      <el-table-column prop="dtCreateTime" label="创建时间" width="180"/>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="showDetail(row)">详情</el-button>
          <el-button v-if="row.iStatus === 1" size="small" type="primary" @click="handleShip(row)">发货</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pagination" background layout="total, prev, pager, next"
                   :total="total" :page-size="query.pageSize" :current-page="query.pageNum"
                   @current-change="onPageChange"/>

    <el-dialog v-model="detailVisible" title="销售单详情" width="640px">
      <el-table :data="detail.items" border>
        <el-table-column label="商品">
          <template #default="{ row }">{{ goodsName(row.sGoodsId) }}</template>
        </el-table-column>
        <el-table-column label="录入" width="120">
          <template #default="{ row }">{{ row.iInputQty }} {{ row.sInputUnit }}</template>
        </el-table-column>
        <el-table-column prop="iQtyBase" label="基本单位数量" width="120"/>
        <el-table-column label="售价（元）" width="100">
          <template #default="{ row }">{{ fenToYuan(row.iPrice) }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { saleApi } from '@/api/sale'
import { goodsApi } from '@/api/goods'
import { fenToYuan } from '@/utils/money'
import { SALE_TYPE_OPTIONS, SALE_STATUS_OPTIONS, labelOf, tagTypeOf } from '@/utils/dict'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ iSaleType: null, iStatus: null, pageNum: 1, pageSize: 10 })
const goodsOptions = ref([])

function goodsName(id) {
  const g = goodsOptions.value.find((x) => x.sId === id)
  return g ? g.sName : id
}

async function loadData() {
  loading.value = true
  try {
    const res = await saleApi.page(query)
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

async function handleShip(row) {
  await ElMessageBox.confirm(`确认对单号「${row.sSaleNo}」发货？库存将被扣减。`, '发货确认', { type: 'warning' })
  await saleApi.ship(row.sId)
  ElMessage.success('发货成功')
  loadData()
}

const detailVisible = ref(false)
const detail = reactive({ saleOrder: null, items: [] })

async function showDetail(row) {
  const res = await saleApi.detail(row.sId)
  detail.saleOrder = res.saleOrder
  detail.items = res.items
  detailVisible.value = true
}

onMounted(async () => {
  goodsOptions.value = (await goodsApi.page({ pageNum: 1, pageSize: 1000 })).list
  await loadData()
})
</script>

<style scoped>
.pagination { margin-top: 16px; }
</style>
