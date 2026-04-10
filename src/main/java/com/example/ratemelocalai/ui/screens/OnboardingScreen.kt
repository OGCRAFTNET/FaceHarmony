package com.example.ratemelocalai.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ratemelocalai.ui.components.PremiumButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage("Welcome", "Experience the next generation of facial analysis.", Color(0xFF0A84FF)),
        OnboardingPage("Privacy First", "100% On-device processing. No data ever leaves your phone.", Color(0xFF5E5CE6)),
        OnboardingPage("Deep Insights", "Understand your facial harmony and proportions with AI.", Color(0xFF64D2FF))
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Dynamic background blur
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(100.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            pages[pagerState.currentPage].color.copy(alpha = 0.15f),
                            Color.Black
                        )
                    )
                )
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            OnboardingContent(pages[pageIndex])
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page Indicator dots
            Row(
                modifier = Modifier.padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == i) 24.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == i) pages[i].color else Color.White.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            AnimatedContent(targetState = pagerState.currentPage == 2, label = "btn_anim") { isLast ->
                PremiumButton(
                    text = if (isLast) "Get Started" else "Next",
                    onClick = {
                        if (isLast) onFinish()
                        else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth(0.8f)
                )
            }
        }
    }
}

data class OnboardingPage(val title: String, val description: String, val color: Color)

@Composable
fun OnboardingContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(240.dp)
                .blur(40.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(page.color.copy(alpha = 0.3f), Color.Transparent)
                    )
                )
        )
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = page.title,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.description,
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp
        )
    }
}
