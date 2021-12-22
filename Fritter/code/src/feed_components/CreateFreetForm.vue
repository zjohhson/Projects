<template>
  <div>
    <form id="create-freet" v-on:submit.prevent="createFreet">
        <input type="text" id="freet" name="freet" v-model="freet" placeholder="Freet"> 
        <br/> 
        <input type="submit" value="Create Freet"/>
        <p class="error"> {{error}}</p>
    </form>
  </div>
</template>

<script>
import axios from "axios";
import {eventBus} from "../main";

export default {
  name: "CreateFreetsForm",
  props: [],
  data() {
    return {
        freet: "",
        error: ""
    };
  },
  created: function() {
  },
  methods: {
      createFreet() {

      if (!this.freet) {
        this.error = "Freet must be at least one character.";
      } else {
        axios.post("/api/freets", {content: this.freet, })
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
          this.freet = ""
      }
    }
  },
};
</script>

<style></style>
