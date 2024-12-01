package nl.buildtool.views.settings

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.buildtool.views.MainLayout

@PageTitle("Settings")
@Route(value = "settings", layout = MainLayout::class)
class SettingsView : VerticalLayout() {
    init {
        val layoutColumn2 = VerticalLayout()
        val textField = TextField()
        val textField2 = TextField()
        val textField3 = TextField()
        width = "100%"
        style.set("flex-grow", "1")
        layoutColumn2.setWidthFull()
        setFlexGrow(1.0, layoutColumn2)
        layoutColumn2.width = "100%"
        layoutColumn2.style["flex-grow"] = "1"
        textField.label = "GIT Branch name regexp"
        textField.width = "192px"
        textField2.label = "Root folder"
        textField2.width = "min-content"
        textField3.label = "MAVEN Home"
        textField3.width = "min-content"
        add(layoutColumn2)
        layoutColumn2.add(textField)
        layoutColumn2.add(textField2)
        layoutColumn2.add(textField3)
    }
}
