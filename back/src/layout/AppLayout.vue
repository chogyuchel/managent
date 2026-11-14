<template>
  <el-container class="layout">
    <el-aside :width="collapsed ? '76px' : '228px'" class="sidebar">
      <div class="brand">
        <div class="brand-mark app-gradient">T</div>
        <transition name="fade">
          <div v-show="!collapsed" class="brand-text">
            <strong>Tlias</strong>
            <span>教学管理系统</span>
          </div>
        </transition>
      </div>

      <el-menu
        :default-active="route.path"
        :collapse="collapsed"
        :collapse-transition="false"
        router
        class="side-menu"
        background-color="transparent"
      >
        <el-menu-item v-for="menu in menus" :key="menu.path" :index="menu.path">
          <el-icon><component :is="menu.icon" /></el-icon>
          <template #title>{{ menu.title }}</template>
        </el-menu-item>
      </el-menu>

      <div v-show="!collapsed" class="sidebar-foot">数据驱动的教学管理平台</div>
    </el-aside>

    <el-container class="main-wrap">
      <el-header class="topbar">
        <div class="topbar-left">
          <el-button text class="collapse-btn" @click="collapsed = !collapsed">
            <el-icon :size="18">
              <component :is="collapsed ? Expand : Fold" />
            </el-icon>
          </el-button>
          <div class="crumb">
            <span>首页</span>
            <el-icon :size="12"><ArrowRight /></el-icon>
            <strong>{{ pageTitle }}</strong>
          </div>
        </div>

        <el-dropdown trigger="click" @command="handleLogout">
          <div class="user-chip">
            <el-avatar :size="32" class="user-avatar app-gradient">
              {{ avatarText }}
            </el-avatar>
            <span class="user-name">{{ displayName }}</span>
            <el-icon :size="12"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="page" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, ArrowRight, Expand, Fold, SwitchButton } from '@element-plus/icons-vue'
import { clearAuth, getUser } from '../utils/auth'

const route = useRoute()
const router = useRouter()
const collapsed = ref(false)

const menus = (router.options.routes.find((item) => item.path === '/')?.children || [])
  .filter((item) => item.meta?.title)
  .map((item) => ({
    path: `/${item.path}`,
    title: item.meta.title,
    icon: item.meta.icon
  }))

const pageTitle = computed(() => route.meta.title || '')
const user = getUser()
const displayName = computed(() => user.name || user.username || '未登录')
const avatarText = computed(() => displayName.value.slice(0, 1).toUpperCase())

async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定退出当前账号吗？', '退出登录', {
      type: 'warning',
      confirmButtonText: '退出'
    })
  } catch {
    return
  }
  clearAuth()
  ElMessage.success('已退出登录')
  router.replace('/login')
}
</script>

<style scoped>
.layout {
  height: 100vh;
  background: var(--app-bg);
}

.sidebar {
  display: flex;
  flex-direction: column;
  background: var(--app-sidebar);
  overflow: hidden;
  transition: width 0.22s ease;
  position: relative;
}

.sidebar::after {
  content: '';
  position: absolute;
  inset: auto -80px -120px -80px;
  height: 260px;
  background: radial-gradient(circle, rgba(61, 107, 255, 0.28), transparent 65%);
  pointer-events: none;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  height: 72px;
  padding: 0 18px;
  flex-shrink: 0;
}

.brand-mark {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
  border-radius: 12px;
  display: grid;
  place-items: center;
  font-size: 20px;
  font-weight: 700;
  box-shadow: 0 8px 18px rgba(79, 124, 255, 0.4);
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.25;
  color: #fff;
  white-space: nowrap;
}

.brand-text strong {
  font-size: 17px;
  letter-spacing: 0.4px;
}

.brand-text span {
  font-size: 11px;
  color: #8b96ae;
  letter-spacing: 0.6px;
}

.side-menu {
  border-right: none;
  padding: 8px 12px;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  z-index: 1;
}

.side-menu :deep(.el-menu-item) {
  height: 46px;
  margin-bottom: 6px;
  border-radius: 10px;
  font-size: 14px;
  color: #aab3c8;
}

.side-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
}

.side-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, #3d6bff, #5b83ff);
  color: #fff;
  box-shadow: 0 8px 18px rgba(61, 107, 255, 0.35);
}

.side-menu :deep(.el-menu-item .el-icon) {
  font-size: 18px;
}

.sidebar-foot {
  padding: 18px 20px 24px;
  color: #5d6883;
  font-size: 11px;
  letter-spacing: 0.4px;
  white-space: nowrap;
  z-index: 1;
}

.main-wrap {
  min-width: 0;
  overflow: hidden;
}

.topbar {
  height: 64px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--app-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 22px 0 12px;
  flex-shrink: 0;
}

.topbar-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.collapse-btn {
  color: #5a6478;
  padding: 8px;
}

.crumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--app-muted);
}

.crumb strong {
  color: var(--app-text);
  font-weight: 600;
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 5px 10px 5px 5px;
  border-radius: 30px;
  cursor: pointer;
  transition: background 0.18s ease;
  outline: none;
}

.user-chip:hover {
  background: #f2f5fc;
}

.user-avatar {
  font-size: 13px;
  font-weight: 600;
}

.user-name {
  font-size: 13px;
  color: var(--app-text);
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.main-content {
  padding: 22px 24px 32px;
  overflow-y: auto;
  background: var(--app-bg);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.18s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 720px) {
  .user-name {
    display: none;
  }

  .main-content {
    padding: 16px 14px 24px;
  }
}
</style>