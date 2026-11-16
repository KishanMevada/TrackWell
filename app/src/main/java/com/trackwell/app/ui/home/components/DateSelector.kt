package com.trackwell.app.ui.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.trackwell.app.R
import com.trackwell.app.ui.theme.TargetTokens
import com.trackwell.app.ui.theme.TargetTypography
import com.trackwell.app.ui.theme.ds
import com.trackwell.app.ui.theme.dsp

/**
 * Responsive Date selector pill:
 * - Wrap-content width with minimum 155 ds baseline.
 * - [ < ]  |  [ Nov 14, 2026 v ]  |  [ > ]
 * - When a historical date is selected (!isToday), a 1-tap pastel green "Today" chip
 *   fades in smoothly beside the pill to instantly return to current data.
 */
@Composable
fun DateSelector(
    dateLabel: String,
    isToday: Boolean,
    canGoForward: Boolean,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onDatePickerClick: () -> Unit,
    onResetToToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Main Date Pill: wrap-content with minimum 155 ds design width
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 155.ds)
                .height(43.ds)
                .wrapContentWidth()
                .neuRaisedS(cornerRadius = 21.5.ds)
                .clip(RoundedCornerShape(21.5.ds)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(43.ds)
                    .padding(horizontal = 10.ds),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Previous Day Chevron
                Box(
                    modifier = Modifier
                        .size(24.ds)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onPreviousDay
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_left),
                        contentDescription = "Previous Day",
                        tint = TargetTokens.chevronSide,
                        modifier = Modifier.size(width = 8.ds, height = 13.ds)
                    )
                }

                Spacer(modifier = Modifier.width(6.ds))

                // Left Divider: #D9DFEA plus 1dp white line
                Box(
                    modifier = Modifier
                        .width(2.ds)
                        .height(20.ds)
                        .drawBehind {
                            drawLine(
                                color = TargetTokens.divider,
                                start = Offset(0f, 0f),
                                end = Offset(0f, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                            drawLine(
                                color = Color.White,
                                start = Offset(1.dp.toPx(), 0f),
                                end = Offset(1.dp.toPx(), size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                )

                // Date text + down chevron
                Row(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onDatePickerClick
                        )
                        .padding(horizontal = 10.ds),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = dateLabel,
                        style = TargetTypography.datePill.copy(fontSize = 15.dsp),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(6.ds))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_down),
                        contentDescription = "Pick Date",
                        tint = TargetTokens.chevronDown,
                        modifier = Modifier.size(width = 12.ds, height = 7.ds)
                    )
                }

                // Right Divider: #D9DFEA plus 1dp white line
                Box(
                    modifier = Modifier
                        .width(2.ds)
                        .height(20.ds)
                        .drawBehind {
                            drawLine(
                                color = TargetTokens.divider,
                                start = Offset(0f, 0f),
                                end = Offset(0f, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                            drawLine(
                                color = Color.White,
                                start = Offset(1.dp.toPx(), 0f),
                                end = Offset(1.dp.toPx(), size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                )

                Spacer(modifier = Modifier.width(6.ds))

                // Next Day Chevron
                Box(
                    modifier = Modifier
                        .size(24.ds)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = canGoForward,
                            onClick = onNextDay
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_right),
                        contentDescription = "Next Day",
                        tint = if (canGoForward) TargetTokens.chevronSide else TargetTokens.chevronSide.copy(alpha = 0.35f),
                        modifier = Modifier.size(width = 8.ds, height = 13.ds)
                    )
                }
            }
        }

        // 1-Tap "Today" Quick-Jump Pill (Appears smoothly when viewing historical dates)
        AnimatedVisibility(
            visible = !isToday,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(8.ds))
                Box(
                    modifier = Modifier
                        .height(35.ds)
                        .neuChip(chipColor = TargetTokens.chipGreen, cornerRadius = 17.5.ds)
                        .clip(RoundedCornerShape(17.5.ds))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onResetToToday
                        )
                        .padding(horizontal = 12.ds),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Today",
                        style = TargetTypography.chipText.copy(fontSize = 13.dsp),
                        color = Color(0xFF1E4620)
                    )
                }
            }
        }
    }
}
