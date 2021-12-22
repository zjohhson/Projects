<template>
  <div id="user-settings">
    <div v-if="isSignedIn" class="bigButtonContainer">
      <SignOut/>
    </div>
    <div v-else class="form-container">
      <SignIn/>
      <br>
      <SignUp/>
    </div>
    <div v-if='messages.length' class="success-message" style="text-align:center;">
      <div v-for='message in messages' v-bind:key='message.id'><b>{{ message }}</b></div>
    </div>
  </div>
</template>

<script>
import SignIn from "./SignIn.vue";
import SignOut from "./SignOut.vue";
import SignUp from "./SignUp.vue";
import DeleteUser from "./DeleteUser.vue"
import ChangeUsername from "./ChangeUsername.vue";
import ChangePassword from "./ChangePassword.vue";
import { eventBus } from "../main";

export default {
  name: "UserSettings",

  components: {
    SignIn,
    SignOut,
    SignUp,
    ChangeUsername,
    ChangePassword,
    DeleteUser
  },

  data() {
    return {
      isSignedIn: false,
      messages: []
    };
  },

  created: function() {
    let authenticated = this.$cookies.get('fritter-auth');
    if (authenticated && authenticated != "undefined") {
      this.isSignedIn = true;
    }

    eventBus.$on("signin-success", (d) => {
      this.$cookies.set('fritter-auth', d.data);
      this.isSignedIn = true;
      this.messages.push("You have been signed in!");
      this.clearMessages();

    });

    
    eventBus.$on("signout-success", () => {
      this.$cookies.set('fritter-auth', '');
      this.isSignedIn = false;
      this.messages.push("You have been signed out!");
      this.clearMessages();
    });

    eventBus.$on("signout-creation", () => {
      this.$cookies.set('fritter-auth', '');
      this.isSignedIn = false;
      this.clearMessages();
    });

    eventBus.$on("change-username-success", (res) => {
      this.$cookies.set('fritter-auth', res.newUsername);
      this.isSignedIn = true;
      this.messages.push("You have successfully changed your username!");
      this.clearMessages();

    });

    eventBus.$on("change-password-success", () => {
      this.isSignedIn = true;
      this.messages.push("You have successfully changed your password!");
      this.clearMessages();

    });

    eventBus.$on("delete-user-success", () => {
      this.$cookies.set('fritter-auth', null);
      this.isSignedIn = false;
      this.messages.push("Your account has been deleted!");
      this.clearMessages();
    });

  },
  methods: {
    resetForm: function() {
      this.username = ""
      this.password = ""
    },

    clearMessages: function() {
      setInterval(() => {
        this.messages = [];
      }, 5000);
    }
  }
};
</script>