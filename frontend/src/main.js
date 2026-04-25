import Vue from 'vue';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import App from './App.vue';
import router from './router';
import '@/assets/global.css'
import { Pagination } from 'element-ui'; // 假设使用 Element UI 的分页组件
Vue.component('pagination', Pagination); // 注册为全局组件


Vue.use(ElementUI);


Vue.config.productionTip = false

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
