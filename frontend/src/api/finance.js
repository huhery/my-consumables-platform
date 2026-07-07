import request from './request'

// 费用/收入分类与记账接口
export const expenseApi = {
  // 分类列表（type 可选：1支出 2收入）
  listCategory: (type) => request.get('/expense-category', { params: { type } }),
  // 新增分类
  createCategory: (data) => request.post('/expense-category', data),
  // 删除分类
  removeCategory: (id) => request.delete(`/expense-category/${id}`),
  // 登记费用支出
  recordExpense: (data) => request.post('/expense', data),
  // 登记其他收入
  recordIncome: (data) => request.post('/income', data)
}

// 资金流水接口
export const fundApi = {
  page: (params) => request.get('/fund-flow/page', { params })
}

// 报表接口
export const reportApi = {
  // 资金汇总
  fundSummary: (params) => request.get('/report/fund-summary', { params }),
  // 费用/收入分类汇总
  expenseSummary: (params) => request.get('/report/expense-summary', { params })
}

// 送货提醒接口
export const deliveryApi = {
  list: () => request.get('/delivery-reminder')
}
