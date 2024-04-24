package com.shopme.review;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


import com.shopme.common.entity.Review;
import com.shopme.common.entity.product.Product;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

	@Query("SELECT r FROM Review r WHERE r.customer.id = ?1")
	public Page<Review> findByCustomer(Integer customerId, Pageable pageable);

	@Query("SELECT r FROM Review r WHERE r.customer.id = ?1 AND (" + "r.headline LIKE %?2% OR r.comment LIKE %?2% OR "
			+ "r.product.name LIKE %?2%)")
	public Page<Review> findByCustomer(Integer customerId, String keyword, Pageable pageable);

	@Query("SELECT r FROM Review r WHERE r.customer.id = ?1 AND r.id = ?2")
	public Review findByCustomerAndId(Integer customerId, Integer reviewId);

	public Page<Review> findByProduct(Product product, Pageable pageable);

	@Query("SELECT COUNT(r.id) FROM Review r WHERE r.customer.id = ?1 AND r.product.id = ?2")
	public Long countByCustomerAndProduct(Integer customerId, Integer productId);

	@Query("UPDATE Review r SET r.votes = (SELECT COALESCE(ABS(SUM(v.votes)), 0) FROM ReviewVote v WHERE v.review.id = ?1) WHERE r.id = ?1")
	@Modifying
	public void updateVoteCount(Integer reviewId);

	@Query("SELECT r.votes FROM Review r WHERE r.id = ?1")
	public Integer getVoteCount(Integer reviewId);

	@Modifying
	@Query("UPDATE Review r SET r.positiveVotes = (SELECT COALESCE(ABS(SUM(CASE WHEN v.votes > 0 THEN v.votes ELSE 0 END)), 0) FROM ReviewVote v "
	        + "WHERE v.review.id = r.id) WHERE r.id = ?1")
	void updatePositiveVotes(Integer reviewId);



	@Query("SELECT r.positiveVotes FROM Review r WHERE r.id = ?1")
	public Integer getPositiveVoteCount(Integer reviewId);

	@Modifying
	@Query("UPDATE Review r SET r.negativeVotes = (SELECT COALESCE(ABS(SUM(CASE WHEN v.votes < 0 THEN v.votes ELSE 0 END)), 0) FROM ReviewVote v "
			+ " WHERE v.review.id = r.id) WHERE r.id = ?1")
	void updateNegativeVotes(Integer reviewId);
    
	@Query("SELECT r.negativeVotes FROM Review r WHERE r.id = ?1")
	public Integer getNegativeVotesCount(Integer reviewId);

	@Query("SELECT CONCAT(c.firstName, ' ', c.lastName) AS fullName FROM Customer c JOIN ReviewVote v ON c.id = v.customer.id WHERE v.review.id = ?1")
	public List<String> findCustomerFullNamesByReviewVote(Integer reviewVoteId);
}
