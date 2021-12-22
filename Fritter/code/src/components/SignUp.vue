<template>
  <div>
    <form id="sign-up" class='component' v-on:submit.prevent="signUp" method="post">
      <h3>Join Fritter!</h3>

      <input id='up-username' v-model.trim='username' type='text' name='username' placeholder="Username">
      <input id='up-password' v-model.trim='password' type='password' name='password' placeholder="Password">
      <input type='submit' value='Sign Up' class="btn btn-primary">

      <p class="error">{{error}}</p>

    </form>
  </div>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";

export default {
  name: "SignUp",

  data() {
    return {
      error: "",
      username: "",
      password: "",
      intervalID: null
    }
  },

  methods: {
    signUp: function() {
      this.error = "";

        if (this.username == "") {
          this.error = "Please enter a username with atleast 1 character.";
        } else if (this.password == "") {
          this.error = "Please enter a password with atleast 1 character.";
        } else {
          axios.
          post("/api/user", {username: this.username, password: this.password})
          .then((response) => {
              eventBus.$emit('signin-success', {data: response.data.username});
              this.error = ""
              eventBus.$emit("freet-data-update"); 
          })
          .catch((error) => {
              this.error = error.response.data.error;
          })
          .then(() => {
            // always executed
            this.resetForm();
            this.clearMessages();
          });
        }

    },

    resetForm: function() {
      this.username = ""
      this.password = ""
    },

    clearMessages: function() {
      if (this.intervalID) {
        clearInterval(this.intervalID);
      }
      this.intervalID = setInterval(() => {
        this.errors = [];
      }, 5000);
    }
  }
}
</script>