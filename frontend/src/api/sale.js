import request from './request'

// 出货接口
export const saleApi = {
  // 分页查询销售单
  page: (params) => request.get('/sale/page', { params }),
  // 新增批发销售单（待发货）
  createWholesale: (data) => request.post('/sale/wholesale', data),
  // 批发销售单发货
  ship: (id) => request.post(`/sale/${id}/ship`),
  // 门店散卖（保存即出库）
  createStore: (data) => request.post('/sale/store', data),
  // 销售单详情
  detail: (id) => request.get(`/sale/${id}`)
}
