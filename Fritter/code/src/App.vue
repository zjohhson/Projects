<template>
  <div id="app">
    <!-- Set name or it defaults to '/' in router.js -->
    <router-view/>
  </div>
</template>

<script>
import axios from "axios";
import { eventBus } from './main' 


export default {
  name: "app",
  beforeCreate: function() {
    // let authenticated = this.$cookie.get('fritter-auth');
    // if (!authenticated) {
    //   this.$router.push("account");
    // }
    // alert("this happened");
    axios.get("/api/user/signedin")
          .then((response) => {   
              if (response.data.username != undefined 
                  && response.data.username != null 
                  && response.data.username != "null") {
                      this.author = response.data.username; 
                      this.signedIn = true; 
                      this.$cookies.set('fritter-auth', response.data.username); 
                      

              } else {
                  this.$cookies.set('fritter-auth', undefined); 
                  eventBus.$emit('signout-creation');
              }
          })

  },
  methods: {
  }
};
</script>

<style scoped>

  #page-container {
    position: relative;
    min-height: 100vh;
  }

  #content-wrap {
    padding-bottom: 2.5rem;    /* Footer height */
  }

  #footer {
    position: absolute;
    bottom: 0;
    width: 100%;
    height: 2.5rem;            /* Footer height */
  }
</style>