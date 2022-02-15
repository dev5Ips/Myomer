package com.myomer.myomer.viewmodel

import com.myomer.myomer.core.uI.BaseViewModel
import com.myomer.myomer.network.ApiException
import com.myomer.myomer.repository.DemoRepository
import com.myomer.myomer.util.extensionFunction.convertIntoErrorObjet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by JeeteshSurana.
 */

@HiltViewModel
class DemoViewModel @Inject constructor(
    private val repository: DemoRepository
) : BaseViewModel() {

    suspend fun getData() = withContext(Dispatchers.Main) {
        try {
            repository.getWeather()
        } catch (e: ApiException) {
            mError.postValue(convertIntoErrorObjet(e))
        }
    }
}