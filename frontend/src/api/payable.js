import request from './request'

// 应付账款与采购付款接口
export const payableApi = {
  // 分页查询应付
  page: (params) => request.get('/payable/page', { params }),
  // 采购付款汇总（按供应商）
  summary: () => request.get('/payable/summary'),
  // 某供应商应付明细
  bySupplier: (supplierId) => request.get(`/payable/supplier/${supplierId}`),
  // 登记采购付款
  createPayment: (data) => request.post('/payment', data)
}
