var cartItem ;
$(document).ready(function() {
	cartItem = $("#cartItems");
	totalCartItem();
});

function totalCartItem() {

	url = contextPath + "cart/totalItems";


	$.ajax({
		type: "GET",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response) {
		if(response == 'null'){
		cartItem.text("");
 }else{
	 cartItem.text(response);
 }

	}).fail(function() {
		showErrorModal("Error while  updateding cart items");
	});
}
