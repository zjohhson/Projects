<template>
  <v-container>
    <v-row align="center" justify="center">
      <v-col cols="4" align="center" justify="center" align-text="center">
        <v-avatar color="blue darken-1" size="8em">
          <span class="white--text text-h3"
            >{{ user.first_name.charAt(0).toUpperCase() +
            user.last_name.charAt(0).toUpperCase()}}
            </span>
        </v-avatar>
        <h2>{{ user.first_name }} {{ user.last_name }}</h2>
        <h4>@{{ user.username }}</h4>
        <h5 v-if="user.is_admin"><v-icon>mdi-wrench</v-icon> Admin Account</h5>
      </v-col>
    </v-row>
    <v-row justify="center" v-if="!user.is_admin">
      <History :user="user" />
    </v-row>
    <v-row justify="center">
      <v-card class="interests">
        <v-card-title class="justify-center">
          Interests &nbsp;
          <v-menu
            offset-y
            :close-on-click="true"
            :close-on-content-click="false"
            v-model="menuOpen"
          >
            <template v-slot:activator="{ on }">
              <v-btn small v-on="on"> Edit </v-btn>
            </template>
            <InterestForm />
          </v-menu>
        </v-card-title>
        <TagList :tags="user.interests" />
      </v-card>
    </v-row>
  </v-container>
</template>

<script>
import History from "../components/History.vue";
import TagList from "../components/TagList.vue";
import InterestForm from "../components/InterestForm.vue";

export default {
  name: "ProfileHeader",
  components: {
    History,
    TagList,
    InterestForm,
  },
  props: { user: Object },
  data() {
    return {
      menuOpen: false,
    };
  },
  methods: {},
};
</script>

<style>
.interests {
  padding: 10px;
  margin: 10px;
  width: 550px;
}
</style>
