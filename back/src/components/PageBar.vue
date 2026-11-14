<template>
  <el-pagination
    class="pager"
    background
    :total="total"
    :current-page="page"
    :page-size="pageSize"
    :page-sizes="[5, 10, 20, 50]"
    layout="total, sizes, prev, pager, next, jumper"
    @current-change="onPageChange"
    @size-change="onSizeChange"
  />
</template>

<script setup>
defineProps({
  total: { type: Number, default: 0 },
  page: { type: Number, default: 1 },
  pageSize: { type: Number, default: 10 }
})

const emit = defineEmits(['update:page', 'update:pageSize', 'change'])

function onPageChange(page) {
  emit('update:page', page)
  emit('change')
}

function onSizeChange(size) {
  emit('update:pageSize', size)
  emit('update:page', 1)
  emit('change')
}
</script>