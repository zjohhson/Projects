<template>
  <div class="subcontainer">
    <h1>
      Fritter
    </h1>
    <div class="freet-form-container">
      <!-- the submit event will no longer reload the page -->
      <form id='create-freet' v-on:submit.prevent='createFreet' method='post'>

        <input id='freet' v-model.trim='freetBody' type='text' maxlength = "140" name='freet'  placeholder="Enter Freet">

        <input type='submit' value="Post Freet" id="createFreet" class="btn btn-primary">

        <div v-if='success' class="success-message">
          {{ success }}
        </div>

        <div v-if='errors.length' class="error-message">
          <b>Please correct the following error(s):</b>
          <ul>
            <li v-for='error in errors' v-bind:key='error.id'>{{ error }}</li>
          </ul>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";

export default {
  name: "CreateFreetForm",

  data() {
    return {
      errors: [],
      success: "",
      freetBody: "",
      intervalID: null
    };
  },

  methods: {
    createFreet: function() {
      this.errors = [];

      if (!this.freetBody) {
        this.errors.push("Freet must be at least one character.");
        this.clearMessages();
      } else {
        axios.post("/api/freets", {content: this.freetBody, })
          .then((response) => {
            eventBus.$emit("freet-data-update", {
              data: response.data,
              status: response.status
            })
            this.error = ""
          })
          .catch(err => {
            // handle error
            this.errors.push(err.response.data.message);
          })
          .then(() => {
            // always executed
            this.resetForm();
            this.clearMessages();
          });
      }
    },

    resetForm: function() {
      this.freetBody = "";
    },

    clearMessages: function() {
      if (this.intervalID) {
        clearInterval(this.intervalID);
      }
      this.intervalID = setInterval(() => {
        this.errors = [];
        this.success = "";
      }, 5000);
    }
  }
};
</script>
