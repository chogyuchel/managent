<template>
  <div class="page">
    <PageHeader title="部门管理" subtitle="维护组织架构中的部门信息">
      <el-button type="primary" :icon="Plus" @click="openCreate">新增部门</el-button>
    </PageHeader>

    <div class="panel">
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column type="index" label="序号" width="64" />
        <el-table-column prop="name" label="部门名称" min-width="200" />
        <el-table-column label="创建时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="修改时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.updateTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <TableEmpty description="暂无部门数据" />
        </template>
      </el-table>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑部门' : '新增部门'"
      width="440px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="form.name" maxlength="20" show-word-limit placeholder="请输入部门名称" />
        </el-form-item>
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
import { Plus } from '@element-plus/icons-vue'
import { addDept, deleteDept, getDepts, updateDept } from '../api'
import PageHeader from '../components/PageHeader.vue'
import TableEmpty from '../components/TableEmpty.vue'
import { confirmDelete } from '../utils/confirm'
import { formatDateTime } from '../utils/format'

const loading = ref(false)
const list = ref([])

const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref()
const form = reactive({ id: null, name: '' })
const isEdit = computed(() => form.id != null)

const rules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    list.value = (await getDepts()) || []
  } catch {
    // 错误提示由响应拦截器统一处理
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.id = null
  form.name = ''
  formRef.value?.clearValidate()
}

function openCreate() {
  resetForm()
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, { id: row.id, name: row.name })
  dialogVisible.value = true
}

async function submit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateDept({ id: form.id, name: form.name })
      ElMessage.success('修改成功')
    } else {
      await addDept({ name: form.name })
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
    await confirmDelete(`确定删除部门「${row.name}」吗？`)
  } catch {
    return
  }

  try {
    await deleteDept(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 错误提示由响应拦截器统一处理
  }
}

onMounted(loadData)
</script>