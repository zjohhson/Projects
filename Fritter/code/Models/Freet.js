let freetModel = (function() {
	let freetList = []; 
	let counter = 0;

	return {
		// added refreet field to freet
		createFreet: function(username, content, refreetContent, refreeter) {
			let freet = {username: username, content: content, id: counter, refreetContent: refreetContent, refreeter: refreeter, count: 0}; // freets store the username of the user who posted, a unique ID number, and the content
			freetList.unshift(freet);
			counter++;
			return freet;
		},
		findFreet: function(id) { 
			return freetList.filter(f => f.id === parseInt(id))[0];
		},
		editFreet: function(id, content) {
			const freet = freetList.filter(f => f.id === parseInt(id))[0];
			freet.content = content;
			return freet;
		},
		editRefreet: function(id, content) {
			const freet = freetList.filter(f => f.id === parseInt(id))[0];
			freet.refreetContent = content;
			return freet;
		},
		deleteFreet: function(id) {
			const freet = freetList.filter(f => f.id === parseInt(id))[0]; // using parseInt since id comes from our URL and is a string
			freetList = freetList.filter(f => f.id != parseInt(id));
			return freet;
		},
		viewAllFreets: function() {
			freetList.sort(function(a,b) {return b.count - a.count});
			return freetList;
		},
		viewFreetsByAuthor: function(username) {
			return freetList.filter(freet => freet.username === username);
		},
		updateUsernames: function(oldUsername, newUsername) {
			freetList.map((freet) => {
				if (freet.username === oldUsername) {
					freet.username = newUsername
				}
			})
			return freetList
		},
		deleteUserFreets: function(username) {
			freetList = freetList.filter(f => f.username != username);
			return freetList;
		},
		incrementCount: function(id) {
			const freet = freetList.filter(f => f.id === parseInt(id))[0];
			freet.count += 1;
			return freet;
		},
		decrementCount: function(id) {
			const freet = freetList.filter(f => f.id === parseInt(id))[0];
			freet.count -= 1;
			return freet;
		},
		getCount: function(id) {
			const freet = freetList.filter(f => f.id === parseInt(id))[0];
			return freet.count;			
		}
	};
})();

module.exports.freetModel = freetModel