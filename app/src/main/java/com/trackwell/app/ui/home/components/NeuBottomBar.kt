package com.trackwell.app.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.trackwell.app.R
import com.trackwell.app.ui.theme.TargetTokens
import com.trackwell.app.ui.theme.ds

/**
 * 4-slot NeuBottomBar (372 x 64 design dp), Raised-L, fully rounded (radius 32 ds).
 * Active nav pill: 76 x 53 ds, pressed style, radius 26 ds.
 * 4 slots: Home, List, Bell, Settings.
 */
@Composable
fun NeuBottomBar(
    selectedIndex: Int = 0,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val icons = listOf(
        R.drawable.ic_nav_home to "Today",
        R.drawable.ic_nav_list to "History",
        R.drawable.ic_nav_bell to "Medicines",
        R.drawable.ic_nav_gear to "Settings"
    )

    Box(
        modifier = modifier
            .size(width = 372.ds, height = 64.ds)
            .neuRaisedL(cornerRadius = 32.ds)
            .clip(RoundedCornerShape(32.ds)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.ds),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            icons.forEachIndexed { index, (drawableId, label) ->
                val isSelected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .size(width = 76.ds, height = 53.ds)
                        .clip(RoundedCornerShape(26.ds))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTabSelected(index) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        // Active pressed pill (76 x 53 ds) centered on slot
                        Box(
                            modifier = Modifier
                                .size(width = 76.ds, height = 53.ds)
                                .neuPressed(cornerRadius = 26.ds)
                                .clip(RoundedCornerShape(26.ds)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = drawableId),
                                contentDescription = label,
                                tint = TargetTokens.iconActive,
                                modifier = Modifier.size(24.ds)
                            )
                        }
                    } else {
                        Icon(
                            painter = painterResource(id = drawableId),
                            contentDescription = label,
                            tint = TargetTokens.iconInactive,
                            modifier = Modifier.size(24.ds)
                        )
                    }
                }
            }
        }
    }
}
