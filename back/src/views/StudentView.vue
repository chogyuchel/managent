<template>
  <div class="page">
    <PageHeader title="学员管理" subtitle="维护学员档案、班级归属与违纪记录">
      <el-button type="primary" :icon="Plus" @click="openCreate">新增学员</el-button>
    </PageHeader>

    <div class="panel">
      <div class="filter-bar">
        <el-input
          v-model="filters.name"
          placeholder="学员姓名"
          clearable
          :prefix-icon="Search"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="filters.degree" placeholder="学历" clearable>
          <el-option
            v-for="item in DEGREE_OPTIONS"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-select v-model="filters.clazzId" placeholder="班级" clearable filterable>
          <el-option
            v-for="clazz in clazzs"
            :key="clazz.id"
            :label="clazz.name"
            :value="clazz.id"
          />
        </el-select>
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
        <el-table-column prop="name" label="姓名" min-width="80" />
        <el-table-column prop="no" label="学号" min-width="110" />
        <el-table-column label="性别" width="64">
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
        <el-table-column prop="phone" label="手机号" min-width="116" />
        <el-table-column label="学历" width="70">
          <template #default="{ row }">{{ labelOf(DEGREE_OPTIONS, row.degree) }}</template>
        </el-table-column>
        <el-table-column prop="clazzName" label="班级" min-width="118" />
        <el-table-column prop="violationCount" label="违纪次数" width="78" />
        <el-table-column prop="violationScore" label="扣分" width="60" />
        <el-table-column prop="graduationDate" label="毕业日期" width="100" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="warning" @click="openViolation(row)">违纪</el-button>
            <el-button link type="danger" @click="confirmAndDelete([row.id])">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <TableEmpty description="暂无学员数据" />
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
      :title="isEdit ? '编辑学员' : '新增学员'"
      width="680px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
        <div class="form-grid">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="form.name" placeholder="学员姓名" />
          </el-form-item>
          <el-form-item label="学号" prop="no">
            <el-input v-model="form.no" placeholder="学号" />
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
          <el-form-item label="身份证号" prop="idCard">
            <el-input v-model="form.idCard" maxlength="18" placeholder="身份证号" />
          </el-form-item>
          <el-form-item label="学历" prop="degree">
            <el-select v-model="form.degree" placeholder="请选择学历" style="width: 100%">
              <el-option
                v-for="item in DEGREE_OPTIONS"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="院校毕业">
            <el-radio-group v-model="form.isCollege">
              <el-radio :value="1">是</el-radio>
              <el-radio :value="0">否</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="毕业日期">
            <el-date-picker
              v-model="form.graduationDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="选择毕业日期"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="所属班级" prop="clazzId">
            <el-select v-model="form.clazzId" placeholder="请选择班级" filterable style="width: 100%">
              <el-option
                v-for="clazz in clazzs"
                :key="clazz.id"
                :label="clazz.name"
                :value="clazz.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="联系地址" class="span-2">
            <el-input v-model="form.address" placeholder="联系地址" />
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="violationVisible" title="违纪处理" width="420px" @closed="violationStudent = null">
      <p class="violation-tip">
        为学员「<strong>{{ violationStudent?.name }}</strong
        >」记录一次违纪，并扣除相应分数。
      </p>
      <div class="violation-row">
        <span class="violation-label">扣分分值</span>
        <el-input-number v-model="violationScore" :min="1" :max="10" />
      </div>
      <template #footer>
        <el-button @click="violationVisible = false">取消</el-button>
        <el-button type="warning" :loading="violationSubmitting" @click="submitViolation">
          确认处理
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete, Plus, Search } from '@element-plus/icons-vue'
import {
  addStudent,
  deleteStudents,
  getAllClazzs,
  getStudent,
  getStudents,
  handleViolation,
  updateStudent
} from '../api'
import {
  DEGREE_OPTIONS,
  GENDER_OPTIONS,
  labelOf,
  tagTypeOf
} from '../constants/dictionaries'
import PageBar from '../components/PageBar.vue'
import PageHeader from '../components/PageHeader.vue'
import TableEmpty from '../components/TableEmpty.vue'
import { confirmDelete } from '../utils/confirm'
import { pageIndex } from '../utils/format'
import { phoneRule } from '../utils/rules'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const clazzs = ref([])
let clazzsLoaded = false
const selectedIds = ref([])

