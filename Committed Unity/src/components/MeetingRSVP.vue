<template>
  <v-chip-group
    v-if="!user.is_admin"
    v-model="selection"
    active-class="blue lighten-1 white--text"
    mandatory
    style="justify-content: center"
  >
    <div class="flex-row" style="justify-content: center; gap: 6px">
      <v-chip @click="rsvpGoing" style="margin-right: 0">Going</v-chip>
      <v-chip @click="rsvpMaybe" style="margin-right: 0">Maybe</v-chip>
      <v-chip @click="rsvpNotGoing" style="margin-right: 0">Not Going</v-chip>
    </div>
  </v-chip-group>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";
export default {
  name: "Meeting",
  props: ["meetingID", "user", "going", "maybe", "notGoing"],
  data() {
    return {
      selection: this.intialSelection(),
    };
  },
  methods: {
    intialSelection: function () {
      const goingUsernames = this.going
        ? this.going.map((u) => u.username)
        : [];
      const maybeUsernames = this.maybe
        ? this.maybe.map((u) => u.username)
        : [];
      const notGoingUsernames = this.notGoing
        ? this.notGoing.map((u) => u.username)
        : [];
      const username = this.$cookie.get("auth-username");
      if (goingUsernames.includes(username)) {
        return 0;
      } else if (maybeUsernames.includes(username)) {
        return 1;
      } else if (notGoingUsernames.includes(username)) {
        return 2;
      }
      this.rsvpNotGoing();
      return 2; // default to not going (mandatory)
    },
    rsvpGoing: function () {
      axios
        .patch(`/api/meetings/${this.meetingID}/rsvp`, {
          goingId: this.user._id,
        })
        .then(() => {
          eventBus.$emit("changed-rsvp");
        })
        .catch((error) => {
          eventBus.$emit("error-msg", {
            errorMsg: error.response.data.error,
          });
        });
    },
    rsvpMaybe: function () {
      axios
        .patch(`/api/meetings/${this.meetingID}/rsvp`, {
          maybeId: this.user._id,
        })
        .then(() => {
          eventBus.$emit("changed-rsvp");
        })
        .catch((error) => {
          eventBus.$emit("error-msg", {
            errorMsg: error.response.data.error,
          });
        });
    },
    rsvpNotGoing: function () {
      axios
        .patch(`/api/meetings/${this.meetingID}/rsvp`, {
          notGoingId: this.user._id,
        })
        .then(() => {
          eventBus.$emit("changed-rsvp");
        })
        .catch((error) => {
          eventBus.$emit("error-msg", {
            errorMsg: error.response.data.error,
          });
        });
    },
  },
};
</script>

<style>
.v-slide-group__content {
  justify-content: center;
}
/* ::v-deep .v-slide-group__content {
  justify-content: center;
} */
</style>
