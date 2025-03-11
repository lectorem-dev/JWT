package ru.jwt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LookupService {

    private final JdbcTemplate jdbcTemplate;

    // Массив допустимых таблиц
    private static final List<String> VALID_TABLES = Arrays.asList("tax_type", "user_role", "status_type", "users",
            "tax_assessments", "requests", "organizations");


    public int getTableCount(String tableName) {
        // Проверяем, что таблица входит в список разрешённых
        if (!VALID_TABLES.contains(tableName.toLowerCase())) {
            log.warn("Unknown or invalid table: {}", tableName);
            return -1;  // Возвращаем -1 для обозначения ошибки
        }

        String sqlQuery = "SELECT COUNT(*) FROM " + tableName;
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class);
    }

    public List<Map<String, Object>> getAllEntities(String tableName) {
        // Проверяем, что таблица входит в список разрешённых
        if (!VALID_TABLES.contains(tableName.toLowerCase())) {
            log.warn("Unknown or invalid table: {}", tableName);
            return null;  // Возвращаем null в случае ошибки
        }

        String sqlQuery = "SELECT * FROM " + tableName;
        return jdbcTemplate.queryForList(sqlQuery);
    }
}
