package br.com.firstsoft.target.server.ui.overlay.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.firstsoft.core.common.hwinfo.Frametime
import br.com.firstsoft.core.common.hwinfo.HwInfoData
import br.com.firstsoft.core.common.hwinfo.getReading
import br.com.firstsoft.target.server.ui.ColorTokens.OffWhite
import br.com.firstsoft.target.server.ui.components.Pill
import br.com.firstsoft.target.server.ui.models.OverlaySettings
import br.com.firstsoft.target.server.ui.overlay.conditional
import java.util.*

@Composable
internal fun fps(overlaySettings: OverlaySettings, data: HwInfoData) {
    if (overlaySettings.fps || overlaySettings.frametime) {
        if (overlaySettings.isHorizontal) {
            Pill(
                title = "FPS",
                isHorizontal = true,
            ) {
                if (overlaySettings.fps) {
                    val reading = data.getReading(overlaySettings.framerateReadingId.readingId, overlaySettings.framerateReadingId.sensorIndex)
                    val readingValue = (reading?.value ?: 1f).coerceAtLeast(1f).toInt()

                    Text(
                        text = "$readingValue",
                        color = Color.White,
                        fontSize = 16.sp,
                        lineHeight = 0.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }

                if (overlaySettings.frametime) {
                    FrametimeGraph(data, true)
                    Text(
                        text = "${String.format("%02.01f", data.Frametime, Locale.US)} ms",
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 0.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.3f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val reading = data.getReading(overlaySettings.framerateReadingId.readingId, overlaySettings.framerateReadingId.sensorIndex)
                    val readingValue = (reading?.value ?: 1f).coerceAtLeast(1f).toInt()

                    Text(
                        text = "FPS",
                        fontSize = 10.sp,
                        color = OffWhite,
                        lineHeight = 0.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 1.sp
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        if (overlaySettings.fps) {
                            Text(
                                text = "$readingValue",
                                color = Color.White,
                                fontSize = 16.sp,
                                lineHeight = 0.sp,
                                fontWeight = FontWeight.Normal,
                            )
                        }

                        if (overlaySettings.frametime) {
                            Text(
                                text = "${String.format("%02.01f", data.Frametime, Locale.US)} ms",
                                color = Color.White,
                                fontSize = 12.sp,
                                lineHeight = 0.sp,
                                fontWeight = FontWeight.Normal,
                            )
                        }
                    }
                }

                if (overlaySettings.frametime) {
                    FrametimeGraph(data = data, isHorizontal = false)
                }
            }
        }
    }
}

@Composable
internal fun FrametimeGraph(data: HwInfoData, isHorizontal: Boolean) {
    val largestFrametime = remember { mutableFloatStateOf(0f) }
    val listSize = 30
    val frametimePoints = remember { mutableStateListOf<Float>() }

    val frametimePaint = remember {
        Paint().apply {
            isAntiAlias = true
            color = Color.White
            strokeWidth = 1f
            blendMode = BlendMode.DstAtop
        }
    }

    LaunchedEffect(data) {
        if (data.Frametime > largestFrametime.floatValue) {
            largestFrametime.floatValue = data.Frametime
        }
        frametimePoints.add(data.Frametime / largestFrametime.floatValue)
        if (frametimePoints.size > listSize) frametimePoints.removeFirst()
    }

    Box(modifier = Modifier
        .conditional(
            predicate = isHorizontal,
            ifTrue = { width(100.dp).height(45.dp) },
            ifFalse = { fillMaxWidth().height(30.dp) },
        )
        .graphicsLayer { alpha = 0.99f }
        .drawWithContent {
            val colors = listOf(Color.Transparent, Color.Black, Color.Black, Color.Black, Color.Transparent)
            val frametimeZip = frametimePoints.zipWithNext()

            drawIntoCanvas { canvas ->
                drawLine(frametimeZip, listSize, canvas, frametimePaint)
            }

            drawRect(brush = Brush.horizontalGradient(colors), blendMode = BlendMode.DstIn)
        })
}
