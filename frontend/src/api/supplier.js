import request from './request'

// 供应商接口
export const supplierApi = {
  // 分页查询
  page: (params) => request.get('/supplier/page', { params }),
  // 新增
  create: (data) => request.post('/supplier', data),
  // 修改
  update: (id, data) => request.put(`/supplier/${id}`, data),
  // 删除
  remove: (id) => request.delete(`/supplier/${id}`)
}
