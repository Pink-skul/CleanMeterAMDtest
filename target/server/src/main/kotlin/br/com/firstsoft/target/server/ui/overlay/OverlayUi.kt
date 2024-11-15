package br.com.firstsoft.target.server.ui.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import br.com.firstsoft.core.common.hwinfo.HwInfoData
import br.com.firstsoft.core.os.hwinfo.HwInfoReader
import br.com.firstsoft.target.server.ui.models.OverlaySettings
import br.com.firstsoft.target.server.ui.overlay.sections.cpu
import br.com.firstsoft.target.server.ui.overlay.sections.fps
import br.com.firstsoft.target.server.ui.overlay.sections.gpu
import br.com.firstsoft.target.server.ui.overlay.sections.net
import br.com.firstsoft.target.server.ui.overlay.sections.ram


inline fun Modifier.conditional(
    predicate: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: Modifier.() -> Modifier = { this },
): Modifier = if (predicate) ifTrue(this) else ifFalse(this)

@Composable
fun OverlayUi(
    reader: HwInfoReader,
    overlaySettings: OverlaySettings,
) {
    val data by reader.currentData.collectAsState(null)

    if (data == null) {
        return
    }

    if (overlaySettings.isHorizontal) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()
                .background(Color.Black.copy(alpha = 0.36f), CircleShape)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Content(
                data!!,
                overlaySettings = overlaySettings,
            )
        }
    } else {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.36f), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Content(
                data!!,
                overlaySettings = overlaySettings,
            )
        }
    }
}

@Composable
fun Content(data: HwInfoData, overlaySettings: OverlaySettings) {
    if (overlaySettings.isHorizontal) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            fps(overlaySettings, data)
            gpu(overlaySettings, data)
            cpu(overlaySettings, data)
            ram(overlaySettings, data)
            net(overlaySettings, data)
        }
    } else {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Layout(
                content = {
                    fps(overlaySettings, data)
                    gpu(overlaySettings, data)
                    cpu(overlaySettings, data)
                    ram(overlaySettings, data)
                    net(overlaySettings, data)
                },
                measurePolicy = object : MeasurePolicy {
                    override fun MeasureScope.measure(
                        measurables: List<Measurable>,
                        constraints: Constraints
                    ): MeasureResult {
                        val maxWidth = maxIntrinsicWidth(measurables, constraints.maxHeight)
                        val placeables = measurables.map { it.measure(Constraints.fixedWidth(maxWidth)) }
                        val height = placeables.sumOf { it.height } + 4.dp.roundToPx() * (placeables.size - 1)

                        return layout(maxWidth, height) {
                            var yPosition = 0
                            placeables.forEach { placeable ->
                                placeable.placeRelative(x = 0, y = yPosition)
                                yPosition += placeable.height + 4.dp.roundToPx()
                            }
                        }
                    }

                    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
                        measurables: List<IntrinsicMeasurable>,
                        height: Int
                    ): Int {
                        return measurables.map { it.maxIntrinsicWidth(height) }.maxOf { it }
                    }
                })
        }
    }
}
