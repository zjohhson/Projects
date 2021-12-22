<template>
  <v-list-item>
    <v-list-item-content>
      <v-menu
        v-model="dateMenu"
        :close-on-content-click="false"
        :nudge-right="40"
        transition="scale-transition"
        offset-y
        min-width="auto"
      >
        <template v-slot:activator="{ on, attrs }">
          <v-text-field
            v-model="date"
            label="Filter by date"
            color="blue darken-1"
            append-icon="mdi-close"
            @click:append="refresh"
            v-bind="attrs"
            v-on="on"
          ></v-text-field>
        </template>
        <v-date-picker
          v-model="date"
          v-on:change="onChange"
          @input="dateMenu = false"
          :show-current="true"
        ></v-date-picker>
      </v-menu>
    </v-list-item-content>
  </v-list-item>
</template>

<script>
import { eventBus } from "../main";

export default {
  name: "SearchByDate",
  data() {
    return {
      date: "",
      dateMenu: false,
    };
  },
  methods: {
    onChange() {
      if (this.date) {
        eventBus.$emit("search-by-date", { date: this.date });
      }
    },
    refresh() {
      this.date = "";
      eventBus.$emit("refresh");
    },
  },
};
</script>
