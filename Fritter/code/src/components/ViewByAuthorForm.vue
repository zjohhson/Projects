<template>
    <div>
        <p> View Freets by Author </p>
        <input class="edit" v-model.trim='author' type="text" name="author" placeholder="Enter Author">
        <button class="btn btn-primary" v-on:click='submitAuthor'>View by Author</button>
    </div>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";

export default {
  name: "ViewByAuthorForm",

  props: {
  },

  data() {
    return {
      author: ""
    };
  },

  computed: {
  },

  methods: {
    submitAuthor: function() {
      const body = {username: this.author};
      axios
        .get(`/api/freet/byauthor/${this.author}`, body)
        .then(() => {
          // handle success
          eventBus.$emit("submit-author-success", this.author);
        })
        .catch(() => {
          // handle error
          eventBus.$emit("submit-author-error", this.author);
        })
        .then(() => (this.author = ""));
    }
  }
};
</script>