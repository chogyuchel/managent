export const phoneRule = {
  validator: (rule, value, callback) => {
    if (!value || /^1[3-9]\d{9}$/.test(value)) callback()
    else callback(new Error('手机号格式不正确'))
  },
  trigger: 'blur'
}