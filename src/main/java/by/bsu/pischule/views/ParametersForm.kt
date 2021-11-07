package by.bsu.pischule.views

import by.bsu.pischule.model.Parameters
import com.vaadin.flow.component.*
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.upload.Upload
import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.binder.ValidationException
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import com.vaadin.flow.shared.Registration
import org.apache.commons.io.IOUtils
import java.io.InputStream
import java.time.LocalDate
import java.util.*

class ParametersForm : FormLayout() {
    var h3 = H3("Выписка по счету")
    var binder: Binder<Parameters> = BeanValidationBinder(
        Parameters::class.java
    )
    var parameters = Parameters()

    val name = TextField("Владелец счета")
    val account = IntegerField("Номер счета")
    val dateFrom = DatePicker("Начало интервала")
    val dateTo = DatePicker("Конец интервала")
    val allCaps = Checkbox(ALL_CAPS_CHECKBOX_TEXT)
    val template = MemoryBuffer()
    val uploadTemplateFile = Upload(template)
    val rowCount = IntegerField("Количество записей")
    val generateButton = Button("Сгенерировать данные")
    val downloadButton = Button("Создать документ")
    private fun configureAllCapsButton() {
        allCaps.addValueChangeListener { e: ComponentValueChangeEvent<Checkbox?, Boolean> ->
            if (e.value) {
                allCaps.label = ALL_CAPS_CHECKBOX_TEXT.uppercase()
            } else {
                allCaps.label = ALL_CAPS_CHECKBOX_TEXT
            }
        }
    }

    private fun configureUpload() {
        uploadTemplateFile.setAcceptedFileTypes(
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        )
    }

    private fun configureRowCount() {
        rowCount.setHasControls(true)
        rowCount.step = 100
    }

    private fun configureGenerateButton() {
        generateButton.addClickListener { e: ClickEvent<Button?>? -> validateAndGenerate() }
    }

    private fun configureDatePickers() {
        dateFrom.locale = APP_LOCALE
        dateFrom.isAutoOpen = true
        dateFrom.placeholder = DATE_PLACEHOLDER
        dateTo.locale = APP_LOCALE
        dateFrom.isAutoOpen = true
        dateFrom.placeholder = DATE_PLACEHOLDER
        dateFrom.addValueChangeListener { e: ComponentValueChangeEvent<DatePicker?, LocalDate?> ->
            dateTo.min = e.value
        }
        dateTo.addValueChangeListener { e: ComponentValueChangeEvent<DatePicker?, LocalDate?> ->
            dateFrom.max = e.value
        }
    }

    private fun configureDownloadButton() {
        downloadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        downloadButton.addClickListener { e: ClickEvent<Button> -> downloadFile() }
        downloadButton.addClickShortcut(Key.ENTER)
    }

    private fun validateAndGenerate() {
        try {
            binder.writeBean(parameters)
            fireEvent(GenerateEvent(this, parameters))
        } catch (e: ValidationException) {
            e.printStackTrace()
        }
    }

    private val studentInfo: Div
        get() {
            val div = Div()
            div.add(Paragraph("Студент: Пищулёнок Максим Сергеевич"))
            div.add(Paragraph("Курс: 4; Группа: 4."))
            return div
        }
    private val templateLink: Component
        get() {
            val downloadLink = Anchor(
                StreamResource("template.docx", InputStreamFactory { templateFileInputStream }),
                "Пример шаблона"
            )
            downloadLink.element.setAttribute("download", true)
            return downloadLink
        }

    public override fun <T : ComponentEvent<*>?> addListener(
        eventType: Class<T>,
        listener: ComponentEventListener<T>
    ): Registration {
        return eventBus.addListener(eventType, listener)
    }

    fun setFormData(data: Parameters) {
        binder.readBean(data)
        parameters = data
    }

    private fun downloadFile() {
        if (template.fileData != null) {
            val `is` = template.inputStream
            try {
                parameters.template = IOUtils.toByteArray(`is`)
            } catch (ignored: Exception) {
            }
        }
        parameters.allCaps = allCaps.value
        fireEvent(DownloadEvent(this, parameters))
    }

    private val templateFileInputStream: InputStream
        get() {
            val classLoader = javaClass.classLoader
            return classLoader.getResourceAsStream("template.docx")
        }

    abstract class FormEvent(source: ParametersForm?, val parameters: Parameters) :
        ComponentEvent<ParametersForm?>(source, false)

    class GenerateEvent(source: ParametersForm?, parameters: Parameters) : FormEvent(source, parameters)
    class DownloadEvent(source: ParametersForm?, parameters: Parameters) : FormEvent(source, parameters)
    companion object {
        private const val DATE_PLACEHOLDER = "ДД.ММ.ГГГГ"
        private val APP_LOCALE = Locale("ru", "RU")
        private const val ALL_CAPS_CHECKBOX_TEXT = "Заменить строчные заглавными"
    }

    init {
        setSizeFull()
        binder.bindInstanceFields(this)
        configureDatePickers()
        configureRowCount()
        configureGenerateButton()
        configureDownloadButton()
        configureUpload()
        configureAllCapsButton()
        add(
            h3,
            name,
            account,
            dateFrom,
            dateTo,
            allCaps,
            uploadTemplateFile,
            templateLink,
            rowCount,
            generateButton,
            downloadButton,
            studentInfo
        )
    }
}