<template>
  <div class="user-list">
    <h3>{{title}}</h3>
    <User
      v-for="(f, i) in users"
      :key="i"
      :id="f._id"
      :username="f.username"
      :firstName="f.first_name"
      :lastName="f.last_name"
      :points="f.points"
      :badges="f.badges"
      :interests="f.interests"
      :is_admin="f.is_admin"
      :rank="i"
    />
  </div>
</template>

<script>
// import axios from "axios";
// import { eventBus } from "../main";
import User from "./User.vue";

export default {
  name: "UserList",
  props: { users: Array, title: String },
  components: { User },
  data() {
    return {
    };
  },
  created: function () {
  },
  methods: {
  },
};
</script>

<style>
.user-list {
  text-align: center;
  max-width: 800px;
  margin: auto
}
</style>
