package com.example.workout.job.pass;

import com.example.workout.repository.pass.PassEntity;
import com.example.workout.repository.pass.PassStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ExpirePassesJobConfig {
    private final int CHUNK_SIZE = 10;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job expirePassesJob(){
        return this.jobBuilderFactory.get("expirePassesJob")
                .incrementer(new RunIdIncrementer())
                .start(expirePassesStep())
                .build();

    }

    @Bean
    @JobScope
    public Step expirePassesStep(){
        return this.stepBuilderFactory.get("expirePassesStep")
                .<PassEntity, PassEntity>chunk(CHUNK_SIZE)
                .reader(expirePassesItemReader())
                .processor(expirePassesItemProcessor())
                .writer(expirePassesItemWriter())
                .build();
    }

    /*
    * JpaCursorItemReader: JpaPagingItemReader만 지원하다가 Spring 4.3에서 추가되었습니다.
    * 페이징 기법보다 보다 높은 성능으로, 데이터 변경에 무관한 무결성 조회가 가능합니다.
    * */
    @Bean
    @StepScope
    public JpaCursorItemReader<PassEntity> expirePassesItemReader(){
        return new JpaCursorItemReaderBuilder<PassEntity>()
                .name("expirePassesItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT p FROM PassEntity p where p.status = :status and p.endedAt <= :endedAt")
                .parameterValues(Map.of("status", PassStatus.PROGRESSED, "endedAt", LocalDateTime.now()))
                .build();
            // p.status = :status <- 이것 떄문에 JpaCursorItemReader를 씀
            // paging방식으로 할 경우, 변경된 걸 또 가져올 수 가 있음, 리스크가 존재
            // 차라리 1개씩 처리하는 cursor방식이 나을 수 있음
    }
            // TODO 아래 같은 방식도 있음.

            //        @StepScope
            //        @Bean // RepositoryItemReader -> json이냐, JDBC, Repository냐에 따라 다름
            //        public RepositoryItemReader<Orders> trOrdersReader() {
            //            return new RepositoryItemReaderBuilder<Orders>()
            //                    .name("trOrdersReader")
            //                    .repository(ordersRepository)
            //                    .methodName("findAll")
            //                    .pageSize(5)// chunk size와 동일하게
            //                    //.arguments(Arrays.of())// 파라미터 있는 메소드면 필요함, 없는데도 필수인지 모르겠음
            //                    .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
            //                    .build();
            //        }



    @Bean
    @StepScope
    public ItemProcessor<PassEntity,PassEntity> expirePassesItemProcessor(){
        return passEntity ->{
            passEntity.setStatus(PassStatus.EXPIRED);
            passEntity.setExpiredAt(LocalDateTime.now());
            return passEntity;
        };
    }

    @Bean
    @StepScope
    public JpaItemWriter<PassEntity> expirePassesItemWriter(){
    return new JpaItemWriterBuilder<PassEntity>()
            .entityManagerFactory(entityManagerFactory)
            .build();
    }
}
