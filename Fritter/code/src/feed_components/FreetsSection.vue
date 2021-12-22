<template>
<div>
  <div class="toggle" v-if="feed">
    <button class="feed_btn" v-on:click.prevent="toggle(true)">Freet Feed</button>
    <button class="search_btn" v-on:click.prevent="toggle(false)">User Search</button>
  </div>

  <div class="toggle" v-else>
    <button class="feed_btn_2" v-on:click.prevent="toggle(true)">Freet Feed</button>
    <button class="search_btn_2" v-on:click.prevent="toggle(false)">User Search</button>
  </div>



   <div class = "scrollbox" v-if="feed">
      <FreetFeed    v-if="feed"
                    v-bind:freet_data=freet_data 
                    v-bind:user=user
                    v-bind:show_type=show
                    />
    </div>

    <div class = "scrollbox" v-else>
      <Profile v-bind:author=author
               v-bind:user=user
                  />
    </div>
  </div>
</template>

<script>
// import axios from "axios";
import {eventBus} from "../main";
 
import FreetFeed from "./FreetFeed.vue"

import Profile from "./Profile.vue"


export default {
  name: "FreetsSection",
  components: { Profile, FreetFeed},
  props: ["freet_data", "user", "all_freets"],
  data() {
    return {error: "", feed: true, 
            author: " ", search: "",
            color: "#6666cc",
            show: true};
  },
  created: function() {
    eventBus.$on("signout-success", ()=>{
        this.feed = true; 
    }),

    eventBus.$on("signin-success", ()=>{
        this.feed = true; 
    })
  },
  methods: {

    toggle(b) {
      this.feed = b;
      eventBus.$emit("feed_toggle");
      eventBus.$emit("freet-data-update"); 
    },
  }
};
</script>

<style></style>
