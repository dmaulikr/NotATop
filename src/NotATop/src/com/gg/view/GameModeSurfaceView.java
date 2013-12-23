package com.gg.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.widget.Toast;

import com.gg.game.ClassicGame;
import com.gg.game.GameFrame;
import com.gg.module.DrawBackground;
import com.gg.module.DrawTrack;
import com.gg.top.*;
import com.gg.util.*;


public class GameModeSurfaceView extends GLSurfaceView {

	private MainActivity mainActivity; // ʹ�ô˽����Activity
	private SceneRenderer sceneRender; // ������Ⱦ��

	private DrawTop freeDrawTop;
	private DrawTop oneDrawTop;
	private DrawTop twoDrawTop;
	private DrawTop threeDrawTop;
	private DrawTop fourDrawTop;
	private DrawTop fiveDrawTop;
	private DrawTop sixDrawTop;
	private DrawTop sevenDrawTop;
	private DrawTop eightDrawTop;
	
	private int level;


	private DrawTrack drawTrack; // ��ʾ�����켣�Ķ���

	private DrawBackground drawBackground; // ��ʾ�����Ķ���
	
	private Point downPoint;
	private Point upPoint;
	
	private double downX;
	private double downY;
	private double upX;
	private double upY;

	
	public GameModeSurfaceView(Context context) {
		super(context);
		mainActivity = (MainActivity) context; // ���ʹ�ô˽����Activity
		sceneRender = new SceneRenderer(); // ����������Ⱦ��
		setRenderer(sceneRender); // ������Ⱦ��
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);// ������ȾģʽΪ������Ⱦ
		
		downPoint = new Point(0,0,0);
		upPoint = new Point(0,0,0);
		
		
		SharedPreferences settings = mainActivity.getSettings();
		//voiceControlFlag = settings.getBoolean("voiceControlFlag", true);
		level = settings.getInt("level", 1);
//	    SharedPreferences.Editor editor = settings.edit();
//	    editor.putBoolean("voiceControlFlag", false);
//	    editor.commit();
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		switch(e.getAction()){
		case MotionEvent.ACTION_DOWN:
			downX = Constant.convertX(e.getX());
			downY = Constant.convertY(e.getY());
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:			
			upX = Constant.convertX(e.getX());
			upY = Constant.convertY(e.getY());
			
			if(downX>-1.6 && downX<-0.45 && downY>-0.8 && downY<0.8f){ // ��������ģʽ
				if(upX>-1.6 && upX<-0.45 && upY>-0.8 && upY<0.8f){
					mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.CLASSIC_GAME);
				}
			}
			
