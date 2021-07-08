package junmin.netflix.costreportservice.repository;

import junmin.netflix.costreportservice.model.EPItemizedCostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EPItemizedCostRepository extends JpaRepository<EPItemizedCostEntity, Long> {
}
