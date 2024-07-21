package nl.buildtool

import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.dependency.NpmPackage
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
//@Theme(value = "build-tool3")
@NpmPackage(value = "lumo-css-framework", version = "^4.0.10")
@NpmPackage(value = "line-awesome", version = "1.3.0")
@CssImport("./styles/build-tool.css")
class VaadinBuildToolApplication

fun main(args: Array<String>) {
    runApplication<VaadinBuildToolApplication>(*args)
}
