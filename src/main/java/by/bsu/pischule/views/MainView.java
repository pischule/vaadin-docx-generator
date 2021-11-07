package by.bsu.pischule.views;

import by.bsu.pischule.generator.TransactionGenerator;
import by.bsu.pischule.model.FormData;
import by.bsu.pischule.model.Transaction;
import by.bsu.pischule.service.TemplateService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataCommunicator;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import org.apache.poi.xssf.usermodel.TextAlign;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.vaadin.flow.component.grid.ColumnTextAlign.*;

@PageTitle("Выписка по счету")
@Route(value = "")
public class MainView extends VerticalLayout {

    private final ParametersForm form = new ParametersForm();
    private final Grid<Transaction> grid = new Grid<>(Transaction.class);
    private final List<Transaction> transactionList = new ArrayList<>();
    private final TransactionGenerator generator;
    private final TemplateService templateDocumentService;
    private final Anchor invisibleAnchor = new Anchor();

    public MainView(TransactionGenerator generator, TemplateService templateDocumentService) {
        this.generator = generator;
        this.templateDocumentService = templateDocumentService;

        setSizeFull();
        configureGrid();
        configureForm();
        configureAnchor();

        add(
                getContent(),
                this.invisibleAnchor
        );

        setInitData();
    }

    private void configureAnchor() {
        this.invisibleAnchor.setId("downloadAnchor");
        this.invisibleAnchor.getElement().setAttribute("download", true);
    }

    private void configureForm() {
        form.setWidth("25em");
        form.addListener(ParametersForm.GenerateEvent.class, e -> updateList(e.getFormData()));
        form.addListener(ParametersForm.DownloadEvent.class, this::generateAndDownloadFile);
    }

    private void updateList(FormData data) {
        transactionList.clear();
        transactionList.addAll(generator.generateTransactions(data));
        grid.setItems(transactionList);
    }

    private void setInitData() {
        FormData data = getDefaultForm();
        form.setFormData(data);
        transactionList.clear();
        transactionList.addAll(generator.generateTransactions(data));
        grid.setItems(transactionList);
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setColumns();
        grid.setMultiSort(true);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        grid.addColumn(t -> t.getDate().format(formatter)).setHeader("Дата").setTextAlign(CENTER).setComparator(
                Comparator.comparing(Transaction::getDate));
        grid.addColumn(Transaction::getId).setHeader("Номер транзакции").setTextAlign(END);
        grid.addColumn(Transaction::getDescription).setHeader("Описание").setTextAlign(START);
        grid.addColumn(Transaction::getCurrency).setHeader("Валюта").setTextAlign(START);
        grid.addColumn(Transaction::getAmount).setHeader("Сумма").setTextAlign(END);

        grid.getColumns().forEach(c -> c.setAutoWidth(true).setSortable(true));
    }

    private FormData getDefaultForm() {
        return FormData.builder()
                .account(42)
                .name("Артур Филип Дент")
                .allCaps(false)
                .dateFrom(LocalDate.now().minusDays(10))
                .dateTo(LocalDate.now())
                .rowCount(100)
                .build();
    }

    private List<Transaction> getSortedItems() {
        ListDataProvider<Transaction> dataProvider = (ListDataProvider<Transaction>) grid.getDataProvider();
        int totalSize = dataProvider.getItems().size();
        DataCommunicator<Transaction> dataCommunicator = grid.getDataCommunicator();
        Stream<Transaction> stream = dataProvider.fetch(new Query<>(
                0,
                totalSize,
                dataCommunicator.getBackEndSorting(),
                dataCommunicator.getInMemorySorting(),
                dataProvider.getFilter()));
        return stream.collect(Collectors.toList());
    }

    private void generateAndDownloadFile(ParametersForm.DownloadEvent e) {
        try {
            InputStream document = templateDocumentService.fillDocument(e.getFormData(), getSortedItems());
            StreamResource streamResource = new StreamResource("transactions.docx", () -> document);
            invisibleAnchor.setHref(streamResource);
            UI.getCurrent().getPage().executeJavaScript("$0.click();", this.invisibleAnchor.getElement());

        } catch (Exception exception) {
            Notification.show(exception.getMessage());
        }
    }
}
