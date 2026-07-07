import request from './request'

// 客户接口
export const customerApi = {
  // 分页查询
  page: (params) => request.get('/customer/page', { params }),
  // 新增
  create: (data) => request.post('/customer', data),
  // 修改
  update: (id, data) => request.put(`/customer/${id}`, data),
  // 删除
  remove: (id) => request.delete(`/customer/${id}`)
}