const createFilters = () => ({ name: '', degree: null, clazzId: null, page: 1, pageSize: 10 })
const filters = reactive(createFilters())
const rowIndex = pageIndex(filters)

const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref()

const emptyForm = () => ({
  id: null,
  name: '',
  no: '',
  gender: 1,
  phone: '',
  idCard: '',
  isCollege: 0,
  address: '',
  degree: null,
  graduationDate: '',
  clazzId: null
})

const form = reactive(emptyForm())
const isEdit = computed(() => form.id != null)

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  no: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  degree: [{ required: true, message: '请选择学历', trigger: 'change' }],
  clazzId: [{ required: true, message: '请选择班级', trigger: 'change' }],
  phone: [phoneRule]
}

const violationVisible = ref(false)
const violationSubmitting = ref(false)
const violationStudent = ref(null)
const violationScore = ref(1)

async function loadData() {
  loading.value = true
  try {
    const params = { page: filters.page, pageSize: filters.pageSize }
    if (filters.name.trim()) params.name = filters.name.trim()
    if (filters.degree) params.degree = filters.degree
    if (filters.clazzId) params.clazzId = filters.clazzId

    const data = await getStudents(params)
    list.value = data?.rows || []
    total.value = data?.total || 0
  } catch {
    // 错误提示由响应拦截器统一处理
  } finally {
    loading.value = false
  }
}

async function ensureClazzs() {
  if (clazzsLoaded) return
  try {
    clazzs.value = (await getAllClazzs()) || []
    clazzsLoaded = true
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
  loadData()
}

function handleSelectionChange(rows) {
  selectedIds.value = rows.map((row) => row.id)
}

function resetForm() {
  Object.assign(form, emptyForm())
  formRef.value?.clearValidate()
}

function openCreate() {
  ensureClazzs()
  resetForm()
  dialogVisible.value = true
}

async function openEdit(row) {
  ensureClazzs()
  try {
    const data = await getStudent(row.id)
    Object.assign(form, {
      ...emptyForm(),
      id: data.id,
      name: data.name,
      no: data.no,
      gender: data.gender,
      phone: data.phone,
      idCard: data.idCard,
      isCollege: data.isCollege,
      address: data.address,
      degree: data.degree,
      graduationDate: data.graduationDate,
      clazzId: data.clazzId
    })
    dialogVisible.value = true
  } catch {
    // 错误提示由响应拦截器统一处理
  }
}

async function submit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateStudent({ ...form })
      ElMessage.success('修改成功')
    } else {
      await addStudent({ ...form })
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

function openViolation(row) {
  violationStudent.value = row
  violationScore.value = 1
  violationVisible.value = true
}

async function submitViolation() {
  violationSubmitting.value = true
  try {
    await handleViolation(violationStudent.value.id, violationScore.value)
    ElMessage.success('违纪处理完成')
    violationVisible.value = false
    loadData()
  } catch {
    // 错误提示由响应拦截器统一处理
  } finally {
    violationSubmitting.value = false
  }
}

async function confirmAndDelete(ids) {
  if (!ids.length) return
  try {
    await confirmDelete(`确定删除选中的 ${ids.length} 名学员吗？`)
  } catch {
    return
  }

  try {
    await deleteStudents(ids)
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
.violation-tip {
  margin: 0 0 18px;
  color: var(--app-muted);
  font-size: 13px;
}

.violation-tip strong {
  color: var(--app-text);
}

.violation-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.violation-label {
  font-size: 13px;
  color: #5a6478;
}
</style>