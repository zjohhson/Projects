<template>
  <div>
    <form id="list-all">
      <input
        type="submit"
        value="List All Freets"
        v-on:click.prevent="listFreets"
      />
    </form>
  </div>
</template>

<script>
import axios from "axios";
import {eventBus} from "../main";

export default {
  name: "ListAllFreetsForm",
  props: [],
  data() {
    return {};
  },
  created: function() {},
  methods: {
    listFreets() {
      axios.get("/api/freets")
      .then(() => {
        eventBus.$emit("list-all");
      })
    },
  },
};
</script>
<style></style>
