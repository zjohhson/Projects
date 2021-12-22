<template>
  <div>
    <form id="change-username" v-on:submit.prevent="changeUsername">
      <h3>Change Username</h3>
      <div>
        <input id="username" name="username" v-model="username" placeholder="new username">
      </div>
      <input type="submit" value="Change Username" class="btn btn-primary">
      <p class="error">{{error}}</p>

      
    </form>
  </div>
</template>

<script>
import axios from "axios";
import { eventBus } from '../main.js'


export default {
  name: "ChangeUsername",
  props: [],
  data() {
    return {
    username: "",
    intervalID: null, 
    error: "", 
    };
  },

  methods: {
    changeUsername() {
      this.error = "";

      let original = this.$cookies.get('fritter-auth');

      if(!this.username) {
            this.error = "Username can't be empty"; 
        } else {
          axios.put("/api/user/" + this.username, {username: this.username})
            .then((response) => {
              
              eventBus.$emit("signin-success", {data: response.data.username}); 
              eventBus.$emit("freet-data-update"); 
              eventBus.$emit('username-change', {data: original});
            })
            .catch((error) => {
                this.error = error.response.data.error;
            })
            this.username = ""
        }
    },

    resetForm: function() {
      this.username = ""
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