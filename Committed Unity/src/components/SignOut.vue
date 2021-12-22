<template>
  <v-btn @click="signOut">Sign Out</v-btn>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";

export default {
  name: "SignOut",
  props: [],
  data() {
    return {};
  },
  created: function () {},
  methods: {
    signOut() {
      axios
        .delete("/api/users/session")
        .then(() => {
          eventBus.$emit("signed-out");
        })
        .catch((error) => {
          eventBus.$emit("error-msg", {
            errorMsg: error.response.data.error,
          });
        });
    },
  },
};
</script>

<style></style>
