<template>
  <v-row justify="center">
    <v-card>
      <v-card-title>Edit Your Interests</v-card-title>
      <v-card-text>
        <v-autocomplete
          dense
          label="Select"
          :items="tags"
          v-model="tag"
        ></v-autocomplete>
        <v-btn small @click="add">Add</v-btn>
        &nbsp;
        <v-btn small @click="remove">Delete</v-btn>
      </v-card-text>
    </v-card>
  </v-row>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";

export default {
  name: "InterestForm",
  data() {
    return {
      tag: "",
      tags: [],
    };
  },
  created: function () {
    this.getTags();
  },
  methods: {
    add() {
      axios
        .put("/api/users/tags", {
          tag: this.tag,
        })
        .then(() => {
          eventBus.$emit("refresh-user-list");
          this.tag = "";
        })
        .catch((error) => {
          eventBus.$emit("error-msg", {
            errorMsg: error.response.data.error,
          });
        });
    },
    remove() {
      axios
        .patch("/api/users/tags", {
          tag: this.tag,
        })
        .then(() => {
          eventBus.$emit("refresh-user-list");
          this.tag = "";
        })
        .catch((error) => {
          eventBus.$emit("error-msg", {
            errorMsg: error.response.data.error,
          });
        });
    },
    getTags() {
      // gets the tags for dropdown options
      axios
        .get("api/tags")
        .then((response) => {
          this.tags = response.data;
        })
        .catch((error) => {
          eventBus.$emit("error-msg", {
            errorMsg: error.response.data.error,
          });
        });
    },
  },
  mounted: function () {
    this.getTags();
  },
};
</script>

<style scoped>
.v-card {
  width: 40%;
  min-width: 250px;
  margin-top: 28px;
  margin-bottom: 16px;
  padding: 10px;
  margin: 10px
}
</style>
