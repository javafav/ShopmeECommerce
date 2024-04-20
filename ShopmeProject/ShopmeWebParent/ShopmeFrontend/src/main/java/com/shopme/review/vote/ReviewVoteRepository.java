package com.shopme.review.vote;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ReviewVote;

public interface ReviewVoteRepository extends JpaRepository<ReviewVote, Integer> {

	@Query("SELECT v FROM ReviewVote v WHERE v.review.id = ?1 AND v.customer.id = ?2")
	public ReviewVote findByReviewAndCustomer(Integer reviewId, Integer customerId);
	
	@Query("SELECT v FROM ReviewVote v WHERE v.review.product.id = ?1 AND v.customer.id = ?2")
	public List<ReviewVote> findByProductAndCustomer(Integer productId, Integer customerId);	
	
	@Query("SELECT CONCAT(c.firstName, ' ', c.lastName) AS fullName FROM Customer c JOIN ReviewVote v ON c.id = v.customer.id WHERE v.review.id = ?1")
	public List<String> findCustomerFullNamesByReviewVote(Integer reviewVoteId);
	
	@Query("SELECT SUM(CASE WHEN v.votes > 0 AND v.review.id = ?1 THEN v.votes ELSE 0 END) FROM ReviewVote v")
	public int sumPositiveValues(Integer reviewId);

	@Query("SELECT SUM( ABS(CASE WHEN v.votes < 0 AND v.review.id = ?1 THEN v.votes ELSE 0 END)) FROM ReviewVote v")
	 public int sumNegativeValues(Integer reviewId);

}
