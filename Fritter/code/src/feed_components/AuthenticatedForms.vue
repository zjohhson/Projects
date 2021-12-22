<template>
  <div>
    <CreateFreetForm/>
  </div>
</template>

<script>

import CreateFreetForm from "./CreateFreetForm.vue";
// import {eventBus} from "../main";


export default {
  name: "AuthenticatedForms",
  components: {CreateFreetForm},
              // ChangeUsernameForm, SignOutForm, ChangePasswordForm, DeleteUserForm},
  props: [], 
  data() {
    return {};
  },
  mounted() {},
  methods: {},
};
</script>

<style></style>
