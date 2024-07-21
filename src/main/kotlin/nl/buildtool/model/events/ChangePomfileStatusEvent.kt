package nl.buildtool.model.events

import nl.buildtool.model.BuildStatus

data class ChangePomfileStatusEvent(val pomfileName: String, val pomfileVersion: String, val status: BuildStatus)