package com.example.booky.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.booky.data.pref.AppSettings

@Composable
fun SettingsScreen(viewModel: BookViewModel) {
    val settings by viewModel.settings.collectAsState()

    SettingsContent(
        settings = settings,
        onFontSizeChange = { viewModel.updateFontSize(it) },
        onFontFamilyChange = { viewModel.updateFontFamily(it) },
        onThemeColorChange = { viewModel.updateThemeColor(it) }
    )
}

@Composable
fun SettingsContent(
    settings: AppSettings,
    onFontSizeChange: (Float) -> Unit,
    onFontFamilyChange: (String) -> Unit,
    onThemeColorChange: (Int) -> Unit
) {
    val colorOptions = listOf(
        0xFF2E7D32 to "أخضر",
        0xFF1976D2 to "أزرق",
        0xFF8E24AA to "بنفسجي",
        0xFFD32F2F to "أحمر",
        0xFFFF6F00 to "برتقالي",
        0xFF00796B to "تركواز"
    )
    val fontFamilies = listOf("Default", "Serif", "Monospace")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "الإعدادات",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Font Size Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "حجم الخط",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "${settings.fontSize.toInt()} sp",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = settings.fontSize,
                    onValueChange = onFontSizeChange,
                    valueRange = 12f..32f,
                    steps = 10,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }


        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "نوع الخط",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(fontFamilies) { family ->
                        FilterChip(
                            selected = settings.fontFamily == family,
                            onClick = { onFontFamilyChange(family) },
                            label = { Text(family) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        }

        // Theme Color Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "لون التطبيق",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(colorOptions) { colorPair ->
                        val (colorHex, colorName) = colorPair
                        val color = Color(colorHex)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .clickable { onThemeColorChange(color.toArgb()) },
                                color = color,
                                border = if (settings.themeColor == color.toArgb()) {
                                    BorderStroke(
                                        3.dp,
                                        MaterialTheme.colorScheme.primary
                                    )
                                } else null,
                                shadowElevation = if (settings.themeColor == color.toArgb()) 8.dp else 2.dp
                            ) {}
                            if (settings.themeColor == color.toArgb()) {
                                Text(
                                    colorName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SettingsPreview() {
    SettingsContent(
        settings = AppSettings(),
        onFontSizeChange = {},
        onFontFamilyChange = {},
        onThemeColorChange = {}
    )
}
