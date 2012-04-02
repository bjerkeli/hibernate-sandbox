package com.proteuez.hibernate.domain;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.SequenceGenerator;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
@Entity
public class MainOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ")
    @SequenceGenerator(name = "SEQ", sequenceName = "order_id_sequence", allocationSize = 100)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = false)
    @Fetch(FetchMode.JOIN)
    @OrderColumn(name = "ordinal")
    @JoinColumn(name = "main_order", nullable = false, updatable = false, insertable = true)
    private List<OrderLine> orderLines = new LinkedList<OrderLine>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public List<OrderLine> getOrderLines() {
        return Collections.unmodifiableList(orderLines);
    }

    public MainOrder add(OrderLine orderLine) {
        orderLine.setMainOrder(this);
        this.orderLines.add(orderLine);
        return this;
    }
}
