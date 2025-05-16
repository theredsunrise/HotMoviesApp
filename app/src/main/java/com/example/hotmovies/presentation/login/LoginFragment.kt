package com.example.hotmovies.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.displayCutout
import androidx.core.view.WindowInsetsCompat.Type.statusBars
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.hotmovies.R
import com.example.hotmovies.databinding.FragmentLoginBinding
import com.example.hotmovies.presentation.login.viewModel.actions.LoginViewModel
import com.example.hotmovies.presentation.login.viewModel.actions.LoginViewModel.Intents.Animation
import com.example.hotmovies.presentation.login.viewModel.actions.LoginViewModel.Intents.Login
import com.example.hotmovies.presentation.shared.transitions.TransitionFactory
import com.example.hotmovies.shared.Event
import com.example.hotmovies.shared.ResultState
import com.example.hotmovies.shared.application
import com.example.hotmovies.shared.checkMainThread
import com.example.hotmovies.shared.doOnLayoutAsync
import com.example.hotmovies.shared.hideSoftKeyboardAsync
import com.example.hotmovies.shared.safeNavigation
import com.example.hotmovies.shared.userInteractionComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels {
        application.appComponent.loginViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.layoutViewModel = loginViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupInsets()
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
                        processLoginAction(state.loginAction, animator)
                    }
                }
            }
        }
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val detectedInsets = insets.getInsets(statusBars() or displayCutout())
            view.updatePadding(
                view.paddingLeft,
                detectedInsets.top,
                view.paddingRight,
                view.paddingBottom
            )
            WindowInsetsCompat.CONSUMED
        }
    }

    private suspend fun processLoginAction(
        loginAction: Event<ResultState<Boolean>>,
        animator: LoginAnimator
    ) {
        checkMainThread()
        val loginAction = loginAction.getContentIfNotHandled() ?: return
        when {
            loginAction.isSuccessTrue -> navigate()
            loginAction.isFailure -> {
                animator.transitionToStartAsync(immediately = true)
            }
        }
    }

    private fun processAnimationAction(state: ResultState<LoginAnimator.TransitionState>) {
        checkMainThread()
        loginViewModel.sendIntent(Animation(state.isProgress))
        if (state.success?.isEnd == true) {
            loginViewModel.sendIntent(Login)
        }
    }

    private fun navigate() = findNavController().safeNavigation(R.id.loginFragment) {
        val navDirection = LoginFragmentDirections.actionLoginFragmentToMovieFragment(true)
        val extras = FragmentNavigatorExtras(binding.login to "toCardView")
        navigate(navDirection, extras)
    }
}
