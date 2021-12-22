<template>
  <v-col cols="12">
    <v-combobox
      v-model="select"
      :items="tags"
      label="Add or create new relevant tags"
      hint="What are some keywords for this meeting?"
      multiple
      chips
      prepend-icon="mdi-tag-plus-outline"
      v-bind:selectedTags="select"
      @change="emitToParent"
    >
      <template v-slot:selection="data">
        <Tag :tagName="data.item" />
        <!-- <v-chip
          :key="JSON.stringify(data.item)"
          v-bind="data.attrs"
          :input-value="data.selected"
          :disabled="data.disabled"
          @click:close="data.parent.selectItem(data.item)"
        >
          <v-avatar
            class="accent white--text"
            left
            v-text="data.item.slice(0, 1).toUpperCase()"
          ></v-avatar>
          {{ data.item }}
        </v-chip> -->
      </template>
    </v-combobox>
  </v-col>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";
import Tag from "./Tag.vue";

export default {
  components: {
    Tag,
  },
  data() {
    return {
      select: [],
      tags: [],
    };
  },
  mounted: function () {
    this.getTags();
  },
  created: function () {
    eventBus.$on("edit-meeting-success", () => {
      this.select = [];
    });
  },
  methods: {
    getTags() {
      axios
        .get("api/tags")
        .then((response) => {
          this.tags = response.data;
        })
        .catch((error) => {
          eventBus.$emit("error-msg", {
            errorMsg: error.response.data.error,
          });
        });
    },
    emitToParent() {
      this.$emit("childToParent", this.select);
    },
  },
};
</script>
