import request from './request'

// 进货接口
export const purchaseApi = {
  // 分页查询进货单
  page: (params) => request.get('/purchase/page', { params }),
  // 新增进货单（保存即入库）
  create: (data) => request.post('/purchase', data),
  // 进货单详情
  detail: (id) => request.get(`/purchase/${id}`)
}
