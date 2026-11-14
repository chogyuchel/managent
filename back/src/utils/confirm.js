import { ElMessageBox } from 'element-plus'

export function confirmDelete(message) {
  return ElMessageBox.confirm(message, '删除确认', {
    type: 'warning',
    confirmButtonText: '删除'
  })
}