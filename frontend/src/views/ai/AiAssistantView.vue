<template>
  <div class="ai-container">
    <el-card class="ai-card">
      <template #header>
        <div class="ai-header">
          <el-icon><ChatDotRound/></el-icon>
          <span>智能问数</span>
        </div>
      </template>

      <div class="ai-tips">
        试试问我：本月卖了多少钱、哪个客户欠款最多、供应商还欠多少、库存概览、收支概览、待送货提醒
      </div>

      <div class="ai-input">
        <el-input
            v-model="question"
            placeholder="用一句话问经营数据，如：这个月卖了多少钱"
            clearable
            @keyup.enter="handleAsk"/>
        <el-button type="primary" :loading="loading" @click="handleAsk">提问</el-button>
      </div>

      <div v-if="answer" class="ai-answer">
        <el-alert
            :title="answerTitle"
            :type="answerType"
            :closable="false"
            show-icon>
          <pre class="answer-text">{{ answer.answer }}</pre>
        </el-alert>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { aiApi } from '@/api/ai'

const question = ref('')
const answer = ref(null)
const loading = ref(false)

// 回答类型：拒答/降级为警告，正常为成功
const answerType = ref('success')
const answerTitle = ref('回答')

async function handleAsk() {
  const q = question.value.trim()
  if (!q) {
    ElMessage.warning('请输入您要查询的问题')
    return
  }
  loading.value = true
  try {
    const res = await aiApi.ask(q)
    answer.value = res
    if (res.degraded) {
      answerType.value = 'warning'
      answerTitle.value = '智能助手暂时不可用'
    } else if (res.rejected) {
      answerType.value = 'info'
      answerTitle.value = '暂无法回答'
    } else {
      answerType.value = 'success'
      answerTitle.value = '回答'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.ai-container {
  max-width: 760px;
  margin: 0 auto;
}

.ai-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
}

.ai-tips {
  color: #909399;
  font-size: 13px;
  margin-bottom: 12px;
}

.ai-input {
  display: flex;
  gap: 10px;
}

.ai-answer {
  margin-top: 16px;
}

.answer-text {
  white-space: pre-wrap;
  word-break: break-word;
  margin: 0;
  font-family: inherit;
  font-size: 14px;
  line-height: 1.6;
}
</style>
