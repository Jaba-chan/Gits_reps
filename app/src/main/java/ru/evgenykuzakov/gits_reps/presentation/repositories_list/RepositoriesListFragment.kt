package ru.evgenykuzakov.gits_reps.presentation.repositories_list

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.evgenykuzakov.gits_reps.R
import ru.evgenykuzakov.gits_reps.common.ErrorsDescriptionConstants
import ru.evgenykuzakov.gits_reps.data.storage.KeyValueStorage
import ru.evgenykuzakov.gits_reps.databinding.RepositoriesListFragmentBinding
import ru.evgenykuzakov.gits_reps.domain.model.Repo
import ru.evgenykuzakov.gits_reps.presentation.repositories_list.ReposRecyclerViewAdapter.OnItemClickListener
import javax.inject.Inject

@AndroidEntryPoint
class RepositoriesListFragment : Fragment(R.layout.repositories_list_fragment) {

    @Inject
    lateinit var adapter: ReposRecyclerViewAdapter
    private val viewModel: RepositoriesListViewModel by viewModels()

    private var _binding: RepositoriesListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = RepositoriesListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        bindToViewModel()
    }

    private fun bindToViewModel(){
        viewModel.isUserAuth.observe(viewLifecycleOwner) { isUserAuth ->
            if (!isUserAuth) {
                navigateToAuth()
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.pb.root.visibility =
                if (state == RepositoriesListViewModel.State.Loading) View.VISIBLE else View.INVISIBLE
            binding.messageGroup.root.visibility =
                if (state == RepositoriesListViewModel.State.Empty || state is RepositoriesListViewModel.State.Error) View.VISIBLE else View.GONE
            binding.rvMain.visibility =
                if (state is RepositoriesListViewModel.State.Loaded) View.VISIBLE else View.INVISIBLE
            binding.messageGroup.bt.root.text =
                if (state == RepositoriesListViewModel.State.Empty) getString(R.string.refresh) else getString(
                    R.string.retry
                )
            binding.messageGroup.tvMessage.setTextColor(
                if (state == RepositoriesListViewModel.State.Empty) getColorTheme(
                    com.google.android.material.R.attr.colorSecondary
                ) else getColor(R.color.errorMessage)
            )

            adapter.setItems(if (state is RepositoriesListViewModel.State.Loaded) state.repos else emptyList())

            if (state == RepositoriesListViewModel.State.Empty) {
                setMessageGroupThenStateIsEmpty()
            } else if (state is RepositoriesListViewModel.State.Error) {
                when (state.error) {
                    ErrorsDescriptionConstants.UNEXPECTED_ERROR -> {
                        setMessageGroupThenUnexpectedError()
                    }
                    ErrorsDescriptionConstants.NO_INTERNET_CONNECTION_ERROR -> {
                        setMessageGroupThenNoInternetConnectionError()
                    }
                    else -> setMessageGroupThenUnexpectedError()
                }
            }
        }
    }

    private fun initViews() {
        binding.rvMain.adapter = adapter

        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onClick(repo: Repo) {
                navigateToDetailInfo(repo.name, repo.owner, repo.defaultBranch)
            }
        })
        binding.messageGroup.bt.root.setOnClickListener {
            if (viewModel.state.value != RepositoriesListViewModel.State.Loading){
                viewModel.onActionButtonPressed()
            }
        }

        binding.rvMain.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )
        binding.toolbar.ibLogOut.setOnClickListener {
            viewModel.onLogOutButtonPressed()
            exitApplication()
        }
    }

    private fun setMessageGroupThenStateIsEmpty() {
        binding.messageGroup.tvMessage.text = getString(R.string.empty)
        binding.messageGroup.tvExtraInfo.text = getString(R.string.no_repositories_at_the_moment)
        binding.messageGroup.ivRequestState.setImageResource(R.drawable.ic_empty)
    }

    private fun setMessageGroupThenUnexpectedError() {
        binding.messageGroup.tvMessage.text = getString(R.string.something_error)
        binding.messageGroup.tvExtraInfo.text = getString(R.string.check_your_something)
        binding.messageGroup.ivRequestState.setImageResource(R.drawable.ic_something_wrong)
    }

    private fun setMessageGroupThenNoInternetConnectionError() {
        binding.messageGroup.tvMessage.text = getString(R.string.connection_error)
        binding.messageGroup.tvExtraInfo.text = getString(R.string.check_your_internet_connection)
        binding.messageGroup.ivRequestState.setImageResource(R.drawable.ic_no_internet)
    }

    private fun navigateToDetailInfo(repoName: String, owner: String, defaultBranch: String) {
        findNavController().navigate(
            RepositoriesListFragmentDirections.actionRepositoriesListFragmentToDetailInfoFragment(
                repoName, owner, defaultBranch
            )
        )
    }

    private fun navigateToAuth() {
        findNavController().navigate(
            RepositoriesListFragmentDirections.actionRepositoriesListFragmentToAuthFragment(),
            NavOptions
                .Builder().setPopUpTo(R.id.repositoriesListFragment, true)
                .build()
        )
    }


    private fun getColorTheme(colorId: Int): Int {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(
            colorId, typedValue, true
        )
        return typedValue.data
    }

    private fun getColor(colorId: Int): Int {
        return ContextCompat.getColor(
            requireContext(), colorId
        )
    }

    private fun exitApplication() {
        requireActivity().finishAffinity()
    }

}