<template>
  <div class="page">
      <TabsHeader v-bind:selected=selected />
      <br>
    <div class='profile' v-if='signedIn'>
          <h1 class="profile_user">@{{signedInUser}}</h1>
        <div class = 'profileName'>
          <ChangeUsername class="change_form" />
          <ChangePassword class="change_form" />
        </div>
        <div class="followLists"> 
          <FollowersList class="followlist" />
          <FollowingList class="followlist" />
        </div>
        <h2 class="your_freets" v-if='freet_data.length > 0'> Your freets: </h2>
        <router-link to="/feed" v-else>
          <h2 class="your_freets"> You have no posted Freets. Create one now!</h2>
        </router-link>
        <FreetFeed 
                        v-bind:freet_data=freet_data 
                        v-bind:user="signedInUser"
                        class = "profile_feed"
                        />    
        <DeleteUser/>
    </div>
    <div v-else> 
      <h2 class="profile-signin"> Sign in or create an account to look at your profile! </h2> 
    </div>
  </div>
</template>

<script>
import FritterTitle from "../components/FritterTitle.vue";
import FollowersList from "../components/FollowersList.vue";
import FollowingList from "../components/FollowingList.vue";
import TabsHeader from "../components/TabsHeader.vue";
import ChangeUsername from '../components/ChangeUsername.vue';
import ChangePassword from '../components/ChangePassword.vue';
import DeleteUser from '../components/DeleteUser.vue';
import FreetFeed from '../feed_components/FreetFeed.vue';

import {eventBus} from '../main.js';
import axios from "axios";


export default {
  name: "Profile",
  components: {
    FritterTitle,
    FollowersList,
    FollowingList,
    TabsHeader,
    ChangeUsername,
    ChangePassword,
    DeleteUser, 
    FreetFeed
  },
  created: function() {



    this.signedInUser = this.$cookies.get('fritter-auth'); 
    this.signedIn = this.$cookies.get('fritter-auth') && this.$cookies.get('fritter-auth') != "undefined"; 

    if (this.signedIn) {
      this.getFreetData(); 
    }

    eventBus.$on("freet-data-update", ()=>{this.getFreetData()});

    

    eventBus.$on("signin-success", (d)=>{
        this.signedIn = true; 
        this.signedInUser = d.data; 
        this.$cookies.set('fritter-auth', d.data);
        this.getFreetData(); 
    })

    eventBus.$on("signout-success", ()=>{
        this.signedIn = false; 
        this.signedInUser = false; 
        this.$cookies.set('fritter-auth', undefined);
    })

  }, 
  data() {
    return {
      signedInUser: this.$cookies.get('fritter-auth'),
      signedIn: this.$cookies.get('fritter-auth') && this.$cookies.get('fritter-auth') != "undefined", 
      selected: 'profile',
      freet_data: [], 
    }
  }, 
  methods: {
    getFreetData: function() {
        axios.get("/api/freets/" + this.signedInUser)
            .then((response) => {
                this.freet_data = response.data; 
                this.found = true;
            })
    }, 

  }, 

};
</script>