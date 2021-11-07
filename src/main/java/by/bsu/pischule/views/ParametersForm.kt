package by.bsu.pischule.views;

import by.bsu.pischule.model.Parameters;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.Locale;

public class ParametersForm extends FormLayout {

    private static final String DATE_PLACEHOLDER = "ДД.ММ.ГГГГ";
    private static final Locale APP_LOCALE = new Locale("ru", "RU");
    private static final String ALL_CAPS_CHECKBOX_TEXT = "Заменить строчные заглавными";

    H3 h3 = new H3("Выписка по счету");

    Binder<Parameters> binder = new BeanValidationBinder<>(Parameters.class);
    Parameters parameters = new Parameters();

    TextField name = new TextField("Владелец счета");
    IntegerField account = new IntegerField("Номер счета");
    DatePicker dateFrom = new DatePicker("Начало интервала");
    DatePicker dateTo = new DatePicker("Конец интервала");
    Checkbox allCaps = new Checkbox(ALL_CAPS_CHECKBOX_TEXT);
    MemoryBuffer template = new MemoryBuffer();
    Upload uploadTemplateFile = new Upload(template);
    IntegerField rowCount = new IntegerField("Количество записей");

    Button generateButton = new Button("Сгенерировать данные");
    Button downloadButton = new Button("Создать документ");


    public ParametersForm() {
        setSizeFull();
        binder.bindInstanceFields(this);
        configureDatePickers();
        configureRowCount();
        configureGenerateButton();
        configureDownloadButton();
        configureUpload();
        configureAllCapsButton();

        add(
                h3,
                name,
                account,
                dateFrom,
                dateTo,
                allCaps,
                uploadTemplateFile,
                getTemplateLink(),
                rowCount,
                generateButton,
                downloadButton,
                getStudentInfo()
        );
    }

    private void configureAllCapsButton() {
        allCaps.addValueChangeListener(e -> {
            if (e.getValue()) {
                allCaps.setLabel(ALL_CAPS_CHECKBOX_TEXT.toUpperCase());
            } else {
                allCaps.setLabel(ALL_CAPS_CHECKBOX_TEXT);
            }
        });
    }

    private void configureUpload() {
        uploadTemplateFile.setAcceptedFileTypes(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

    private void configureRowCount() {
        rowCount.setHasControls(true);
        rowCount.setStep(100);
    }

    private void configureGenerateButton() {
        generateButton.addClickListener(e -> validateAndGenerate());
    }

    private void configureDatePickers() {
        dateFrom.setLocale(APP_LOCALE);
        dateFrom.setAutoOpen(true);
        dateFrom.setPlaceholder(DATE_PLACEHOLDER);

        dateTo.setLocale(APP_LOCALE);
        dateFrom.setAutoOpen(true);
        dateFrom.setPlaceholder(DATE_PLACEHOLDER);

        dateFrom.addValueChangeListener(e -> dateTo.setMin(e.getValue()));
        dateTo.addValueChangeListener(e -> dateFrom.setMax(e.getValue()));
    }

    private void configureDownloadButton() {
        downloadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        downloadButton.addClickListener(this::downloadFile);
        downloadButton.addClickShortcut(Key.ENTER);
    }

    private void validateAndGenerate() {
        try {
            binder.writeBean(parameters);
            fireEvent(new GenerateEvent(this, parameters));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    private Div getStudentInfo() {
        Div div = new Div();
        div.add(new Paragraph("Студент: Пищулёнок Максим Сергеевич"));
        div.add(new Paragraph("Курс: 4; Группа: 4."));
        return div;
    }

    private Component getTemplateLink() {
        Anchor downloadLink = new Anchor(
                new StreamResource("template.docx",
                        this::getTemplateFileInputStream),
                "Пример шаблона");
        downloadLink.getElement().setAttribute("download", true);
        return downloadLink;
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    public void setFormData(Parameters data) {
        binder.readBean(data);
        this.parameters = data;
    }

    private void downloadFile(ClickEvent<Button> e) {
        if (template.getFileData() != null) {
            InputStream is = template.getInputStream();
            try {
                parameters.setTemplate(IOUtils.toByteArray(is));
            } catch (Exception ignored) {
            }
        }
        parameters.setAllCaps(allCaps.getValue());
        fireEvent(new DownloadEvent(this, parameters));
    }


    private InputStream getTemplateFileInputStream() {
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream("template.docx");
    }

    public static abstract class FormEvent extends ComponentEvent<ParametersForm> {
        @Getter
        private final Parameters parameters;

        public FormEvent(ParametersForm source, Parameters parameters) {
            super(source, false);
            this.parameters = parameters;
        }
    }

    public static class GenerateEvent extends FormEvent {
        public GenerateEvent(ParametersForm source, Parameters parameters) {
            super(source, parameters);
        }
    }

    public static class DownloadEvent extends FormEvent {
        public DownloadEvent(ParametersForm source, Parameters parameters) {
            super(source, parameters);
        }
    }
}
