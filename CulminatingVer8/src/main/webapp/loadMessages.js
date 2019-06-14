/**
 * Load any new messages and display them
 */
function loadNewMessages() {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
				      
			//Get the JSON object	
			var newMessages = JSON.parse(this.responseText);
					      
			//Add the messages to their respective divs
			for(i = 0; i < newMessages.length; i++) {
					    	  
				var ptag = document.createElement("p");
				var message = document.createTextNode(newMessages[i][0]);
				var useType = newMessages[i][1];
				ptag.appendChild(message);
				if(userType === "Patient") {
					document.getElementById("patientMessagesDiv").appendChild(ptag);
				} else {
					document.getElementById("therapistMessagesDiv").appendChild(ptag);
				}
							  
			}
						  
		loadNewMessages();
					      
		}
	};
	xhttp.open("POST", "/asyncDisplayMessages", true);
	xhttp.send(); 
}