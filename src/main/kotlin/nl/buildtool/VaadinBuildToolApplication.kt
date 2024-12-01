package nl.buildtool

import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.dependency.NpmPackage
import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.component.page.Push
import com.vaadin.flow.server.AppShellSettings
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@NpmPackage(value = "lumo-css-framework", version = "^4.0.10")
@NpmPackage(value = "line-awesome", version = "1.3.0")
@CssImport("./styles/build-tool.css")
@Push
class VaadinBuildToolApplication : AppShellConfigurator {
    @Override
    override fun configurePage(settings: AppShellSettings) {
        settings.addFavIcon("icon", "favicon.ico", "32x32")
        settings.addLink("shortcut icon", "favicon.ico")
    }
}

fun main(args: Array<String>) {
    runApplication<VaadinBuildToolApplication>(*args)
}
