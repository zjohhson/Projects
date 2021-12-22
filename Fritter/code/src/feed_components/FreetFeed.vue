<template>
<div>
      <p class="feed_type" v-if="show_type"> Showing: {{feed_type_message}} </p> 
      <FreetItem
        v-for="freet in freet_data"
        v-bind:key="freet.id"
        v-bind:freet="freet"
        v-bind:user="user"
      />
  </div>
</template>

<script>
// import axios from "axios";
import {eventBus} from "../main";
import FreetItem from "./FreetItem.vue"

export default {
  name: "FreetsFeed",
  components: {FreetItem },
  props: ["freet_data", "user", "show_type"],
  data() {
    return {error: "", all_freets: false};
  },
  created: function() {
    this.get_type(); 
    

    eventBus.$on("list-all", () => {this.all_freets = true;});
    eventBus.$on("freet-data-update", () => {
          this.get_type();
      });
    eventBus.$on("signout-success", () => {this.all_freets = true;});
    eventBus.$on("signin-success", () => {this.all_freets = false;});
  },
  computed: {
    feed_type_message: function() {
      if (this.all_freets) {
        return "All Freets"; 
      } else {
        return "Followed Feed";
      }
    }
  },
  methods: {
    get_type: function() {
        let auth = this.user; 
        if (auth != 'undefined' && auth != 'null' && auth) {
            this.all_freets = false; 
        } else {
            this.all_freets = true; 
        }
    }
  }
};
</script>

<style></style>
