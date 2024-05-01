function doVote(currentLink, entityName) {
	requestURL = currentLink.attr("href");

	$.ajax({
		type: "POST",
		url: requestURL,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(voteResult) {
		console.log(voteResult);

		if (voteResult.successful) {
			$("#modalDialog").on("hide.bs.modal", function(e) {
				updateVoteCountAndIcons(currentLink, voteResult, entityName);
			});
		}

		showModalDialog("Vote " + entityName, voteResult.message);

	}).fail(function() {
		showErrorModal("Error voting " + entityName);
	});
}

function updateVoteCountAndIcons(currentLink, voteResult, entityName) {
	itemId = currentLink.attr(entityName + "Id");
	voteUpLink = $("#linkVoteUp-" + entityName + "-" + itemId);
	voteDownLink = $("#linkVoteDown-" + entityName + "-" + itemId);



	$("#voteCount-" + entityName + "-" + itemId).text(voteResult.voteCount + " Votes");

	message = voteResult.message;

	if (message.includes("successfully voted up")) {
		highlightVoteUpIcon(currentLink, voteDownLink, entityName);
		updateThumbsUpCount(itemId, voteResult, entityName);
		updateThumbsDownCount(itemId, voteResult, entityName);
	} else if (message.includes("successfully voted down")) {
		highlightVoteDownIcon(currentLink, voteUpLink, entityName);
		updateThumbsUpCount(itemId, voteResult, entityName);
		updateThumbsDownCount(itemId, voteResult, entityName);
	} else if (message.includes("unvoted down")) {
		unhighlightVoteDownIcon(voteDownLink, entityName);
		updateThumbsUpCount(itemId, voteResult, entityName);
		updateThumbsDownCount(itemId, voteResult, entityName);
	} else if (message.includes("unvoted up")) {
		unhighlightVoteDownIcon(voteUpLink, entityName);
		updateThumbsUpCount(itemId, voteResult, entityName);
		updateThumbsDownCount(itemId, voteResult, entityName);
	}
}

function highlightVoteUpIcon(voteUpLink, voteDownLink, entityName) {
	voteUpLink.removeClass("far").addClass("fas");
	voteUpLink.attr("title", "Undo vote up this " + entityName);
	voteDownLink.removeClass("fas").addClass("far");
}

function highlightVoteDownIcon(voteDownLink, voteUpLink, entityName) {
	voteDownLink.removeClass("far").addClass("fas");
	voteDownLink.attr("title", "Undo vote down this" + entityName);
	voteUpLink.removeClass("fas").addClass("far");
}

function unhighlightVoteDownIcon(voteDownLink, entityName) {
	voteDownLink.attr("title", "Vote down this " + entityName);
	voteDownLink.removeClass("fas").addClass("far");
}

function unhighlightVoteUpIcon(voteUpLink, entityName) {
	voteUpLink.attr("title", "Vote up this " + entityName);
	voteUpLink.removeClass("fas").addClass("far");
}

function updateThumbsUpCount(itemId, voteResult, entityName) {
		$("#linkThumbsUp-" + entityName + "-" + itemId).text(voteResult.positiveVoteCount);

}

function updateThumbsDownCount(itemId, voteResult, entityName) {
	$("#linkThumbsDown-" + entityName + "-" + itemId).text(voteResult.negativeVoteCount);

}


function displayVotesByCustomerNameAndReview(link) {
	var requestURL = link.attr("href");
	$.ajax({
		type: "GET",
		url: requestURL,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(names) {
		var html = "<ul>";
		if (names && names.length > 0) {
			$.each(names, function(index, name) {
				html += "<li>" + name + "</li>";
			});
		} else {
			html += "<li>No customers have voted</li>";
		}
		html += "</ul>";
		$("#customerListModal").find(".modal-body").html(html);
		$("#customerListModal").modal("show");
	}).fail(function() {
		showErrorModal("Error voting review.");
	});
}

function displayVotesByCustomerNameAndQuestion(link) {
	var requestURL = link.attr("href");
	$.ajax({
		type: "GET",
		url: requestURL,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(names, enityName) {
		var html = "<ul>";
		if (names && names.length > 0) {
			$.each(names, function(index, name) {
				html += "<li>" + name + "</li>";
			});
		} else {
			html += "<li>No customers have reacted</li>";
		}
		html += "</ul>";
		$("#customerQuestionModal").find(".modal-body").html(html);
		$("#customerQuestionModal").modal("show");
	}).fail(function() {
		showErrorModal("Error reacting question.");
	});
}
