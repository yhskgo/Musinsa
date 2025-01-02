package com.musinsa.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.musinsa.entity.Musinsa;
import com.musinsa.service.MusinsaService;

@RestController
@RequestMapping("/api/musinsa")
public class MusinsaController {

    @Autowired
    private MusinsaService musinsaService;

    @GetMapping("/musinsa")
    public List<Musinsa> getAllMusinsa() {
        return musinsaService.findAll();
    }
    
    @GetMapping("/minprices")
    public ResponseEntity<String> getMinPrices() throws JsonProcessingException {
        String jsonResult = musinsaService.findMinPrices();
        return new ResponseEntity<>(jsonResult, HttpStatus.OK);
    }
    
    @GetMapping("/minpricebrand")
    public ResponseEntity<String> getMinPriceBrand() throws JsonProcessingException {
        String jsonResult = musinsaService.findMinPriceBrand();
        return new ResponseEntity<>(jsonResult, HttpStatus.OK);
    }
    
    @GetMapping("/minMaxPrice/{category}")
    public ResponseEntity<String> getMinPriceBrand(@PathVariable String category) throws JsonProcessingException {
        String jsonResult = null;
		try {
			jsonResult = musinsaService.findMinMaxPrice(category);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return new ResponseEntity<>(jsonResult, HttpStatus.OK);
    }
    
    //4)
    @PostMapping
    public ResponseEntity<Object> createMusinsa(@RequestBody Musinsa musinsa) {
        return musinsaService.createMusinsa(musinsa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateMusinsa(@PathVariable Long id, @RequestBody Musinsa musinsa) {
        return musinsaService.updateMusinsa(id, musinsa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMusinsa(@PathVariable Long id) {
        return musinsaService.deleteMusinsa(id);
    }
}