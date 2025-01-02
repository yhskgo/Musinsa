package com.musinsa.service;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.musinsa.MusinsaExamApplication;
import com.musinsa.entity.Musinsa;
import com.musinsa.repository.MusinsaRepository;


@Service
public class MusinsaService {
	
	private static final Logger log = LoggerFactory.getLogger(MusinsaExamApplication.class);

	@Autowired
    private MusinsaRepository musinsaRepository;
	@Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DataSource dataSource;

    public List<Musinsa> findAll() {
        return musinsaRepository.findAll();
    }

    public String findMinPrices() throws JsonProcessingException {
        List<Object[]> results = musinsaRepository.findMinPrices();
        Map<String, List<Object[]>> resultsByCategory = results.stream().collect(Collectors.groupingBy(r -> (String) r[0]));

        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode categoryArray = objectMapper.createArrayNode();
        int totalAmount = 0;
        List<String> categories = Arrays.asList("상의", "아우터", "바지", "스니커즈", "가방", "모자", "양말", "액세서리");

        for (String category : categories) {
            String dbCategory = "";
            switch (category) {
                case "상의": dbCategory = "tops"; break;
                case "아우터": dbCategory = "outer"; break;
                case "바지": dbCategory = "pants"; break;
                case "스니커즈": dbCategory = "sneakers"; break;
                case "가방": dbCategory = "bag"; break;
                case "모자": dbCategory = "cap"; break;
                case "양말": dbCategory = "socks"; break;
                case "액세서리": dbCategory = "accessory"; break;
            }

            List<Object[]> categoryResults = resultsByCategory.get(dbCategory);
            if (categoryResults != null && !categoryResults.isEmpty()) {
                Object[] minPriceResult = categoryResults.stream().min(
                        (r1, r2) -> {
                            
                            int price1 = getSafeInt(r1[1]);
                            int price2 = getSafeInt(r2[1]);
                            if (price1 != price2) {
                                return price1 - price2;
                            } else {
                                String brand1 = (String) (r1[2] != null ? r1[2] : "");
                                String brand2 = (String) (r2[2] != null ? r2[2] : "");
                                return brand2.compareTo(brand1); // 브랜드를 내림차순으로 정렬 (알파벳 순서 뒤에 있는 브랜드 우선)
                            }
                        }
                ).orElse(null);

                if (minPriceResult != null) {
                    ObjectNode categoryNode = objectMapper.createObjectNode();
                    categoryNode.put("카테고리", category);
                    categoryNode.put("브랜드", (String) minPriceResult[2]);
//                    categoryNode.put("가격", String.format("%,d", (int) minPriceResult[1]));
//                    categoryArray.add(categoryNode);
//                    totalAmount += (int) minPriceResult[1];
                    
                    categoryNode.put("가격", String.format("%,d", getSafeInt(minPriceResult[1])));
                    categoryArray.add(categoryNode);
                    totalAmount += getSafeInt(minPriceResult[1]);
                } else {
                    ObjectNode categoryNode = objectMapper.createObjectNode();
                    categoryNode.put("카테고리", category);
                    categoryNode.put("브랜드", "");
                    categoryNode.put("가격", "0");
                    categoryArray.add(categoryNode);
                }
            } else {
                ObjectNode categoryNode = objectMapper.createObjectNode();
                categoryNode.put("카테고리", category);
                categoryNode.put("브랜드", "");
                categoryNode.put("가격", "0");
                categoryArray.add(categoryNode);
            }
        }

        rootNode.set("category", categoryArray);
        rootNode.put("총액", String.format("%,d", totalAmount));
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }
    
