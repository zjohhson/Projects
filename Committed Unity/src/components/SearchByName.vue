<template>
  <v-list-item>
    <v-list-item-content>
      <v-text-field
        v-model="searchName"
        v-on:keyup.enter="onEnter"
        hide-details
        append-icon="mdi-close"
        @click:append="refresh"
        label="Search by name"
      ></v-text-field>
    </v-list-item-content>
  </v-list-item>
</template>

<script>
import { eventBus } from "../main";

export default {
  name: "SearchByName",
  data() {
    return {
      searchName: "",
    };
  },
  methods: {
    onEnter() {
      eventBus.$emit("search-by-name", { searchName: this.searchName });
    },
    refresh() {
      this.searchName = "";
      eventBus.$emit("refresh");
    },
  },
};
</script>

<style>
.v-text-field label {
  font-size: 14px;
  line-height: 14px;
}

.v-text-field input {
  font-size: 14px;
  line-height: 14px;
}
</style>
