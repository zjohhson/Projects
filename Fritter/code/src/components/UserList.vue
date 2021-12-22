<template>
  <div class="subcontainer">

    <h1> Active Accounts </h1>
        
    <div>
      <div v-if='success' class="success-message">
        {{ success }}
      </div>
      <div v-if='error' class="error-message">
        {{ error }}
      </div>

      <div style="height:240px;width480px;border:1px solid #ccc;overflow:auto;">  
        <div v-if="filteredByFollowing.length">
          <User
            v-for="userEntry in filteredByFollowing"
            v-bind:key="userEntry[0].username"
            v-bind:user="userEntry[0]"
            v-bind:isFollowing="userEntry[1]"
          />
        </div>
        <div v-else>
          <p style="text-align: center;">No other active accounts.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import User from "./User";

import { eventBus } from "../main";

export default {
  name: "UserList",

  components: { User },

  data() {
    return {
      error: "",
      success: "",
      userlist: [],
      username: this.$cookie.get('fritter-auth'),
      followers: [],
      following: []
    };
  },

  created: function() {
    eventBus.$on("follow-user-success", res => {
      this.success = `Followed @${res.username}.`;
      this.clearMessages();
      this.loadUsers();
      this.loadFollowers();
      this.loadFollowing();
    });

    eventBus.$on("unfollow-user-success", res => {
      this.success = `Unfollowed @${res.username}.`;
      this.clearMessages();
      this.loadUsers();
      this.loadFollowers();
      this.loadFollowing();
    });

    eventBus.$on("follow-user-error", res => {
      this.error = `Error following @${res.username}.`;
      this.clearMessages();
      this.loadUsers();
      this.loadFollowers();
      this.loadFollowing();
    });

    eventBus.$on("unfollow-user-error", res => {
      this.error = `Error unfollowing @${res.username}.`;
      this.clearMessages();
      this.loadUsers();
      this.loadFollowers();
      this.loadFollowing();
    });

  },

  computed: {
    filteredUsers: function() {
      if (this.username) {
        return this.userlist.filter(user => user.username!=this.username)
      }
      else {
        return this.userlist
      }
    },

    filteredByFollowing: function() {
      return this.filteredUsers.map((user) => {
          return [user, this.following.includes(user.username)];
      })
    }
  },

  mounted: function() {
    this.loadUsers();
    this.loadFollowers();
    this.loadFollowing();
  },

  methods: {
    loadUsers: function() {
      axios.get("/api/user")
        .then(response => {
          this.userlist = response.data.users;
        })
    },

    loadFollowers: function() {
      axios.get(`/api/user/${this.$cookie.get('fritter-auth')}/followers`)
      .then(response => {
          this.followers = response.data.followers;
      })
    },

    loadFollowing: function() {
      axios.get(`/api/user/${this.$cookie.get('fritter-auth')}/following`)
      .then(response => {
          this.following = response.data.following;
      })
    },

    clearMessages: function() {
      setInterval(() => {
        this.success = "";
        this.error = "";
      }, 5000);
    }
  }
};
</script>