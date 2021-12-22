<template>
  <div>
    <AuthenticatedForms v-if="signedIn"/>
    <ListAllFreetsForm v-if="signedIn"/>
  </div>
</template>

<script>

import ListAllFreetsForm from "./ListAllFreetsForm.vue";
import AuthenticatedForms from "./AuthenticatedForms.vue"; 

export default {
  name: "FormSection",
  components: {ListAllFreetsForm, AuthenticatedForms},
  props: ["signedIn"], 
  data() {
    return {};
  },
  created: function() {
    // this.signedIn = 
  }, 
  mounted() {},
  methods: {},
};
</script>

<style></style>
