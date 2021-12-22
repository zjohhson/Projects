<template>
  <div class="profile_page"> 
      <h1> User Profile </h1>
      <div class= "follows">
          <p> @{{author}} </p>
          <button  v-on:click.prevent="follow" v-if="other_user"> 
              {{btn_message}}
          </button>
      </div> 
  </div>
</template>

<script>
import axios from "axios";
import {eventBus} from "../main";

export default {
  name: "ProfileInfo",
  components: { },
  props: ["author"],
  data() {
    return {error: "", follows: false, other_user: true, };
  },
  created: function() {
        if(this.author == this.$cookies.get('fritter-auth')) {
            this.other_user = false;
        } 
        
        if(this.$cookies.get('fritter-auth') == "undefined") {
            this.other_user = false;
        } 
  },
  mounted: function () {
      axios.get("/api/user/following")
        .then((response) => {
            if(response.data.includes(this.author)) {
                this.follows = true;    
            } else {
                this.follows = false; 
            }
        })
  }, 
  computed: {
      btn_message: function () { 
          if (this.follows) {
              return "Unfollow"; 
          } else {
              return "Follow"; 
          }
      }
  },    
  methods: {
      follow: function () { 
          this.follows = !this.follows; 
          axios.patch('/api/user/toggleFollow/' + this.author); 
          eventBus.$emit("freet-data-update"); 
            // .then(() => {
            //     alert("followed");
            // })
            // .catch(() => {
            //     alert("failed following");
            // })
      },
  }
};
</script>

<style></style>
