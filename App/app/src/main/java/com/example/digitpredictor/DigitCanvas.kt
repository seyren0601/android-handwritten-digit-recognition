package com.example.digitpredictor

import android.R.attr
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class DigitCanvas (context: Context, attrs:AttributeSet? = null): View(context, attrs) {
    var background:Int
    var defaultBitmap:Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    var defaultCanvas:Canvas = Canvas(defaultBitmap)
    var mPaint:Paint
    var mPath:Path = Path()
    var mWidth:Int = 1
    var mHeight:Int = 1
    var init = false

    init{
        mPaint = Paint()
        background = Color.WHITE
        mPaint.setColor(Color.BLACK)
        mPaint.strokeWidth = 50F
        mPaint.style = Paint.Style.STROKE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mWidth =  w
        mHeight = h

        defaultBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
        defaultCanvas = Canvas(defaultBitmap)
        defaultCanvas.drawColor(background)

        /*
        // Draw border of canvas
        mPaint.color = Color.BLACK
        mPaint.style = Paint.Style.STROKE
        // need to pad by 30 pixel, or the bottom and right border will exceed the canvas
        defaultCanvas.drawRect(x, y, x + width - (2 * mPaint.strokeWidth), y + height - (2 * mPaint.strokeWidth), mPaint)
        */
    }

    override fun onDraw(canvas: Canvas){
        super.onDraw(canvas)

        canvas.drawBitmap(defaultBitmap, 0F, 0F, null)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val xTouch = event!!.x
        val yTouch = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(xTouch, yTouch)
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(xTouch, yTouch)
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                touchUp()
            }
        }

        return true
    }

    var mX :Float = 0.0F
    var mY: Float = 0.0F

    private fun touchStart(x:Float, y:Float){
        mPath.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun touchMove(x:Float, y:Float){
        var dx = Math.abs(x - mX)
        var dy = Math.abs(y - mY)

        if(dx >= 4 || dy >= 4){
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY) / 2)
            mX= x
            mY = y

            defaultCanvas.drawPath(mPath, mPaint)
        }
    }

    private fun touchUp(){
        mPath.reset()
    }

    fun clearCanvas(){
        defaultBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
        defaultCanvas = Canvas(defaultBitmap)
        defaultCanvas.drawColor(background)

        invalidate()
    }
}