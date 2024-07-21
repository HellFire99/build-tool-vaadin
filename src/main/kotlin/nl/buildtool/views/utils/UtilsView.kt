package nl.buildtool.views.utils

import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.buildtool.views.MainLayout

@PageTitle("Utils")
@Route(value = "utils", layout = MainLayout::class)
class UtilsView : Composite<VerticalLayout?>() {
    init {
        content!!.width = "100%"
        content!!.style["flex-grow"] = "1"
    }
}
