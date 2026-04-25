<template>
  <div class="home">
    <div class="text-container">
      <div class="welcome-text">欢迎进入医疗器械公司信息管理系统</div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Home",
};
</script>

<style scoped>
.home {
  background: linear-gradient(
    to bottom right,
    #ffffff,
    #4a90e2
  ); /* 左上白色到右下蓝色的渐变 */
  min-height: 100vh; /* 确保背景覆盖整个视口 */
  display: flex;
  justify-content: center;
  align-items: flex-start; /* 改为flex-start使内容靠上 */
  padding-top: 10vh; /* 添加顶部内边距进一步上移 */
}

.text-container {
  text-align: center;
  background: rgba(255, 255, 255, 0.9); /* 半透明白色背景增强标题区域对比 */
  padding: 40px;
  border-radius: 15px; /* 圆角 */
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2); /* 阴影效果 */
}

.welcome-text {
  font-size: 3.5rem; /* 更大字体 */
  font-weight: 800; /* 更粗 */
  color: #1a3c73; /* 深蓝色文字 */
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif; /* 更现代的字体 */
  letter-spacing: 1.5px; /* 字间距 */
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1); /* 轻微文字阴影 */
  padding: 10px;
}
</style>