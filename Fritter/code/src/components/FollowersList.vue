<template>
  <div class="subcontainer">

    <h4> {{this.followers.length}} followers </h4>

    <div class="followbox">  
        <div v-if="followers.length">
            <li class="following" v-for="user in followers" v-bind:key="user">
                @{{user}}
            </li>
        </div>
        <div v-else>
          <p style="text-align: center;">No followers.</p>
        </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";

export default {
  name: "FollowersList",

  components: {},

  data() {
    return {
      username: this.$cookies.get('fritter-auth'),
      followers: []
    };
  },

  methods: {
    loadFollowers: function() {
      axios.get(`/api/user/followers`)
        .then(response => {
          this.followers = response.data;
        })
    },

    loadUsers: function() {
      axios.get("/api/user")
        .then(response => {
          this.users = response.data.users;
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
      this.loadFollowers();
    });

    eventBus.$on("unfollow-user-success", () => {
      this.clearMessages();
      this.loadFollowers();
    });

    eventBus.$on("delete-user-success", () => {
      this.clearMessages();
      this.loadFollowers();
    });

    eventBus.$on("change-username-success", () => {
      this.clearMessages();
      this.loadFollowers();
    });
  },

  mounted: function() {
    this.loadFollowers();
  },
};
</script>