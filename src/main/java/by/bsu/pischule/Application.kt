package by.bsu.pischule

import com.vaadin.flow.component.dependency.NpmPackage
import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.server.PWA
import com.vaadin.flow.theme.Theme
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.vaadin.artur.helpers.LaunchUtil

@SpringBootApplication
@Theme(value = "documentgenerator")
@PWA(name = "Document Generator", shortName = "Document Generator", offlineResources = ["images/logo.png"])
@NpmPackage(value = "line-awesome", version = "1.3.0")
open class Application : AppShellConfigurator

fun main(args: Array<String>) {
    LaunchUtil.launchBrowserInDevelopmentMode(SpringApplication.run(Application::class.java, *args))
}
