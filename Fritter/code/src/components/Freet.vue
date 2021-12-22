<template>
  <!-- TODO: use 'short-container-popular' : 'short-container' CSS classes to delineate popular shorts -->
  <div :class="'freet-container'">
    <div v-if = "freet.refreeter !== null"> 
      <mark v-if = "following.includes(freet.refreeter)">@{{ freet.refreeter }}: {{freet.refreetContent}}</mark> 
      <p v-else>@{{ freet.refreeter }}: {{freet.refreetContent}}</p>
      <i v-if = "freet.refreeter !== null"> Refreeted: </i>
      <input class="edit" v-model.trim='refreetEditBody' v-if = "freet.refreeter===userName" type="text" maxlength = "140" name="refreetEditBody" placeholder="Edit Refreet message">
      <button v-if = "freet.refreeter===userName" v-on:click="updateRefreet">Update</button>
      <button v-if = "freet.refreeter===userName" v-on:click="deleteRefreet">Delete</button>
    </div>
    <div> 
      <mark v-if = "following.includes(freet.username)">@{{ freet.username }}: {{freet.content}}</mark>
      <p v-else>@{{ freet.username }}: {{freet.content}}</p>
    </div>
    <div class="freet-actions">
      <input class="edit" v-model.trim='freetBody' v-if = "freet.username===userName && freet.refreeter === null" type="text" maxlength = "140" name="freetBody" placeholder="Edit Freet">
      <button v-if = "freet.username===userName && freet.refreeter === null" v-on:click="updateFreet">Update</button>
      <button v-if = "freet.username===userName && freet.refreeter === null" v-on:click="deleteFreet">Delete</button>
      <input class="refreet" v-model.trim='refreetBody' v-if = "userName.length > 0 && freet.username !== userName" type="text" maxlength = "140" name="refreetBody" placeholder="Add Message">
      <button v-if = "userName.length > 0 && freet.username !== userName" v-on:click="refreet">Refreet</button>
      <button v-if = "liked" v-on:click="unlikeFreet">Unlike</button>
      <button v-else v-on:click="likeFreet">Like</button>
      <br>
      <p>{{freet.count}} likes</p>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";

export default {
  name: "Freet",

  props: {
    freet: Object,
    liked: Boolean 
  },

  data() {
    return {
      freetBody: "",
      refreetBody: "",
      refreetEditBody: "",
      userName: this.$cookie.get('fritter-auth'),
      following: []
    };
  },

  mounted: function() {
    this.loadFollowing();
  },

  methods: {
    updateFreet: function() {
      const body = {content: this.freetBody, id: this.freet.id};
      axios
        .put(`/api/freet/${this.freet.id}`, body)
        .then(() => {
          // handle success
          eventBus.$emit("update-freet-success", this.freet);
        })
        .catch(() => {
          // handle error
          eventBus.$emit("update-freet-error", this.freet);
        })
        .then(() => (this.freetBody = ""));
    },

    updateRefreet: function() {
      const body = {content: this.refreetEditBody, id: this.freet.id};
      console.log(this.freetlist)
      axios
        .put(`/api/freet/refreet/${this.freet.id}`, body)
        .then(() => {
          // handle success
          eventBus.$emit("update-refreet-success", this.freet);
        })
        .catch(() => {
          // handle error
          eventBus.$emit("update-refreet-error", this.freet);
        })
        .then(() => (this.refreetEditBody = ""));
    },

    deleteFreet: function() {
      axios
        .delete(`/api/freet/${this.freet.id}`, {})
        .then(() => {
          eventBus.$emit("delete-freet-success", this.freet);
        })
        .catch(() => {
          eventBus.$emit("delete-freet-error", this.freet);
        });
    },

    deleteRefreet: function() {
      axios
        .delete(`/api/freet/refreet/${this.freet.id}`, {})
        .then(() => {
          eventBus.$emit("delete-refreet-success", this.freet);
        })
        .catch(() => {
          eventBus.$emit("delete-refreet-error", this.freet);
        });
    },

    likeFreet: function() {
        axios.patch(`api/user/${this.userName}/like/${this.freet.id}`, {})
        .then(() => {
          axios.patch(`api/freet/increment/${this.freet.id}`, {})
          .then(() => {eventBus.$emit("like-freet-success", this.freet);}) // wrap in a .then()
        })
        .catch(() => {
          // handle error
          eventBus.$emit("like-freet-error", this.freet);
        });
    },

    unlikeFreet: function() {
        axios.patch(`api/user/${this.userName}/unlike/${this.freet.id}`, {})
        .then(() => {
          axios.patch(`api/freet/decrement/${this.freet.id}`, {})
          .then(() => {eventBus.$emit("unlike-freet-success", this.freet);})
        })
        .catch(() => {
          // handle error
          eventBus.$emit("unlike-freet-error", this.freet);
        });
    },
    
    refreet: function() {
      const body = {content: this.refreetBody, id: this.freet.id};
      axios
        .post(`/api/freet/refreet/${this.freet.id}`, body)
        .then(() => {
          // handle success
          eventBus.$emit("refreet-success", this.freet);
        })
        .catch(() => {
          // handle error
          eventBus.$emit("refreet-error", this.freet);
        })
        .then(() => (this.content = ""));
    },

    loadFollowing: function() {
      axios.get(`/api/user/${this.userName}/following`)
      .then(response => {
          this.following = response.data.following;
      })
    },
  },
  created: function() {
    eventBus.$on("follow-user-success", res => {
      this.success = `Followed @${res.username}.`;
      this.loadFollowing();
    });

    eventBus.$on("unfollow-user-success", res => {
      this.success = `Unfollowed @${res.username}.`;
      this.loadFollowing();
    });

    eventBus.$on("follow-user-error", res => {
      this.error = `Error following @${res.username}.`;
      this.loadFollowing();
    });

    eventBus.$on("unfollow-user-error", res => {
      this.error = `Error unfollowing @${res.username}.`;
      this.loadFollowing();
    });
  }
};
</script>