package com.proteuez.hibernate.domain;

import javax.persistence.Entity;

/**
 *
 */
@Entity
public class UnstructuredOrderLine extends OrderLine {
    private String orderDescription;


    public UnstructuredOrderLine() {
    }

    public UnstructuredOrderLine(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }
}
