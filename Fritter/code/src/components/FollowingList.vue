<template>
  <div class="subcontainer">

    <h4> {{this.following.length}} following </h4>

    <div class="followbox">  
        <div v-if="following.length">
            <li class="following" v-for="user in following" v-bind:key="user">
                @{{user}}
            </li>
        </div>
        <div v-else>
          <p style="text-align: center;">No following.</p>
        </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";

export default {
  name: "FollowingList",

  components: {},

  data() {
    return {
      username: this.$cookies.get('fritter-auth'),
      following: []
    };
  },

  methods: {
    loadFollowing: function() {
      axios.get(`/api/user/following`)
        .then(response => {
          this.following = response.data.filter(u => u != this.username);
        })
    },

    clearMessages: function() {
      setInterval(() => {
        this.success = "";
        this.error = "";
      }, 5000);
    }
  },

  created: function() {
    eventBus.$on("follow-user-success", () => {
      this.clearMessages();
      this.loadFollowing();
    });

    eventBus.$on("unfollow-user-success", () => {
      this.clearMessages();
      this.loadFollowing();
    });

    eventBus.$on("delete-user-success", () => {
      this.clearMessages();
      this.loadFollowing();
    });

    eventBus.$on("change-username-user-success", () => {
      this.clearMessages();
      this.loadFollowing();
    });
  },

  mounted: function() {
    this.loadFollowing();
  }
};
</script>