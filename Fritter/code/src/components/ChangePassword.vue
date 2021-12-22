<template>
  <div>
    <form id="change-password" v-on:submit.prevent="changePassword">
      <h3>Change Password</h3>
      <div>
        <input id="password" name="password" type ="password" v-model="password" placeholder="new password">
      </div>
      <input type="submit" value="Change Password" class="btn btn-primary">
      <p class="error">{{error}}</p>
      
    </form>
  </div>
</template>

<script>
import axios from "axios";


export default {
  name: "ChangePassword",
  props: [],
  data() {
    return {
    password: "",
    error: "",
    intervalID: null
    };
  },
  created: function() {},
  methods: {
    changePassword() {
      this.error = ""; 
      axios.put("/api/user", {password: this.password})
        .catch((error) => {
            this.error = error.response.data.error;
        })
        .then(() => {
            // always executed
            this.resetForm();
            this.clearMessages();
        });
    },

    resetForm: function() {
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
  },
};
</script>

<style></style>