			if(downX>-0.35 && downY<0.35 && upX>-0.35 && upY<0.35){
				int currentLevel = 10;
				if(downX>-0.35 && downX<0.15 && downY>-0.2 && downY<0.35){
					if(upX>-0.35 && upX<0.15 && upY>-0.2 && upY<0.35){
						currentLevel = 1;
					}
				}else if(downX>0.15 && downX<0.65 && downY>-0.2 && downY<0.35){
					if(upX>0.15 && upX<0.65 && upY>-0.2 && upY<0.35){
						currentLevel = 2;
					}
				}else if(downX>0.65 && downX<1.15 && downY>-0.2 && downY<0.35){
					if(upX>0.65 && upX<1.15 && upY>-0.2 && upY<0.35){
						currentLevel = 3;
					}
				}else if(downX>1.15 && downX<1.7 && downY>-0.2 && downY<0.35){
					if(upX>1.15 && upX<1.7 && upY>-0.2 && upY<0.35){
						currentLevel = 4;
					}
				}else if(downX>-0.35 && downX<0.15 && downY>-1.0 && downY<-0.2){
					if(upX>-0.35 && upX<0.15 && upY>-1.0 && upY<-0.2){
						currentLevel = 5;
					}
				}else if(downX>0.15 && downX<0.65 && downY>-1.0 && downY<-0.2){
					if(upX>0.15 && upX<0.65 && upY>-1.0 && upY<-0.2){
						currentLevel = 6;
					}
				}else if(downX>0.65 && downX<1.15 && downY>-1.0 && downY<-0.2){
					if(upX>0.65 && upX<1.15 && upY>-1.0 && upY<-0.2){
						currentLevel = 7;
					}
				}else if(downX>1.15 && downX<1.7 && downY>-1.0 && downY<-0.2){
					if(upX>1.15 && upX<1.7 && upY>-1.0 && upY<-0.2){
						currentLevel = 8;
					}
				}

		
				SharedPreferences settings = mainActivity.getSettings();				
				if(settings.getInt("level", 1)>=currentLevel){
				    SharedPreferences.Editor editor = settings.edit();
				    editor.putInt("currentLevel", currentLevel);
				    editor.commit();	
				    
				    mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.TIME_GAME);
				}
				

			}
			break;
		}
		
		drawTrack.onTouchEvent(e); // ��Ӧ������Ϣ�����켣

		requestRender(); // ǿ���ػ滭��

		return true;
	}


	private class SceneRenderer implements GLSurfaceView.Renderer { // ��ʾ��ǰ3D�������Ⱦ��

		private int lightAngle = 90;// �Ƶĵ�ǰ�Ƕ�

		private int freeConeTextureId; // Բ׶������Id
		private int freeCylinderTextureId; // Բ��������Id
		private int freeCircleTextureId; // Բ������Id

		private int oneConeTextureId;
		private int oneCylinderTextureId;
		private int oneCircleTextureId;
		private int twoConeTextureId;
		private int twoCylinderTextureId;
		private int twoCircleTextureId;
		private int threeConeTextureId;
		private int threeCylinderTextureId;
		private int threeCircleTextureId;
		private int fourConeTextureId;
		private int fourCylinderTextureId;
		private int fourCircleTextureId;
		private int fiveConeTextureId;
		private int fiveCylinderTextureId;
		private int fiveCircleTextureId;
		private int sixConeTextureId;
		private int sixCylinderTextureId;
		private int sixCircleTextureId;
		private int sevenConeTextureId;
		private int sevenCylinderTextureId;
		private int sevenCircleTextureId;
		private int eightConeTextureId;
		private int eightCylinderTextureId;
		private int eightCircleTextureId;
		
		
		private int backgroundTextureId; // ����������Id
		
		private int trackTextureId;


		public SceneRenderer() { // ��Ⱦ���Ĺ��캯��

		}

		public void onSurfaceCreated(final GL10 gl, EGLConfig config) { // ��������ʱ���ã����ڳ�ʼ��������Դ

			if(mainActivity.isFirstTimeFlag()){
				mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.FIRST_TIME);
				mainActivity.setFirstTimeFlag(false);
			}
			
			
			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // �������������ɫ
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT); // �����ɫ����

			freeConeTextureId = initTexture(gl, R.drawable.free_cone); // ��ʼ��Բ׶������
			freeCylinderTextureId = initTexture(gl, R.drawable.free_cylinder); // ��ʼ��Բ��������
			freeCircleTextureId = initTexture(gl, R.drawable.free_circle); // ��ʼ��Բ������

			oneConeTextureId = initTexture(gl, R.drawable.one_cone);
			oneCylinderTextureId = initTexture(gl, R.drawable.one_cylinder);
			oneCircleTextureId = initTexture(gl, R.drawable.one_circle);
			twoConeTextureId = initTexture(gl, R.drawable.two_cone);
			twoCylinderTextureId = initTexture(gl, R.drawable.two_cylinder);
			twoCircleTextureId = initTexture(gl, R.drawable.two_circle);
			threeConeTextureId = initTexture(gl, R.drawable.three_cone);
			threeCylinderTextureId = initTexture(gl, R.drawable.three_cylinder);
			threeCircleTextureId = initTexture(gl, R.drawable.three_circle);
			fourConeTextureId = initTexture(gl, R.drawable.four_cone);
			fourCylinderTextureId = initTexture(gl, R.drawable.four_cylinder);
			fourCircleTextureId = initTexture(gl, R.drawable.four_circle);
			fiveConeTextureId = initTexture(gl, R.drawable.five_cone);
			fiveCylinderTextureId = initTexture(gl, R.drawable.five_cylinder);
			fiveCircleTextureId = initTexture(gl, R.drawable.five_circle);
			sixConeTextureId = initTexture(gl, R.drawable.six_cone);
			sixCylinderTextureId = initTexture(gl, R.drawable.six_cylinder);
			sixCircleTextureId = initTexture(gl, R.drawable.six_circle);
			sevenConeTextureId = initTexture(gl, R.drawable.seven_cone);
			sevenCylinderTextureId = initTexture(gl, R.drawable.seven_cylinder);
			sevenCircleTextureId = initTexture(gl, R.drawable.seven_circle);
			eightConeTextureId = initTexture(gl, R.drawable.eight_cone);
			eightCylinderTextureId = initTexture(gl, R.drawable.eight_cylinder);
			eightCircleTextureId = initTexture(gl, R.drawable.eight_circle);
			

			backgroundTextureId = initTexture(gl, R.drawable.game_mode_bg); // ��ʼ������������
			
			trackTextureId = initTexture(gl, R.drawable.track);
			
			
			freeDrawTop = new DrawTop(freeConeTextureId,freeCylinderTextureId,freeCircleTextureId);
			freeDrawTop.setRadius(0.45f);
			freeDrawTop.setBasicPoint(new Point(-1.05f, -2.1f));
			freeDrawTop.setAngleSpeed(5);
			freeDrawTop.generateData();
			
			oneDrawTop = new DrawTop(oneConeTextureId,oneCylinderTextureId,oneCircleTextureId);
			oneDrawTop.setRadius(0.2f);
			oneDrawTop.setBasicPoint(new Point(-0.1f, -0.9f));
			oneDrawTop.setAngleSpeed(3);
			oneDrawTop.generateData();
			
			twoDrawTop = new DrawTop(twoConeTextureId,twoCylinderTextureId,twoCircleTextureId);
			twoDrawTop.setRadius(0.2f);
			twoDrawTop.setBasicPoint(new Point(0.4f, -0.9f));
			twoDrawTop.setAngleSpeed(3);
			twoDrawTop.generateData();
			
			threeDrawTop = new DrawTop(threeConeTextureId,threeCylinderTextureId,threeCircleTextureId);
			threeDrawTop.setRadius(0.2f);
			threeDrawTop.setBasicPoint(new Point(0.9f, -0.9f));
			threeDrawTop.setAngleSpeed(3);
			threeDrawTop.generateData();
			
			fourDrawTop = new DrawTop(fourConeTextureId,fourCylinderTextureId,fourCircleTextureId);
			fourDrawTop.setRadius(0.2f);
			fourDrawTop.setBasicPoint(new Point(1.4f, -0.9f));
			fourDrawTop.setAngleSpeed(3);
			fourDrawTop.generateData();
			
			fiveDrawTop = new DrawTop(fiveConeTextureId,fiveCylinderTextureId,fiveCircleTextureId);
			fiveDrawTop.setRadius(0.2f);
			fiveDrawTop.setBasicPoint(new Point(-0.1f, -2.6f));
			fiveDrawTop.setAngleSpeed(3);
			fiveDrawTop.generateData();
			
			sixDrawTop = new DrawTop(sixConeTextureId,sixCylinderTextureId,sixCircleTextureId);
			sixDrawTop.setRadius(0.2f);
			sixDrawTop.setBasicPoint(new Point(0.4f, -2.6f));
			sixDrawTop.setAngleSpeed(3);
			sixDrawTop.generateData();
			
			sevenDrawTop = new DrawTop(sevenConeTextureId,sevenCylinderTextureId,sevenCircleTextureId);
			sevenDrawTop.setRadius(0.2f);
			sevenDrawTop.setBasicPoint(new Point(0.9f, -2.6f));
			sevenDrawTop.setAngleSpeed(3);
			sevenDrawTop.generateData();
			
			eightDrawTop = new DrawTop(eightConeTextureId,eightCylinderTextureId,eightCircleTextureId);
			eightDrawTop.setRadius(0.2f);
			eightDrawTop.setBasicPoint(new Point(1.4f, -2.6f));
			eightDrawTop.setAngleSpeed(3);
			eightDrawTop.generateData();
			

			

			drawTrack = new DrawTrack(trackTextureId); // �������켣�Ķ���

			drawBackground = new DrawBackground(backgroundTextureId); // �����������Ķ���
			

			gl.glDisable(GL10.GL_DITHER); // �رտ�����
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST); // �����ض�Hint��Ŀ��ģʽ������Ϊ����Ϊʹ�ÿ���ģʽ
			gl.glClearColor(0, 0, 0, 0); // ������Ļ����ɫ��ɫRGBA
			gl.glShadeModel(GL10.GL_SMOOTH); // ������ɫģ��Ϊƽ����ɫ
			gl.glEnable(GL10.GL_DEPTH_TEST); // ������Ȳ���


			new Thread() // ת�������
			{
				public void run() {
					while (true) {
						freeDrawTop.rotate();
//						oneDrawTop.rotate();
//						twoDrawTop.rotate();
//						threeDrawTop.rotate();
//						fourDrawTop.rotate();
//						fiveDrawTop.rotate();
//						sixDrawTop.rotate();
//						sevenDrawTop.rotate();
//						eightDrawTop.rotate();

						switch(level){
						case 10:
						case 9:
						case 8:
							eightDrawTop.rotate();
						case 7:
							sevenDrawTop.rotate();
						case 6:
							sixDrawTop.rotate();
						case 5:
							fiveDrawTop.rotate();
						case 4:
							fourDrawTop.rotate();
						case 3:
							threeDrawTop.rotate();
						case 2:
							twoDrawTop.rotate();
						case 1:
							oneDrawTop.rotate();
							break;
							
						}
						
						try{
							sleep(Constant.INTERVAL);
						}catch(Exception e){
							e.printStackTrace();
						}
					}

				}
			}.start();
			
			
		}

		public void onDrawFrame(GL10 gl) { // ����Ļ�ͼ���������ܿ���ˢ��Ƶ��
			// gl.glClearColor(0.5f, 0.5f, 0.5f, 0.0f); // ʹ���ض��������ɫ
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT); // �����ɫ����

			gl.glMatrixMode(GL10.GL_MODELVIEW); // ���õ�ǰ����Ϊģʽ����
			gl.glLoadIdentity(); // ���õ�ǰ����Ϊ��λ����
			gl.glPushMatrix();// �����任�����ֳ�

			float lx = 0; // �趨��Դ��λ��
			float ly = (float) (7 * Math.cos(Math.toRadians(lightAngle)));
			float lz = (float) (7 * Math.sin(Math.toRadians(lightAngle)));
			float[] positionParamsRed = { lx, ly, lz, 0 };
			gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, positionParamsRed, 0);

			initMaterial(gl);// ��ʼ������

			initLight(gl);// ����

			gl.glTranslatef(0f, 0f, -100.0f); // ��������ͼ����100�����ֵûʲô���壩����֤���Կ������ݶ���
			

			gl.glPushMatrix();
			gl.glRotatef(-70, 1, 0, 0);
			freeDrawTop.drawSelf(gl);
			oneDrawTop.drawSelf(gl);
			twoDrawTop.drawSelf(gl);
			threeDrawTop.drawSelf(gl);
			fourDrawTop.drawSelf(gl);
			fiveDrawTop.drawSelf(gl);
			sixDrawTop.drawSelf(gl);
			sevenDrawTop.drawSelf(gl);
			eightDrawTop.drawSelf(gl);
			gl.glPopMatrix();
			
			
			

			gl.glPushMatrix();
			drawTrack.drawSelf(gl);
			gl.glPopMatrix();

			gl.glPushMatrix(); // ������ǰ����
