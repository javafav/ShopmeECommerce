$(document)
	.ready(function() {
			$("#buttonCancel").on("click", function() {
				window.location = moduleURL;
			});
 
			$("#fileImage").change(function() {
				if(!checkFileSize(this)){
					return;
				}else{
					showThumbnail(this);
				}

					});

		});
		
		 
function showThumbnail(fileInput) {
	var file = fileInput.files[0];
	var reader = new FileReader();
	reader.onload = function(e) {
		$("#thumbnail").attr("src", e.target.result);
	}
	reader.readAsDataURL(file);
}


function checkFileSize(fileInput){
	fileSize = fileInput.files[0].size;
						if (fileSize >  MAX_FILE_SIZE) {
						 MB_SIZE = Math.floor(MAX_FILE_SIZE / 1000000);
							fileInput.setCustomValidity("You must choose file less then" + MB_SIZE + " MB" );
							fileInput.reportValidity();
							return false;
						} else {
							fileInput.setCustomValidity("");
							
							return true;

						}
	
}


function showModalDialog(title, message) {
	$ ("#modalTitle").text(title);
	$("#modalBody").text(message);
	$("#modalDialog").modal();

}

function showWarningDialog(message) {
	showModalDialog("Warning", message)
}
function showErrorDialog(message) {
	showModalDialog("Error", message)
}
