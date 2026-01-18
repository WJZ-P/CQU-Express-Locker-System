<template>
  <div class="compartment">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>仓门管理</span>
        </div>
      </template>

      <!-- 选择快递柜 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="选择快递柜">
          <el-select v-model="selectedLocker" placeholder="请选择快递柜" @change="handleLockerChange">
            <el-option
              v-for="locker in lockerList"
              :key="locker.id"
              :label="`${locker.id} - ${locker.location}`"
              :value="locker.id"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <!-- 仓门可视化 -->
      <div class="compartment-grid" v-if="selectedLocker">
        <div
          v-for="comp in compartmentList"
          :key="comp.id"
          class="compartment-item"
          :class="getCompartmentClass(comp)"
          @click="handleCompartmentClick(comp)"
        >
          <div class="comp-header">
            <span class="comp-id">{{ comp.id }}</span>
            <el-tag :type="getStatusType(comp.status)" size="small">{{ comp.status }}</el-tag>
          </div>
          <div class="comp-body">
            <el-icon v-if="comp.hasItem" class="item-icon"><Box /></el-icon>
            <span v-else class="empty-text">空</span>
          </div>
          <div class="comp-footer">
            <span class="size">{{ comp.size }}</span>
          </div>
        </div>
      </div>
      <el-empty v-else description="请选择快递柜" />
    </el-card>

    <!-- 仓门详情对话框 -->
    <el-dialog v-model="dialogVisible" title="仓门详情" width="500px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="仓门编号">{{ currentComp.id }}</el-descriptions-item>
        <el-descriptions-item label="尺寸">{{ currentComp.size }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentComp.status)">{{ currentComp.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="是否有物品">{{ currentComp.hasItem ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item label="压力传感器">{{ currentComp.pressure }}kg</el-descriptions-item>
        <el-descriptions-item label="温度">{{ currentComp.temperature }}°C</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleOpenDoor" :disabled="currentComp.status !== '正常'">
          远程开门
        </el-button>
        <el-button 
          :type="currentComp.status === '禁用' ? 'success' : 'warning'"
          @click="handleToggleStatus"
        >
          {{ currentComp.status === '禁用' ? '启用' : '禁用' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const selectedLocker = ref('')
const dialogVisible = ref(false)

const lockerList = ref([
  { id: 'L001', location: '重庆大学A区1号门' },
  { id: 'L002', location: '重庆大学A区2号门' },
  { id: 'L003', location: '重庆大学B区食堂' }
])

const compartmentList = ref([])

const currentComp = reactive({
  id: '',
  size: '',
  status: '',
  hasItem: false,
  pressure: 0,
  temperature: 0
})

const handleLockerChange = () => {
  // 模拟获取仓门数据
  compartmentList.value = [
    { id: 'C01', size: '小', status: '正常', hasItem: true, pressure: 1.2, temperature: 25 },
    { id: 'C02', size: '小', status: '正常', hasItem: false, pressure: 0, temperature: 25 },
    { id: 'C03', size: '中', status: '正常', hasItem: true, pressure: 3.5, temperature: 24 },
    { id: 'C04', size: '中', status: '故障', hasItem: false, pressure: 0, temperature: 0 },
    { id: 'C05', size: '大', status: '正常', hasItem: true, pressure: 5.8, temperature: 25 },
    { id: 'C06', size: '大', status: '禁用', hasItem: false, pressure: 0, temperature: 0 },
    { id: 'C07', size: '小', status: '正常', hasItem: false, pressure: 0, temperature: 26 },
    { id: 'C08', size: '中', status: '正常', hasItem: true, pressure: 2.1, temperature: 25 }
  ]
}

const getStatusType = (status) => {
  const map = { '正常': 'success', '故障': 'danger', '禁用': 'info' }
  return map[status] || 'info'
}

const getCompartmentClass = (comp) => {
  return {
    'has-item': comp.hasItem,
    'is-fault': comp.status === '故障',
    'is-disabled': comp.status === '禁用'
  }
}

const handleCompartmentClick = (comp) => {
  Object.assign(currentComp, comp)
  dialogVisible.value = true
}

const handleOpenDoor = () => {
  ElMessageBox.confirm('确定要远程打开该仓门吗？', '提示', { type: 'warning' }).then(() => {
    ElMessage.success('仓门已打开')
    dialogVisible.value = false
  })
}

const handleToggleStatus = () => {
  const action = currentComp.status === '禁用' ? '启用' : '禁用'
  currentComp.status = currentComp.status === '禁用' ? '正常' : '禁用'
  ElMessage.success(`${action}成功`)
}
</script>

<style lang="scss" scoped>
.compartment {
  .search-form {
    margin-bottom: 20px;
  }

  .compartment-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
  }

  .compartment-item {
    border: 2px solid #dcdfe6;
    border-radius: 8px;
    padding: 12px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      transform: translateY(-2px);
    }

    &.has-item {
      border-color: #409EFF;
      background-color: #ecf5ff;
    }

    &.is-fault {
      border-color: #F56C6C;
      background-color: #fef0f0;
    }

    &.is-disabled {
      border-color: #909399;
      background-color: #f4f4f5;
      opacity: 0.6;
    }

    .comp-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;

      .comp-id {
        font-weight: bold;
      }
    }

    .comp-body {
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;

      .item-icon {
        font-size: 32px;
        color: #409EFF;
      }

      .empty-text {
        color: #909399;
      }
    }

    .comp-footer {
      text-align: center;
      font-size: 12px;
      color: #909399;
    }
  }
}
</style>
