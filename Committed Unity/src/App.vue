<template>
  <v-app>
    <v-main>
      <v-container fluid class="pa-0" id="app">
        <v-snackbar
          v-if="snackbar.message"
          v-model="snackbar.show"
          timeout="3000"
        >
          {{ snackbar.message }}
          <template v-slot:action="{ attrs }">
            <v-btn
              v-if="errorMsg"
              color="pink"
              text
              v-bind="attrs"
              @click="snackbar = false"
            >
              Close
            </v-btn>
            <v-btn
              v-else-if="successMsg"
              color="success"
              text
              v-bind="attrs"
              @click="snackbar = false"
            >
              Close
            </v-btn>
          </template>
        </v-snackbar>
        <router-view />
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import axios from "axios";
import { eventBus } from "./main";

export default {
  name: "app",
  components: {},
  data() {
    return {
      snackbar: {
        show: false,
        message: null,
        color: null,
      },
      errorMsg: "",
      successMsg: "",
    };
  },
  computed: {},
  created: function () {
    axios
      .get("/api/users/session")
      .then((response) => {
        if (response.data.signedIn === false) {
          this.$cookie.set("auth-username", "");
        }
      })
      .catch((error) => {
        // this.errorMsg = error.response.data.error;
        this.snackbar = {
          message: error.response.data.error,
          color: "error",
          show: true,
        };
        this.errorMsg = error.response.data.error;
      });

    eventBus.$on("error-msg", (data) => {
      this.snackbar = {
        message: data.errorMsg,
        color: "error",
        show: true,
      };
      this.errorMsg = data.errorMsg;
    });
    eventBus.$on("success-msg", (data) => {
      this.snackbar = {
        message: data.successMsg,
        color: "grey",
        show: true,
      };
      this.successMsg = data.successMsg;
    });
    eventBus.$on("signed-in", (data) => {
      this.$cookie.set("auth-username", data.username);
      if (this.$route.name !== "Main") {
        this.$router.push({ name: "Main" });
      }
    });
    eventBus.$on("signed-out", () => {
      this.$cookie.set("auth-username", "");
      if (this.$route.name !== "signIn") {
        this.$router.push({ name: "signIn" });
      }
    });
  },
  methods: {},
};
</script>

<style></style>
