import request from './request'

/* ---------------- 登录 ---------------- */
export const login = (data) => request.post('/login', data)

/* ---------------- 部门 ---------------- */
export const getDepts = () => request.get('/depts')
export const getDept = (id) => request.get(`/depts/${id}`)
export const addDept = (data) => request.post('/depts', data)
export const updateDept = (data) => request.put('/depts', data)
export const deleteDept = (id) => request.delete('/depts', { params: { id } })

/* ---------------- 员工 ---------------- */
export const getEmps = (params) => request.get('/emps', { params })
export const getEmp = (id) => request.get(`/emps/${id}`)
export const getAllEmps = () => request.get('/emps/list')
export const addEmp = (data) => request.post('/emps', data)
export const updateEmp = (data) => request.put('/emps', data)
export const deleteEmps = (ids) =>
  request.delete('/emps', { params: { ids: ids.join(',') } })

/* ---------------- 班级 ---------------- */
export const getClazzs = (params) => request.get('/clazzs', { params })
export const getAllClazzs = () => request.get('/clazzs/list')
export const getClazz = (id) => request.get(`/clazzs/${id}`)
export const addClazz = (data) => request.post('/clazzs', data)
export const updateClazz = (data) => request.put('/clazzs', data)
export const deleteClazz = (id) => request.delete(`/clazzs/${id}`)

/* ---------------- 学员 ---------------- */
export const getStudents = (params) => request.get('/students', { params })
export const getStudent = (id) => request.get(`/students/${id}`)
export const addStudent = (data) => request.post('/students', data)
export const updateStudent = (data) => request.put('/students', data)
export const deleteStudents = (ids) => request.delete(`/students/${ids.join(',')}`)
export const handleViolation = (id, score) =>
  request.put(`/students/violation/${id}/${score}`)

/* ---------------- 数据统计 ---------------- */
export const getEmpJobData = () => request.get('/report/empJobData')
export const getEmpGenderData = () => request.get('/report/empGenderData')
export const getStudentCountData = () => request.get('/report/studentCountData')
export const getStudentDegreeData = () => request.get('/report/studentDegreeData')

/* ---------------- 操作日志 ---------------- */
export const getLogPage = (params) => request.get('/log/page', { params })

/* ---------------- 文件上传 ---------------- */
export const uploadFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/upload', formData)
}