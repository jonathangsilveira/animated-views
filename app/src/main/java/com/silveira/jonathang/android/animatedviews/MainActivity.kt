package com.silveira.jonathang.android.animatedviews

import android.app.Activity
import android.os.Bundle
import android.transition.ChangeClipBounds
import android.transition.Fade
import android.transition.Fade.IN
import android.transition.Fade.OUT
import android.transition.Scene
import android.transition.Scene.getSceneForLayout
import android.transition.Slide
import android.transition.TransitionManager.go
import android.transition.TransitionSet
import android.transition.TransitionSet.ORDERING_TOGETHER
import android.view.Gravity.END
import android.view.Gravity.START
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.SearchView
import com.silveira.jonathang.android.animatedviews.R.id.animatedFirstButton
import com.silveira.jonathang.android.animatedviews.R.id.animatedFirstImageButton
import com.silveira.jonathang.android.animatedviews.R.id.animatedHeader
import com.silveira.jonathang.android.animatedviews.R.id.animatedProfileImageView
import com.silveira.jonathang.android.animatedviews.R.id.animatedSearchField
import com.silveira.jonathang.android.animatedviews.R.id.animatedSecondImageButton
import com.silveira.jonathang.android.animatedviews.R.layout.activity_main
import com.silveira.jonathang.android.animatedviews.R.layout.scene_1
import com.silveira.jonathang.android.animatedviews.R.layout.scene_2

class MainActivity : Activity() {
    private val headerView: ViewGroup?
        get() = findViewById(animatedHeader)

    private val enterScene: Scene by lazy {
        getSceneForLayout(headerView, scene_1, this)
    }

    private val exitScene: Scene by lazy {
        getSceneForLayout(headerView, scene_2, this)
    }

    private val allScenes: List<Scene> by lazy {
        listOf(enterScene, exitScene)
    }

    private val transitionSet: TransitionSet
        get() = TransitionSet()
            .setOrdering(ORDERING_TOGETHER)
            .setDuration(500)
            .addTransition(
                Slide(END)
                    .setDuration(500)
                    .addTarget(animatedFirstButton)
            )
            .addTransition(
                Slide(END)
                    .setDuration(500)
                    .addTarget(animatedFirstImageButton)
                    .addTarget(animatedSecondImageButton)
            )
            .addTransition(
                Slide(START)
                    .setDuration(500)
                    .addTarget(animatedProfileImageView)
            )
            .addTransition(
                Fade(OUT)
                    .setDuration(500)
                    .addTarget(animatedFirstImageButton)
                    .addTarget(animatedSecondImageButton)
                    .addTarget(animatedProfileImageView)
            )
            .addTransition(
                Fade(IN)
                    .setDuration(500)
                    .addTarget(animatedFirstButton)
            )
            .addTransition(
                Fade(OUT)
                    .setDuration(500)
                    .addTarget(animatedFirstButton)
            )
            .addTransition(
                ChangeClipBounds()
                    .setDuration(400)
                    .setStartDelay(100)
                    .setInterpolator(LinearInterpolator())
                    .addTarget(animatedSearchField)
            )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)
        setupScenes()
    }

    private fun setupScenes() {
        allScenes.forEach { scene ->
            scene.setEnterAction { onEnterScene(scene) }
        }
        enterScene.enter()
    }

    private fun animateTransition(hasFocus: Boolean) {
        if (hasFocus)
            go(exitScene, transitionSet)
        else
            go(enterScene, transitionSet)
    }

    private fun onFocusChanged(hasFocus: Boolean) {
        println("MainActivity -> onFocusChanged ($hasFocus)")
        animateTransition(hasFocus)
    }

    private fun onStartSearchIntent() {
        println("MainActivity -> onStartSearchIntent")
        animateTransition(true)
    }

    private fun onCloseSearchIntent(): Boolean {
        println("MainActivity -> onCloseSearchIntent")
        animateTransition(false)
        return true
    }

    private fun onEnterScene(scene: Scene) {
        println("MainActivity -> onEnterScene (${scene.sceneRoot?.id})")
        with(scene.sceneRoot) {
            findViewById<SearchView>(animatedSearchField)?.run {
                setOnQueryTextFocusChangeListener { _, hasFocus ->
                    onFocusChanged(hasFocus)
                }
                setOnSearchClickListener { onStartSearchIntent() }
                setOnCloseListener { onCloseSearchIntent() }
            }
            findViewById<Button>(animatedFirstButton)
                ?.setOnClickListener { animateTransition(false) }
        }
    }
}
