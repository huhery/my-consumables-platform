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
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="showDetail(row)">详情</el-button>
          <el-button size="small" type="success" @click="handlePrint(row)">打印</el-button>
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

    <!-- 打印预览弹窗 -->
    <el-dialog v-model="printVisible" title="打印销售单" width="700px">
      <div id="print-area" ref="printAreaRef">
        <div style="text-align:center;font-size:18px;font-weight:bold;margin-bottom:12px;">销售单</div>
        <div style="display:flex;justify-content:space-between;margin-bottom:8px;font-size:14px;">
          <span>单号：{{ printData.saleNo }}</span>
          <span>日期：{{ printData.createTime }}</span>
        </div>
        <div style="margin-bottom:12px;font-size:14px;">
          <span>客户：{{ printData.customerName }}</span>
        </div>
        <table style="width:100%;border-collapse:collapse;font-size:14px;" border="1" cellpadding="6">
          <thead>
            <tr style="background:#f5f5f5;">
              <th>序号</th>
              <th>商品</th>
              <th>规格</th>
              <th>数量</th>
              <th>单位</th>
              <th>单价(元)</th>
              <th>折扣</th>
              <th>金额(元)</th>
              <th>备注</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(it, i) in printData.items" :key="i">
              <td style="text-align:center;">{{ i + 1 }}</td>
              <td>{{ it.goodsName }}</td>
              <td>{{ it.spec }}</td>
              <td style="text-align:center;">{{ it.qty }}</td>
              <td style="text-align:center;">{{ it.unit }}</td>
              <td style="text-align:right;">{{ it.price }}</td>
              <td style="text-align:center;">{{ it.discount }}%</td>
              <td style="text-align:right;">{{ it.amount }}</td>
              <td>{{ it.remark }}</td>
            </tr>
          </tbody>
          <tfoot>
            <tr>
              <td colspan="7" style="text-align:right;font-weight:bold;">合计：</td>
              <td style="text-align:right;font-weight:bold;">{{ printData.totalYuan }}</td>
              <td></td>
            </tr>
          </tfoot>
        </table>
      </div>
      <template #footer>
        <el-button @click="printVisible = false">关闭</el-button>
        <el-button type="primary" @click="doPrint">打印</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { saleApi } from '@/api/sale'
import { goodsApi } from '@/api/goods'
import { customerApi } from '@/api/customer'
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

// 打印相关
const printVisible = ref(false)
const printData = reactive({ saleNo: '', createTime: '', customerName: '', items: [], totalYuan: '0.00' })
const customerOptions = ref([])

function customerName(id) {
  const c = customerOptions.value.find((x) => x.sId === id)
  return c ? c.sName : ''
}

async function handlePrint(row) {
  const res = await saleApi.detail(row.sId)
  printData.saleNo = row.sSaleNo
  printData.createTime = row.dtCreateTime ? String(row.dtCreateTime).slice(0, 16) : ''
  printData.customerName = customerName(row.sCustomerId)
  printData.totalYuan = fenToYuan(row.iTotalAmount)
  printData.items = (res.items || []).map((it) => {
    const g = goodsOptions.value.find((x) => x.sId === it.sGoodsId)
    const price = fenToYuan(it.iPrice)
    const discount = it.iDiscount || 100
    const discountPrice = (Number(price) * discount / 100).toFixed(2)
    const amount = (Number(discountPrice) * it.iQtyBase).toFixed(2)
    return {
      goodsName: g ? g.sName : '',
      spec: g ? (g.sSpec || '') : '',
      qty: it.iInputQty,
      unit: it.sInputUnit,
      price,
      discount,
      amount,
      remark: it.sRemark || ''
    }
  })
  printVisible.value = true
}

function doPrint() {
  const content = document.getElementById('print-area').innerHTML
  const win = window.open('', '_blank')
  win.document.write('<html><head><title>销售单</title><style>body{font-family:sans-serif;padding:20px;}table{width:100%;border-collapse:collapse;}th,td{border:1px solid #333;padding:6px;}</style></head><body>')
  win.document.write(content)
  win.document.write('</body></html>')
  win.document.close()
  win.print()
  win.close()
}

onMounted(async () => {
  goodsOptions.value = (await goodsApi.page({ pageNum: 1, pageSize: 1000 })).list
  customerOptions.value = (await customerApi.page({ pageNum: 1, pageSize: 1000 })).list
  await loadData()
})
</script>

<style scoped>
.pagination { margin-top: 16px; }
</style>
