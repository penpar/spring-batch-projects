package com.penpar.csvtodbbatch;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.penpar.csvtodbbatch.model.StockInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private int totalItemCount = 0;


    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, EntityManagerFactory entityManagerFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public FlatFileItemReader<StockInfo> reader() {
        return new FlatFileItemReaderBuilder<StockInfo>()
            .name("stockInfoReader")
            .resource(new FileSystemResource("csv-to-db-batch/data/stockinfo_202011.csv"))
            .delimited()
            .names(new String[]{"basDt", "srtnCd", "isinCd", "itmsNm", "mrktCtg", "clpr", "vs",
            "fltRt", "mkp", "hipr", "lopr", "trqu", "trPrc", "lstgStCnt", "mrktTotAmt"})
            .linesToSkip(1) // add this line to skip the header
            .encoding("UTF-8")
            .fieldSetMapper(new BeanWrapperFieldSetMapper<StockInfo>() {{
                setTargetType(StockInfo.class);
            }})
            .build();
    }

    @Bean
    public ItemProcessor<StockInfo, StockInfo> processor() {
        return stockInfo -> {                
            totalItemCount++;
            log.info("Total items processed so far: " + totalItemCount);
            return stockInfo;
        };
        
    }
    
    @Bean
    public JpaItemWriter<StockInfo> writer() {
        JpaItemWriter<StockInfo> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean
    public Step csvToDbStep(ItemWriter<StockInfo> writer) {
        return stepBuilderFactory.get("importStockInfoStep")
            .<StockInfo, StockInfo>chunk(1000)
            .reader(reader())
            .processor(processor())
            .writer(writer)
            .build();
    }

    @Bean
    public Job csvToDbJob(Step step) {
        return jobBuilderFactory.get("importStockInfoJob")
            .incrementer(new RunIdIncrementer())
            .flow(step)
            .end()
            .build();
    }
}
