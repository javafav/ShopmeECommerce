var extraImagesCount = 0;


$(document).ready(function() {

	$("input[name='extraImage']").each(function(index) {
		extraImagesCount++;
		$(this).change(function() {
			if (!checkFileSize(this)) {
				return;
			}

			showExtraImageThumbnail(this, index);
		});
	});

	$("a[name= 'removeImageLink']").each(function(index) {
		$(this).click(function() {
			removeExtraImage(index);
		});
	});



});

function showExtraImageThumbnail(fileInput, index) {
	var file = fileInput.files[0];
	var reader = new FileReader();
	  fileName = file.name;
	 imageNameHiddenField = $("#imageName" + index);
	 
	 if (imageNameHiddenField.length) {
		imageNameHiddenField.val(fileName);
	}
	 
	reader.onload = function(e) {
		$("#extraThumbnail" + index).attr("src", e.target.result);
	}
	reader.readAsDataURL(file);
	if (index >= extraImagesCount - 1) {
		addNextExtraImageSection(index + 1);
	}
}

function addNextExtraImageSection(index) {
	htmlExtraImage = `
		
			<div class="col border m-3 p-2" id="divExtraImage${index}">
			<div id="extraImageHeader${index}"><label>Extra Image # ${index + 1}:</label></div>
				
				<div>
					<img class="img-fluid product-images"  id="extraThumbnail${index}"
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
  <a class="btn fa-regular fa-circle-xmark fa-2x icon-dark float-right" 
     href="javascript:removeExtraImage(${index - 1})" 
     title = "Remove this image"></a>
`;
	$("#divProductImages").append(htmlExtraImage);
	$("#extraImageHeader" + (index - 1)).append(htmlLinkRemove);
	extraImagesCount++;
}

function removeExtraImage(index) {
	$("#divExtraImage" + index).remove();
}

