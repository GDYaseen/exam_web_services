package com;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CommandeResolver {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CommandeRepository commandeRepository;  // Inject the repository

    private static final String PRODUITS_SERVICE_URL = "http://localhost:8081/products"; // URL of the "Produits" microservice

    // Query to get a Commande by ID
    @QueryMapping
    public Commande getCommande(String id) {
        ProductDTO product = restTemplate.getForObject(PRODUITS_SERVICE_URL + "/" + id, ProductDTO.class);
        Commande commande = new Commande();
        commande.setId(id);
        commande.setProduitId(product.getId());
        commande.setQuantity(1); // Default quantity
        return commande;
    }

    // Query to get all commandes
    @QueryMapping
    public List<Commande> getAllCommandes() {
        return List.of(new Commande()); // Return a list of commandes
    }

    // Mutation to create a new Commande
    @MutationMapping
    public Commande createCommande(String produitId, int quantity) {
        // Create a new Commande object
        Commande commande = new Commande();
        commande.setProduitId(produitId);
        commande.setQuantity(quantity);

        // Make a call to the "Produits" microservice to check if the product exists
        ProductDTO product = restTemplate.getForObject(PRODUITS_SERVICE_URL + "/" + produitId, ProductDTO.class);

        if (product != null) {
            // If the product exists, save the commande (for now, just return it)
            return commandeRepository.save(commande);
        } else {
            throw new RuntimeException("Product not found");
        }
    }
}
