package com.example.gamecenter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class SnakeView extends View {


	private int xCount = 18;
	private int yCount = 24;
	private int nodeSize = 0;
	private int xOffset = 0;
	private int yOffset = 0;
	public TextView scoreTextView = null;
	public TextView restTimeTextView = null ;

	//...........................状态..................................
	public final int READY = 0;
	public final int RUNNING = 1;
	public final int GAMEOVER = 2;
	public final int PAUSE = 3;
	private int status = READY;
	public int getstatus()
	{
		return status;
	}
	public void setstatus(int s)
	{
		status = s;
	}
	public void start()
	{
		status = RUNNING;
	}
	public void pause()
	{
		status = PAUSE;

	}
	//...........................状态..................................


	//................................方向.................................
	public final int UP = 0;
	public final int DOWN = 1;
	public final int LEFT = 2;
	public final int RIGHT = 3;
	private int direction = UP;
	private LinkedList<Integer> que = new LinkedList<Integer>();
	public void addDirToQue(Integer d)
	{
		if(!que.isEmpty())
		{
			if(d.intValue() == que.getLast().intValue() || conflict(d,que.getLast()))
				return;
			else que.addLast(d);
		}
		else
		{
			if(conflict(d,direction))
				return;
			else que.addLast(d);
		}
	}
	private boolean conflict(Integer a, Integer b)
	{
		if(a.intValue() == UP && b.intValue() == DOWN ||
			a.intValue() == DOWN && b.intValue() == UP ||
			a.intValue() == LEFT && b.intValue() == RIGHT || 
			a.intValue() == RIGHT && b.intValue() == LEFT)
		return true;
		return false;
	}
	public void setDirection()
	{
		if(que.isEmpty())return;
		direction = que.removeFirst().intValue();
	}
	//................................方向.................................


	//................................速度.................................
	private int speed = 0;
	//................................速度.................................


	//.........................身体.........................................
	private ArrayList<Coordinate> snakeNodes = new ArrayList<SnakeView.Coordinate>();
	public void addNode(Coordinate cd)
	{
		snakeNodes.add(0,cd);
	}
	public void delTail()
	{
		snakeNodes.remove(snakeNodes.size() - 1);
	}
	//.........................身体.........................................


	//.......................食物.............................................
	private Coordinate food;
	public void addFood()
	{
		Random rd = new Random();
		Coordinate newCoord = null;
		boolean found = false;
		while (!found) {
			int newX = rd.nextInt(xCount);
			int newY = rd.nextInt(yCount);
			newCoord = new Coordinate(newX, newY);
			boolean collision = false;
			int snakelength = snakeNodes.size();
			for (int index = 0; index < snakelength; index++) {
				if (snakeNodes.get(index).equals(newCoord)) {
					collision = true;
				}
			}

			found = !collision;
		}
		food = newCoord;
		calDistence();
		showRestTime();
	}
	//......................食物..............................................


	//........................地图.............................................
	public final static int EMPTY = 0;
	public final static int HEAD = 1;
	public final static int BODY = 2;
	public final static int FOOD = 3;

	private int[][] map;
	public void setMap(Coordinate cd, int obj)
	{
		map[cd.x][cd.y] = obj;
	}
	public void clearMap()
	{
		for(int x=0;x<xCount;x++)
			for(int y=0;y<yCount;y++)
				map[x][y]= EMPTY;
	}
	public void updateMap()
	{
		clearMap();
		int i, len = snakeNodes.size();
		if(len == 0) return;
		setMap(snakeNodes.get(0), HEAD);
		for(i=1;i<len;i++)
			setMap(snakeNodes.get(i), BODY);
		setMap(food, FOOD);
	}
	//........................地图.............................................


	//.......................分数及剩余时间.................................
	private int score = 0;
	private int restTime = 0;
	public void showScore()
	{
		scoreTextView.setText("score:"+score);
	}
	public void showRestTime()
	{
		restTimeTextView.setText("rest time:"+restTime);
	}
	public void calDistence()
	{
		updateMap();
		setMap(food, EMPTY);
		Coordinate head = snakeNodes.get(0);
		//restTime = (Math.abs(head.x - food.x) + Math.abs(head.y - food.y));
		//如果bfs能够找到食物，则设置时间为步数，否则时间为100
		//先复制一个地图副本，然后在此副本上进行BFS
		int[][] tmp = new int[xCount][yCount];
		for(int i=0;i<xCount;i++)
			for(int j=0;j<yCount;j++)
				tmp[i][j]=map[i][j];
		
		LinkedList<Coordinate> que = new LinkedList<Coordinate>();
		que.addLast(head);
		boolean flag = false;
		while(!que.isEmpty())
		{
			Coordinate n = que.removeFirst();
			
			if(n.equals(food))
			{
				flag = true;
				restTime = tmp[n.x][n.y];
				break;
			}
			
			int[][] dir = new int[][]{ {0,1},{0,-1},{1,0},{-1,0}};
			for(int k=0;k<4;k++)
			{
				int newx = n.x+dir[k][0];
				int newy = n.y+dir[k][1];
				if(newx >=0 && newx < xCount && 
				   newy >=0 && newy < yCount &&
				   tmp[newx][newy] == EMPTY)
				{
					tmp[newx][newy] = tmp[n.x][n.y]+1;
					que.addLast(new Coordinate(newx, newy));
				}

			}
		}
		if(!flag)restTime = 100;
	}
	public void minusTime()
	{
		if(getstatus() != RUNNING)return;
		
		restTime--;
		if(restTime == 0)
		{
			this.delTail();
			if(snakeNodes.size() == 0)
			{
				gameOver();
				return;
			}
			addFood();
			score--;
			speed+=10;
			if(score<0)score=0;
		}
		showRestTime();
		showScore();
	}
	//.......................分数及剩余时间.................................



	//................................初始化游戏................................
	public void initNewGame()
	{
		this.score = 0;
		this.restTime = 0;
		this.direction = DOWN;
		this.speed = 500;
		this.snakeNodes.clear();
		this.que.clear();
		this.clearMap();
		this.setstatus(RUNNING);
		addNode(new Coordinate(1, 1));
		addNode(new Coordinate(1, 2));
		addFood();
		showScore();
		showRestTime();
		this.invalidate();
		
	}
	//...............................初始化游戏................................


	//.........................移动.............................................
	public void snakeMove(int direction)
	{
		if(getstatus() != RUNNING) return;
		
		Coordinate head = snakeNodes.get(0);
		Coordinate newHead = new Coordinate(0, 0);

		switch (direction) {
		case RIGHT: {
			newHead = new Coordinate(head.x+1, head.y);
			break;
		}
		case LEFT: {
			newHead = new Coordinate(head.x-1, head.y);
			break;
		}
		case UP: {
			newHead = new Coordinate(head.x, head.y-1);
			break;
		}
		case DOWN: {
			newHead = new Coordinate(head.x, head.y+1);
			break;
			}
		}

		//是否超出边界	
		if ((newHead.x < 0) || (newHead.y < 0) || (newHead.y > yCount-1)
				|| (newHead.x > xCount-1)) {
			gameOver();
			return;
		}

		//是否撞到自己	
				int snakelength = snakeNodes.size();
		for (int snakeindex = 0; snakeindex < snakelength; snakeindex++) {
			Coordinate c = snakeNodes.get(snakeindex);
			if (c.equals(newHead)) {
				gameOver();
				return;
			}
		}

		//吃食物	
		if (food.equals(newHead))
		{
			this.addNode(newHead);
			this.addFood();
			score++;
			speed -= 10;
			showScore();
		}else
		{
			this.addNode(newHead);
			this.delTail();
		}
		
		this.invalidate();
	}
	//.........................移动.............................................




	private void gameOver() {
		// TODO Auto-generated method stub
		this.setstatus(GAMEOVER);
		this.invalidate();
	}
	public SnakeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		map = new int[xCount][yCount];
		moveHandler.postDelayed(moveRunnable, speed);
		timerHandler.postDelayed(TimerRunnable, 1000);
	}

	Paint paint = new Paint();  
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		
		paint.setColor(Color.BLUE);  
		paint.setTextSize(40);  
		paint.setStyle(Paint.Style.FILL);  
		paint.setStrokeWidth(1);

		//游戏处于结束状态 	
		if(this.getstatus() == GAMEOVER){
			canvas.drawText("GAME OVER", 50, 200, paint);
			canvas.drawText("TOUCH TO START", 10, 300, paint);
			this.setstatus(READY);
			return;
		}
		
		//游戏处于准备就绪状态
		if(this.getstatus() == READY){
			canvas.drawText("TOUCH TO START", 10, 200, paint);
			return;
		}
		//画网格	
		for (int x=0;x<=xCount;x++)
			canvas.drawLine(xOffset+x*nodeSize, yOffset, xOffset+x*nodeSize, yOffset+yCount*nodeSize, paint);
		for(int y=0;y<=yCount;y++)
			canvas.drawLine(xOffset, yOffset+y*nodeSize, xOffset+xCount*nodeSize, yOffset+y*nodeSize, paint);	

		//画地图	
		updateMap();
		int[] colors = new int[]{Color.WHITE, Color.RED, Color.GREEN, Color.BLUE};
		for(int x=0;x<xCount;x++)
			for(int y=0;y<yCount;y++)
			{
				paint.setColor(colors[map[x][y]]);
				canvas.drawRect(xOffset+x*nodeSize+2, yOffset+y*nodeSize+2, xOffset+x*nodeSize+nodeSize-1, yOffset+y*nodeSize+nodeSize-1, paint);
			}
		
		super.onDraw(canvas);
	}

	//定时移动
	Handler moveHandler=new Handler();
	Runnable moveRunnable=new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(SnakeView.this.getstatus() != SnakeView.this.RUNNING)
			{
				SnakeView.this.moveHandler.postDelayed(this, SnakeView.this.speed);
				return;
			}
			SnakeView.this.setDirection();
			SnakeView.this.snakeMove(SnakeView.this.direction);
			SnakeView.this.moveHandler.postDelayed(this, SnakeView.this.speed);
		}
	};
	//倒计时还剩多少时间要吃到食物
	Handler timerHandler=new Handler();
	Runnable TimerRunnable=new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(SnakeView.this.getstatus() != SnakeView.this.RUNNING)
			{
				SnakeView.this.timerHandler.postDelayed(this, 1000);
				return;
			}
			SnakeView.this.minusTime();
			SnakeView.this.timerHandler.postDelayed(this, 1000);
		}
	};
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		int wSize = (int)Math.floor(w / xCount);
		int hSize = (int)Math.floor(h / yCount);

		if (wSize >= hSize)
			nodeSize = hSize;
		else 
			nodeSize = wSize;

		xOffset = ((w - nodeSize * xCount) / 2);
		yOffset = ((h - nodeSize * yCount) / 2);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	
	private class Coordinate {
		public int x;
		public int y;

		public Coordinate(int newX, int newY) {
			x = newX;
			y = newY;
		}

		public boolean equals(Coordinate other) {
			if (x == other.x && y == other.y) {
				return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return "Coordinate: [" + x + "," + y + "]";
		}
	}

}
