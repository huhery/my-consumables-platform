<template>
  <div>
    <el-card>
      <template #header>销售欠款汇总</template>
      <el-table :data="summary" border v-loading="loading">
        <el-table-column prop="sCustomerName" label="客户"/>
        <el-table-column label="应收总额（元）">
          <template #default="{ row }">{{ fenToYuan(row.iTotalAmount) }}</template>
        </el-table-column>
        <el-table-column label="已收总额（元）">
          <template #default="{ row }">{{ fenToYuan(row.iReceivedAmount) }}</template>
        </el-table-column>
        <el-table-column label="欠款（元）">
          <template #default="{ row }">
            <span class="debt">{{ fenToYuan(row.iUnreceivedAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="showDetail(row)">明细</el-button>
            <el-button size="small" type="primary" @click="openReceipt(row)">收款</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 客户应收明细 -->
    <el-dialog v-model="detailVisible" title="应收明细" width="640px">
      <el-table :data="detailList" border>
        <el-table-column prop="sSaleId" label="销售单ID" width="200" show-overflow-tooltip/>
        <el-table-column label="应收（元）">
          <template #default="{ row }">{{ fenToYuan(row.iTotalAmount) }}</template>
        </el-table-column>
        <el-table-column label="已收（元）">
          <template #default="{ row }">{{ fenToYuan(row.iReceivedAmount) }}</template>
        </el-table-column>
        <el-table-column label="未收（元）">
          <template #default="{ row }">{{ fenToYuan(row.iTotalAmount - row.iReceivedAmount) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="tagTypeOf(SETTLE_STATUS_OPTIONS, row.iStatus)">
              {{ labelOf(SETTLE_STATUS_OPTIONS, row.iStatus) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 收款登记 -->
    <el-dialog v-model="receiptVisible" title="登记收款" width="480px">
      <el-form :model="receiptForm" label-width="100px">
        <el-form-item label="客户">
          <span>{{ receiptForm.customerName }}</span>
        </el-form-item>
        <el-form-item label="关联销售单">
          <el-select v-model="receiptForm.sSaleId" placeholder="不选则冲减总欠款" clearable style="width: 100%">
            <el-option v-for="r in unpaidReceivables" :key="r.sSaleId"
                       :label="`单${r.sSaleId.slice(0,8)} 未收${fenToYuan(r.iTotalAmount - r.iReceivedAmount)}元`"
                       :value="r.sSaleId"/>
          </el-select>
        </el-form-item>
        <el-form-item label="收款金额（元）" required>
          <el-input-number v-model="receiptForm.amountYuan" :min="0.01" :precision="2" :step="1" style="width: 100%"/>
        </el-form-item>
        <el-form-item label="收款日期" required>
          <el-date-picker v-model="receiptForm.dOccurDate" type="date" value-format="YYYY-MM-DD" style="width: 100%"/>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="receiptForm.sRemark" type="textarea"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="receiptVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReceipt">确认收款</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { receivableApi } from '@/api/receivable'
import { fenToYuan, yuanToFen } from '@/utils/money'
import { SETTLE_STATUS_OPTIONS, labelOf, tagTypeOf } from '@/utils/dict'

const summary = ref([])
const loading = ref(false)

async function loadSummary() {
  loading.value = true
  try {
    summary.value = await receivableApi.summary()
  } finally {
    loading.value = false
  }
}

// 明细
const detailVisible = ref(false)
const detailList = ref([])

async function showDetail(row) {
  detailList.value = await receivableApi.byCustomer(row.sCustomerId)
  detailVisible.value = true
}

// 收款
const receiptVisible = ref(false)
const unpaidReceivables = ref([])
const receiptForm = reactive({
  sCustomerId: '', customerName: '', sSaleId: '', amountYuan: 0,
  dOccurDate: new Date().toISOString().slice(0, 10), sRemark: ''
})

async function openReceipt(row) {
  receiptForm.sCustomerId = row.sCustomerId
  receiptForm.customerName = row.sCustomerName
  receiptForm.sSaleId = ''
  receiptForm.amountYuan = 0
  receiptForm.sRemark = ''
  // 加载该客户未收清的应收单供关联
  const all = await receivableApi.byCustomer(row.sCustomerId)
  unpaidReceivables.value = all.filter((r) => r.iStatus === 1)
  receiptVisible.value = true
}

async function submitReceipt() {
  if (!receiptForm.amountYuan || receiptForm.amountYuan <= 0) {
    ElMessage.warning('请填写收款金额')
    return
  }
  await receivableApi.createReceipt({
    sCustomerId: receiptForm.sCustomerId,
    sSaleId: receiptForm.sSaleId || undefined,
    iAmount: yuanToFen(receiptForm.amountYuan),
    dOccurDate: receiptForm.dOccurDate,
    sRemark: receiptForm.sRemark
  })
  ElMessage.success('收款成功')
  receiptVisible.value = false
  loadSummary()
}

onMounted(loadSummary)
</script>

<style scoped>
.debt {
  color: #f56c6c;
  font-weight: bold;
}
</style>
