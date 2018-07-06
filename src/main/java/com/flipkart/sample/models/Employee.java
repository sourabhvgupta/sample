package com.flipkart.sample.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonSnakeCase
public class Employee {

    private String employeeId;
    private String firstName;
    private String lastName;

    @JsonIgnore
    private String title;
    private double Salary;


}
