<template>
  <!-- TODO: use 'short-container-popular' : 'short-container' CSS classes to delineate popular shorts -->
  <div :class="'user-container'">
    <p>@{{ user.username }}</p>
    <div class="user-actions">
      <button v-if = "isFollowing" v-on:click="unfollow">Unfollow</button>
      <button v-else v-on:click="follow">Follow</button>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";

export default {
  name: "User",

  props: {
    user: Object,
    isFollowing: Boolean 
  },

  methods: {

    follow: function() {
        axios.patch(`api/user/${this.$cookie.get('fritter-auth')}/follow/${this.user.username}`, {})
        .then(() => {
          eventBus.$emit("follow-user-success", this.user);
        })
        .catch(() => {
          // handle error
          eventBus.$emit("follow-user-error", this.user);
        });
    },

    unfollow: function() {
        axios.patch(`api/user/${this.$cookie.get('fritter-auth')}/unfollow/${this.user.username}`, {})
        .then(() => {
          eventBus.$emit("unfollow-user-success", this.user);
        })
        .catch(() => {
          // handle error
          eventBus.$emit("unfollow-user-error", this.user);
        });
    }
  }
};
</script>