

dropdownBrands = $("#brand");
dropdownCategory = $("#category")

$(document).ready(function() {
	
	
$("#shortDescription").richText();
$("#fullDescription").richText();
	
	dropdownBrands.change(function() {
		dropdownCategory.empty();
		getCategories();
	});
	getCategories();


});



function getCategories() {
	brandId = dropdownBrands.val();
	url = brandModuleURL + "/" + brandId + "/categories";

	$.get(url, function(responseJSON) {
		$.each(responseJSON, function(index, category) {
			$("<option>").val(category.id).text(category.name).appendTo(dropdownCategory);
		});
	});
}

function checkUnique(form) {
	
	productId = $("#id").val();
	productName = $("#name").val();
	csrfValue = $("input[name = '_csrf']").val();

	params = { id: productId, name: productName, _csrf: csrfValue };

	$.post(checkUniqueProductURL, params, function(response) {
		if (response == 'OK') {
			form.submit();
		} else if (response == 'Duplicate') {
			showWarningDialog("There is another product exisits with the name: " + productName);
		} else {
			showErrorDialog("Unknown response from server")
		}

	}).fail(function() {
		showErrorDialog("Could not connect to the server");

	});
	return false;
}
