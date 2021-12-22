<template>
  <v-treeview dense :items="items" open-on-click>
    <template v-slot:prepend="{ item }">
      <div v-if="!item.children">
        <!-- If they are not an admin and have a badge (badge[0] !== None and it is truthy) -->
        <v-badge
          left
          v-if="
            !item.isAdmin &&
            item.name !== 'None' &&
            item.badges[0] !== 'None' &&
            item.badges[0]
          "
          icon="mdi-trophy"
          bordered
          overlap
          :color="badgeColors[item.badges[0]]"
        >
          <v-avatar color="blue darken-1" size="36" class="white--text text-h7">
            {{ initials(item.firstName, item.lastName) }}
          </v-avatar>
        </v-badge>
        <!-- If they are not an admin and don't have a badge -->
        <v-avatar
          color="blue darken-1"
          size="36"
          class="white--text text-h7"
          v-if="
            !item.isAdmin &&
            item.name !== 'None' &&
            (!item.badges[0] || item.badges[0] === 'None')
          "
        >
          {{ initials(item.firstName, item.lastName) }}
        </v-avatar>
        <!-- If they are an admin and don't have a badge 
        (i don't think this should ever run because admin can't RSVP) -->
        <v-badge
          left
          v-if="item.isAdmin"
          icon="mdi-wrench"
          bordered
          overlap
          color="grey darken-1"
        >
          <v-avatar color="blue darken-1" size="36" class="white--text text-h7">
            {{ initials(item.firstName, item.lastName) }}
          </v-avatar>
        </v-badge>
      </div>
      <!-- <v-avatar
        v-if="item.is_admin && !item.children"
        color="secondary"
        size="36"
        class="white--text text-h7"
        >{{ initials(item.firstName, item.lastName) }}
      </v-avatar> -->
    </template>
  </v-treeview>
</template>

<script>
import axios from "axios";

export default {
  name: "MeetingStatusPanel",
  components: {},
  props: {
    going: Array,
    maybe: Array,
    notGoing: Array,
    attendees: Array,
    host_id: String,
  },
  data() {
    return {
      loggedInAccount: this.$cookie.get("auth-username"),
      user: "",
      badgeColors: { Gold: "#FFD700", Silver: "#C0C0C0", Bronze: "#CD7F32" },
    };
  },
  computed: {
    items: function () {
      const none = [
        {
          id: 5,
          name: "None",
          isAdmin: false,
          badges: [],
          firstName: "",
          lastName: "",
        },
      ];
      let tree = [
        {
          id: 1,
          name: "Going",
          children:
            this.going && this.going.length !== 0
              ? this.going.map((u) => this.convertUserToTreeStruct(u))
              : none,
        },
        {
          id: 2,
          name: "Maybe",
          children:
            this.maybe && this.maybe.length !== 0
              ? this.maybe.map((u) => this.convertUserToTreeStruct(u))
              : none,
        },
        {
          id: 3,
          name: "Not Going",
          children:
            this.notGoing && this.notGoing.length !== 0
              ? this.notGoing.map((u) => this.convertUserToTreeStruct(u))
              : none,
        },
      ];
      if (this.user._id === this.host_id) {
        tree.push({
          id: 4,
          name: "Attendees",
          children:
            this.attendees && this.attendees.length !== 0
              ? this.attendees.map((u) => this.convertUserToTreeStruct(u))
              : none,
        });
      }
      return tree;
    },
  },
  methods: {
    getUser() {
      axios
        .get(`/api/users/${this.loggedInAccount}`)
        .then((response) => {
          this.user = response.data;
        })
        .catch(() => {
          this.user = "";
        });
    },
    convertUserToTreeStruct(user) {
      return {
        name: "@" + user.username,
        isAdmin: user.is_admin,
        badges: user.badges,
        firstName: user.first_name,
        lastName: user.last_name,
      };
    },
    initials: function (firstName, lastName) {
      if (firstName && lastName) {
        return firstName[0].toUpperCase() + lastName[0].toUpperCase();
      }
      return "";
    },
  },
  mounted: function () {
    this.getUser();
  },
};
</script>

<style>
.v-treeview-node__root .v-treeview-node__prepend {
  min-width: 0;
}
</style>
