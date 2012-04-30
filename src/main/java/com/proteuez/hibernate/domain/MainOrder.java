package com.proteuez.hibernate.domain;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

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

    //@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate datePlaced;

    private DateTime timePlaced;

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

    public LocalDate getDatePlaced() {
        return datePlaced;
    }

    public MainOrder setDatePlaced(LocalDate datePlaced) {
        this.datePlaced = datePlaced;
        return this;
    }

    public DateTime getTimePlaced() {
        return timePlaced;
    }

    public void setTimePlaced(DateTime timePlaced) {
        this.timePlaced = timePlaced;
    }
}
