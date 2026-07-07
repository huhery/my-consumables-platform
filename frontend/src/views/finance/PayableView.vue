<template>
  <div>
    <el-card>
      <template #header>采购付款汇总</template>
      <el-table :data="summary" border v-loading="loading">
        <el-table-column prop="sSupplierName" label="供应商"/>
        <el-table-column label="应付总额（元）">
          <template #default="{ row }">{{ fenToYuan(row.iTotalAmount) }}</template>
        </el-table-column>
        <el-table-column label="已付总额（元）">
          <template #default="{ row }">{{ fenToYuan(row.iPaidAmount) }}</template>
        </el-table-column>
        <el-table-column label="未付（元）">
          <template #default="{ row }">
            <span class="debt">{{ fenToYuan(row.iUnpaidAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="showDetail(row)">明细</el-button>
            <el-button size="small" type="primary" @click="openPayment(row)">付款</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailVisible" title="应付明细" width="640px">
      <el-table :data="detailList" border>
        <el-table-column prop="sPurchaseId" label="进货单ID" width="200" show-overflow-tooltip/>
        <el-table-column label="应付（元）">
          <template #default="{ row }">{{ fenToYuan(row.iTotalAmount) }}</template>
        </el-table-column>
        <el-table-column label="已付（元）">
          <template #default="{ row }">{{ fenToYuan(row.iPaidAmount) }}</template>
        </el-table-column>
        <el-table-column label="未付（元）">
          <template #default="{ row }">{{ fenToYuan(row.iTotalAmount - row.iPaidAmount) }}</template>
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

    <el-dialog v-model="paymentVisible" title="登记付款" width="480px">
      <el-form :model="paymentForm" label-width="100px">
        <el-form-item label="供应商">
          <span>{{ paymentForm.supplierName }}</span>
        </el-form-item>
        <el-form-item label="关联进货单">
          <el-select v-model="paymentForm.sPurchaseId" placeholder="不选则冲减总应付" clearable style="width: 100%">
            <el-option v-for="p in unpaidPayables" :key="p.sPurchaseId"
                       :label="`单${p.sPurchaseId.slice(0,8)} 未付${fenToYuan(p.iTotalAmount - p.iPaidAmount)}元`"
                       :value="p.sPurchaseId"/>
          </el-select>
        </el-form-item>
        <el-form-item label="付款金额（元）" required>
          <el-input-number v-model="paymentForm.amountYuan" :min="0.01" :precision="2" :step="1" style="width: 100%"/>
        </el-form-item>
        <el-form-item label="付款日期" required>
          <el-date-picker v-model="paymentForm.dOccurDate" type="date" value-format="YYYY-MM-DD" style="width: 100%"/>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="paymentForm.sRemark" type="textarea"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="paymentVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPayment">确认付款</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { payableApi } from '@/api/payable'
import { fenToYuan, yuanToFen } from '@/utils/money'
import { SETTLE_STATUS_OPTIONS, labelOf, tagTypeOf } from '@/utils/dict'

const summary = ref([])
const loading = ref(false)

async function loadSummary() {
  loading.value = true
  try {
    summary.value = await payableApi.summary()
  } finally {
    loading.value = false
  }
}

const detailVisible = ref(false)
const detailList = ref([])

async function showDetail(row) {
  detailList.value = await payableApi.bySupplier(row.sSupplierId)
  detailVisible.value = true
}

const paymentVisible = ref(false)
const unpaidPayables = ref([])
const paymentForm = reactive({
  sSupplierId: '', supplierName: '', sPurchaseId: '', amountYuan: 0,
  dOccurDate: new Date().toISOString().slice(0, 10), sRemark: ''
})

async function openPayment(row) {
  paymentForm.sSupplierId = row.sSupplierId
  paymentForm.supplierName = row.sSupplierName
  paymentForm.sPurchaseId = ''
  paymentForm.amountYuan = 0
  paymentForm.sRemark = ''
  const all = await payableApi.bySupplier(row.sSupplierId)
  unpaidPayables.value = all.filter((p) => p.iStatus === 1)
  paymentVisible.value = true
}

async function submitPayment() {
  if (!paymentForm.amountYuan || paymentForm.amountYuan <= 0) {
    ElMessage.warning('请填写付款金额')
    return
  }
  await payableApi.createPayment({
    sSupplierId: paymentForm.sSupplierId,
    sPurchaseId: paymentForm.sPurchaseId || undefined,
    iAmount: yuanToFen(paymentForm.amountYuan),
    dOccurDate: paymentForm.dOccurDate,
    sRemark: paymentForm.sRemark
  })
  ElMessage.success('付款成功')
  paymentVisible.value = false
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
