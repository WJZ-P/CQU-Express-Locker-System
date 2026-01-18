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
          <el-select v-model="selectedLocker" placeholder="请选择快递柜" @change="handleLockerChange" style="width: 250px">
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getLockerList, getCompartmentList, updateCompartment, openCompartment } from '@/api/locker'

const selectedLocker = ref('')
const dialogVisible = ref(false)
const lockerList = ref([])
const compartmentList = ref([])

onMounted(async () => {
  try {
    const res = await getLockerList({ page: 1, pageSize: 100 })
    lockerList.value = res.data.list
  } catch(e) { console.error(e) }
})

const handleLockerChange = async (val) => {
  try {
    const res = await getCompartmentList(val)
    compartmentList.value = res.data.map(c => ({
      ...c,
      hasItem: c.status === '占用'
    }))
  } catch(e) { console.error(e) }
}

const currentComp = reactive({
  id: '',
  size: '',
  status: '',
  hasItem: false,
  pressure: 0,
  temperature: 0
})

const getStatusType = (status) => {
  const map = { '空闲': 'success', '占用': 'warning', '故障': 'danger', '禁用': 'info' }
  return map[status] || 'info'
}

const getCompartmentClass = (comp) => {
  return {
    'is-active': currentComp.id === comp.id,
    'comp-free': comp.status === '空闲',
    'comp-used': comp.status === '占用',
    'comp-error': comp.status === '故障',
    'comp-disabled': comp.status === '禁用'
  }
}

const handleCompartmentClick = (comp) => {
  Object.assign(currentComp, comp)
  dialogVisible.value = true
}

const handleOpenDoor = async () => {
  try {
    await openCompartment(currentComp.id)
    ElMessage.success('开门指令已发送')
  } catch(e) { console.error(e) }
}

const handleToggleStatus = async () => {
    const newStatus = currentComp.status === '禁用' ? '空闲' : '禁用'
    try {
        await updateCompartment(currentComp.id, { ...currentComp, status: newStatus })
        ElMessage.success('状态更新成功')
        dialogVisible.value = false
        handleLockerChange(selectedLocker.value) // refresh
    } catch(e) { console.error(e) }
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
