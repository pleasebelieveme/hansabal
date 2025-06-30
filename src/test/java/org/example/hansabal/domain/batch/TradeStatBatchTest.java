package org.example.hansabal.domain.batch;

import org.example.hansabal.domain.admin.entity.ProductTradeStatDaily;
import org.example.hansabal.domain.batch.repository.ProductTradeStatDailyRepository;
import org.example.hansabal.domain.product.entity.Product;
import org.example.hansabal.domain.product.entity.ProductStatus;
import org.example.hansabal.domain.product.repository.ProductRepository;
import org.example.hansabal.domain.trade.entity.RequestStatus;
import org.example.hansabal.domain.trade.entity.Trade;
import org.example.hansabal.domain.trade.entity.TradeStatus;
import org.example.hansabal.domain.trade.repository.TradeRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.entity.UserStatus;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.batch.job.names=ProductTradeStatJob")
@SpringBatchTest
class TradeStatBatchTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private Job productTradeStatJob;

    @BeforeEach
    void setUpJob() {
        jobLauncherTestUtils.setJob(productTradeStatJob);
    }

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductTradeStatDailyRepository productTradeStatDailyRepository;

    @BeforeEach
    void setup() {
        productTradeStatDailyRepository.deleteAll();
        tradeRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        User user = userRepository.save(
                User.builder()
                        .email("test@example.com")
                        .name("테스트 사용자")
                        .nickname("tester")
                        .password("encodedPassword")
                        .userRole(UserRole.USER)
                        .userStatus(UserStatus.ACTIVE)
                        .build()
        );

        User writer = userRepository.save(
                User.builder()
                        .email("writer@example.com")
                        .name("작성자")
                        .nickname("작성자")
                        .password("encodedPassword")
                        .userRole(UserRole.USER)
                        .userStatus(UserStatus.ACTIVE)
                        .build()
        );
        Product product = productRepository.save(
                Product.builder()
                        .name("테스트상품")
                        .quantity(10)
                        .productStatus(ProductStatus.FOR_SALE)
                        .user(user)
                        .build()
        );

        tradeRepository.save(
                Trade.builder()
                        .title("테스트 거래")
                        .contents("테스트 내용")
                        .price(1000L)
                        .totalPrice(1000)
                        .isOccupied(false)
                        .writer(writer)
                        .status(TradeStatus.FINISHED)
                        .restatus(RequestStatus.DONE)
                        .product(product)
                        .build()
        );
    }

    @Test
    void tradeStatJob_원시데이터_가공_통계_생성_검증() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        List<ProductTradeStatDaily> stats = productTradeStatDailyRepository.findAll();
        assertThat(stats).hasSize(1);
    }
}
