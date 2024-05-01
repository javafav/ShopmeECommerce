var thumbsUpCount;
var unorderdListForCustomers;

$(document).ready(function() {
   unorderdListForCustomers = $("#unorderdListForCustomers")
	$(".linkVoteReview").on("click", function(e) {
		e.preventDefault();
		voteReview($(this));
	});

});


function voteReview(currentLink) {

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
				updateVoteCountAndIcons(currentLink, voteResult);
			});
		}

		showModalDialog("Vote Review", voteResult.message);

	}).fail(function() {
		showErrorModal("Error voting review.");
	});
}

function updateVoteCountAndIcons(currentLink, voteResult) {
	reviewId = currentLink.attr("reviewId");
	voteUpLink = $("#linkVoteUp-review-" + reviewId);
	voteDownLink = $("#linkVoteDown-review-" + reviewId);




	$("#voteCount-" + reviewId).text(voteResult.voteCount + " Votes");


	message = voteResult.message;

	if (message.includes("successfully voted up")) {
		highlightVoteUpIcon(currentLink, voteDownLink);
		updateThumbsUpCount(reviewId, voteResult);
		updateThumbsDownCount(reviewId, voteResult);

	} else if (message.includes("successfully voted down")) {
		highlightVoteDownIcon(currentLink, voteUpLink);
		updateThumbsUpCount(reviewId, voteResult);
		updateThumbsDownCount(reviewId, voteResult);


	} else if (message.includes("unvoted down")) {
		unhighlightVoteDownIcon(voteDownLink);
		updateThumbsUpCount(reviewId, voteResult);
		updateThumbsDownCount(reviewId, voteResult);

	} else if (message.includes("unvoted up")) {
		unhighlightVoteDownIcon(voteUpLink);
		updateThumbsUpCount(reviewId, voteResult);
		updateThumbsDownCount(reviewId, voteResult);

	}
}

function highlightVoteUpIcon(voteUpLink, voteDownLink) {
	voteUpLink.removeClass("far").addClass("fas");
	voteUpLink.attr("title", "Undo vote up this review");
	voteDownLink.removeClass("fas").addClass("far");
}

function highlightVoteDownIcon(voteDownLink, voteUpLink) {
	voteDownLink.removeClass("far").addClass("fas");
	voteDownLink.attr("title", "Undo vote down this review");
	voteUpLink.removeClass("fas").addClass("far");
}

function unhighlightVoteDownIcon(voteDownLink) {
	voteDownLink.attr("title", "Vote down this review");
	voteDownLink.removeClass("fas").addClass("far");
}

function unhighlightVoteUpIcon(voteUpLink) {
	voteUpLink.attr("title", "Vote up this review");
	voteUpLink.removeClass("fas").addClass("far");
}

function updateThumbsUpCount(reviewId, voteResult) {
	$("#linkThumbsUp-" + reviewId).text(voteResult.positiveVoteCount);

}

function updateThumbsDownCount(reviewId, voteResult) {
	$("#linkThumbsDown-" + reviewId).text(voteResult.negativeVoteCount);

}