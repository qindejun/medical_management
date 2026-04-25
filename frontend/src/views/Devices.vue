<template>
    <div>
        <!-- 查询表单 -->
        <el-form :inline="true" :model="params" class="search-form">
            <el-form-item label="名称">
                <el-input v-model="params.deviceName" placeholder="请输入器械名称" />
            </el-form-item>
            <el-form-item label="类型">
                <el-select v-model="params.category" placeholder="请选择类型">
                    <el-option label="诊断设备" value="诊断设备" />
                    <el-option label="治疗设备" value="治疗设备" />
                    <el-option label="监护设备" value="监护设备" />
                    <el-option label="手术器械" value="手术器械" />
                    <el-option label="耗材" value="耗材" />
                </el-select>
            </el-form-item>
            <el-form-item label="生产商">
                <el-input v-model="params.manufacturer" placeholder="请输入生产商" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="search">查询</el-button>
                <el-button @click="clearSearch">清空</el-button>
            </el-form-item>
        </el-form>

        <el-button type="danger" @click="handleBatchDelete" :disabled="multipleSelection.length === 0">
            批量删除
        </el-button>
        <el-button type="success" @click="showAddDialog = true; resetDeviceForm()">
            新增器械
        </el-button>

        <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" />
            <el-table-column prop="deviceName" label="名称" />
            <el-table-column prop="modelNumber" label="型号" />
            <el-table-column prop="category" label="类型" />
            <el-table-column prop="manufacturer" label="生产商" />
            <el-table-column prop="registrationCert" label="注册证号" />
            <el-table-column prop="costPrice" label="成本价" />
            <el-table-column prop="unitPrice" label="售价" />
            <el-table-column prop="storageRequirements" label="保存方法" />
            <el-table-column label="操作" width="150">
                <template slot-scope="scope">
                    <el-button size="small" @click="handleEdit(scope.row)">修改</el-button>
                    <el-button size="small" type="danger" @click="handleDelete(scope.row.deviceID)">
                        删除
                    </el-button>
                </template>
            </el-table-column>
            <template slot="empty">
                <span>暂无数据</span>
            </template>
        </el-table>

        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
            :current-page="params.pageNum" :page-sizes="[5, 10, 15, 20]" :page-size="params.pageSize"
            layout="total, sizes, prev, pager, next, jumper" :total="total">
        </el-pagination>

        <el-dialog :visible.sync="showAddDialog" title="新增医疗器械" width="600px" center>
            <el-form :model="deviceForm" :rules="deviceRules" ref="deviceFormRef">
                <el-form-item label="名称" prop="deviceName">
                    <el-input v-model="deviceForm.deviceName" />
                </el-form-item>
                <el-form-item label="型号" prop="modelNumber">
                    <el-input v-model="deviceForm.modelNumber" />
                </el-form-item>
                <el-form-item label="类型" prop="category">
                    <el-select v-model="deviceForm.category" placeholder="请选择类型">
                        <el-option label="诊断设备" value="诊断设备" />
                        <el-option label="治疗设备" value="治疗设备" />
                        <el-option label="监护设备" value="监护设备" />
                        <el-option label="手术器械" value="手术器械" />
                        <el-option label="耗材" value="耗材" />
                    </el-select>
                </el-form-item>
                <el-form-item label="生产商" prop="manufacturer">
                    <el-input v-model="deviceForm.manufacturer" />
                </el-form-item>
                <el-form-item label="注册证号" prop="registrationCert">
                    <el-input v-model="deviceForm.registrationCert" />
                </el-form-item>
                <el-form-item label="成本价" prop="costPrice">
                    <el-input v-model.number="deviceForm.costPrice" />
                </el-form-item>
                <el-form-item label="售价" prop="unitPrice">
                    <el-input v-model.number="deviceForm.unitPrice" />
                </el-form-item>
                <el-form-item label="保存方法" prop="storageRequirements">
                    <el-input v-model="deviceForm.storageRequirements" />
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="showAddDialog = false">取消</el-button>
                <el-button type="primary" @click="handleAdd">保存</el-button>
            </div>
        </el-dialog>

        <el-dialog :visible.sync="showEditDialog" title="修改医疗器械" width="600px" center>
            <el-form :model="deviceForm" :rules="deviceRules" ref="deviceFormRef">
                <el-form-item label="名称" prop="deviceName">
                    <el-input v-model="deviceForm.deviceName" />
                </el-form-item>
                <el-form-item label="型号" prop="modelNumber">
                    <el-input v-model="deviceForm.modelNumber" />
                </el-form-item>
                <el-form-item label="类型" prop="category">
                    <el-select v-model="deviceForm.category" placeholder="请选择类型">
                        <el-option label="诊断设备" value="诊断设备" />
                        <el-option label="治疗设备" value="治疗设备" />
                        <el-option label="监护设备" value="监护设备" />
                        <el-option label="手术器械" value="手术器械" />
                        <el-option label="耗材" value="耗材" />
                    </el-select>
                </el-form-item>
                <el-form-item label="生产商" prop="manufacturer">
                    <el-input v-model="deviceForm.manufacturer" />
                </el-form-item>
                <el-form-item label="注册证号" prop="registrationCert">
                    <el-input v-model="deviceForm.registrationCert" />
                </el-form-item>
                <el-form-item label="售价" prop="unitPrice">
                    <el-input v-model.number="deviceForm.unitPrice" />
                </el-form-item>
                <el-form-item label="保存方法" prop="storageRequirements">
                    <el-input v-model="deviceForm.storageRequirements" />
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="showEditDialog = false">取消</el-button>
                <el-button type="primary" @click="handleUpdate">保存</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import request from '@/utils/request';

