package com.myomer.myomer.core.uI

import androidx.lifecycle.ViewModel
import com.myomer.myomer.data.remote.model.ErrorModel
import com.myomer.myomer.util.bindingAdapter.mutableLiveData

/**
 * Created by JeeteshSurana.
 */

open class BaseViewModel : ViewModel() {
    var mError = mutableLiveData(ErrorModel())
}