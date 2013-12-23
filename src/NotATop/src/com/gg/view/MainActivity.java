/*
 * Copyright (C) 2012 The Project ���������ݡ�
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gg.view;

import java.util.ArrayList;
import com.gg.module.*;
import com.gg.util.Constant;
import com.gg.util.DateUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.*;
import android.widget.Toast;

/*		��������Ψһ��Activity��ͨ���ı�ʹ���Զ�����������л���ͬ�Ľ���		*/
public class MainActivity extends Activity {
	public final static int EXIT_MESSAGE = 100;	// ��ʾ�˳���Ϣ�ĳ���
	public final static int VOICE_MESSAGE = 101; // ��ʾ������Ϣ�ĳ���
	public final static int VIBRATE_MESSAGE = 102;
	
	private boolean pressMenu = false;
	
	private boolean firstTimeFlag;

	private Vibrator vibrator;
	private VoiceControl voiceControl; // ����Google Voice�������ز���
	
	SharedPreferences settings;
	
	private Toast highScoreToast;
	private Toast notHighScoreToast;

	private Handler handler; // ������Ϣ�������л���ͬ�Ľ���
	
	private int surfaceViewIndex; // ��¼��ǰ���������
	
	private SoundControl soundControl;
	
	int currScore;//��Ϸ������ĵ÷�
	int highestScore;
	SQLiteDatabase sld;//SQLiteDatabase���ݿ�
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		requestWindowFeature(Window.FEATURE_NO_TITLE); // ���ر�����
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, // ����ȫ��
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // ���ú���
		
		Display display = getWindowManager().getDefaultDisplay();
		Constant.SCREEN_WIDTH = display.getWidth();
		Constant.SCREEN_HEIGHT = display.getHeight();
		Constant.SCREEN_RATE = (float)(0.5*4/Constant.SCREEN_HEIGHT);
		
		
		
		settings = getPreferences(MODE_PRIVATE);
	    firstTimeFlag = settings.getBoolean("firstTime", true);
	    //System.out.println("flag = "+firstTimeFlag);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean("firstTime", false);
	    editor.commit();
	    
	    soundControl = new SoundControl(this);
	    soundControl.setMusic();
		

