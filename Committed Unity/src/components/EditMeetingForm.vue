<template>
  <div class="meeting-form-container">
    <!-- the submit event will no longer reload the page -->
    <v-form id="edit-meeting" v-on:submit.prevent="editMeeting" method="post">
      <v-container>
        <v-row>
          <v-col cols="12">
            <v-text-field
              v-model.trim="newName"
              color="blue darken-1"
              label="New meeting name"
              counter="100"
              maxlength="100"
              prepend-icon="mdi-gamepad"
              required
            ></v-text-field>

            <v-text-field
              v-model.trim="newLocation"
              color="blue darken-1"
              label="New location"
              counter="100"
              maxlength="100"
              prepend-icon="mdi-map-marker"
              required
            ></v-text-field>
            <v-menu
              v-model="dateMenu"
              :close-on-content-click="false"
              :nudge-right="40"
              transition="scale-transition"
              offset-y
              min-width="auto"
            >
              <template v-slot:activator="{ on, attrs }">
                <v-text-field
                  v-model="newDate"
                  label="New date"
                  color="blue darken-1"
                  prepend-icon="mdi-calendar"
                  v-bind="attrs"
                  v-on="on"
                ></v-text-field>
              </template>
              <v-date-picker
                v-model="newDate"
                @input="dateMenu = false"
                :min="currentDate"
                :show-current="true"
              ></v-date-picker>
            </v-menu>
            <v-row>
              <v-col cols="6">
                <v-menu
                  ref="menu"
                  v-model="startTimeMenu"
                  :close-on-content-click="false"
                  :nudge-right="40"
                  transition="scale-transition"
                  offset-y
                  max-width="290px"
                  min-width="290px"
                >
                  <template v-slot:activator="{ on, attrs }">
                    <v-text-field
                      v-model="startTime12Hr"
                      label="New start time"
                      color="blue darken-1"
                      prepend-icon="mdi-clock-time-four-outline"
                      readonly
                      v-bind="attrs"
                      v-on="on"
                    ></v-text-field>
                  </template>

                  <v-time-picker
                    v-if="startTimeMenu"
                    v-model="newStartTime"
                    :allowed-minutes="allowedMinutes"
                    ampm-in-title
                    format="ampm"
                    scrollable
                    full-width
                    @click:minute="$refs.menu.save(newStartTime)"
                  ></v-time-picker>
                </v-menu>
              </v-col>

              <v-col cols="6">
                <v-menu
                  ref="menu2"
                  color="blue lighten-2"
                  v-model="endTimeMenu"
                  :close-on-content-click="false"
                  :nudge-right="40"
                  transition="scale-transition"
                  offset-y
                  max-width="290px"
                  min-width="290px"
                >
                  <template v-slot:activator="{ on, attrs }">
                    <v-text-field
                      v-model="endTime12Hr"
                      color="blue darken-1"
                      label="New end time"
                      prepend-icon="mdi-clock-time-four"
                      readonly
                      v-bind="attrs"
                      v-on="on"
                    ></v-text-field>
                  </template>

                  <v-time-picker
                    v-if="endTimeMenu"
                    v-model="newEndTime"
                    :allowed-minutes="allowedMinutes"
                    ampm-in-title
                    format="ampm"
                    scrollable
                    full-width
                    @click:minute="$refs.menu.save(newEndTime)"
                  ></v-time-picker>
                </v-menu>
              </v-col>
            </v-row>
            <v-textarea
              counter="500"
              color="blue darken-1"
              v-model="newAgenda"
              label="What is your new meeting agenda?"
              prepend-icon="mdi-pencil"
            ></v-textarea>
            <v-text-field
              readonly
              color="blue darken-1"
              prepend-icon="mdi-qrcode"
              label="Attendance Code (Generated)"
              :value="attendanceCode"
            ></v-text-field>
          </v-col>

          <MeetingTags v-on:childToParent="onChange" />

          <div v-if="success" class="success-message">
            {{ success }}
          </div>

          <div v-if="errors.length" class="error-message">
            <b>Please correct the following error(s):</b>
            <ul>
              <li v-for="error in errors" v-bind:key="error.id">{{ error }}</li>
            </ul>
          </div>
        </v-row>
      </v-container>
    </v-form>
  </div>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";
import MeetingTags from "./MeetingTags.vue";

