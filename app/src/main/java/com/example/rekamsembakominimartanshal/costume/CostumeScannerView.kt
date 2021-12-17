package com.example.rekamsembakominimartanshal.costume

import android.content.Context
import me.dm7.barcodescanner.core.IViewFinder
import me.dm7.barcodescanner.zxing.ZXingScannerView

class CostumeScannerView (context: Context): ZXingScannerView(context) {

    override fun createViewFinderView(context: Context): IViewFinder {
        var viewFinderView:CostumeViewFinder = CostumeViewFinder(context)

//        viewFinderView.setLaserColor(context.getColor(R.color.colorPrimary))
        viewFinderView.setBorderCornerRounded(true)
        viewFinderView.setBorderCornerRadius(40)
//        viewFinderView.setBorderColor(context.getColor(R.color.colorPrimary))
        viewFinderView.setBorderLineLength(80)
        viewFinderView.setBorderStrokeWidth(15)
//        viewFinderView.playSoundEffect(R.)
//        viewFinderView.setMaskColor(context.getColor(R.color.transparant))

        viewFinderView.setLaserEnabled(true)
        return viewFinderView
    }

    override fun setResultHandler(resultHandler: ResultHandler?) {
        super.setResultHandler(resultHandler)
    }


}