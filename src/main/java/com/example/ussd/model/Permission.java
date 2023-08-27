package com.example.ussd.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Permission {
    @Id
    private int id;
    private String name;
}
