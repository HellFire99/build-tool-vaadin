package nl.buildtool.views.components

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.radiobutton.RadioGroupVariant
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import nl.buildtool.model.*

class AutoDetectCustomOrResetRadio(
    autoDetectInfoMessage: TextArea,
    customPrefixTextfield: TextField,
    middleColumn: VerticalLayout,
    evaluateExecuteButtonEnabling: () -> Unit
) : RadioButtonGroup<String>() {
    init {
        setId("customOrAutoDetectPrefixRadio")
        label = "Auto-detect or custom prefix"
        width = "min-content"
        setItems(
            RADIO_VALUE_AUTO_DETECT,
            RADIO_VALUE_CUSTOM_PREFIX,
            RADIO_VALUE_RESET_POMS
        )

        addThemeVariants(RadioGroupVariant.LUMO_VERTICAL)
        addValueChangeListener {
            when (it.value) {
                RADIO_VALUE_AUTO_DETECT -> {
                    autoDetectInfoMessage.value = MESSAGE_AUTO_DETECT_INFO
                    autoDetectInfoMessage.label = LABEL_AUTO_DETECT_INFO
                    middleColumn.remove(customPrefixTextfield)
                    middleColumn.add(autoDetectInfoMessage)
                }

                RADIO_VALUE_CUSTOM_PREFIX -> {
                    middleColumn.remove(autoDetectInfoMessage)
                    autoDetectInfoMessage.value = ""
                    autoDetectInfoMessage.label = ""
                    middleColumn.add(customPrefixTextfield)
                }

                RADIO_VALUE_RESET_POMS -> {
                    autoDetectInfoMessage.value = MESSAGE_RESET_POMS
                    autoDetectInfoMessage.label = LABEL_RESET_POMS
                    middleColumn.remove(customPrefixTextfield)
                    middleColumn.add(autoDetectInfoMessage)
                }
            }
            evaluateExecuteButtonEnabling()
        }
    }
}