<template>
  <div id="sign-out" class="component">
    <button v-on:click="signOut" class="bigButton">Sign Out</button>
  </div>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";

export default {
  name: "SignOut",
  
  methods: {
    signOut: function() {
      axios.post("/api/user/signout", {username: this.username, password: this.password})
        .then(() => {
            eventBus.$emit("signout-success");
            eventBus.$emit("freet-data-update"); 
        })
    }
  }
}
</script>