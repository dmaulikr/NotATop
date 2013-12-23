package com.gg.view;

import com.gg.util.Constant;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class FirstTimeSurfaceView extends SurfaceView 
implements SurfaceHolder.Callback  //ʵ���������ڻص��ӿ�
{
	MainActivity mainActivity;
	Paint paint;//����
	Bitmap background;//��ǰlogoͼƬ����
	
	private Toast helpToast;

	
	public FirstTimeSurfaceView(MainActivity mainActivity) {
		super(mainActivity);
		this.mainActivity = mainActivity;
		this.getHolder().addCallback(this);//�����������ڻص��ӿڵ�ʵ����
		paint = new Paint();//��������
		paint.setAntiAlias(true);//�򿪿����
		
		//����ͼƬ
		background=BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.first_time_bg); 
		
		helpToast = Toast.makeText(mainActivity, "�����������", Toast.LENGTH_SHORT);

	}
	public void onDraw(Canvas canvas){	
		
		//���ƺ��������屳��
		paint.setColor(Color.WHITE);//���û�����ɫ
		paint.setAlpha(255);
		canvas.drawRect(0, 0, Constant.SCREEN_WIDTH, Constant.SCREEN_WIDTH, paint);

		canvas.drawBitmap(background, new Rect(0, 0,
				(int) background.getWidth(), (int) background
						.getHeight()), new Rect(0, 0,
				(int) Constant.SCREEN_WIDTH, (int) Constant.SCREEN_HEIGHT), paint);


	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
	}
	public void surfaceCreated(SurfaceHolder holder) {//����ʱ������		

		SurfaceHolder myholder=FirstTimeSurfaceView.this.getHolder();
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
				
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {//����ʱ������

	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		double x = Constant.convertX(event.getX());
		double y = Constant.convertY(event.getY());
		

		SharedPreferences settings = mainActivity.getSettings();
		//voiceControlFlag = settings.getBoolean("voiceControlFlag", true);
	    SharedPreferences.Editor editor = settings.edit();
//	    editor.putBoolean("voiceControlFlag", false);
//	    editor.commit();
		
		if(x>-1 && x<-0.2 && y>-0.8 && y<-0.2){
		    editor.putBoolean("voiceControlFlag", true);
		    editor.commit();
		    mainActivity.getVoiceControl().setFlag(true);

		    helpToast.show();
			mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.HELP);
		}else if(x>0.2 && x<1 && y>-0.8 && y<-0.2){
		    editor.putBoolean("voiceControlFlag", false);
		    editor.commit();

		    helpToast.show();
			mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.HELP);
		}
		
		
		//return super.onTouchEvent(event);
		return false;
	}
	
	
}