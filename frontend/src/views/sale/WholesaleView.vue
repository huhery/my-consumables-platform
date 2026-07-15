<template>
  <div>
    <el-card>
      <template #header>卖给超市</template>
      <el-form :model="form" label-width="90px">
        <el-form-item label="客户" required>
          <el-select v-model="form.sCustomerId" placeholder="请选择" filterable style="width: 260px">
            <el-option v-for="c in customerOptions" :key="c.sId" :label="c.sName" :value="c.sId"/>
          </el-select>
        </el-form-item>
        <el-form-item label="出库地点" required>
          <el-select v-model="form.sWarehouseId" placeholder="请选择" style="width: 260px">
            <el-option v-for="w in warehouseOptions" :key="w.sId" :label="w.sName" :value="w.sId"/>
          </el-select>
        </el-form-item>
        <el-form-item label="期望送达日期">
          <el-date-picker v-model="form.dExpectDelivery" type="date" value-format="YYYY-MM-DD"
                          placeholder="可选，用于送货提醒" style="width: 260px"/>
        </el-form-item>
        <el-form-item label="货已送出">
          <el-switch v-model="form.shipped"/>
          <span class="ship-hint">开着＝开单即发货扣库存；关掉＝先存单，之后再发货</span>
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
            <el-input-number v-model="row.iInputQty" :min="1" style="width: 100%"/>
          </template>
        </el-table-column>
        <el-table-column label="折算（基本单位）" width="130">
          <template #default="{ row }">{{ row.iInputQty * row.rate }}</template>
        </el-table-column>
        <el-table-column label="售价（元/基本单位）" width="150">
          <template #default="{ row }">
            <el-input-number v-model="row.priceYuan" :min="0" :precision="2" :step="0.1" style="width: 100%"/>
          </template>
        </el-table-column>
        <el-table-column label="折扣(%)" width="100">
          <template #default="{ row }">
            <el-input-number v-model="row.discount" :min="1" :max="100" style="width: 100%"/>
          </template>
        </el-table-column>
        <el-table-column label="折后价" width="90">
          <template #default="{ row }">{{ (row.priceYuan * row.discount / 100).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="备注" width="120">
          <template #default="{ row }">
            <el-input v-model="row.remark" placeholder="备注" style="width: 100%"/>
          </template>
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
        <el-button type="primary" size="large" @click="handleSubmit">
          {{ form.shipped ? '开单并发货' : '保存（待发货）' }}
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { saleApi } from '@/api/sale'
import { customerApi } from '@/api/customer'
import { warehouseApi } from '@/api/warehouse'
import { goodsApi } from '@/api/goods'
import { yuanToFen } from '@/utils/money'

const customerOptions = ref([])
const warehouseOptions = ref([])
const goodsOptions = ref([])

const form = reactive({ sCustomerId: '', sWarehouseId: '', dExpectDelivery: '', shipped: true, items: [] })

function newItem() {
  return { sGoodsId: '', sInputUnit: '', iInputQty: 1, priceYuan: 0, discount: 100, remark: '', rate: 1, unitOptions: [] }
}

function addItem() {
  form.items.push(newItem())
}

function removeItem(index) {
  form.items.splice(index, 1)
}

async function onGoodsChange(row) {
  const goods = goodsOptions.value.find((g) => g.sId === row.sGoodsId)
  const units = [{ name: goods.sBaseUnit, rate: 1 }]
  const packUnits = await goodsApi.listUnits(row.sGoodsId)
  packUnits.forEach((u) => units.push({ name: u.sUnitName, rate: u.iConvertRate }))
  row.unitOptions = units
  row.sInputUnit = goods.sBaseUnit
  row.rate = 1
}

function onUnitChange(row) {
  const u = row.unitOptions.find((x) => x.name === row.sInputUnit)
  row.rate = u ? u.rate : 1
}

const totalYuan = computed(() => {
  return form.items
      .reduce((sum, r) => sum + r.iInputQty * r.rate * r.priceYuan * r.discount / 100, 0)
      .toFixed(2)
})

async function loadOptions() {
  customerOptions.value = (await customerApi.page({ pageNum: 1, pageSize: 1000 })).list
  warehouseOptions.value = (await warehouseApi.page({ pageNum: 1, pageSize: 1000 })).list
  goodsOptions.value = (await goodsApi.page({ pageNum: 1, pageSize: 1000 })).list
}

async function handleSubmit() {
  if (!form.sCustomerId || !form.sWarehouseId) {
    ElMessage.warning('请选择客户与出库地点')
    return
  }
  if (form.items.length === 0) {
    ElMessage.warning('请至少添加一条明细')
    return
  }
  for (const r of form.items) {
    if (!r.sGoodsId || !r.sInputUnit) {
      ElMessage.warning('请完整填写每条明细')
      return
    }
  }
  const payload = {
    sCustomerId: form.sCustomerId,
    sWarehouseId: form.sWarehouseId,
    dExpectDelivery: form.dExpectDelivery || undefined,
    items: form.items.map((r) => ({
      sGoodsId: r.sGoodsId,
      sInputUnit: r.sInputUnit,
      iInputQty: r.iInputQty,
      iPrice: yuanToFen(r.priceYuan),
      iDiscount: r.discount,
      sRemark: r.remark || ''
    }))
  }
  const saleId = await saleApi.createWholesale(payload)
  if (form.shipped) {
    // 一步发货：开单后立即发货扣库存；库存不足时后端拒绝，单据保留为待发货
    try {
      await saleApi.ship(saleId)
      ElMessage.success('已开单并发货')
    } catch (e) {
      // ship 失败（如库存不足）由响应拦截器提示；单据已存为待发货，可补货后到卖货记录发货
      ElMessage.warning('已开单，但发货失败（可能库存不足）。请补货后到「卖货记录」发货')
      return
    }
  } else {
    ElMessage.success('批发单已保存，请到「卖货记录」发货')
  }
  form.sCustomerId = ''
  form.sWarehouseId = ''
  form.dExpectDelivery = ''
  form.shipped = true
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

.ship-hint {
  margin-left: 12px;
  color: #909399;
  font-size: 13px;
}
</style>
