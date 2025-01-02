package com.musinsa.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.musinsa.entity.Musinsa;

public interface MusinsaRepository extends JpaRepository<Musinsa, Long> {
	
	//구현 2) - 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하는 API
	List<Musinsa> findAll();
	
    @Query(nativeQuery = true, value =
            "SELECT category, MIN(price), brand \n"
            + "                    FROM (SELECT 'tops' AS category, tops AS price, brand FROM musinsa UNION ALL \n"
            + "                    SELECT 'outer', outer, brand FROM musinsa UNION ALL \n"
            + "                    SELECT 'pants', pants, brand FROM musinsa UNION ALL \n"
            + "                    SELECT 'sneakers', sneakers, brand FROM musinsa UNION ALL \n"
            + "                    SELECT 'bag', bag, brand FROM musinsa UNION ALL \n"
            + "                    SELECT 'cap', cap, brand FROM musinsa UNION ALL \n"
            + "                    SELECT 'socks', socks, brand FROM musinsa UNION ALL \n"
            + "                    SELECT 'accessory', accessory, brand FROM musinsa) AS subquery \n"
            + "                    GROUP BY category, brand ORDER BY price ASC"
    )
    List<Object[]> findMinPrices();
    
    //:#{#foo}
//    @Query(nativeQuery = true, value = "SELECT brand, CASE WHEN :category = tops THEN tops WHEN :category = outer THEN outer WHEN :category = pants THEN pants :category = sneakers THEN sneakers :category = bag THEN bag :category = cap THEN cap :category = socks THEN socks :category = accessory THEN accessory ELSE NULL END AS price FROM musinsa")
    @Query(nativeQuery = true, value = "SELECT brand, CASE WHEN tops = tops THEN tops ELSE NULL END AS price FROM musinsa")
    List<Object[]> findPricesByCategory(@Param("category") String category);
}