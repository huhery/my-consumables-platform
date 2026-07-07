<template>
  <div>
    <el-card style="max-width: 560px">
      <template #header>手工库存调整</template>
      <el-form :model="form" label-width="100px">
        <el-form-item label="库存地点" required>
          <el-select v-model="form.sWarehouseId" placeholder="请选择" style="width: 100%">
            <el-option v-for="w in warehouseOptions" :key="w.sId" :label="w.sName" :value="w.sId"/>
          </el-select>
        </el-form-item>
        <el-form-item label="商品" required>
          <el-select v-model="form.sGoodsId" placeholder="请选择" filterable style="width: 100%">
            <el-option v-for="g in goodsOptions" :key="g.sId" :label="g.sName" :value="g.sId"/>
          </el-select>
        </el-form-item>
        <el-form-item label="调整数量" required>
          <el-input-number v-model="form.iChangeQty" :step="1"/>
          <span class="tip">正数增加，负数减少（基本单位）</span>
        </el-form-item>
        <el-form-item label="调整原因" required>
          <el-input v-model="form.sReason" type="textarea" placeholder="如：盘盈、盘亏、报损"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交调整</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { stockApi } from '@/api/stock'
import { goodsApi } from '@/api/goods'
import { warehouseApi } from '@/api/warehouse'

const form = reactive({ sWarehouseId: '', sGoodsId: '', iChangeQty: 0, sReason: '' })
const goodsOptions = ref([])
const warehouseOptions = ref([])

async function loadOptions() {
  const g = await goodsApi.page({ pageNum: 1, pageSize: 1000 })
  goodsOptions.value = g.list
  const w = await warehouseApi.page({ pageNum: 1, pageSize: 1000 })
  warehouseOptions.value = w.list
}

async function handleSubmit() {
  if (!form.sWarehouseId || !form.sGoodsId || !form.sReason) {
    ElMessage.warning('请完整填写表单')
    return
  }
  if (!form.iChangeQty) {
    ElMessage.warning('调整数量不能为 0')
    return
  }
  await stockApi.adjust(form)
  ElMessage.success('调整成功')
  form.iChangeQty = 0
  form.sReason = ''
}

onMounted(loadOptions)
</script>

<style scoped>
.tip {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}
</style>
