package ru.jwt.controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.jwt.service.LookupService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lookup")
@Slf4j
public class LookupController {
    private final LookupService lookupService;

    // curl --location 'http://localhost:8080/lookup/users/all' --header 'Authorization: Bearer token'

    @Autowired
    public LookupController(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    @GetMapping("/{tableName}/count")
    public ResponseEntity<Integer> getTableCount(
            @Parameter(description = "Имя таблицы", required = true) @PathVariable String tableName) {

        int count = lookupService.getTableCount(tableName);

        if (count == -1) {
            log.error("Invalid table name: {}", tableName); // Логирование ошибки
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null); // Некорректная таблица
        }

        log.info("Table count for {}: {}", tableName, count); // Логирование успешного ответа
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{tableName}/all")
    public ResponseEntity<List<Map<String, Object>>> getAllEntities(
            @Parameter(description = "Имя таблицы", required = true) @PathVariable String tableName) {

        List<Map<String, Object>> entities = lookupService.getAllEntities(tableName);

        if (entities == null) {
            log.error("Invalid table name: {}", tableName); // Логирование ошибки
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null); // Некорректная таблица
        }

        log.info("Fetched {} entities from table {}", entities.size(), tableName); // Логирование успешного ответа
        return ResponseEntity.ok(entities);
    }
}

