import request from './request'

// 应收账款与销售收款接口
export const receivableApi = {
  // 分页查询应收
  page: (params) => request.get('/receivable/page', { params }),
  // 销售欠款汇总（按客户）
  summary: () => request.get('/receivable/summary'),
  // 某客户应收明细
  byCustomer: (customerId) => request.get(`/receivable/customer/${customerId}`),
  // 登记销售收款
  createReceipt: (data) => request.post('/receipt', data)
}
