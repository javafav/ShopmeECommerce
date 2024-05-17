package com.shopme.product;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.shopme.common.entity.product.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

	@Query("SELECT p FROM Product p WHERE p.enabled = true"
			+ " AND (p.category.id = ?1 OR p.category.allPaentIds LIKE %?2%)"
			+ " ORDER BY p.name ASC")
	public Page<Product> listProductByCategory(Integer categoryId, String IdMatch, Pageable pagebale);
	
	@Query("SELECT p FROM Product p WHERE p.enabled = true ORDER BY p.name ASC ")
	public Page<Product> listAllProduct(Pageable pagebale);
	
	@Query("SELECT p FROM Product p WHERE p.enabled = true AND p.averageRating >= ?1 ORDER BY p.name ASC ")
	public Page<Product> listAllMostRatedProduct(float avgRating, Pageable pagebale);
	
	@Query("SELECT p FROM Product p JOIN OrderDetail od ON od.product.id = p.id GROUP BY p.id HAVING SUM(od.quantity) >= ?1 ORDER BY p.name DESC")
	public Page<Product> listAllBestSellingProduct(Long quantity, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE  p.createdTime BETWEEN ?1 AND ?2 ")
    Page<Product> listAllProductsAddedLastXMonths(Date startTime, Date endTime,Pageable pageable);
	
	public Product findByAlias(String alias);
	
	@Query(value = "SELECT * FROM products p " + "JOIN brands b ON p.brand_id = b.id "
			+ "JOIN categories c ON p.category_id = c.id " + "WHERE p.enabled = true AND "
			+ "(MATCH(p.name, p.short_description, p.full_description) AGAINST (?1 IN NATURAL LANGUAGE MODE) "
			+ "OR b.name LIKE %?1% OR c.name LIKE %?1%)", nativeQuery = true, countQuery = "SELECT COUNT(*) FROM products p "
					+ "JOIN brands b ON p.brand_id = b.id " + "JOIN categories c ON p.category_id = c.id "
					+ "WHERE p.enabled = true AND "
					+ "(MATCH(p.name, p.short_description, p.full_description) AGAINST (?1 IN NATURAL LANGUAGE MODE) "
					+ "OR b.name LIKE %?1% OR c.name LIKE %?1%)")
	public Page<Product> search(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT p FROM Product p where p.enabled = true AND p.discountPercent > 0")
	public Page<Product> findAllProductOnSaleOrDiscount(Pageable pageable);
	

	@Query("Update Product p SET p.averageRating = COALESCE((SELECT AVG(r.rating) FROM Review r WHERE r.product.id = ?1), 0),"
			+ " p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id =?1) "
			+ "WHERE p.id = ?1")
	@Modifying
	public void updateReviewCountAndAverageRating(Integer productId);
}
