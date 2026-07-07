import request from './request'

// 库存与流水接口
export const stockApi = {
  // 分页查询库存
  page: (params) => request.get('/stock/page', { params }),
  // 分页查询出入库流水
  flowPage: (params) => request.get('/stock/flow/page', { params }),
  // 手工库存调整
  adjust: (data) => request.post('/stock/adjust', data)
}
