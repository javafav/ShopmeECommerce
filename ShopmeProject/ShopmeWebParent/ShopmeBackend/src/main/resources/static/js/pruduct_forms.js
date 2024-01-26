
$("#shortDescription").richText();
$("#fullDescription").richText()

dropdownBrands = $("#brand");
dropdownCategory = $("#category")

$(document).ready(function() {
	dropdownBrands.change(function() {
		dropdownCategory.empty();
		getCategories();
	});
	getCategories();


	$("input[name= 'extraImage']").each(function(index) {
		$(this).change(function() {
			showExtraImageThumbnail(this, index);
		});
	});


});

function showExtraImageThumbnail(fileInput, index) {
	var file = fileInput.files[0];
	var reader = new FileReader();
	reader.onload = function(e) {
		$("#extraThumbnail" + index).attr("src", e.target.result);
	}
	reader.readAsDataURL(file);
	addExtraImageSection(index + 1);
}

function addExtraImageSection(index) {
	htmlExtraImage = `
		
			<div class="col border m-2 p-2">
				<label id = "extraImageHeader${index}">Extra Image # ${index + 1}:</label>
				
				<div>
					<img class="img-fluid"  id="extraThumbnail${index}"
					     alt="Extra image # ${index + 1} preview" 
					     src="${defaultImageSrc}"/>
				</div>
				
				<div class="mt-2">
					<input class="m-2"  name="extraImage" type="file"
						accept="image/png, image/jpeg"
						onchange="showExtraImageThumbnail(this,${index})">
						
				</div>

			</div>
	`;
	
	htmlLinkRemove = `
	
	<a class='btn fa-regular fa-circle-xmark fa-2x icon-dark float-right'></a>
	
	`
	;
	$("#divProductImages").append(htmlExtraImage);
	$("#extraImageHeader"+ (index-1) ).append(htmlLinkRemove);
}


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
	url = "[[@{/products/check_unique}]]";
	productId = $("#id").val();
	productName = $("#name").val();
	csrfValue = $("input[name = '_csrf']").val();

	params = { id: productId, name: productName, _csrf: csrfValue };

	$.post(url, params, function(response) {
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




function showModalDialog(title, message) {
	$("#modalTitle").text(title);
	$("#modalBody").text(message);
	$("#modalDialog").modal();

}

function showWarningDialog(message) {
	showModalDialog("Warning", message)
}
function showErrorDialog(message) {
	showModalDialog("Error", message)
}