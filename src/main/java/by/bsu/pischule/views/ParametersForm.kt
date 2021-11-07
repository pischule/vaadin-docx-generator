package by.bsu.pischule.views

import by.bsu.pischule.model.Parameters
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.Key
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
import java.util.*

class ParametersForm : FormLayout() {
    private val h3 = H3("Выписка по счету")
    private val binder: Binder<Parameters> = BeanValidationBinder(
        Parameters::class.java
    )
    var parameters = Parameters()

    private val name = TextField("Владелец счета")
    private val account = IntegerField("Номер счета")
    private val dateFrom = DatePicker("Начало интервала")
    private val dateTo = DatePicker("Конец интервала")
    private val allCaps = Checkbox(ALL_CAPS_CHECKBOX_TEXT)
    private val template = MemoryBuffer()
    private val uploadTemplateFile = Upload(template)
    private val rowCount = IntegerField("Количество записей")
    private val generateButton = Button("Сгенерировать данные")
    private val downloadButton = Button("Создать документ")
    private fun configureAllCapsButton() {
        allCaps.addValueChangeListener {
            if (it.value) {
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
        generateButton.addClickListener { validateAndGenerate() }
    }

    private fun configureDatePickers() {
        with(dateFrom) {
            locale = APP_LOCALE
            isAutoOpen = true
            placeholder = DATE_PLACEHOLDER
            isAutoOpen = true
        }

        with(dateTo) {
            locale = APP_LOCALE
            isAutoOpen = true
            placeholder = DATE_PLACEHOLDER
            isAutoOpen = true
        }

        dateFrom.addValueChangeListener { dateTo.min = it.value }
        dateTo.addValueChangeListener { dateFrom.max = it.value }
    }

    private fun configureDownloadButton() {
        downloadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        downloadButton.addClickListener { downloadFile() }
        downloadButton.addClickShortcut(Key.ENTER)
    }

    private fun validateAndGenerate() = try {
        binder.writeBean(parameters)
        fireEvent(GenerateEvent(this, parameters))
    } catch (e: ValidationException) {
        e.printStackTrace()
    }

    private val studentInfo: Div
        get() {
            val div = Div()
            div.add(
                Paragraph("Студент: Пищулёнок Максим Сергеевич"),
                Paragraph("Курс: 4; Группа: 4.")
            )
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
        eventType: Class<T>, listener: ComponentEventListener<T>
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