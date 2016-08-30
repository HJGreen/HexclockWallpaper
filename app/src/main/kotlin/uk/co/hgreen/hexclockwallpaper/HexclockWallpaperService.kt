package uk.co.hgreen.hexclockwallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Typeface
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.text.SimpleDateFormat
import java.util.Calendar

class HexclockWallpaperService : WallpaperService() {

    override fun onCreateEngine(): WallpaperService.Engine {
        return WallpaperEngine()
    }

    private inner class WallpaperEngine : WallpaperService.Engine() {
        private val handler = Handler()
        private val drawRunner = Runnable { draw() }
        private val paint = Paint()
        private var width: Int = 0
        private var height: Int = 0
        private var visible = true
        private val dateFormat: SimpleDateFormat
        private val font: Typeface

        init {
            font = Typeface.createFromAsset(applicationContext.assets, "fonts/Lato-Hairline.ttf")
            paint.isAntiAlias = true
            paint.color = Color.WHITE
            paint.textSize = 120.0f
            paint.textAlign = Align.CENTER
            paint.typeface = font
            handler.post(drawRunner)
            dateFormat = SimpleDateFormat("HHmmss")
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                handler.post(drawRunner)
            } else {
                handler.removeCallbacks(drawRunner)
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            this.visible = false
            handler.removeCallbacks(drawRunner)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            this.width = width
            this.height = height
            super.onSurfaceChanged(holder, format, width, height)
        }

        private fun draw() {
            val holder = surfaceHolder
            var canvas: Canvas? = null

            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    drawTime(canvas)
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }

            handler.removeCallbacks(drawRunner)

            if (visible) {
                handler.postDelayed(drawRunner, 50)
            }
        }

        private fun drawTime(canvas: Canvas) {
            val xPos = width / 2
            val yPos = (height / 2 - (paint.descent() + paint.ascent()) / 2).toInt()
            val current = dateFormat.format(Calendar.getInstance().time)
            val hex = Integer.parseInt(current, 16)
            val r = hex shr 16 and 0xFF
            val g = hex shr 8 and 0xFF
            val b = hex shr 0 and 0xFF
            canvas.drawRGB(r, g, b)
            canvas.drawText("#" + current, xPos.toFloat(), (yPos - 60).toFloat(), paint)
        }
    }

}