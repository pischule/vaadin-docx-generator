package by.bsu.pischule.views

import by.bsu.pischule.generator.TransactionGenerator
import by.bsu.pischule.model.Parameters
import by.bsu.pischule.model.Transaction
import by.bsu.pischule.service.TemplateService
import by.bsu.pischule.views.ParametersForm.DownloadEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.grid.ColumnTextAlign
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.function.ValueProvider
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.function.Consumer

@PageTitle("Выписка по счету")
@Route(value = "")
class MainView(
    private val generator: TransactionGenerator,
    private val templateDocumentService: TemplateService
) : VerticalLayout() {
    private val form = ParametersForm()
    private val grid = Grid(
        Transaction::class.java
    )
    private val transactionList: MutableList<Transaction> = ArrayList()
    private val invisibleAnchor = Anchor()
    private fun configureAnchor() {
        invisibleAnchor.setId("downloadAnchor")
        invisibleAnchor.element.setAttribute("download", true)
    }

    private fun configureForm() {
        form.width = "25em"
        form.addListener(ParametersForm.GenerateEvent::class.java) { e: ParametersForm.GenerateEvent -> updateList(e.parameters) }
        form.addListener(DownloadEvent::class.java) { e: DownloadEvent -> generateAndDownloadFile(e) }
    }

    private fun updateList(data: Parameters) {
        transactionList.clear()
        transactionList.addAll(generator.generateTransactions(data))
        grid.setItems(transactionList)
    }

    private fun setInitData() {
        val data = defaultForm
        form.setFormData(data)
        transactionList.clear()
        transactionList.addAll(generator.generateTransactions(data))
        grid.setItems(transactionList)
    }

    private val content: Component
        get() {
            val content = HorizontalLayout(grid, form)
            content.setFlexGrow(2.0, grid)
            content.setFlexGrow(1.0, form)
            content.addClassName("content")
            content.setSizeFull()
            return content
        }

    private fun configureGrid() {
        grid.setSizeFull()
        grid.setColumns()
        grid.isMultiSort = true
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        grid.addColumn(ValueProvider<Transaction, Any> { t: Transaction -> t.date.format(formatter) })
            .setHeader("Дата").setTextAlign(ColumnTextAlign.CENTER).setComparator(
                Comparator.comparing(Transaction::date)
            )
        grid.addColumn(Transaction::id).setHeader("Номер транзакции").setTextAlign(ColumnTextAlign.END)
        grid.addColumn(Transaction::description).setHeader("Описание").setTextAlign(ColumnTextAlign.START)
        grid.addColumn(Transaction::currency).setHeader("Валюта").setTextAlign(ColumnTextAlign.START)
        grid.addColumn(Transaction::amount).setHeader("Сумма").setTextAlign(ColumnTextAlign.END)
        grid.columns.forEach(Consumer { c: Grid.Column<Transaction> -> c.setAutoWidth(true).setSortable(true) })
    }

    private val defaultForm: Parameters
        get() = Parameters(
            account = 42,
            name = "Артур Филип Дент",
            allCaps = false,
            dateFrom = LocalDate.now().minusDays(10),
            dateTo = LocalDate.now(),
            rowCount = 100
        )
    private val sortedItems: List<Transaction>
        get() {
            val dataProvider = grid.dataProvider as ListDataProvider<Transaction>
            val totalSize = dataProvider.items.size
            val dataCommunicator = grid.dataCommunicator
            val stream = dataProvider.fetch(
                Query(
                    0,
                    totalSize,
                    dataCommunicator.backEndSorting,
                    dataCommunicator.inMemorySorting,
                    dataProvider.filter
                )
            )
            return stream.toList()
        }

    private fun generateAndDownloadFile(e: DownloadEvent) {
        try {
            val document = templateDocumentService.fillDocument(e.parameters, sortedItems)
            val streamResource = StreamResource("transactions.docx", InputStreamFactory { document })
            invisibleAnchor.setHref(streamResource)
            invisibleAnchor.element.callJsFunction("click")
        } catch (exception: Exception) {
            Notification.show(exception.message)
        }
    }

    init {
        setSizeFull()
        configureGrid()
        configureForm()
        configureAnchor()
        add(
            content,
            invisibleAnchor
        )
        setInitData()
    }
}