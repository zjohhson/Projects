import Vue from "vue";
import Router from "vue-router";

Vue.use(Router);

export default new Router({
  mode: "history",
  routes: [
    {
      path: "/",
      name: "Main",
      component: () => import("./views/Main.vue"),
    },
    {
      path: "/signIn",
      name: "signIn",
      component: () => import("./views/SignInUp.vue"),
    },
    {
      path: "/:catchAll(.*)",
      component: () => import("./views/Main.vue"),
      name: "invalid",
    },
  ],
});
