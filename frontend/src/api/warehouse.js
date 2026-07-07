import request from './request'

// 库存地点接口
export const warehouseApi = {
  // 分页查询
  page: (params) => request.get('/warehouse/page', { params }),
  // 新增
  create: (data) => request.post('/warehouse', data),
  // 修改
  update: (id, data) => request.put(`/warehouse/${id}`, data),
  // 删除
  remove: (id) => request.delete(`/warehouse/${id}`)
}
