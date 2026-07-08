import request from './request'

// AI 智能问数接口
export const aiApi = {
  // 提交自然语言问题，返回回答（成功/拒答/降级）
  ask: (question) => request.post('/ai/ask', { question }),
  // 查询当前商家是否开通 AI
  status: () => request.get('/ai/status')
}
