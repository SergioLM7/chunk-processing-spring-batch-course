package com.springbatch.processor;

import com.springbatch.domain.Product;
import org.springframework.batch.item.ItemProcessor;

public class MyProductItemProcessor implements ItemProcessor<Product, Product> {

    @Override
    public Product process(Product item) throws Exception {
        System.out.println("Processor executed.");
        double price = item.getProductPrice();

        item.setProductPrice(price - (price*0.10));

        return item;
    }
}
