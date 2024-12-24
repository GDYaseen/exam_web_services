package com;

import lombok.Data;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "products")
public class Product implements Serializable{
    @Id
    private String id;
    private String name;
    private double price;
}