package ru.evgenykuzakov.gits_reps.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.evgenykuzakov.gits_reps.common.ErrorsDescriptionConstants
import ru.evgenykuzakov.gits_reps.common.Resource
import ru.evgenykuzakov.gits_reps.data.storage.KeyValueStorage
import ru.evgenykuzakov.gits_reps.domain.use_case.AuthUseCase
import javax.inject.Inject

private const val INVALID_TOKEN_ERROR_CODE = 401

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val keyValueStorage: KeyValueStorage
) : ViewModel() {

    private val _token = MutableLiveData<String>()
    val token: MutableLiveData<String> = _token

    private val _state = MutableLiveData<State>(State.Idle)
    val state: LiveData<State> = _state

    private val _action = MutableSharedFlow<Action>()
    val actions: Flow<Action> = _action

    fun onTokenTextChanged(text: String) {
        _token.value = text
    }

    fun onDialogPositiveButtonPressed() {
        resetStateToIdle()
    }

    fun onTokenTextEdited() {
        resetStateToIdle()
    }

    fun resetStateToIdle() {
        _state.value = State.Idle
    }

    fun onSignButtonPressed() {
        authUseCase(token.value ?: "").onEach { result ->
            when (result) {
                is Resource.Error ->
                    if (result.message == ErrorsDescriptionConstants.INVALID_TOKEN_ERROR)
                        _state.value = State.InvalidInput(result.message)
                    else _action.emit(Action.ShowError(ErrorsDescriptionConstants.UNEXPECTED_ERROR))
                is Resource.Loading -> _state.value = State.Loading
                is Resource.Success -> {
                    _action.emit(Action.RouteToMain)
                    keyValueStorage.authToken = token.value
                }
            }
        }.launchIn(viewModelScope)
    }

    sealed interface State {
        object Idle : State
        object Loading : State
        data class InvalidInput(val reason: String) : State
    }

    sealed interface Action {
        data class ShowError(val message: String) : Action
        object RouteToMain : Action
    }

}