package com.example.gazpromtest.presentation.cities

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.util.copy
import com.example.gazpromtest.R
import com.example.gazpromtest.domain.entity.City
import com.example.gazpromtest.domain.entity.Main
import com.example.gazpromtest.presentation.cities.stickyLabel.ListItem
import com.example.gazpromtest.presentation.cities.stickyLabel.StickyList
import com.example.gazpromtest.presentation.theme.ButtonColor
import com.example.gazpromtest.presentation.theme.MainBackground

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CitiesContent(component: CitiesComponent) {

    val state by component.model.collectAsState()

    when(val citiesState = state.citiesState) {
        CitiesStore.State.CitiesState.Error -> Error(component)
        is CitiesStore.State.CitiesState.Loaded -> {
            val list = citiesState.cities
            SuccessLoaded(list, component)
        }
        CitiesStore.State.CitiesState.Loading -> Loading()
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun SuccessLoaded(listOfCities: List<City>, component: CitiesComponent) {

    val fontFamilies = FontFamily(
        Font(R.font.roboto_medium, FontWeight.Medium),
        Font(R.font.roboto_regular, FontWeight.Normal)
    )

    val gutterWidth = 48.dp

    fun processCities(listOfCities: List<City>): List<ListItem> {
        return listOfCities
            .filter { it.city!!.isNotEmpty() }
            .map { ListItem(city = it) }
    }

    val finalList = processCities(listOfCities)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
    ) {
        StickyList(
            items = finalList.sortedBy {
                it.city.city?.uppercase()
            },
            modifier = Modifier
                .padding(top = 25.dp),
            gutterWidth = gutterWidth,
        ) { item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(gutterWidth)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(
                            topStart = 8.dp,
                            bottomStart = 8.dp
                        ))
                        .clickable {
                            component.onClickCity(item.city)
                        },
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = item.city.city ?: stringResource(R.string.no_city_name_string),
                        modifier = Modifier
                            .padding(
                                start = 16.dp,
                                top = 8.dp,
                                bottom = 8.dp,
                                end = 16.dp
                            )
                            .fillMaxWidth(),
                        fontSize = 16.sp,
                        fontFamily = fontFamilies,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = Color.Blue,
            modifier = Modifier
                .size(48.dp)
        )
    }
}

@Composable
private fun Error(component: CitiesComponent) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.error_text),
            fontSize = 14.sp,
            lineHeight = 20.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
        )
        Spacer(Modifier.height(42.dp))
        Button(
            shape = MaterialTheme.shapes.extraLarge,
            onClick = { component.onUpdateRequest() },
            colors = ButtonDefaults.buttonColors(
                containerColor = ButtonColor
            )
        ) {
            Text(
                text = stringResource(R.string.update_text),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = MaterialTheme.colorScheme.background
            )
        }
    }
}