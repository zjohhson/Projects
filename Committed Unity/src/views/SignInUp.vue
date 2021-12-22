<template>
  <div id="sign-in-up">
    <NavBar :username="username" view="sign-in" />
    <h3 style="text-align: center; margin-top: 20px">
      Sign in to view and save meetings you're interested in!
    </h3>
    <SignIn />
    <SignUp />
  </div>
</template>

<script>
import { eventBus } from "../main";
import SignIn from "../components/SignIn.vue";
import SignUp from "../components/SignUp.vue";
import NavBar from "../components/NavBar.vue";

export default {
  name: "SignInUp",
  components: {
    SignIn,
    SignUp,
    NavBar,
  },
  data() {
    return {};
  },
  computed: {
    username: function () {
      return this.$cookie.get("auth-username");
    },
  },
  created: function () {
    eventBus.$on("change-to-home-page", () => {
      if (this.$route.name !== "Main") {
        this.$router.push({ name: "Main" });
      }
    });
  },
  methods: {},
};
</script>

<style></style>
