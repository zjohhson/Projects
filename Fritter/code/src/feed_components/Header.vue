<template>
  <div>
    <h1 class="welcome">Welcome to Fritter!</h1>
    <h2 class="sign-in-message">{{signedInMessage}}</h2>
  </div>
</template>

<script>

export default {
  name: "Header",
  props: ["signedIn", "author"],
  data() {
    return {};
  },
  created: function() {
  },
  computed: {
      signedInMessage: function () {
          if (this.signedIn === false) {
              return "Sign in to start posting freets!"; 
          } else {
              return "Hello " + this.author + "!" ;
          }
      }
  }, 
  methods: {},
};
</script>

<style></style>
