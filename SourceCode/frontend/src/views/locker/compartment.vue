<template>
  <div class="compartment">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>格口管理</span>
          <el-button type="primary" @click="handleAdd" v-if="selectedLocker">
            <el-icon><Plus /></el-icon>
            新增格口
          </el-button>
        </div>
      </template>

      <!-- 选择快递柜 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="选择快递柜">
          <el-select v-model="selectedLocker" placeholder="请选择快递柜" @change="handleLockerChange" :loading="lockerLoading">
            <el-option
              v-for="locker in lockerList"
              :key="locker.id"
              :label="`${locker.name || locker.serialNo} - ${locker.location}`"
              :value="locker.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable @change="loadCompartments">
            <el-option label="空闲" :value="0" />
            <el-option label="占用" :value="1" />
            <el-option label="故障" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="启用状态">
          <el-select v-model="searchForm.enabled" placeholder="请选择" clearable @change="loadCompartments">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>

      <!-- 格口可视化 -->
      <div class="compartment-grid" v-if="selectedLocker" v-loading="loading">
        <div
          v-for="comp in compartmentList"
          :key="comp.id"
          class="compartment-item"
          :class="getCompartmentClass(comp)"
          @click="handleCompartmentClick(comp)"
        >
          <div class="comp-header">
            <span class="comp-id">{{ comp.boxNo }}</span>
            <el-tag :type="getStatusType(comp.status)" size="small">{{ getStatusText(comp.status) }}</el-tag>
          </div>
          <div class="comp-body">
            <el-icon v-if="comp.status === 1" class="item-icon"><Box /></el-icon>
            <span v-else class="empty-text">{{ comp.enabled === 0 ? '禁用' : '空' }}</span>
          </div>
          <div class="comp-footer">
            <span class="size">{{ getSizeText(comp.size) }}</span>
          </div>
        </div>
      </div>
      <el-empty v-else description="请选择快递柜" />

      <!-- 分页 -->
      <el-pagination
        v-if="selectedLocker && pagination.total > 0"
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        layout="total, prev, pager, next"
        class="pagination"
        @current-change="loadCompartments"
      />
    </el-card>

    <!-- 格口详情对话框 -->
    <el-dialog v-model="dialogVisible" title="格口详情" width="500px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="格口编号">{{ currentComp.boxNo }}</el-descriptions-item>
        <el-descriptions-item label="尺寸">{{ getSizeText(currentComp.size) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentComp.status)">{{ getStatusText(currentComp.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="启用状态">
          <el-tag :type="currentComp.enabled === 1 ? 'success' : 'danger'">
            {{ currentComp.enabled === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="压力传感器">{{ currentComp.pressure || 0 }}kg</el-descriptions-item>
        <el-descriptions-item label="温度">{{ currentComp.temperature || '-' }}°C</el-descriptions-item>
        <el-descriptions-item label="湿度">{{ currentComp.humidity || '-' }}%</el-descriptions-item>
        <el-descriptions-item label="门锁状态">{{ currentComp.isLocked === 1 ? '关闭' : '打开' }}</el-descriptions-item>
        <el-descriptions-item label="故障原因" v-if="currentComp.status === 2" :span="2">
          {{ currentComp.faultReason || '未知' }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleOpenDoor" :disabled="currentComp.enabled === 0">
          远程开门
        </el-button>
        <el-button 
          :type="currentComp.enabled === 0 ? 'success' : 'warning'"
          @click="handleToggleEnabled"
        >
          {{ currentComp.enabled === 0 ? '启用' : '禁用' }}
        </el-button>
        <el-button 
          v-if="currentComp.status !== 2"
          type="danger"
          @click="handleMarkFault"
        >
          标记故障
        </el-button>
        <el-button 
          v-else
          type="success"
          @click="handleClearFault"
        >
          清除故障
        </el-button>
      </template>
    </el-dialog>

    <!-- 新增格口对话框 -->
    <el-dialog v-model="addDialogVisible" title="新增格口" width="400px">
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="80px">
        <el-form-item label="格口编号" prop="boxNo">
          <el-input v-model="addForm.boxNo" placeholder="如 A01" />
        </el-form-item>
        <el-form-item label="尺寸" prop="boxType">
          <el-select v-model="addForm.boxType" placeholder="请选择">
            <el-option label="小" value="small" />
            <el-option label="中" value="medium" />
            <el-option label="大" value="large" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getLockerList, getCompartmentList, addCompartment, openCompartment, toggleCompartmentEnabled, markCompartmentFault, clearCompartmentFault } from '@/api/locker'

const route = useRoute()
const selectedLocker = ref('')
const dialogVisible = ref(false)
const addDialogVisible = ref(false)
const loading = ref(false)
const lockerLoading = ref(false)
const submitLoading = ref(false)
const addFormRef = ref()

const searchForm = reactive({
  status: null,
  enabled: null
})

const pagination = reactive({
  page: 1,
  pageSize: 20,
  total: 0
})

const lockerList = ref([])
const compartmentList = ref([])

const currentComp = reactive({
  id: null,
  boxNo: '',
  size: 1,
  status: 0,
  enabled: 1,
  pressure: 0,
  temperature: 0,
  humidity: 0,
  isLocked: 1,
  faultReason: ''
})

const addForm = reactive({
  boxNo: '',
  boxType: 'medium'
})

const addRules = {
  boxNo: [{ required: true, message: '请输入格口编号', trigger: 'blur' }],
  boxType: [{ required: true, message: '请选择尺寸', trigger: 'change' }]
}

// 加载快递柜列表
const loadLockers = async () => {
  lockerLoading.value = true
  try {
    const res = await getLockerList({ page: 1, pageSize: 100 })
    lockerList.value = res.data.list || []
    
    // 如果URL有lockerId参数，自动选中
    const lockerId = route.query.lockerId
    if (lockerId && lockerList.value.some(l => l.id == lockerId)) {
      selectedLocker.value = parseInt(lockerId)
      loadCompartments()
    }
  } catch (error) {
    console.error('加载快递柜失败:', error)
  } finally {
    lockerLoading.value = false
  }
}

// 加载格口列表
const loadCompartments = async () => {
  if (!selectedLocker.value) return
  
  loading.value = true
  try {
    const params = {
      lockerId: selectedLocker.value,
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })
    const res = await getCompartmentList(params)
    compartmentList.value = res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载格口失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadLockers()
})

const handleLockerChange = () => {
  pagination.page = 1
  loadCompartments()
}

const getStatusType = (status) => {
  const map = { 0: 'success', 1: 'primary', 2: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '空闲', 1: '占用', 2: '故障' }
  return map[status] || '未知'
}

const getSizeText = (size) => {
  const map = { 1: '小', 2: '中', 3: '大' }
  return map[size] || '未知'
}

const getCompartmentClass = (comp) => {
  return {
    'has-item': comp.status === 1,
    'is-fault': comp.status === 2,
    'is-disabled': comp.enabled === 0
  }
}

const handleCompartmentClick = (comp) => {
  Object.assign(currentComp, comp)
  dialogVisible.value = true
}

const handleOpenDoor = async () => {
  try {
    await ElMessageBox.confirm('确定要远程打开该格口吗？', '提示', { type: 'warning' })
    await openCompartment(currentComp.id)
    ElMessage.success('格口已打开')
    dialogVisible.value = false
    loadCompartments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('开门失败:', error)
    }
  }
}

const handleToggleEnabled = async () => {
  const newEnabled = currentComp.enabled === 1 ? 0 : 1
  const action = newEnabled === 1 ? '启用' : '禁用'
  try {
    await toggleCompartmentEnabled(currentComp.id, newEnabled)
    ElMessage.success(`${action}成功`)
    dialogVisible.value = false
    loadCompartments()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const handleMarkFault = async () => {
  try {
    const { value } = await ElMessageBox.prompt('请输入故障原因', '标记故障', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入故障原因'
    })
    await markCompartmentFault(currentComp.id, value)
    ElMessage.success('已标记为故障')
    dialogVisible.value = false
    loadCompartments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

const handleClearFault = async () => {
  try {
    await clearCompartmentFault(currentComp.id)
    ElMessage.success('故障已清除')
    dialogVisible.value = false
    loadCompartments()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const handleAdd = () => {
  addForm.boxNo = ''
  addForm.boxType = 'medium'
  addDialogVisible.value = true
}

const handleAddSubmit = async () => {
  const valid = await addFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  try {
    await addCompartment({
      lockerId: selectedLocker.value,
      boxNo: addForm.boxNo,
      boxType: addForm.boxType
    })
    ElMessage.success('新增成功')
    addDialogVisible.value = false
    loadCompartments()
  } catch (error) {
    console.error('新增失败:', error)
  } finally {
    submitLoading.value = false
  }
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