export default {
    data() {
        return {
            tableData: [],
            loading: false,
            // 分页参数
            total: 0,

            // 搜索表单数据
            params: {
                deviceName: '',
                category: '',
                manufacturer: '',
                pageNum: 1,
                pageSize: 5,
            },
            // 新增/修改表单数据
            deviceForm: {
                deviceID: null,
                deviceName: '',
                modelNumber: '',
                category: '',
                manufacturer: '',
                registrationCert: '',
                costPrice: 0,
                unitPrice: 0,
                storageRequirements: ''
            },
            // 表单验证规则
            deviceRules: {
                deviceName: [{ required: true, message: '请输入器械名称', trigger: 'blur' }],
                modelNumber: [{ required: true, message: '请输入型号', trigger: 'blur' }],
                category: [{ required: true, message: '请选择类型', trigger: 'change' }],
                manufacturer: [{ required: true, message: '请输入生产商', trigger: 'blur' }],
                registrationCert: [{ required: true, message: '请输入注册证号', trigger: 'blur' }],
                unitPrice: [{ required: true, message: '请输入成本价', trigger: 'blur' }],
                unitPrice: [{ required: true, message: '请输入售价', trigger: 'blur' }]
            },
            // 对话框状态
            showAddDialog: false,
            showEditDialog: false,
            // 选中的行数据（用于批量操作）
            multipleSelection: []
        };
    },
    // 页面加载时 
    created() {
        console.log('Devices component created');
        this.load();
    },
    methods: {
        load() {
            this.loading = true;
            request.get("/devices", { params: this.params }).then(res => {
                console.log('Load response:', res); // 打印响应数据
                if (res.code === 1 || res.code === '1') {
                    this.tableData = res.data.list || [];
                    this.total = res.data.total || 0;
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
        search() {
            request.get("/devices/search", { params: this.params }).then(res => {
                console.log('Search response:', res); // 打印响应数据
                if (res.code === 1 || res.code === '1') {
                    this.tableData = res.data.list || [];
                    this.total = res.data.total || 0;
                } else {
                    this.$message.error(`加载数据失败: ${res.msg || '未知错误'}`);
                }
            }).catch(error => {
                console.error('Request error:', error);
                this.$message.error('请求失败，请检查网络或后端服务');
            });
        },
        clearSearch() {
            this.params = {
                deviceName: '',
                category: '',
                manufacturer: '',
                // 重置 pageNum 和 pageSize
                pageNum: 1,
                pageSize: this.params.pageSize
            };
            this.load();
        },
        handleSizeChange(newSize) {
            this.params.pageSize = newSize;
            this.params.pageNum = 1; // 切换每页大小时重置到第一页
            this.load();
        },
        handleCurrentChange(newPage) {
            this.params.pageNum = newPage;
            this.load();
        },
        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        handleAdd() {
            this.$refs.deviceFormRef.validate((valid) => {
                if (valid) {
                    request.post("/devices", this.deviceForm).then(res => {
                        if (res.code === 1 || res.code === '1') {
                            this.$message.success('新增成功'); 
                            this.showAddDialog = false;
                            this.resetDeviceForm();
                            this.load();
                        } else {
                            this.$message({
                                message: res.msg,
                                type: "error"
                            })
                        }
                    }).catch(error => {
                        console.error('Request error:', error);
                        this.$message.error('请求失败，请检查网络或后端服务');
                    });
                } else {
                    return false;
                }
            });
        },
        handleEdit(row) {
            this.deviceForm = { ...row };
            this.showEditDialog = true;
        },
        handleUpdate() {
            this.$refs.deviceFormRef.validate((valid) => {
                if (valid) {
                    request.put("/devices", this.deviceForm).then(res => {
                        if (res.code === 1 || res.code === '1') {
                            this.$message.success('修改成功');
                            this.showEditDialog = false;
                            this.resetDeviceForm();
                            this.load();
                        } else {
                            this.$message.error(`修改失败: ${res.msg || '未知错误'}`);
                        }
                    }).catch(error => {
                        console.error('Request error:', error);
                        this.$message.error('请求失败，请检查网络或后端服务');
                    });
                } else {
                    return false;
                }
            });
        },
        handleDelete(deviceId) {
            this.$confirm('确定要删除该器械吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                request.delete(`/devices/${deviceId}`).then(res => {
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
        },
        // 批量删除方法
        handleBatchDelete() {           
            this.$confirm('确定要批量删除选中的器械吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                request.put(`/devices/batch`,this.multipleSelection).then(res => {
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
        resetDeviceForm() {
            this.deviceForm = {
                deviceID: null,
                deviceName: '',
                modelNumber: '',
                category: '',
                manufacturer: '',
                registrationCert: '',
                unitPrice: 0,
                costPrice: 0,
                storageRequirements: ''
            };
        }
    }
}
</script>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.el-table {
  width: 100%;
  max-height: 608px;
  overflow-y: auto;
  margin-top: 20px;
  box-sizing: border-box;
}

/* 表头固定（若需要） */
.el-table__header {
  background-color: #fff;
  /* 表头背景色 */
}

.el-pagination {
  margin-top: 20px;
  text-align: left;
}
</style>    