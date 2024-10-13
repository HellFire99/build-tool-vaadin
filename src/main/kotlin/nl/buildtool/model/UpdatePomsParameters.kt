package nl.buildtool.model

data class UpdatePomsParameters(
    val customOrAutoDetectPrefixRadio: String,
    val pomFileSelectRadioValue: String,
    val customPrefixTextfield: String? = null,
    val selectedPomFiles: MutableSet<PomFile>? = mutableSetOf()
)