//			if(showFlag){
//				gl.glRotatef(showAngle, 0, 0, 1);		
//			}
			drawBackground.drawSelf(gl); // ������
			gl.glPopMatrix(); // �ظ�֮ǰ�任����

			closeLight(gl);// �ص�

			gl.glPopMatrix();// �ָ��任�����ֳ�
		}

		public void onSurfaceChanged(GL10 gl, int width, int height) { // ���ڷ����ı�ʱ���ã�һ��ֻ���ܻ����һ��
			gl.glViewport(0, 0, width, height); // �����Ӵ���С��λ��
			gl.glMatrixMode(GL10.GL_PROJECTION); // ���õ�ǰ����ΪͶӰ����
			gl.glLoadIdentity(); // ���õ�ǰ����Ϊ��λ����
			float ratio = (float) width / height; // ����͸��ͶӰ�ı���
			// gl.glFrustumf(-ratio, ratio, -1, 1, 1, 100); // ���ô˷����������͸��ͶӰ����
			gl.glOrthof(-ratio, ratio, -1, 1, 1, 100); // ʹ��ƽ��ͶӰ�����˿��ŵ�3DЧ��������100Ҳ��ûʲô�����

			Constant.SCREEN_WIDTH = width; // ��Ļ�ı�ʱ����Constant�ı������˺���һ��ֻ�����һ�Σ��������Ӻ����
			Constant.SCREEN_HEIGHT = height;
			Constant.SCREEN_RATE = (float) (0.5 * 4 / Constant.SCREEN_HEIGHT);

		}
		
		

		

	}

	private void initLight(GL10 gl) { // ��ʼ����ɫ��
		gl.glEnable(GL10.GL_LIGHTING);// �������
		gl.glEnable(GL10.GL_LIGHT1);// ��1�ŵ�

		// ����������
		float[] ambientParams = { 0.2f, 0.2f, 0.2f, 1.0f };// ����� RGBA
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, ambientParams, 0);

		// ɢ�������
		float[] diffuseParams = { 1f, 1f, 1f, 1.0f };// ����� RGBA
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, diffuseParams, 0);

		// ���������
		float[] specularParams = { 1f, 1f, 1f, 1.0f };// ����� RGBA
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPECULAR, specularParams, 0);
	}

	private void closeLight(GL10 gl) {// �رյ�
		gl.glDisable(GL10.GL_LIGHT1);
		gl.glDisable(GL10.GL_LIGHTING);
	}

	private void initMaterial(GL10 gl) { // ��ʼ������
		// ������
		float ambientMaterial[] = { 248f / 255f, 242f / 255f, 144f / 255f, 1.0f };
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,
				ambientMaterial, 0);
		// ɢ���
		float diffuseMaterial[] = { 248f / 255f, 242f / 255f, 144f / 255f, 1.0f };
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
				diffuseMaterial, 0);
		// �߹����
		float specularMaterial[] = { 248f / 255f, 242f / 255f, 144f / 255f,
				1.0f };
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR,
				specularMaterial, 0);
		gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 100.0f);
	}

	public int initTexture(GL10 gl, int drawableId) // ��ʼ������
	{
		// ��������ID
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);
		int currTextureId = textures[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, currTextureId);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR_MIPMAP_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR_MIPMAP_LINEAR);
		((GL11) gl).glTexParameterf(GL10.GL_TEXTURE_2D,
				GL11.GL_GENERATE_MIPMAP, GL10.GL_TRUE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_REPEAT);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_REPEAT);

		InputStream is = this.getResources().openRawResource(drawableId);
		Bitmap bitmapTmp;
		try {
			bitmapTmp = BitmapFactory.decodeStream(is);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmapTmp, 0);
		bitmapTmp.recycle();

		return currTextureId;
	}
	

}