		voiceControl = new VoiceControl(this); // �������ع��ܵĶ���
		//voiceControl.setFlag(true); // �������ع��ܿ���
		voiceControl.setFlag( settings.getBoolean("voiceControlFlag", false) );
		

		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);  
		
		highScoreToast = Toast.makeText(MainActivity.this, "��ϲ��ô�����߼�¼", Toast.LENGTH_SHORT);
		notHighScoreToast = Toast.makeText(MainActivity.this, "���ź�û�ܴ�����߼�¼", Toast.LENGTH_SHORT);


		surfaceViewIndex = SurfaceViewFactory.WELCOME; // һ��ʼ���ý���Ϊ��ӭ����
		//surfaceViewIndex = SurfaceViewFactory.GAME_MODE; // ֻ�Ƿ�����Զ���
		setContentView(SurfaceViewFactory.getView(this, surfaceViewIndex)); // ʹ���Զ���Ļ�ӭ����

		handler = new Handler() { // ������Ϣ�������������Ӧ�Ӳ�ͬ���洫������Ϣ���л�����
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);

				if (msg.what == VOICE_MESSAGE) {
					
					if(voiceControl.isFlag()==true){
						Toast.makeText(MainActivity.this, "��ѡ����ʵ�����", Toast.LENGTH_SHORT).show();
						voiceControl.start(); // �������ع���
					}else{
						Toast.makeText(MainActivity.this, "δ��װGoogle Voice���޷��������ع���", Toast.LENGTH_SHORT).show();
					}
				} else if (msg.what == EXIT_MESSAGE) {
					exit(); // ��ʾ�Ƿ��˳�����
				} else if(msg.what == VIBRATE_MESSAGE){
					vibrator.vibrate(50);
				} else {
					soundControl.choose();
					surfaceViewIndex = msg.what; // ���ݴ�������ͬ����Ϣ���õ�ǰ����
					setContentView(SurfaceViewFactory.getView( // ʹ�ù���ģʽ��ò�ͬ�Ľ������
							MainActivity.this, surfaceViewIndex));
				}
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}


	@Override
	public boolean onPrepareOptionsMenu(Menu menu) { // ���²˵���ʱ����
		// TODO Auto-generated method stub
		
		switch(surfaceViewIndex){ // ���ݵ�ǰ��ͬ�Ľ������Բ�ͬ�Ĳ���
		case SurfaceViewFactory.WELCOME:
			break;
		case SurfaceViewFactory.MAIN_MENU:
			break;
		case SurfaceViewFactory.CLASSIC_GAME:
		case SurfaceViewFactory.TIME_GAME:
			if(pressMenu==false){
				pressMenu = true;
			}
			break;
		case SurfaceViewFactory.SELECT:
			break;
		case SurfaceViewFactory.SCORE:
			break;
		case SurfaceViewFactory.HELP:
			break;
		case SurfaceViewFactory.END:
			break;
		case SurfaceViewFactory.GAME_MODE:
//			surfaceViewIndex = SurfaceViewFactory.MAIN_MENU;
//			setContentView(SurfaceViewFactory.getView(MainActivity.this, surfaceViewIndex));
			break;
		case SurfaceViewFactory.FIRST_TIME:
			break;
		}
		

		return super.onPrepareOptionsMenu(menu);
	}
	

	@Override
	public void onBackPressed() { // ���·��ؼ�ʱ����
		// TODO Auto-generated method stub
		// super.onBackPressed(); // ����ע�͵�����ִ������Ĵ���
		
		switch(surfaceViewIndex){ // ���ݵ�ǰ��ͬ�Ľ������Բ�ͬ�Ĳ���
		case SurfaceViewFactory.WELCOME:
			break;
		case SurfaceViewFactory.MAIN_MENU:
			exit();
			break;
		case SurfaceViewFactory.CLASSIC_GAME:
		case SurfaceViewFactory.TIME_GAME:
			if(pressMenu==false){ // ������ͣ��־
				pressMenu = true;
			}
			break;
		case SurfaceViewFactory.SELECT:
		case SurfaceViewFactory.SCORE:
		case SurfaceViewFactory.HELP:
		case SurfaceViewFactory.END:
		case SurfaceViewFactory.GAME_MODE:
			surfaceViewIndex = SurfaceViewFactory.MAIN_MENU;
			setContentView(SurfaceViewFactory.getView(MainActivity.this, surfaceViewIndex));
			break;
		case SurfaceViewFactory.FIRST_TIME:
			vibrator.vibrate(50);
			new AlertDialog.Builder(MainActivity.this) // �����Ի���
			.setTitle("Ϊ��֤������������") // ���ñ���ͼ��ȵ�
			.setIcon(R.drawable.ic_launcher)
			.setMessage("�Ƿ�����������ƹ���")
			.setPositiveButton("ȷ��", // ���ѡ���ˡ�ȷ�ϡ�
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub

						    SharedPreferences.Editor editor = settings.edit();
						    editor.putBoolean("voiceControlFlag", false);
						    editor.commit();
						    
							handler.sendEmptyMessage(SurfaceViewFactory.MAIN_MENU);
							//System.exit(0);
						}
					})
			.setNegativeButton("ȡ��", // ���ѡ���ˡ�ȡ����
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub

						}
					}).show();	
			break;
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { // ��Ӧ���ع��ܴ������ĸ�����Ϣ
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		boolean recognizeFlag = false;

		if (voiceControl.isFlag()) { // ������������ع����������ȥ
			ArrayList<String> words = voiceControl.result(requestCode, // �������ƥ����ַ���
					resultCode, data);
			if (words != null) { // �������ƥ����ַ����������ȥ
				for (int i = 0; i < words.size(); ++i) { // �������п���ƥ����ַ���
					//System.out.println(words.get(i));
					if (words.get(i).equals("start") // ��ӦӢ���������ת�����ֽ���
							|| words.get(i).equals("��ʼ") // ��Ӧ��ͨ������
							|| words.get(i).equals("�_ʼ")) { // ��Ӧ��������
						recognizeFlag = true;
						Toast.makeText(this, "������Ϸ����", Toast.LENGTH_SHORT).show();
						handler.sendEmptyMessage(SurfaceViewFactory.CLASSIC_GAME);
					} else if (words.get(i).equals("select")
							|| words.get(i).equals("ѡ��")
							|| words.get(i).equals("�x��")) {
						recognizeFlag = true;
						Toast.makeText(this, "��������ѡ�����", Toast.LENGTH_SHORT).show();
						handler.sendEmptyMessage(SurfaceViewFactory.SELECT);
					} else if (words.get(i).equals("score")
							|| words.get(i).equals("�߷ְ�")
							|| words.get(i).equals("�߷ְ�")) {
						recognizeFlag = true;
						Toast.makeText(this, "����߷ְ����", Toast.LENGTH_SHORT).show();
						handler.sendEmptyMessage(SurfaceViewFactory.SCORE);
					} else if (words.get(i).equals("help")
							|| words.get(i).equals("����")
							|| words.get(i).equals("����")) {
						recognizeFlag = true;
						Toast.makeText(this, "�����������", Toast.LENGTH_SHORT).show();
						handler.sendEmptyMessage(SurfaceViewFactory.HELP);
					} else if (words.get(i).equals("exit")
							|| words.get(i).equals("�˳�")
							|| words.get(i).equals("�˳�")) {
						recognizeFlag = true;
						handler.sendEmptyMessage(EXIT_MESSAGE);
					}
				}
				if(recognizeFlag==false){
					Toast.makeText(this, "�����޷�ʶ��������", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	
	public Handler getHandler() { // ���ⲿ���ܹ�������Ϣ������󣬲�����Activity���벻ͬ��Ϣ���л�����
		return handler;
	}
	
	
	public boolean isPressMenu() {
		return pressMenu;
	}
	

	public boolean isFirstTimeFlag() {
		return firstTimeFlag;
	}

	public void setFirstTimeFlag(boolean firstTimeFlag) {
		this.firstTimeFlag = firstTimeFlag;
	}

	public void setPressMenu(boolean pressMenu) {
		this.pressMenu = pressMenu;
	}

	public void exit(){ // �Զ�����˳��������ᵯ���Ի������û�ѡ���Ƿ����Ҫ�˳�����
		
		vibrator.vibrate(50);
		//vibrator.vibrate(new long[]{100,400,100,400}, 0);
		
		new AlertDialog.Builder(MainActivity.this) // �����Ի���
		.setTitle("����") // ���ñ���ͼ��ȵ�
		.setIcon(R.drawable.ic_launcher)
		.setMessage("ȷ���˳���Ϸ")
		.setPositiveButton("ȷ��", // ���ѡ���ˡ�ȷ�ϡ�
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub

					    SharedPreferences.Editor editor = settings.edit();
					    editor.putBoolean("crack", false);
					    editor.commit();
					    
						System.exit(0);
					}
				})
		.setNegativeButton("ȡ��", // ���ѡ���ˡ�ȡ����
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub

					}
				}).show();	
	}

	
	public void setVoiceControl(){
		new AlertDialog.Builder(MainActivity.this) // �����Ի���
		.setTitle("��һ�����г���") // ���ñ���ͼ��ȵ�
		.setIcon(R.drawable.ic_launcher)
		.setMessage("Ϊʵ�������ٿع��ܣ���ȷ���Ƿ�װGoogle Voice")
		.setPositiveButton("�Ѱ�װ",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub

						voiceControl.setFlag(true);
					}
				})
		.setNegativeButton("δ��װ",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub

						voiceControl.setFlag(false);
					}
				}).show();		
	}
	
	
	
	
	
	
	
	
	
	
    //�򿪻򴴽����ݿ�ķ���
    public void openOrCreateDatabase()
    {
    	try
    	{
	    	sld=SQLiteDatabase.openDatabase
	    	(
	    			"/data/data/com.gg.view/mydb", //���ݿ�����·��
	    			null, 								//CursorFactory
	    			SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.CREATE_IF_NECESSARY //��д�����������򴴽�
	    	);
	    	String sql="create table if not exists highScore" +
	    			"( " +
	    			"score integer," +
	    			"date varchar(20)" +
	    			");";
	    	sld.execSQL(sql);
    	}
    	catch(Exception e)
    	{
    		Toast.makeText(this, "���ݿ����"+e.toString(), Toast.LENGTH_SHORT).show();
    	}
    }
    //�ر����ݿ�ķ���
    public void closeDatabase()
    {
    	try
    	{
	    	sld.close();
    	}
		catch(Exception e)
		{
			Toast.makeText(this, "���ݿ����"+e.toString(), Toast.LENGTH_SHORT).show();;
		}
    }
    //�����¼�ķ���
    public void insert(int score,String date)
    {
    	try
    	{
        	String sql="insert into highScore values("+score+",'"+date+"');";
        	sld.execSQL(sql);
        	sld.close();
    	}
		catch(Exception e)
		{
			Toast.makeText(this, "���ݿ����"+e.toString(), Toast.LENGTH_SHORT).show();;
		}
    }
    //��ѯ�ķ���
    public String query(int posFrom,int length)//��ʼ��λ�ã�Ҫ��Ѱ�ļ�¼����
    {
    	StringBuilder sb=new StringBuilder();//Ҫ���صĽ��
    	Cursor cur=null;
    	openOrCreateDatabase();
        String sql="select score,date from highScore order by score desc;";    	
        cur=sld.rawQuery(sql, null);
    	try
    	{
    		
        	cur.moveToPosition(posFrom);//���α��ƶ���ָ���Ŀ�ʼλ��
        	int count=0;//��ǰ��ѯ��¼����
        	while(cur.moveToNext()&&count<length)
        	{
        		int score=cur.getInt(0);
        		String date=cur.getString(1);        		
        		sb.append(score);
        		sb.append("/");
        		sb.append(date);
        		sb.append("/");//����¼��"/"�ָ���
        		count++;
        	}        			
    	}
		catch(Exception e)
		{
			Toast.makeText(this, "���ݿ����"+e.toString(), Toast.LENGTH_SHORT).show();
		}
		finally
		{
			cur.close();
			closeDatabase();
		}
		//ת�����ַ���������
		return sb.toString();
    }
    //�õ����ݿ��м�¼�����ķ���
    public int getRowCount()
    {
    	int result=0;
    	Cursor cur=null;
    	openOrCreateDatabase();
    	try
    	{
    		String sql="select count(score) from highScore;";    	
            cur=sld.rawQuery(sql, null);
        	if(cur.moveToNext())
        	{
        		result=cur.getInt(0);
        	}
    	}
    	catch(Exception e)
		{
			Toast.makeText(this, "���ݿ����"+e.toString(), Toast.LENGTH_SHORT).show();
		}
		finally
		{
			cur.close();
			closeDatabase();
		}
        
    	return result;
    }
    
    
    
    
    
    public int getCurrScore() {
		return currScore;
	}

	public void setCurrScore(int currScore) {
		this.currScore = currScore;
	}
	

    //���÷ֺ�ʱ��������ݿ⣬����ת����Ӧ�Ľ�������
    public void goToOverView()
    {
    	Cursor cur=null;
    	openOrCreateDatabase();//�򿪻򴴽����ݿ�
    	try
    	{	
    		//�����ݿ���ѡ����߷�
        	String sql="select max(score) from highScore;";   	
        	cur=sld.rawQuery(sql, null);
        	if(cur.moveToNext())//����������Ϊ�գ��Ƶ���һ��
        	{
        		this.highestScore=cur.getInt(0);
        	}
        	insert(currScore,DateUtil.getCurrentDate());//��õ�ǰ���������ڲ��������ݿ�        	
    	}
		catch(Exception e)
		{
			Toast.makeText(this, "���ݿ����"+e.toString(), Toast.LENGTH_SHORT).show();
		}
		finally
		{
			cur.close();
			closeDatabase();
		}
		
		if(currScore>=highestScore)//�����ǰ�÷ִ��ڻ��ְ�����߷�
		{    	
	    	//this.gotoWinView();//����ʤ���Ľ���
			highScoreToast.show();
			handler.sendEmptyMessage(SurfaceViewFactory.END);
		}
		else//�����ǰ�÷ֲ����ڻ��ְ�����߷�
		{
			//this.gotoFailView();//����ʧ�ܵĽ���
			notHighScoreToast.show();
			handler.sendEmptyMessage(SurfaceViewFactory.END);
		}
    	
    }
    

	public VoiceControl getVoiceControl() {
		return voiceControl;
	}

	public SharedPreferences getSettings() {
		return settings;
	}

	public SoundControl getSoundControl() {
		return soundControl;
	}

	public void setSoundControl(SoundControl soundControl) {
		this.soundControl = soundControl;
	}

    
    
    
}

