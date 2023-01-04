package com.eazybytes.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
//tells me the table name
@Table(name = "authorities")
public class Authority {

    //id is primary key
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    private Long id;

    //db column 'name' is the same name so don't have to give it a column
    private String name;

    //relationship with the customer table
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

}
