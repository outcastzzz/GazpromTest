package com.example.gazpromtest.presentation.cities.stickyLabel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gazpromtest.presentation.theme.MainBackground

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun <T : StickyListItem> StickyList(
    items: List<T>,
    modifier: Modifier = Modifier,
    gutterWidth: Dp = 80.dp,
    initialTextStyle: TextStyle = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Medium
    ),
    itemFactory: @Composable (LazyItemScope.(T) -> Unit),
) {
    val state: LazyListState = rememberLazyListState()
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val gutterPx = with(density) {
        gutterWidth.toPx()
    }
    var itemHeight by remember { mutableIntStateOf(0) }
    Box(
        modifier = modifier
            .background(MainBackground)
            .drawWithCache {
                onDrawBehind {
                    var initial: Char? = null
                    if (itemHeight == 0) {
                        itemHeight = state.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 0
                    }
                    state.layoutInfo.visibleItemsInfo.forEachIndexed { index, itemInfo ->
                        val itemInitial = items.getOrNull(itemInfo.index)?.initial
                        if (itemInitial != null && itemInitial != initial) {
                            initial = itemInitial
                            val nextInitial = items.getOrNull(itemInfo.index + 1)?.initial
                            val textLayout = textMeasurer.measure(
                                text = AnnotatedString(itemInitial.toString()),
                                style = initialTextStyle,
                            )
                            val horizontalOffset = (gutterPx - textLayout.size.width) / 2
                            val verticalOffset = ((itemHeight - textLayout.size.height) / 2)
                            drawText(
                                textLayoutResult = textLayout,
                                color = Color.Black,
                                topLeft = Offset(
                                    x = horizontalOffset,
                                    y = if (index != 0 || itemInitial != nextInitial) {
                                        itemInfo.offset.toFloat()
                                    } else {
                                        0f
                                    } + verticalOffset,
                                ),
                            )
                        }
                    }
                }
            }
    ) {
        LazyColumn(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = gutterWidth)
        ) {
            items(items) { item ->
                itemFactory(item)
            }
        }
    }
}
