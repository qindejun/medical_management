<template>
  <div class="auth-code-management">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchForm.name" placeholder="请输入员工姓名" style="width: 200px; margin-right: 10px;"></el-input>
      <el-select v-model="searchForm.role" placeholder="请选择角色" style="width: 150px; margin-right: 10px;">
        <el-option label="管理员" value="1"></el-option>
        <el-option label="销售员" value="2"></el-option>
        <el-option label="仓管员" value="3"></el-option>
      </el-select>
      <el-select v-model="searchForm.status" placeholder="请选择状态" style="width: 150px; margin-right: 10px;">
        <el-option label="已使用" value="0"></el-option>
        <el-option label="未使用" value="1"></el-option>
      </el-select>
      <el-button type="primary" @click="search">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <!-- 操作按钮 -->
    <div class="operation-buttons" style="margin: 20px 0;">
      <el-button type="danger" :disabled="!selectedRows.length" @click="batchDelete">批量删除</el-button>
      <el-button type="primary" @click="showAddDialog">新增授权码</el-button>
    </div>

    <!-- 授权码表格 -->
    <el-table :data="tableData" border style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="authCode" label="授权码" width="220px"></el-table-column>
      <el-table-column prop="name" label="使用者" width="130px"></el-table-column>
      <el-table-column prop="position" label="职位" width="140px"></el-table-column>
      <el-table-column prop="department" label="部门" width="140px"></el-table-column>
      <el-table-column prop="role" label="角色" width="140px"></el-table-column>
      <el-table-column prop="status" label="授权码状态" width="140px">
        <template slot-scope="scope">
          {{ scope.row.status === 1 ? '未使用' : '已使用' }}
        </template>
      </el-table-column>
      <el-table-column prop="usedAt" label="使用时间"></el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button type="text" :disabled="scope.row.status === 0" @click="showEditDialog(scope.row)">修改</el-button>
          <el-button type="text" @click="deleteAuthCode(scope.row.codeId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
      :current-page="searchForm.pageNum" :page-sizes="[5, 10, 15, 20]" :page-size="searchForm.pageSize"
      layout="total, sizes, prev, pager, next, jumper" :total="total">
    </el-pagination>

    <!-- 新增授权码对话框 -->
    <el-dialog title="新增授权码" :visible.sync="addDialogVisible" width="30%">
      <el-form :model="addForm" label-width="100px">
        <el-form-item label="角色">
          <el-select v-model="addForm.role" placeholder="请选择角色">
            <el-option label="管理员" value="1"></el-option>
            <el-option label="销售员" value="2"></el-option>
            <el-option label="仓管员" value="3"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确定</el-button>
      </span>
    </el-dialog>

    <!-- 修改授权码对话框 -->
    <el-dialog title="修改授权码" :visible.sync="editDialogVisible" width="30%">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="角色">
          <el-select v-model="editForm.role" placeholder="请选择角色">
            <el-option label="管理员" value="1"></el-option>
            <el-option label="销售员" value="2"></el-option>
            <el-option label="仓管员" value="3"></el-option>
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
  name: 'AuthCodeManagement',
  data() {
    return {
      // 搜索表单
      searchForm: {
        name: '',
        role: '',
        status: '',
        pageNum: 1,
        pageSize: 5
      },
      // 下拉选项
      roles: ['管理员', '仓管员', '销售员'],
      // 表格数据
      tableData: [],
      total: 0,
      // 选中的行
      selectedRows: [],
      // 分页信息
      pagination: {
        currentPage: 1,
        pageSize: 10,
      },
      // 新增对话框
      addDialogVisible: false,
      addForm: {
        role: '',
      },
      // 修改对话框
      editDialogVisible: false,
      editForm: {
        codeId: '',
        role: '',
        usedBy: '',
      }
    }
  },
  created() {
    console.log('Authcode component created');
    this.load();
  },
  methods: {
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
      request.get("/authcode", { params: this.searchForm }).then(res => {
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
      console.log(this.searchForm);
      request.get("/authcode/search", { params: this.searchForm }).then(res => {
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
    // 重置搜索
    resetSearch() {
      this.searchForm = {
        name: '',
        role: '',
        status: ''
      }
      this.search()
    },
    // 表格选择变化
    handleSelectionChange(val) {
      this.selectedRows = val
    },
    // 批量删除
    batchDelete() {
      this.$confirm('确定要批量删除选中的授权码吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const idArray = this.selectedRows.map(row => row.codeId);
        request.put(`/authcode/batch`, idArray).then(res => {
          if (res.code === 1 || res.code === '1') {
            this.$message.success('批量删除成功');
            this.search();
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
    handleSizeChange(newSize) {
      this.searchForm.pageSize = newSize;
      this.searchForm.pageNum = 1; // 切换每页大小时重置到第一页
      this.load();
    },
    handleCurrentChange(newPage) {
      this.searchForm.pageNum = newPage;
      this.load();
    },
    // 显示新增对话框
    showEditDialog(row) {
      this.editForm = {
        ...row,
      };
      this.editDialogVisible = true
    },
    // 提交新增
    submitAdd() {
      console.log('新增加的角色', this.addForm.role);
      request.post("/authcode/add", this.addForm.role).then(res => {
        console.log('Load response:', res);
        if (res.code === 1 || res.code === '1') {
          this.$message.success('新增成功!')
          this.addDialogVisible = false
          this.search();
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
    // 显示修改对话框
    showEditDialog(row) {
      this.editForm = { codeId: row.codeId, role: row.role };
      this.editDialogVisible = true
    },
    showAddDialog() {
      this.addDialogVisible = true
    },
    // 提交修改
    submitEdit() {
      if (!this.editForm.codeId) {
        this.$message.error('授权码ID不能为空');
        return;
      }
      const submitData = {
        codeId: this.editForm.codeId,
        role: this.editForm.role
      };
      console.log('Submitting data:', submitData);
      request.put("/authcode/update", submitData).then(res => {
        console.log('Update response:', res);
        if (res.code === 1 || res.code === '1') {
          this.$message.success('修改成功');
          this.editDialogVisible = false;
          this.tableData = (res.data.list || []).map(item => {
            const mappedItem = {
              ...item,
              role: this.roleToText(item.role),
              status: item.status
            };
            console.log('Mapped item:', mappedItem);
            return mappedItem;
          });
          this.total = res.data.total;
          this.search();
        } else {
          this.$message.error(`修改失败: ${res.msg || '未知错误'}`);
        }
      }).catch(error => {
        console.error('Request error:', error);
        this.$message.error('请求失败，请检查网络或后端服务');
      }).finally(() => {
        this.loading = false;
      });
    },
    // 删除授权码
    deleteAuthCode(codeId) {
      console.log(codeId);
      this.$confirm('确定要删除该条授权码吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.delete(`/authcode/${codeId}`).then(res => {
          if (res.code === 1 || res.code === '1') {
            this.$message.success('删除成功');
            this.search();
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
  }
}
</script>

<style scoped>
.auth-code-management {
  padding: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
</style>