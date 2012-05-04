package com.proteuez.hibernate.domain;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.SequenceGenerator;

/**
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class OrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ")
    @SequenceGenerator(name = "SEQ", sequenceName = "order_line_id_sequence", allocationSize = 1000)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "main_order", nullable = false, updatable = false, insertable = false)
    private MainOrder mainOrder;


    public MainOrder getMainOrder() {
        return mainOrder;
    }

    public OrderLine setMainOrder(MainOrder mainOrder) {
        this.mainOrder = mainOrder;
        return this;
    }

    public Long getId() {
        return id;
    }
}
