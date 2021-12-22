<template>
  <div>
      <v-card class="history">
        <v-card-title class="justify-center">
          Point Stats
        </v-card-title>
        <v-card-text class="justify-center">
          Insight into your community engagement.
        </v-card-text>
        <v-row class="justify-center">
          <v-card class="history-card" >
            <v-card-subtitle> This Month's Points </v-card-subtitle>
            <v-card-title class="justify-center">
              {{ user.points }}
            </v-card-title>
          </v-card>
          <v-card class="history-card">
            <v-card-subtitle> Total Points </v-card-subtitle>
            <v-card-title class="justify-center"
              >{{ histData ? histData.total + user.points : user.points }}
            </v-card-title>
          </v-card>
          <v-card class="history-card">
            <v-card-subtitle> Average Monthly Points </v-card-subtitle>
            <v-card-title class="justify-center"
              >{{ histData ? histData.total : 0 }} 
            </v-card-title>
          </v-card>
        </v-row>
      </v-card>
  </div>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";

export default {
  name: "History",
  components: {},
  props: { user: Object },
  data() {
    return {
      histData: "",
      date: "",
      points: 0,
    };
  },
  created() {
    this.getTotalPoints();
  },

  mounted: function() {
    this.getTotalPoints();
  },

  methods: {
    getTotalPoints() {
      axios
        .get("api/users/history")
        .then((response) => {
          this.histData = response.data[0];
        })
        .catch((error) => {
          eventBus.$emit("error-msg", {
            errorMsg: error.response.data.error,
          });
        });
    },
    addHistory() {
      axios
        .post("api/users/history", { date: this.date, points: this.points })
        .then((response) => {
          this.histData = response.data;
        });
    },
  },
};
</script>

<style>
.history-card {
  align-items: justify-center;
  margin: 10px;
}

.history{
  align-items: justify-center;
  margin: 10px;
  padding: 10px;
  width: 550px;
}
</style>
