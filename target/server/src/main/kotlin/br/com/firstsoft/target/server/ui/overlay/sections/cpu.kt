package br.com.firstsoft.target.server.ui.overlay.sections

import androidx.compose.runtime.Composable
import br.com.firstsoft.core.common.hwinfo.HwInfoData
import br.com.firstsoft.core.common.hwinfo.getReading
import br.com.firstsoft.target.server.ui.components.Pill
import br.com.firstsoft.target.server.ui.components.Progress
import br.com.firstsoft.target.server.ui.models.OverlaySettings
import java.util.*

@Composable
internal fun cpu(overlaySettings: OverlaySettings, data: HwInfoData) {
    if (overlaySettings.cpuTemp || overlaySettings.cpuUsage) {
        Pill(
            title = "CPU",
            isHorizontal = overlaySettings.isHorizontal,
        ) {
            if (overlaySettings.cpuTemp) {
                val cpuTemp = data.getReading(overlaySettings.cpuTempReadingId.readingId, overlaySettings.cpuTempReadingId.sensorIndex)
                val cpuTempValue = (cpuTemp?.value ?: 1f).coerceAtLeast(1f).toInt()

                Progress(
                    value = cpuTempValue / 100f,
                    label = "$cpuTempValue",
                    unit = cpuTemp?.szUnit.orEmpty(),
                    progressType = overlaySettings.progressType
                )
            }

            if (overlaySettings.cpuUsage) {
                val cpuUsage = data.getReading(overlaySettings.cpuUsageReadingId.readingId, overlaySettings.cpuUsageReadingId.sensorIndex)
                val cpuUsageValue = (cpuUsage?.value ?: 1f).coerceAtLeast(1f)

                Progress(
                    value = cpuUsageValue / 100f,
                    label = String.format("%02d", cpuUsageValue.toInt(), Locale.US),
                    unit = cpuUsage?.szUnit.orEmpty(),
                    progressType = overlaySettings.progressType
                )
            }
        }
    }
}