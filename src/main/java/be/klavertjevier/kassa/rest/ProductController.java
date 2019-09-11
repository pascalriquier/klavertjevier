package be.klavertjevier.kassa.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import be.klavertjevier.kassa.domain.Product;
import be.klavertjevier.kassa.domain.ProductRepository;

@RestController
public class ProductController {

	@Autowired
	private ProductRepository productRepository;
	
	
	@PostMapping(value = "/rest/product")
	public Product addProduct(@RequestBody Product product) {
		return productRepository.save(product);
	}
	
	@DeleteMapping(value = "/rest/product/{code}")
	public void removeProduct(@PathVariable int code) {
		productRepository.delete(productRepository.findById(code).orElseThrow(() -> new IllegalArgumentException("Moet bestaan")));
	}
	
	@GetMapping(value = "/rest/products")
	public Iterable<Product> getProducts() {
		return productRepository.findAll();
	}
}
