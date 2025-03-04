package ru.evgenykuzakov.gits_reps.presentation.repositories_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.evgenykuzakov.gits_reps.common.ErrorsDescriptionConstants
import ru.evgenykuzakov.gits_reps.common.Resource
import ru.evgenykuzakov.gits_reps.data.storage.KeyValueStorage
import ru.evgenykuzakov.gits_reps.domain.model.Repo
import ru.evgenykuzakov.gits_reps.domain.use_case.GetReposUseCase
import javax.inject.Inject

@HiltViewModel
class RepositoriesListViewModel @Inject constructor(
    private val getReposUseCase: GetReposUseCase,
    private val keyValueStorage: KeyValueStorage,
) : ViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> get() = _state

    private val _isUserAuth = MutableLiveData<Boolean>()
    val isUserAuth: LiveData<Boolean> get() = _isUserAuth

    init {
        _isUserAuth.value = keyValueStorage.authToken != null
        if (_isUserAuth.value == true) fetchRepositories()
    }

    fun onLogOutButtonPressed() {
        keyValueStorage.authToken = null
    }

    fun onActionButtonPressed() {
        fetchRepositories()
    }

    private fun fetchRepositories() {
        getReposUseCase().onEach { result ->
            when (result) {
                is Resource.Error ->
                    if (result.message == ErrorsDescriptionConstants.NO_INTERNET_CONNECTION_ERROR)
                        _state.value = State.Error(result.message)
                    else
                        _state.value = State.Error(
                            result.message ?: ErrorsDescriptionConstants.UNEXPECTED_ERROR
                        )

                is Resource.Loading -> _state.value = State.Loading
                is Resource.Success -> _state.value =
                    if (result.data?.isEmpty() == true) State.Empty else result.data?.let {
                        State.Loaded(it)
                    }
            }
        }.launchIn(viewModelScope)
    }

    sealed interface State {
        object Loading : State
        data class Loaded(val repos: List<Repo>) : State
        data class Error(val error: String) : State
        object Empty : State
    }
}