package com.example.gamecenter;

import android.content.Context;
import android.widget.ImageButton;

public class MyButton extends ImageButton {

	public MyButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private int xCoordinate;
	private int yCoordinate;
	public void setx(int x)
	{
		xCoordinate = x;
	}
	public int getx()
	{
		return xCoordinate;
	}
	public void sety(int y)
	{
		yCoordinate = y;
	}
	public int gety()
	{
		return yCoordinate;
	}
	
	public final static int COVER = 0;
	public final static int UNCOVER = 1;
	public final static int FLAG = 2;
	private int status = COVER;
	public void setStatus(int s)
	{
		status = s;
	}
	public int getStatus()
	{
		return status;
	}
	
	private int mineCount; //-1 is mine, >=0 is the mine count around the grid
	public void setMineCount(int c)
	{
		this.mineCount = c;
	}
	public int getMineCount() 
	{
		return mineCount;	
	}
}
