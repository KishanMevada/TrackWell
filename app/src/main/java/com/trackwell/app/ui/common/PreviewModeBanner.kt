package com.trackwell.app.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.ui.home.components.neuRaisedL
import com.trackwell.app.ui.theme.TargetTokens
import com.trackwell.app.ui.theme.TargetTypography

@Composable
fun PreviewModeBanner(
    onExitPreviewMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .neuRaisedL(cornerRadius = 18.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(TargetTokens.surface)
            .border(1.dp, TargetTokens.chipPeachColors[0], RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Preview mode — sample information only",
                    style = TargetTypography.statCardLabel.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                    color = Color(0xFF92400E)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                onClick = onExitPreviewMode,
                shape = RoundedCornerShape(10.dp),
                color = TargetTokens.chipBlue,
                border = BorderStroke(1.dp, Color(0xFFBAE6FD))
            ) {
                Text(
                    text = "Exit",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0284C7),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}
