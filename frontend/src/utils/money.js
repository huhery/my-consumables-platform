// 金额换算工具：后端以「分」存储传输，前端以「元」展示/录入

// 分转元（用于展示），保留两位小数
export function fenToYuan(fen) {
  if (fen === null || fen === undefined || fen === '') {
    return '0.00'
  }
  return (Number(fen) / 100).toFixed(2)
}

// 元转分（用于提交），四舍五入取整
export function yuanToFen(yuan) {
  if (yuan === null || yuan === undefined || yuan === '') {
    return 0
  }
  return Math.round(Number(yuan) * 100)
}
