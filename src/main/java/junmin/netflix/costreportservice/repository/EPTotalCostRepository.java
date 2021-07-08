package junmin.netflix.costreportservice.repository;

import junmin.netflix.costreportservice.model.EPTotalCostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EPTotalCostRepository extends JpaRepository<EPTotalCostEntity, Long> {
	public List<EPTotalCostEntity> findByProdNameAndEpCode(String prodName, String epCode);
	public List<EPTotalCostEntity> findByProdName(String prodName);
}
