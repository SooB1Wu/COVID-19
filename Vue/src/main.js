import Vue from 'vue'
import router from "./router";
import store from "./store/index";
import App from './App.vue'

import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

// //引入axios模块(先下载`axios`--)
 import axios from 'axios'
// //将axios挂载在vue原型链上
 Vue.prototype.$axios = axios;





Vue.use(ElementUI);

Vue.config.productionTip = false

new Vue({
    router,
    store,
  render: h => h(App),
}).$mount('#app')
