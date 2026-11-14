export const GENDER_OPTIONS = [
  { value: 1, label: '男', tag: 'primary' },
  { value: 2, label: '女', tag: 'danger' }
]

export const JOB_OPTIONS = [
  { value: 1, label: '班主任' },
  { value: 2, label: '讲师' },
  { value: 3, label: '学工主管' },
  { value: 4, label: '教研主管' },
  { value: 5, label: '咨询师' }
]

// 学科编号与后端 clazz.subject 对应，如与数据库字典不一致可在此调整
export const SUBJECT_OPTIONS = [
  { value: 1, label: 'Java' },
  { value: 2, label: '前端' },
  { value: 3, label: '大数据' },
  { value: 4, label: 'Python' },
  { value: 5, label: '测试' },
  { value: 6, label: '人工智能' }
]

export const DEGREE_OPTIONS = [
  { value: 1, label: '初中' },
  { value: 2, label: '高中' },
  { value: 3, label: '大专' },
  { value: 4, label: '本科' },
  { value: 5, label: '硕士' },
  { value: 6, label: '博士' }
]

const CLAZZ_STATUS_TYPES = {
  未开班: 'warning',
  在读中: 'success',
  已结课: 'info'
}

export function labelOf(options, value, fallback = '-') {
  return options.find((item) => item.value === value)?.label ?? fallback
}

export function tagTypeOf(options, value, fallback = 'info') {
  return options.find((item) => item.value === value)?.tag ?? fallback
}

export function clazzStatusType(status) {
  return CLAZZ_STATUS_TYPES[status] || 'info'
}