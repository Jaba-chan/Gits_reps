package ru.evgenykuzakov.gits_reps.presentation.auth

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.evgenykuzakov.gits_reps.R
import ru.evgenykuzakov.gits_reps.databinding.AuthFragmentBinding
import ru.evgenykuzakov.gits_reps.presentation.detail_info.RepositoryInfoViewModel

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.auth_fragment) {

    private var _binding: AuthFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AuthFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        bindToViewModel()
    }

    private fun initViews(){
        binding.btSingIn.root.setOnClickListener {
            if (viewModel.state.value != AuthViewModel.State.Loading) {
                viewModel.onSignButtonPressed()
            }
        }

        binding.edToken.doAfterTextChanged { text ->
            viewModel.onTokenTextChanged(text.toString())
        }

        binding.edToken.setOnClickListener {
            viewModel.onTokenTextEdited()
        }
    }

    private fun bindToViewModel(){
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.pb.root.visibility = if (state == AuthViewModel.State.Loading) View.VISIBLE else View.INVISIBLE
            binding.btSingIn.root.text = if (state == AuthViewModel.State.Loading) "" else getString(R.string.sing_in)
            binding.tilToken.error = if (state is AuthViewModel.State.InvalidInput) getString(R.string.invalid_token) else null

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.actions.collect { action ->
                when (action) {
                    is AuthViewModel.Action.RouteToMain -> routeToMain()
                    is AuthViewModel.Action.ShowError -> showError()
                }
            }
        }
    }

    private fun routeToMain() {
        viewModel.resetStateToIdle()
        hideKeyboard(binding.edToken)
        navigateToRepositoriesList()
    }

    private fun showError() {
        createAnErrorDialog().show()
    }


    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun createAnErrorDialog(): AlertDialog {
        val builder = AlertDialog.Builder(context, R.style.AlertDialog)
            .setMessage(R.string.error_dialog_text)
            .setTitle(R.string.error)
            .setPositiveButton(R.string.OK) { dialog, _ ->
                viewModel.onDialogPositiveButtonPressed()
                dialog.dismiss()
            }
        return builder.create()
    }

    private fun navigateToRepositoriesList() {
        findNavController().navigate(AuthFragmentDirections
            .actionAuthFragmentToRepositoriesListFragment(true))
    }

}