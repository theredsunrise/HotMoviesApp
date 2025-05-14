package com.example.hotmovies.presentation.initialization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.hotmovies.R
import com.example.hotmovies.databinding.FragmentInitializationBinding
import com.example.hotmovies.presentation.initialization.viewModel.InitializationViewModel
import com.example.hotmovies.presentation.initialization.viewModel.InitializationViewModel.Intents.CheckSessionValidity
import com.example.hotmovies.presentation.initialization.viewModel.InitializationViewModelFactory
import com.example.hotmovies.presentation.shared.fragments.DialogFragment.Actions.Accept
import com.example.hotmovies.presentation.shared.fragments.DialogFragment.Actions.Cancel
import com.example.hotmovies.presentation.shared.fragments.DialogFragment.Actions.None
import com.example.hotmovies.presentation.shared.helpers.DialogFragmentHandler
import com.example.hotmovies.shared.Event
import com.example.hotmovies.shared.ResultState
import com.example.hotmovies.shared.checkMainThread
import com.example.hotmovies.shared.diContainer
import com.example.hotmovies.shared.safeNavigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InitializationFragment : Fragment() {
    private lateinit var binding: FragmentInitializationBinding
    private val errorDialogFactory = DialogFragmentHandler("sessionValidity")
    private val initializationViewModel: InitializationViewModel by viewModels {
        InitializationViewModelFactory(
            diContainer()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(errorDialogFactory)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentInitializationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main.immediate) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch(Dispatchers.Main.immediate) {
                    initializationViewModel.state.collect { state ->
                        processSessionValidityAction(state)
                    }
                }
                launch(Dispatchers.Main.immediate) {
                    errorDialogFactory.state.collect { action ->
                        when (action) {
                            is Accept -> initializationViewModel.sendIntent(CheckSessionValidity)
                            is Cancel -> requireActivity().finish()
                            is None -> {}
                        }
                    }
                }
            }
        }
    }

    private fun processSessionValidityAction(sessionValidityAction: Event<ResultState<Boolean>>) {
        checkMainThread()
        val sessionValidityAction = sessionValidityAction.getContentIfNotHandled() ?: return

        binding.indicator.isVisible = sessionValidityAction.isProgress
        when (sessionValidityAction) {
            is ResultState.Success -> navigate(sessionValidityAction.value)
            is ResultState.Failure -> {
                errorDialogFactory.showErrorDialog(
                    findNavController(),
                    sessionValidityAction.exception
                )
            }

            ResultState.Progress -> {}
        }
    }

    private fun navigate(isSessionValid: Boolean) =
        findNavController().safeNavigation(R.id.initializationFragment) {
            val directions =
                if (isSessionValid) InitializationFragmentDirections.actionInitializationFragmentToMovieFragment(
                    false
                )
                else InitializationFragmentDirections.actionInitializationFragmentToLoginFragment()
            navigate(directions)
        }
}