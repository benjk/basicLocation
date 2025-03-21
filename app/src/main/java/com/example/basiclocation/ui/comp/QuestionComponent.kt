package com.example.basiclocation.ui.comp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.basiclocation.ui.theme.Typography
import com.example.basiclocation.ui.theme.primaryColor
import com.example.basiclocation.ui.theme.secondaryColor
import com.example.basiclocation.ui.theme.thirdColor
import com.example.basiclocation.viewmodels.QuizQuestion

@Composable
fun QuestionComponent(
    question: QuizQuestion,
    selectedAnswerIndex: Int,
    onAnswerSelected: (Int) -> Unit
) {
    val spacing = 12.dp

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        Spacer(modifier = Modifier.weight(1f)) // Espace avant la question

        Text(
            text = question.question,
            style = Typography.headlineMedium,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f),
                verticalArrangement = Arrangement.spacedBy(spacing)
            ) {
                question.options.chunked(2).forEach { rowOptions ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        rowOptions.forEach { option ->
                            val globalIndex = question.options.indexOf(option)
                            val isSelected = globalIndex == selectedAnswerIndex

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        4.dp,
                                        if (isSelected) secondaryColor else thirdColor,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onAnswerSelected(globalIndex) },
                                elevation = if (isSelected) CardDefaults.cardElevation(4.dp) else CardDefaults.cardElevation(
                                    1.dp
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(thirdColor)
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = option,
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center,
                                        color = primaryColor,
                                        style = Typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}