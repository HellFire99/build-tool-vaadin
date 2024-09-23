package nl.buildtool.views

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.html.*
import com.vaadin.flow.component.orderedlayout.Scroller
import com.vaadin.flow.component.sidenav.SideNav
import com.vaadin.flow.component.sidenav.SideNavItem
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.theme.lumo.LumoUtility
import nl.buildtool.views.about.AboutView
import nl.buildtool.views.build.BuildView
import nl.buildtool.views.utils.UtilsView
import org.vaadin.lineawesome.LineAwesomeIcon

/**
 * The main view is a top-level placeholder for other views.
 */
class MainLayout : AppLayout() {
    private var viewTitle: H1? = null

    init {
        primarySection = Section.DRAWER
        addDrawerContent()
        addHeaderContent()
    }

    private fun addHeaderContent() {
        val toggle = DrawerToggle()
        toggle.setAriaLabel("Menu toggle")

        viewTitle = H1()
        viewTitle!!.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.NONE)

        addToNavbar(true, toggle, viewTitle)
    }

    private fun addDrawerContent() {
        val appName = Span("Rob's build-tool")
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE)

        val headerDiv = Div()
        headerDiv.add(appName)
        headerDiv.addClassNames("padding-10")
        // Test

        val header = Header(headerDiv)

        val scroller = Scroller(createNavigation())

        addToDrawer(header, scroller, createFooter())
    }

    private fun createNavigation(): SideNav {
        val nav = SideNav()

        nav.addItem(SideNavItem("Build", BuildView::class.java, LineAwesomeIcon.BUILDING.create()))
        nav.addItem(SideNavItem("Utils", UtilsView::class.java, LineAwesomeIcon.TOOLS_SOLID.create()))
        nav.addItem(SideNavItem("About", AboutView::class.java, LineAwesomeIcon.FILE.create()))

        return nav
    }

    private fun createFooter(): Footer {
        val layout = Footer()

        return layout
    }

    override fun afterNavigation() {
        super.afterNavigation()
        viewTitle!!.text = currentPageTitle
    }

    private val currentPageTitle: String
        get() {
            val title = content.javaClass.getAnnotation(
                PageTitle::class.java
            )
            return title?.value ?: ""
        }
}
