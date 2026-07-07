import request from './request'

// 商品与包装单位接口
export const goodsApi = {
  // 分页查询商品
  page: (params) => request.get('/goods/page', { params }),
  // 新增商品
  create: (data) => request.post('/goods', data),
  // 修改商品
  update: (id, data) => request.put(`/goods/${id}`, data),
  // 删除商品
  remove: (id) => request.delete(`/goods/${id}`),
  // 查询商品包装单位列表
  listUnits: (id) => request.get(`/goods/${id}/units`),
  // 新增包装单位
  addUnit: (id, data) => request.post(`/goods/${id}/units`, data),
  // 删除包装单位
  removeUnit: (unitId) => request.delete(`/goods/units/${unitId}`)
}
