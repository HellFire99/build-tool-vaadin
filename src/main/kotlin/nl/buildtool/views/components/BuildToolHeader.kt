package nl.buildtool.views.components

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.sidenav.SideNavItem

class BuildToolHeader(
    header: String,
    view: Class<out Component>,
    prefixComponent: Component
) : SideNavItem(header, view, prefixComponent) {
    init {

        style.setColor("#2a72e1")
    }
}