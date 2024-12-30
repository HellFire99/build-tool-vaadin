package nl.buildtool.views.components

import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.Span

class BadgeWithLink(badgeText: String, url: String, minWidthCustom: String? = "") : Span() {
    init {
        val anchor = Anchor(url, badgeText)
        anchor.element.setAttribute("target", "_blank")
        addComponentAtIndex(0, anchor)

        width = "min-content"
        minWidth = minWidthCustom ?: ""
        element.themeList.add("badge pill")
    }
}