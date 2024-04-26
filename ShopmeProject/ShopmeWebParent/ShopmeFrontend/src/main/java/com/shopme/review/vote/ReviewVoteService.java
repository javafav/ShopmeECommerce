package com.shopme.review.vote;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.ReviewVote;
import com.shopme.review.ReviewRepository;
import com.shopme.vote.VoteResult;
import com.shopme.vote.VoteType;

@Service
@Transactional
public class ReviewVoteService {
	
	@Autowired private ReviewRepository reviewRepo;
	@Autowired private ReviewVoteRepository voteRepo;
	
	public VoteResult undoVote(ReviewVote vote, Integer reviewId, VoteType voteType) {
		
		voteRepo.delete(vote);
		reviewRepo.updateVoteCount(reviewId);
		
		reviewRepo.updatePositiveVotes(reviewId);
		reviewRepo.updateNegativeVotes(reviewId);
		
		Integer voteCount = reviewRepo.getVoteCount(reviewId);
		Integer positiveVoteCount = reviewRepo.getPositiveVoteCount(reviewId);
		Integer negativeVotesCount = reviewRepo.getNegativeVotesCount(reviewId);
		
		// System.out.println("Undo Vote:  Vote Count: " + voteCount + " form ReviewId: " + reviewId );
		// System.out.println("Undo Vote: Postive Vote : " + positiveVoteCount + " Negitve Vote: " + negativeVotesCount +  " form ReviewId: " + reviewId );
		
		return VoteResult.success("You have unvoted " + voteType + " that review.", voteCount, positiveVoteCount, negativeVotesCount);
	}
	
	public VoteResult doVote(Integer reviewId, Customer customer, VoteType voteType) {
		Review review = null;
		
		try {
			review = reviewRepo.findById(reviewId).get();
		} catch (NoSuchElementException ex) {
			return VoteResult.fail("The review ID " + reviewId + " no longer exists.");
		}
		
		ReviewVote vote = voteRepo.findByReviewAndCustomer(reviewId, customer.getId());
		
		if (vote != null) {
			if (vote.isUpvoted() && voteType.equals(VoteType.UP) || 
					vote.isDownvoted() && voteType.equals(VoteType.DOWN)) {
				return undoVote(vote, reviewId, voteType);
			} else if (vote.isUpvoted() && voteType.equals(VoteType.DOWN)) {
				vote.voteDown();
			} else if (vote.isDownvoted() && voteType.equals(VoteType.UP)) {
				vote.voteUp();
			}
		} else {
			vote = new ReviewVote();
			vote.setCustomer(customer);
			vote.setReview(review);
			
			if (voteType.equals(VoteType.UP)) {
				vote.voteUp();
			} else {
				vote.voteDown();
			}
		}
		
		voteRepo.save(vote);
		reviewRepo.updateVoteCount(reviewId);
		reviewRepo.updatePositiveVotes(reviewId);
		reviewRepo.updateNegativeVotes(reviewId);
		
		Integer voteCount = reviewRepo.getVoteCount(reviewId);
		Integer positiveVoteCount = reviewRepo.getPositiveVoteCount(reviewId);
		Integer negativeVotesCount = reviewRepo.getNegativeVotesCount(reviewId);
	
     //	System.out.println("Undo Vote:  Vote Count: " + voteCount + " form ReviewId: " + reviewId );
	//	System.out.println("Do Vote: Postive Vote : " + positiveVoteCount + " Negitve Vote: " + negativeVotesCount +  " form ReviewId: " + reviewId );
		
		return VoteResult.success("You have successfully voted " + voteType + " that review.", 
				voteCount, positiveVoteCount, negativeVotesCount);
	}
	
	public void markReviewsVotedForProductByCustomer(List<Review> listReviews, Integer productId,
			Integer customerId) {
		List<ReviewVote> listVotes = voteRepo.findByProductAndCustomer(productId, customerId);
		
		for (ReviewVote vote : listVotes) {
			Review votedReview = vote.getReview();
			
			if (listReviews.contains(votedReview)) {
				int index = listReviews.indexOf(votedReview);
				Review review = listReviews.get(index);
				
				if (vote.isUpvoted()) {
					review.setUpvotedByCurrentCustomer(true);
				} else if (vote.isDownvoted()) {
					review.setDownvotedByCurrentCustomer(true);
				}
			}
		}
	}

	  public List<String> findCustomersWhoVotedByReviewId(int  reviewId) {
	        return voteRepo.findCustomerFullNamesByReviewId(reviewId);
	    }
	  

}
