package org.example.hansabal.domain.users.batch.config;

import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserStatus;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DormantUserJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final UserRepository userRepository;

    @Bean
    public Job markDormantUserJob(Step markDormantUserStep) {
        return new JobBuilder("markDormantUserJob", jobRepository)
                .start(markDormantUserStep)
                .build();
    }

    @Bean
    public Step markDormantUserStep() {
        return new StepBuilder("markDormantUserStep", jobRepository)
                .<User, User>chunk(100, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public ItemReader<User> reader() {
        LocalDateTime threshold = LocalDateTime.now().minusYears(1);
        return new IteratorItemReader<>(
                userRepository.findAllByLastLoginAtBeforeAndUserStatus(threshold, UserStatus.ACTIVE)
        );
    }

    @Bean
    public ItemProcessor<User, User> processor() {
        return user -> {
            user.markAsDormant();
            return user;
        };
    }

    @Bean
    public ItemWriter<User> writer() {
        return users -> userRepository.saveAll(users);
    }
}
