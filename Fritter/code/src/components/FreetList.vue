<template>
  <div class="subcontainer">

    <h4> View Freets by Author </h4>
      <input class="edit" v-model.trim='temp' type="text" name="author" placeholder="Enter Author">
      <button v-on:click="submitAuthor" class="btn btn-primary">View by Author</button>
    <br>
    <br>

    <h1>
      Feed
    </h1>
        
    <div>
      <div v-if='success' class="success-message">
        {{ success }}
      </div>
      <div v-if='error' class="error-message">
        {{ error }}
      </div>

      <div style="height:240px;width480px;border:1px solid #ccc;overflow:auto;">  
        <div v-if="filteredFreets.length">
          <Freet
            v-for="freetEntry in filteredLikedFreets"
            v-bind:key="freetEntry[0].id"
            v-bind:freet="freetEntry[0]"
            v-bind:liked="freetEntry[1]"
          />
        </div>
        <div v-else>
          <p style="text-align: center;">There are no freets to display.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import Freet from "./Freet";
import DeleteUser from "./DeleteUser";

import { eventBus } from "../main";

export default {
  name: "FreetList",

  components: { Freet, DeleteUser },

  data() {
    return {
      error: "",
      success: "",
      freetlist: [],
      temp: "",
      author: "",
      likedFreets: []
    };
  },

  created: function() {
    eventBus.$on("create-freet-success", res => {
      this.freetlist.unshift(res.data);
      this.loadFreets();
    });

    eventBus.$on("update-freet-success", res => {
      this.success = `Freet ${res.id} has been updated.`;
      this.clearMessages();
      this.loadFreets();
      this.loadUserLikes();
    });

    eventBus.$on("update-refreet-success", res => {
      this.success = `Freet ${res.id} has been updated.`;
      this.clearMessages();
      this.loadFreets();
      this.loadUserLikes();
    });

    eventBus.$on("delete-freet-success", res => {
      this.success = `Freet ${res.id} has been deleted.`;
      this.clearMessages();
      this.loadFreets();
      this.loadUserLikes();
    });

    eventBus.$on("delete-refreet-success", res => {
      this.success = `Freet ${res.id} has been deleted.`;
      this.clearMessages();
      this.loadFreets();
      this.loadUserLikes();
    });

    eventBus.$on("refreet-success", res => {
      this.success = `Freet ${res.id} has been refreeted.`;
      this.clearMessages();
      this.loadFreets();
    });
    
    eventBus.$on("delete-user-success", res => {
      this.freetlist = this.freetlist.filter(f => f.username != res.username)
      this.clearMessages();
      this.loadFreets();
      this.loadUserLikes();
    });

    eventBus.$on("like-freet-success", res => {
      this.success = `You liked freet ${res.id}.`;
      this.clearMessages();
      this.loadFreets();
      this.loadUserLikes();
    });

    eventBus.$on("unlike-freet-success", res => {
      this.success = `You unliked freet ${res.id}.`;
      this.clearMessages();
      this.loadFreets();
      this.loadUserLikes();
    });

    eventBus.$on("update-freet-error", res => {
      this.error = `Error updating Freet ${res.id}.`;
      this.clearMessages();
      this.loadFreets();
      this.loadUserLikes();
    });

    eventBus.$on("update-refreet-error", res => {
      this.error = `Error updating Freet ${res.id}.`;
      this.clearMessages();
      this.loadFreets();
      this.loadUserLikes();
    });

    eventBus.$on("delete-freet-error", res => {
      this.error = `Error deleting Freet ${res.id}.`;
      this.clearMessages();
      this.loadFreets();
      this.loadUserLikes();
    });

    eventBus.$on("delete-refreet-error", res => {
      this.error = `Error deleting Freet ${res.id}.`;
      this.clearMessages();
      this.loadFreets();
      this.loadUserLikes();
    });

    eventBus.$on("like-freet-error", res => {
      this.error = `Error liking Freet ${res.id}.`;
      this.clearMessages();
      this.loadFreets();
      this.loadUserLikes();
    });

    eventBus.$on("unlike-freet-error", res => {
      this.error = `Error unliking Freet ${res.id}.`;
      this.clearMessages();
      this.loadFreets();
      this.loadUserLikes();
    });

    eventBus.$on("refreet-error", res => {
      this.error = `Error refreeting Freet ${res.id}.`;
      this.clearMessages();
      this.loadFreets();
    });

  },

  computed: {
    filteredFreets: function() {
      if (this.author) {
        return this.freetlist.filter(freet => freet.username===this.author)
      }
      else {
        return this.freetlist
      }
    },

    filteredLikedFreets: function() {
      return this.filteredFreets.map((freet) => {
          return [freet, this.likedFreets.includes(String(freet.id))];
      })
    }
  },

  mounted: function() {
    this.loadFreets();
    this.loadUserLikes();
  },

  methods: {
    loadFreets: function() {
      axios.get("/api/freet")
        .then(response => {
          this.freetlist = response.data.freets;
        })
    },

    loadUserLikes: function() {
      axios.get(`/api/user/${this.$cookie.get('fritter-auth')}/likes`)
      .then(response => {
          this.likedFreets = response.data.likes;
      })
    },

    submitAuthor: function() {
      this.author = this.temp;
      this.temp = '';
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