    public String findMinPriceBrand() throws JsonProcessingException {
        List<Musinsa> musinsaList = musinsaRepository.findAll();
        if (musinsaList.isEmpty()) return objectMapper.writeValueAsString(new HashMap<>());

        // 브랜드별 총 가격을 계산합니다.
        Map<String, Integer> totalPricesByBrand = musinsaList.stream()
                .collect(Collectors.toMap(Musinsa::getBrand, m -> Arrays.asList(m.getTops(), m.getOuter(), m.getPants(), m.getSneakers(), m.getBag(), m.getCap(), m.getSocks(), m.getAccessory()).stream()
                		.mapToInt(p -> {
                            try {
                                return Integer.parseInt(String.valueOf(p).replaceAll(",", ""));
                            } catch (NumberFormatException | NullPointerException e) {
                                return 0; // null 또는 잘못된 형식의 값은 0으로 처리
                            }
                        }).sum()));
//                			.mapToInt(Integer::intValue).sum()));

        // 총 가격이 가장 작은 브랜드들을 찾습니다.
        int minPrice = totalPricesByBrand.values().stream().min(Integer::compareTo).orElse(0);
        List<String> minPriceBrands = totalPricesByBrand.entrySet().stream()
                .filter(entry -> entry.getValue() == minPrice)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 각 브랜드별 최저가 정보를 담은 JSON 배열을 생성합니다.
        ArrayNode minPriceBrandsArray = objectMapper.createArrayNode();
        for (String brand : minPriceBrands) {
            Optional<Musinsa> minBrand = musinsaList.stream().filter(m -> m.getBrand().equals(brand)).findFirst();
            if (minBrand.isPresent()) {
                Musinsa minBrandData = minBrand.get();
                ObjectNode minPriceBrandNode = objectMapper.createObjectNode();
                ArrayNode categoryArray = objectMapper.createArrayNode();
                int totalAmount = 0;
                List<String> categories = Arrays.asList("상의", "아우터", "바지", "스니커즈", "가방", "모자", "양말", "액세서리");
                List<Integer> prices = Arrays.asList(minBrandData.getTops(), minBrandData.getOuter(), minBrandData.getPants(), minBrandData.getSneakers(), minBrandData.getBag(), minBrandData.getCap(), minBrandData.getSocks(), minBrandData.getAccessory());

                for (int i = 0; i < categories.size(); i++) {
                    ObjectNode categoryNode = objectMapper.createObjectNode();
                    categoryNode.put("카테고리", categories.get(i));
                    categoryNode.put("가격", String.format("%,d", prices.get(i)));
                    categoryArray.add(categoryNode);
                    totalAmount += prices.get(i);
                }

                minPriceBrandNode.put("브랜드", minBrandData.getBrand());
                minPriceBrandNode.set("카테고리", categoryArray);
                minPriceBrandNode.put("총액", String.format("%,d", totalAmount));
                minPriceBrandsArray.add(minPriceBrandNode);
            }
        }

        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.set("최저가", minPriceBrandsArray);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }
    
