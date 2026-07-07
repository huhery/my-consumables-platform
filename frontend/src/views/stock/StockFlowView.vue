<template>
  <div>
    <el-form :inline="true" :model="query">
      <el-form-item label="商品">
        <el-select v-model="query.sGoodsId" placeholder="全部商品" clearable filterable style="width: 180px">
          <el-option v-for="g in goodsOptions" :key="g.sId" :label="g.sName" :value="g.sId"/>
        </el-select>
      </el-form-item>
      <el-form-item label="地点">
        <el-select v-model="query.sWarehouseId" placeholder="全部地点" clearable style="width: 160px">
          <el-option v-for="w in warehouseOptions" :key="w.sId" :label="w.sName" :value="w.sId"/>
        </el-select>
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="query.iFlowType" placeholder="全部" clearable style="width: 140px">
          <el-option v-for="o in FLOW_TYPE_OPTIONS" :key="o.value" :label="o.label" :value="o.value"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" border v-loading="loading">
      <el-table-column label="商品">
        <template #default="{ row }">{{ goodsName(row.sGoodsId) }}</template>
      </el-table-column>
      <el-table-column label="地点">
        <template #default="{ row }">{{ warehouseName(row.sWarehouseId) }}</template>
      </el-table-column>
      <el-table-column label="类型" width="140">
        <template #default="{ row }">
          <el-tag :type="tagTypeOf(FLOW_TYPE_OPTIONS, row.iFlowType)">
            {{ labelOf(FLOW_TYPE_OPTIONS, row.iFlowType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="iChangeQty" label="变动数量" width="100"/>
      <el-table-column prop="sRemark" label="备注"/>
      <el-table-column prop="dtCreateTime" label="时间" width="180"/>
    </el-table>

    <el-pagination class="pagination" background layout="total, prev, pager, next"
                   :total="total" :page-size="query.pageSize" :current-page="query.pageNum"
                   @current-change="onPageChange"/>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { stockApi } from '@/api/stock'
import { goodsApi } from '@/api/goods'
import { warehouseApi } from '@/api/warehouse'
import { FLOW_TYPE_OPTIONS, labelOf, tagTypeOf } from '@/utils/dict'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ sGoodsId: '', sWarehouseId: '', iFlowType: null, pageNum: 1, pageSize: 10 })

const goodsOptions = ref([])
const warehouseOptions = ref([])

function goodsName(id) {
  const g = goodsOptions.value.find((x) => x.sId === id)
  return g ? g.sName : id
}

function warehouseName(id) {
  const w = warehouseOptions.value.find((x) => x.sId === id)
  return w ? w.sName : id
}

async function loadOptions() {
  const g = await goodsApi.page({ pageNum: 1, pageSize: 1000 })
  goodsOptions.value = g.list
  const w = await warehouseApi.page({ pageNum: 1, pageSize: 1000 })
  warehouseOptions.value = w.list
}

async function loadData() {
  loading.value = true
  try {
    const res = await stockApi.flowPage(query)
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

onMounted(async () => {
  await loadOptions()
  await loadData()
})
</script>

<style scoped>
.pagination { margin-top: 16px; }
</style>
