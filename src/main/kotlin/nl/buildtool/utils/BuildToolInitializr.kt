package nl.buildtool.utils

import nl.buildtool.model.Globals
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component

@Component
class BuildToolInitializr(private val directoryCrawler: DirectoryCrawler) : InitializingBean {
    override fun afterPropertiesSet() {
        Globals.pomFileList = directoryCrawler.getPomFileList()
    }
}