package by.bsu.pischule.service;

import by.bsu.pischule.model.FormData;
import by.bsu.pischule.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TemplateService {

    public InputStream getDocument() {
        return getClass().getClassLoader().getResourceAsStream("template.docx");
    }

    public InputStream fillDocument(FormData formData, Collection<Transaction> transactions) throws IOException, XmlException {

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        Map<String, String> words = Map.of(
                "{OWNER_NAME}", formData.getName(),
                "{ACCOUNT_NUMBER}", formData.getAccount() + "",
                "{DATE_FROM}", df.format(formData.getDateFrom()),
                "{DATE_TO}", df.format(formData.getDateTo())
        );

        List<Map<String, String>> data = transactions.stream()
                .map(r -> Map.of(
                        "{DATE}", df.format(r.getDate()),
                        "{ID}", r.getId() + "",
                        "{DESCRIPTION}", r.getDescription(),
                        "{CURRENCY}", r.getCurrency(),
                        "{AMOUNT}", r.getAmount().toString()
                )).collect(Collectors.toList());

        Function<String, String> textTransformFunction;
        if (Boolean.TRUE.equals(formData.getAllCaps())) {
            textTransformFunction = String::toUpperCase;
        } else {
            textTransformFunction = Function.identity();
        }

        InputStream stream;
        if (formData.getTemplate() == null) {
            stream = getDocument();
        } else {
            stream = new ByteArrayInputStream(formData.getTemplate());
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             XWPFDocument document = new XWPFDocument(stream)) {

            substituteParagraphWords(document, words);
            substituteTable(document, data, textTransformFunction);
            if (Boolean.TRUE.equals(formData.getAllCaps())) {
                document.getHeaderList().forEach(h -> transformBody(h, textTransformFunction));
                transformBody(document, textTransformFunction);
                document.getFooterList().forEach(f -> transformBody(f, textTransformFunction));
            }
            document.write(outputStream);
            stream.close();

            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }

    public void substituteTable(XWPFDocument document, List<Map<String, String>> replaceRows,
                                Function<String, String> textTransform) throws XmlException, IOException {
        for (Iterator<XWPFTable> it = document.getTablesIterator(); it.hasNext(); ) {
            XWPFTable tbl = it.next();
            List<XWPFTableRow> rows = tbl.getRows();
            if (rows.size() != 2) continue;

            XWPFTableRow rowTemplate = copyRow(tbl.getRow(1), tbl);
            tbl.removeRow(1);

            for (Map<String, String> dataRow : replaceRows) {
                XWPFTableRow row = copyRow(rowTemplate, tbl);
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            String text = r.getText(0);
                            if (text == null) continue;
                            for (String k : dataRow.keySet()) {
                                text = text.replace(k, dataRow.get(k));
                            }
                            r.setText(textTransform.apply(text), 0);
                        }
                    }
                }
                tbl.addRow(row);
            }
        }
    }

    private XWPFTableRow copyRow(XWPFTableRow row, XWPFTable tbl) throws XmlException, IOException {
        CTRow ctrow = CTRow.Factory.parse(row.getCtRow().newInputStream());
        return new XWPFTableRow(ctrow, tbl);
    }

    public void substituteParagraphWords(XWPFDocument document, Map<String, String> replaceWords) {
        for (Iterator<XWPFParagraph> it = document.getParagraphsIterator(); it.hasNext(); ) {
            XWPFParagraph p = it.next();
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text == null) continue;
                    for (String k : replaceWords.keySet()) {
                        if (!text.contains(k)) continue;
                        text = text.replace(k, replaceWords.get(k));
                    }
                    r.setText(text, 0);
                }
            }
        }
    }

    private static void transformBody(IBody documentPart, Function<String, String> transformFunction) {
        for (XWPFParagraph p : documentPart.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null) {
                        r.setText(transformFunction.apply(text), 0);
                    }
                }
            }
        }
        for (XWPFTable tbl : documentPart.getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            String text = r.getText(0);
                            if (text != null) {
                                r.setText(transformFunction.apply(text), 0);
                            }
                        }
                    }
                }
            }
        }
    }
}
