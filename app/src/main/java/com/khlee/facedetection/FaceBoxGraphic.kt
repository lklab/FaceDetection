package com.khlee.facedetection

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log

import com.google.mlkit.vision.face.Face

class FaceBoxGraphic(overlay: GraphicOverlay): GraphicOverlay.Graphic(overlay)
{
    companion object
    {
//        const val FACE_POSITION_RADIUS = 10.0f
        const val ID_TEXT_SIZE = 70.0f
//        const val ID_Y_OFFSET = 80.0f
//        const val ID_X_OFFSET = -70.0f
        const val BOX_STROKE_WIDTH = 5.0f

        val COLOR_CHOICES = arrayOf(Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.RED, Color.WHITE, Color.YELLOW)

        var currentColorIndex: Int = 0
    }

    private val facePositionPaint: Paint
    private val idPaint: Paint
    private val boxPaint: Paint

    private var face: Face? = null

    init
    {
        currentColorIndex = (currentColorIndex + 1) % COLOR_CHOICES.size
        val selectedColor = COLOR_CHOICES[currentColorIndex]

        facePositionPaint = Paint()
        facePositionPaint.color = selectedColor

        idPaint = Paint()
        idPaint.color = selectedColor
        idPaint.textSize = ID_TEXT_SIZE

        boxPaint = Paint()
        boxPaint.color = selectedColor
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = BOX_STROKE_WIDTH
    }

    fun updateFace(face: Face)
    {
        this.face = face
        postInvalidate()
    }

    override fun draw(canvas: Canvas)
    {
        val face = this.face ?: return

        val x = translateX(face.boundingBox.centerX().toFloat())
        val y = translateY(face.boundingBox.centerY().toFloat())
//        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint)
//        canvas.drawText("id: ${face.trackingId}", x + ID_X_OFFSET, y + ID_Y_OFFSET, idPaint)

        val xOffset = scaleX(face.boundingBox.width().toFloat() / 2.0f)
        val yOffset = scaleY(face.boundingBox.height().toFloat() / 2.0f)
        val left = x - xOffset
        val top  = y - yOffset
        val right = x + xOffset
        val bottom = y + yOffset
        canvas.drawRect(left, top, right, bottom, boxPaint)
    }
}
