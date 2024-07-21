package nl.buildtool.views.about

import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.lumo.LumoUtility.Margin
import nl.buildtool.views.MainLayout

@PageTitle("About")
@Route(value = "about", layout = MainLayout::class)
class AboutView : VerticalLayout() {
    init {
        isSpacing = false

        val img = Image("images/empty-plant.png", "placeholder plant")
        img.width = "200px"
        add(img)

        val header = H2("This place intentionally left empty")
        header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM)
        add(header)
        add(Paragraph("It’s a place where you can grow your own UI 🤗"))

        setSizeFull()
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER
        style["text-align"] = "center"
    }
}
