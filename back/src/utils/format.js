export function formatDateTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}

// 表格序号：跨页连续，(当前页 - 1) * 每页条数 + 行下标 + 1
export const pageIndex = (filters) => (index) =>
  (filters.page - 1) * filters.pageSize + index + 1