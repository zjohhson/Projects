<template>
  <div>
    <form id="list-all" v-on:submit.prevent="listFreetsByAuthor">
      <div>
          <input type="text" id="author" name="author" v-model="author" placeholder="Author">
      </div>
      <input
        type="submit"
        value="List Freets By Author"
      />
    </form>
  </div>
</template>

<script>
import {eventBus} from "../main";

export default {
  name: "ViewByAuthorForm",
  props: [],
  data() {
    return {author: "",};
  },
  created: function() {},
  methods: {
    listFreetsByAuthor() {
      eventBus.$emit("findby-author-success", {author: this.author });
      this.author = "";
    },
  },
};
</script>
<style></style>