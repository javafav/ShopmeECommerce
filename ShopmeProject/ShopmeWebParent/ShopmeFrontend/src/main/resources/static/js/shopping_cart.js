$(document).ready(function() {
	
	$(".linkMinus").on("click", function(evt) {
		evt.preventDefault();
		
		decreaseQuantity($(this));
		
	});
	
	$(".linkPlus").on("click", function(evt) {
		evt.preventDefault();
		
		increaseQuantity($(this));
		
	});	
	
	$(".linkRemove").on("click", function(evt) {
		
		evt.preventDefault();
		
		removeProduct($(this));
		
	
		
	});
});

function decreaseQuantity(link) {
	productId = link.attr("pid");
	quantityInput = $("#quantity" + productId);
	newQuantity = parseInt(quantityInput.val()) - 1;
	
	if (newQuantity > 0) {
	
		quantityInput.val(newQuantity);
		updateQuantity(productId, newQuantity);
		
	} else {
		showWarningModal('Minimum quantity is 1');
	}	
}

function increaseQuantity(link) {
		productId = link.attr("pid");
		quantityInput = $("#quantity" + productId);
		newQuantity = parseInt(quantityInput.val()) + 1;
		
		if (newQuantity <= 5) {
			
			quantityInput.val(newQuantity);
			updateQuantity(productId, newQuantity);
			
		} else {
			showWarningModal('Maximum quantity is 5');
		}	
}

function updateQuantity(productId, quantity) {
	url = contextPath + "cart/update/" + productId + "/" + quantity;
	
	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(updatedSubtotal) {
		updateSubtotal(updatedSubtotal, productId);
		totalCartItem();
		updateTotal();
	}).fail(function() {
		showErrorModal("Error while updating product quantity.");
	});	
}

function updateSubtotal(updatedSubtotal, productId) {
	formattedSubtotal = $.number(updatedSubtotal, 2);
	$("#subtotal" + productId).text(formattedSubtotal);
}

function updateTotal() {
	total = 0.0;
	
	$(".subtotal").each(function(index, element) {
		total += parseFloat(element.innerHTML.replaceAll(",", ""));
	});
	
	formattedTotal = $.number(total, 2);
	$("#total").text(formattedTotal);
}

function removeProduct(link) {
	url = link.attr("href");

	$.ajax({
		type: "DELETE",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(responseJSON) {
	 rowNumber = link.attr("rowNumber")
	 removeProductHTML(rowNumber);
	 updateTotal();
	 updateCountNumber();
	 totalCartItem();
	showModalDialog("Shopping Cart", responseJSON);
	}).fail(function() {
		showErrorModal("Error while removing product .");
	});

}

function removeProductHTML(rowNumber){
	$("#row" + rowNumber).remove();
}
function removeProductHTML(rowNumber) {
	$("#row" + rowNumber).remove();
	$("#blankLine" + rowNumber).remove();
}

function updateCountNumbers() {
	$(".divCount").each(function(index, element) {
		element.innerHTML = "" + (index + 1);
	}); 
}