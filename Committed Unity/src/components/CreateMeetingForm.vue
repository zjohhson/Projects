<template>
  <div class="meeting-form-container">
    <!-- the submit event will no longer reload the page -->
    <v-form id="create-meeting" @submit.prevent="createMeeting" method="post">
      <v-container>
        <v-row>
          <v-col cols="12">
            <v-text-field
              v-model.trim="meetingName"
              color="blue darken-1"
              label="Meeting name"
              counter="100"
              maxlength="100"
              prepend-icon="mdi-gamepad"
              required
            ></v-text-field>
            <v-text-field
              v-model.trim="location"
              color="blue darken-1"
              label="Location"
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
                  v-model="date"
                  label="Date"
                  color="blue darken-1"
                  prepend-icon="mdi-calendar"
                  v-bind="attrs"
                  v-on="on"
                ></v-text-field>
              </template>
              <v-date-picker
                v-model="date"
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
                      label="Start time"
                      color="blue darken-1"
                      prepend-icon="mdi-clock-time-four-outline"
                      readonly
                      v-bind="attrs"
                      v-on="on"
                    ></v-text-field>
                  </template>

                  <v-time-picker
                    v-if="startTimeMenu"
                    v-model="startTime"
                    :allowed-minutes="allowedMinutes"
                    ampm-in-title
                    format="ampm"
                    scrollable
                    full-width
                    @click:minute="$refs.menu.save(startTime)"
                  ></v-time-picker>
                </v-menu>
              </v-col>

              <v-col cols="6">
                <v-menu
                  ref="menu2"
                  color="blue darken-1"
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
                      label="End time"
                      prepend-icon="mdi-clock-time-four"
                      readonly
                      v-bind="attrs"
                      v-on="on"
                    ></v-text-field>
                  </template>

                  <v-time-picker
                    v-if="endTimeMenu"
                    v-model="endTime"
                    :allowed-minutes="allowedMinutes"
                    ampm-in-title
                    format="ampm"
                    scrollable
                    full-width
                    @click:minute="$refs.menu.save(endTime)"
                  ></v-time-picker>
                </v-menu>
              </v-col>
            </v-row>
            <v-textarea
              counter="500"
              color="blue darken-1"
              v-model="agenda"
              label="What is your meeting agenda?"
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
          {{ select }}

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
  name: "CreateMeetingForm",
  components: {
    MeetingTags,
  },
  props: ["select"],
  data() {
    return {
      errors: [],
      success: "",
      meetingName: "",
      location: "",
      startTime: "",
      endTime: "",
      date: "",
      agenda: "",
      tags: [],
      allTags: [],
      attendanceCode: this.generateCode(5),
      host: "",
      intervalID: null,
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
    startDateTime: function () {
      return `${this.date}T${this.startTime}:00Z`;
    },
    endDateTime: function () {
      return `${this.date}T${this.endTime}:00Z`;
    },
    endTime12Hr: function () {
      // doesn't matter the date, just used to display the TIME
      // in a more user friendly manner
      return this.endTime
        ? new Date(`2000-05-09T${this.endTime}`).toLocaleTimeString([], {
            timeStyle: "short",
          })
        : "";
    },
    startTime12Hr: function () {
      // doesn't matter the date, just used to display the TIME
      // in a more user friendly manner
      return this.startTime
        ? new Date(`2000-05-09T${this.startTime}`).toLocaleTimeString([], {
            timeStyle: "short",
          })
        : "";
    },
  },

  methods: {
    createMeeting: function () {
      this.errors = [];

      if (
        !this.meetingName ||
        !this.location ||
        !this.startTime ||
        !this.endTime ||
        !this.date ||
        !this.agenda ||
        this.tags.length === 0
      ) {
        this.errors.push("Please fill out all fields.");
        this.clearMessages();
      } else if (!this.host) {
        this.errors.push("You must be an admin to create a meeting.");
        this.clearMessages();
      } else {
        axios
          .post("/api/meetings", {
            name: this.meetingName,
            startDateTime: this.startDateTime,
            endDateTime: this.endDateTime,
            location: this.location,
            agenda: this.agenda,
            tags: this.tags,
            host_id: this.host._id,
            attendees: [],
            going: [],
            maybe: [],
            notGoing: [],
            attendanceCode: this.attendanceCode,
          })
          .then((response) => {
            eventBus.$emit("create-meeting-success", {
              data: response.data,
              status: response.status,
            });
            this.error = "";
          })
          .catch((err) => {
            // handle error
            this.errors.push(err.response.data.message);
          })
          .then(() => {
            // always executed
            this.resetForm();
            this.clearMessages();
          });
      }
    },

    addTags: function () {
      for (const tag of this.tags) {
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

    addSimilarTags: function () {
      for (const tag of this.tags) {
        if (!this.allTags.includes(tag)) {
          const simTags = this.tags.filter((t) => t != tag);
          axios
            .post("api/users/similarTags", { tag: tag, simTags: simTags })
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
      this.tags = value;
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
          this.host = response.data;
        })
        .catch(() => {
          this.host = "";
        });
    },

    resetForm: function () {
      this.meetingName = "";
      this.location = "";
      this.startTime = "";
      this.endTime = "";
      this.date = "";
      this.agenda = "";
      this.tags = [];
      this.attendanceCode = this.generateCode(5);
    },

    generateCode(length) {
      var result = "";
      var characters =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
      var charactersLength = characters.length;
      for (var i = 0; i < length; i++) {
        result += characters.charAt(
          Math.floor(Math.random() * charactersLength)
        );
      }
      return result;
    },
  },
  created: function () {
    eventBus.$on("submit-meeting", () => {
      this.createMeeting();
      this.addTags();
      this.addSimilarTags();
    });
  },

  mounted: function () {
    // this.loadHost();
    this.getUser();
    this.getAllTags();
  },
};
</script>
