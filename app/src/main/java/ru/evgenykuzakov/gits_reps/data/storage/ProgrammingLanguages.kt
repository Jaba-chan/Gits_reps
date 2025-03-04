package ru.evgenykuzakov.gits_reps.data.storage

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import org.yaml.snakeyaml.Yaml
import ru.evgenykuzakov.gits_reps.R
import java.io.InputStream
import javax.inject.Inject

private const val LANGUAGE_COLORS_YML_FILE_NAME = "languages.yml"

class ProgrammingLanguages @Inject constructor(
    private val yaml: Yaml,
    private val context: Context,
) {
    private var languageColors: Map<String, String>

    init {
        languageColors = loadLanguageColors()
    }

    private fun loadLanguageColors(): Map<String, String> {
        val inputStream: InputStream = context.assets.open(LANGUAGE_COLORS_YML_FILE_NAME)
        val languages: Map<String, Map<String, Any>> = yaml.load(inputStream)
        return languages.mapValues {
            val color = it.value["color"] as? String ?: "#000000"
            color
        }
    }

    fun getLanguageColor(lang: String): Int {
        val color =
            languageColors[lang] ?: return ContextCompat.getColor(context, R.color.transparent)
        return Color.parseColor(color)
    }
}


