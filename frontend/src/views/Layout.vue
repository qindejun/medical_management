<template>
    <div id = "layout">
      <el-container style="height: 100vh;">
        <el-header style="background-color: #0080c0; display: flex; align-items: center; padding-left: 20px;">
          <img src="@/assets/医疗器械.png" style="width: 50px;">
          <span style="font-size: 20px;margin-top: 5px;color: #ffffff;">&nbsp;医疗器械公司信息管理系统</span>
        </el-header>
        <el-container style="height: calc(100% - 60px);">
          <el-aside width="200px" style="background-color: #0080c0; height: 100%;">
            <el-menu :default-active="$router.path" router class="el-menu-vertical-demo" @open="handleOpen" @close="handleClose"
              background-color="#0080c0" text-color="#fff" active-text-color="#ffd04b">
              <el-menu-item index="/">
                <i class="el-icon-menu"></i>
                <span slot="title">首页</span>
              </el-menu-item>

              <el-menu-item index="/devices" v-if="user.role ===1 || user.role ===3">
                <i class="el-icon-s-tools"></i>
                <span slot="title">医疗器械信息维护</span>
              </el-menu-item>

              <el-submenu index="/inventory" v-if="user.role ===1 || user.role ===3">
                <template slot="title">
                  <i class="el-icon-s-operation"></i>
                  <span>库存管理</span>
                </template>
                <el-menu-item index="/inventory/management">库存信息管理</el-menu-item>
                <el-menu-item index="/inventory/transaction">库存操作记录</el-menu-item>
              </el-submenu>

              <el-submenu index="/order" v-if="user.role ===1 || user.role ===2">
                <template slot="title">
                  <i class="el-icon-s-order"></i>
                  <span>订单管理</span>
                </template>
                <el-menu-item index="/order/myorder" v-if="user.role ===2">我的订单</el-menu-item>
                <el-menu-item index="/order/history">历史订单</el-menu-item>
              </el-submenu>
  
              <el-submenu index="/stats">
                <template slot="title">
                  <i class="el-icon-document-copy"></i>
                  <span>数据统计</span>
                </template>
                <el-menu-item index="/stats/inventory">库存信息统计</el-menu-item>
                <el-menu-item index="/stats/order">订单信息统计</el-menu-item>
              </el-submenu>
  
              <el-menu-item index="/emp" v-if="user.role === 1">
                <i class="el-icon-user-solid"></i>
                <span slot="title">员工管理</span>
              </el-menu-item>

              <el-menu-item index="/authcode" v-if="user.role === 1">
                <i class="el-icon-document"></i>
                <span slot="title">授权码管理</span>
              </el-menu-item>
              <el-menu-item index="user">
                <i class="el-icon-user"></i>
                <span slot="title">个人中心</span>
              </el-menu-item>
            </el-menu>
          </el-aside>
          <el-main style="overflow: hidden; background-color: aliceblue; height: 100%;">
            <router-view/>
          </el-main>
        </el-container>
      </el-container>
  
    </div>
  </template>
  
  <script>
  export default {
    name: 'HomeView',
    data(){
      return{
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
      }
    },
    methods: {
      handleOpen(key, keyPath) {
        console.log(key, keyPath);
      },
      handleClose(key, keyPath) {
        console.log(key, keyPath);
      }
    }
  }
  </script>
  
  <style scoped>
  .el-menu-item span,
  .el-submenu__title span,
  .el-submenu .el-menu-item {
    user-select: none;
    cursor: default;
  }
  .el-menu {
    border-right: none !important;
  }
  </style>    