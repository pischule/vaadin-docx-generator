package by.bsu.pischule.views;

import by.bsu.pischule.model.FormData;
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
import org.apache.pdfbox.util.filetypedetector.FileType;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Locale;

public class ParametersForm extends FormLayout {

    private static final Locale APP_LOCALE = new Locale("ru", "RU");

    H3 h3 = new H3("Выписка по счету");

    Binder<FormData> binder = new BeanValidationBinder<>(FormData.class);
    FormData formData = new FormData();

    TextField name = new TextField("Владелец счета");
    IntegerField account = new IntegerField("Номер счета");
    DatePicker dateFrom = new DatePicker("Начало интервала");
    DatePicker dateTo = new DatePicker("Конец интервала");
    Checkbox allCaps = new Checkbox("Заменить строчные заглавными");
    MemoryBuffer template = new MemoryBuffer();
    Upload uploadTemplateFile = new Upload(template);
    IntegerField rowCount = new IntegerField("Количество записей");

    Button generateButton = new Button("Сгенерировать данные");
    Button downloadButton = new Button("Создать документ");


    public ParametersForm() {
        binder.bindInstanceFields(this);
        configure();

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

    private void configure() {
        setSizeFull();

        dateFrom.setLocale(APP_LOCALE);
        dateFrom.setHelperText("Формат: ДД.ММ.ГГГГ");
        dateFrom.setRequired(true);
        dateFrom.setMax(LocalDate.now());
        dateFrom.addValueChangeListener(e -> dateTo.setMin(e.getValue()));

        dateTo.setLocale(APP_LOCALE);
        dateTo.setMax(LocalDate.now());
        dateTo.setRequired(true);
        dateTo.setHelperText("Формат: ДД.ММ.ГГГГ");
        dateTo.addValueChangeListener(e -> dateFrom.setMax(e.getValue()));

        rowCount.setHasControls(true);

        uploadTemplateFile.setAcceptedFileTypes(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        generateButton.addClickListener(e -> validateAndGenerate());

        downloadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        downloadButton.addClickListener(this::downloadFile);
        downloadButton.addClickShortcut(Key.ENTER);
    }

    private void validateAndGenerate() {
        try {
            binder.writeBean(formData);
            fireEvent(new GenerateEvent(this, formData));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    private Div getStudentInfo() {
        Div list = new Div();

        list.add(new Paragraph("Студент: Пищулёнок Максим Сергеевич"));
        list.add(new Paragraph("Курс: 4; Группа: 4"));

        return list;
    }

    private Component getTemplateLink(){
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

    public void setFormData(FormData data) {
        binder.readBean(data);
        this.formData = data;
    }

    private void downloadFile(ClickEvent<Button> e) {
        if (template.getFileData() != null) {
            InputStream is = template.getInputStream();
            try {
                formData.setTemplate(IOUtils.toByteArray(is));
            } catch (Exception ignored) {}
        }
        formData.setAllCaps(allCaps.getValue());
        fireEvent(new DownloadEvent(this, formData));
    }

    @Getter
    public static abstract class FormEvent extends ComponentEvent<ParametersForm> {
        private final FormData formData;

        public FormEvent(ParametersForm source, FormData formData) {
            super(source, false);
            this.formData = formData;
        }
    }

    private InputStream getTemplateFileInputStream() {
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream("template.docx");
    }

    public static class GenerateEvent extends FormEvent {
        public GenerateEvent(ParametersForm source, FormData formData) {
            super(source, formData);
        }
    }

    public class DownloadEvent extends FormEvent {
        public DownloadEvent(ParametersForm source, FormData formData) {
            super(source, formData);
        }
    }
}
