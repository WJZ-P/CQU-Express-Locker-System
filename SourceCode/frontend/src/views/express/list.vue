<template>
  <div class="express-list">
    <el-card shadow="hover">
      <template #header>
        <span>快递列表</span>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="快递单号">
          <el-input v-model="searchForm.trackingNo" placeholder="请输入快递单号" clearable />
        </el-form-item>
        <el-form-item label="收件人手机">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待取件" value="待取件" />
            <el-option label="已取件" value="已取件" />
            <el-option label="已超时" value="已超时" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="trackingNo" label="快递单号" width="180" />
        <el-table-column prop="courier" label="快递员" width="100" />
        <el-table-column prop="receiver" label="收件人" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="locker" label="快递柜" width="100" />
        <el-table-column prop="compartment" label="仓门号" width="80" />
        <el-table-column prop="pickupCode" label="取件码" width="100" />
        <el-table-column prop="inTime" label="入柜时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next"
        class="pagination"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const searchForm = reactive({
  trackingNo: '',
  phone: '',
  status: ''
})

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 100
})

const tableData = ref([
  { trackingNo: 'SF1234567890', courier: '张三', receiver: '李明', phone: '138****1234', locker: 'L001', compartment: 'C01', pickupCode: '123456', inTime: '2026-01-18 10:30', status: '待取件' },
  { trackingNo: 'YT9876543210', courier: '李四', receiver: '王芳', phone: '139****5678', locker: 'L001', compartment: 'C03', pickupCode: '654321', inTime: '2026-01-18 09:15', status: '已取件' },
  { trackingNo: 'ZT1122334455', courier: '张三', receiver: '刘伟', phone: '137****9012', locker: 'L002', compartment: 'C05', pickupCode: '112233', inTime: '2026-01-17 14:20', status: '已超时' }
])

const getStatusType = (status) => {
  const map = { '待取件': 'warning', '已取件': 'success', '已超时': 'danger' }
  return map[status] || 'info'
}

const handleSearch = () => ElMessage.success('查询成功')
const handleReset = () => Object.assign(searchForm, { trackingNo: '', phone: '', status: '' })
const handleView = (row) => ElMessage.info(`查看快递 ${row.trackingNo}`)
</script>

<style lang="scss" scoped>
.express-list {
  .search-form { margin-bottom: 20px; }
  .pagination { margin-top: 20px; justify-content: flex-end; }
}
</style>
