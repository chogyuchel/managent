<template>
  <div class="page">
    <PageHeader title="员工管理" subtitle="维护员工档案、岗位信息与工作经历">
      <el-button type="primary" :icon="Plus" @click="openCreate">新增员工</el-button>
    </PageHeader>

    <div class="panel">
      <div class="filter-bar">
        <el-input
          v-model="filters.name"
          placeholder="员工姓名"
          clearable
          :prefix-icon="Search"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="filters.gender" placeholder="性别" clearable>
          <el-option
            v-for="item in GENDER_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="入职开始"
          end-placeholder="入职结束"
          value-format="YYYY-MM-DD"
        />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
        <div class="filter-spacer"></div>
        <el-button
          type="danger"
          plain
          :icon="Delete"
          :disabled="!selectedIds.length"
          @click="confirmAndDelete(selectedIds)"
        >
          批量删除
        </el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="list"
        row-key="id"
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="44" />
        <el-table-column type="index" label="序号" :index="rowIndex" width="64" />
        <el-table-column label="头像" width="68">
          <template #default="{ row }">
            <el-avatar :size="34" :src="row.image" class="emp-avatar app-gradient">
              {{ row.name?.slice(0, 1) }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" min-width="110" />
        <el-table-column prop="name" label="姓名" min-width="90" />
        <el-table-column label="性别" width="70">
          <template #default="{ row }">
            <el-tag
              :type="tagTypeOf(GENDER_OPTIONS, row.gender)"
              effect="light"
              round
              size="small"
            >
              {{ labelOf(GENDER_OPTIONS, row.gender) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" min-width="120" />
        <el-table-column label="职位" width="100">
          <template #default="{ row }">{{ labelOf(JOB_OPTIONS, row.job) }}</template>
        </el-table-column>
        <el-table-column label="薪资" width="100">
          <template #default="{ row }">
            {{ row.salary != null ? `¥${Number(row.salary).toLocaleString()}` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="deptName" label="部门" min-width="90" />
        <el-table-column prop="entryDate" label="入职日期" min-width="110" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="confirmAndDelete([row.id])">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <TableEmpty description="暂无员工数据" />
        </template>
      </el-table>

      <PageBar
        v-model:page="filters.page"
        v-model:page-size="filters.pageSize"
        :total="total"
        @change="loadData"
      />
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑员工' : '新增员工'"
      width="760px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
        <div class="form-grid">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="登录用户名" />
          </el-form-item>
          <el-form-item v-if="isEdit" label="密码">
            <el-input
              v-model="form.password"
              type="password"
              show-password
              placeholder="留空则不修改密码"
            />
          </el-form-item>
          <el-form-item label="姓名" prop="name">
            <el-input v-model="form.name" placeholder="员工姓名" />
          </el-form-item>
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="form.gender">
              <el-radio v-for="item in GENDER_OPTIONS" :key="item.value" :value="item.value">
                {{ item.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" maxlength="11" placeholder="11 位手机号" />
          </el-form-item>
          <el-form-item label="职位" prop="job">
            <el-select v-model="form.job" placeholder="请选择职位" style="width: 100%">
              <el-option
                v-for="item in JOB_OPTIONS"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="薪资">
            <el-input-number
              v-model="form.salary"
              :min="0"
              :step="500"
              controls-position="right"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="部门" prop="deptId">
            <el-select v-model="form.deptId" placeholder="请选择部门" style="width: 100%">
              <el-option
                v-for="dept in depts"
                :key="dept.id"
                :label="dept.name"
                :value="dept.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="入职日期" prop="entryDate">
            <el-date-picker
              v-model="form.entryDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="选择日期"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="头像">
            <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :http-request="handleUpload"
              accept="image/*"
            >
              <div v-loading="uploading" class="avatar-box">
                <img v-if="form.image" :src="form.image" alt="头像" />
                <el-icon v-else :size="22"><Plus /></el-icon>
              </div>
            </el-upload>
          </el-form-item>
        </div>

        <el-divider content-position="left">工作经历</el-divider>

        <div v-for="(expr, index) in form.exprList" :key="index" class="expr-row">
          <el-input v-model="expr.company" placeholder="公司名称" />
          <el-input v-model="expr.job" placeholder="职位" />
          <el-date-picker
            v-model="expr.begin"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="开始日期"
          />
          <el-date-picker
            v-model="expr.end"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="结束日期"
          />
          <el-button
            :icon="Delete"
            circle
            plain
            type="danger"
            @click="form.exprList.splice(index, 1)"
          />
        </div>

        <el-button link type="primary" :icon="Plus" @click="addExpr">添加工作经历</el-button>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete, Plus, Search } from '@element-plus/icons-vue'
import {
  addEmp,
  deleteEmps,
  getDepts,
  getEmp,
  getEmps,
  updateEmp,
  uploadFile
} from '../api'
import { GENDER_OPTIONS, JOB_OPTIONS, labelOf, tagTypeOf } from '../constants/dictionaries'
import PageBar from '../components/PageBar.vue'
import PageHeader from '../components/PageHeader.vue'
import TableEmpty from '../components/TableEmpty.vue'
import { confirmDelete } from '../utils/confirm'
import { pageIndex } from '../utils/format'
import { phoneRule } from '../utils/rules'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const depts = ref([])
let deptsLoaded = false
const selectedIds = ref([])
const dateRange = ref([])

const createFilters = () => ({ name: '', gender: null, page: 1, pageSize: 10 })
const filters = reactive(createFilters())
const rowIndex = pageIndex(filters)

const dialogVisible = ref(false)
const submitting = ref(false)
const uploading = ref(false)
const formRef = ref()

const emptyForm = () => ({
  id: null,
  username: '',
  password: '',
  name: '',
  gender: 1,
  phone: '',
  job: null,
  salary: null,
  image: '',
  entryDate: '',
  deptId: null,
  exprList: []
})

const form = reactive(emptyForm())
const isEdit = computed(() => form.id != null)

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [phoneRule],
  job: [{ required: true, message: '请选择职位', trigger: 'change' }],
  deptId: [{ required: true, message: '请选择部门', trigger: 'change' }],
  entryDate: [{ required: true, message: '请选择入职日期', trigger: 'change' }]
}

async function loadData() {
  loading.value = true
  try {
    const params = { page: filters.page, pageSize: filters.pageSize }
    if (filters.name.trim()) params.name = filters.name.trim()
    if (filters.gender) params.gender = filters.gender
    if (dateRange.value?.length === 2) {
      params.begin = dateRange.value[0]
      params.end = dateRange.value[1]
    }

    const data = await getEmps(params)
    list.value = data?.rows || []
    total.value = data?.total || 0
  } catch {
    // 错误提示由响应拦截器统一处理
  } finally {
    loading.value = false
  }
}

async function ensureDepts() {
  if (deptsLoaded) return
  try {
    depts.value = (await getDepts()) || []
    deptsLoaded = true
  } catch {
    // 错误提示由响应拦截器统一处理
  }
}

function handleSearch() {
  filters.page = 1
  loadData()
}

function handleReset() {
  Object.assign(filters, createFilters())
  dateRange.value = []
  loadData()
}

function handleSelectionChange(rows) {
  selectedIds.value = rows.map((row) => row.id)
}

function resetForm() {
  Object.assign(form, emptyForm())
  formRef.value?.clearValidate()
}

function addExpr() {
  form.exprList.push({ company: '', job: '', begin: '', end: '' })
}

function openCreate() {
  ensureDepts()
  resetForm()
  dialogVisible.value = true
}

async function openEdit(row) {
  ensureDepts()
  try {
    const data = await getEmp(row.id)
    Object.assign(form, {
      ...emptyForm(),
      id: data.id,
      username: data.username,
      password: '',
      name: data.name,
      gender: data.gender,
      phone: data.phone,
      job: data.job,
      salary: data.salary,
      image: data.image,
      entryDate: data.entryDate,
      deptId: data.deptId,
      exprList: (data.exprList || []).map((item) => ({
        company: item.company,
        job: item.job,
        begin: item.begin,
        end: item.end
      }))
    })
    dialogVisible.value = true
  } catch {
    // 错误提示由响应拦截器统一处理
  }
}

async function handleUpload({ file }) {
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过 10MB')
    return
  }

  uploading.value = true
  try {
    const url = await uploadFile(file)
    form.image = url
    ElMessage.success('头像上传成功')
  } catch {
    // 错误提示由响应拦截器统一处理
  } finally {
    uploading.value = false
  }
}

async function submit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  const payload = {
    ...form,
    exprList: form.exprList.filter(
      (item) => item.company || item.job || item.begin || item.end
    )
  }

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateEmp(payload)
      ElMessage.success('修改成功')
    } else {
      await addEmp(payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch {
    // 错误提示由响应拦截器统一处理
  } finally {
    submitting.value = false
  }
}

async function confirmAndDelete(ids) {
  if (!ids.length) return
  try {
    await confirmDelete(`确定删除选中的 ${ids.length} 名员工吗？`)
  } catch {
    return
  }

  try {
    await deleteEmps(ids)
    ElMessage.success('删除成功')
    if (list.value.length === ids.length && filters.page > 1) filters.page -= 1
    loadData()
  } catch {
    // 错误提示由响应拦截器统一处理
  }
}

onMounted(loadData)
</script>

<style scoped>
.emp-avatar {
  font-size: 13px;
  font-weight: 600;
}

.avatar-uploader :deep(.el-upload) {
  border-radius: 12px;
}

.avatar-box {
  width: 84px;
  height: 84px;
  border-radius: 12px;
  border: 1px dashed #d5dced;
  background: #f8fafd;
  display: grid;
  place-items: center;
  color: #9aa3b5;
  overflow: hidden;
  transition: border-color 0.18s ease;
}

.avatar-box:hover {
  border-color: var(--el-color-primary);
}

.avatar-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.expr-row {
  display: grid;
  grid-template-columns: 1.2fr 1fr 1fr 1fr auto;
  gap: 8px;
  margin-bottom: 10px;
}

@media (max-width: 720px) {
  .expr-row {
    grid-template-columns: 1fr 1fr;
  }
}
</style>