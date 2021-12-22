let userModel = (function() {
	let userList = []; 

	return {
		createUser: function(username, password) {
			const user = {username: username, password: password, likes: [], followers: [], following: []} // user is an object that stores a username and password for each unique account
			userList.push(user)
			return user.username;
		},
		findUser: function(username) {
			return userList.filter(u => u.username === username)[0];
		},
		changeUsername: function(oldUsername, newUsername) {
			const user = userList.filter(u => u.username === oldUsername)[0]
			user.username = newUsername
			return user.username;
		},
		changePassword: function(username, newPassword) {
			const user = userList.filter(u => u.username === username)[0]
			userList.find(u => u.username === username).password = newPassword; // finds matching user and updates password property
			return user.username;
		},
		deleteUser: function(username) { 
			const user = userList.filter(user => user.username === username)[0];
			userList = userList.filter(u => u.username !== username); // uses filter functional to only keep any user whose username does not match the username we are trying to delete
			return user.username;
		},
		signIn: function(username, password) { // returns boolean: true if username and password match a user in our database
			const user = userList.filter(u => u.username === username)[0];
			return user.username===username && user.password===password; 
		},
		likeFreet: function(username, id) {
			const user = userList.filter(u => u.username === username)[0];
			user.likes.push(id);
			return user.username;
		},
		unlikeFreet: function(username, id) {
			const user = userList.filter(u => u.username === username)[0];
			user.likes = user.likes.filter(i => i != id);
			return user.username;
		},
		getLikedFreets: function(username) {
			const user = userList.filter(u => u.username === username)[0];
			return user.likes;
		},
		follow: function(u1, u2) {
			const user1 = userList.filter(u => u.username === u1)[0];
			const user2 = userList.filter(u => u.username === u2)[0];
			user1.following.push(user2.username);
			user2.followers.push(user1.username);
			return user1;
		},
		unfollow: function(u1, u2) {
			const user1 = userList.filter(u => u.username === u1)[0];
			const user2 = userList.filter(u => u.username === u2)[0];
			user1.following = user1.following.filter(u => u != u2);
			user2.followers = user1.followers.filter(u => u != u1);
			return user1;
		},
		getFollowers: function(username) {
			const user = userList.filter(u => u.username === username)[0];
			return user.followers;
		},
		getFollowing: function(username) {
			const user = userList.filter(u => u.username === username)[0];
			return user.following;
		},
		getAllUsers: function() {
			return userList;
		},
		updateFollowers: function(oldUsername, newUsername) {
			userList.map((user) => {
				if (user.followers.includes(oldUsername)) {
					user.followers = user.followers.filter(username => username != oldUsername);
					user.followers.push(newUsername)
				}
			})
			return userList
		},
		updateFollowing: function(oldUsername, newUsername) {
			userList.map((user) => {
				if (user.following.includes(oldUsername)) {
					user.following = user.following.filter(username => username != oldUsername);
					user.following.push(newUsername)
				}
			})
			return userList
		},
		deleteFollowing: function(username) {
			userList.map((user) => {
				if (user.following.includes(username)) {
					user.following = user.following.filter(u => u != username);
				}
			})
			return userList
		},
		deleteFollower: function(username) {
			userList.map((user) => {
				if (user.followers.includes(username)) {
					user.followers = user.followers.filter(u => u != username);
				}
			})
			return userList
		}
	};
})();

module.exports.userModel = userModel