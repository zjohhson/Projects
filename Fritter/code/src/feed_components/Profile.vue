<template>
<div>
      <form id="search-user" v-on:submit.prevent="searchUser">
        <input type="text" id="search" name="search"
            v-model="search" placeholder="Search for a user"> 
        <input type="submit" value="Search"/>
      </form> 
        <div v-if="searched">
            <ProfileInfo 
             v-if="found"
             v-bind:author=author /> 
            <FreetFeed  v-if="found"
                        v-bind:freet_data=freet_data 
                        v-bind:user="user"
                        />    
            <h2 class="serror" v-else> {{error}} </h2>
        </div>
  </div>
</template>

<script>
import axios from "axios";
import {eventBus} from "../main";
// import FreetItem from "./FreetItem.vue"
import ProfileInfo from "./ProfileInfo.vue"
import FreetFeed from "./FreetFeed.vue"



export default {
  name: "Profile",
  components: {ProfileInfo, FreetFeed},
  props: ["freet_data", "author", "user"],
  data() {
    return {error: "", feed: true, found: false, searched: false};
  },
  created: function() {
      eventBus.$on("freet-data-update", ()=>{
        this.getAuthorFreets();
    })

  }, 
  methods: {
      getAuthorFreets: function () {
        axios.get("/api/freets/" + this.author)
            .then((response) => {
                this.freet_data = response.data; 
                this.found = true; ``
            })
            .catch(() => {
                this.error = "User with username: " + this.author + " does not exist."; 
            })
      }, 

      searchUser() {
        this.found = false; 
        this.searched = true; 
        this.author = this.search; 
        if (this.author) {
            this.getAuthorFreets();
        } else {
            this.error = "Please enter a username with more than 1 character."
        }
    }, 
  }
};
</script>

<style></style>
