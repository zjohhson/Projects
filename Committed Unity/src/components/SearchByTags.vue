<template>
  <v-container fluid>
    <b>Filter By Tags:</b>
    <v-checkbox
      class="tag-checkbox"
      v-for="(t, i) in tags"
      :key="i"
      v-model="selectedTags"
      :label="t"
      :value="t"
      dense
      v-on:change="search"
    ></v-checkbox>
  </v-container>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";

export default {
  name: "SearchByTags",
  data() {
    return {
      tag: "",
      tags: [],
      selectedTags: [],
    };
  },
  created: function () {
    this.getTags();
    eventBus.$on("refresh", () => {
      this.selectedTags = [];
    });
  },
  methods: {
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
    search() {
      eventBus.$emit("search-by-tags", { tags: this.selectedTags });
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
}
::v-deep .tag-checkbox .v-label {
  font-size: 14px;
}
</style>
