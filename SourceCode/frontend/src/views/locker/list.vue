<template>
  <div class="locker-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>快递柜列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增快递柜
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="柜号">
          <el-input v-model="searchForm.id" placeholder="请输入柜号" clearable />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="searchForm.location" placeholder="请输入位置" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" value="正常" />
            <el-option label="故障" value="故障" />
            <el-option label="禁用" value="禁用" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="柜号" width="100" />
        <el-table-column prop="location" label="位置" />
        <el-table-column prop="compartmentCount" label="仓门数" width="100" />
        <el-table-column prop="electricity" label="今日用电(kWh)" width="130" />
        <el-table-column prop="temperature" label="温度(°C)" width="100" />
        <el-table-column prop="humidity" label="湿度(%)" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button 
              :type="row.status === '禁用' ? 'success' : 'warning'" 
              link 
              @click="handleToggleStatus(row)"
            >
              {{ row.status === '禁用' ? '启用' : '禁用' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="柜号" prop="id">
          <el-input v-model="form.id" placeholder="请输入柜号" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入安装位置" />
        </el-form-item>
        <el-form-item label="仓门数量" prop="compartmentCount">
          <el-input-number v-model="form.compartmentCount" :min="1" :max="20" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getLockerList, addLocker, updateLocker, deleteLocker, toggleLockerStatus } from '@/api/locker'

const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const searchForm = reactive({
  id: '',
  location: '',
  status: ''
})

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await getLockerList({
      page: pagination.current,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    tableData.value = res.data.list.map(l => ({...l, electricity: (Math.random()*5).toFixed(1)}))
    pagination.total = res.data.total
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})

const form = reactive({
  id: '',
  location: '',
  compartmentCount: 8,
  remark: ''
})

const rules = {
  id: [{ required: true, message: '请输入柜号', trigger: 'blur' }],
  location: [{ required: true, message: '请输入安装位置', trigger: 'blur' }],
  compartmentCount: [{ required: true, message: '请选择仓门数量', trigger: 'change' }]
}

const dialogTitle = ref('新增快递柜')

const getStatusType = (status) => {
  const map = { '正常': 'success', '故障': 'danger', '禁用': 'info' }
  return map[status] || 'info'
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.id = ''
  searchForm.location = ''
  searchForm.status = ''
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增快递柜'
  form.id = ''
  form.location = ''
  form.compartmentCount = 8
  form.remark = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑快递柜'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleView = (row) => {
  ElMessage.info(`查看快递柜 ${row.id} 详情`)
}

const handleToggleStatus = (row) => {
  const action = row.status === '禁用' ? '启用' : '禁用'
  const newStatus = row.status === '禁用' ? '正常' : '禁用'
  ElMessageBox.confirm(`确定要${action}快递柜 ${row.id} 吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    await toggleLockerStatus(row.id, {status: newStatus})
    ElMessage.success(`${action}成功`)
    loadData()
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除快递柜 ${row.id} 吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteLocker(row.id)
    ElMessage.success('删除成功')
    loadData()
  })
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (isEdit.value) {
      await updateLocker(form.id, form)
      ElMessage.success('编辑成功')
    } else {
      await addLocker(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch(e) {
    console.error(e)
  }
}

const handleSizeChange = (val) => {
    pagination.pageSize = val
    loadData()
}
const handleCurrentChange = (val) => {
    pagination.current = val
    loadData()
}
</script>

<style lang="scss" scoped>
.locker-list {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .search-form {
    margin-bottom: 20px;
  }

  .pagination {
    margin-top: 20px;
    justify-content: flex-end;
  }
}
</style>
