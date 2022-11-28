package com.khlee.facedetection

import android.content.Context
import android.graphics.Canvas
import android.hardware.camera2.CameraCharacteristics
import android.util.AttributeSet
import android.view.View

import kotlin.collections.MutableSet
import kotlin.collections.HashSet

class GraphicOverlay(context: Context, attrs: AttributeSet) : View(context, attrs)
{
    private val lock: Any = Object()
    private var previewWidth: Int = 0
    private var offsetX: Int = 0
    private var previewHeight: Int = 0
    private var scaleFactor: Float = 1.0f
    private var offsetY: Int = 0
    private var facing: Int = CameraCharacteristics.LENS_FACING_BACK
    private var graphics: MutableSet<Graphic> = HashSet()

    abstract class Graphic(private var overlay: GraphicOverlay)
    {
        abstract fun draw(canvas: Canvas)

        fun scaleX(horizontal: Float) = horizontal * overlay.scaleFactor
        fun scaleY(vertical: Float) = vertical * overlay.scaleFactor

        val applicationContext: Context = overlay.context.applicationContext

        fun translateX(x: Float): Float
        {
            return if (overlay.facing == CameraCharacteristics.LENS_FACING_FRONT)
                overlay.width - (scaleX(x) + overlay.offsetX)
            else
                scaleX(x) + overlay.offsetX
        }

        fun translateY(y: Float) = scaleY(y) + overlay.offsetY

        fun postInvalidate()
        {
            overlay.postInvalidate()
        }
    }

    fun clear()
    {
        synchronized(lock)
        {
            graphics.clear()
        }
        postInvalidate()
    }

    fun add(graphic: Graphic)
    {
        synchronized(lock)
        {
            graphics.add(graphic)
        }
        postInvalidate()
    }

    fun remove(graphic: Graphic)
    {
        synchronized(lock)
        {
            graphics.remove(graphic)
        }
        postInvalidate()
    }

    fun setCameraInfo(previewWidth: Int, previewHeight: Int, facing: Int)
    {
        synchronized(lock)
        {
            this.previewWidth = previewWidth
            this.previewHeight = previewHeight
            this.facing = facing
        }
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas)
    {
        super.onDraw(canvas)

        synchronized(lock)
        {
            if ((previewWidth != 0) && (previewHeight != 0))
            {
                val canvasRatio = width.toFloat() / height.toFloat()
                val previewRatio = previewWidth.toFloat() / previewHeight.toFloat()

                if (canvasRatio > previewRatio)
                {
                    scaleFactor = width.toFloat() / previewWidth.toFloat()
                    offsetX = 0
                    offsetY = -((previewHeight.toFloat() * scaleFactor - height.toFloat()) * 0.5f).toInt()
                }
                else
                {
                    scaleFactor = height.toFloat() / previewHeight.toFloat()
                    offsetX = -((previewWidth.toFloat() * scaleFactor - width.toFloat()) * 0.5f).toInt()
                    offsetY = 0
                }
            }

            for (graphic: Graphic in graphics)
                graphic.draw(canvas)
        }
    }
}
