<template>
  <v-btn
    icon
    :loading="loading5"
    :disabled="loading5"
    class=""
    fab
    @click="
      loader = 'loading5';
      deleteMeeting();
    "
  >
    <v-icon color="red lighten-2"> mdi-delete </v-icon>
  </v-btn>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";
export default {
  props: {
    id: String,
  },
  data() {
    return {
      loader: null,
      loading5: false,
      errors: [],
      success: "",
      intervalID: null,
    };
  },
  watch: {
    loader() {
      const l = this.loader;
      this[l] = !this[l];
      setTimeout(() => (this[l] = false), 1000);
      this.loader = null;
    },
  },
  methods: {
    deleteMeeting() {
      this.errors = [];
      axios
        .delete(`/api/meetings/${this.id}`, {})
        .then((response) => {
          eventBus.$emit("delete-meeting-success", {
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
          this.clearMessages();
        });
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
  },
};
</script>

<style>
.custom-loader {
  animation: loader 0.5s infinite;
  display: flex;
}
@-moz-keyframes loader {
  from {
    transform: rotate(0);
  }
  to {
    transform: rotate(360deg);
  }
}
@-webkit-keyframes loader {
  from {
    transform: rotate(0);
  }
  to {
    transform: rotate(360deg);
  }
}
@-o-keyframes loader {
  from {
    transform: rotate(0);
  }
  to {
    transform: rotate(360deg);
  }
}
@keyframes loader {
  from {
    transform: rotate(0);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
