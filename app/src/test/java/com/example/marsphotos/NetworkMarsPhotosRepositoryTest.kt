package com.example.marsphotos

import com.example.marsphotos.data.NetworkMarsPhotosRepository
import com.example.marsphotos.model.MarsPhoto
import com.example.marsphotos.network.MarsApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import java.io.IOException

class NetworkMarsPhotosRepositoryTest {
    @Test fun returnsPhotosFromApi() = runTest {
        val expected = listOf(MarsPhoto("1", "https://example.com/mars.jpg"))
        var calls = 0
        val api = object : MarsApiService {
            override suspend fun getPhotos(): List<MarsPhoto> {
                calls++
                return expected
            }
        }
        assertEquals(expected, NetworkMarsPhotosRepository(api).getMarsPhotos())
        assertEquals(1, calls)
    }

    @Test fun propagatesConnectionFailure() = runTest {
        val expected = IOException("No connection")
        val api = object : MarsApiService {
            override suspend fun getPhotos(): List<MarsPhoto> = throw expected
        }
        try {
            NetworkMarsPhotosRepository(api).getMarsPhotos()
            throw AssertionError("Expected IOException")
        } catch (actual: IOException) {
            assertSame(expected, actual)
        }
    }
}
