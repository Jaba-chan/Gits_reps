package ru.evgenykuzakov.gits_reps.presentation.detail_info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import ru.evgenykuzakov.gits_reps.R
import ru.evgenykuzakov.gits_reps.databinding.DetailInfoFragmentBinding
import ru.evgenykuzakov.gits_reps.domain.model.Repo

@AndroidEntryPoint
class DetailInfoFragment : Fragment(R.layout.detail_info_fragment) {

    private var _binding: DetailInfoFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RepositoryInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailInfoFragmentBinding.inflate(inflater, container, false)
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

    private fun bindToViewModel() {
        viewModel.repoName.observe(viewLifecycleOwner) { name ->
            binding.toolbar.tvTitle.text = name
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.pbDetailInfo.root.visibility =
                if (state == RepositoryInfoViewModel.State.Loading) View.VISIBLE else View.INVISIBLE
            binding.messageGroup.root.visibility =
                if (state is RepositoryInfoViewModel.State.Error) View.VISIBLE else View.GONE
            binding.clDetailInfo.visibility =
                if (state is RepositoryInfoViewModel.State.Loaded) View.VISIBLE else View.GONE
            if (state is RepositoryInfoViewModel.State.Loaded) {
                doThenRepositoryInfoStateIsLoaded(state.githubRepo)
                binding.pbReadme.root.visibility =
                    if (state.readmeState == RepositoryInfoViewModel.ReadmeState.Loading) View.VISIBLE else View.INVISIBLE
                binding.messageGroup.root.visibility =
                    if (state.readmeState is RepositoryInfoViewModel.ReadmeState.Error) View.VISIBLE else View.GONE
                binding.tvReadme.visibility =
                    if (state.readmeState is RepositoryInfoViewModel.ReadmeState.Loaded || state.readmeState == RepositoryInfoViewModel.ReadmeState.Empty) View.VISIBLE else View.INVISIBLE
                when (state.readmeState) {
                    RepositoryInfoViewModel.ReadmeState.Empty -> binding.tvReadme.text =
                        getString(R.string.no_readme_md)

                    is RepositoryInfoViewModel.ReadmeState.Loaded -> setMarkdown(state.readmeState.markdown)
                    else -> {}
                }
            }
        }
    }

    private fun doThenRepositoryInfoStateIsLoaded(githubRepo: Repo) {
        binding.tvLink.text = githubRepo.link.substringAfter("https://")
        binding.tvLicenseName.text = githubRepo.licenseHeading
        binding.tvStartsRate.text = githubRepo.starsCount.toString()
        binding.tvForksRate.text = githubRepo.forksCount.toString()
        binding.tvWatchersRate.text = githubRepo.watchersCount.toString()
        binding.tvLink.setOnClickListener {
            openRepoLinkInBrowser(githubRepo.link)
        }
    }

    private fun initViews() {
        binding.toolbar.ibNavigation.visibility = View.VISIBLE
        binding.messageGroup.tvMessage.text = getString(R.string.something_error)
        binding.messageGroup.tvMessage.setTextColor(getColor(R.color.errorMessage))
        binding.messageGroup.tvExtraInfo.text = getString(R.string.check_your_something)
        binding.messageGroup.ivRequestState.setImageResource(R.drawable.ic_something_wrong)
        binding.messageGroup.bt.root.text = getString(R.string.retry)

        binding.toolbar.ibNavigation.setOnClickListener {
            navigationToBackScreen()
        }
        binding.toolbar.ibLogOut.setOnClickListener {
            viewModel.onLogOutButtonPressed()
            exitApplication()
        }

        binding.messageGroup.bt.root.setOnClickListener {
            viewModel.onRetryButtonPressed()
        }
    }

    private fun setMarkdown(content: String) {
        val markwon = Markwon.builder(requireContext()).build()
        markwon.setMarkdown(binding.tvReadme, decodeBase64(content))
    }

    private fun decodeBase64(encoded: String): String {
        val decodedBytes = Base64.decode(encoded, Base64.DEFAULT)
        return String(decodedBytes, Charsets.UTF_8)
    }

    private fun getColor(resId: Int): Int {
        return ContextCompat.getColor(requireContext(), resId)
    }

    private fun openRepoLinkInBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun navigationToBackScreen() {
        findNavController().navigateUp()

    }

    private fun exitApplication() {
        requireActivity().finishAffinity()
    }
}