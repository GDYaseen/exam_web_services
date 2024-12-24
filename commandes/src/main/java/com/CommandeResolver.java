package com;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import reactor.core.publisher.Mono;

@Component
public class CommandeResolver {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CommandeRepository commandeRepository;  

    @Autowired
    private ReactiveCircuitBreaker circuitBreaker;

    private static final String PRODUITS_SERVICE_URL = "http://localhost:8000/products";

    // Query to get a Commande by ID
    @QueryMapping
    public Mono<Commande> getCommande(String id) {
        // Use the circuit breaker to wrap the call to the "Produits" microservice
        return circuitBreaker.run(
            Mono.fromCallable(() -> restTemplate.getForObject(PRODUITS_SERVICE_URL + "/" + id, ProductDTO.class)),
            throwable -> {
                // Fallback logic if the circuit breaker is open or an error occurs
                System.out.println("Error calling Produits service: " + throwable.getMessage());
                return null; // Return null or a default value in case of failure
            }
        )
        .map(product -> {
            if (product != null) {
                Commande commande = new Commande();
                commande.setId(id);
                commande.setProduitId(product.getId());
                commande.setQuantity(1); // Default quantity
                return commande;
            } else {
                throw new RuntimeException("Product not found");
            }
        });
    }

    @QueryMapping
    public List<Commande> getAllCommandes() {
        return List.of(new Commande()); // Return a list of commandes
    }

    // Mutation to create a new Commande
    @MutationMapping
    public Mono<Commande> createCommande(String produitId, int quantity) {
        // Create a new Commande object
        Commande commande = new Commande();
        commande.setProduitId(produitId);
        commande.setQuantity(quantity);

        // Use the circuit breaker to wrap the call to the "Produits" microservice
        return circuitBreaker.run(
            Mono.fromCallable(() -> restTemplate.getForObject(PRODUITS_SERVICE_URL + "/" + produitId, ProductDTO.class)),
            throwable -> {
                // Fallback logic if the circuit breaker is open or an error occurs
                System.out.println("Error calling Produits service: " + throwable.getMessage());
                return null; // Return null or a default value in case of failure
            }
        )
        .map(product -> {
            if (product != null) {
                // If the product exists, save the commande to the database
                return commandeRepository.save(commande); // Save the commande to MySQL
            } else {
                throw new RuntimeException("Product not found");
            }
        });
    }
}
