<template>
  <div class="page2">
    <TabsHeader v-bind:selected=selected />
    <Header
        v-bind:signedIn=signedIn 
        v-bind:author=author />
        <FormSection v-bind:signedIn=signedIn />

        <FreetsSection
            v-bind:freet_data=freet_data 
            v-bind:user=author
            v-bind:all_freets=all_freets
                />
  </div>
</template>
 

<script>
import { eventBus } from '../main' 
import FreetsSection from "../feed_components/FreetsSection.vue";
import FormSection from "../feed_components/FormSection.vue";
import Header from "../feed_components/Header.vue";
import TabsHeader from "../components/TabsHeader.vue"
import axios from "axios";



export default {
  name: "app",
  components: {FreetsSection, FormSection, Header, TabsHeader},
  data() {
    return {freet_data: "", signedIn: false, author: "", all_freets: true, selected: 'feed'};
  }, 
  beforeCreate: function() {
      axios.get("/api/user/signedin")
            .then((response) => {   
                if (response.data.username != undefined 
                    && response.data.username != null 
                    && response.data.username != "null"
                    && response.data.username) {
                        this.author = response.data.username; 
                        this.signedIn = true; 
                        this.$cookies.set('fritter-auth', response.data.username); 
                } else {
                    this.$cookies.set('fritter-auth', undefined); 
                }
            })
  }, 
  created: function() {
    //get currently signed in user
    this.author = this.$cookies.get('fritter-auth'); 
    if (this.author != 'undefined' && this.author != 'null' && this.author) {
        this.signedIn = true;  
    }

    //load freets when created, only followed posts if signedin
    if (this.signedIn) {
        axios.get("api/user/freetsByFollowing")
            .then((response) => {
                this.freet_data = response.data; 
            })
    } else {
        axios.get("/api/freets")
            .then((response) => {
                this.freet_data = response.data; 
            })
    }
     

    eventBus.$on("signin-success", (d)=>{
        this.signedIn = true; 
        this.author = d.data; 
        this.$cookies.set('fritter-auth', d.data);
        // alert(d.data);
    })

    eventBus.$on("signout-success", ()=>{
        this.signedIn = false; 
        this.author = undefined; 
        this.$cookies.set('fritter-auth', undefined);
    })

    eventBus.$on("delete-success", ()=>{
        eventBus.$emit("signout-success"); 
        // eventBus.$emit("freet-data-update"); 
    })

    eventBus.$on("list-all", () => {
        axios.get("/api/freets")
                .then((response) => {
                    this.freet_data = response.data; 
                })
                .catch(() => {
                    alert("list all error"); 
                })
    });


    eventBus.$on("freet-data-update", ()=>{
        if (this.signedIn) {
            this.all_freets = false; 
            axios.get("api/user/freetsByFollowing")
                .then((response) => {
                    this.freet_data = response.data; 
                })
                .catch(() => {
                    alert("freet data update error"); 
                })
        } else {
            this.all_freets = true; 
            axios.get("/api/freets")
                .then((response) => {
                    this.freet_data = response.data; 
                })
                // .catch((error) => {
                //     alert("freet data update error 2"); 
                // })
        }
    })

    eventBus.$on("findby-author-success", (data)=>{
         axios.get("/api/freets/"+data.author)
            .then((response) => {
                this.freet_data = response.data; 
            })
            .catch((error) => {
                alert(error); 
            })
    })

    eventBus.$on("create-success", (response) => {
        this.author = response.data.username;
        this.$cookies.set('fritter-auth',  response.data.username);
    });
  },
  computed: {},
  methods: {},
};
</script>

<style></style>
