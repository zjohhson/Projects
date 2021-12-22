<template>
  <div class="freet">
    <div class="tags"> 
       <p class="refreet_message"> {{refreet_message(freet.id, freet.refreeted)}} </p>
       <p class="edited"> {{isEdited(edited)}} </p>
       <p class="freetId"> {{id}} </p>
    </div>
    <p class="author">@{{author}}:</p> 
    <p class="content"> {{freet.content}}</p>  
      <input class="edit" type="text" name="freet" placeholder="Freet" v-model="updatedFreet">
      <button class="update" v-on:click="updateFreet(freet.id)">Update</button>
      <button class="delete" v-on:click="deleteFreet(freet.id)">Delete</button>
      <button class="upvote" v-on:click="upvoteFreet(freet.id)">{{upvoteMessage}}</button>
      <button class="retweet" v-on:click="refreet(freet.id)">Refreet</button>
    <p class="error">{{error}} </p> 
  </div>  
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";

export default {
  name: "FreetItem",
  props: ["freet", "user"],
  data() {
    return {
      updatedFreet: "",
      error: "", 
      edited: this.freet.edited, 
      author: this.freet.author, 
      id: this.freet.id, 
      count: this.freet.upvotes.length, 
      liked: this.user != null && this.freet.upvotes.includes(this.user),
    };
  },
  created: function () {
    eventBus.$on("signout-success", () => {
      this.liked = false; 
      this.error = "";
    }),

    eventBus.$on("signin-success", (d) => {
      this.liked = this.freet.upvotes.includes(d.data);  
      this.error = "";
    }), 

    eventBus.$on("username-change", (d) => {
      if (this.author == d.data) {
        this.author = this.$cookies.get('fritter-auth');
      }
      this.error = "";
    }), 

    eventBus.$on("feed_toggle", () => {
      this.liked = this.freet.upvotes.includes(this.user);  
    }), 


    eventBus.$on("freet-upvote", () => {

       axios.get("/api/freets/id/"+this.freet.refreeted)
            .then((response) => {
                if (response.data) {
                this.liked = response.data.upvotes.includes(this.user); 
                this.count = response.data.upvotes.length;
                this.error = "";    
                } 
                
            })  
            .catch((error) => {
                alert(error); 
            })
    })
  }, 

  methods: {
    isEdited: function (edited) {
      if (edited) {
        return "(Edited) "; 
      } else {
        return ""; 
      }
    }, 
    refreet_message: function (id, refreet) {
      if (id === refreet) {
        return ""; 
      } else {
        axios.get("/api/freets/id/"+refreet)
            .then((response) => {
                this.edited = response.data.edited; 
                this.author = response.data.author; 
                this.id = id+ ' (original ID: ' + refreet + ')';
            })
            .catch((error) => {
                alert(error); 
            }); 
        return "Refreeted by: @" + this.freet.author; 
      }
    }, 
    updateFreet(f_id) {
      if(!this.updatedFreet) {
        this.error = "Freet must be at least one character.";
      } else if (this.freet.refreeted !== this.freet.id) {
        this.error = "Cannot edit a refreet."
      } else {
        axios.put("/api/freets/" + f_id, {content: this.updatedFreet, id: f_id})
          .then((response) => {
            this.edited = true;
            eventBus.$emit("freet-data-update", {
              data: response.data,
              status: response.status
            })
            this.error = "";
          })
          .catch((error) => {
            this.error = error.response.data.error;
          })
          this.updatedFreet = ""
      }
    },
    deleteFreet(f_id) {
      axios.delete("/api/freets/" + f_id, {id: f_id})
      .then((response) => {
        eventBus.$emit("freet-data-update", {
          data: response.data,
          status: response.status
        })
        this.error = ""
      })
      .catch((error) => {
        this.error = error.response.data.error;
      })
    },
    upvoteFreet(f_id) {
      axios.patch("/api/user/toggleUpvote/" + f_id, {id: f_id})
      .then((response) => {
        this.freet = response.data;
        eventBus.$emit("freet-upvote"); 
        eventBus.$emit("freet-data-update"); 
        this.liked = this.user != null && this.freet.upvotes.includes(this.user);
        // alert(this.freet.upvotes.includes(this.$cookies.get("auth-username")));
        // eventBus.$emit("freet-data-update", {
        //   data: response.data,
        //   status: response.status
        // })
        this.error = ""
      })
      .catch((error) => {
        this.error = error.response.data.error;
      })
    },
    refreet(f_id) {
      axios.patch("/api/user/refreet/" + f_id, {id: f_id})
      .then((response) => {
        eventBus.$emit("freet-data-update", {
          // TODO buggy?
          data: response.data,
          status: response.status
        })
        this.error = ""
      })
      .catch((error) => {
        this.error = error.response.data.error;
      })
    }
  },
  computed: {
    upvoteMessage: function() {
      return (this.liked ? `♥` : `♡`) + " " + this.count; 
    }, 
  }, 
};
</script>