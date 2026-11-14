<template>
  <div class="login-page">
    <section class="intro">
      <div class="glow glow-1"></div>
      <div class="glow glow-2"></div>
      <div class="grid-lines"></div>

      <div class="intro-top">
        <div class="brand-mark app-gradient">T</div>
        <span>Tlias</span>
      </div>

      <div class="intro-main">
        <p class="eyebrow">SMART EDUCATION OPS</p>
        <h1>让教学管理<br />回归简单。</h1>
        <p class="intro-desc">员工、班级、学员与教学数据，一站式完成管理。</p>
        <ul class="intro-points">
          <li>统一员工与部门档案</li>
          <li>班级学员全流程跟踪</li>
          <li>实时统计与操作审计</li>
        </ul>
      </div>

      <p class="intro-foot">Tlias 教学管理系统 · Teaching Management</p>
    </section>

    <section class="login-panel">
      <div class="login-card">
        <h2>登录系统</h2>
        <p class="login-sub">使用员工账号继续访问</p>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          size="large"
          @keyup.enter="submit"
        >
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" :prefix-icon="User" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              show-password
              :prefix-icon="Lock"
              placeholder="请输入密码"
            />
          </el-form-item>
          <el-button class="login-btn" type="primary" :loading="loading" @click="submit">
            登录
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </el-form>

        <p class="login-hint">账号由管理员在员工管理中创建</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowRight, Lock, User } from '@element-plus/icons-vue'
import { login } from '../api'
import { setAuth } from '../utils/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function submit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const data = await login({ username: form.username, password: form.password })
    setAuth(data)
    ElMessage.success(`欢迎回来，${data.name || data.username}`)
    router.replace('/dashboard')
  } catch {
    // 错误提示由响应拦截器统一处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  min-height: 100vh;
  background: #fff;
}

/* ---------- 左侧品牌区 ---------- */
.intro {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 42px 56px;
  overflow: hidden;
  color: #fff;
  background:
    radial-gradient(circle at 15% 10%, rgba(79, 124, 255, 0.35), transparent 45%),
    linear-gradient(145deg, #0f1525 0%, #151d33 50%, #1b2647 100%);
}

.glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(2px);
  pointer-events: none;
}

.glow-1 {
  width: 460px;
  height: 460px;
  right: -180px;
  bottom: -160px;
  background: radial-gradient(circle, rgba(122, 92, 255, 0.35), transparent 68%);
}

.glow-2 {
  width: 320px;
  height: 320px;
  left: -120px;
  top: 22%;
  background: radial-gradient(circle, rgba(61, 107, 255, 0.25), transparent 68%);
}

.grid-lines {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.035) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.035) 1px, transparent 1px);
  background-size: 56px 56px;
  mask-image: radial-gradient(circle at 30% 40%, #000 20%, transparent 75%);
}

.intro-top {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 0.6px;
}

.brand-mark {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  font-size: 21px;
  font-weight: 700;
  box-shadow: 0 10px 24px rgba(79, 124, 255, 0.45);
}

.intro-main {
  position: relative;
  max-width: 520px;
  padding: 24px 0;
}

.eyebrow {
  margin: 0 0 18px;
  font-size: 12px;
  letter-spacing: 3px;
  color: #7f93c9;
}

.intro-main h1 {
  margin: 0;
  font-size: 54px;
  line-height: 1.16;
  letter-spacing: -1px;
  font-weight: 650;
}

.intro-desc {
  margin: 22px 0 30px;
  font-size: 15px;
  line-height: 1.8;
  color: #a9b5d1;
}

.intro-points {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 18px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.intro-points li {
  position: relative;
  padding-left: 18px;
  font-size: 13px;
  color: #c6d0e6;
}

.intro-points li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 6px;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #5b83ff;
  box-shadow: 0 0 10px rgba(91, 131, 255, 0.9);
}

.intro-foot {
  position: relative;
  margin: 0;
  font-size: 11px;
  letter-spacing: 1px;
  color: #5d6883;
}

/* ---------- 右侧登录区 ---------- */
.login-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background:
    radial-gradient(circle at 80% 10%, rgba(61, 107, 255, 0.06), transparent 40%),
    #fff;
}

.login-card {
  width: 100%;
  max-width: 380px;
}

.login-card h2 {
  margin: 0;
  font-size: 28px;
  letter-spacing: -0.4px;
}

.login-sub {
  margin: 8px 0 30px;
  color: var(--app-muted);
  font-size: 13px;
}

.login-card :deep(.el-form-item__label) {
  font-size: 13px;
  color: #5a6478;
  padding-bottom: 6px;
}

.login-card :deep(.el-input__wrapper) {
  padding: 4px 12px;
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e3e8f2 inset;
  transition: box-shadow 0.18s ease;
}

.login-card :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1.5px var(--el-color-primary) inset;
}

.login-btn {
  width: 100%;
  height: 46px;
  margin-top: 10px;
  border-radius: 10px;
  font-size: 15px;
  box-shadow: 0 10px 22px rgba(61, 107, 255, 0.28);
}

.login-btn .el-icon {
  margin-left: 6px;
}

.login-hint {
  margin: 26px 0 0;
  text-align: center;
  font-size: 12px;
  color: #a1a9bb;
}

@media (max-width: 960px) {
  .login-page {
    grid-template-columns: 1fr;
  }

  .intro {
    display: none;
  }

  .login-panel {
    min-height: 100vh;
    padding: 24px;
  }
}
</style>