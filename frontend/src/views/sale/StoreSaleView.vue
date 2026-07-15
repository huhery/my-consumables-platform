<template>
  <div>
    <el-card>
      <template #header>门店散卖（仅门店地点，按基本单位出库，保存即出库）</template>
      <el-form :model="form" label-width="90px">
        <el-form-item label="门店" required>
          <el-select v-model="form.sWarehouseId" placeholder="请选择门店" style="width: 260px">
            <el-option v-for="w in storeOptions" :key="w.sId" :label="w.sName" :value="w.sId"/>
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
        <el-table-column label="基本单位" width="100">
          <template #default="{ row }">{{ row.baseUnit }}</template>
        </el-table-column>
        <el-table-column label="数量" width="140">
          <template #default="{ row }">
            <el-input-number v-model="row.iInputQty" :min="1" style="width: 100%"/>
          </template>
        </el-table-column>
        <el-table-column label="售价（元/基本单位）" width="160">
          <template #default="{ row }">
            <el-input-number v-model="row.priceYuan" :min="0" :precision="2" :step="0.1" style="width: 100%"/>
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
        <el-button type="primary" @click="handleSubmit">确认出货</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { saleApi } from '@/api/sale'
import { warehouseApi } from '@/api/warehouse'
import { goodsApi } from '@/api/goods'
import { yuanToFen } from '@/utils/money'
import { LOCATION_TYPE } from '@/utils/dict'

const storeOptions = ref([])
const goodsOptions = ref([])

const form = reactive({ sWarehouseId: '', items: [] })

function newItem() {
  return { sGoodsId: '', baseUnit: '', iInputQty: 1, priceYuan: 0 }
}

function addItem() {
  form.items.push(newItem())
}

function removeItem(index) {
  form.items.splice(index, 1)
}

function onGoodsChange(row) {
  const goods = goodsOptions.value.find((g) => g.sId === row.sGoodsId)
  row.baseUnit = goods ? goods.sBaseUnit : ''
}

const totalYuan = computed(() => {
  return form.items
      .reduce((sum, r) => sum + r.iInputQty * r.priceYuan, 0)
      .toFixed(2)
})

async function loadOptions() {
  // 仅展示门店类型地点（前端过滤，后端兜底校验）
  const ws = (await warehouseApi.page({ pageNum: 1, pageSize: 1000 })).list
  storeOptions.value = ws.filter((w) => w.iLocationType === LOCATION_TYPE.STORE)
  goodsOptions.value = (await goodsApi.page({ pageNum: 1, pageSize: 1000 })).list
}

async function handleSubmit() {
  if (!form.sWarehouseId) {
    ElMessage.warning('请选择门店')
    return
  }
  if (form.items.length === 0) {
    ElMessage.warning('请至少添加一条明细')
    return
  }
  for (const r of form.items) {
    if (!r.sGoodsId) {
      ElMessage.warning('请完整填写每条明细')
      return
    }
  }
  const payload = {
    sWarehouseId: form.sWarehouseId,
    items: form.items.map((r) => ({
      sGoodsId: r.sGoodsId,
      sInputUnit: r.baseUnit,
      iInputQty: r.iInputQty,
      iPrice: yuanToFen(r.priceYuan)
    }))
  }
  await saleApi.createStore(payload)
  ElMessage.success('门店散卖出货成功')
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
