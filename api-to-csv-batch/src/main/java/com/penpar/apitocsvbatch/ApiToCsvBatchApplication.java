package com.penpar.apitocsvbatch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.client.RestTemplate;

import com.penpar.apitocsvbatch.model.Item;
import com.penpar.apitocsvbatch.reader.ApiReader;

@SpringBootApplication
@EnableBatchProcessing
public class ApiToCsvBatchApplication {

    private final JobBuilderFactory jobBuilderFactory;  
    private final StepBuilderFactory stepBuilderFactory; 

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
                .<List<Item>, List<Item>>chunk(1)
                .reader(apiReader())
                .writer(csvWriter())
                .build();
    }

    @Bean
    public ApiReader apiReader() {
        return new ApiReader(restTemplate());
    }

    @Bean
    public ItemWriter<List<Item>> csvWriter() {
        BeanWrapperFieldExtractor<Item> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {
            "basDt", "srtnCd", "isinCd", "itmsNm", "mrktCtg", "clpr", 
            "vs", "fltRt", "mkp", "hipr", "lopr", "trqu", 
            "trPrc", "lstgStCnt", "mrktTotAmt"
        });

        DelimitedLineAggregator<Item> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        return items -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String formattedDate = LocalDate.now().format(formatter);

            FlatFileItemWriter<Item> csvWriter = new FlatFileItemWriterBuilder<Item>()
                    .name("csvWriter")
                    .resource(new FileSystemResource("stockinfo_" + formattedDate + ".csv")) // output file
                    .lineAggregator(lineAggregator)
                    .build();

            try {
                csvWriter.open(new ExecutionContext());
                for (List<Item> itemList : items) {
                    csvWriter.write(itemList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                csvWriter.close();
            }
        };
    }
}