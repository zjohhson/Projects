<template>
  <v-row justify="center" class="user-form">
    <v-card>
      <v-card-title>Sign In</v-card-title>
      <v-card-text>
        <v-text-field
          v-model="username"
          :counter="30"
          label="Username"
          required
        ></v-text-field>
        <v-text-field
          v-model="password"
          :counter="30"
          label="Password"
          type="password"
          required
        ></v-text-field>
        <v-btn class="mr-4" @click="signIn"> submit </v-btn>
      </v-card-text>
    </v-card>
  </v-row>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";

export default {
  name: "SignIn",
  data() {
    return {
      username: "",
      password: "",
    };
  },
  created: function () {},
  methods: {
    signIn() {
      axios
        .post("/api/users/session", {
          username: this.username,
          password: this.password,
        })
        .then(() => {
          eventBus.$emit("signed-in", { username: this.username });
          this.username = "";
          this.password = "";
        })
        .catch((error) => {
          eventBus.$emit("error-msg", {
            errorMsg: error.response.data.error,
          });
        });
    },
  },
};
</script>

<style scoped>
.v-card {
  width: 40%;
  min-width: 250px;
  margin-top: 28px;
  margin-bottom: 16px;
}
</style>
