<template>
  <div class="subcontainer">
    <div class="check-in">
      <v-text-field
        style="max-width: 150px"
        v-model="attendanceCode"
        :counter="5"
        label="Attendance Code"
        append-icon="mdi-check-bold"
        @click:append="checkIn"
        required
      ></v-text-field>
    </div>
    <span>{{ meeting }}</span>
  </div>
</template>

<script>
import { eventBus } from "../main";
import axios from "axios";

export default {
  name: "AttendanceChecker",
  props: {
    meetingCode: String,
    meetingID: String,
    attendees_ids: Array,
  },
  data() {
    return {
      attendanceCode: "",
      valid: false,
      meeting: undefined,
      user: "",
    };
  },
  methods: {
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
    checkIn() {
      if (this.attendanceCode != this.meetingCode) {
        eventBus.$emit("error-msg", {
          errorMsg: "Meeting attendance code is not valid.",
        });
        return;
      } else {
        if (!this.attendees_ids.includes(this.user._id)) {
          axios
            .patch(`api/meetings/${this.meetingID}/attendance`, {
              userId: this.user._id,
              meetingId: this.meetingID,
            })
            .then(() => {
              this.attendanceCode = "";
              this.valid = true;
            })
            .catch((error) => {
              console.log("ERROR HERE", error)
              eventBus.$emit("error-msg", {
                errorMsg: error.response.data.error,
              });
            });
          axios
            .patch("/api/users/point")
            .then(() => {
              eventBus.$emit("increment");
            })
            .catch((error) => {
              eventBus.$emit("error-msg", {
                errorMsg: error.response.data.error,
              });
            });
          eventBus.$emit("success-msg", {
            successMsg: "You checked in!",
          });
          eventBus.$emit("attend-success", {});
        } else {
          eventBus.$emit("error-msg", {
            errorMsg: "You have already checked into this meeting.",
          });
        }
      }
    },
  },
  mounted: function () {
    this.getUser();
  },
};
</script>

<style>
.check-in {
  display: flex;
  justify-content: center;
}

.message {
  display: flex;
  justify-content: center;
  width: 60%;
}

.card {
  display: flex;
  justify-content: center;
}
</style>
