package com.trackwell.app.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.R
import com.trackwell.app.ui.theme.TargetTokens
import com.trackwell.app.ui.theme.TargetTypography
import com.trackwell.app.ui.theme.ds
import com.trackwell.app.ui.theme.dsp

/**
 * Stat Card (minimum 168 ds height), Raised-L, radius 24 ds.
 * In a Row with equal weights and 17 ds gap.
 */
@Composable
fun StatCard(
    labelLine1: String,
    labelLine2: String?,
    valueText: String,
    unitText: String,
    chipText: String,
    chipColor: Color,
    showDownArrow: Boolean = false,
    showUpArrow: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 168.ds)
            .height(168.ds)
            .neuRaisedL(cornerRadius = 24.ds)
            .clip(RoundedCornerShape(24.ds))
            .then(clickModifier),
        contentAlignment = Alignment.TopCenter
    ) {
        // 1. Label at +22.5 ds (or +25.5 ds if single line)
        val labelTop = if (labelLine2.isNullOrBlank()) 25.5.ds else 22.5.ds
        Column(
            modifier = Modifier
                .offset(y = labelTop),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = labelLine1,
                style = TargetTypography.statCardLabel.copy(fontSize = 17.dsp),
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )
            if (!labelLine2.isNullOrBlank()) {
                Text(
                    text = labelLine2,
                    style = TargetTypography.statCardLabel.copy(fontSize = 17.dsp),
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // 2. Value and unit at digit top +73 ds, baseline +102 ds
        Row(
            modifier = Modifier
                .offset(y = 73.ds),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = valueText,
                style = TargetTypography.statCardValue.copy(fontSize = 32.dsp)
            )
            Spacer(modifier = Modifier.width(5.ds))
            Text(
                text = unitText,
                style = TargetTypography.statCardUnit.copy(fontSize = 17.dsp),
                modifier = Modifier.padding(bottom = 3.ds)
            )
        }

        // 3. Chip at top +124 ds, 94 x 30 ds
        Box(
            modifier = Modifier
                .offset(y = 124.ds)
                .size(width = 94.ds, height = 30.ds)
                .neuChip(chipColor = chipColor, cornerRadius = 15.ds)
                .clip(RoundedCornerShape(15.ds)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (showDownArrow) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_down),
                        contentDescription = "Drop",
                        tint = TargetTokens.textPrimary,
                        modifier = Modifier
                            .size(width = 10.ds, height = 12.ds)
                            .padding(end = 3.ds)
                    )
                }
                Text(
                    text = chipText,
                    style = TargetTypography.chipText.copy(fontSize = 17.dsp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
