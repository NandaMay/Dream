package com.example.ui.screens.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.WarmGold

/**
 * Subtle Myanmar-inspired ornamental divider with lotus petal & delicate kanote motif.
 */
@Composable
fun MyanmarOrnamentalDivider(
  modifier: Modifier = Modifier,
  color: Color = WarmGold.copy(alpha = 0.6f)
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(18.dp),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxWidth().height(18.dp)) {
      val w = size.width
      val h = size.height
      val midY = h / 2f
      val midX = w / 2f

      // Left delicate line
      drawLine(
        color = color.copy(alpha = 0.2f),
        start = Offset(24f, midY),
        end = Offset(midX - 36f, midY),
        strokeWidth = 1.5f
      )

      // Right delicate line
      drawLine(
        color = color.copy(alpha = 0.2f),
        start = Offset(midX + 36f, midY),
        end = Offset(w - 24f, midY),
        strokeWidth = 1.5f
      )

      // Center decorative lotus/heart petal motif
      val path = Path().apply {
        moveTo(midX, midY - 6f)
        cubicTo(midX + 8f, midY - 8f, midX + 14f, midY, midX, midY + 6f)
        cubicTo(midX - 14f, midY, midX - 8f, midY - 8f, midX, midY - 6f)
      }
      drawPath(path, color = color, style = Stroke(width = 1.8f))

      // Small flanking accent dots
      drawCircle(color = color, radius = 2f, center = Offset(midX - 18f, midY))
      drawCircle(color = color, radius = 2f, center = Offset(midX + 18f, midY))
    }
  }
}
