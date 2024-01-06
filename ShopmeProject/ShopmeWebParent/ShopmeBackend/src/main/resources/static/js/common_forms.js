$(document)
				.ready(
						function() {
							$("#buttonCancel").on("click", function() {
								window.location = moduleURL;
							});

							$("#fileimage")
									.on(
											"change",
											function() {
												fileSize = this.files[0].size;
												if (fileSize > 848576) {
													this
															.setCustomValidity("You must choose file less then 1MB!");
													this.reportValidity();
												} else {
													this.setCustomValidity("");

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