<template>
  <div class="courier-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>快递员管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增快递员
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="工号">
          <el-input v-model="searchForm.courierNo" placeholder="请输入工号" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="快递公司">
          <el-select v-model="searchForm.company" placeholder="请选择" clearable>
            <el-option label="顺丰" value="顺丰" />
            <el-option label="圆通" value="圆通" />
            <el-option label="中通" value="中通" />
            <el-option label="韵达" value="韵达" />
            <el-option label="申通" value="申通" />
            <el-option label="极兔" value="极兔" />
            <el-option label="邮政" value="邮政" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="在职" :value="1" />
            <el-option label="离职" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="courierNo" label="工号" width="100" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="company" label="快递公司" width="120" />
        <el-table-column prop="depositCount" label="累计投递" width="100" />
        <el-table-column prop="createTime" label="入职时间" width="170" />
        <el-table-column prop="lastLoginTime" label="最后登录" width="170" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleResetPassword(row)">重置密码</el-button>
            <el-button 
              :type="row.status === 1 ? 'warning' : 'success'" 
              link 
              @click="handleToggle(row)"
            >
              {{ row.status === 1 ? '离职' : '复职' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="工号" prop="courierNo">
          <el-input v-model="formData.courierNo" placeholder="请输入工号" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="快递公司" prop="company">
          <el-select v-model="formData.company" placeholder="请选择快递公司">
            <el-option label="顺丰" value="顺丰" />
            <el-option label="圆通" value="圆通" />
            <el-option label="中通" value="中通" />
            <el-option label="韵达" value="韵达" />
            <el-option label="申通" value="申通" />
            <el-option label="极兔" value="极兔" />
            <el-option label="邮政" value="邮政" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCourierList, addCourier, updateCourier, deleteCourier, toggleCourierStatus, resetCourierPassword } from '@/api/user'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const searchForm = reactive({ courierNo: '', name: '', company: '', status: null })
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const tableData = ref([])

const formData = reactive({
  id: null,
  courierNo: '',
  password: '',
  name: '',
  phone: '',
  company: ''
})

const formRules = {
  courierNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  company: [{ required: true, message: '请选择快递公司', trigger: 'change' }]
}

const dialogTitle = ref('新增快递员')

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      courierNo: searchForm.courierNo || undefined,
      name: searchForm.name || undefined,
      company: searchForm.company || undefined,
      status: searchForm.status != null ? searchForm.status : undefined
    }
    const res = await getCourierList(params)
    tableData.value = res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载快递员列表失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.courierNo = ''
  searchForm.name = ''
  searchForm.company = ''
  searchForm.status = null
  pagination.page = 1
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增快递员'
  Object.assign(formData, { id: null, courierNo: '', password: '', name: '', phone: '', company: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑快递员'
  Object.assign(formData, {
    id: row.id,
    courierNo: row.courierNo,
    password: '',
    name: row.name,
    phone: row.phone,
    company: row.company
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateCourier(formData)
      ElMessage.success('更新成功')
    } else {
      await addCourier(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('操作失败:', error)
  } finally {
    submitLoading.value = false
  }
}

const handleToggle = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '复职' : '离职'
  try {
    await ElMessageBox.confirm(`确定将快递员 ${row.name} 标记为${action}？`, '提示')
    await toggleCourierStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(`确定重置快递员 ${row.name} 的密码？`, '提示', { type: 'warning' })
    await resetCourierPassword(row.id)
    ElMessage.success('密码已重置为默认密码')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除快递员 ${row.name}？`, '提示', { type: 'warning' })
    await deleteCourier(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}
</script>

<style lang="scss" scoped>
.courier-list {
  .card-header { display: flex; justify-content: space-between; align-items: center; }
  .search-form { margin-bottom: 20px; }
  .pagination { margin-top: 20px; justify-content: flex-end; }
}
</style>
