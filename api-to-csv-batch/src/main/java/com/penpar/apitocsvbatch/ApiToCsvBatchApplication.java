package com.penpar.apitocsvbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.client.RestTemplate;

import com.penpar.apitocsvbatch.reader.ApiReader;
import com.penpar.apitocsvbatch.reader.Item;

@SpringBootApplication
@EnableBatchProcessing
public class ApiToCsvBatchApplication {

    private final JobBuilderFactory jobBuilderFactory;  // Job 빌더 생성을 위한 빌더 팩토리 생성
	private final StepBuilderFactory stepBuilderFactory; // Step 빌더 생성을 위한 빌더 팩토리 생성

	ApiToCsvBatchApplication(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}
    
    public static void main(String[] args) {
        SpringApplication.run(ApiToCsvBatchApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Job apiToCsvJob() {
        return jobBuilderFactory.get("apiToCsvJob")
                .incrementer(new RunIdIncrementer())
                .flow(apiToCsvStep())
                .end()
                .build();
    }

    @Bean
    public Step apiToCsvStep() {
        return stepBuilderFactory.get("apiToCsvStep")
                .<Item, Item>chunk(1)
                .reader(apiReader())
                .writer(csvWriter())
                .build();
    }

    @Bean
    public ApiReader apiReader() {
        return new ApiReader(restTemplate());
    }

    @Bean
    public FlatFileItemWriter<Item> csvWriter() {
        FlatFileItemWriter<Item> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("output.csv"));

        BeanWrapperFieldExtractor<Item> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"basDt", "srtnCd", "isinCd", "mrktCtg", "itmsNm", "crno", "corpNm"});
        DelimitedLineAggregator<Item> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        writer.setLineAggregator(lineAggregator);
        return writer;
    }
}