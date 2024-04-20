var thumbsUpCount;

$(document).ready(function() {

	$(".linkVoteReview").on("click", function(e) {
		e.preventDefault();
		voteReview($(this));
	});
$(".linkVoteReviewCount").hover(function() {
    // Mouse enters the element
    voteCount($(this));
}, function() {

});
});

function voteCount(link) {
    var reviewId = link.attr("reviewId");
    var requestURL = contextPath + 'vote_review/count/' + reviewId;

    $.ajax({
        type: "GET",
        url: requestURL,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function(response) {
 console.log(response);
        if (Array.isArray(response)) {
            // Convert the response array to a string
            var customerNames = response.join(' ');
            // Set the tooltip content to the customer names
            link.attr('title', customerNames);
            // Initialize and show the tooltip
            link.tooltip().tooltip('show');
        } else {
            showErrorModal("Invalid response format");
        }
    }).fail(function(response) {
        showErrorModal("Failed");
    });
}

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
	voteUpLink = $("#linkVoteUp-" + reviewId);
	voteDownLink = $("#linkVoteDown-" + reviewId);
	
	voteThmbsUp = $("#thumbsUp-" + reviewId);
	
	
	$("#voteCount-" + reviewId).text(voteResult.voteCount + " Votes");
	
	message = voteResult.message;
	
	if (message.includes("successfully voted up")) {
		highlightVoteUpIcon(currentLink, voteDownLink);
      voteThmbsUp.text("Test: " + reviewId);
	} else if (message.includes("successfully voted down")) {
		highlightVoteDownIcon(currentLink, voteUpLink);
		
	} else if (message.includes("unvoted down")) {
		unhighlightVoteDownIcon(voteDownLink);
	} else if (message.includes("unvoted up")) {
		unhighlightVoteDownIcon(voteUpLink);
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

function updateThumbsUpCount(reviewId ,voteResult){
	$("#thumbsUp-" + reviewId).text(voteResult.sumOfPositiveVoteCount);
}