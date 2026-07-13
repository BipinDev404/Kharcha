package com.example

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MarkdownText(text: String, modifier: Modifier = Modifier) {
    val lines = text.split("\n")
    Column(modifier = modifier) {
        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.isEmpty()) {
                Text(text = "", modifier = Modifier.padding(bottom = 8.dp))
                continue
            }
            
            var isHeader3 = false
            var isHeader2 = false
            var isHeader1 = false
            var isBullet = false
            var content = trimmedLine

            if (content.startsWith("### ")) {
                isHeader3 = true
                content = content.substring(4)
            } else if (content.startsWith("## ")) {
                isHeader2 = true
                content = content.substring(3)
            } else if (content.startsWith("# ")) {
                isHeader1 = true
                content = content.substring(2)
            } else if (content.startsWith("- ") || content.startsWith("* ")) {
                isBullet = true
                content = content.substring(2)
            }

            val annotatedString = buildAnnotatedString {
                var currentIndex = 0
                while (currentIndex < content.length) {
                    val boldStart = content.indexOf("**", currentIndex)
                    if (boldStart != -1) {
                        val boldEnd = content.indexOf("**", boldStart + 2)
                        if (boldEnd != -1) {
                            append(content.substring(currentIndex, boldStart))
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(content.substring(boldStart + 2, boldEnd))
                            }
                            currentIndex = boldEnd + 2
                        } else {
                            append(content.substring(currentIndex))
                            break
                        }
                    } else {
                        append(content.substring(currentIndex))
                        break
                    }
                }
            }

            val textStyle = when {
                isHeader1 -> MaterialTheme.typography.headlineLarge
                isHeader2 -> MaterialTheme.typography.headlineMedium
                isHeader3 -> MaterialTheme.typography.titleLarge
                else -> MaterialTheme.typography.bodyLarge
            }

            val finalModifier = if (isBullet) {
                Modifier.padding(start = 16.dp, bottom = 4.dp)
            } else {
                Modifier.padding(bottom = 4.dp)
            }

            val bulletPrefix = if (isBullet) "• " else ""

            Text(
                text = buildAnnotatedString {
                    append(bulletPrefix)
                    append(annotatedString)
                },
                style = textStyle,
                modifier = finalModifier,
                lineHeight = if (isHeader1 || isHeader2 || isHeader3) textStyle.lineHeight else 24.sp
            )
        }
    }
}
