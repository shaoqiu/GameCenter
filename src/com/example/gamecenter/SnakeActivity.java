package com.example.gamecenter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class SnakeActivity extends Activity {

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			snake.addDirToQue(Integer.valueOf(snake.UP));
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			snake.addDirToQue(Integer.valueOf(snake.RIGHT));
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			snake.addDirToQue(Integer.valueOf(snake.DOWN));
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			snake.addDirToQue(Integer.valueOf(snake.LEFT));
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private GestureDetector gestureDetector;
	private SnakeView snake;
	private ImageButton statusButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE); 

		this.setContentView(R.layout.snake);
		statusButton = (ImageButton)findViewById(R.id.status_button);
		statusButton.setBackgroundColor(Color.argb(0, 0, 0, 0));
		//		statusButton.setImageResource(R.drawable.start);
		statusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(snake.getstatus() == snake.READY)
				{
					return;
				}
				else if(snake.getstatus() == snake.RUNNING)
				{
					//					statusButton.setText("start");
					statusButton.setImageResource(R.drawable.start);
					snake.pause();
				}
				else
				{
					//					statusButton.setText("pause");
					statusButton.setImageResource(R.drawable.pause);
					snake.start();
				}
			}
		});

		snake = (SnakeView)findViewById(R.id.snake);
		snake.scoreTextView = (TextView)findViewById(R.id.scoreTextView);
		snake.restTimeTextView = (TextView)findViewById(R.id.restTimeTextView);

		gestureDetector = new BuileGestureExt(this,new BuileGestureExt.OnGestureResult() {
			@Override
			public void onGestureResult(int direction) {

				if (snake.getstatus() == snake.READY)
				{
					snake.initNewGame();
					statusButton.setImageResource(R.drawable.pause);
					return;
				}
				if(snake.getstatus() != snake.RUNNING)
				{
					return;
				}


				if (direction == BuileGestureExt.GESTURE_UP)
				{
					snake.addDirToQue(Integer.valueOf(snake.UP));
				}
				else if (direction == BuileGestureExt.GESTURE_DOWN)
				{
					snake.addDirToQue(Integer.valueOf(snake.DOWN));
				}
				else if (direction == BuileGestureExt.GESTURE_LEFT)
				{
					snake.addDirToQue(Integer.valueOf(snake.LEFT));
				}
				else if (direction == BuileGestureExt.GESTURE_RIGHT)
				{
					snake.addDirToQue(Integer.valueOf(snake.RIGHT));
				}
			}
		}
				).Buile();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

}
