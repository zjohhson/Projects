<template>
  <v-dialog v-model="dialog" width="500">
    <template v-slot:activator="{ on, attrs }">
      <v-btn color="blue lighten-2" dark fab v-bind="attrs" v-on="on" icon>
        <v-icon> mdi-pencil</v-icon>
      </v-btn>
    </template>

    <v-card>
      <v-card-title class="text-h5 grey lighten-2"> Edit Meeting </v-card-title>

      <v-card-text>
        <EditMeetingForm
          :id="id"
          :name="name"
          :start_date_time="start_date_time"
          :end_date_time="end_date_time"
          :location="location"
          :agenda="agenda"
          :attendanceCode="attendanceCode"
        />
      </v-card-text>

      <v-divider></v-divider>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" text @click="editMeeting"> Save </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import EditMeetingForm from "./EditMeetingForm.vue";
import { eventBus } from "../main";

export default {
  name: "EditMeetingFormVuetify",
  components: {
    EditMeetingForm,
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
    attendees: Array,
    going: Array,
    maybe: Array,
    notGoing: Array,
    attendanceCode: String,
  },
  data() {
    return {
      dialog: false,
    };
  },
  methods: {
    editMeeting() {
      eventBus.$emit("edit-meeting", { id: this.id });
      eventBus.$on("edit-meeting-success", () => {
        this.dialog = false;
      });
    },
  },
};
</script>
