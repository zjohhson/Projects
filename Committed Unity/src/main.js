import Vue from "vue";
import VueCookie from "vue-cookie";
import App from "./App.vue";
import router from "./router";
import Vuetify from "vuetify";
import "vuetify/dist/vuetify.min.css";

Vue.use(VueCookie);
Vue.use(Vuetify);

export const eventBus = new Vue();

Vue.config.productionTip = false;

new Vue({
  router,
  vuetify: new Vuetify(),
  render: (h) => h(App),
}).$mount("#app");
