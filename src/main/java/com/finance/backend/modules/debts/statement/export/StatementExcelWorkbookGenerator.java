package com.finance.backend.modules.debts.statement.export;

import com.finance.backend.modules.debts.statement.dto.StatementResponse;
import com.finance.backend.modules.debts.statemententry.dto.StatementEntryResponse;
import com.finance.backend.modules.debts.statemententry.model.StatementEntryType;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class StatementExcelWorkbookGenerator {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final String CURRENCY_FORMAT = "$#,##0.00;[Red]($#,##0.00)";
    private static final String TOTAL_CURRENCY_FORMAT = "$#,##0.00;[Red]($#,##0.00);-";
    private static final String DATE_FORMAT = "dd/mm/yyyy";
    private static final int LEFT_SPACER_COLUMNS = 1;
    private static final int SUMMARY_HEADER_ROW = 0;
    private static final int CHIP_ROW = 5;
    private static final int DETAIL_HEADER_ROW = 7;
    private static final List<String> MONTH_NAMES = List.of(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre");

    public byte[] generate(
            List<StatementResponse> statements,
            List<StatementEntryResponse> entries) {

        try (XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            WorkbookStyles styles = new WorkbookStyles(workbook);
            createMonthSheets(workbook, styles, statements, entries);
            createTotalSheet(workbook, styles, statements, entries);
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new UncheckedIOException("No se pudo generar el archivo Excel", exception);
        }
    }

    private void createMonthSheets(
            XSSFWorkbook workbook,
            WorkbookStyles styles,
            List<StatementResponse> statements,
            List<StatementEntryResponse> entries) {

        for (int month = 1; month <= MONTH_NAMES.size(); month++) {
            int monthNumber = month;
            List<StatementResponse> monthStatements = statements.stream()
                    .filter(statement -> Objects.equals(statement.month(), monthNumber))
                    .toList();
            Set<Long> statementIds = monthStatements.stream()
                    .map(StatementResponse::statementId)
                    .collect(Collectors.toSet());
            List<StatementEntryResponse> monthEntries = entries.stream()
                    .filter(entry -> statementIds.contains(entry.statementId()))
                    .toList();

            createReportSheet(
                    workbook,
                    styles,
                    MONTH_NAMES.get(month - 1),
                    monthStatements,
                    monthEntries);
        }
    }

    private void createTotalSheet(
            XSSFWorkbook workbook,
            WorkbookStyles styles,
            List<StatementResponse> statements,
            List<StatementEntryResponse> entries) {

        Sheet sheet = workbook.createSheet("Total");
        List<String> debtors = entries.stream()
                .map(StatementEntryResponse::debtor)
                .filter(debtor -> debtor != null && !debtor.isBlank())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(LinkedHashSet::new),
                        ArrayList::new));

        int monthColumn = LEFT_SPACER_COLUMNS;
        int firstDebtorColumn = monthColumn + 1;
        int totalColumn = firstDebtorColumn + debtors.size();

        Row header = row(sheet, 0);
        setText(header, monthColumn, "", styles.header);
        for (int index = 0; index < debtors.size(); index++) {
            setText(header, firstDebtorColumn + index, debtors.get(index), styles.header);
        }
        setText(header, totalColumn, "Total", styles.header);

        BigDecimal[] debtorTotals = new BigDecimal[debtors.size()];
        java.util.Arrays.fill(debtorTotals, ZERO);

        for (int month = 1; month <= MONTH_NAMES.size(); month++) {
            int rowIndex = month;
            List<StatementResponse> monthStatements = statements.stream()
                    .filter(statement -> Objects.equals(statement.month(), rowIndex))
                    .toList();
            Set<Long> statementIds = monthStatements.stream()
                    .map(StatementResponse::statementId)
                    .collect(Collectors.toSet());
            List<StatementEntryResponse> monthEntries = entries.stream()
                    .filter(entry -> statementIds.contains(entry.statementId()))
                    .toList();

            Row row = row(sheet, month);
            setText(row, monthColumn, MONTH_NAMES.get(month - 1), styles.matrixMonth);

            for (int debtorIndex = 0; debtorIndex < debtors.size(); debtorIndex++) {
                String debtor = debtors.get(debtorIndex);
                BigDecimal subtotal = sum(monthEntries, entry -> debtor.equals(entry.debtor()));
                setMoney(row, firstDebtorColumn + debtorIndex, subtotal, styles.matrixCurrency);
                debtorTotals[debtorIndex] = debtorTotals[debtorIndex].add(subtotal);
            }

            BigDecimal monthTotal = sum(monthEntries, ignored -> true);
            if (monthTotal.compareTo(ZERO) == 0) {
                setText(row, totalColumn, "-", styles.matrixTotalCurrency);
            } else {
                setMoney(row, totalColumn, monthTotal, styles.matrixTotalCurrency);
            }
        }

        Row grandTotalRow = row(sheet, MONTH_NAMES.size() + 1);
        setText(grandTotalRow, monthColumn, "Total", styles.matrixMonth);
        for (int index = 0; index < debtors.size(); index++) {
            setMoney(grandTotalRow, firstDebtorColumn + index, debtorTotals[index], styles.matrixGrandCurrency);
        }
        BigDecimal annualTotal = sum(entries, ignored -> true);
        setMoney(grandTotalRow, totalColumn, annualTotal, styles.matrixGrandCurrency);

        sheet.setColumnWidth(0, 3 * 256);
        sheet.setColumnWidth(monthColumn, 24 * 256);
        for (int index = 0; index < debtors.size(); index++) {
            sheet.setColumnWidth(firstDebtorColumn + index, 24 * 256);
        }
        sheet.setColumnWidth(totalColumn, 24 * 256);
        sheet.setDefaultRowHeightInPoints(25);
        sheet.createFreezePane(firstDebtorColumn, 1);
    }

    private void createReportSheet(
            XSSFWorkbook workbook,
            WorkbookStyles styles,
            String sheetName,
            List<StatementResponse> statements,
            List<StatementEntryResponse> entries) {

        Sheet sheet = workbook.createSheet(sheetName);
        setColumnWidths(sheet);

        List<String> debtors = entries.stream()
                .map(StatementEntryResponse::debtor)
                .filter(debtor -> debtor != null && !debtor.isBlank())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(LinkedHashSet::new),
                        ArrayList::new));

        int totalColumn = debtors.size() + 1;
        int datePanelColumn = Math.max(totalColumn + 2, 7) + LEFT_SPACER_COLUMNS;
        createSummary(sheet, styles, entries, debtors, totalColumn, LEFT_SPACER_COLUMNS);
        createDatePanel(sheet, styles, statements, datePanelColumn);
        createDebtorChips(sheet, styles, debtors, LEFT_SPACER_COLUMNS, datePanelColumn + 1);
        createDetails(sheet, styles, entries, LEFT_SPACER_COLUMNS);

        int lastColumn = Math.max(datePanelColumn + 1, 7);
        for (int column = 0; column <= lastColumn; column++) {
            if (column == 0) {
                sheet.setColumnWidth(column, 3 * 256);
            } else {
                sheet.setColumnWidth(column, Math.max(sheet.getColumnWidth(column), 12 * 256));
            }
        }

        sheet.setAutoFilter(new CellRangeAddress(
                DETAIL_HEADER_ROW,
                DETAIL_HEADER_ROW + entries.size(),
                LEFT_SPACER_COLUMNS,
                LEFT_SPACER_COLUMNS + 7));
        sheet.createFreezePane(LEFT_SPACER_COLUMNS, DETAIL_HEADER_ROW + 1);
        sheet.setDefaultRowHeightInPoints(21);
        sheet.getRow(SUMMARY_HEADER_ROW).setHeightInPoints(25);
        sheet.getRow(CHIP_ROW).setHeightInPoints(43);
        sheet.getRow(DETAIL_HEADER_ROW).setHeightInPoints(25);
    }

    private void createSummary(
            Sheet sheet,
            WorkbookStyles styles,
            List<StatementEntryResponse> entries,
            List<String> debtors,
            int totalColumn,
            int columnOffset) {

        Row header = row(sheet, SUMMARY_HEADER_ROW);
        setText(header, columnOffset, "Concepto", styles.header);
        for (int index = 0; index < debtors.size(); index++) {
            setText(header, columnOffset + index + 1, debtors.get(index), styles.header);
        }
        setText(header, columnOffset + totalColumn, "Total", styles.header);

        List<SummaryRow> summaryRows = List.of(
                new SummaryRow("Total Gastado", ignored -> true),
                new SummaryRow("Monto Pagado", entry -> Boolean.TRUE.equals(entry.paid())),
                new SummaryRow("Monto Restante", entry -> !Boolean.TRUE.equals(entry.paid())));

        for (int rowIndex = 0; rowIndex < summaryRows.size(); rowIndex++) {
            SummaryRow summary = summaryRows.get(rowIndex);
            Row row = row(sheet, SUMMARY_HEADER_ROW + rowIndex + 1);
            setText(row, columnOffset, summary.label(), styles.summaryBody);

            BigDecimal grandTotal = ZERO;
            for (int debtorIndex = 0; debtorIndex < debtors.size(); debtorIndex++) {
                String debtor = debtors.get(debtorIndex);
                BigDecimal subtotal = sum(
                        entries,
                        entry -> debtor.equals(entry.debtor()) && summary.predicate().test(entry));
                setMoney(row, columnOffset + debtorIndex + 1, subtotal, styles.summaryCurrency);
                grandTotal = grandTotal.add(subtotal);
            }

            if (debtors.isEmpty()) {
                grandTotal = sum(entries, summary.predicate());
            }
            setMoney(row, columnOffset + totalColumn, grandTotal, styles.summaryCurrency);
        }
    }

    private void createDatePanel(
            Sheet sheet,
            WorkbookStyles styles,
            List<StatementResponse> statements,
            int startColumn) {

        Row cutoffHeader = row(sheet, 0);
        mergeHeader(sheet, cutoffHeader, startColumn, "Fecha del corte", styles.cutoffHeader);
        Row cutoffValues = row(sheet, 1);
        setDate(cutoffValues, startColumn, minDate(statements, StatementResponse::periodStart), styles.date);
        setDate(cutoffValues, startColumn + 1, maxDate(statements, StatementResponse::periodEnd), styles.date);

        Row paymentHeader = row(sheet, 2);
        mergeHeader(sheet, paymentHeader, startColumn, "Fecha del pago", styles.paymentHeader);
        Row paymentValues = row(sheet, 3);
        setDate(paymentValues, startColumn, minDate(statements, StatementResponse::paymentDate), styles.date);
        setDate(paymentValues, startColumn + 1, maxDate(statements, StatementResponse::paymentDate), styles.date);
    }

    private void createDebtorChips(
            Sheet sheet,
            WorkbookStyles styles,
            List<String> debtors,
            int firstColumn,
            int lastColumn) {

        Row chipRow = row(sheet, CHIP_ROW);
        for (int column = firstColumn; column <= lastColumn; column++) {
            setText(chipRow, column, "", styles.chipArea);
        }
        for (int index = 0; index < debtors.size(); index++) {
            setText(chipRow, firstColumn + index, debtors.get(index), styles.chip);
        }
    }

    private void createDetails(
            Sheet sheet,
            WorkbookStyles styles,
            List<StatementEntryResponse> entries,
            int columnOffset) {

        Row header = row(sheet, DETAIL_HEADER_ROW);
        List<String> headers = List.of(
                "Concepto", "Especificación", "Deudor", "Fecha", "MSI", "Pagado", "Precio", "Total");
        for (int column = 0; column < headers.size(); column++) {
            setText(header, columnOffset + column, headers.get(column), styles.header);
        }

        List<StatementEntryResponse> orderedEntries = entries.stream()
                .sorted(Comparator
                        .comparing(StatementEntryResponse::date, Comparator.nullsFirst(Comparator.naturalOrder()))
                        .thenComparing(entry -> safeText(entry.conceptName()), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(entry -> safeText(entry.debtor()), String.CASE_INSENSITIVE_ORDER))
                .toList();

        BigDecimal statementTotal = ZERO;
        for (int index = 0; index < orderedEntries.size(); index++) {
            StatementEntryResponse entry = orderedEntries.get(index);
            boolean alternate = index % 2 == 1;
            Row row = row(sheet, DETAIL_HEADER_ROW + index + 1);
            setText(row, columnOffset, entry.conceptName(), alternate ? styles.bodyAlt : styles.body);
            setText(row, columnOffset + 1, entry.specification(), alternate ? styles.bodyAlt : styles.body);
            setText(row, columnOffset + 2, entry.debtor(), alternate ? styles.bodyAlt : styles.body);

            if (entry.entryType() == StatementEntryType.RECURRING) {
                setText(row, columnOffset + 3, "Permanente", alternate ? styles.centeredBodyAlt : styles.centeredBody);
                setText(row, columnOffset + 4, "Permanente", alternate ? styles.centeredBodyAlt : styles.centeredBody);
            } else {
                if (entry.date() == null) {
                    setText(row, columnOffset + 3, "-", alternate ? styles.centeredBodyAlt : styles.centeredBody);
                } else {
                    setDate(row, columnOffset + 3, entry.date(), alternate ? styles.dateAlt : styles.date);
                }
                setText(row, columnOffset + 4, msiLabel(entry), alternate ? styles.centeredBodyAlt : styles.centeredBody);
            }

            setText(row, columnOffset + 5, paidLabel(entry.paid()), alternate ? styles.centeredBodyAlt : styles.centeredBody);
            if (entry.purchaseAmount() == null) {
                setText(row, columnOffset + 6, "-", alternate ? styles.centeredBodyAlt : styles.centeredBody);
            } else {
                setMoney(row, columnOffset + 6, entry.purchaseAmount(), alternate ? styles.currencyAlt : styles.currency);
            }
            BigDecimal amount = amountOrZero(entry.amount());
            setMoney(row, columnOffset + 7, amount, alternate ? styles.currencyAlt : styles.currency);
            statementTotal = statementTotal.add(amount);
        }

        Row totalRow = row(sheet, DETAIL_HEADER_ROW + orderedEntries.size() + 1);
        setText(totalRow, columnOffset, "Total", styles.totalLabel);
        for (int column = 1; column < 7; column++) {
            setText(totalRow, columnOffset + column, "", styles.totalBody);
        }
        setMoney(totalRow, columnOffset + 7, statementTotal, styles.totalCurrency);
    }

    private static void setColumnWidths(Sheet sheet) {
        int[] widths = {28, 30, 22, 17, 14, 14, 18, 18};
        sheet.setColumnWidth(0, 3 * 256);
        for (int column = 0; column < widths.length; column++) {
            sheet.setColumnWidth(LEFT_SPACER_COLUMNS + column, widths[column] * 256);
        }
    }

    private static void mergeHeader(
            Sheet sheet,
            Row row,
            int startColumn,
            String value,
            CellStyle style) {

        setText(row, startColumn, value, style);
        setText(row, startColumn + 1, "", style);
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), startColumn, startColumn + 1));
    }

    private static LocalDate minDate(
            List<StatementResponse> statements,
            java.util.function.Function<StatementResponse, LocalDate> dateExtractor) {

        return statements.stream()
                .map(dateExtractor)
                .filter(date -> date != null)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    private static LocalDate maxDate(
            List<StatementResponse> statements,
            java.util.function.Function<StatementResponse, LocalDate> dateExtractor) {

        return statements.stream()
                .map(dateExtractor)
                .filter(date -> date != null)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    private BigDecimal sum(
            List<StatementEntryResponse> entries,
            Predicate<StatementEntryResponse> predicate) {

        return entries.stream()
                .filter(predicate)
                .map(StatementEntryResponse::amount)
                .map(StatementExcelWorkbookGenerator::amountOrZero)
                .reduce(ZERO, BigDecimal::add);
    }

    private static BigDecimal amountOrZero(BigDecimal amount) {
        return amount == null ? ZERO : amount;
    }

    private static String msiLabel(StatementEntryResponse entry) {
        if (entry.msiCurrent() == null || entry.msiTotal() == null) {
            return "No";
        }
        return entry.msiCurrent() + "/" + entry.msiTotal();
    }

    private static String paidLabel(Boolean paid) {
        if (paid == null) {
            return "";
        }
        return paid ? "Sí" : "No";
    }

    private static String safeText(String value) {
        return value == null ? "" : value;
    }

    private static void setText(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(safeText(value));
        cell.setCellStyle(style);
    }

    private static void setMoney(Row row, int column, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(amountOrZero(value).doubleValue());
        cell.setCellStyle(style);
    }

    private static void setDate(Row row, int column, LocalDate value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value != null) {
            cell.setCellValue(java.sql.Date.valueOf(value));
        }
        cell.setCellStyle(style);
    }

    private record SummaryRow(String label, Predicate<StatementEntryResponse> predicate) {
    }

    private static final class WorkbookStyles {

        private final CellStyle header;
        private final CellStyle body;
        private final CellStyle bodyAlt;
        private final CellStyle centeredBody;
        private final CellStyle centeredBodyAlt;
        private final CellStyle summaryBody;
        private final CellStyle date;
        private final CellStyle dateAlt;
        private final CellStyle currency;
        private final CellStyle currencyAlt;
        private final CellStyle cutoffHeader;
        private final CellStyle paymentHeader;
        private final CellStyle chip;
        private final XSSFCellStyle chipArea;
        private final CellStyle totalLabel;
        private final CellStyle totalBody;
        private final CellStyle totalCurrency;
        private final CellStyle summaryCurrency;
        private final CellStyle matrixMonth;
        private final CellStyle matrixCurrency;
        private final CellStyle matrixTotalCurrency;
        private final CellStyle matrixGrandCurrency;

        private WorkbookStyles(XSSFWorkbook workbook) {
            DataFormat dataFormat = workbook.createDataFormat();

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLACK.getIndex());
            header = coloredStyle(workbook, headerFont, 242, 194, 213);
            header.setAlignment(HorizontalAlignment.CENTER);
            header.setWrapText(true);

            body = baseStyle(workbook, null);
            bodyAlt = coloredStyle(workbook, null, 242, 242, 242);
            centeredBody = copyWithAlignment(workbook, body, HorizontalAlignment.CENTER);
            centeredBodyAlt = copyWithAlignment(workbook, bodyAlt, HorizontalAlignment.CENTER);

            summaryBody = baseStyle(workbook, null);
            summaryBody.setAlignment(HorizontalAlignment.CENTER);

            date = copyWithAlignment(workbook, body, HorizontalAlignment.CENTER);
            date.setDataFormat(dataFormat.getFormat(DATE_FORMAT));
            dateAlt = copyWithAlignment(workbook, bodyAlt, HorizontalAlignment.CENTER);
            dateAlt.setDataFormat(dataFormat.getFormat(DATE_FORMAT));

            currency = copyWithAlignment(workbook, body, HorizontalAlignment.RIGHT);
            currency.setDataFormat(dataFormat.getFormat(CURRENCY_FORMAT));
            currencyAlt = copyWithAlignment(workbook, bodyAlt, HorizontalAlignment.RIGHT);
            currencyAlt.setDataFormat(dataFormat.getFormat(CURRENCY_FORMAT));

            Font cutoffFont = workbook.createFont();
            cutoffFont.setBold(true);
            cutoffHeader = coloredStyle(workbook, cutoffFont, 190, 226, 174);
            cutoffHeader.setAlignment(HorizontalAlignment.CENTER);

            Font paymentFont = workbook.createFont();
            paymentFont.setBold(true);
            paymentHeader = coloredStyle(workbook, paymentFont, 247, 203, 169);
            paymentHeader.setAlignment(HorizontalAlignment.CENTER);

            Font chipFont = workbook.createFont();
            chipFont.setColor(IndexedColors.WHITE.getIndex());
            chip = coloredStyle(workbook, chipFont, 139, 204, 74);
            chip.setAlignment(HorizontalAlignment.LEFT);
            chip.setWrapText(true);

            chipArea = workbook.createCellStyle();
            chipArea.setBorderTop(BorderStyle.THIN);
            chipArea.setBorderBottom(BorderStyle.THIN);
            chipArea.setBorderLeft(BorderStyle.THIN);
            chipArea.setBorderRight(BorderStyle.THIN);
            chipArea.setTopBorderColor(rgb(112, 173, 71));
            chipArea.setBottomBorderColor(rgb(112, 173, 71));
            chipArea.setLeftBorderColor(rgb(112, 173, 71));
            chipArea.setRightBorderColor(rgb(112, 173, 71));

            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalLabel = coloredStyle(workbook, totalFont, 232, 232, 232);
            totalBody = coloredStyle(workbook, totalFont, 232, 232, 232);
            totalCurrency = copyWithAlignment(workbook, totalBody, HorizontalAlignment.RIGHT);
            totalCurrency.setDataFormat(dataFormat.getFormat(CURRENCY_FORMAT));

            summaryCurrency = copyWithAlignment(workbook, body, HorizontalAlignment.RIGHT);
            summaryCurrency.setDataFormat(dataFormat.getFormat(CURRENCY_FORMAT));

            Font matrixMonthFont = workbook.createFont();
            matrixMonthFont.setBold(true);
            matrixMonth = coloredStyle(workbook, matrixMonthFont, 242, 194, 213);
            matrixMonth.setAlignment(HorizontalAlignment.CENTER);

            matrixCurrency = copyWithAlignment(workbook, body, HorizontalAlignment.RIGHT);
            matrixCurrency.setDataFormat(dataFormat.getFormat(CURRENCY_FORMAT));

            matrixTotalCurrency = coloredStyle(workbook, null, 238, 238, 238);
            matrixTotalCurrency.setAlignment(HorizontalAlignment.RIGHT);
            matrixTotalCurrency.setDataFormat(dataFormat.getFormat(TOTAL_CURRENCY_FORMAT));

            Font matrixGrandFont = workbook.createFont();
            matrixGrandFont.setBold(true);
            matrixGrandCurrency = coloredStyle(workbook, matrixGrandFont, 232, 232, 232);
            matrixGrandCurrency.setAlignment(HorizontalAlignment.RIGHT);
            matrixGrandCurrency.setDataFormat(dataFormat.getFormat(CURRENCY_FORMAT));

        }

        private static CellStyle baseStyle(XSSFWorkbook workbook, Font font) {
            CellStyle style = workbook.createCellStyle();
            if (font != null) {
                style.setFont(font);
            }
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            setThinBorders(style);
            return style;
        }

        private static XSSFCellStyle coloredStyle(XSSFWorkbook workbook, Font font, int red, int green, int blue) {
            XSSFCellStyle style = workbook.createCellStyle();
            if (font != null) {
                style.setFont(font);
            }
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setFillForegroundColor(new XSSFColor(
                    new byte[]{(byte) red, (byte) green, (byte) blue},
                    new DefaultIndexedColorMap()));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setThinBorders(style);
            return style;
        }

        private static CellStyle copyWithAlignment(
                XSSFWorkbook workbook,
                CellStyle source,
                HorizontalAlignment alignment) {

            CellStyle style = workbook.createCellStyle();
            style.cloneStyleFrom(source);
            style.setAlignment(alignment);
            return style;
        }

        private static XSSFColor rgb(int red, int green, int blue) {
            return new XSSFColor(new byte[]{(byte) red, (byte) green, (byte) blue}, new DefaultIndexedColorMap());
        }

        private static void setThinBorders(CellStyle style) {
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
        }
    }

    private static Row row(Sheet sheet, int index) {
        Row existing = sheet.getRow(index);
        return existing == null ? sheet.createRow(index) : existing;
    }
}
