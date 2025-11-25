package com.springbatch.processor;

import com.springbatch.domain.Product;
import org.springframework.batch.item.ItemProcessor;

public class MyProductItemProcessor implements ItemProcessor<Product, Product> {

    @Override
    public Product process(Product item) throws Exception {
        System.out.println("Processor executed.");
        return item;
    }
}
