<template>
  <v-card class="left-bar" elevation="4" width="256">
    <v-navigation-drawer floating permanent>
      <v-list dense rounded>
        <CreateMeetingFormVuetify v-if="isAdmin" />
        <SearchByName />
        <SearchByDate />
        <SearchByTags />
      </v-list>
    </v-navigation-drawer>
  </v-card>
</template>

<script>
import SearchByName from "./SearchByName.vue";
import SearchByTags from "./SearchByTags.vue";
import SearchByDate from "./SearchByDate.vue";
import CreateMeetingFormVuetify from "./CreateMeetingFormVuetify.vue";

export default {
  name: "FilterLeftBar",
  props: {
    isAdmin: Boolean,
  },
  components: {
    SearchByName,
    SearchByTags,
    SearchByDate,
    CreateMeetingFormVuetify,
  },

  created: function () {},
  methods: {},
  mounted: function () {},
};
</script>

<style>
.left-bar {
  margin-right: 0;
}
</style>
