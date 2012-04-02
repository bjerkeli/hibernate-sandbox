package com.proteuez.hibernate.domain;

import javax.persistence.Entity;

/**
 *
 */
@Entity
public class StructuredOrderLine extends OrderLine {
    private int product;
    private int quantity;
    private String additionalInfo;

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
