<template>
  <div>
    <el-card>
      <template #header>新建进货单（保存即入库）</template>
      <el-form :model="form" label-width="90px">
        <el-form-item label="供应商" required>
          <el-select v-model="form.sSupplierId" placeholder="请选择" filterable style="width: 260px">
            <el-option v-for="s in supplierOptions" :key="s.sId" :label="s.sName" :value="s.sId"/>
          </el-select>
        </el-form-item>
        <el-form-item label="入库地点" required>
          <el-select v-model="form.sWarehouseId" placeholder="请选择" style="width: 260px">
            <el-option v-for="w in warehouseOptions" :key="w.sId" :label="w.sName" :value="w.sId"/>
          </el-select>
        </el-form-item>
      </el-form>

      <el-table :data="form.items" border>
        <el-table-column label="商品" min-width="180">
          <template #default="{ row }">
            <el-select v-model="row.sGoodsId" placeholder="选择商品" filterable
                       @change="onGoodsChange(row)" style="width: 100%">
              <el-option v-for="g in goodsOptions" :key="g.sId" :label="`[${g.sCategory || ''}] ${g.sName} ${g.sSpec || ''}`" :value="g.sId"/>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="单位" width="120">
          <template #default="{ row }">
            <el-select v-model="row.sInputUnit" placeholder="单位" @change="onUnitChange(row)" style="width: 100%">
              <el-option v-for="u in row.unitOptions" :key="u.name" :label="u.name" :value="u.name"/>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="数量" width="120">
          <template #default="{ row }">
            <el-input-number v-model="row.iInputQty" :min="1" @change="recalc(row)" style="width: 100%"/>
          </template>
        </el-table-column>
        <el-table-column label="折算（基本单位）" width="130">
          <template #default="{ row }">{{ row.iInputQty * row.rate }}</template>
        </el-table-column>
        <el-table-column label="进价（元/基本单位）" width="150">
          <template #default="{ row }">
            <el-input-number v-model="row.priceYuan" :min="0" :precision="2" :step="0.1" style="width: 100%"/>
          </template>
        </el-table-column>
        <el-table-column label="行小计（元）" width="120">
          <template #default="{ row }">{{ (row.iInputQty * row.rate * row.priceYuan).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="{ $index }">
            <el-button size="small" type="danger" @click="removeItem($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="actions">
        <el-button @click="addItem">添加明细</el-button>
        <span class="total">合计：{{ totalYuan }} 元</span>
        <el-button type="primary" @click="handleSubmit">保存进货单</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { purchaseApi } from '@/api/purchase'
import { supplierApi } from '@/api/supplier'
import { warehouseApi } from '@/api/warehouse'
import { goodsApi } from '@/api/goods'
import { yuanToFen } from '@/utils/money'

const supplierOptions = ref([])
const warehouseOptions = ref([])
const goodsOptions = ref([])

const form = reactive({ sSupplierId: '', sWarehouseId: '', items: [] })

function newItem() {
  return { sGoodsId: '', sInputUnit: '', iInputQty: 1, priceYuan: 0, rate: 1, unitOptions: [] }
}

function addItem() {
  form.items.push(newItem())
}

function removeItem(index) {
  form.items.splice(index, 1)
}

// 选择商品后加载其可用单位（基本单位 + 包装单位）
async function onGoodsChange(row) {
  const goods = goodsOptions.value.find((g) => g.sId === row.sGoodsId)
  const units = [{ name: goods.sBaseUnit, rate: 1 }]
  const packUnits = await goodsApi.listUnits(row.sGoodsId)
  packUnits.forEach((u) => units.push({ name: u.sUnitName, rate: u.iConvertRate }))
  row.unitOptions = units
  // 默认选基本单位
  row.sInputUnit = goods.sBaseUnit
  row.rate = 1
}

function onUnitChange(row) {
  const u = row.unitOptions.find((x) => x.name === row.sInputUnit)
  row.rate = u ? u.rate : 1
}

function recalc() {
  // 触发计算列刷新，无需额外逻辑（模板内联计算）
}

const totalYuan = computed(() => {
  return form.items
      .reduce((sum, r) => sum + r.iInputQty * r.rate * r.priceYuan, 0)
      .toFixed(2)
})

async function loadOptions() {
  supplierOptions.value = (await supplierApi.page({ pageNum: 1, pageSize: 1000 })).list
  warehouseOptions.value = (await warehouseApi.page({ pageNum: 1, pageSize: 1000 })).list
  goodsOptions.value = (await goodsApi.page({ pageNum: 1, pageSize: 1000 })).list
}

async function handleSubmit() {
  if (!form.sSupplierId || !form.sWarehouseId) {
    ElMessage.warning('请选择供应商与入库地点')
    return
  }
  if (form.items.length === 0) {
    ElMessage.warning('请至少添加一条明细')
    return
  }
  for (const r of form.items) {
    if (!r.sGoodsId || !r.sInputUnit) {
      ElMessage.warning('请完整填写每条明细的商品与单位')
      return
    }
  }
  const payload = {
    sSupplierId: form.sSupplierId,
    sWarehouseId: form.sWarehouseId,
    items: form.items.map((r) => ({
      sGoodsId: r.sGoodsId,
      sInputUnit: r.sInputUnit,
      iInputQty: r.iInputQty,
      iPrice: yuanToFen(r.priceYuan)
    }))
  }
  await purchaseApi.create(payload)
  ElMessage.success('进货单已保存并入库')
  form.sSupplierId = ''
  form.sWarehouseId = ''
  form.items = []
}

onMounted(async () => {
  await loadOptions()
  addItem()
})
</script>

<style scoped>
.actions {
  margin-top: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.total {
  font-weight: bold;
  color: #f56c6c;
}
</style>
