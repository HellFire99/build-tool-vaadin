package nl.buildtool.model

data class UpdatePomsParameters(
    val autoDetectCustomOrReset: String,
    val pomFileSelectRadioValue: String,
    val customPrefixTextfield: String? = null,
    val selectedPomFiles: MutableSet<PomFile>? = mutableSetOf()
)