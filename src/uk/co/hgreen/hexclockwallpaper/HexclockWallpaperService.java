package uk.co.hgreen.hexclockwallpaper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.text.format.Time;
import android.view.SurfaceHolder;

public class HexclockWallpaperService extends WallpaperService {

	@Override
	public Engine onCreateEngine() {
		return new MyWallpaperEngine();
	}

	private class MyWallpaperEngine extends Engine {
		private final Handler handler = new Handler();
		private final Runnable drawRunner = new Runnable() {
			@Override
			public void run() {
				draw();
			}

		};
		private Paint paint = new Paint();
		private boolean visible = true;
		private Time time;
		private Typeface font;

		public MyWallpaperEngine() {
			font =  Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Lato-Hairline.ttf");
			paint.setAntiAlias(true);
			paint.setColor(Color.WHITE);
			paint.setTextSize(120.0f);
			paint.setTextAlign(Align.CENTER);
			paint.setTypeface(font);
			handler.post(drawRunner);
			time = new Time(Time.getCurrentTimezone());
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			this.visible = visible;
			if (visible) {
				handler.post(drawRunner);
			} else {
				handler.removeCallbacks(drawRunner);
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			this.visible = false;
			handler.removeCallbacks(drawRunner);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
		}

		private void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;
			
			try {
				canvas = holder.lockCanvas();
				if (canvas != null) {
					drawTime(canvas);
				}
			} finally {
				if (canvas != null)
					holder.unlockCanvasAndPost(canvas);
			}
			handler.removeCallbacks(drawRunner);
			if (visible) {
				handler.postDelayed(drawRunner, 50);
			}
		}
		
		private void drawTime(Canvas canvas){
			int xPos = (canvas.getWidth() / 2);
			int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ; 
			time.setToNow();
			String current = time.format("%k%M%S");
			int hex = Integer.parseInt(current, 16);
			int r = (hex >> 16) & 0xFF;
			int g = (hex >> 8) & 0xFF;
			int b = (hex >> 0) & 0xFF;
			canvas.drawRGB(r, g, b);
			canvas.drawText("#"+current, xPos, yPos-60, paint);
		}
	}

}