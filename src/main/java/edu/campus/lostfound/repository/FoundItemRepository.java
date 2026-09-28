package edu.campus.lostfound.repository;

import edu.campus.lostfound.model.FoundItem;
import edu.campus.lostfound.model.FoundStatus;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoundItemRepository extends JpaRepository<FoundItem, Long> {
	List<FoundItem> findAllByStatusOrderByFoundDateDesc(FoundStatus status);
	long countByStatus(FoundStatus status);

	@Query("select f from FoundItem f join fetch f.category join fetch f.staff where f.status = :status")
	List<FoundItem> findAllWithCategoryByStatus(FoundStatus status);
}