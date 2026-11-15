package co.edu.uniquindio.servify

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class StringsParityTest {

    @Test
    fun testStringsParityBetweenDefaultAndEs() {
        val defaultFile = File("src/main/res/values/strings.xml")
        val esFile = File("src/main/res/values-es/strings.xml")

        assertTrue("Default strings.xml must exist", defaultFile.exists())
        assertTrue("Spanish strings-es.xml must exist", esFile.exists())

        val defaultKeys = parseStringKeysAndValues(defaultFile)
        val esKeys = parseStringKeysAndValues(esFile)

        assertEquals("Keys must match between values/strings.xml and values-es/strings.xml", defaultKeys.keys, esKeys.keys)

        for ((key, defaultVal) in defaultKeys) {
            val esVal = esKeys[key] ?: ""
            assertTrue("Value for key '$key' in default must not be empty", defaultVal.isNotBlank())
            assertTrue("Value for key '$key' in es must not be empty", esVal.isNotBlank())

            val defaultPlaceholders = countPlaceholders(defaultVal)
            val esPlaceholders = countPlaceholders(esVal)
            assertEquals("Placeholders must match for key '$key'", defaultPlaceholders, esPlaceholders)
        }
    }

    private fun parseStringKeysAndValues(file: File): Map<String, String> {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(file)
        doc.documentElement.normalize()

        val map = mutableMapOf<String, String>()
        val nodeList = doc.getElementsByTagName("string")
        for (i in 0 until nodeList.length) {
            val node = nodeList.item(i)
            val name = node.attributes.getNamedItem("name")?.nodeValue
            val text = node.textContent
            if (name != null) {
                map[name] = text
            }
        }
        return map
    }

    private fun countPlaceholders(text: String): List<String> {
        val regex = "%[0-9]+\\$?[sd]".toRegex()
        return regex.findAll(text).map { it.value }.toList()
    }
}
