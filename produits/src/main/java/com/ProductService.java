package com;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    public Product getProductById(String id){
        return productRepository.findById(id).get();
    }
    public Product addProduct(Product p){
        return productRepository.save(p);
    }
    public Product updateProduct(String id, Product product) {
        if (productRepository.existsById(id)) {
            // Set the ID of the product to ensure it updates the correct entity
            product.setId(id);
            // Save and return the updated product
            return productRepository.save(product);
        } else {
            throw new RuntimeException("Product with ID " + id + " not found.");
        }
    }
    public void deleteProduct(String id){
        productRepository.deleteById(id);;
    }
}
