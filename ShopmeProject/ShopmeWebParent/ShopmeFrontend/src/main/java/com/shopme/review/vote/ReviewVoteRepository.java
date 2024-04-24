package com.shopme.review.vote;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.ReviewVote;

public interface ReviewVoteRepository extends JpaRepository<ReviewVote, Integer> {

	@Query("SELECT v FROM ReviewVote v WHERE v.review.id = ?1 AND v.customer.id = ?2")
	public ReviewVote findByReviewAndCustomer(Integer reviewId, Integer customerId);
	
	@Query("SELECT v FROM ReviewVote v WHERE v.review.product.id = ?1 AND v.customer.id = ?2")
	public List<ReviewVote> findByProductAndCustomer(Integer productId, Integer customerId);	
	
	@Query("SELECT DISTINCT  CONCAT(c.firstName, ' ', c.lastName) AS fullName FROM Customer c JOIN ReviewVote v ON c.id = v.customer.id WHERE v.review.id = ?1")
	public List<String> findCustomerFullNamesByReviewId(int reviewId);
	
	//public List<ReviewVote> findAllByReviewId(int reviewId);


}
