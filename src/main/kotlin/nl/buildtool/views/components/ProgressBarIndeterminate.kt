package nl.buildtool.views.components

import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.progressbar.ProgressBar

class ProgressBarIndeterminate : Div() {
    private val progressBar: ProgressBar

    init {
        this.width = "100%"
        this.height = "42.5px"
        this.style?.set("flex-grow", "1")
        this.style?.set("gap", "0")

        this.progressBar = ProgressBar()
        progressBar.width = "100%"
        progressBar.height = "5px"
        progressBar.isIndeterminate = true
        progressBar.isVisible = false
        add(progressBar)
    }

    override fun setVisible(visible: Boolean) {
        super.setVisible(visible)
        this.progressBar.isVisible = visible
    }
}