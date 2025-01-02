package com.musinsa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Musinsa {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;;
	private String brand;
	private Integer tops;
    private Integer outer;
    private Integer pants;
    private Integer sneakers;
    private Integer bag;
    private Integer cap;
    private Integer socks;
    private Integer accessory;
    

}
