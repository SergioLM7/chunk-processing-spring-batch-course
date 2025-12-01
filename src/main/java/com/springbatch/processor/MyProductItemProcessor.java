package com.springbatch.processor;

import com.springbatch.domain.OSProduct;
import com.springbatch.domain.Product;
import org.springframework.batch.item.ItemProcessor;

public class MyProductItemProcessor implements ItemProcessor<Product, OSProduct> {

    @Override
    public OSProduct process(Product item) throws Exception {
        System.out.println("Processor executed.");

        OSProduct osProduct = new OSProduct();
        osProduct.setProductId(item.getProductId());
        osProduct.setProductName(item.getProductName());
        osProduct.setProductCategory(item.getProductCategory());
        osProduct.setProductPrice(item.getProductPrice());
        osProduct.setTaxPerncentage(item.getProductCategory().equals("Sports Accesories") ? 5 : 18);
        osProduct.setSku(item.getProductCategory().substring(0,3) + item.getProductId());
        osProduct.setShippingRate(item.getProductPrice() < 1000 ? 75 : 0);

        return osProduct;
    }
}
