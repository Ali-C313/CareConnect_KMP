package com.example.careconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.careconnect.i18n.tr
import com.example.careconnect.ui.theme.CareConnectTheme
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch

/**
 * Profilbild auswählen (Galerie auf Android und iOS über FileKit).
 * @param imageBytes neu gewähltes Bild (noch nicht hochgeladen)
 * @param imageUrl bereits gespeichertes Bild
 */
@Composable
fun ProfileImagePicker(
    imageBytes: ByteArray?,
    imageUrl: String,
    onImagePicked: (ByteArray) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val launcher = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        if (file != null) {
            scope.launch {
                onImagePicked(file.readBytes())
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { launcher.launch() },
            contentAlignment = Alignment.Center
        ) {
            when {
                imageBytes != null -> AsyncImage(
                    model = imageBytes,
                    contentDescription = tr("Profilbild"),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                imageUrl.isNotEmpty() -> AsyncImage(
                    model = imageUrl,
                    contentDescription = tr("Profilbild"),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                else -> Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        TextButton(onClick = { launcher.launch() }) {
            Icon(Icons.Default.Photo, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text(tr("Bild auswählen"))
        }
    }
}

@Preview
@Composable
fun ProfileImagePickerPreview() {
    CareConnectTheme(darkTheme = false) {
        ProfileImagePicker(imageBytes = null, imageUrl = "", onImagePicked = {})
    }
}
