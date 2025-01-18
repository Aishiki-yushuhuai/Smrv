package com.shelley.entity;

import java.util.List;
import java.util.Map;

public class ExcelData {
    private String fileName;
    private List<String> headers;  // 列名
    private List<Map<String, String>> rows;  // 行数据

    public ExcelData(String fileName, List<String> headers, List<Map<String, String>> rows) {
        this.fileName = fileName;
        this.headers = headers;
        this.rows = rows;
    }

    // getters and setters
    public String getFileName() {
        return fileName;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<Map<String, String>> getRows() {
        return rows;
    }
}