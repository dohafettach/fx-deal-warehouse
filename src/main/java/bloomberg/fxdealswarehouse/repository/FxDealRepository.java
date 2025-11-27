package bloomberg.fxdealswarehouse.repository;

import bloomberg.fxdealswarehouse.entity.FxDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FxDealRepository extends JpaRepository<FxDeal, String> {
    boolean existsByDealId(String dealId);
}
