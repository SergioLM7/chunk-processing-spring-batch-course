package com.springbatch.domain;

public class OSProduct extends Product {

    private Integer taxPerncentage;
    private String sku;
    private Integer shippingRate;

    public Integer getTaxPerncentage() {
        return taxPerncentage;
    }

    public void setTaxPerncentage(Integer taxPerncentage) {
        this.taxPerncentage = taxPerncentage;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getShippingRate() {
        return shippingRate;
    }

    public void setShippingRate(Integer shippingRate) {
        this.shippingRate = shippingRate;
    }

    @Override
    public String toString() {
        return "OSProduct [" +
                "taxPerncentage=" + taxPerncentage +
                ", sku='" + sku + '\'' +
                ", shippingRate=" + shippingRate +
                ']';
    }
}
