Backend:

[users.js]

- @name POST /api/users
  - @throws {400} - if the username is taken or if the username or password is empty
- @name GET /api/users/session
  - @throws {403} - if not logged in
- @name DELETE /api/users/session
  - @throws {403} - if not logged in
- @name POST /api/users/session
  - @throws {401} - if wrong password
  - @throws {404} - if user does not exist
- @name GET /api/users/tags
  - @throws {404} - if user does not exist
- @name GET /api/users/recMeetings
  - @throws {404} - if user does not exist
- @name GET /api/users/
  - @throws {404} - if user does not exist
- @name GET /api/users/:username?
  - @throws {404} - if user does not exist
- @name GET /api/users/id/:id?
  - @throws {404} - if user does not exist
- @name PUT /api/users/tags
  - @throws {404} - the tag doesn't exist in list for addition
- @name PATCH /api/users/tags
  - @throws {404} - the tag isn't found in user's interests
- @name POST /api/users/tags
- @name PATCH /api/users/tags
  - @throws {404} - the tag isn't found in user's interests
- @name PATCH /api/users/point
  - @throws {404} - the user isn't found

[meetings.js]

- @name GET /api/meetings
- @name POST /api/meetings
  - @throws {400} - meeting end time needs to end after start
- @name DELETE /api/meetings
  - @throws {403} - only admin can delete meetings
- @name PUT /api/meetings
  - @throws {403} - only admin can delete meetings
  - @throws {400} - meeting end time needs to end after start
- @name PATCH /api/meetings/:id/rsvp
  - @throws {404} - cannot find in db
- @name GET /api/meetings/attendanceCodes
  - @throws {404} - cannot find in db
- @name PATCH /api/meetings/:id/attendance
  - @throws {404} - cannot find in db

[tags.js]

- @name GET /api/tags/


Front end:

- /
  - home page with different tabs to meetings, profile, etc.
- /signIn
  - sign in or create account view
