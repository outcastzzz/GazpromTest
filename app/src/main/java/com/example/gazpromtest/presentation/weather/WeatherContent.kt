package com.example.gazpromtest.presentation.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gazpromtest.R
import com.example.gazpromtest.domain.entity.City
import com.example.gazpromtest.domain.entity.Weather
import com.example.gazpromtest.presentation.cities.CitiesComponent
import com.example.gazpromtest.presentation.theme.ButtonColor
import com.example.gazpromtest.presentation.theme.MainBackground
import kotlin.math.truncate

@Composable
fun WeatherContent(component: WeatherComponent) {

    val state by component.model.collectAsState()

    Scaffold (
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MainBackground
    ) { padding ->
        Box (
            modifier = Modifier
                .padding(padding)
        ) {
            when(val weatherState = state.weatherState) {
                WeatherStore.State.WeatherState.Error -> Error(component)
                WeatherStore.State.WeatherState.Initial -> {  }
                is WeatherStore.State.WeatherState.Loaded -> {
                    SuccessLoaded(component, weatherState.weather)
                }
                WeatherStore.State.WeatherState.Loading -> Loading()
            }
        }
    }

}

@Composable
private fun SuccessLoaded(component: WeatherComponent, weather: Weather) {

    val tempInC = (weather.main.temp - 272.15).toInt()
    val tempStr = String.format("%s°C", tempInC)

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "go back",
                modifier = Modifier
                    .padding(
                        start = 20.dp
                    )
                    .size(32.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable {
                        component.onClickBack()
                    }
            )
        }
        Text(
            modifier = Modifier
                .padding(top = 40.dp),
            text = tempStr,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = component.model.value.city.city ?: stringResource(R.string.no_city_name_string),
            fontSize = 32.sp,
            lineHeight = 40.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Normal
        )
        Spacer(Modifier.weight(1f))
        Button(
            modifier = Modifier
                .padding(bottom = 36.dp),
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

@Composable
private fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.Blue,
            modifier = Modifier
                .size(48.dp)
        )
    }
}


@Composable
private fun Error(component: WeatherComponent) {
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