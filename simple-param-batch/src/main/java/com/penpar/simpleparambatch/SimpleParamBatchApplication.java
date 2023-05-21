package com.penpar.simpleparambatch;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collections;

@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
public class SimpleParamBatchApplication {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobLauncher jobLauncher;

    @Bean
    public Step step(ItemReader<String> reader, ItemWriter<String> writer) {
        return stepBuilderFactory.get("step")
                .<String, String>chunk(1)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public Job job(Step step) {
        return jobBuilderFactory.get("job")
                .start(step)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<String> reader(@Value("#{jobParameters['message']}") String message) {
        return new ListItemReader<>(Collections.singletonList(message));
    }

    @Bean
    public ItemWriter<String> writer() {
        return list -> list.forEach(System.out::println);
    }

    @Scheduled(fixedDelay = 5000)
    public void perform() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("message", "Hello, Spring")
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
    
        jobLauncher.run(job(step(reader(null), writer())), params);
    }

    public static void main(String[] args) {
        SpringApplication.run(SimpleParamBatchApplication.class, args);
    }
}