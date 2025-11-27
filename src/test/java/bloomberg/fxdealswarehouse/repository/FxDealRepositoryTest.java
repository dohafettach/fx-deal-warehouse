package bloomberg.fxdealswarehouse.repository;


import bloomberg.fxdealswarehouse.entity.FxDeal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FxDealRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FxDealRepository fxDealRepository;

    @Test
    void existsByDealId_WhenDealExists_ShouldReturnTrue() {
        FxDeal deal = new FxDeal(
                "DEAL001",
                "USD",
                "MAD",
                LocalDateTime.now(),
                new BigDecimal("1000.50")
        );
        entityManager.persist(deal);
        entityManager.flush();

        boolean exists = fxDealRepository.existsByDealId("DEAL001");

        assertTrue(exists);
    }

    @Test
    void existsByDealId_WhenDealDoesNotExist_ShouldReturnFalse() {
        boolean exists = fxDealRepository.existsByDealId("NONEXISTENT");

        assertFalse(exists);
    }

    @Test
    void save_ShouldPersistDeal() {
        FxDeal deal = new FxDeal(
                "DEAL002",
                "MAD",
                "JPY",
                LocalDateTime.now(),
                new BigDecimal("5000.00")
        );

        FxDeal saved = fxDealRepository.save(deal);

        assertNotNull(saved);
        assertEquals("DEAL002", saved.getDealId());
        assertEquals("MAD", saved.getFromCurrency());
        assertEquals("JPY", saved.getToCurrency());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void findAll_ShouldReturnAllDeals() {
        FxDeal deal1 = new FxDeal("DEAL003", "USD", "MAD", LocalDateTime.now(), new BigDecimal("1000"));
        FxDeal deal2 = new FxDeal("DEAL004", "MAD", "JPY", LocalDateTime.now(), new BigDecimal("2000"));

        entityManager.persist(deal1);
        entityManager.persist(deal2);
        entityManager.flush();

        var deals = fxDealRepository.findAll();

        assertEquals(2, deals.size());
    }
}