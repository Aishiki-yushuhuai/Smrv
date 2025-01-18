package com.shelley.controller;

import com.shelley.common.Result;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/excel")
@CrossOrigin // 处理跨域请求
public class ExcelController {

    // 存储上传的Excel文件数据
    private Map<String, List<Map<String, String>>> excelDataMap = new HashMap<>();
    /**
     * 处理Excel文件上传
     */
    @PostMapping("/upload")
    public Result uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();

            // 添加调试日志
            System.out.println("正在处理文件: " + fileName);

            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            // 获取表头（列名）
            Row headerRow = sheet.getRow(0);
            List<String> columns = new ArrayList<>();
            for (Cell cell : headerRow) {
                String columnName = getCellValueAsString(cell);
                columns.add(columnName);
                // 添加调试日志
                System.out.println("读取到列名: " + columnName);
            }

            // 读取数据
            List<Map<String, String>> data = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Map<String, String> rowData = new HashMap<>();
                    for (int j = 0; j < columns.size(); j++) {
                        Cell cell = row.getCell(j);
                        String value = getCellValueAsString(cell);
                        rowData.put(columns.get(j), value);
                    }
                    // 添加调试日志
                    System.out.println("读取到行数据: " + rowData);
                    data.add(rowData);
                }
            }

            // 存储数据
            excelDataMap.put(fileName, data);

            workbook.close();

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("columns", columns);
            responseData.put("fileName", fileName);

            // 添加调试日志
            System.out.println("文件处理完成，列名列表: " + columns);

            return Result.success("文件上传成功", responseData);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 处理Excel文件比对
     */
    @PostMapping("/compare")
    public Result compareExcel(@RequestBody Map<String, Object> params) {
        try {
            String firstColumn = (String) params.get("firstColumn");
            String secondColumn = (String) params.get("secondColumn");
            List<String> filterColumns = (List<String>) params.get("filterColumns");
            String compareLogic = (String) params.get("compareLogic");
            String firstFileName = (String) params.get("firstFile");
            String secondFileName = (String) params.get("secondFile");

            if (excelDataMap.size() < 2) {
                return Result.error("请先上传两个Excel文件");
            }

            // 获取对应的文件数据
            Map.Entry<String, List<Map<String, String>>> firstEntry = null;
            Map.Entry<String, List<Map<String, String>>> secondEntry = null;

            for (Map.Entry<String, List<Map<String, String>>> entry : excelDataMap.entrySet()) {
                if (entry.getKey().equals(firstFileName)) {
                    firstEntry = entry;
                } else if (entry.getKey().equals(secondFileName)) {
                    secondEntry = entry;
                }
            }

            if (firstEntry == null || secondEntry == null) {
                return Result.error("未找到对应的文件数据，请确认文件是否已上传。\n" +
                        "需要的文件：" + firstFileName + ", " + secondFileName + "\n" +
                        "已上传的文件：" + excelDataMap.keySet());
            }

            List<Map<String, String>> firstFileData = firstEntry.getValue();
            List<Map<String, String>> secondFileData = secondEntry.getValue();

            // 添加调试日志
            System.out.println("第一个文件第一行数据: " + firstFileData.get(0));
            System.out.println("第二个文件第一行数据: " + secondFileData.get(0));

            // 打印第一个文件的列名
            if (!firstFileData.isEmpty()) {
                System.out.println("第一个文件可用的列: " + firstFileData.get(0).keySet());
            }

            // 打印第二个文件的列名
            if (!secondFileData.isEmpty()) {
                System.out.println("第二个文件可用的列: " + secondFileData.get(0).keySet());
            }

            // 比对结果
            List<Map<String, String>> results = new ArrayList<>();

            // 进行比对
            for (Map<String, String> firstRow : firstFileData) {
                String firstValue = firstRow.get(firstColumn);
                // 打印第一个文件的值
                System.out.println("第一个文件的值: " + firstValue + " (列名: " + firstColumn + ")");

                for (Map<String, String> secondRow : secondFileData) {
                    String secondValue = secondRow.get(secondColumn);
                    // 打印第二个文件的值
                    System.out.println("第二个文件的值: " + secondValue + " (列名: " + secondColumn + ")");

                    // 如果任一值为null，打印完整的行数据以便调试
                    if (firstValue == null || secondValue == null) {
                        System.out.println("发现空值:");
                        System.out.println("第一行数据: " + firstRow);
                        System.out.println("第二行数据: " + secondRow);
                    }

                    if (compareValues(firstValue, secondValue, compareLogic)) {
                        // 创建结果行，合并两个文件的所有列数据
                        Map<String, String> resultRow = new HashMap<>();

                        // 添加第一个文件的所有列数据
                        for (String column : filterColumns) {
                            String value = firstRow.get(column);
                            if (value != null) {
                                // 如果是重复的列名，添加文件标识
                                if (secondRow.containsKey(column)) {
                                    resultRow.put(column + " (文件1)", value);
                                } else {
                                    resultRow.put(column, value);
                                }
                            }
                        }
                        // 添加第二个文件的所有列数据
                        for (String column : filterColumns) {
                            String value = secondRow.get(column);
                            if (value != null) {
                                // 如果是重复的列名，添加文件标识
                                if (firstRow.containsKey(column) && !column.equals(secondColumn)) {
                                    resultRow.put(column + " (文件2)", value);
                                } else if (!firstRow.containsKey(column)) {
                                    resultRow.put(column, value);
                                }
                            }
                        }
                        System.out.println(resultRow);
                        results.add(resultRow);
                        break;
                    }
                }
            }

            // 生成唯一的下载ID
            String downloadId = UUID.randomUUID().toString();
            // 存储结果用于后续下载
            excelDataMap.put(downloadId, results);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("results", results);
            responseData.put("total", results.size());
            responseData.put("downloadId", downloadId);
            return Result.success("比对完成spring", responseData);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("比对失败：" + e.getMessage());
        }
    }

    /**
     * 下载比对结果
     */
    @GetMapping("/download/{downloadId}")
    public void downloadResults(@PathVariable String downloadId, HttpServletResponse response) throws IOException {
        List<Map<String, String>> results = excelDataMap.get(downloadId);
        if (results == null || results.isEmpty()) {
            throw new RuntimeException("未找到比对结果或结果已过期");
        }

        // 创建新的工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("比对结果");

        // 创建表头
        Row headerRow = sheet.createRow(0);
        List<String> columns = new ArrayList<>(results.get(0).keySet());
        for (int i = 0; i < columns.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns.get(i));
        }

        // 填充数据
        for (int i = 0; i < results.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Map<String, String> rowData = results.get(i);
            for (int j = 0; j < columns.size(); j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(rowData.get(columns.get(j)));
            }
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=comparison_results.xlsx");

        // 写入响应
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * 比较两个值
     */
    private boolean compareValues(String value1, String value2, String compareLogic) {
        if (value1 == null || value2 == null) {
            return false;
        }

        // 先尝试清理字符串，去除可能的空格
        value1 = value1.trim();
        value2 = value2.trim();

        // 对于数值比较，先尝试转换为 BigDecimal 以处理各种数字格式
        try {
            if ("equals".equals(compareLogic)) {
                // 对于相等比较，先尝试直接字符串比较
                if (value1.equals(value2)) {
                    return true;
                }
                // 如果字符串不相等，尝试数值比较
                BigDecimal num1 = new BigDecimal(value1);
                BigDecimal num2 = new BigDecimal(value2);
                return num1.compareTo(num2) == 0;
            } else {
                // 对于其他数值比较
                BigDecimal num1 = new BigDecimal(value1);
                BigDecimal num2 = new BigDecimal(value2);
                
                switch (compareLogic) {
                    case "greater":
                        return num1.compareTo(num2) > 0;
                    case "greaterOrEquals":
                        return num1.compareTo(num2) >= 0;
                    case "less":
                        return num1.compareTo(num2) < 0;
                    case "lessOrEquals":
                        return num1.compareTo(num2) <= 0;
                    default:
                        return num1.compareTo(num2) == 0;
                }
            }
        } catch (NumberFormatException e) {
            // 如果无法转换为数字，则进行字符串比较
            switch (compareLogic) {
                case "equals":
                    return value1.equals(value2);
                case "greater":
                    return value1.compareTo(value2) > 0;
                case "greaterOrEquals":
                    return value1.compareTo(value2) >= 0;
                case "less":
                    return value1.compareTo(value2) < 0;
                case "lessOrEquals":
                    return value1.compareTo(value2) <= 0;
                default:
                    return value1.equals(value2);
            }
        }
    }

    /**
     * 获取单元格的值并转换为字符串
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        // 添加调试日志
        System.out.println("正在读取单元格，类型: " + cell.getCellType());

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                // 使用 DataFormatter 来保持原始格式
                DataFormatter formatter = new DataFormatter();
                return formatter.formatCellValue(cell).trim();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (IllegalStateException e) {
                    return cell.getStringCellValue();
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    /**
     * 获取已上传的文件列表
     */
    @GetMapping("/files")
    public Result getUploadedFiles() {
        List<Map<String, Object>> files = new ArrayList<>();
        
        for (Map.Entry<String, List<Map<String, String>>> entry : excelDataMap.entrySet()) {
            String fileName = entry.getKey();
            // 跳过临时的下载ID
            if (!fileName.contains("-")) {  // 假设下载ID包含连字符
                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("fileName", fileName);
                fileInfo.put("columns", new ArrayList<>(entry.getValue().get(0).keySet()));
                files.add(fileInfo);
            }
        }
        
        return Result.success("获取成功", files);
    }
}