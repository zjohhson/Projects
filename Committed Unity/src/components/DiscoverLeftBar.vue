<template>
  <v-card class="left-bar" elevation="4" width="256">
    <v-navigation-drawer floating permanent>
      <v-list dense rounded>
        <v-list-item>
          <v-list-item-content>
            <v-container fluid>
              <b>Here are some tags we think you might like!</b>
              <br><br>
              <Tag
              v-for="(t, i) in recTags"
              :key="i"
              :tagName="t"
              >
              </Tag>
            </v-container>
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
  </v-card>
</template>

<script>
import Tag from "./Tag.vue";

export default {
  name: "DiscoverLeftBar",
  props: { recTags: Array },
  components: { Tag },
  data() {
    return {};
  },

  created: function () {},
  mounted() {},
  methods: {},
};
</script>

<style scoped>
::v-deep .tag-checkbox .v-label {
  font-size: 14px;
}
</style>
