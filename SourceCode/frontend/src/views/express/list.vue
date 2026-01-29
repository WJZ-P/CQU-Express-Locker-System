<template>
  <div class="express-list">
    <el-card shadow="hover">
      <template #header>
        <span>快递列表</span>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="快递单号">
          <el-input v-model="searchForm.expressNo" placeholder="请输入快递单号" clearable />
        </el-form-item>
        <el-form-item label="收件人手机">
          <el-input v-model="searchForm.receiverPhone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="快递柜">
          <el-select v-model="searchForm.lockerId" placeholder="请选择" clearable>
            <el-option
              v-for="locker in lockerList"
              :key="locker.id"
              :label="`${locker.name || locker.serialNo} - ${locker.location}`"
              :value="locker.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待取件" :value="0" />
            <el-option label="已取件" :value="1" />
            <el-option label="已超时" :value="2" />
            <el-option label="已退回" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="expressNo" label="快递单号" width="160" />
        <el-table-column prop="courierName" label="快递员" width="100" />
        <el-table-column prop="receiverPhone" label="收件人手机" width="130" />
        <el-table-column prop="lockerName" label="快递柜" width="150" />
        <el-table-column prop="boxNo" label="格口号" width="80" />
        <el-table-column prop="pickupCode" label="取件码" width="100" />
        <el-table-column prop="depositTime" label="入柜时间" width="170" />
        <el-table-column prop="pickupTime" label="取件时间" width="170" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button 
              v-if="row.status === 0" 
              type="warning" 
              link 
              @click="handleForcePickup(row)"
            >强制取件</el-button>
            <el-button 
              v-if="row.status === 0" 
              type="danger" 
              link 
              @click="handleReturn(row)"
            >退回</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        class="pagination"
        @current-change="loadData"
        @size-change="loadData"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="快递详情" width="600px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="快递单号">{{ currentOrder.expressNo }}</el-descriptions-item>
        <el-descriptions-item label="取件码">{{ currentOrder.pickupCode }}</el-descriptions-item>
        <el-descriptions-item label="快递员">{{ currentOrder.courierName }}</el-descriptions-item>
        <el-descriptions-item label="快递员电话">{{ currentOrder.courierPhone }}</el-descriptions-item>
        <el-descriptions-item label="收件人手机">{{ currentOrder.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="快递柜">{{ currentOrder.lockerName }}</el-descriptions-item>
        <el-descriptions-item label="格口号">{{ currentOrder.boxNo }}</el-descriptions-item>
        <el-descriptions-item label="入柜时间" :span="2">{{ currentOrder.depositTime }}</el-descriptions-item>
        <el-descriptions-item label="取件时间" :span="2" v-if="currentOrder.pickupTime">{{ currentOrder.pickupTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2" v-if="currentOrder.remark">{{ currentOrder.remark }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, forcePickup, returnExpress } from '@/api/express'
import { getLockerList } from '@/api/locker'

const loading = ref(false)
const detailVisible = ref(false)
const currentOrder = ref(null)
const lockerList = ref([])

const searchForm = reactive({
  expressNo: '',
  receiverPhone: '',
  lockerId: null,
  status: null
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref([])

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger', 3: 'info' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待取件', 1: '已取件', 2: '已超时', 3: '已退回' }
  return map[status] || '未知'
}

const loadLockers = async () => {
  try {
    const res = await getLockerList({ page: 1, pageSize: 100 })
    lockerList.value = res.data.list || []
  } catch (error) {
    console.error('加载快递柜列表失败:', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      expressNo: searchForm.expressNo || undefined,
      receiverPhone: searchForm.receiverPhone || undefined,
      lockerId: searchForm.lockerId || undefined,
      status: searchForm.status != null ? searchForm.status : undefined
    }
    const res = await getOrderList(params)
    tableData.value = res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadLockers()
  loadData()
})

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  Object.assign(searchForm, { expressNo: '', receiverPhone: '', lockerId: null, status: null })
  pagination.page = 1
  loadData()
}

const handleView = (row) => {
  currentOrder.value = row
  detailVisible.value = true
}

const handleForcePickup = async (row) => {
  try {
    await ElMessageBox.confirm(`确定强制取出快递 ${row.expressNo}？这将打开格口并标记为已取件。`, '提示', { type: 'warning' })
    await forcePickup(row.id)
    ElMessage.success('强制取件成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

const handleReturn = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入退回原因', '退回快递', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入退回原因'
    })
    await returnExpress(row.id, value)
    ElMessage.success('快递已退回')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}
</script>

<style lang="scss" scoped>
.express-list {
  .search-form { margin-bottom: 20px; }
  .pagination { margin-top: 20px; justify-content: flex-end; }
}
</style>
