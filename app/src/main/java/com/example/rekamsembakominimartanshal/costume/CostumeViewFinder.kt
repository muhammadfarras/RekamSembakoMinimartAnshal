package com.example.rekamsembakominimartanshal.costume

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import me.dm7.barcodescanner.core.ViewFinderView

class CostumeViewFinder (context: Context) : ViewFinderView(context){

    override fun drawViewFinderMask(canvas: Canvas?) {
        val width = canvas!!.width
        val height = canvas!!.height
        val framingRect = framingRect

        var paintText = Paint ();
        paintText.textSize = 70f;
//        paintText.color = context.getColor(android.R.color.white)

        canvas!!.drawRect(0f, 0f, width.toFloat(), framingRect.top.toFloat(), mFinderMaskPaint)
        canvas!!.drawRect(
            0f,
            framingRect.top.toFloat(),
            framingRect.left.toFloat(),
            framingRect.bottom + 1.toFloat(),
            mFinderMaskPaint
        )
        canvas!!.drawRect(
            framingRect.right + 1.toFloat(),
            framingRect.top.toFloat(),
            width.toFloat(),
            framingRect.bottom + 1.toFloat(),
            mFinderMaskPaint
        )
        canvas!!.drawRect(
            0f,
            framingRect.bottom + 1.toFloat(),
            width.toFloat(),
            height.toFloat(),
            mFinderMaskPaint
        )
    }

    override fun drawLaser(canvas: Canvas?) {
        super.drawLaser(canvas)
    }
}