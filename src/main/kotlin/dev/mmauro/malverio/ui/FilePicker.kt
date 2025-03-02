package dev.mmauro.malverio.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import java.io.File

@Composable
fun <T : Any> FilePicker(name: String, transform: (File) -> T): T? {
    var pickedFile by remember { mutableStateOf<File?>(null) }
    var pickedValue by remember { mutableStateOf<T?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberFilePickerLauncher(
        type = PickerType.File(extensions = listOf("json")),
        mode = PickerMode.Single,
        title = "Pick the $name",
    ) { files ->
        val file = files?.file
        pickedFile = file
        if (file != null) {
            try {
                pickedValue = transform(file)
                error = null
            } catch (e: Throwable) {
                pickedValue = null
                error = "Couldn't load ${file.name}: ${e.message}"
            }
        } else {
            pickedValue = null
            error = null
        }
    }
    BaseFilePicker(name, pickedFile, error = error) {
        filePickerLauncher.launch()
    }
    return pickedValue
}

@Composable
fun OutputFilePicker(name: String): State<File?> {
    val pickedFile = remember { mutableStateOf<File?>(null) }
    val filePickerLauncher = rememberFileSaverLauncher { file ->
        pickedFile.value = file?.file
    }
    BaseFilePicker(name, pickedFile.value) {
        filePickerLauncher.launch(
            baseName = "pandemic-save-file",
            extension = "json"
        )
    }
    return pickedFile
}

@Composable
private fun BaseFilePicker(
    name: String,
    pickedFile: File?,
    error: String? = null,
    launchFilePicker: () -> Unit,
) {
    RowWithButton(
        buttonLabel = "Browse",
        onClick = launchFilePicker,
    ) {
        Column {
            Text(pickedFile?.toString() ?: "No $name selected")
            if (error != null) {
                Text(error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}