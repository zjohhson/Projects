<template>
  <div class="nav-bar">
    <div>
        <span v-if="userName">
          <router-link to="/account">
            <h2>Hi @{{userName}}!</h2>
          </router-link>
          <router-link to="/profile">
            <h2>Go to profile</h2>
          </router-link>
        </span>
        <span v-else>
          <router-link to="/account">
            <h2>Sign In/Sign Up</h2>
          </router-link>
        </span>
    </div>
  </div>
</template>

<script>
export default {
  name: "NavBar",
  data() {
    return {
      userName: this.$cookie.get('fritter-auth')
    }
  },
};
</script>

<style scoped>
.nav-icon-container {
    width: 20px;
    height: 20px;
    display: inline-block;
}

.nav-icon-container img {
  width: inherit;
  height: inherit;
}
</style>