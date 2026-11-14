/*
 * Adaptado de Android Open Source Project (2023), licencia Apache 2.0.
 * Adaptación académica para Carlos Luis Ortiz García.
 */
package com.example.marsphotos.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.marsphotos.R
import com.example.marsphotos.model.MarsPhoto
import com.example.marsphotos.ui.theme.MarsPhotosTheme

@Composable
fun HomeScreen(
    marsUiState: MarsUiState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onRetry: () -> Unit = {}
) {
    when (marsUiState) {
        MarsUiState.Loading -> LoadingScreen(modifier.fillMaxSize().padding(contentPadding))
        is MarsUiState.Success -> PhotosGridScreen(marsUiState.photos, modifier, contentPadding)
        MarsUiState.Error -> ErrorScreen(modifier.fillMaxSize().padding(contentPadding), onRetry)
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
fun PhotosGridScreen(
    photos: List<MarsPhoto>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding
    ) {
        items(items = photos, key = { it.id }) { photo ->
            MarsPhotoCard(photo, Modifier.padding(4.dp).fillMaxWidth().aspectRatio(1f))
        }
    }
}

@Composable
fun MarsPhotoCard(photo: MarsPhoto, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.imgSrc)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.mars_photo_description, photo.id),
            placeholder = painterResource(R.drawable.loading_img),
            error = painterResource(R.drawable.ic_broken_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PhotosGridScreenPreview() {
    MarsPhotosTheme {
        PhotosGridScreen(List(6) { MarsPhoto(it.toString(), "https://example.com/$it.jpg") })
    }
}
