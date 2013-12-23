package com.gg.view;

import com.gg.util.Constant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class ScoreSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
	MainActivity mainActivity;//mainActivity������
	Paint paint;//��������
	DrawThread drawThread;//�����߳�����		
	Bitmap bgBitmap;//����ͼƬ	
	Bitmap titleBitmap;//���ֵ�ͼƬ
	Bitmap scoreBitmap;
	Bitmap dataBitmap;
	Bitmap[] numberBitmaps;//����ͼƬ	
	Bitmap dashBitmap;//����"-"��Ӧ��ͼƬ
	int bmpx;//����λ��	
	String queryResultStr;//��ѯ���ݿ�Ľ��
	String[] splitResultStrs;//����ѯ����зֺ������
	private int numberWidth;//����ͼƬ�Ŀ��
	private int posFrom=-1;//��ѯ�Ŀ�ʼλ��
	private int length=6;//��ѯ������¼����
	int downY=0;//���º�̧���y����
	int upY=0;
	double downX;
	double upX;
	
	public ScoreSurfaceView(MainActivity mainActivity) {
		super(mainActivity);
		this.mainActivity=mainActivity;
		//��ý��㲢����Ϊ�ɴ���
		this.requestFocus();
        this.setFocusableInTouchMode(true);
		getHolder().addCallback(this);//ע��ص��ӿ�	
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//���Ʊ���
		canvas.drawColor(Color.WHITE);
		
		//canvas.drawBitmap(bgBitmap, 0, 0, paint);
		canvas.drawBitmap(bgBitmap, new Rect(0, 0,
				(int) bgBitmap.getWidth(), (int) bgBitmap
						.getHeight()), new Rect(0, 0,
				(int) Constant.SCREEN_WIDTH, (int) Constant.SCREEN_HEIGHT), paint);
		
		//��������
		//canvas.drawBitmap(titleBitmap, bmpx, Constant.SCREEN_HEIGHT/20, paint);
		canvas.drawBitmap(scoreBitmap, Constant.SCREEN_WIDTH*2/3-10, titleBitmap.getHeight()+Constant.SCREEN_HEIGHT/10, paint);
		canvas.drawBitmap(dataBitmap, Constant.SCREEN_WIDTH*1/3, titleBitmap.getHeight()+Constant.SCREEN_HEIGHT/10, paint);
		//���Ƶ÷ֺ�ʱ��
		int x,y;
		for(int i=0;i<splitResultStrs.length;i++)
		{
			if(i%2==0)//����÷ֵ�λ��
			{
				x=Constant.SCREEN_WIDTH*3/4;				
			}
			else//����ʱ���λ��
			{
				x=Constant.SCREEN_WIDTH/2;
			}
			y=Constant.SCREEN_HEIGHT/20+titleBitmap.getHeight()+scoreBitmap.getHeight()+(numberBitmaps[0].getHeight()+Constant.SCREEN_HEIGHT/45)*(i/2+1);
			//�����ַ���
			drawDateBitmap(splitResultStrs[i],x,y,canvas,paint);
		}
	}
	//������ͼƬ�ķ���
	public void drawDateBitmap(String numberStr,float endX,float endY,Canvas canvas,Paint paint)
	{
		for(int i=0;i<numberStr.length();i++)
		{
			char c=numberStr.charAt(i);
			if(c=='-')
			{
				canvas.drawBitmap(dashBitmap,endX-numberWidth*(numberStr.length()-i), endY, paint);
			}
			else
			{
				canvas.drawBitmap
				(
						numberBitmaps[c-'0'], 
						endX-numberWidth*(numberStr.length()-i), 
						endY, 
						paint
				);
			}			
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int y = (int) event.getY();		
    	switch(event.getAction())
    	{
    	case MotionEvent.ACTION_DOWN:
    		downY=y;
    		downX = event.getX();
    		break;
    	case MotionEvent.ACTION_UP:
    		upY=y;    	
    		upX = event.getX();
    		
    		if(downX>Constant.SCREEN_WIDTH*7.5/9 && downY>Constant.SCREEN_HEIGHT*4/5){
    			if(upX>Constant.SCREEN_WIDTH*7.5/9 && upY>Constant.SCREEN_HEIGHT*4/5){
    				mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.MAIN_MENU);
    			}
    		}
    		
        	if(Math.abs(downY-upY)<20)//����ֵ��Χ�ڣ�����ҳ
        	{
        		return true;
        	}
        	else if(downY<upY)//����Ĩ
        	{	
        		//���Ĩ����ǰҳ��������Ĩ
        		if(this.posFrom-this.length>=-1)
        		{
        			this.posFrom-=this.length;        			
        		}
        	}
        	else//����Ĩ
        	{	
        		//���Ĩ�����ҳ��������Ĩ
        		if(this.posFrom+this.length<mainActivity.getRowCount()-1)
        		{
        			this.posFrom+=this.length;        			
        		}
        	}
        	queryResultStr=mainActivity.query(posFrom,length);//�õ����ݿ��е�����
			splitResultStrs=queryResultStr.split("/", 0);//��"/"�з֣�������մ�
    		break;    	
    	}
		return true;
	}	
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {		
	}

	public void surfaceCreated(SurfaceHolder holder){
		paint=new Paint();//��������
		paint.setAntiAlias(true);//�򿪿����	
		createAllThreads();//���������߳�
		initBitmap();//��ʼ��λͼ��Դ	
		numberWidth=numberBitmaps[0].getWidth()+3;//�õ�����ͼƬ�Ŀ��
		//��ʼ��ͼƬ��λ��
		bmpx=(Constant.SCREEN_WIDTH-titleBitmap.getWidth())/2;
		posFrom=-1;//��ѯ�Ŀ�ʼλ����-1			
		queryResultStr=mainActivity.query(posFrom,length);//�õ����ݿ��е�����
		splitResultStrs=queryResultStr.split("/", 0);//��"/"�з֣�������մ�		
		startAllThreads();//���������߳�
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) {
		  boolean retry = true;
	        stopAllThreads();
	        while (retry) {
	            try {
	            	drawThread.join();
	                retry = false;
	            } 
	            catch (InterruptedException e) {e.printStackTrace();}//���ϵ�ѭ����ֱ��ˢ֡�߳̽���
	        }
	}
	//��ͼƬ����
	public void initBitmap(){
		titleBitmap=BitmapFactory.decodeResource(this.getResources(), R.drawable.high_score);	
		bgBitmap=BitmapFactory.decodeResource(this.getResources(), R.drawable.score_bg);
		numberBitmaps=new Bitmap[]{
				BitmapFactory.decodeResource(this.getResources(), R.drawable.n0),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.n1),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.n2),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.n3),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.n4),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.n5),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.n6),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.n7),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.n8),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.n9),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.n0),
		};
		dashBitmap=BitmapFactory.decodeResource(this.getResources(), R.drawable.gang);
		scoreBitmap=BitmapFactory.decodeResource(this.getResources(), R.drawable.score);
		dataBitmap=BitmapFactory.decodeResource(this.getResources(), R.drawable.data);
	}
	void createAllThreads()
	{
		drawThread=new DrawThread(this);//���������߳�		
	}
	void startAllThreads()
	{
		drawThread.setFlag(true);     
		drawThread.start();
	}
	void stopAllThreads()
	{
		drawThread.setFlag(false);       
	}
	private class DrawThread extends Thread{
		private boolean flag = true;	
		private int sleepSpan = 100;
		ScoreSurfaceView fatherView;
		SurfaceHolder surfaceHolder;
		public DrawThread(ScoreSurfaceView fatherView){
			this.fatherView = fatherView;
			this.surfaceHolder = fatherView.getHolder();
		}
		public void run(){
			Canvas c;
	        while (this.flag) {
	            c = null;
	            try {
	            	// �����������������ڴ�Ҫ��Ƚϸߵ�����£����������ҪΪnull
	                c = this.surfaceHolder.lockCanvas(null);
	                synchronized (this.surfaceHolder) {
	                	fatherView.onDraw(c);//����
	                }
	            } finally {
	                if (c != null) {
	                	//���ͷ���
	                    this.surfaceHolder.unlockCanvasAndPost(c);
	                }
	            }
	            try{
	            	Thread.sleep(sleepSpan);//˯��ָ��������
	            }
	            catch(Exception e){
	            	e.printStackTrace();//��ӡ��ջ��Ϣ
	            }
	        }
		}
		public void setFlag(boolean flag) {
			this.flag = flag;
		}
	}
}
