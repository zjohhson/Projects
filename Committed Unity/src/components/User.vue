<template>
  <div class="leaderboard-container">
    {{ rank + 1 }}
    <v-card class="user-card" elevation="3">
      <v-card-text>
        <v-badge
          class="badge"
          left
          v-if="badges[0] !== 'None' && badges[0]"
          icon="mdi-trophy"
          bordered
          overlap
          :color="badgeColors[this.badges[0]]"
        >
          <v-avatar color="blue darken-1" size="36">
            <span class="white--text text-h7">
              {{
                firstName.charAt(0).toUpperCase() +
                lastName.charAt(0).toUpperCase()
              }}
            </span>
          </v-avatar>
        </v-badge>

        <v-avatar
          color="blue darken-1"
          size="36"
          v-if="badges[0] === 'None' || !badges[0]"
        >
          <span class="white--text text-h7">
            {{
              firstName.charAt(0).toUpperCase() +
              lastName.charAt(0).toUpperCase()
            }}
          </span>
        </v-avatar>
        <!-- <v-icon large :color="badgeColors[badges[0]]" v-if="badges[0]">
        mdi-trophy mdi-medal
      </v-icon>
      <v-icon medium v-if="is_admin"> mdi-wrench </v-icon> -->
        <span class="user-card-name">
          <b>{{ firstName }} {{ lastName }}</b>
          <i> @{{ username }}</i>
        </span>
        <span class="user-card-right">
          <b>Points</b><br />
          {{ points }}
        </span>
        <!-- <TagList :tags="interests" /> -->
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
// import TagList from "./TagList.vue";

export default {
  name: "User",
  props: {
    id: String,
    firstName: String,
    lastName: String,
    username: String,
    points: Number,
    badges: Array,
    interests: Array,
    is_admin: Boolean,
    rank: Number,
  },
  components: {
    // TagList,
  },
  data() {
    return {
      badgeColors: { Gold: "#FFD700", Silver: "#C0C0C0", Bronze: "#CD7F32" },
    };
  },
  created: function () {},
  methods: {},
  mounted: function() {
    console.log(this.badges)
  }
};
</script>

<style scoped>
.leaderboard-container {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1976d2;
  font-weight: bold;
}
.user-card {
  width: 60%;
  margin: 10px 0px 10px 10px;
  text-align: left;
}
.user-card-name {
  margin-left: 0.5em;
}
.user-card-right {
  float: right;
  text-align: center;
}
</style>
