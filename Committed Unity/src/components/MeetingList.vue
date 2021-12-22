<template>
  <div class="meeting-list">
    <p v-if="meetings.length === 0">
      {{ emptyMsg }}
    </p>
    <Meeting
      v-for="(f, i) in meetings"
      :key="i"
      :id="f._id"
      :name="f.name"
      :start_date_time="f.start_date_time"
      :end_date_time="f.end_date_time"
      :location="f.location"
      :agenda="f.agenda"
      :tags="f.tags"
      :host_id="f.host_id"
      :host="f.host"
      :attendees="f.attendees"
      :going="f.going"
      :maybe="f.maybe"
      :notGoing="f.not_going"
      :attendanceCode="f.attendance_code"
      :attendees_ids="f.attendees_ids"
    />
  </div>
</template>

<script>
import axios from "axios";
// import { eventBus } from "../main";
import Meeting from "./Meeting.vue";

export default {
  name: "MeetingList",
  props: { meetings: Array, emptyMsg: String },
  components: { Meeting },
  data() {
    return {
      username: this.$cookie.get("auth-username"),
      user: "",
    };
  },
  created: function () {},
  methods: {
    getUser() {
      axios
        .get(`/api/users/${this.username}`)
        .then((response) => {
          this.user = response.data;
        })
        .catch(() => {
          this.user = "";
        });
    },

    getUserID() {
      axios
        .get(`/api/users/id/${this.user._id}`)
        .then((response) => {
          return response.data;
        })
        .catch(() => {});
    },
  },
  mounted: function () {
    this.getUser();
    this.getUserID();
  },
};
</script>

<style>
.meeting-list {
  text-align: center;
  /* min-width: 450px; */
}
</style>
