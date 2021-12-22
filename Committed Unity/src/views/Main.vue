<template>
  <div id="main-feed">
    <NavBar :username="username" :isAdmin="user.is_admin" view="main" />
    <div v-if="!username">
      <div class="title">
        <h2>Welcome to</h2>
        <br />
        <h1>CommittedUnity!</h1>
        <br />
        <router-link :to="{ name: 'signIn' }">
          Sign in or create an account</router-link
        >
        to save Meetings and more!
      </div>
    </div>
    <div v-if="username">
      <v-row v-if="currTab === 'all-meetings'">
        <v-spacer></v-spacer>
        <FilterLeftBar :isAdmin="user.is_admin" />
        <div class="meeting-list-main-view">
          <MeetingList
            :meetings="meetings"
            emptyMsg="There are no meetings that match your search."
          />
        </div>
        <v-spacer></v-spacer>
      </v-row>

      <v-row v-if="currTab === 'my-meetings'">
        <v-spacer></v-spacer>
        <FilterLeftBar :isAdmin="user.is_admin" />
        <div class="meeting-list-main-view">
          <MeetingList
            :meetings="myMeetings"
            emptyMsg="There are no meetings that match your search."
          />
        </div>
        <v-spacer></v-spacer>
      </v-row>

      <v-row v-if="currTab === 'discover-meetings'">
        <v-spacer></v-spacer>
        <DiscoverLeftBar :recTags="recTags" />
        <div class="meeting-list-main-view">
          <MeetingList
            :meetings="recMeetings"
            emptyMsg="There are no recommended meetings. Try adding more interests or wait for
      more meetings to be posted!"
          />
        </div>
        <v-spacer></v-spacer>
      </v-row>

      <div v-if="currTab === 'profile'">
        <ProfileHeader :user="user" />
      </div>
      <div v-if="currTab === 'attend'">
        <AttendanceChecker />
      </div>
      <div v-if="currTab === 'leaderboard'">
        <UserList
          :users="thisMonthUserRankings"
          :title="'Top users this month'"
        />
        <!-- <UserList :users="allTimeUserRankings" :title="'All time time top users'"/> -->
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { eventBus } from "../main";
import MeetingList from "../components/MeetingList.vue";
import UserList from "../components/UserList.vue";
import NavBar from "../components/NavBar.vue";
import AttendanceChecker from "../components/AttendanceChecker.vue";
import ProfileHeader from "../components/ProfileHeader.vue";
import FilterLeftBar from "../components/FilterLeftBar.vue";
import DiscoverLeftBar from "../components/DiscoverLeftBar.vue";

