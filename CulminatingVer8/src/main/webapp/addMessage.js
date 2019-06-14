/**
 * Add a message to the current chat
 */
function addMessage() {
				
	//Get key that was released
	var key = window.event.keyCode;
				
	var patientMessage = document.getElementById("textarea").innerHTML;
	document.getElementById("textarea").innerHTML = "";
				
	// If ENTER was pressed
	if (key === 13) {
		$.post("/CulminatingVer8/addMessageAsync",
			{
			    message : patientMessage
			});
	}
}