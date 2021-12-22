<template>
  <v-chip label outlined :class="creatorTag" style="margin: 5px">
    <b>{{ tagName }}</b>
    <v-spacer></v-spacer>
    <v-icon
      v-if="isCreator"
      class="delete-tag-btn"
      md
      color="red lighten-2"
      @click="deleteTag"
    >
      mdi-close
    </v-icon>
  </v-chip>
</template>

<script>
import { eventBus } from "../main";
export default {
  name: "Tag",
  props: { tagName: String, isCreator: Boolean, meetingID: String },
  data() {
    return {};
  },
  methods: {
    deleteTag() {
      eventBus.$emit("delete-tag-from-meeting", {
        tagName: this.tagName,
        id: this.meetingID,
      });
    },
  },
  computed: {
    creatorTag: function () {
      return this.isCreator ? "creator-tag" : "";
    },
  },
};
</script>

<style>
.delete-tag-btn {
  margin-left: 0px;
  margin-right: 0px;
  cursor: pointer;
}

.v-chip.creator-tag {
  padding-right: 6px;
}
</style>
