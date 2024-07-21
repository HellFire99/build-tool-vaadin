package nl.buildtool.views.build

import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteAlias
import com.vaadin.flow.theme.lumo.LumoUtility
import nl.buildtool.views.MainLayout

@PageTitle("Build")
@Route(value = "", layout = MainLayout::class)
@RouteAlias(value = "", layout = MainLayout::class)
class BuildView : Composite<VerticalLayout?>() {
    init {
        val layoutRow2 = HorizontalLayout()
        val layoutRow = HorizontalLayout()
        val layoutColumn2 = VerticalLayout()
        val layoutColumn3 = VerticalLayout()
        val layoutRow3 = HorizontalLayout()
        content!!.width = "100%"
        content!!.style["flex-grow"] = "1"
        layoutRow2.addClassName(LumoUtility.Gap.MEDIUM)
        layoutRow2.width = "100%"
        layoutRow2.height = "min-content"
        layoutRow.addClassName(LumoUtility.Gap.MEDIUM)
        layoutRow.width = "100%"
        layoutRow.style["flex-grow"] = "1"
        layoutColumn2.width = "100%"
        layoutColumn2.style["flex-grow"] = "1"
        layoutColumn3.style["flex-grow"] = "1"
        layoutRow3.addClassName(LumoUtility.Gap.MEDIUM)
        layoutRow3.width = "100%"
        layoutRow3.height = "min-content"
        content!!.add(layoutRow2)
        content!!.add(layoutRow)
        layoutRow.add(layoutColumn2)
        layoutRow.add(layoutColumn3)
        content!!.add(layoutRow3)
    }
}
