<template>
  <v-row justify="center" class="user-form">
    <v-card>
      <v-card-title>Sign Up</v-card-title>
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
        <v-text-field
          v-model="firstName"
          :counter="30"
          label="First Name"
          required
        ></v-text-field>
        <v-text-field
          v-model="lastName"
          :counter="30"
          label="Last Name"
          required
        ></v-text-field>
        <v-checkbox
          v-model="checked"
          label="Are you an admin?"
          name="isAdmin"
        />
        <!-- <label for="isAdmin"> Are you an admin?</label><br/> -->
        <v-text-field
          v-if="checked"
          v-model="adminCode"
          label="Admin Code"
          required
        ></v-text-field>
        <!-- <span v-else> TODO show selecting initial tags of interest </span><br> -->
        <v-btn class="mr-4" @click="signUp"> submit </v-btn>
      </v-card-text>
    </v-card>
  </v-row>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";

export default {
  name: "SignUp",
  data() {
    return {
      username: "",
      password: "",
      firstName: "",
      lastName: "",
      checked: "",
      adminCode: "",
    };
  },
  created: function () {},
  methods: {
    signUp() {
      axios
        .post("/api/users/", {
          username: this.username,
          password: this.password,
          firstName: this.firstName,
          lastName: this.lastName,
          isAdmin: this.checked ? this.adminCode : false,
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
  margin-bottom: 16px;
}
</style>
