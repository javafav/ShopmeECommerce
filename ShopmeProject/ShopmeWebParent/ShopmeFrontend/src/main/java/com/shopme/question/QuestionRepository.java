package com.shopme.question;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

	@Query("SELECT q FROM Question q WHERE q.approved = true AND q.product.id = ?1")
	Page<Question> findAll(Integer productId, Pageable pageable);

	@Query("SELECT q FROM Question q WHERE q.approved = true AND q.product.alias = ?1")
	Page<Question> findByAlias(String alias, Pageable pageable);
	
	@Query("SELECT COUNT (q) FROM Question q WHERE q.approved = true AND q.answer IS NOT NULL and q.product.id =?1")
	int countAnsweredQuestions(Integer productId);
	
	@Query("SELECT COUNT (q) FROM Question q WHERE q.approved = true and q.product.id =?1")
	int countApprovedQuestions(Integer productId);

	@Query("UPDATE Question q SET q.votes = (SELECT COALESCE(SUM(ABS(v.votes)), 0) FROM QuestionVote v WHERE v.question.id = ?1) WHERE q.id = ?1")
	//@Query("UPDATE Review r SET r.votes = (SELECT COALESCE(ABS(SUM(v.votes)), 0) FROM ReviewVote v WHERE v.review.id = ?1) WHERE r.id = ?1")
	@Modifying
	public void updateVoteCount(Integer questionId);
	
	@Query("SELECT q.votes FROM Question q WHERE q.id = ?1")
	public Integer getVoteCount(Integer questionId);	
	
	@Query("SELECT q FROM Question q WHERE q.asker.id = ?1")
	Page<Question> findByCustomer(Integer customerId, Pageable pageable);
	
	@Query("SELECT q FROM Question q WHERE q.asker.id = ?1 AND ("
			+ "q.questionContent LIKE %?2% OR "
			+ "q.answer LIKE %?2% OR q.product.name LIKE %?2%)")
	Page<Question> findByCustomer(Integer customerId, String keyword, Pageable pageable);
	
	@Query("SELECT q FROM Question q WHERE q.asker.id = ?1 AND q.id = ?2")
	Question findByCustomerAndId(Integer customerId, Integer questionId);
	
	
	@Modifying
	@Query("UPDATE Question q SET q.positiveVotes = (SELECT COALESCE(SUM(CASE WHEN v.votes > 0 THEN v.votes ELSE 0 END), 0) FROM QuestionVote v "
	        + "WHERE v.question.id = q.id) WHERE q.id = ?1")
	void updatePositiveVotes(Integer questionId);



	@Query("SELECT q.positiveVotes FROM Question q WHERE q.id = ?1")
	public Integer getPositiveVoteCount(Integer questionId);

	@Modifying
	@Query("UPDATE Question q SET q.negativeVotes = (SELECT COALESCE(ABS(SUM(CASE WHEN v.votes < 0 THEN v.votes ELSE 0 END)), 0) FROM QuestionVote v "
			+ " WHERE v.question.id = q.id) WHERE q.id = ?1")
	void updateNegativeVotes(Integer questionId);
    
	@Query("SELECT q.negativeVotes FROM Question q WHERE q.id = ?1")
	public Integer getNegativeVotesCount(Integer questionId);

	
	@Query("SELECT DISTINCT  CONCAT(c.firstName, ' ', c.lastName) AS fullName FROM Customer c JOIN QuestionVote v ON c.id = v.customer.id WHERE v.question.id = ?1")
	public List<String> findCustomerFullNamesByQuestionId(int reviewId);

}
