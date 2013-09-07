package com.example.gamecenter;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.TextView;

public class SnakeActivity extends Activity {

	private GestureDetector gestureDetector;
	private SnakeView snake;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		
		this.setContentView(R.layout.snake);
		
		snake = (SnakeView)findViewById(R.id.snake);
		snake.scoreTextView = (TextView)findViewById(R.id.scoreTextView);
		snake.restTimeTextView = (TextView)findViewById(R.id.restTimeTextView);
		
		gestureDetector = new BuileGestureExt(this,new BuileGestureExt.OnGestureResult() {
            @Override
            public void onGestureResult(int direction) {
            	
            	if (snake.getStatue() == snake.READY)
            	{
            		snake.initNewGame();
            		return;
            	}
            	
            	if (direction == BuileGestureExt.GESTURE_UP)
            	{
            		snake.addDirToQue(new Integer(snake.UP));
            	}
            	else if (direction == BuileGestureExt.GESTURE_DOWN)
            	{
            		snake.addDirToQue(new Integer(snake.DOWN));
            	}
            	else if (direction == BuileGestureExt.GESTURE_LEFT)
            	{
            		snake.addDirToQue(new Integer(snake.LEFT));
            	}
            	else if (direction == BuileGestureExt.GESTURE_RIGHT)
            	{
            		snake.addDirToQue(new Integer(snake.RIGHT));
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
