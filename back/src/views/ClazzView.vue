<template>
  <div class="page">
    <PageHeader title="班级管理" subtitle="维护班级信息、班主任与开课安排">
      <el-button type="primary" :icon="Plus" @click="openCreate">新增班级</el-button>
    </PageHeader>

    <div class="panel">
      <div class="filter-bar">
        <el-input
          v-model="filters.name"
          placeholder="班级名称"
          clearable
          :prefix-icon="Search"
          @keyup.enter="handleSearch"
        />
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="结课开始"
          end-placeholder="结课结束"
          value-format="YYYY-MM-DD"
        />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table v-loading="loading" :data="list" row-key="id" stripe>
        <el-table-column type="index" label="序号" :index="rowIndex" width="64" />
        <el-table-column prop="name" label="班级名称" min-width="150" />
        <el-table-column prop="room" label="教室" width="90" />
        <el-table-column prop="beginDate" label="开课日期" min-width="110" />
        <el-table-column prop="endDate" label="结课日期" min-width="110" />
        <el-table-column prop="masterName" label="班主任" min-width="100" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="clazzStatusType(row.status)" effect="light" round size="small">
              {{ row.status || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="修改时间" min-width="160">
          <template #default="{ row }">{{ formatDateTime(row.updateTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <TableEmpty description="暂无班级数据" />
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
      :title="isEdit ? '编辑班级' : '新增班级'"
      width="620px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
        <div class="form-grid">
          <el-form-item label="班级名称" prop="name" class="span-2">
            <el-input v-model="form.name" placeholder="如：JavaEE 就业 166 期" />
          </el-form-item>
          <el-form-item label="教室" prop="room">
            <el-input v-model="form.room" placeholder="如：101" />
          </el-form-item>
          <el-form-item label="学科" prop="subject">
            <el-select v-model="form.subject" placeholder="请选择学科" style="width: 100%">
              <el-option
                v-for="item in SUBJECT_OPTIONS"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="开课日期" prop="beginDate">
            <el-date-picker
              v-model="form.beginDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="选择开课日期"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="结课日期" prop="endDate">
            <el-date-picker
              v-model="form.endDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="选择结课日期"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="班主任" prop="masterId" class="span-2">
            <el-select
              v-model="form.masterId"
              filterable
              placeholder="请选择班主任"
              style="width: 100%"
            >
              <el-option
                v-for="emp in masters"
                :key="emp.id"
                :label="`${emp.name}（${emp.username}）`"
                :value="emp.id"
              />
            </el-select>
          </el-form-item>
        </div>
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
import { Plus, Search } from '@element-plus/icons-vue'
import { addClazz, deleteClazz, getAllEmps, getClazz, getClazzs, updateClazz } from '../api'
import { SUBJECT_OPTIONS, clazzStatusType } from '../constants/dictionaries'
import PageBar from '../components/PageBar.vue'
import PageHeader from '../components/PageHeader.vue'
import TableEmpty from '../components/TableEmpty.vue'
import { confirmDelete } from '../utils/confirm'
import { formatDateTime, pageIndex } from '../utils/format'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const masters = ref([])
let mastersLoaded = false
const dateRange = ref([])

const createFilters = () => ({ name: '', page: 1, pageSize: 10 })
const filters = reactive(createFilters())
const rowIndex = pageIndex(filters)

const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref()

const emptyForm = () => ({
  id: null,
  name: '',
  room: '',
  beginDate: '',
  endDate: '',
  masterId: null,
  subject: null
})

const form = reactive(emptyForm())
const isEdit = computed(() => form.id != null)

const rules = {
  name: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  room: [{ required: true, message: '请输入教室', trigger: 'blur' }],
  subject: [{ required: true, message: '请选择学科', trigger: 'change' }],
  beginDate: [{ required: true, message: '请选择开课日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结课日期', trigger: 'change' }],
  masterId: [{ required: true, message: '请选择班主任', trigger: 'change' }]
}

async function loadData() {
  loading.value = true
  try {
    const params = { page: filters.page, pageSize: filters.pageSize }
    if (filters.name.trim()) params.name = filters.name.trim()
    if (dateRange.value?.length === 2) {
      params.begin = dateRange.value[0]
      params.end = dateRange.value[1]
    }

    const data = await getClazzs(params)
    list.value = data?.rows || []
    total.value = data?.total || 0
  } catch {
    // 错误提示由响应拦截器统一处理
  } finally {
    loading.value = false
  }
}

async function ensureMasters() {
  if (mastersLoaded) return
  try {
    masters.value = (await getAllEmps()) || []
    mastersLoaded = true
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

function resetForm() {
  Object.assign(form, emptyForm())
  formRef.value?.clearValidate()
}

function openCreate() {
  ensureMasters()
  resetForm()
  dialogVisible.value = true
}

async function openEdit(row) {
  ensureMasters()
  try {
    // 列表接口未返回 masterId / subject，编辑前查询详情
    const data = await getClazz(row.id)
    Object.assign(form, {
      id: data.id,
      name: data.name,
      room: data.room,
      beginDate: data.beginDate,
      endDate: data.endDate,
      masterId: data.masterId,
      subject: data.subject
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
      await updateClazz({ ...form })
      ElMessage.success('修改成功')
    } else {
      await addClazz({ ...form })
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

async function handleDelete(row) {
  try {
    await confirmDelete(
      `确定删除班级「${row.name}」吗？班级下有学员时将无法删除。`
    )
  } catch {
    return
  }

  try {
    await deleteClazz(row.id)
    ElMessage.success('删除成功')
    if (list.value.length === 1 && filters.page > 1) filters.page -= 1
    loadData()
  } catch {
    // 错误提示由响应拦截器统一处理
  }
}

onMounted(loadData)
</script>