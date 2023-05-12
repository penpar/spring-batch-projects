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

	private final JobBuilderFactory jobBuilderFactory;  // Job 빌더 생성을 위한 빌더 팩토리 생성
	private final StepBuilderFactory stepBuilderFactory; // Step 빌더 생성을 위한 빌더 팩토리 생성

	SimpleBatchApplication(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}

    public static void main(String[] args) {
        SpringApplication.run(SimpleBatchApplication.class, args);
    }
    
    @Bean
    public Tasklet tasklet() { // Tasklet 또는 Chunk 방식 중 하나를 선택하여 구현
        return (contribution, chunkContext) -> {
            System.out.println("Hello, Spring Batch!"); // 작업 수행
            return RepeatStatus.FINISHED;   // 작업 완료 상태 반환
        };
    }

    @Bean
    public Step step() {    // Step 설정
        return stepBuilderFactory.get("step")   // Step 빌더 생성
            .tasklet(tasklet()) // Tasklet 설정
            .build();
    }

    @Bean
    public Job job() {  // Job 설정
        return jobBuilderFactory.get("job")     // Job 빌더 생성
            .incrementer(new RunIdIncrementer()) // Job 인스턴스 구분을 위한 incrementer 설정
            .start(step()) // Step 설정
            .build();
    }
}