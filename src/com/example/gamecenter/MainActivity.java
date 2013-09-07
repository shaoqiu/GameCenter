package com.example.gamecenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClicked (View v)
	{
		Intent intent = new Intent();
		if(v.getId() == R.id.snake)
	
			intent.setClass(MainActivity.this, SnakeActivity.class);

		else if(v.getId() == R.id.mine)
	
			intent.setClass(MainActivity.this, MineActivity.class);
		startActivity(intent);
	}
}
