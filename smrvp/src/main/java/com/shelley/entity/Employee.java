package com.shelley.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Employee implements Serializable {

    private Integer id;
    private String name;
    private String path;
    private Double salary;
    private Integer age;

}
