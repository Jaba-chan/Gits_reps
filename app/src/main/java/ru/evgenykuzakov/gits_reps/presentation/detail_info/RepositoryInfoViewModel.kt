package ru.evgenykuzakov.gits_reps.presentation.detail_info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.evgenykuzakov.gits_reps.common.ErrorsDescriptionConstants
import ru.evgenykuzakov.gits_reps.common.Resource
import ru.evgenykuzakov.gits_reps.data.storage.KeyValueStorage
import ru.evgenykuzakov.gits_reps.domain.model.Repo
import ru.evgenykuzakov.gits_reps.domain.use_case.GetDetailInfoUseCase
import ru.evgenykuzakov.gits_reps.domain.use_case.GetRepoReadmeUseCase
import javax.inject.Inject

private const val REPO_NAME_ARG_KEY = "repoName"
private const val REPO_OWNER_ARG_KEY = "owner"
private const val REPO_BRANCH_NAME_ARG_KEY = "defaultBranch"
private const val NOT_FOUND_ERROR_CODE = 404

@HiltViewModel
class RepositoryInfoViewModel @Inject constructor(
    private val getDetailInfoUseCase: GetDetailInfoUseCase,
    private val getRepoReadmeUseCase: GetRepoReadmeUseCase,
    private val keyValueStorage: KeyValueStorage,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _repoName = MutableLiveData(savedStateHandle.get<String>(REPO_NAME_ARG_KEY) ?: "")
    private val _owner = savedStateHandle.get<String>(REPO_OWNER_ARG_KEY) ?: ""
    private val _branchName = savedStateHandle.get<String>(REPO_BRANCH_NAME_ARG_KEY) ?: ""

    val repoName: LiveData<String> = _repoName

    private val _state = MutableLiveData<State>()
    private val _readmeState = MutableLiveData<ReadmeState>()
    val state: LiveData<State> = _state

    init {
        fetchRepository(_repoName.value ?: "")
    }

    fun onRetryButtonPressed() {
        if (_state.value is State.Error)
            fetchRepository(_repoName.value ?: "")
        if (_readmeState.value is ReadmeState.Error)
            fetchReadmeFile(_repoName.value ?: "", _owner, _branchName)
        if (_state.value is State.Error && _readmeState.value is ReadmeState.Error)
            fetchRepository(_repoName.value ?: "")
    }

    fun onLogOutButtonPressed() {
        keyValueStorage.authToken = null
    }


    sealed interface State {
        object Loading : State
        data class Error(val error: String) : State
        data class Loaded(
            val githubRepo: Repo,
            val readmeState: ReadmeState
        ) : State
    }

    sealed interface ReadmeState {
        object Loading : ReadmeState
        object Empty : ReadmeState
        data class Error(val error: String) : ReadmeState
        data class Loaded(val markdown: String) : ReadmeState
    }

    private fun fetchRepository(repoName: String) {
        getDetailInfoUseCase(repoName).onEach { result ->
            when (result) {
                is Resource.Error -> _state.value = State.Error(
                    result.message ?: ErrorsDescriptionConstants.UNEXPECTED_ERROR
                )

                is Resource.Loading -> _state.value = State.Loading
                is Resource.Success -> {
                    _state.value =
                        result.data?.let {
                            State.Loaded(
                                githubRepo = it,
                                readmeState = ReadmeState.Loading
                            )
                        }
                    fetchReadmeFile(_repoName.value ?: "", _owner, _branchName)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchReadmeFile(repoName: String, ownerName: String, branchName: String) {
        getRepoReadmeUseCase(
            repo = repoName,
            owner = ownerName,
            branchName = branchName
        ).onEach { result ->
            when (result) {
                is Resource.Error ->
                    if (result.code == NOT_FOUND_ERROR_CODE) _readmeState.value =
                        ReadmeState.Empty else
                        _readmeState.value = ReadmeState.Error(
                            result.message
                                ?: ErrorsDescriptionConstants.NO_INTERNET_CONNECTION_ERROR
                        )

                is Resource.Loading -> _readmeState.value = ReadmeState.Loading
                is Resource.Success -> _readmeState.value =
                    if (result.data != null) ReadmeState.Loaded(result.data) else ReadmeState.Empty
            }
            _state.value = (_state.value as? State.Loaded)?.copy(readmeState = _readmeState.value!!)
        }.launchIn(viewModelScope)
    }
}