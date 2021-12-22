<template>
  <v-row justify="center">
    <v-dialog v-model="dialog" width="500">
      <template v-slot:activator="{ on, attrs }">
        <v-btn
          class="create-button"
          width="80%"
          color="blue lighten-2"
          dark
          v-bind="attrs"
          v-on="on"
        >
          Create Meeting
        </v-btn>
      </template>

      <v-card>
        <v-card-title class="text-h5 grey lighten-2">
          Create Meeting
        </v-card-title>

        <v-card-text>
          <CreateMeetingForm />
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" text @click="submitMeeting"> Create </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import CreateMeetingForm from "./CreateMeetingForm.vue";
import { eventBus } from "../main";

export default {
  name: "CreateMeetingFormVuetify",
  components: {
    CreateMeetingForm,
  },
  data() {
    return {
      dialog: false,
    };
  },
  methods: {
    submitMeeting() {
      eventBus.$emit("submit-meeting");
      eventBus.$on("create-meeting-success", () => {
        this.dialog = false;
      });
    },
  },
};
</script>

<style>
.create-button {
  margin: 20px;
}
</style>
