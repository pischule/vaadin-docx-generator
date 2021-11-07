package by.bsu.pischule.service

import by.bsu.pischule.model.Parameters
import by.bsu.pischule.model.Transaction
import org.apache.poi.xwpf.usermodel.IBody
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.apache.poi.xwpf.usermodel.XWPFTableRow
import org.apache.xmlbeans.XmlException
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.time.format.DateTimeFormatter
import java.util.function.Consumer
import java.util.function.Function

@Service
class TemplateService {
    private val document: InputStream
        get() = javaClass.classLoader.getResourceAsStream("template.docx")

    @Throws(IOException::class, XmlException::class)
    fun fillDocument(parameters: Parameters, transactions: Collection<Transaction>): InputStream {
        val df = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val words = mapOf(
            "{OWNER_NAME}" to parameters.name,
            "{ACCOUNT_NUMBER}" to parameters.account.toString() + "",
            "{DATE_FROM}" to df.format(parameters.dateFrom),
            "{DATE_TO}" to df.format(parameters.dateTo)
        )

        val data = transactions.map {
            mapOf(
                "{DATE}" to df.format(it.date),
                "{ID}" to it.id.toString() + "",
                "{DESCRIPTION}" to it.description,
                "{CURRENCY}" to it.currency,
                "{AMOUNT}" to it.amount.toString()
            )
        }.toList()

        val textTransformFunction: Function<String?, String?> = if (parameters.allCaps) {
            Function { obj: String? -> obj!!.uppercase() }
        } else {
            Function.identity()
        }

        val stream: InputStream = if (parameters.template == null) {
            document
        } else {
            ByteArrayInputStream(parameters.template)
        }

        ByteArrayOutputStream().use { outputStream ->
            XWPFDocument(stream).use { document ->
                substituteParagraphWords(document, words)
                substituteTable(document, data, textTransformFunction)
                if (parameters.allCaps) {
                    document.headerList
                        .forEach(Consumer { transformBody(it, textTransformFunction) })
                    transformBody(document, textTransformFunction)
                    document.footerList
                        .forEach(Consumer { transformBody(it, textTransformFunction) })
                }
                document.write(outputStream)
                stream.close()
                return ByteArrayInputStream(outputStream.toByteArray())
            }
        }
    }

    @Throws(XmlException::class, IOException::class)
    fun substituteTable(
        document: XWPFDocument, replaceRows: List<Map<String, String>>,
        textTransform: Function<String?, String?>
    ) {
        val it = document.tablesIterator
        while (it.hasNext()) {
            val tbl = it.next()
            val rows = tbl.rows
            if (rows.size != 2) {
                continue
            }
            val rowTemplate = copyRow(tbl.getRow(1), tbl)
            tbl.removeRow(1)
            for (dataRow in replaceRows) {
                val row = copyRow(rowTemplate, tbl)
                for (cell in row.tableCells) {
                    for (p in cell.paragraphs) {
                        for (r in p.runs) {
                            var text: String? = r.getText(0) ?: continue
                            for (k in dataRow.keys) {
                                text = text!!.replace(k, dataRow[k]!!)
                            }
                            r.setText(textTransform.apply(text), 0)
                        }
                    }
                }
                tbl.addRow(row)
            }
        }
    }

    @Throws(XmlException::class, IOException::class)
    private fun copyRow(row: XWPFTableRow, tbl: XWPFTable): XWPFTableRow {
        val ctrow = CTRow.Factory.parse(row.ctRow.newInputStream())
        return XWPFTableRow(ctrow, tbl)
    }

    private fun substituteParagraphWords(document: XWPFDocument, replaceWords: Map<String, String?>) {
        val it = document.paragraphsIterator
        while (it.hasNext()) {
            val p = it.next()
            val runs = p.runs
            if (runs != null) {
                for (r in runs) {
                    var text: String? = r.getText(0) ?: continue
                    for (k in replaceWords.keys) {
                        if (!text!!.contains(k)) continue
                        text = text.replace(k, replaceWords[k]!!)
                    }
                    r.setText(text, 0)
                }
            }
        }
    }

    companion object {
        private fun transformBody(documentPart: IBody, transformFunction: Function<String?, String?>) {
            for (p in documentPart.paragraphs) {
                val runs = p.runs
                if (runs != null) {
                    for (r in runs) {
                        val text = r.getText(0)
                        if (text != null) {
                            r.setText(transformFunction.apply(text), 0)
                        }
                        text?.let { r.setText(transformFunction.apply(it), 0) }
                    }
                }
            }
            for (tbl in documentPart.tables) {
                for (row in tbl.rows) {
                    for (cell in row.tableCells) {
                        for (p in cell.paragraphs) {
                            for (r in p.runs) {
                                val text: String? = r.getText(0)
                                text?.let { r.setText(transformFunction.apply(it), 0) }
                            }
                        }
                    }
                }
            }
        }
    }
}