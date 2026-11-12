/*
 * Adaptado de Android Open Source Project (2023), licencia Apache 2.0.
 * Adaptación académica para Carlos Luis Ortiz García.
 */
package com.example.marsphotos.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.marsphotos.R
import com.example.marsphotos.ui.theme.MarsPhotosTheme

@Composable
fun HomeScreen(
    marsUiState: MarsUiState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onRetry: () -> Unit = {}
) {
    val screenModifier = modifier.fillMaxSize().padding(contentPadding)
    when (marsUiState) {
        MarsUiState.Loading -> LoadingScreen(screenModifier)
        is MarsUiState.Success -> ResultScreen(marsUiState.photos, screenModifier)
        MarsUiState.Error -> ErrorScreen(screenModifier, onRetry)
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Column(modifier, Arrangement.Center, Alignment.CenterHorizontally) {
        CircularProgressIndicator()
        Text(stringResource(R.string.loading), Modifier.padding(16.dp))
    }
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier, onRetry: () -> Unit = {}) {
    Column(modifier, Arrangement.Center, Alignment.CenterHorizontally) {
        Image(painterResource(R.drawable.ic_connection_error), contentDescription = null,
            modifier = Modifier.size(100.dp))
        Text(stringResource(R.string.loading_failed), Modifier.padding(16.dp))
        Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
    }
}

@Composable
fun ResultScreen(photos: String, modifier: Modifier = Modifier) {
    Box(modifier.padding(24.dp), contentAlignment = Alignment.Center) {
        Text(photos, textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    MarsPhotosTheme { ResultScreen("Success: 838 Mars photos retrieved", Modifier.fillMaxSize()) }
}
