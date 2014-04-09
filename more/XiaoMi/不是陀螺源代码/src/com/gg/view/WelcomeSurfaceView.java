package com.gg.view;

import com.gg.util.Constant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class WelcomeSurfaceView extends SurfaceView 
implements SurfaceHolder.Callback  //ʵ���������ڻص��ӿ�
{
	MainActivity mainActivity;
	Paint paint;//����
	int currentAlpha=0;//��ǰ�Ĳ�͸��ֵ
	
	int screenWidth=Constant.SCREEN_WIDTH;//��Ļ���
	int screenHeight=Constant.SCREEN_HEIGHT;//��Ļ�߶�
	int sleepSpan=50;//������ʱ��ms
	
	Bitmap[] images=new Bitmap[2];//logoͼƬ����
	Bitmap currentImage;//��ǰlogoͼƬ����
	int currentX;
	int currentY;
	
	public WelcomeSurfaceView(MainActivity mainActivity) {
		super(mainActivity);
		this.mainActivity = mainActivity;
		this.getHolder().addCallback(this);//�����������ڻص��ӿڵ�ʵ����
		paint = new Paint();//��������
		paint.setAntiAlias(true);//�򿪿����
		
		//����ͼƬ
		images[0]=BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.welcome_top); 
		images[1]=BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.welcome_gg);
	}
	public void onDraw(Canvas canvas){	
		//���ƺ��������屳��
		paint.setColor(Color.BLACK);//���û�����ɫ
		paint.setAlpha(255);
		canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
		
		//����ƽ����ͼ
		if(currentImage==null)return;
		paint.setAlpha(currentAlpha);		
		//canvas.drawBitmap(currentImage, currentX, currentY, paint);	
		
		canvas.drawBitmap(currentImage, new Rect(0, 0,
				(int) currentImage.getWidth(), (int) currentImage
						.getHeight()), new Rect(0, 0,
				(int) Constant.SCREEN_WIDTH, (int) Constant.SCREEN_HEIGHT), paint);

	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
	}
	public void surfaceCreated(SurfaceHolder holder) {//����ʱ������		
		new Thread()
		{
			public void run()
			{
				for(Bitmap bm:images)
				{
					currentImage=bm;
					//����ͼƬλ��
					currentX=screenWidth/2-bm.getWidth()/2;
					currentY=screenHeight/2-bm.getHeight()/2;
					
					for(int i=255;i>-100;i=i-15)
					{//��̬����ͼƬ��͸����ֵ�������ػ�	
						currentAlpha=i;
						if(currentAlpha<0)
						{
							currentAlpha=0;
						}
						SurfaceHolder myholder=WelcomeSurfaceView.this.getHolder();
						Canvas canvas = myholder.lockCanvas();//��ȡ����
						try{
							synchronized(myholder){
								onDraw(canvas);//����
							}
						}
						catch(Exception e){
							e.printStackTrace();
						}
						finally{
							if(canvas != null){
								myholder.unlockCanvasAndPost(canvas);
							}
						}						
						try
						{
							if(i==255)
							{//������ͼƬ����ȴ�һ��
								Thread.sleep(1000);
							}
							Thread.sleep(sleepSpan);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
				//����������Ϻ�ȥ���˵�����
				//activity.sendMessage(WhatMessage.GOTO_MAIN_MENU_VIEW);
				mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.MAIN_MENU);
			}
		}.start();
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {//����ʱ������

	}
}