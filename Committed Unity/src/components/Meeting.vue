<template>
  <v-card class="meeting" elevation="4">
    <div class="flex-row">
      <v-card-title style="padding: 16px 0 0 16px">
        {{ name }}
      </v-card-title>

      <v-spacer></v-spacer>
      <div class="flex-row admin-buttons">
        <EditMeetingFormVuetify
          v-if="user._id === host_id"
          :id="id"
          :name="name"
          :start_date_time="start_date_time"
          :end_date_time="end_date_time"
          :location="location"
          :agenda="agenda"
          :tags="tags"
          :attendanceCode="attendanceCode"
        />
        <DeleteMeetingForm v-if="user._id === host_id" :id="id" />
      </div>
    </div>
    <v-divider></v-divider>
    <div class="flex-row" style="align-items: center">
      <div style="min-width: 300px">
        <v-card-subtitle>
          <!-- <v-icon> mdi-calendar-clock-outline </v-icon> -->
          <v-icon small> mdi-calendar </v-icon>
          {{ dateDay }} <br />
          <v-icon small> mdi-clock-time-four-outline </v-icon>
          {{ dateTime }}<br />
          <v-icon small> mdi-map-marker </v-icon>
          <a :href="locationLink" target="_blank" rel="noopener noreferrer">{{
            location
          }}</a>
        </v-card-subtitle>
      </div>
      <v-spacer></v-spacer>
      <div class="tag-top-right" style="align-content: flex-end">
        <TagList
          v-if="user._id === host_id"
          :isCreator="true"
          :tags="tags"
          :meetingID="id"
        />
        <TagList v-else :isCreator="false" :tags="tags" :meetingID="id" />
      </div>
    </div>
    <v-divider></v-divider>
    <div class="flex-row">
      <v-card-text class="left-col">
        <div class="agenda-text" style="max-width: 100%">
          <!-- <v-icon> mdi-pencil </v-icon>  -->
          <h3>Agenda</h3>
          <v-card-subtitle>{{ agenda }}</v-card-subtitle>
        </div>

        <div class="meeting-footer-text">
          <div v-if="user._id === host_id">
            <v-icon small v-if="user._id === host_id"> mdi-qrcode </v-icon>
            Attendance Code: {{ attendanceCode }}
          </div>
          <i>Hosted by @{{ host[0].username }}</i>
        </div>
      </v-card-text>
      <v-card-text class="right-col">
        <AttendanceChecker
          v-if="!user.is_admin"
          :meetingCode="attendanceCode"
          :meetingID="id"
          :attendees_ids="attendees_ids"
        />

        <MeetingRSVP
          :user="user ? user : {}"
          :meetingID="id"
          :going="going"
          :maybe="maybe"
          :notGoing="notGoing"
        />
        <MeetingStatusPanel
          :going="going"
          :maybe="maybe"
          :notGoing="notGoing"
          :attendees="attendees"
          :host_id="host_id"
        />
      </v-card-text>
    </div>
  </v-card>
</template>

<script>
import TagList from "./TagList.vue";
import MeetingStatusPanel from "./MeetingStatusPanel.vue";
import EditMeetingFormVuetify from "./EditMeetingFormVuetify.vue";
import DeleteMeetingForm from "./DeleteMeetingForm.vue";
import MeetingRSVP from "./MeetingRSVP.vue";
import AttendanceChecker from "./AttendanceChecker.vue";
import axios from "axios";
import { eventBus } from "../main";

export default {
  name: "Meeting",
  components: {
    TagList,
    MeetingStatusPanel,
    EditMeetingFormVuetify,
    DeleteMeetingForm,
    MeetingRSVP,
    AttendanceChecker,
  },
  props: {
    id: String,
    name: String,
    start_date_time: String,
    end_date_time: String,
    location: String,
    agenda: String,
    tags: Array,
    host_id: String,
    host: Array,
    attendees: Array,
    going: Array,
    maybe: Array,
    notGoing: Array,
    attendanceCode: String,
    belongs: Boolean,
    attendees_ids: Array,
  },
  watch: {},
  data() {
    return {
      selection: "",
      loggedInAccount: this.$cookie.get("auth-username"),
      newName: "",
      newStartTime: "",
      newEndTime: "",
      newDate: "",
      newLocation: "",
      newAgenda: "",
      // host: "",
      user: "",
      prevStatus: "",
      newStatus: "",
    };
  },
  computed: {
    locationLink: function () {
      return "https://maps.google.com/?q=" + this.location;
    },
    dateDay: function () {
      return new Date(this.start_date_time.substring(0, 16)).toDateString();
    },
    dateTime: function () {
      let start = new Date(
        this.start_date_time.substring(0, 16)
      ).toLocaleTimeString([], { timeStyle: "short" });
      let end = new Date(
        this.end_date_time.substring(0, 16)
      ).toLocaleTimeString([], { timeStyle: "short" });

      return start + " - " + end;
    },
  },
  created: function () {
    eventBus.$on("delete-tag-from-meeting", (response) => {
      const filteredTags = this.tags.filter((t) => t !== response.tagName);
      if (response.id === this.id) {
        this.putTags(response.id, filteredTags);
      }
    });
  },
  methods: {
    putTags(inputID, tags) {
      axios
        .put(`/api/meetings/${inputID}`, { tags: tags })
        .then((response) => {
          eventBus.$emit("edit-meeting-success", {
            data: response.data,
            status: response.status,
          });
        })
        .catch((error) => {
          eventBus.$emit("error-msg", {
            errorMsg: error.response.data.error,
          });
        });
    },

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
  },
  mounted: function () {
    this.getUser();
  },
};
</script>

<style>
.agenda-text {
  margin-bottom: 45px;
}

.flex-row {
  display: flex;
  flex-direction: row;
}

.admin-buttons {
  justify-content: flex-end;
}

.header {
  margin: 12px;
}

.meeting {
  width: 100%;
  /* min-width: 450px; */
  margin: 0px auto 12px auto;
  text-align: left;
  padding: 6px 12px 6px 12px;
}

.left-col {
  flex: 3 1 auto;
  max-width: 60%;
  justify-content: space-between;
}

.right-col {
  flex: 12 2 auto;
  width: 20%;
  min-width: 280px;
  align-items: center;
}

.tag-top-right {
  margin-right: 20px;
}

.tag-top-margin-if {
  margin-top: 22px;
}

.meeting-footer-text {
  position: absolute;
  bottom: 20px;
  font-size: small;
  color: gray;
}
</style>
