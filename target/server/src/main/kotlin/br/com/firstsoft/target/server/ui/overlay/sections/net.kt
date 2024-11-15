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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.firstsoft.core.common.hwinfo.DlRate
import br.com.firstsoft.core.common.hwinfo.DlRateUnit
import br.com.firstsoft.core.common.hwinfo.HwInfoData
import br.com.firstsoft.core.common.hwinfo.UpRate
import br.com.firstsoft.core.common.hwinfo.UpRateUnit
import br.com.firstsoft.target.server.ui.ColorTokens.Cyan
import br.com.firstsoft.target.server.ui.ColorTokens.OffWhite
import br.com.firstsoft.target.server.ui.ColorTokens.Purple
import br.com.firstsoft.target.server.ui.components.Pill
import br.com.firstsoft.target.server.ui.components.ProgressLabel
import br.com.firstsoft.target.server.ui.components.ProgressUnit
import br.com.firstsoft.target.server.ui.models.OverlaySettings
import br.com.firstsoft.target.server.ui.overlay.conditional
import java.util.*

@Composable
internal fun net(overlaySettings: OverlaySettings, data: HwInfoData) {
    if (overlaySettings.upRate || overlaySettings.downRate) {
        if (overlaySettings.isHorizontal) {
            Pill(
                title = "NET",
                isHorizontal = true,
            ) {
                if (overlaySettings.downRate) {
                    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.widthIn(min = 35.dp)) {
                        Icon(
                            painterResource("icons/arrow_down.svg"),
                            "",
                            tint = Cyan,
                            modifier = Modifier.padding(end = 4.dp, bottom = 3.dp)
                        )
                        ProgressLabel(String.format("%02.2f", data.DlRate, Locale.US))
                        ProgressUnit(data.DlRateUnit)
                    }
                }

                if (overlaySettings.upRate) {
                    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.widthIn(min = 35.dp)) {
                        Icon(
                            painterResource("icons/arrow_down.svg"),
                            "",
                            tint = Purple,
                            modifier = Modifier.padding(end = 4.dp, bottom = 3.dp).rotate(180f)
                        )
                        ProgressLabel(String.format("%02.2f", data.UpRate, Locale.US))
                        ProgressUnit(data.UpRateUnit)
                    }
                }

                if (overlaySettings.netGraph) {
                    NetGraph(data = data, isHorizontal = false, overlaySettings = overlaySettings)
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
                    Text(
                        text = "NET",
                        fontSize = 10.sp,
                        color = OffWhite,
                        lineHeight = 0.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 1.sp
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (overlaySettings.downRate) {
                            Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.widthIn(min = 35.dp)) {
                                Icon(
                                    painterResource("icons/arrow_down.svg"),
                                    "",
                                    tint = Cyan,
                                    modifier = Modifier.padding(end = 4.dp, bottom = 3.dp)
                                )
                                ProgressLabel(String.format("%02.2f", data.DlRate, Locale.US))
                                ProgressUnit(data.DlRateUnit)
                            }
                        }

                        if (overlaySettings.upRate) {
                            Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.widthIn(min = 35.dp)) {
                                Icon(
                                    painterResource("icons/arrow_down.svg"),
                                    "",
                                    tint = Purple,
                                    modifier = Modifier.padding(end = 4.dp, bottom = 3.dp).rotate(180f)
                                )
                                ProgressLabel(String.format("%02.2f", data.UpRate, Locale.US))
                                ProgressUnit(data.UpRateUnit)
                            }
                        }
                    }
                }

                if (overlaySettings.netGraph) {
                    NetGraph(data = data, isHorizontal = false, overlaySettings = overlaySettings)
                }
            }
        }
    }
}

@Composable
internal fun NetGraph(
    data: HwInfoData,
    isHorizontal: Boolean,
    overlaySettings: OverlaySettings
) {
    if (!overlaySettings.upRate && !overlaySettings.downRate) return

    val largestUp = remember { mutableFloatStateOf(0f) }
    val largestDown = remember { mutableFloatStateOf(0f) }
    val listSize = 30
    val upRatePoints = remember { mutableStateListOf<Float>() }
    val downRatePoints = remember { mutableStateListOf<Float>() }

    val upRatePaint = remember {
        Paint().apply {
            isAntiAlias = true
            color = Purple
            strokeWidth = 1f
            blendMode = BlendMode.DstAtop
        }
    }
    val downRatePaint = remember {
        Paint().apply {
            isAntiAlias = true
            color = Cyan
            strokeWidth = 1f
            blendMode = BlendMode.DstAtop
        }
    }

    LaunchedEffect(data) {
        if (data.UpRate > largestUp.floatValue) {
            largestUp.floatValue = data.UpRate
        }
        if (data.DlRate > largestDown.floatValue) {
            largestDown.floatValue = data.DlRate + .2f
        }

        upRatePoints.add((data.UpRate / largestUp.floatValue).coerceIn(0f, 1f))
        downRatePoints.add((data.DlRate / largestDown.floatValue + .2f).coerceIn(0f, 1f))
        if (upRatePoints.size > listSize) upRatePoints.removeFirst()
        if (downRatePoints.size > listSize) downRatePoints.removeFirst()
    }

    Box(
        modifier = Modifier
        .conditional(
            predicate = isHorizontal,
            ifTrue = { width(100.dp) },
            ifFalse = { fillMaxWidth() },
        )
        .height(if (isHorizontal) 45.dp else 30.dp)
        .graphicsLayer { alpha = 0.99f }
        .drawWithContent {
            val colors = listOf(Color.Transparent, Color.Black, Color.Black, Color.Black, Color.Transparent)
            val upRateZip = upRatePoints.zipWithNext()
            val downRateZip = downRatePoints.zipWithNext()

            drawIntoCanvas { canvas ->
                if (overlaySettings.upRate) {
                    drawLine(upRateZip, listSize, canvas, upRatePaint)
                }
                if (overlaySettings.downRate) {
                    drawLine(downRateZip, listSize, canvas, downRatePaint)
                }
            }
            drawRect(brush = Brush.horizontalGradient(colors), blendMode = BlendMode.DstIn)
        })
}