package com.example.my3dapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.my3dapp.databinding.ActivityMainBinding
import com.example.my3dapp.databinding.ViewModelContainerBinding
import io.github.sceneview.node.ModelNode
import io.github.sceneview.SceneView



class MainActivity : AppCompatActivity() {
    data class ModelItem(val id: String,
                         val name: String,
                         val assetPath: String)
    private lateinit var binding: ActivityMainBinding
    private val standardModelList = listOf(
        ModelItem("1", "Damaged Helmet", "DamagedHelmet.glb"),
        ModelItem("2", "Range Rover", "model2.glb"),
        ModelItem("3", "Yellow Car", "model1.glb"),
        ModelItem("4", "White Car","model4.glb"),
        ModelItem("5", "Red Car","model5.glb")
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSpawnModel.setOnClickListener {
            openModelSelectionMenu()
        }

    }
    private fun openModelSelectionMenu() {
        val optionsList = standardModelList.map { it.name }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Add 3D Model Viewport")
            .setItems(optionsList) { _, selectionIndex ->
                generate3DContainerView(standardModelList[selectionIndex])
            }
            .show()
    }

@SuppressLint("ClickableViewAccessibility")
private fun generate3DContainerView(targetModel: ModelItem) {
    val containerBinding = ViewModelContainerBinding.inflate(
        LayoutInflater.from(this), binding.canvasContainer, false
    )
    val dynamicRootFrame = containerBinding.rootContainer
    dynamicRootFrame.tag = targetModel.id

    val screenWidth = resources.displayMetrics.widthPixels
    val childCount = binding.canvasContainer.childCount

    dynamicRootFrame.layoutParams = FrameLayout.LayoutParams(
        (screenWidth * 0.88).toInt(),
        (screenWidth * 1.1).toInt()
    ).apply {
        leftMargin = 20 + (childCount * 40)
        topMargin  = 80 + (childCount * 40)
    }
    binding.canvasContainer.addView(dynamicRootFrame)
    containerBinding.btnClose.bringToFront()
    containerBinding.btnClose.elevation = 100f

    val sceneView = containerBinding.sceneView

    sceneView.post {
        loadModel(sceneView, targetModel)
    }

    var interactionMode = false
    var initX = 0f
    var initY = 0f

    containerBinding.btnClose.setOnClickListener {
        binding.canvasContainer.removeView(dynamicRootFrame)
    }

    //  Mode toggle button
    containerBinding.btnInteractMode.setOnClickListener {
        interactionMode = !interactionMode
        if (interactionMode) {
            containerBinding.btnInteractMode.text = "Mode: 3D Object"
            containerBinding.btnInteractMode
                .setBackgroundColor(getColor(android.R.color.holo_orange_dark))
        } else {
            containerBinding.btnInteractMode.text = "Mode: Viewport"
            containerBinding.btnInteractMode.setBackgroundColor(
                getColor(com.google.android.material.R.color.design_default_color_primary)
            )
        }
    }

    //  Pinch — resizes card in normal mode only
    val scaleDetector = ScaleGestureDetector(this,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(d: ScaleGestureDetector): Boolean {
                if (interactionMode) return false
                val lp = dynamicRootFrame.layoutParams as FrameLayout.LayoutParams
                lp.width  = (dynamicRootFrame.width  * d.scaleFactor).toInt().coerceIn(300, 2000)
                lp.height = (dynamicRootFrame.height * d.scaleFactor).toInt().coerceIn(300, 2000)
                dynamicRootFrame.layoutParams = lp
                return true
            }
        })

    // Touch — strictly separated by mode
    dynamicRootFrame.setOnTouchListener { view, event ->

        if (interactionMode) {
            // Interaction mode — all touches go to SceneView
            // drag = rotate model, pinch = zoom model
            sceneView.onTouchEvent(event)
            return@setOnTouchListener true
        }

        // Normal mode — drag moves card, pinch resizes card
        scaleDetector.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                initX = view.x - event.rawX
                initY = view.y - event.rawY
                view.bringToFront()
                containerBinding.btnClose.bringToFront()
                containerBinding.btnInteractMode.bringToFront()
            }
            MotionEvent.ACTION_MOVE -> {
                if (!scaleDetector.isInProgress) {
                    view.animate()
                        .x(event.rawX + initX)
                        .y(event.rawY + initY)
                        .setDuration(0)
                        .start()
                }
            }
        }
        true
    }
}

    private fun loadModel(sceneView: SceneView, targetModel: ModelItem) {
        try {
            val modelInstance = sceneView.modelLoader.createModelInstance(
                assetFileLocation = targetModel.assetPath
            )
            val modelNode = ModelNode(
                modelInstance = modelInstance,
                scaleToUnits  = 1.0f
            )
            sceneView.addChildNode(modelNode)
            Log.d("MODEL_TEST", " Model loaded: ${targetModel.assetPath}")
        } catch (e: Exception) {
            Log.e("MODEL_TEST", "Failed: ${e.message}", e)
        }
    }
}
