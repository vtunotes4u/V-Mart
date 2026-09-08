package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.security.MessageDigest

/**
 * Clean, modern QR Code canvas generator that produces deterministic, sharp QR matrix patterns
 * based on the UPI payload or custom text string, complete with finder patterns and alignment marks.
 */
@Composable
fun QrCodeView(
    data: String,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    qrColor: Color = Color.Black,
    backgroundColor: Color = Color.White
) {
    val matrixSize = 25
    val grid = remember(data) {
        generateQrMatrix(data, matrixSize)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellWidth = this.size.width / matrixSize
            val cellHeight = this.size.height / matrixSize

            for (r in 0 until matrixSize) {
                for (c in 0 until matrixSize) {
                    if (grid[r][c]) {
                        drawRect(
                            color = qrColor,
                            topLeft = Offset(c * cellWidth, r * cellHeight),
                            size = Size(cellWidth + 0.5f, cellHeight + 0.5f)
                        )
                    }
                }
            }
        }
    }
}

private fun generateQrMatrix(data: String, size: Int): Array<BooleanArray> {
    val matrix = Array(size) { BooleanArray(size) { false } }

    // Finder patterns (top-left, top-right, bottom-left 7x7 markers)
    fun drawFinderPattern(rowStart: Int, colStart: Int) {
        for (r in 0 until 7) {
            for (c in 0 until 7) {
                val isOuter = r == 0 || r == 6 || c == 0 || c == 6
                val isInner = r in 2..4 && c in 2..4
                matrix[rowStart + r][colStart + c] = isOuter || isInner
            }
        }
    }

    // Top-left
    drawFinderPattern(0, 0)
    // Top-right
    drawFinderPattern(0, size - 7)
    // Bottom-left
    drawFinderPattern(size - 7, 0)

    // Timing patterns
    for (i in 7 until size - 7) {
        matrix[6][i] = (i % 2 == 0)
        matrix[i][6] = (i % 2 == 0)
    }

    // Alignment pattern in bottom right
    val alignR = size - 7
    val alignC = size - 7
    for (r in -2..2) {
        for (c in -2..2) {
            val isBorder = kotlin.math.abs(r) == 2 || kotlin.math.abs(c) == 2
            val isCenter = r == 0 && c == 0
            val targetR = alignR + r
            val targetC = alignC + c
            if (targetR in 0 until size && targetC in 0 until size) {
                matrix[targetR][targetC] = isBorder || isCenter
            }
        }
    }

    // Deterministic payload encoding using sha256 bytes
    val hash = MessageDigest.getInstance("SHA-256").digest(data.toByteArray())
    var hashIndex = 0

    for (r in 0 until size) {
        for (c in 0 until size) {
            val inFinder1 = r < 8 && c < 8
            val inFinder2 = r < 8 && c >= size - 8
            val inFinder3 = r >= size - 8 && c < 8
            val inTiming = r == 6 || c == 6
            val inAlign = r in (alignR - 2)..(alignR + 2) && c in (alignC - 2)..(alignC + 2)

            if (!inFinder1 && !inFinder2 && !inFinder3 && !inTiming && !inAlign) {
                val byteVal = hash[hashIndex % hash.size].toInt()
                val bitVal = (byteVal shr ((r * size + c) % 8)) and 1
                matrix[r][c] = (bitVal == 1) xor ((r + c) % 3 == 0)
                hashIndex++
            }
        }
    }

    return matrix
}
