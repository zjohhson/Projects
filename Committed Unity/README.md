# [https://committedunity-mvp.herokuapp.com/](https://committedunity.herokuapp.com/)

### Purpose and Functionality

The current Cambridge City Council webpage discourages civic engagement by offering a not-user-friendly interface. Users have to read through entire meeting agenda PDFs to determine whether the meeting is of interest. Our system allows admin to advertise their meetings by tagging it with relevant topics or tags, and shows all of the information to users in a way that is more convenient to view and save to their profiles. Another feature is that users can earn points and badges for attending meetings. These points and badges are publicly seen on the application, and so meeting organizers can give rewards and recognition to those who are especially involved. Our system's purpose is to increase Cambridge City Council Meeting engagement by making meeting information conveniently available.

For our MVP, we've implemented tagging capabilities for meetings, admin vs. general user privileges, attendance tracking, a user RSVP system, and an initial recommender system for both tags and meetings. On one hand, admin can create, read, edit, and delete meetings, and view people who have RSVPed for all meetings. On the other hand, users can RSVP to meetings as going, not going, or maybe going. They can also verify attendance for meetings by inputting an attendance code, and earn a point for each meeting attendance. Currently, there is a "Discover" tag which recommends tags and meetings with those tags to users with our initial recommender algorithm. For the final product, we plan to also implement the "My Meetings" tab, which will show users their "Going" and "Maybe" RSVP-ed meetings; for admin, the tab will show their created meetings.

### Instructions to Run Locally:

In command line:

```console
$ npm i
$ npm run serve
```

In a separate shell:

```console
$ npm start

```

Make sure you have `mongo` installed and have a local instance running as well for the database setup.

then you will find the application at `localhost:8080` in the browser

### Authorship:

- **Kelly He**:
  - src
    - App.vue
    - Meeting.vue
    - MeetingList.vue
    - MeetingRSVP.vue
    - MeetingStatusPanel.vue
    - MeetingTags.vue
    - NavBar.vue
    - SignIn.vue
    - SignUp.vue
    - Tag.vue
    - TagList.vue
    - UserList.vue
    - Username.vue
    - UsernameList.vue
  - models:
    - Meeting.js
    - User.js
  - routes:
    - index.js
    - meetings-controller.js
    - meetings.js
    - middleware.js
    - tags.js
    - user-controller.js
    - user.js
  - Boilerplate code (main.js, router.js, etc.)

- **Grace Hu**:
  - src
    - App.vue
    - Main.vue
    - ProfileHeader.vue
    - SimilarTags.vue
    - User.vue
    - UserList.vue
  - models:
    - History.js
  - routes:
    - history-controller.js
    - users.js
    - middleware.js
    - users-controller.js

- **Zach Johnson**:
  - src
    - CreateMeetingForm.vue
    - CreateMeetingFormVuetify.vue
    - EditMeetingForm.vue
    - EditMeetingFormVuetify.vue
  - models:
    - Meeting.js
    - User.js
  - routes:
    - middleware.js
    - users.js
    - meetings-controller.js
    - users-controller.js

- **Kelly Ho**:
  - src/components
    - AttendanceChecker.vue
    - InterestForm.vue
    - Tag.vue
    - CreateMeetingForm.vue
    - Main.vue
  - models:
    - Meeting.js
  - routes:
    - meetings.js
    - meetings-controller.js
  - App.vue/router.js
