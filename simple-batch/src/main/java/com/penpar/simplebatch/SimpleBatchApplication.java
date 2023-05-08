package com.penpar.simplebatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableBatchProcessing
@SpringBootApplication
public class SimpleBatchApplication {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	SimpleBatchApplication(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}

    public static void main(String[] args) {
        SpringApplication.run(SimpleBatchApplication.class, args);
    }
    
    @Bean
    public Tasklet tasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("Hello, Spring Batch!"); // 간단한 작업 수행
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get("step")
            .tasklet(tasklet()) // Tasklet 설정
            .build();
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
            .incrementer(new RunIdIncrementer()) // Job 인스턴스 구분을 위한 incrementer 설정
            .start(step()) // Step 설정
            .build();
    }
}