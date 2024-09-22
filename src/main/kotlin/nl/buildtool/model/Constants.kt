package nl.buildtool.model

import java.util.concurrent.TimeUnit
import javax.swing.JLabel

const val TITLE = "Rob's BuildTool"

// Labels
val LBL_SELECTED = JLabel("Selected")
val LBL_POM_FILES = JLabel("Pom files")
val LBL_MAVEN_PROJECTS = JLabel("Maven projects")
val LBL_POMS = JLabel("Pom's")

// Tooltips
const val TOOLTIP_CLEAN = "Maven Clean"
const val TOOLTIP_INSTALL = "Maven Install"
const val TOOLTIP_COMPILE = "Maven Compile"
const val TOOLTIP_TEST = "Maven TEST"
const val TOOLTIP_GIT_PULL = "Perform a Git Pull on every git directory"
const val TOOLTIP_STOP_ON_ERROR = "Stop building when a build fails"
const val TOOLTIP_ORDERED_BUILD = "Build projects in order or not"
const val TOOLTIP_CLEAR = "Clear the console"

// Texts
const val TXT_CLEAN = "clean"
const val TXT_COMPILE = "compile"
const val TXT_INSTALL = "install"
const val TXT_TEST = "test"
const val TXT_GIT_PULL = "Git Pull"
const val TXT_STOP_ON_ERROR = "Stop on error"
const val TXT_ORDERED_BUILD = "Ordered build"
const val TXT_BUILD = "Build"
const val TXT_CANCEL = "Cancel"
const val TXT_REFRESH = "Refresh"
const val TXT_CLEAR = "Clear"
const val TXT_TOGGLE = "Toggle"
const val TXT_EXPAND = "Expand"
const val TXT_COLLAPSE = "Collapse"
const val TXT_CONSOLE_DEFAULT = "Output window > \n"

// Images
const val ICON_REFRESH = "/images/icon_refresh.png"
const val ICON_QUEUED = "/images/queued.png"
const val ICON_BUILDING = "/images/tools.png"
const val ICON_CHECK = "/images/check.gif"
const val ICON_ERROR = "/images/error.png"
const val ICON_NONE = "/images/none.png"
const val ICON_BURGER = "/images/burger.png"
const val ICON_CLEAR = "/images/broom.png"
const val ICON_EXPAND = "/images/down.png"
const val ICON_COLLAPSE = "/images/up.png"

const val showAllId = "showAllToggle"
const val showSelectedId = "showSelectedToggle"

const val timeoutAmount: Long = 60
val timeoutUnit: TimeUnit = TimeUnit.SECONDS

const val RADIO_VALUE_ALL_IN_WORSPACE = "All in workspace"
const val RADIO_VALUE_SELECTION = "Selection"
const val RADIO_VALUE_AUTO_DETECT = "Auto-detect"
const val RADIO_VALUE_CUSTOM_PREFIX = "Custom prefix"

enum class GitCommand(val command: String) {
    SHOW_CURRENT_BRANCH("git branch --show-current"),
    PULL("git pull")
}