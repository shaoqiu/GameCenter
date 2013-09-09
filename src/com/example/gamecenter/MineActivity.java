package com.example.gamecenter;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MineActivity extends Activity {

	private int xCount = 0;
	private int yCount = 0;
	private int totalCount = 0;
	private int flagCount = 0;
	private int findCount = 0;
	private int coverGridCount = 0;
	
	public final static int RUNNING = 0;
	public final static int GAMEOVER = 1;
	public final static int GAMEWIN = 2;
	private int status = GAMEOVER;
	
	private boolean firstClicked = true;
	private MyButton[][] bts = null;
	
	LinearLayout rootLayout;
	ImageButton newGameButton;
	TextView flagTotalTextView;
	Chronometer timerView;
	
	int[][] dir = new int[][]{ {0,1},{0,-1},{1,0},{-1,0},{-1,-1},{-1,1},{1,-1},{1,1}};
	int[] ids = new int[]{R.drawable.empty, R.drawable.one, R.drawable.two,
			R.drawable.three, R.drawable.four, R.drawable.five,
			R.drawable.six, R.drawable.seven, R.drawable.eight};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.mine);
		
		newGameButton = (ImageButton) findViewById(R.id.new_game_button);
		flagTotalTextView = (TextView) (findViewById(R.id.flag_total_view));
		timerView = (Chronometer)findViewById(R.id.timer);
		rootLayout = (LinearLayout)findViewById(R.id.layout);
		newGame(null);
	}
	
	class LevelButtonClickedListener implements OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Button bt = (Button)v;
			String str = bt.getText().toString();

			if(str.startsWith("8 * 8"))
				initNewGame(8, 8, 10);
			else if(str.startsWith("16 * 16"))
				initNewGame(16, 16, 40);
			else if(str.startsWith("24 * 24"))
				initNewGame(24, 24, 99);
			else if(str.startsWith("custom"))
			{
				customer();
			}
		}

	}
	
	
	public void customer()
	{
		LayoutInflater factory = LayoutInflater.from(this);  
        final View textEntryView = factory.inflate(R.layout.input_dialog, null);
        
		final EditText rowEdit = (EditText) textEntryView.findViewById(R.id.row_edit);
		final EditText colEdit = (EditText) textEntryView.findViewById(R.id.col_edit);
		final EditText mineEdit = (EditText) textEntryView.findViewById(R.id.mine_edit);
		
		AlertDialog.Builder ad1 = new AlertDialog.Builder(this);  
        ad1.setTitle("customer:");  
        ad1.setIcon(android.R.drawable.ic_dialog_info);  
        ad1.setView(textEntryView);  
        ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int i) {  
            	int row = Integer.parseInt(rowEdit.getText().toString());
            	int col = Integer.parseInt(colEdit.getText().toString());
            	int mine = Integer.parseInt(mineEdit.getText().toString());
            	//合理性的判断
            	if(row < 8) row = 8;
            	if(row > 32) row = 32;
            	if(col < 8) col = 8;
            	if(col > 32) col = 32;
            	if(mine < 10) mine = 10;
            	if(mine > row*col/2) mine = row*col/2;
            	initNewGame(col, row, mine);
            }  
        });  
        ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int i) {  
  
            }  
        });  
        ad1.show();// 显示对话框  
	}
	public void initNewGame(int x, int y, int total)
	{
		xCount = x;
		yCount = y;
		flagCount = 0;
		totalCount = total;
		findCount = 0;
		coverGridCount = x * y;
		setStatus(RUNNING);
		updateFlagTotalView();
		bts = new MyButton[y][x]; //y row ,x col
		firstClicked = true;
		

		int width = rootLayout.getWidth();
		int height = rootLayout.getHeight();
		int w = width / x;
		int h = height / y;
		int s = w < h ? w:h;
		if(w < h)
			rootLayout.setPadding(0, (height - width)/2, 0, (height - width)/2);
		else
			rootLayout.setPadding((height - width)/2, 0, (height - width)/2, 0);

		MineButtonClickedListener mineButtonListener = new MineButtonClickedListener();
		rootLayout.removeAllViews();
		for(int i=0;i<y;i++)
		{
			LinearLayout ll = new LinearLayout(this);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			rootLayout.addView(ll);

			for(int j=0;j<x;j++)
			{
				MyButton bt = new MyButton(this);
				ll.addView(bt);
				bt.setStatus(MyButton.COVER);
				bt.setx(j);
				bt.sety(i);
				bt.setMineCount(0);
				bt.getLayoutParams().width = s;
				bt.getLayoutParams().height = s;
				bt.setAdjustViewBounds(true);
				bt.setPadding(0, 0, 0, 0);
				bt.setOnClickListener(mineButtonListener);
				bt.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						MyButton bt = (MyButton) v;
						if(bt.getStatus() == MyButton.COVER)
							flag(bt);
						return true;
					}
				});
				bts[i][j] = bt;
			}

		}
	}
	
	public void newGame(View v)
	{
		stopTimer();
		newGameButton.setImageResource(R.drawable.face_smile);
		
		String[][] str = new String[][]{{"8 * 8","16 * 16"},{"24 * 24","custom"}};
		LevelButtonClickedListener listener = new LevelButtonClickedListener();

		rootLayout.removeAllViews();
		for(int i=0;i<2;i++)
		{
			LinearLayout ll = new LinearLayout(this);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			rootLayout.addView(ll);
			ll.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 
					LayoutParams.WRAP_CONTENT, 1));

			for(int j=0;j<2;j++)
			{
				Button bt = new Button(this);
				ll.addView(bt);
				bt.setText(str[i][j]);
				bt.setPadding(2, 2, 2, 2);
				bt.setOnClickListener(listener);
				bt.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
				bt.getLayoutParams().height = LayoutParams.MATCH_PARENT;
				bt.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 
						LayoutParams.MATCH_PARENT, 1));
			}

		}

	}
	
	public void startTimer()
	{
		timerView.setBase(SystemClock.elapsedRealtime());
		timerView.start();
	}
	public void stopTimer()
	{
		timerView.stop();
	}
	
	class MineButtonClickedListener implements OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(getStatus() == GAMEOVER || getStatus() == GAMEWIN)return;
			MyButton bt = (MyButton) v;
			
			if(firstClicked)
			{
				firstClicked(bt);
				return;
			}

			if(bt.getStatus() == MyButton.COVER){
				unCover(bt);
			}
			else if(bt.getStatus() == MyButton.UNCOVER){
				clickedOnUncover(bt);
			}
			else if(bt.getStatus() == MyButton.FLAG){
				unFlag(bt);
			}
		}
		
	}
	private void clickedOnUncover(MyButton b)
	{
		
		int k, count = 0;
		for(k=0;k<8;k++)
		{
			int newx = b.getx()+dir[k][0];
			int newy = b.gety()+dir[k][1];
			if(newx >=0 && newx < getXCount() && 
			   newy >=0 && newy < getYCount())
			{
				if(bts[newy][newx].getStatus() == MyButton.FLAG)
					count++;
			}

		}
		if(count == b.getMineCount())
		{
			for(k=0;k<8;k++)
			{
				int newx = b.getx()+dir[k][0];
				int newy = b.gety()+dir[k][1];
				if(newx >=0 && newx < getXCount() && 
				   newy >=0 && newy < getYCount())
				{
					MyButton c = bts[newy][newx];
					if(c.getStatus() == MyButton.FLAG && c.getMineCount() != -1)
						c.setImageResource(R.drawable.erro_flag);
					else if(c.getStatus() == MyButton.COVER)
						unCover(c);
				}

			}
		}
	}
	private void gameOver()
	{
		stopTimer();
		setStatus(MineActivity.GAMEOVER);

		for(int x=0;x<getXCount();x++)
			for(int y=0;y<getYCount();y++)
			{
				MyButton bt = bts[y][x];
				if(bt.getStatus() == MyButton.COVER && bt.getMineCount() == -1)
					bt.setImageResource(R.drawable.mine);
			}
		newGameButton.setImageResource(R.drawable.face_sad);
		showInfoDialog(GAMEOVER);
	}

	private void gameWin()
	{
		stopTimer();
		setStatus(MineActivity.GAMEWIN);
		newGameButton.setImageResource(R.drawable.face_cool);
		showInfoDialog(GAMEWIN);

	}
	private void showInfoDialog(int status)
	{
		AlertDialog.Builder ad1 = new AlertDialog.Builder(this);  
         
        if(status == GAMEWIN)
        {
        	ad1.setTitle("you win!"); 
        	ad1.setIcon(R.drawable.face_win);  
        }
        else{
        	ad1.setTitle("you lose"); 
        	ad1.setIcon(R.drawable.face_sad);
        }
        ad1.setPositiveButton("OK", null);
        ad1.show();// 显示对话框  
	}
	private void flag(MyButton bt)
	{
		bt.setStatus(MyButton.FLAG);
		bt.setImageResource(R.drawable.flag);
		flagCount++;
		//标记的是否真的是雷
		if(bt.getMineCount() == -1)
		{
			findCount++;
			coverGridCount--;
		}
		updateFlagTotalView();
		if(findCount == totalCount || coverGridCount == (totalCount-findCount))
			gameWin();
	}
	private void unFlag(MyButton bt)
	{
		bt.setStatus(MyButton.COVER);
		bt.setImageResource(R.drawable.cover);
		flagCount--;
		if(bt.getMineCount() == -1)
		{
			findCount--;
			coverGridCount++;
		}
		updateFlagTotalView();
	}
	private void firstClicked(MyButton bt) {
		firstClicked=false;
		startTimer();

		int count = 0;
		Random rd = new Random();
		while(count < totalCount)
		{
			int x = rd.nextInt(getXCount());
			int y = rd.nextInt(getYCount());

			//不是第一个点，且之前没有放过雷
			if(	x != bt.getx() && y != bt.gety() &&
					bts[y][x].getMineCount() != -1 )
			{
				int k;
				//雷不能在第一个点的周边！
				for(k=0;k<8;k++)
					if(x == bt.getx()+dir[k][0] && y == bt.gety()+dir[k][1])
						break;
				if (k<8) continue;

				//设置为雷
				bts[y][x].setMineCount(-1);
				count ++;

				//遍历雷的八个方向，如果某点不是雷，则该点的雷数加1
				for(k=0;k<8;k++)
				{
					int newx = x+dir[k][0];
					int newy = y+dir[k][1];
					if(newx >=0 && newx < getXCount() && 
							newy >=0 && newy < getYCount() && 
							bts[newy][newx].getMineCount() != -1)
						bts[newy][newx].setMineCount(bts[newy][newx].getMineCount()+1);
				}
			}
		}
		//打开第一个点
		unCover(bt);

	}
	
	private void unCover(MyButton bt)
	{
		if(bt.getMineCount() == -1)
		{
			bt.setImageResource(R.drawable.bang);
			bt.setStatus(MyButton.UNCOVER);
			gameOver();
			return;
		}

		bt.setImageResource(ids[bt.getMineCount()]);
		bt.setStatus(MyButton.UNCOVER);
		coverGridCount--;
		
		if(bt.getMineCount() == 0)
		{
			lianSuoFangYing(bt);
		}
		
		if(coverGridCount == (totalCount-findCount))
			gameWin();

		updateFlagTotalView();
	}

	private void lianSuoFangYing(MyButton bt)
	{
		MyButton[] que = new MyButton[1000];
		int head=0,tail=0;
		que[tail++] = bt;
		int k;MyButton b;
		while(head!=tail)
		{
			b = que[head++];
			for(k=0;k<8;k++)
			{
				int newx = b.getx()+dir[k][0];
				int newy = b.gety()+dir[k][1];
				if(newx >=0 && newx < getXCount() && 
						newy >=0 && newy < getYCount() && 
						bts[newy][newx].getStatus() == MyButton.COVER)
				{
					bts[newy][newx].setImageResource(ids[bts[newy][newx].getMineCount()]);
					bts[newy][newx].setStatus(MyButton.UNCOVER);
					coverGridCount--;
					if(bts[newy][newx].getMineCount() == 0)
						que[tail++] = bts[newy][newx];
				}

			}
		}
	}
	
	
	
	public int getXCount(){
		return xCount;
	}
	public int getYCount(){
		return yCount;
	}
	public void updateFlagTotalView()
	{
		flagTotalTextView.setText(""+flagCount+"/"+totalCount);
	}
	public void setStatus(int s)
	{
		status = s;
	}
	public int getStatus()
	{
		return status;
	}
}
