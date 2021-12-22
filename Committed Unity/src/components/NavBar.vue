<template>
  <v-app-bar
    app
    color="#96C5F7"
    dark
    shrink-on-scroll
    :src="require('@/assets/harvardsq.jpeg')"
    fade-img-on-scroll
  >
    <template v-slot:img="{ props }">
      <v-img
        v-bind="props"
        gradient="to top right, rgba(8,59,114,.7), rgba(150,197,247,.7)"
      ></v-img>
    </template>
    <v-app-bar-title
      @click.native="goHome"
      style="font-weight: bold; font-size=20px;"
    >
      <div>CommittedUnity</div>
    </v-app-bar-title>
    <v-spacer></v-spacer>
    <v-menu
      bottom
      origin="center center"
      transition="scale-transition"
      offset-y
    >
      <template v-slot:activator="{ on, attrs }">
        <v-btn dark icon v-bind="attrs" v-on="on">
          <v-icon>mdi-dots-vertical</v-icon>
        </v-btn>
      </template>

      <v-card>
        <v-card-text v-if="username">
          You are signed in as @{{ username }} <br />
          <SignOut />
        </v-card-text>
        <v-card-text v-else> Please Sign In </v-card-text>
      </v-card>
    </v-menu>

    <template v-slot:extension>
      <v-tabs v-if="view === 'main'">
        <v-spacer></v-spacer>
        <v-tab @click="goHome">All Meetings</v-tab>
        <v-tab @click="goInterested">My Meetings</v-tab>
        <v-tab @click="goRecommended" v-if="!isAdmin">Discover</v-tab>
        <v-spacer></v-spacer>
        <v-spacer></v-spacer>
        <v-tab @click="goLeaderboard">Leaderboard</v-tab>
        <v-tab @click="goProfile">Profile</v-tab>
        <v-spacer></v-spacer>
      </v-tabs>
      <v-tabs align-with-title v-if="view === 'sign-in'">
        <v-tab>Sign In</v-tab>
      </v-tabs>
    </template>
  </v-app-bar>
</template>

<script>
import SignOut from "./SignOut.vue";
import { eventBus } from "../main";

export default {
  name: "NavBar",
  components: {
    SignOut,
  },
  props: {
    username: String,
    isAdmin: Boolean,
    view: String,
  },
  data() {
    return {};
  },
  methods: {
    goHome: function () {
      eventBus.$emit("change-main-tab", { tab: "all-meetings" });
      eventBus.$emit("change-to-home-page");
    },
    goInterested: function () {
      eventBus.$emit("change-main-tab", { tab: "my-meetings" });
    },
    goRecommended: function () {
      eventBus.$emit("change-main-tab", { tab: "discover-meetings" });
    },
    goAttend: function () {
      eventBus.$emit("change-main-tab", { tab: "attend" });
    },
    goProfile: function () {
      eventBus.$emit("change-main-tab", { tab: "profile" });
    },
    goLeaderboard: function () {
      eventBus.$emit("change-main-tab", { tab: "leaderboard" });
    },
  },
};
</script>

<style scoped>
.v-app-bar-title:hover {
  cursor: pointer;
}
</style>
