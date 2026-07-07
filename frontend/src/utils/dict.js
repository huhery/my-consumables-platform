// 枚举字典与展示工具

// 地点类型
export const LOCATION_TYPE = {
  WAREHOUSE: 1,
  STORE: 2
}

export const LOCATION_TYPE_OPTIONS = [
  { value: 1, label: '仓库' },
  { value: 2, label: '门店' }
]

// 流水类型
export const FLOW_TYPE_OPTIONS = [
  { value: 1, label: '采购入库', tagType: 'success' },
  { value: 2, label: '批发出库', tagType: 'danger' },
  { value: 3, label: '门店散卖出库', tagType: 'danger' },
  { value: 4, label: '手工调整', tagType: 'info' }
]

// 销售类型
export const SALE_TYPE_OPTIONS = [
  { value: 1, label: '批发出货' },
  { value: 2, label: '门店散卖' }
]

// 销售单状态
export const SALE_STATUS_OPTIONS = [
  { value: 1, label: '待发货', tagType: 'warning' },
  { value: 2, label: '已完成', tagType: 'success' }
]

// 资金流水类型
export const FUND_FLOW_TYPE_OPTIONS = [
  { value: 1, label: '销售收款', tagType: 'success' },
  { value: 2, label: '采购付款', tagType: 'danger' },
  { value: 3, label: '费用支出', tagType: 'warning' },
  { value: 4, label: '其他收入', tagType: 'success' }
]

// 分类类型
export const CATEGORY_TYPE_OPTIONS = [
  { value: 1, label: '支出' },
  { value: 2, label: '收入' }
]

// 结清状态
export const SETTLE_STATUS_OPTIONS = [
  { value: 1, label: '未结清', tagType: 'warning' },
  { value: 2, label: '已结清', tagType: 'success' }
]

// 通用：按值取标签
export function labelOf(options, value) {
  const item = options.find((o) => o.value === value)
  return item ? item.label : value
}

// 通用：按值取标签的 tagType
export function tagTypeOf(options, value) {
  const item = options.find((o) => o.value === value)
  return item ? item.tagType : ''
}
