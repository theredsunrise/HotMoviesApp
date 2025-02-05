package com.example.hotmovies.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.hotmovies.R
import com.example.hotmovies.databinding.FragmentLoginBinding
import com.example.hotmovies.presentation.login.viewModel.LoginViewModelFactory
import com.example.hotmovies.presentation.login.viewModel.actions.LoginViewModel
import com.example.hotmovies.presentation.login.viewModel.actions.LoginViewModel.Actions.Animation
import com.example.hotmovies.presentation.login.viewModel.actions.LoginViewModel.Actions.Login
import com.example.hotmovies.presentation.shared.transitions.TransitionFactory
import com.example.hotmovies.shared.Async
import com.example.hotmovies.shared.diContainer
import com.example.hotmovies.shared.doOnLayoutAsync
import com.example.hotmovies.shared.hideSoftKeyboardAsync
import com.example.hotmovies.shared.safeNavigation
import com.example.hotmovies.shared.userInteractionComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            diContainer()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.layoutViewModel = loginViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exitTransition = TransitionFactory.materialSharedAxis(userInteractionComponent, true)
        reenterTransition = TransitionFactory.materialSharedAxis(userInteractionComponent, false)

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main.immediate) {
            view.doOnLayoutAsync()
            val animator = LoginAnimator(userInteractionComponent, binding.motionLayout)
            animator.transitionToStartAsync(true)
            binding.login.setOnClickListener {
                launch(Dispatchers.Main.immediate) {
                    userInteractionComponent.isEnabled = false
                    activity?.hideSoftKeyboardAsync()
                    userInteractionComponent.isEnabled = true
                    animator.transitionToEndAsync(immediately = false)
                }
            }

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch(Dispatchers.Main.immediate) {
                    animator.state.collect { state ->
                        processAnimationAction(state)
                    }
                }

                launch(Dispatchers.Main.immediate) {
                    loginViewModel.state.collect { state ->
                        processLoginAction(state, animator)
                    }
                }
            }
        }
    }

    private suspend fun processLoginAction(
        state: LoginViewModel.UIState,
        animator: LoginAnimator
    ) {
        val loginAction =
            state.loginAction.getContentIfNotHandled() ?: return
        when {
            loginAction.isSuccessTrue -> navigate()
            loginAction.isFailure -> {
                animator.transitionToStartAsync(immediately = true)
            }
        }
    }

    private fun processAnimationAction(state: Async<LoginAnimator.TransitionState>) {
        loginViewModel.doAction(Animation(state.isProgress))
        if (state.success?.isEnd == true) {
            loginViewModel.doAction(Login)
        }
    }

    private fun navigate() = findNavController().safeNavigation(R.id.loginFragment) {
        val navDirection = LoginFragmentDirections.actionLoginFragmentToMovieFragment(true)
        val extras = FragmentNavigatorExtras(binding.login to "toCardView")
        navigate(navDirection, extras)
    }
}
