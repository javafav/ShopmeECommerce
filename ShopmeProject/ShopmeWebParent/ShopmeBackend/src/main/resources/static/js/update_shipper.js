var confirmText;
var cconfirmModal;
var yesButton;
$(document).ready(function() {
	cconfirmModal = $("#confirmModal")
	confirmText = $("#confirmText");
	
	$(".linkUpdateStatus").on("click", function(e) {
		e.preventDefault();
		showConfirmModal($(this));
   
	});
	
	   addEventHandlerForYesButton();

});

function addEventHandlerForYesButton(){
	yesButton.on("click", function(e){
		e.preventDefault();
	  sendRequestToUpdateOrderStatus($(this));
	
	});
}

function sendRequestToUpdateOrderStatus(button){
	requestURL = button.attr("href");
	
	$.ajax({
		type: 'POST',
		url: requestURL,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
				
	}).done(function(response) {
   console.log(response);
	}).fail(function() {
	
	})	
}

function showConfirmModal(link) {
 
	orderId = link.attr("id");
	orderStatus = link.attr("orderStatus");
	yesButton.attr("href", link.attr("href"));

	confirmText.text("Are you sure you want to update the status of the order ID #" + orderId + " to  " + orderStatus);
	cconfirmModal.modal();

}

