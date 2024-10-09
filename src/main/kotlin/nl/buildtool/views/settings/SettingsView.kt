package nl.buildtool.views.settings

import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.lumo.LumoUtility.Margin
import nl.buildtool.views.MainLayout

@PageTitle("Settings")
@Route(value = "settings", layout = MainLayout::class)
class SettingsView : VerticalLayout() {
    init {
        isSpacing = false

        val img = Image("images/empty-plant.png", "placeholder plant")
        img.width = "200px"
        add(img)

        val header = H2("This place intentionally left empty (prefix input)")
        header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM)
        add(header)
        add(Paragraph("It’s a place where you can grow your own UI 🤗"))

        setSizeFull()
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER
        style["text-align"] = "center"
    }
}
