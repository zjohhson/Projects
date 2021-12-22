<template>
  <div id="delete-user" class="component">
    <button v-on:click="deleteUser" class = "bigButtonDanger">Delete User</button>
  </div>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";

export default {
  name: "DeleteUser",
  
  methods: {
    deleteUser: function() {
      axios.delete("/api/user")
        .then(() => {
            eventBus.$emit("delete-success"); 
        })
    }
  }
}
</script>