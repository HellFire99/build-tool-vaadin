package nl.buildtool.views.about

import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.H5
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.lumo.LumoUtility.Gap
import com.vaadin.flow.theme.lumo.LumoUtility.Padding
import nl.buildtool.views.MainLayout
import nl.buildtool.views.components.BadgeWithLink


@PageTitle("About")
@Route(value = "about", layout = MainLayout::class)
class AboutView : Composite<VerticalLayout?>() {
    init {
        val layoutColumn2 = VerticalLayout()

        val image = Image("images/NameLogo_fade.png", "Rob's Build Tool")
        image.width = "350px"

        val h3 = H3()
        val h5 = H5()
        val layoutRow = HorizontalLayout()

        val layoutRow2 = HorizontalLayout()

        content?.width = "100%"
        content?.style?.set("flex-grow", "1")
        layoutColumn2.setWidthFull()
        content?.setFlexGrow(1.0, layoutColumn2)
        layoutColumn2.addClassName(Gap.LARGE)
        layoutColumn2.addClassName(Padding.XSMALL)
        layoutColumn2.width = "100%"
        layoutColumn2.style["flex-grow"] = "1"
        layoutColumn2.justifyContentMode = JustifyContentMode.CENTER
        layoutColumn2.alignItems = Alignment.CENTER

        h3.text = "Rob's Build Tool"
        h3.width = "max-content"
        h5.text = "Created and maintained by me:"
        h5.width = "max-content"
        layoutRow.setWidthFull()
        layoutColumn2.setFlexGrow(1.0, layoutRow)
        layoutRow.addClassName(Gap.MEDIUM)
        layoutRow.addClassName(Padding.XSMALL)
        layoutRow.width = "100%"
        layoutRow.height = "min-content"
        layoutRow.alignItems = Alignment.CENTER
        layoutRow.justifyContentMode = JustifyContentMode.CENTER
        val icon = Icon()
        icon.icon = "vaadin:mailbox"

        val mailAnchor = Anchor("mailto:robheikoop@gmail.com", "Rob Heikoop")

        val myNameH5 = H5(mailAnchor)
        myNameH5.width = "max-content"

        layoutRow2.setWidthFull()
        layoutColumn2.setFlexGrow(1.0, layoutRow2)
        layoutRow2.addClassName(Gap.MEDIUM)
        layoutRow2.addClassName(Padding.XSMALL)
        layoutRow2.width = "100%"
        layoutRow2.height = "min-content"
        layoutRow2.alignItems = Alignment.CENTER
        layoutRow2.justifyContentMode = JustifyContentMode.CENTER

        val badgeGitHub = BadgeWithLink(
            badgeText = "GitHub",
            url = "https://github.com/HellFire99"
        )
        val badgeBuyMeACoffee = BadgeWithLink(
            badgeText = "Buy me a coffee",
            url = "https://buymeacoffee.com/HellFire99",
            minWidthCustom = "130px"
        )
        val badgeLinkedIn = BadgeWithLink(
            badgeText = "LinkedIn",
            url = "https://www.linkedin.com/in/rheikoop"
        )

        content?.add(layoutColumn2)
        layoutColumn2.add(image)
        layoutColumn2.add(h3)
        layoutColumn2.add(h5)
        layoutColumn2.add(layoutRow)
        layoutRow.add(icon)
        layoutRow.add(myNameH5)
        layoutColumn2.add(layoutRow2)
        layoutRow2.add(badgeGitHub)
        layoutRow2.add(badgeBuyMeACoffee)
        layoutRow2.add(badgeLinkedIn)
    }
}