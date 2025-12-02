package com.springbatch.domain;

import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

import java.util.List;

public class ProductValidator implements Validator<Product> {

    List<String> validProductCategories = List.of("Mobile Phones", "Tablets", "Televisions", "Sports Accessories");

    @Override
    public void validate(Product item) throws ValidationException {
        if(!validProductCategories.contains(item.getProductCategory())) {
            throw new ValidationException("Invalid Product Category: " + item.getProductCategory());
        }

        if(item.getProductPrice() > 100000) {
            throw new ValidationException("Invalid Product Price: " + item.getProductPrice());
        }
    }
}
