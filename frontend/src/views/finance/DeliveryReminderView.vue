<template>
  <div>
    <el-card>
      <template #header>送货提醒</template>
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
        <el-table-column label="物流状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.iDeliverStatus === 2 ? 'success' : 'warning'">
              {{ row.iDeliverStatus === 2 ? '已发货' : '未发货' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="快递信息" width="180">
          <template #default="{ row }">
            <span v-if="row.sExpressCompany">{{ row.sExpressCompany }} {{ row.sExpressNo }}</span>
            <span v-else class="muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button v-if="row.iDeliverStatus !== 2" size="small" type="primary" @click="openDeliver(row)">发货</el-button>
            <span v-else class="muted">已完成</span>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && list.length === 0" description="暂无待发货单据"/>
    </el-card>

    <!-- 发货弹窗 -->
    <el-dialog v-model="deliverDialogVisible" title="确认发货" width="420px">
      <el-form :model="deliverForm" label-width="90px">
        <el-form-item label="快递公司">
          <el-input v-model="deliverForm.expressCompany" placeholder="如：顺丰、中通"/>
        </el-form-item>
        <el-form-item label="快递单号">
          <el-input v-model="deliverForm.expressNo" placeholder="物流单号"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deliverDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDeliver">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { deliveryApi } from '@/api/finance'
import { saleApi } from '@/api/sale'
import { customerApi } from '@/api/customer'
import { fenToYuan } from '@/utils/money'

const list = ref([])
const loading = ref(false)
const customers = ref([])

function customerName(id) {
  const c = customers.value.find((x) => x.sId === id)
  return c ? c.sName : id
}

function formatExpect(date) {
  if (!date) return '未填'
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

// 发货弹窗
const deliverDialogVisible = ref(false)
const deliverForm = reactive({ saleId: '', expressCompany: '', expressNo: '' })

function openDeliver(row) {
  deliverForm.saleId = row.sId
  deliverForm.expressCompany = ''
  deliverForm.expressNo = ''
  deliverDialogVisible.value = true
}

async function submitDeliver() {
  await saleApi.confirmDeliver(deliverForm.saleId, deliverForm.expressCompany, deliverForm.expressNo)
  ElMessage.success('发货成功')
  deliverDialogVisible.value = false
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.muted { color: #909399; }
</style>
