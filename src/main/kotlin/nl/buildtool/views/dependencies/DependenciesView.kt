package nl.buildtool.views.dependencies

import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import nl.buildtool.views.MainLayout

@PageTitle("Dependencies")
@Route(value = "dependencies", layout = MainLayout::class)
class DependenciesView : Composite<VerticalLayout?>() {
    init {
        content!!.width = "100%"
        content!!.style["flex-grow"] = "1"
    }
}
