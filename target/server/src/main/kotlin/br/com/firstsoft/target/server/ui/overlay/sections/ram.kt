package br.com.firstsoft.target.server.ui.overlay.sections

import androidx.compose.runtime.Composable
import br.com.firstsoft.core.common.hwinfo.HwInfoData
import br.com.firstsoft.core.common.hwinfo.RamUsage
import br.com.firstsoft.core.common.hwinfo.RamUsagePercent
import br.com.firstsoft.target.server.ui.components.Pill
import br.com.firstsoft.target.server.ui.components.Progress
import br.com.firstsoft.target.server.ui.models.OverlaySettings
import java.util.*

@Composable
internal fun ram(overlaySettings: OverlaySettings, data: HwInfoData) {
    if (overlaySettings.ramUsage) {
        Pill(
            title = "RAM",
            isHorizontal = overlaySettings.isHorizontal,
        ) {
            Progress(
                value = data.RamUsagePercent / 100f,
                label = String.format("%02.1f", data.RamUsage / 1000, Locale.US),
                unit = "GB",
                progressType = overlaySettings.progressType
            )
        }
    }
}