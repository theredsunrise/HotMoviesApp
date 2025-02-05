package com.example.hotmovies.presentation.shared.transitions

import android.graphics.Color
import androidx.transition.Transition
import com.example.hotmovies.R
import com.example.hotmovies.presentation.UserInteractionConfigurableComponent
import com.example.hotmovies.shared.Constants
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis

object TransitionFactory {
    fun materialSharedAxis(
        userInteractionComponent: UserInteractionConfigurableComponent,
        forward: Boolean,
        duration: Long = Constants.AnimationDurations.DEFAULT
    ): UserInteractionTransitionWrapper {
        val materialTransition =
            MaterialSharedAxis(MaterialSharedAxis.Z, forward).apply { this.duration = duration }
        return UserInteractionTransitionWrapper(userInteractionComponent, materialTransition)
    }

    fun custom(
        userInteractionComponent: UserInteractionConfigurableComponent,
        transition: Transition,
        duration: Long = Constants.AnimationDurations.DEFAULT
    ): UserInteractionTransitionWrapper {
        return UserInteractionTransitionWrapper(
            userInteractionComponent,
            transition.apply { this.duration = duration })
    }

    fun materialContainerTransform(
        userInteractionComponent: UserInteractionConfigurableComponent,
        duration: Long = Constants.AnimationDurations.DEFAULT,
        drawingViewId: Int = R.id.nav_host_fragment_content_main,
    ): UserInteractionTransitionWrapper {

        val containerTransition = MaterialContainerTransform().apply {
            this.drawingViewId = drawingViewId
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(Color.TRANSPARENT)
        }.apply { this.duration = duration }
        return UserInteractionTransitionWrapper(
            userInteractionComponent,
            containerTransition
        )
    }
}