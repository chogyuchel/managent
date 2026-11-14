<template>
  <div class="page">
    <PageHeader title="操作日志" subtitle="记录增删改操作的人员、参数与耗时">
      <el-button :icon="Refresh" :loading="loading" @click="loadData">刷新</el-button>
    </PageHeader>

    <div class="panel">
      <el-table v-loading="loading" :data="list" row-key="id" stripe>
        <el-table-column type="index" label="序号" :index="rowIndex" width="64" />
        <el-table-column label="操作人" min-width="96">
          <template #default="{ row }">
            {{ operatorName(row) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="className"
          label="类名"
          min-width="190"
          show-overflow-tooltip
        />
        <el-table-column prop="methodName" label="方法" width="90" />
        <el-table-column
          prop="methodParams"
          label="请求参数"
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column
          prop="returnValue"
          label="返回值"
          min-width="160"
          show-overflow-tooltip
        />
        <el-table-column label="耗时" width="80">
          <template #default="{ row }">{{ row.costTime }} ms</template>
        </el-table-column>
        <el-table-column label="操作时间" width="160">
          <template #default="{ row }">{{ formatDateTime(row.operateTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="70" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <TableEmpty description="暂无操作日志" />
        </template>
      </el-table>

      <PageBar
        v-model:page="filters.page"
        v-model:page-size="filters.pageSize"
        :total="total"
        @change="loadData"
      />
    </div>

    <el-dialog v-model="detailVisible" title="日志详情" width="720px" @closed="detail = null">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="操作人">
          {{ operatorName(detail) }}
        </el-descriptions-item>
        <el-descriptions-item label="操作时间">
          {{ formatDateTime(detail?.operateTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="类名" :span="2">
          {{ detail?.className }}
        </el-descriptions-item>
        <el-descriptions-item label="方法名">{{ detail?.methodName }}</el-descriptions-item>
        <el-descriptions-item label="耗时">{{ detail?.costTime }} ms</el-descriptions-item>
      </el-descriptions>

      <h4 class="detail-title">请求参数</h4>
      <pre class="detail-block">{{ detail?.methodParams || '-' }}</pre>

      <h4 class="detail-title">返回值</h4>
      <pre class="detail-block">{{ detail?.returnValue || '-' }}</pre>

      <template #footer>
        <el-button type="primary" @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { getLogPage } from '../api'
import PageBar from '../components/PageBar.vue'
import PageHeader from '../components/PageHeader.vue'
import TableEmpty from '../components/TableEmpty.vue'
import { formatDateTime, pageIndex } from '../utils/format'

const loading = ref(false)
const list = ref([])
const total = ref(0)

const filters = reactive({
  page: 1,
  pageSize: 10
})
const rowIndex = pageIndex(filters)

const detailVisible = ref(false)
const detail = ref(null)

async function loadData() {
  loading.value = true
  try {
    const data = await getLogPage({ page: filters.page, pageSize: filters.pageSize })
    list.value = data?.rows || []
    total.value = data?.total || 0
  } catch {
    // 错误提示由响应拦截器统一处理
  } finally {
    loading.value = false
  }
}

function operatorName(log) {
  return log?.operateEmpName || `员工 #${log?.operateEmpId}`
}

function openDetail(row) {
  detail.value = row
  detailVisible.value = true
}

onMounted(loadData)
</script>

<style scoped>
.detail-title {
  margin: 18px 0 8px;
  font-size: 13px;
  font-weight: 600;
  color: var(--app-text);
}

.detail-block {
  margin: 0;
  padding: 12px 14px;
  border-radius: 10px;
  background: #f7f9fd;
  border: 1px solid var(--app-border);
  color: #3b4560;
  font-size: 12px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 220px;
  overflow: auto;
}
</style>