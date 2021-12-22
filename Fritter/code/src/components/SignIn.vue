<template>
  <div>
    <form id="sign-in" class='component' v-on:submit.prevent="signIn" method="post">
      <h3>Returning User? Sign in</h3>
      
      <input id='in-username' v-model.trim='username' type='text' name='username' placeholder="Username">
      <input id='in-password' v-model.trim='password' type='password' name='password' placeholder="Password">
      <input type='submit' value='Sign In' class="btn btn-primary">

      <p class="error">{{error}}</p>
    </form>
    
  </div>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";

export default {
  name: "SignIn",

  data() {
    return {
      error: "", 
      username: "",
      password: "",
      intervalID: null
    }
  },

  methods: {
    signIn: function() {
      this.error = "";

      if (this.username == "") {
          this.error = "Please enter a username with atleast 1 character.";
        } else if (this.password == "") {
          this.error = "Please enter a password with atleast 1 character.";
        } else {
        axios.post("/api/user/signin", {username: this.username, password: this.password})
          .then((response) => {  
              eventBus.$emit("signin-success", {
                data: response.data.username
              })
          })
          .catch((error) => {
              this.error = this.error = error.response.data.error; 
          }).finally(() => {
            eventBus.$emit("freet-data-update"); 
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