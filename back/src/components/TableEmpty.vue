<template>
  <el-empty :description="description" :image-size="90" />
</template>

<script setup>
defineProps({
  description: { type: String, default: '暂无数据' }
})
</script>