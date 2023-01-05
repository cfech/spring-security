package com.eazybytes.repository;

import com.eazybytes.model.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


// must extend another repository
//CrudRepository<Model, Primary Key type> gives us all the crud operations

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    // spring is smart enough to know that since we have an email in the Customer class, so it can generate teh find by business logic
    // known as derived method name query
    // will compare the string and inject it into the where parameter for the db query

    List<Customer> findByEmail(String email);
    
}
