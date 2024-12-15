package nl.buildtool.views.components

import com.vaadin.flow.component.checkbox.CheckboxGroup
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant

class CheckboxTargets : CheckboxGroup<String>() {
    companion object {
        enum class targets {
            Clean,
            Install,
            Package,
            Test,
            Verify
        }
    }

    init {
        setId("checkboxGroup")
        label = "Targets"
        width = "min-content"

        setItems(targets.entries.map { it.name })
        addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL)
        value = setOf("Clean", "Install")
    }
}