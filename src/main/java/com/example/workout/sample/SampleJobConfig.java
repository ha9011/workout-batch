package com.example.workout.sample;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SampleJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job sampleJob(Step sampleStep){
        return this.jobBuilderFactory.get("sampleJob")
                .incrementer(new RunIdIncrementer())
                .start(sampleStep)
                .build();
    }

    @Bean
    @JobScope
    public Step sampleStep(){
        return this.stepBuilderFactory.get("sampleStep")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("excoute sampleStep");
                    return RepeatStatus.FINISHED;
                }).build();

    }
}
