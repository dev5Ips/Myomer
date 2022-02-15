package com.myomer.myomer.repository

import android.content.Context
import com.myomer.myomer.data.remote.model.response.PostResponseItem
import com.myomer.myomer.network.ApiRestService
import com.myomer.myomer.network.SafeApiRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class DemoRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val api: ApiRestService
) : SafeApiRequest(context) {

    suspend fun getWeather(): List<PostResponseItem> {
        return apiRequest { api.getWeather() }
    }
}