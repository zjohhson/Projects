<template>
  <div class="page">
    <TabsHeader v-bind:selected=selected />
    <UserSettings/>
  </div>
</template>

<script>
import UserSettings from "../components/UserSettings.vue";
import TabsHeader from "../components/TabsHeader.vue";
// import axios from "axios";


export default {
  name: "account",
  components: {
    TabsHeader,
    UserSettings
  },
  beforeCreate: function() {
  }, 

  data() {
    return {
      selected: 'signIn'
    }
  }
};
</script>