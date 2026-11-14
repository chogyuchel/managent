<template>
  <div class="page">
    <PageHeader title="数据总览" subtitle="员工与学员数据的实时统计">
      <el-button :icon="Refresh" :loading="loading" @click="loadData">刷新数据</el-button>
    </PageHeader>

    <div class="stat-grid">
      <div
        v-for="card in statCards"
        :key="card.label"
        class="stat-card"
        :style="{ '--accent': card.color, '--accent-bg': card.bg }"
      >
        <div class="stat-icon">
          <el-icon :size="22"><component :is="card.icon" /></el-icon>
        </div>
        <div class="stat-body">
          <div class="stat-value">{{ card.value }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </div>
      </div>
    </div>

    <div class="chart-grid" v-loading="loading">
      <div class="panel chart-panel">
        <div class="chart-head">
          <h3>员工职位分布</h3>
          <span>单位：人</span>
        </div>
        <div ref="jobRef" class="chart"></div>
      </div>

      <div class="panel chart-panel">
        <div class="chart-head">
          <h3>员工性别构成</h3>
          <span>单位：人</span>
        </div>
        <div ref="genderRef" class="chart"></div>
      </div>

      <div class="panel chart-panel">
        <div class="chart-head">
          <h3>班级人数分布</h3>
          <span>单位：人</span>
        </div>
        <div ref="clazzRef" class="chart"></div>
      </div>

      <div class="panel chart-panel">
        <div class="chart-head">
          <h3>学员学历构成</h3>
          <span>单位：人</span>
        </div>
        <div ref="degreeRef" class="chart"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts/core'
import { BarChart, PieChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { Avatar, OfficeBuilding, Refresh, School, UserFilled } from '@element-plus/icons-vue'
import PageHeader from '../components/PageHeader.vue'
import {
  getDepts,
  getEmpGenderData,
  getEmpJobData,
  getStudentCountData,
  getStudentDegreeData
} from '../api'

echarts.use([
  BarChart,
  PieChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
  CanvasRenderer
])

const AXIS_COLOR = '#7b8499'
const SPLIT_COLOR = '#eef1f7'
const GENDER_COLORS = ['#3d6bff', '#ff7aa2']
const DEGREE_COLORS = ['#4f7cff', '#36c6a8', '#ffb020', '#ff7aa2', '#8b5cf6', '#20b2aa']

const barGradient = (top, bottom, horizontal = false) =>
  new echarts.graphic.LinearGradient(0, 0, horizontal ? 1 : 0, horizontal ? 0 : 1, [
    { offset: 0, color: top },
    { offset: 1, color: bottom }
  ])

const peopleTooltip = () => ({
  trigger: 'axis',
  axisPointer: { type: 'shadow' },
  valueFormatter: (value) => `${value} 人`
})

const pieLegend = (itemGap = 14) => ({
  bottom: 0,
  icon: 'circle',
  itemWidth: 8,
  itemHeight: 8,
  itemGap,
  textStyle: { color: AXIS_COLOR }
})

const loading = ref(false)
const jobRef = ref()
const genderRef = ref()
const clazzRef = ref()
const degreeRef = ref()

const stats = ref({ emp: 0, student: 0, clazz: 0, dept: 0 })
let charts = []

const statCards = computed(() => [
  { label: '员工总数', value: stats.value.emp, icon: UserFilled, color: '#3d6bff', bg: '#eef2ff' },
  { label: '学员总数', value: stats.value.student, icon: Avatar, color: '#12b886', bg: '#e6f8f2' },
  { label: '班级总数', value: stats.value.clazz, icon: School, color: '#f59f00', bg: '#fff6e5' },
  { label: '部门总数', value: stats.value.dept, icon: OfficeBuilding, color: '#8b5cf6', bg: '#f3edff' }
])

const sum = (list) => (list || []).reduce((total, item) => total + Number(item || 0), 0)

function initChart(el, option) {
  if (!el) return
  const chart = echarts.getInstanceByDom(el) || echarts.init(el)
  chart.setOption(option, true)
  if (!charts.includes(chart)) charts.push(chart)
}

function clearCharts() {
  charts.forEach((chart) => chart.dispose())
  charts = []
}

function jobBarOption(job) {
  return {
    color: ['#3d6bff'],
    tooltip: peopleTooltip(),
    grid: { left: 8, right: 16, top: 34, bottom: 6, containLabel: true },
    xAxis: {
      type: 'category',
      data: job.jobList || [],
      axisTick: { show: false },
      axisLine: { lineStyle: { color: '#e3e8f2' } },
      axisLabel: { color: AXIS_COLOR, interval: 0 }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      splitLine: { lineStyle: { color: SPLIT_COLOR } },
      axisLabel: { color: AXIS_COLOR }
    },
    series: [
      {
        type: 'bar',
        data: job.dataList || [],
        barMaxWidth: 30,
        itemStyle: {
          borderRadius: [8, 8, 0, 0],
          color: barGradient('#5b83ff', '#3d6bff')
        },
        label: { show: true, position: 'top', color: '#5a6478', fontWeight: 600 }
      }
    ]
  }
}

function genderPieOption(gender) {
  const data = (gender || []).map((item) => ({
    name: item.name,
    value: Number(item.value || 0)
  }))
  return {
    color: GENDER_COLORS,
    tooltip: { trigger: 'item', formatter: '{b}<br/>{c} 人（{d}%）' },
    legend: pieLegend(18),
    series: [
      {
        type: 'pie',
        radius: ['50%', '70%'],
        center: ['50%', '44%'],
        itemStyle: { borderColor: '#fff', borderWidth: 3 },
        label: { show: false },
        emphasis: {
          scale: true,
          scaleSize: 6,
          label: {
            show: true,
            fontSize: 15,
            fontWeight: 600,
            color: '#1f2a44',
            formatter: '{b}\n{c} 人'
          }
        },
        data
      }
    ]
  }
}

function clazzBarOption(clazz) {
  return {
    tooltip: peopleTooltip(),
    grid: { left: 8, right: 34, top: 14, bottom: 6, containLabel: true },
    xAxis: {
      type: 'value',
      minInterval: 1,
      splitLine: { lineStyle: { color: SPLIT_COLOR } },
      axisLabel: { color: AXIS_COLOR }
    },
    yAxis: {
      type: 'category',
      data: clazz.clazzList || [],
      axisTick: { show: false },
      axisLine: { show: false },
      axisLabel: { color: AXIS_COLOR, width: 92, overflow: 'truncate' }
    },
    series: [
      {
        type: 'bar',
        data: clazz.dataList || [],
        barMaxWidth: 16,
        itemStyle: {
          borderRadius: [0, 8, 8, 0],
          color: barGradient('#36c6a8', '#5ad8bd', true)
        },
        label: { show: true, position: 'right', color: '#5a6478', fontSize: 12 }
      }
    ]
  }
}

function degreePieOption(degree) {
  const data = (degree || []).map((item) => ({
    name: item.name,
    value: Number(item.value || 0)
  }))
  return {
    color: DEGREE_COLORS,
    tooltip: { trigger: 'item', formatter: '{b}<br/>{c} 人（{d}%）' },
    legend: pieLegend(),
    series: [
      {
        type: 'pie',
        radius: '62%',
        center: ['50%', '43%'],
        roseType: 'radius',
        itemStyle: { borderColor: '#fff', borderWidth: 3, borderRadius: 6 },
        label: { color: '#5a6478', fontSize: 12, formatter: '{b} {d}%' },
        labelLine: { length: 10, length2: 8 },
        data
      }
    ]
  }
}

function renderCharts(job, gender, clazz, degree) {
  initChart(jobRef.value, jobBarOption(job || {}))
  initChart(genderRef.value, genderPieOption(gender))
  initChart(clazzRef.value, clazzBarOption(clazz || {}))
  initChart(degreeRef.value, degreePieOption(degree))
}

async function loadData() {
  loading.value = true
  try {
    const [job, gender, clazz, degree, depts] = await Promise.all([
      getEmpJobData(),
      getEmpGenderData(),
      getStudentCountData(),
      getStudentDegreeData(),
      getDepts()
    ])

    stats.value = {
      emp: sum(job?.dataList),
      student: sum((degree || []).map((item) => item.value)),
      clazz: (clazz?.clazzList || []).length,
      dept: Array.isArray(depts) ? depts.length : 0
    }

    await nextTick()
    renderCharts(job, gender, clazz, degree)
  } catch {
    // 错误提示由响应拦截器统一处理
  } finally {
    loading.value = false
  }
}

let resizeFrame = 0

function handleResize() {
  if (resizeFrame) return
  resizeFrame = requestAnimationFrame(() => {
    resizeFrame = 0
    charts.forEach((chart) => chart.resize())
  })
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  cancelAnimationFrame(resizeFrame)
  clearCharts()
})
</script>

<style scoped>
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: var(--app-card-radius);
  box-shadow: var(--app-shadow);
  position: relative;
  overflow: hidden;
}

.stat-card::after {
  content: '';
  position: absolute;
  right: -26px;
  top: -26px;
  width: 92px;
  height: 92px;
  border-radius: 50%;
  background: var(--accent-bg);
  opacity: 0.9;
}

.stat-icon {
  width: 48px;
  height: 48px;
  flex-shrink: 0;
  display: grid;
  place-items: center;
  border-radius: 13px;
  color: var(--accent);
  background: var(--accent-bg);
  position: relative;
  z-index: 1;
}

.stat-body {
  position: relative;
  z-index: 1;
}

.stat-value {
  font-size: 27px;
  font-weight: 700;
  line-height: 1.1;
  letter-spacing: -0.5px;
}

.stat-label {
  margin-top: 5px;
  font-size: 12px;
  color: var(--app-muted);
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  min-height: 360px;
}

.chart-panel {
  padding: 18px 20px 14px;
}

.chart-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 6px;
}

.chart-head h3 {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
}

.chart-head span {
  font-size: 11px;
  color: #a1a9bb;
}

.chart {
  height: 280px;
  width: 100%;
}

@media (max-width: 1100px) {
  .stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 860px) {
  .chart-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 560px) {
  .stat-grid {
    grid-template-columns: 1fr;
  }
}
</style>