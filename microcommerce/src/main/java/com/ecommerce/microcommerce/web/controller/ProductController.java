package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

// pour indiquer que ce contrôleur est un contrôleur REST
@RestController
public class ProductController {

    @Autowired

    private  ProductDao productDao;

    /*public ProductController(ProductDao productDao){
        this.productDao = productDao;
    }*/
    //Récupérer la liste des produits
    //@GetMapping("/Produits")
    @RequestMapping(value = "/Produits", method = RequestMethod.GET)
    public MappingJacksonValue listeProduits() {

        Iterable<Product> produits = productDao.findAll();
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);
        produitsFiltres.setFilters(listDeNosFiltres);
        return produitsFiltres;
    }
    // Récupérer un produit par son ID
    @GetMapping("/Produits/{id}")
    public Product afficherUnProduit(@PathVariable int id) {
       return productDao.findById(id);
    }

    @GetMapping(value = "test/produits/{prixLimit}")
    public List<Product> testeDeRequetes(@PathVariable int prixLimit)
    {
        return productDao.findByPrixGreaterThan(400);
    }


    @PostMapping(value = "/Produits")
    public ResponseEntity<Product> ajouterProduit(@RequestBody Product product) {
        Product productAdded = productDao.save(product);
        if (Objects.isNull(productAdded)) {
            return ResponseEntity.noContent().build();
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping (value = "/Produits/{id}")
    public void supprimerProduit(@PathVariable int id) {
        productDao.deleteById(id);
    }

    //pour mettre à jour
    @PutMapping (value = "/Produits")
    public void updateProduit(@RequestBody Product product)
    {
        productDao.save(product);
    }


}