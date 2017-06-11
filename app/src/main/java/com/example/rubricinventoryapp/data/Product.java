package com.example.rubricinventoryapp.data;

/**
 * Created by root on 6/8/17.
 */

public class Product {

    private String productId;
    private String productName;
    private String productQuantity;
    private String productPrice;
    private int productsSold;
    private String productSupplierName;
    private String productSupplierPhone;

    public Product(String productId, String productName, String productQuantity, String productPrice, int productsSold, String productSupplierName, String productSupplierPhone) {
        this.productId = productId;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
        this.productsSold = productsSold;
        this.productSupplierName = productSupplierName;
        this.productSupplierPhone = productSupplierPhone;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getProductSupplierName() {
        return productSupplierName;
    }

    public String getProductSupplierPhone() {
        return productSupplierPhone;
    }

    public String getProductId() {
        return productId;
    }

    public int getProductsSold() {
        return productsSold;
    }
}
