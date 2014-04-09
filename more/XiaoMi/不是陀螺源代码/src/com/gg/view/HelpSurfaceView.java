package com.gg.view;

import com.gg.util.Constant;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

   

public class HelpSurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable{  
        private SurfaceHolder surfaceHolder;       
        
        private MainActivity mainActivity;
        
        private Thread thread;     //�߳�
        private Canvas canvas;  	//����
        private Paint backgroundPaint;		//����
        private Matrix backgroundMatrix;	//���ڻ������ľ���
        private int width;			//��Ļ���
        private int height;		//��Ļ�߶�
        private Bitmap backgroundImage;		//����ͼƬ
        private boolean endFlag = true;   //�ж�run�����Ƿ����
        private int select = 0;			//�����ж�ѡ��ͼƬ
//        private boolean touchFlag = false;			//�����ж��Ƿ���Ӧ�����¼�
//        private float firstX , endX;			//�����ĵ�һ������һ��,������Ӧ��������
        private double downX;
        private double downY;
        private double upX;
        private double upY;
        
        private Toast lastToast;
        
          
        public HelpSurfaceView(Context context) {    //��ʼ��
            super(context);  
            mainActivity = (MainActivity)context;
            
            surfaceHolder=this.getHolder();  
            surfaceHolder.addCallback(this);  
            thread=new Thread(this);   
            backgroundPaint = new Paint(); 
            backgroundPaint.setColor(Color.RED);
            backgroundMatrix = new Matrix();
            backgroundImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.help1_bg);
            
            lastToast = Toast.makeText(context, "�Ѿ������һҳ��", Toast.LENGTH_SHORT);
              
            this.setKeepScreenOn(true);  
        }  
        
        public void surfaceChanged(SurfaceHolder holder, int format, int width,  
                int height) {  
            // TODO Auto-generated method stub  
              
        }  
        
        public void surfaceCreated(SurfaceHolder holder) {     //��ʼ�� 
        	width = this.getWidth();
        	height = this.getHeight();
        	backgroundMatrix.setScale((float)(width) / backgroundImage.getWidth(), (float)(height) / backgroundImage.getHeight());
            thread.start();  
        }  
        
        
        public void surfaceDestroyed(SurfaceHolder holder) {  
            // TODO Auto-generated method stub  
        	endFlag = false;    //run()������־
        }  
        
        //������Ӧ
        public boolean onTouchEvent(MotionEvent e) {
        	switch(e.getAction()){
        	case MotionEvent.ACTION_DOWN:
        		//touchFlag = false;
        		//firstX = e.getX();   //��һ��
        		downX = Constant.convertX(e.getX());
        		downY = Constant.convertY(e.getY());
        		break;
        		
        	case MotionEvent.ACTION_UP:
        		upX = Constant.convertX(e.getX());
        		upY = Constant.convertY(e.getY());
        		//endX = e.getX();   //���һ��
        		//touchFlag = true;
        		//if(touchFlag)
        		//	if(firstX > endX)   //����
        		//		select ++;
        		//	else select --;   //����
        		
        		
        		if(downX>1.0 && downY<0.3 && upX>1.0 && upY<0.3){
        			mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.MAIN_MENU);
        		}
        		
        		if(downX>0.6 && downX<1.0 && downY>-0.2 && downY<0.2){
        			if(upX>0.6 && upX<1.0 && upY>-0.2 && upY<0.2){
        				select++;
        			}
        		}

        		
//        		if(select < 0){
//        			select = 0;
//        		}
        		if(select > 4){
        			select = 4;
        			lastToast.show();
        		}
        		logic();    //����logic�л�����
        		break;
        	}
        	return true;
        	
        }
        
        
        public void run() {  
            while(endFlag){  
                draw();  
                try {  
                    Thread.sleep(1);  
                } catch (InterruptedException e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
                }  
            }  
        }  
        
        private void draw() {  
            try {  
                canvas=surfaceHolder.lockCanvas();//get a canvas example.  
                canvas.drawBitmap(backgroundImage, backgroundMatrix, backgroundPaint);  
            } catch (Exception e) {  
                // TODO: handle exception  
            }finally{  
                if (canvas!=null) {  
                    surfaceHolder.unlockCanvasAndPost(canvas);//submit a gooad canvas painting.  
                }   
            }  
              
        }
        
        private void logic() {
        	switch(select) {   //�ֱ��Ӧ5��ͼƬ
        	case 0:
        		backgroundImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.help1_bg);
        		break;
        		
        	case 1:
        		backgroundImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.help2_bg);
        		break;
        		
        	case 2:
        		backgroundImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.help3_bg);
        		break;
        		
        	case 3:
        		backgroundImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.help4_bg);
        		break;
        		
        	case 4:
        		backgroundImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.help5_bg);
        		break;
        	}
        	
        	backgroundMatrix.setScale((float)(width) / backgroundImage.getWidth(), (float)(height) / backgroundImage.getHeight());  //���þ���
        }
    }  