export default {
  name: "EditMeetingForm",
  components: {
    MeetingTags,
  },
  props: {
    id: String,
    name: String,
    start_date_time: String,
    end_date_time: String,
    location: String,
    agenda: String,
    host_id: String,
    attendees: Array,
    going: Array,
    maybe: Array,
    notGoing: Array,
    attendanceCode: String,
  },
  data() {
    return {
      errors: [],
      success: "",
      host: this.$cookie.get("auth-username"),
      intervalID: null,
      user: "",
      newName: "",
      newLocation: "",
      newStartTime: "",
      newEndTime: "",
      newDate: "",
      newAgenda: "",
      newTags: [],
      allTags: [],
      dateMenu: false,
      startTimeMenu: false,
      endTimeMenu: false,
      allowedMinutes: [0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55],
    };
  },

  computed: {
    currentDate: function () {
      var today = new Date();
      var dd = today.getDate();
      var mm = today.getMonth() + 1; //January is 0!
      var yyyy = today.getFullYear();

      if (dd < 10) {
        dd = "0" + dd;
      }

      if (mm < 10) {
        mm = "0" + mm;
      }

      today = yyyy + "-" + mm + "-" + dd;
      return today;
    },
    minTime: function () {
      if (this.date === this.currentDate) {
        var today = new Date();
        var time = today.getHours() + ":" + today.getMinutes();
        return time;
      } else {
        return "00:00";
      }
    },
    newStartDateTime: function () {
      if (this.newDate && this.newStartTime) {
        return `${this.newDate}T${this.newStartTime}:00Z`;
      } else if (this.newDate && !this.newStartTime) {
        return `${this.newDate}T${this.start_date_time.substring(11, 16)}:00Z`;
      } else if (!this.newDate && this.newStartTime) {
        return `${this.start_date_time.substring(0, 10)}T${
          this.newStartTime
        }:00Z`;
      } else {
        return `${this.start_date_time.substring(
          0,
          10
        )}T${this.start_date_time.substring(11, 16)}:00Z`;
      }
    },
    newEndDateTime: function () {
      if (this.newDate && this.newEndTime) {
        return `${this.newDate}T${this.newEndTime}:00Z`;
      } else if (this.newDate && !this.newEndTime) {
        return `${this.newDate}T${this.end_date_time.substring(11, 16)}:00Z`;
      } else if (!this.newDate && this.newEndTime) {
        return `${this.end_date_time.substring(0, 10)}T${this.newEndTime}:00Z`;
      } else {
        return `${this.end_date_time.substring(
          0,
          10
        )}T${this.end_date_time.substring(11, 16)}:00Z`;
      }
    },
    endTime12Hr: function () {
      // doesn't matter the date, just used to display the TIME
      // in a more user friendly manner
      return this.newEndTime
        ? new Date(`2000-05-09T${this.newEndTime}`).toLocaleTimeString([], {
            timeStyle: "short",
          })
        : "";
    },
    startTime12Hr: function () {
      // doesn't matter the date, just used to display the TIME
      // in a more user friendly manner
      return this.newStartTime
        ? new Date(`2000-05-09T${this.newStartTime}`).toLocaleTimeString([], {
            timeStyle: "short",
          })
        : "";
    },
  },

  methods: {
    editMeeting: function (inputID) {
      this.errors = [];
      axios
        .patch(`/api/meetings/${inputID}`, {
          id: inputID,
          name: this.newName,
          startDateTime: this.newStartDateTime,
          endDateTime: this.newEndDateTime,
          location: this.newLocation,
          agenda: this.newAgenda,
          tags: this.newTags,
          host: this.user,
        })
        .then((response) => {
          eventBus.$emit("edit-meeting-success", {
            data: response.data,
            status: response.status,
          });
          // clear the form when done editing
          this.newName = "";
          this.newLocation = "";
          this.newStartTime = "";
          this.newEndTime = "";
          this.newDate = "";
          this.newAgenda = "";
          this.error = "";
        })
        .catch((err) => {
          // handle error
          this.errors.push(err.response.data.message);
        })
        .then(() => {
          // always executed
          this.clearMessages();
        });
    },

    addTags: function () {
      for (const tag of this.newTags) {
        if (!this.allTags.includes(tag)) {
          axios
            .post("api/users/tags", { tag: tag })
            .then(() => {
              this.getAllTags();
              this.error = "";
            })
            .catch((err) => {
              this.errors.push(err.response.data.message);
            })
            .then(() => {
              this.clearMessages();
            });
        }
      }
    },

    loadHost: function () {
      if (this.$cookie.get("auth-username")) {
        axios
          .get(`/api/users/${this.$cookie.get("auth-username")}`)
          .then((response) => {
            this.host = `${response.data.first_name} ${response.data.last_name}`;
          });
      }
    },

    clearMessages: function () {
      if (this.intervalID) {
        clearInterval(this.intervalID);
      }
      this.intervalID = setInterval(() => {
        this.errors = [];
        this.success = "";
      }, 5000);
    },

    getUser() {
      axios
        .get(`/api/users/${this.$cookie.get("auth-username")}`)
        .then((response) => {
          this.user = response.data;
        })
        .catch(() => {
          this.user = "";
        });
    },

    getAllTags() {
      axios
        .get("api/tags")
        .then((response) => {
          this.allTags = response.data;
        })
        .catch(() => {
          this.allTags = [];
        });
    },

    onChange(value) {
      this.newTags = value;
    },
  },
  created: function () {
    eventBus.$on("edit-meeting", (response) => {
      if (response.id === this.id) {
        this.editMeeting(response.id);
        this.addTags();
      }
    });
  },

  mounted: function () {
    this.loadHost();
    this.getUser();
    this.getAllTags();
  },
};
</script>