export default {
  name: "Main",
  components: {
    MeetingList,
    UserList,
    NavBar,
    AttendanceChecker,
    ProfileHeader,
    FilterLeftBar,
    DiscoverLeftBar,
  },
  data() {
    return {
      currTab: "all-meetings",
      user: "",
      meetings: [],
      allMeetings: [],
      myMeetings: [],
      recTags: [],
      recMeetings: [],
      thisMonthUserRankings: [],
      allTimeUserRankings: [],
    };
  },
  computed: {
    username: function () {
      return this.$cookie.get("auth-username");
    },
  },
  created: function () {
    this.getThisMonthUserRankings();
    eventBus.$on("change-main-tab", (data) => {
      this.currTab = data.tab;
      if (this.currTab === "discover-meetings") {
        this.getRecTags();
      }
    });

    eventBus.$on("refresh-user-list", () => {
      this.getUser();
    });

    eventBus.$on("create-meeting-success", (res) => {
      this.meetings.unshift(res.data);
      this.getAllMeetings();
    });

    eventBus.$on("edit-meeting-success", () => {
      // this.meetings.unshift(res.data);
      this.getAllMeetings();
    });

    eventBus.$on("delete-meeting-success", () => {
      this.getAllMeetings();
    });

    eventBus.$on("delete-meeting-success", () => {
      this.getAllMeetings();
    });

    eventBus.$on("changed-rsvp", () => {
      this.getAllMeetings();
      this.getRecMeetings();
    });

    eventBus.$on("increment", () => {
      this.getUser();
    });

    eventBus.$on("search-by-name", (res) => {
      if (res.searchName) {
        this.meetings = this.meetings.filter((m) =>
          m.name.toUpperCase().includes(res.searchName.toUpperCase())
        );
      } else {
        this.getAllMeetings();
      }
    });

    eventBus.$on("refresh", () => {
      this.getAllMeetings();
    });

    eventBus.$on("search-by-tags", (res) => {
      if (res.tags.length > 0) {
        let checker = (arr, target) => target.every((v) => arr.includes(v));
        this.meetings = this.allMeetings.filter((m) =>
          checker(m.tags, res.tags)
        );
        // if (this.tab === "my-meetings") {
        this.myMeetings = this.myMeetings.filter((m) =>
          checker(m.tags, res.tags)
        );
        // }
      } else {
        this.getAllMeetings();
      }
    });

    eventBus.$on("search-by-date", (res) => {
      this.meetings = this.meetings.filter(
        (m) => m.start_date_time.substring(0, 10) === res.date
      );
      // if (this.tab === "my-meetings") {
      this.myMeetings = this.myMeetings.filter(
        (m) => m.start_date_time.substring(0, 10) === res.date
      );
      // }
    });
    eventBus.$on("attend-success", () => {
      this.getThisMonthUserRankings();
    });
  },
  methods: {
    getThisMonthUserRankings() {
      axios
        .get("/api/users/")
        .then((response) => {
          this.thisMonthUserRankings = response.data
            .filter((u) => !u.is_admin)
            .sort(function (user1, user2) {
              return user2.points - user1.points;
            });
        })
        .catch((error) => {
          this.thisMonthUserRankings = [];
          eventBus.$emit("error-msg", {
            errorMsg:
              error.response.status + " error: " + error.response.data.error,
          });
        });
    },
    getAllMeetings() {
      axios
        .get("/api/meetings")
        .then((response) => {
          this.meetings = response.data;
          this.allMeetings = response.data;
          if (this.user.is_admin) {
            this.myMeetings = this.allMeetings.filter(
              (m) => m.host_id === this.user._id
            );
          } else {
            this.myMeetings = this.allMeetings.filter(
              (m) =>
                m.going.map((u) => u.username).includes(this.username) ||
                m.maybe.map((u) => u.username).includes(this.username)
            );
          }
        })
        .catch((error) => {
          this.meetings = [];
          eventBus.$emit("error-msg", {
            errorMsg:
              error.response.status + " error: " + error.response.data.error,
          });
        });
    },
    getUser() {
      axios
        .get(`api/users/${this.username}`)
        .then((response) => {
          this.user = response.data;
        })
        .catch((error) => {
          eventBus.$emit("error-msg", {
            errorMsg: error.response.data.error,
          });
        });
    },
    getRecTags() {
      axios
        .get("api/users/tags")
        .then((response) => {
          this.recTags = response.data;
        })
        .catch((error) => {
          eventBus.$emit("error-msg", {
            errorMsg: error.response.data.error,
          });
        });
    },
    getRecMeetings() {
      axios
        .get("api/users/recMeetings")
        .then((response) => {
          this.recMeetings = response.data;
          console.log("MAIN", this.recMeetings);
        })
        .catch((error) => {
          eventBus.$emit("error-msg", {
            errorMsg: error.response.data.error,
          });
        });
    },
  },
  mounted: function () {
    this.getUser();
    this.getAllMeetings();
  },
};
</script>

<style>
#main-feed {
  margin-top: 20px;
}
.meeting-list-main-view {
  padding: 0 0 0 12px;
  min-width: 450px;
  max-width: 750px;
  flex-grow: 2;
}

.title {
  padding: 3%;
  text-align: center;
}

.card {
  display: flex;
  text-align: center;
}

.city {
  font-style: bold;
}
</style>