    // 3)
    public String findMinMaxPrice(String paramCategory) throws JsonProcessingException, SQLException {	
    	paramCategory = paramCategory.replaceAll(" ", "");
    	String category = getCategoryInName(paramCategory);
    	
    	log.info(">@<카테고리 "+category);
   
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<Map<String, Object>> results = jdbcTemplate.queryForList(buildSql(category));
        if(results == null || results.isEmpty()) return objectMapper.writeValueAsString(new HashMap<>());
        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("카테고리", getCategoryOutName(category));
        ArrayNode minPrices = objectMapper.createArrayNode();
        ArrayNode maxPrices = objectMapper.createArrayNode();

        List<String> brandList = results.stream().map(r -> (String) r.get("brand")).collect(Collectors.toList());
        log.info(">@<브랜드 리스트 "+brandList.toString());
        
        List<Integer> priceList = results.stream()
                .map(r -> r.get("price"))
                .map(String::valueOf) // String으로 변환
                .map(priceStr -> {
                    try {
                        return Integer.parseInt(priceStr.replaceAll(",", ""));
                    } catch (NumberFormatException e) {
                        return 0; // 또는 다른 적절한 기본값, 예: -1
                    }
                })
                .collect(Collectors.toList());

        int minPrice = priceList.stream().min(Integer::compareTo).get();
        int minIndex = priceList.indexOf(minPrice);
        ObjectNode minPriceNode = objectMapper.createObjectNode();
        minPriceNode.put("브랜드", brandList.get(minIndex));
        minPriceNode.put("가격", String.format("%,d", minPrice));
        minPrices.add(minPriceNode);

        int maxPrice = priceList.stream().max(Integer::compareTo).get();
        int maxIndex = priceList.indexOf(maxPrice);
        ObjectNode maxPriceNode = objectMapper.createObjectNode();
        maxPriceNode.put("브랜드", brandList.get(maxIndex));
        maxPriceNode.put("가격", String.format("%,d", maxPrice));
        maxPrices.add(maxPriceNode);

        rootNode.set("최저가", minPrices);
        rootNode.set("최고가", maxPrices);

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);

    }
    
    private int getSafeInt(Object value) {
        if (value == null) return 0;
        try {
            return Integer.parseInt(String.valueOf(value).replaceAll(",", ""));
        } catch (NumberFormatException e) {
            return 0; // 또는 다른 적절한 기본값
        }
    }
    
    private static String getSafeString(Object value) {
        return value != null ? String.valueOf(value) : "";
    }
    
    private String getCategoryInName(String englishCategoryName) {
        Map<String, String> categoryMap = Map.of(
                "상의", "tops", "아우터", "outer", "바지", "pants",
                "스니커즈", "sneakers", "가방", "bag", "모자", "cap",
                "양말", "socks", "액세서리", "accessory"
        );
        return categoryMap.getOrDefault(englishCategoryName, englishCategoryName);
    }
    
    private String getCategoryOutName(String englishCategoryName) {
        Map<String, String> categoryMap = Map.of(
                "tops", "상의", "outer", "아우터", "pants", "바지",
                "sneakers", "스니커즈", "bag", "가방", "cap", "모자",
                "socks", "양말", "accessory", "액세서리"
        );
        return categoryMap.getOrDefault(englishCategoryName, englishCategoryName);
    }

    private String buildSql(String category){
        return String.format("SELECT brand, CASE WHEN %s = tops THEN tops WHEN %s = outer THEN outer WHEN %s = pants THEN pants WHEN %s = sneakers THEN sneakers WHEN %s = bag THEN bag WHEN %s = cap THEN cap WHEN %s = socks THEN socks WHEN %s = accessory THEN accessory ELSE NULL END AS price FROM musinsa", category, category, category, category, category, category, category, category);
    }
    
    // 구현 4) 브랜드 및 상품을 추가/업데이트/삭제하는 API
	//    ● 요청값- Request Body JSON
	//    ● 응답값
	//    ● 성공 혹은 실패 여부 JSON
	//    ● API 실패 시, 실패값과 실패 사유를 전달해야 합니다.
    public ResponseEntity<Object> createMusinsa(Musinsa musinsa) {
        try {
            Musinsa savedMusinsa = musinsaRepository.save(musinsa);
            log.info(">@<상품생성: "+savedMusinsa);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("response", savedMusinsa);
            response.put("error", null);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return createErrorResponse("데이터 무결성 위반", e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return createErrorResponse("서버 오류", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    public ResponseEntity<Object> updateMusinsa(Long id, Musinsa musinsa) {
        try {
            Optional<Musinsa> existingMusinsaOptional = musinsaRepository.findById(id);
            if (existingMusinsaOptional.isPresent()) {
                Musinsa existingMusinsa = existingMusinsaOptional.get();
                
                log.info(">@<상품업데이트 대상: "+existingMusinsa);

                // 요청 본문에 값이 있는 경우에만 업데이트
                if (musinsa.getBrand() != null) existingMusinsa.setBrand(musinsa.getBrand());
                if (musinsa.getTops() != null) existingMusinsa.setTops(musinsa.getTops());
                if (musinsa.getOuter() != null) existingMusinsa.setOuter(musinsa.getOuter());
                if (musinsa.getPants() != null) existingMusinsa.setPants(musinsa.getPants());
                if (musinsa.getSneakers() != null) existingMusinsa.setSneakers(musinsa.getSneakers());
                if (musinsa.getBag() != null) existingMusinsa.setBag(musinsa.getBag());
                if (musinsa.getCap() != null) existingMusinsa.setCap(musinsa.getCap());
                if (musinsa.getSocks() != null) existingMusinsa.setSocks(musinsa.getSocks());
                if (musinsa.getAccessory() != null) existingMusinsa.setAccessory(musinsa.getAccessory());

                musinsaRepository.save(existingMusinsa);
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("response", existingMusinsa);
                response.put("error", null);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return createErrorResponse("제품 없음", null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("서버 오류", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> updateMusinsold(Long id, Musinsa musinsa) {
        try {
            Optional<Musinsa> existingMusinsa = musinsaRepository.findById(id);
            if (existingMusinsa.isPresent()) {
                Musinsa updatedMusinsa = existingMusinsa.get();
                log.info(">@<상품삭 대상: "+existingMusinsa);
                updatedMusinsa.setBrand(musinsa.getBrand());
                updatedMusinsa.setTops(musinsa.getTops());
                updatedMusinsa.setOuter(musinsa.getOuter());
                updatedMusinsa.setPants(musinsa.getPants());
                updatedMusinsa.setSneakers(musinsa.getSneakers());
                updatedMusinsa.setBag(musinsa.getBag());
                updatedMusinsa.setCap(musinsa.getCap());
                updatedMusinsa.setSocks(musinsa.getSocks());
                updatedMusinsa.setAccessory(musinsa.getAccessory());
                musinsaRepository.save(updatedMusinsa);
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("response", updatedMusinsa);
                response.put("error", null);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return createErrorResponse("제품 없음", null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("서버 오류", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> deleteMusinsa(Long id) {
        try {
            Optional<Musinsa> existingMusinsa = musinsaRepository.findById(id);
            if (existingMusinsa.isPresent()) {
                Musinsa deletedMusinsa = existingMusinsa.get(); // 삭제할 제품 정보를 가져옵니다.
                log.info(">@<삭제상 대상: "+existingMusinsa);
                String productName = deletedMusinsa.getBrand();
                musinsaRepository.deleteById(id);
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("response", productName + " 제품 삭제 성공"); 
                response.put("deletedProduct", deletedMusinsa); // 삭제된 제품 정보 추가
                response.put("error", null);
                return new ResponseEntity<>(response, HttpStatus.OK); // HTTP 상태 코드를 OK로 변경
            } else {
                return createErrorResponse("제품 없음", null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return createErrorResponse("서버 오류", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Object> createErrorResponse(String error, String message, HttpStatus status) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("success", "false");
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        return new ResponseEntity<>(errorResponse, status);
    }
    
}