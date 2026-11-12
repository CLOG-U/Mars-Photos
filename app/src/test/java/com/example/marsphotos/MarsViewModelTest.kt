package com.example.marsphotos

import com.example.marsphotos.model.MarsPhoto
import com.example.marsphotos.network.MarsApiService
import com.example.marsphotos.ui.screens.MarsUiState
import com.example.marsphotos.ui.screens.MarsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class MarsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun prepare() = Dispatchers.setMain(dispatcher)
    @After fun finish() = Dispatchers.resetMain()
    private fun service(block: suspend () -> List<MarsPhoto>) = object : MarsApiService {
        override suspend fun getPhotos() = block()
    }
    @Test fun parsesImageSource() {
        val photo = Json.decodeFromString<MarsPhoto>(
            """{"id":"1","img_src":"https://example.com/mars.jpg"}"""
        )
        assertEquals("https://example.com/mars.jpg", photo.imgSrc)
    }
    @Test fun retrievesCountFromService() = runTest(dispatcher) {
        val vm = MarsViewModel(service { listOf(MarsPhoto("1", "https://example.com/1"), MarsPhoto("2", "https://example.com/2")) })
        assertEquals(MarsUiState.Loading, vm.marsUiState)
        advanceUntilIdle()
        assertEquals(MarsUiState.Success("Success: 2 Mars photos retrieved"), vm.marsUiState)
    }
    @Test fun showsErrorWithoutNetworkThenRetries() = runTest(dispatcher) {
        var offline = true
        val vm = MarsViewModel(service {
            if (offline) throw IOException("Offline")
            listOf(MarsPhoto("1", "https://example.com/1"))
        })
        advanceUntilIdle()
        assertEquals(MarsUiState.Error, vm.marsUiState)
        offline = false
        vm.getMarsPhotos()
        advanceUntilIdle()
        assertEquals(MarsUiState.Success("Success: 1 Mars photos retrieved"), vm.marsUiState)
    }
    @Test fun showsErrorForHttpFailure() = runTest(dispatcher) {
        val vm = MarsViewModel(service { throw HttpException(Response.error<Any>(503, "Unavailable".toResponseBody())) })
        advanceUntilIdle()
        assertEquals(MarsUiState.Error, vm.marsUiState)
    }
    @Test fun showsErrorForInvalidJson() = runTest(dispatcher) {
        val vm = MarsViewModel(service { throw SerializationException("Invalid JSON") })
        advanceUntilIdle()
        assertEquals(MarsUiState.Error, vm.marsUiState)
    }
}
