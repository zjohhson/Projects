<template>
    <div class="tab">
        <div v-if="selected==='signIn'">
            <router-link to="/account" class = 'tabActive'>
                {{signedInMessage}}<!-- <button class="tablinks" id = 'signIn' v-on:click="switchPage(0)">Sign In/Out</button> -->
            </router-link>
        </div>  
        <div v-else>
            <router-link to="/account" class = 'tabInactive'>
                {{signedInMessage}}<!--<button class="tablinks" id = 'signIn' v-on:click="switchPage(0)">Sign In/Out</button>-->
            </router-link>
        </div>
        <div v-if="selected==='feed'">
            <router-link to="/feed" class = 'tabActive'>
                Feed<!-- <button class="tablinks" id = 'feed'>Feed</button> -->
            </router-link>
        </div>
        <div v-else>
            <router-link to="/feed" class = 'tabInactive'>
                Feed<!-- <button class="tablinks" id = 'feed'>Feed</button> -->
            </router-link>            
        </div>
        <div v-if="selected==='profile'">
        <router-link to="/profile" class = 'tabActive'>
            Profile<!-- <button class="tablinks" id = 'profile'>Profile</button> -->
        </router-link>
        </div>
        <div v-else>
            <router-link to="/profile" class = 'tabInactive'>
                Profile<!-- <button class="tablinks" id = 'profile'>Profile</button> -->
            </router-link>
        </div>
    </div>
</template>



<script>

import {eventBus} from "../main";

export default {
  name: "TabsHeader",
  props: {selected: String},
  data() {
    return {
      activeTab: 'signIn', 
      signedIn: this.$cookies.get('fritter-auth'),
    }
  },
  created: function() {
      eventBus.$on("signin-success", ()=>{
        this.signedIn = true; 
        // alert(d.data);
    })

    eventBus.$on("signout-success", ()=>{
        this.signedIn = false; 
    })
  },
 
  methods: {
    switchPage(index) {
        const pageMap = {0: 'signIn', 1: 'feed', 2: 'profile'}
        var i, tablinks;

        tablinks = document.getElementsByClassName("tablinks");
        for (i = 0; i < tablinks.length; i++) {
            tablinks[i].className = tablinks[i].className.replace(" active", "");
        }
        document.getElementById(pageMap[index]).style.display = 'block';
        tablinks[index].className += " active";
        console.log(tablinks[index])
        // Show the current tab, and add an "active" class to the button that opened the tab
    }, 
  }, 
  computed: {
      signedInMessage() {
        if (this.signedIn && this.signedIn != "undefined") {
            return "Sign Out"
        } else {
            return "Sign-In / Join"
        }
    }, 
  }, 
};
</script>