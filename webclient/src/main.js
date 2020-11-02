import Vue from 'vue'
import App from './App.vue'

import vuetify from './plugins/Vuetify';
import vueRouter from 'vue-router'
import router from './routes'
import store from './store';
import 'vue-loading-overlay/dist/vue-loading.css';
import vueFilterDateFormat from '@vuejs-community/vue-filter-date-format';

import Loading from "vue-loading-overlay";

//Plugins
Vue.config.productionTip = false;
Vue.use(vueRouter);
Vue.use(vueFilterDateFormat)

//Components
Vue.component('loading', Loading);


new Vue({
  vuetify,
  router,
  store,
  render: function (h) { return h(App) },
}).$mount('#app');
