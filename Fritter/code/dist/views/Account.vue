<template>
  <div>
    <FritterTitle/>
    <UserSettings/>
  </div>
</template>

<script>
import UserSettings from "../components/UserSettings.vue";
import FritterTitle from "../components/FritterTitle.vue";

export default {
  name: "account",
  components: {
    UserSettings,
    FritterTitle
  }
};
</script>
