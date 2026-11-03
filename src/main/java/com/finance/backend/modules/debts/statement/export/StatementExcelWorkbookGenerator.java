package com.finance.backend.modules.debts.statement.export;

import com.finance.backend.modules.debts.statement.dto.StatementResponse;
import com.finance.backend.modules.debts.statemententry.dto.StatementEntryResponse;
import com.finance.backend.modules.debts.statemententry.model.StatementEntryType;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class StatementExcelWorkbookGenerator {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final String TEMPLATE_PATH = "templates/Plantilla_Colores.xlsx";
    private static final String CURRENCY_FORMAT = "$#,##0.00;[Red]($#,##0.00);-";
    private static final String DATE_FORMAT = "dd/mm/yyyy";

    private static final List<String> MONTH_NAMES = List.of(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre");

    private static final int DETAIL_HEADER_ROW = 10; // Fila 11 de Excel
    private static final int DETAIL_FIRST_ROW = 11; // Fila 12 de Excel
    private static final int FIRST_DETAIL_COLUMN = 1; // Columna B
    private static final int LAST_DETAIL_COLUMN = 13; // Columna N
    private static final List<String> DETAIL_HEADERS = List.of(
            "Categoría", "Descripción del gasto", "Comercio", "Deudor", "Fecha", "MSI",
            "Pagado", "Importe", "Total compra", "Tipo", "MSI restantes",
            "Saldo MSI restante", "Notas");
    // Reutiliza estilos existentes de texto, fecha y moneda al extender la tabla.
    private static final int[] DETAIL_STYLE_SOURCE_COLUMNS = {
            1, 2, 2, 3, 4, 5, 6, 8, 7, 2, 5, 8, 2
    };

    public byte[] generate(
            List<StatementResponse> statements,
            List<StatementEntryResponse> entries) {

        Objects.requireNonNull(statements, "statements no puede ser null");
        Objects.requireNonNull(entries, "entries no puede ser null");

        try (
                InputStream template = new ClassPathResource(TEMPLATE_PATH).getInputStream();
                XSSFWorkbook workbook = new XSSFWorkbook(template);
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            List<String> debtors = sortedDebtors(workbook, entries);

            for (int month = 1; month <= MONTH_NAMES.size(); month++) {
                String monthName = MONTH_NAMES.get(month - 1);
                final int monthNumber = month;

                List<StatementResponse> monthStatements = statements.stream()
                        .filter(s -> Objects.equals(s.month(), monthNumber))
                        .toList();

                Set<Long> statementIds = monthStatements.stream()
                        .map(StatementResponse::statementId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                List<StatementEntryResponse> monthEntries = entries.stream()
                        .filter(e -> statementIds.contains(e.statementId()))
                        .sorted(Comparator
                                .comparing(
                                        StatementEntryResponse::date,
                                        Comparator.nullsFirst(Comparator.naturalOrder()))
                                .thenComparing(
                                        e -> safe(e.conceptName()),
                                        String.CASE_INSENSITIVE_ORDER)
                                .thenComparing(
                                        e -> safe(e.debtor()),
                                        String.CASE_INSENSITIVE_ORDER))
                        .toList();

                populateMonthSheet(
                        workbook, monthName, monthStatements, monthEntries, debtors);
            }

            populateTotalSheet(workbook, statements, entries, debtors);
            workbook.setForceFormulaRecalculation(true);
            workbook.write(output);

            byte[] result = output.toByteArray();

            // Comprueba que Apache POI pueda volver a abrir el archivo generado.
            try (XSSFWorkbook validation = new XSSFWorkbook(new ByteArrayInputStream(result))) {
                if (validation.getNumberOfSheets() == 0) {
                    throw new IllegalStateException("El Excel generado no contiene hojas.");
                }
            }

            return result;
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "No se pudo generar el Excel usando la plantilla " + TEMPLATE_PATH, e);
        }
    }

    private void populateMonthSheet(
            XSSFWorkbook workbook,
            String monthName,
            List<StatementResponse> statements,
            List<StatementEntryResponse> entries,
            List<String> debtors) {

        Sheet sheet = workbook.getSheet(monthName);
        if (sheet == null) {
            throw new IllegalStateException(
                    "La plantilla no contiene la hoja mensual: " + monthName);
        }

        // Resumen superior. Escribe valores calculados para reemplazar fórmulas rotas.
        writeText(sheet, 1, 1, "Categoría");
        writeText(sheet, 2, 1, "Total Gastado");
        writeText(sheet, 3, 1, "Monto Pagado");
        writeText(sheet, 4, 1, "Monto Restante");

        for (int i = 0; i < 4; i++) {
            int col = 2 + i;
            String debtor = i < debtors.size() ? debtors.get(i) : "";
            writeText(sheet, 1, col, debtor);

            BigDecimal spent = debtor.isBlank()
                    ? ZERO
                    : sum(entries, e -> debtor.equals(e.debtor()));
            BigDecimal paid = debtor.isBlank()
                    ? ZERO
                    : sum(entries, e -> debtor.equals(e.debtor())
                            && Boolean.TRUE.equals(e.paid()));

            writeMoney(sheet, 2, col, spent);
            writeMoney(sheet, 3, col, paid);
            writeMoney(sheet, 4, col, spent.subtract(paid));
        }

        // Panel de fechas.
        writeText(sheet, 1, 7, "Fecha del corte");
        writeText(sheet, 3, 7, "Fecha del pago");
        writeDate(sheet, 2, 7, minDate(statements, StatementResponse::periodStart));
        writeDate(sheet, 2, 8, maxDate(statements, StatementResponse::periodEnd));
        writeDate(sheet, 4, 7, minDate(statements, StatementResponse::paymentDate));
        writeDate(sheet, 4, 8, maxDate(statements, StatementResponse::paymentDate));
        sheet.setColumnWidth(10, 14 * 256);
        sheet.setColumnWidth(11, 16 * 256);
        sheet.setColumnWidth(12, 22 * 256);
        sheet.setColumnWidth(13, 32 * 256);

        Row headerRow = getOrCreateRow(sheet, DETAIL_HEADER_ROW);
        for (int i = 0; i < DETAIL_HEADERS.size(); i++) {
            int column = FIRST_DETAIL_COLUMN + i;
            Cell headerCell = getOrCreateCell(headerRow, column);
            headerCell.setCellStyle(getOrCreateCell(
                    headerRow, DETAIL_STYLE_SOURCE_COLUMNS[i]).getCellStyle());
            writeText(headerRow, column, DETAIL_HEADERS.get(i));
        }

        clearDetailContents(sheet);

        int rowIndex = DETAIL_FIRST_ROW;
        BigDecimal monthTotal = ZERO;

        for (int i = 0; i < entries.size(); i++) {
            StatementEntryResponse entry = entries.get(i);
            Row row = getOrCreateRow(sheet, rowIndex);

            if (rowIndex != DETAIL_FIRST_ROW) {
                copyDetailRowStyles(sheet, DETAIL_FIRST_ROW, rowIndex);
            }

            if (i % 2 == 1) {
                applyAlternateStyle(workbook, sheet, rowIndex,
                        FIRST_DETAIL_COLUMN, LAST_DETAIL_COLUMN);
            }

            writeText(row, 1, entry.conceptName());
            writeText(row, 2, entry.description());
            writeText(row, 3, entry.merchant());
            writeText(row, 4, entry.debtor());

            if (entry.entryType() == StatementEntryType.RECURRING) {
                writeText(row, 5, "Permanente");
                writeText(row, 6, "Permanente");
            } else {
                writeDate(row, 5, entry.date());
                writeText(row, 6, msiLabel(entry));
            }

            writeText(row, 7, paidLabel(entry.paid()));
            if (entry.purchaseAmount() == null) {
                writeText(row, 9, "-");
            } else {
                writeMoney(row, 9, entry.purchaseAmount());
            }

            BigDecimal amount = amountOrZero(entry.amount());
            writeMoney(row, 8, amount);
            writeText(row, 10, entryTypeLabel(entry.entryType()));
            writeInteger(row, 11, entry.remainingMsi());
            writeOptionalMoney(row, 12, entry.remainingMsiAmount());
            writeText(row, 13, entry.notes());
            monthTotal = monthTotal.add(amount);
            rowIndex++;
        }

        // Conserva al menos una fila de detalle antes de la fila Total.
        if (rowIndex < DETAIL_HEADER_ROW + 2) {
            rowIndex = DETAIL_HEADER_ROW + 2;
            getOrCreateRow(sheet, DETAIL_FIRST_ROW);
        }

        Row totalRow = getOrCreateRow(sheet, rowIndex);
        copyDetailRowStyles(sheet, DETAIL_FIRST_ROW, rowIndex);
        writeText(totalRow, 1, "Total");
        for (int col = 2; col <= LAST_DETAIL_COLUMN; col++) {
            writeText(totalRow, col, "");
        }
        writeMoney(totalRow, 8, monthTotal);

        // Actualiza solo la tabla de movimientos de esta hoja.
        resizeDetailTable(sheet, monthName, rowIndex);

        // No agregamos AutoFilter: el filtro pertenece a la tabla de Excel.
        sheet.createFreezePane(FIRST_DETAIL_COLUMN, DETAIL_FIRST_ROW);

        for (int r = DETAIL_FIRST_ROW; r <= rowIndex; r++) {
            Row row = sheet.getRow(r);
            if (row != null && row.getHeightInPoints() <= 0) {
                row.setHeightInPoints(21);
            }
        }
    }

    /** Actualiza el rango de la tabla de movimientos sin eliminar la tabla. */
    private void resizeDetailTable(Sheet sheet, String monthName, int totalRowIndex) {
        if (!(sheet instanceof XSSFSheet xssfSheet)) {
            return;
        }

        for (XSSFTable table : xssfSheet.getTables()) {
            // La tabla de movimientos se llama igual que la hoja (Enero, Febrero, etc.).
            if (!monthName.equalsIgnoreCase(table.getName())) {
                continue;
            }

            int firstRow = DETAIL_HEADER_ROW;
            int lastRow = Math.max(firstRow + 2, totalRowIndex);

            AreaReference area = new AreaReference(
                    new CellReference(firstRow, FIRST_DETAIL_COLUMN),
                    new CellReference(lastRow, LAST_DETAIL_COLUMN),
                    SpreadsheetVersion.EXCEL2007);

            table.setArea(area);
            table.updateHeaders();
            return;
        }
    }

    private void populateTotalSheet(
            XSSFWorkbook workbook,
            List<StatementResponse> statements,
            List<StatementEntryResponse> allEntries,
            List<String> debtors) {

        Sheet sheet = workbook.getSheet("Total");
        if (sheet == null) {
            throw new IllegalStateException("La plantilla no contiene la hoja Total");
        }

        int visibleDebtors = Math.min(debtors.size(), 4);
        for (int i = 0; i < 4; i++) {
            writeText(sheet, 1, 2 + i, i < visibleDebtors ? debtors.get(i) : "");
        }
        writeText(sheet, 1, 6, "Total");

        for (int month = 1; month <= 12; month++) {
            final int monthNumber = month;
            int rowIndex = monthNumber + 1;
            writeText(sheet, rowIndex, 1, MONTH_NAMES.get(monthNumber - 1));

            Set<Long> monthStatementIds = statements.stream()
                    .filter(s -> Objects.equals(s.month(), monthNumber))
                    .map(StatementResponse::statementId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            List<StatementEntryResponse> monthEntries = allEntries.stream()
                    .filter(e -> monthStatementIds.contains(e.statementId()))
                    .toList();

            BigDecimal rowTotal = ZERO;
            for (int debtorIndex = 0; debtorIndex < 4; debtorIndex++) {
                String debtor = debtorIndex < visibleDebtors
                        ? debtors.get(debtorIndex)
                        : "";
                BigDecimal debtorTotal = debtor.isBlank()
                        ? ZERO
                        : sum(monthEntries, entry -> debtor.equals(entry.debtor()));

                writeMoney(sheet, rowIndex, 2 + debtorIndex, debtorTotal);
                rowTotal = rowTotal.add(debtorTotal);
            }
            writeMoney(sheet, rowIndex, 6, rowTotal);
        }

        int totalRow = 14;
        writeText(sheet, totalRow, 1, "Total");
        for (int col = 2; col <= 6; col++) {
            BigDecimal total = ZERO;
            for (int row = 2; row <= 13; row++) {
                Row currentRow = sheet.getRow(row);
                Cell cell = currentRow == null ? null : currentRow.getCell(col);
                if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                    total = total.add(BigDecimal.valueOf(cell.getNumericCellValue()));
                }
            }
            writeMoney(sheet, totalRow, col, total);
        }
    }

    private void clearDetailContents(Sheet sheet) {
        int last = Math.max(sheet.getLastRowNum(), DETAIL_FIRST_ROW + 1);
        for (int r = DETAIL_FIRST_ROW; r <= last; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            for (int c = FIRST_DETAIL_COLUMN; c <= LAST_DETAIL_COLUMN; c++) {
                Cell cell = row.getCell(c);
                if (cell != null) {
                    cell.setBlank();
                }
            }
        }
    }

    private void copyDetailRowStyles(Sheet sheet, int sourceRowIndex, int targetRowIndex) {
        Row source = sheet.getRow(sourceRowIndex);
        Row target = getOrCreateRow(sheet, targetRowIndex);
        if (source == null) {
            return;
        }

        target.setHeight(source.getHeight());
        for (int i = 0; i < DETAIL_STYLE_SOURCE_COLUMNS.length; i++) {
            Cell sourceCell = source.getCell(DETAIL_STYLE_SOURCE_COLUMNS[i]);
            Cell targetCell = getOrCreateCell(target, FIRST_DETAIL_COLUMN + i);
            if (sourceCell != null) {
                targetCell.setCellStyle(sourceCell.getCellStyle());
            }
        }
    }

    private void applyAlternateStyle(
            Workbook workbook, Sheet sheet, int rowIndex,
            int firstColumn, int lastColumn) {

        Row templateRow = sheet.getRow(DETAIL_FIRST_ROW);
        Row row = getOrCreateRow(sheet, rowIndex);
        for (int col = firstColumn; col <= lastColumn; col++) {
            Cell cell = getOrCreateCell(row, col);
            int sourceColumn = DETAIL_STYLE_SOURCE_COLUMNS[col - firstColumn];
            Cell templateCell = templateRow == null ? null : templateRow.getCell(sourceColumn);
            if (templateCell == null) {
                continue;
            }

            CellStyle style = workbook.createCellStyle();
            style.cloneStyleFrom(templateCell.getCellStyle());
            style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cell.setCellStyle(style);
        }
    }

    private static List<String> sortedDebtors(
            XSSFWorkbook workbook, List<StatementEntryResponse> entries) {

        LinkedHashSet<String> result = new LinkedHashSet<>();
        Sheet templateMonth = workbook.getSheet("Enero");
        if (templateMonth != null) {
            Row header = templateMonth.getRow(1);
            if (header != null) {
                for (int col = 2; col <= 5; col++) {
                    Cell cell = header.getCell(col);
                    if (cell != null && cell.getCellType() == CellType.STRING
                            && !cell.getStringCellValue().isBlank()) {
                        result.add(cell.getStringCellValue().trim());
                    }
                }
            }
        }

        entries.stream()
                .map(StatementEntryResponse::debtor)
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .filter(name -> !result.contains(name))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .forEach(result::add);
        return new ArrayList<>(result);
    }

    private static BigDecimal sum(
            List<StatementEntryResponse> entries,
            Predicate<StatementEntryResponse> predicate) {
        return entries.stream()
                .filter(predicate)
                .map(StatementEntryResponse::amount)
                .map(StatementExcelWorkbookGenerator::amountOrZero)
                .reduce(ZERO, BigDecimal::add);
    }

    private static LocalDate minDate(
            List<StatementResponse> statements,
            Function<StatementResponse, LocalDate> extractor) {
        return statements.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    private static LocalDate maxDate(
            List<StatementResponse> statements,
            Function<StatementResponse, LocalDate> extractor) {
        return statements.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    private static BigDecimal amountOrZero(BigDecimal amount) {
        return amount == null ? ZERO : amount;
    }

    private static String msiLabel(StatementEntryResponse entry) {
        if (entry.msiCurrent() == null || entry.msiTotal() == null) {
            return "No";
        }
        return entry.msiCurrent() + " de " + entry.msiTotal();
    }

    private static String paidLabel(Boolean paid) {
        if (paid == null) {
            return "";
        }
        return paid ? "Sí" : "No";
    }

    private static String entryTypeLabel(StatementEntryType entryType) {
        if (entryType == null) {
            return "";
        }
        return entryType == StatementEntryType.RECURRING ? "Recurrente" : "Compra";
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static Row getOrCreateRow(Sheet sheet, int index) {
        Row row = sheet.getRow(index);
        return row == null ? sheet.createRow(index) : row;
    }

    private static Cell getOrCreateCell(Row row, int index) {
        Cell cell = row.getCell(index);
        return cell == null ? row.createCell(index) : cell;
    }

    private static void writeText(Sheet sheet, int rowIndex, int colIndex, String value) {
        writeText(getOrCreateRow(sheet, rowIndex), colIndex, value);
    }

    private static void writeText(Row row, int colIndex, String value) {
        getOrCreateCell(row, colIndex).setCellValue(value == null ? "" : value);
    }

    private static void writeMoney(
            Sheet sheet, int rowIndex, int colIndex, BigDecimal value) {
        writeMoney(getOrCreateRow(sheet, rowIndex), colIndex, value);
    }

    private static void writeMoney(Row row, int colIndex, BigDecimal value) {
        Cell cell = getOrCreateCell(row, colIndex);
        cell.setCellValue(amountOrZero(value).doubleValue());

        CellStyle currentStyle = cell.getCellStyle();
        String format = currentStyle.getDataFormatString();
        if (format == null || !format.contains("$")) {
            CellStyle currencyStyle = row.getSheet().getWorkbook().createCellStyle();
            currencyStyle.cloneStyleFrom(currentStyle);
            currencyStyle.setDataFormat(row.getSheet().getWorkbook()
                    .createDataFormat().getFormat(CURRENCY_FORMAT));
            cell.setCellStyle(currencyStyle);
        }
    }

    private static void writeOptionalMoney(Row row, int colIndex, BigDecimal value) {
        if (value == null) {
            getOrCreateCell(row, colIndex).setBlank();
            return;
        }
        writeMoney(row, colIndex, value);
    }

    private static void writeInteger(Row row, int colIndex, Integer value) {
        Cell cell = getOrCreateCell(row, colIndex);
        if (value == null) {
            cell.setBlank();
        } else {
            cell.setCellValue(value);
        }
    }

    private static void writeDate(
            Sheet sheet, int rowIndex, int colIndex, LocalDate date) {
        writeDate(getOrCreateRow(sheet, rowIndex), colIndex, date);
    }

    private static void writeDate(Row row, int colIndex, LocalDate date) {
        Cell cell = getOrCreateCell(row, colIndex);
        if (date == null) {
            cell.setBlank();
            return;
        }

        cell.setCellValue(java.sql.Date.valueOf(date));
        CellStyle style = row.getSheet().getWorkbook().createCellStyle();
        style.cloneStyleFrom(cell.getCellStyle());
        style.setDataFormat(row.getSheet().getWorkbook()
                .createDataFormat().getFormat(DATE_FORMAT));
        cell.setCellStyle(style);
    }
}
