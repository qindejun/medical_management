<template>
  <div class="employee-management">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchForm.name" placeholder="请输入员工姓名" style="width: 200px; margin-right: 10px;"></el-input>
      <el-select v-model="searchForm.department" placeholder="请选择部门" style="width: 150px; margin-right: 10px;">
        <el-option v-for="item in departments" :key="item" :label="item" :value="item"></el-option>
      </el-select>
      <el-button type="primary" @click="search">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <!-- 批量删除按钮 -->
    <div class="operation-buttons" style="margin: 20px 0;">
      <el-button type="danger" :disabled="!selectedRows.length" @click="batchDelete">批量删除</el-button>
    </div>

    <!-- 员工表格 -->
    <el-table :data="tableData" border style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="name" label="姓名"></el-table-column>
      <el-table-column prop="gender" label="性别"></el-table-column>
      <el-table-column prop="age" label="年龄"></el-table-column>
      <el-table-column prop="position" label="职位"></el-table-column>
      <el-table-column prop="department" label="部门"></el-table-column>
      <el-table-column prop="email" label="邮箱"></el-table-column>
      <el-table-column prop="phone" label="联系电话"></el-table-column>
      <el-table-column prop="role" label="角色"></el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button type="text" @click="showEditDialog(scope.row)">修改</el-button>
          <el-button type="text" @click="deleteEmployee(scope.row.employeeId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
      :current-page="searchForm.pageNum" :page-sizes="[5, 10, 15, 20]" :page-size="searchForm.pageSize"
      layout="total, sizes, prev, pager, next, jumper" :total="total">
    </el-pagination>

    <!-- 修改对话框 -->
    <el-dialog title="修改员工信息" :visible.sync="editDialogVisible" width="30%">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="姓名">
          <el-input v-model="editForm.name"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-input v-model="editForm.gender"></el-input>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="editForm.age"></el-input>
        </el-form-item>
        <el-form-item label="职位">
          <el-input v-model="editForm.position"></el-input>
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="editForm.department" placeholder="请选择部门">
            <el-option v-for="item in departments" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" disabled></el-input>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="editForm.phone" disabled></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.role" placeholder="请选择角色">
            <el-option v-for="item in roles" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request';

export default {
  name: 'EmployeeManagement',
  data() {
    return {
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      roles: ['管理员', '销售员', '仓管员'],
      // 搜索表单
      searchForm: {
        name: '',
        department: '',
        pageNum: 1,
        pageSize: 5,
      },
      // 下拉选项
      departments: ['IT部', '销售部', '仓储部'],
      // 表格数据
      tableData: [],
      total: 0,
      // 选中的行
      selectedRows: [],
      // 修改对话框
      editDialogVisible: false,
      editForm: {
        employeeId: null,
        name: '',
        gender: '',
        age: '',
        position: '',
        department: '',
        email: '',
        phone: '',
        role: ''
      }
    }
  },
  created() {
    console.log('Employees component created');
    this.load();
  },
  methods: {
    // 角色数字转换为中文
    roleToText(role) {
      const roleMap = {
        1: '管理员',
        2: '销售员',
        3: '仓管员'
      };
      return roleMap[role] || '未知角色';
    },
    load() {
      this.loading = true;
      console.log(this.searchForm);
      request.get("/employee", { params: this.searchForm }).then(res => {
        console.log('Load response:', res);
        if (res.code === 1 || res.code === '1') {
          // 转换 role 字段为中文
          this.tableData = (res.data.list || []).map(item => ({
            ...item,
            role: this.roleToText(item.role)
          }));
          this.total = res.data.total;
        } else {
          this.$message.error(`加载数据失败: ${res.msg || '未知错误'}`);
        }
      }).catch(error => {
        console.error('Request error:', error);
        this.$message.error('请求失败，请检查网络或后端服务');
      }).finally(() => {
        this.loading = false;
      });
    },
    // 搜索
    search() {
      console.log("查找参数", this.searchForm);
      // 清理空字符串，转换为 undefined 或不发送
      const params = {
        name: this.searchForm.name || undefined,
        department: this.searchForm.department || undefined,
        pageNum: this.searchForm.pageNum,
        pageSize: this.searchForm.pageSize
      };
      request.get("/employee/search", { params }).then(res => {
        console.log('Search response:', res);
        if (res.code === 1 || res.code === '1') {
          this.tableData = (res.data.list || []).map(item => ({
            ...item,
            role: this.roleToText(item.role)
          }));
          this.total = res.data.total;
        } else {
          this.$message.error(`加载数据失败: ${res.msg || '未知错误'}`);
        }
      }).catch(error => {
        console.error('Request error:', error);
        this.$message.error('请求失败，请检查网络或后端服务');
      });
    },
    // 重置搜索
    resetSearch() {
      this.searchForm = {
        name: '',
        department: '',
        pageNum: 1,
        pageSize: 5
      };
      this.load();
    },
    handleSizeChange(newSize) {
      this.searchForm.pageSize = newSize;
      this.searchForm.pageNum = 1; // 切换每页大小时重置到第一页
      this.load();
    },
    handleCurrentChange(newPage) {
      this.searchForm.pageNum = newPage;
      this.load();
    },
    // 表格选择变化
    handleSelectionChange(val) {
      this.selectedRows = val;
    },
    // 批量删除
    batchDelete() {
      this.$confirm('确定要批量删除选中的员工吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const idArray = this.selectedRows.map(row => row.employeeId);
        request.put(`/employee/batch`, idArray).then(res => {
          if (res.code === 1 || res.code === '1') {
            this.$message.success('批量删除成功');
            this.load();
          } else {
            this.$message.error(`批量删除失败: ${res.msg || '未知错误'}`);
          }
        }).catch(error => {
          console.error('Request error:', error);
          this.$message.error('请求失败，请检查网络或后端服务');
        });
      }).catch(() => {
        this.$message.info('已取消批量删除');
      });
    },
    // 显示修改对话框
    showEditDialog(row) {
      this.editForm = {
        ...row,
      };
      this.editDialogVisible = true;
    },
    // 提交修改
    submitEdit() {
      if (this.editForm.role === "管理员") {
        this.editForm.role = 1
      } else if (this.editForm.role === "销售员") {
        this.editForm.role = 2
      } else {
        this.editForm.role = 3
      }
      console.log(this.editForm);
      request.put("/employee/update", this.editForm).then(res => {
        if (res.code === 1 || res.code === '1') {
          this.$message.success('修改成功');
          this.editDialogVisible = false;
          this.load();
        } else {
          this.$message.error(`修改失败: ${res.msg || '未知错误'}`);
        }
      }).catch(error => {
        console.error('Request error:', error);
        this.$message.error('请求失败，请检查网络或后端服务');
      });
    },
    // 删除员工
    deleteEmployee(employeeId) {
      this.$confirm('确定要删除该员工吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.delete(`/employee/${employeeId}`).then(res => {
          if (res.code === 1 || res.code === '1') {
            this.$message.success('删除成功');
            this.load();
          } else {
            this.$message.error(`删除失败: ${res.msg || '未知错误'}`);
          }
        }).catch(error => {
          console.error('Request error:', error);
          this.$message.error('请求失败，请检查网络或后端服务');
        });
      }).catch(() => {
        this.$message.info('已取消删除');
      });
    }
  },

}

</script>

<style scoped>
.employee-management {
  padding: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
</style>