package nl.buildtool.views

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.html.*
import com.vaadin.flow.component.orderedlayout.Scroller
import com.vaadin.flow.component.sidenav.SideNav
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.theme.lumo.LumoUtility
import nl.buildtool.model.Globals
import nl.buildtool.model.STYLE_COLOR_BACKGROUND
import nl.buildtool.views.about.AboutView
import nl.buildtool.views.build.BuildView
import nl.buildtool.views.components.BuildToolHeader
import nl.buildtool.views.settings.SettingsView
import nl.buildtool.views.utils.UtilsView
import org.vaadin.lineawesome.LineAwesomeIcon

/**
 * The main view is a top-level placeholder for other views.
 */
class MainLayout : AppLayout() {
    private var viewTitle: H1? = null

    init {
        Globals.pomFileList = emptyList()
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
        val appName = H3("Rob's build-tool")
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE)

        val image = Image("images/NameLogo.png", "Rob's Build Tool")
        image.width = "245px"

        val headerDiv = Div()
        headerDiv.add(image)
        headerDiv.addClassNames("padding-10")
        headerDiv.style.set("background-color", STYLE_COLOR_BACKGROUND)
        // Test

        val header = Header(headerDiv)

        val scroller = Scroller(createNavigation())
        scroller.style.set("background-color", STYLE_COLOR_BACKGROUND)
        addToDrawer(header, scroller, createFooter())
    }

    private fun createNavigation(): SideNav {
        val nav = SideNav()

        nav.addItem(BuildToolHeader("Build", BuildView::class.java, LineAwesomeIcon.BUILDING.create()))
        nav.addItem(BuildToolHeader("Utils", UtilsView::class.java, LineAwesomeIcon.TOOLS_SOLID.create()))
        nav.addItem(BuildToolHeader("About", AboutView::class.java, LineAwesomeIcon.FILE.create()))
        nav.addItem(BuildToolHeader("Settings", SettingsView::class.java, LineAwesomeIcon.COG_SOLID.create()))

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
