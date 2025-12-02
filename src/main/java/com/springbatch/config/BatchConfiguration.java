package com.springbatch.config;

import com.springbatch.domain.*;
import com.springbatch.processor.FilterProductItemProcessor;
import com.springbatch.processor.TransformItemProcessor;
import com.springbatch.reader.ProductNameItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
    public ItemReader<String> itemReader() {
        List<String> productList = new ArrayList<>();
        productList.add("Product1");
        productList.add("Product2");
        productList.add("Product3");
        productList.add("Product4");
        productList.add("Product5");

        return new ProductNameItemReader(productList);
    }

    @Bean
    public ItemReader<Product> flatFileItemReader() {
        FlatFileItemReader<Product> itemReader = new FlatFileItemReader<>();
        itemReader.setLinesToSkip(1);
        itemReader.setResource(new ClassPathResource("/data/Product_details.csv"));

        DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("product_id","product_name","product_category","product_price");

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(new ProductFieldSetMapper());


        itemReader.setLineMapper(lineMapper);

        return itemReader;
    }

    @Bean
    public ItemReader<Product> jdbcCursorItemReader() {
        JdbcCursorItemReader<Product> itemReader = new JdbcCursorItemReader<>();
        itemReader.setDataSource(dataSource);
        itemReader.setSql("SELECT * FROM product_details ORDER BY product_id");
        itemReader.setRowMapper(new ProductRowMapper());
        return itemReader;
    }

    @Bean
    public ItemReader<Product> jdbcPagingItemReader() throws Exception {
        JdbcPagingItemReader<Product> itemReader = new JdbcPagingItemReader<>();
        itemReader.setDataSource(dataSource);

        SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
        factory.setDataSource(dataSource);
        factory.setSelectClause("SELECT product_id, product_name, product_category, product_price");
        factory.setFromClause("FROM product_details");
        factory.setSortKey("product_id");

        itemReader.setQueryProvider(factory.getObject());
        itemReader.setRowMapper(new ProductRowMapper());
        itemReader.setPageSize(3);

        return itemReader;
    }

    @Bean
    public ItemWriter<Product> flatFileItemWriter() {

        FlatFileItemWriter<Product> itemWriter = new FlatFileItemWriter<>();
        itemWriter.setResource(new FileSystemResource("src/main/resources/data/Product_Details_Output.csv"));

        DelimitedLineAggregator<Product> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");

        BeanWrapperFieldExtractor<Product> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"productId","productName","productCategory","productPrice"});

        lineAggregator.setFieldExtractor(fieldExtractor);
        itemWriter.setLineAggregator(lineAggregator);

        return itemWriter;
    }

/*    @Bean
    public JdbcBatchItemWriter<Product> jdbcBatchItemWriter() {
        JdbcBatchItemWriter<Product> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO product_details_output values (:productId, :productName, :productCategory, :productPrice)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());

        return itemWriter;
    }*/

    @Bean
    public JdbcBatchItemWriter<OSProduct> jdbcBatchItemWriter() {
        JdbcBatchItemWriter<OSProduct> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO os_product_details values (:productId, :productName, :productCategory, :productPrice, :taxPerncentage, :sku, :shippingRate)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());

        return itemWriter;
    }

    @Bean
    public ItemProcessor<Product, Product> filterProductItemProcessor() {
        return new FilterProductItemProcessor();
    }

    @Bean
    public ItemProcessor<Product, OSProduct> transformItemProcessor() {
        return new TransformItemProcessor();
    }

//    @Bean
//    public ValidatingItemProcessor<Product> validateItemProcessor() {
//        ValidatingItemProcessor<Product> validatingItemProcessor = new ValidatingItemProcessor<>(new ProductValidator());
//        validatingItemProcessor.setFilter(true);
//        return validatingItemProcessor;
//    }

    @Bean
    public BeanValidatingItemProcessor<Product> validateItemProcessor() {
        BeanValidatingItemProcessor<Product> validatingItemProcessor = new BeanValidatingItemProcessor<>();
        validatingItemProcessor.setFilter(true);
        return validatingItemProcessor;
    }

    @Bean
    public CompositeItemProcessor<Product, OSProduct> itemProcessor(){
        CompositeItemProcessor<Product, OSProduct> processor = new CompositeItemProcessor<>();
        List itemProcessorsList = new ArrayList<>();
        itemProcessorsList.add(validateItemProcessor());
        itemProcessorsList.add(filterProductItemProcessor());
        itemProcessorsList.add(transformItemProcessor());
        processor.setDelegates(itemProcessorsList);
        return processor;
    }

	@Bean
	public Step step1() throws Exception {
		return this.stepBuilderFactory.get("step1")
                .<Product, OSProduct>chunk(3)
                .reader(jdbcPagingItemReader())
                .processor(itemProcessor())
                .writer(jdbcBatchItemWriter()).build();
    }

	@Bean
	public Job firstJob() throws Exception {
		return this.jobBuilderFactory.get("job1")
                .start(step1())
				.build();
	}
}
