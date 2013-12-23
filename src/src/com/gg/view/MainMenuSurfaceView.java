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


public class MainMenuSurfaceView extends GLSurfaceView {

	private MainActivity mainActivity; // ʹ�ô˽����Activity
	private SceneRenderer sceneRender; // ������Ⱦ��


	private int onBackgroundTextureId; // ����������Id
	private int offBackgroundTextureId;
	
	
	private DrawTop selectDrawTop;
	private DrawTop startDrawTop;
	private DrawTop scoreDrawTop;
	private DrawTop helpDrawTop;

	private DrawTrack drawTrack; // ��ʾ�����켣�Ķ���

	private DrawBackground drawBackground; // ��ʾ�����Ķ���
	
	private Point downPoint;
	private Point upPoint;
	
	private double downX;
	private double downY;
	private double upX;
	private double upY;


	private boolean crackFlag;
	private long crackTime;
	
	
	public MainMenuSurfaceView(Context context) {
		super(context);
		mainActivity = (MainActivity) context; // ���ʹ�ô˽����Activity
		sceneRender = new SceneRenderer(); // ����������Ⱦ��
		setRenderer(sceneRender); // ������Ⱦ��
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);// ������ȾģʽΪ������Ⱦ
		
		downPoint = new Point(0,0,0);
		upPoint = new Point(0,0,0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) { // Ӧ���Ƕ�㴥����bug
		switch(e.getAction()){
		case MotionEvent.ACTION_DOWN:
			if(e.getX()<1.5/8.8*Constant.SCREEN_WIDTH && e.getY()<1.0/5*Constant.SCREEN_HEIGHT){
				crackFlag = true;
				crackTime = System.currentTimeMillis();
			}
					
			downX = Constant.convertX(e.getX());
			downY = Constant.convertY(e.getY());
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			if(e.getX()<1.5/8.8*Constant.SCREEN_WIDTH && e.getY()<1.0/5*Constant.SCREEN_HEIGHT){
				if(crackFlag==true && System.currentTimeMillis()-crackTime>=3000){
					SharedPreferences settings = mainActivity.getSettings();
					SharedPreferences.Editor editor = settings.edit();
					if(settings.getBoolean("crack", false)==false){
						Toast.makeText(mainActivity, "^_^ �������ع��� ^_^", Toast.LENGTH_SHORT).show();
						editor.putBoolean("crack", true);
					}else{
						Toast.makeText(mainActivity, "^o^ �ر����ع��� ^o^", Toast.LENGTH_SHORT).show();
						editor.putBoolean("crack", false);
					}
				    editor.commit();
				}
			}
			crackFlag = false;
			
			upX = Constant.convertX(e.getX());
			upY = Constant.convertY(e.getY());
			
			if(downX>-1.4 && downX<-0.9 && downY<-0.25){
				if(upX>-1.4 && upX<-0.9 && upY<-0.25){
					mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.HELP);
				}
			}else if(downX>-0.9 && downX<-0.1 && downY<0.2){
				if(upX>-0.9 && upX<-0.1 && upY<0.2){
					mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.SCORE);
				}
			}else if(downX>-0.1 && downX<0.8 && downY<0.5){
				if(upX>-0.1 && upX<0.8 && upY<0.5){
					mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.GAME_MODE);
				}
			}else if(downX>0.8 && downX<1.5 && downY<0.2){
				if(upX>0.8 && upX<1.5 && upY<0.2){
					mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.SELECT);
				}
			}
			
			if(downX<-1.4 && downY<-0.7){
				if(upX<-1.4 && upY<-0.7){
					mainActivity.getHandler().sendEmptyMessage(MainActivity.VOICE_MESSAGE);
				}
			}else if(downX>1.3 && downY>0.6){
				if(upX>1.3 && upY>0.6){
					
					mainActivity.getSoundControl().setMusic();
					mainActivity.getSoundControl().choose();
					
					if(mainActivity.getSoundControl().isSoundOn()){
						drawBackground.setDrawableId(onBackgroundTextureId);
					}else{
						drawBackground.setDrawableId(offBackgroundTextureId);
					}

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

		private int selectConeTextureId; // Բ׶������Id
		private int selectCylinderTextureId; // Բ��������Id
		private int selectCircleTextureId; // Բ������Id
		private int startConeTextureId;
		private int startCylinderTextureId;
		private int startCircleTextureId;
		private int scoreConeTextureId;
		private int scoreCylinderTextureId;
		private int scoreCircleTextureId;
		private int helpConeTextureId;
		private int helpCylinderTextureId;
		private int helpCircleTextureId;
		
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

			selectConeTextureId = initTexture(gl, R.drawable.select_cone); // ��ʼ��Բ׶������
			selectCylinderTextureId = initTexture(gl, R.drawable.select_cylinder); // ��ʼ��Բ��������
			selectCircleTextureId = initTexture(gl, R.drawable.select_circle); // ��ʼ��Բ������
			startConeTextureId = initTexture(gl, R.drawable.start_cone);
			startCylinderTextureId = initTexture(gl, R.drawable.start_cylinder);
			startCircleTextureId = initTexture(gl, R.drawable.start_circle);
			scoreConeTextureId = initTexture(gl, R.drawable.score_cone); 
			scoreCylinderTextureId = initTexture(gl, R.drawable.score_cylinder);
			scoreCircleTextureId = initTexture(gl, R.drawable.score_circle);
			helpConeTextureId = initTexture(gl, R.drawable.help_cone);
			helpCylinderTextureId = initTexture(gl, R.drawable.help_cylinder);
			helpCircleTextureId = initTexture(gl, R.drawable.help_circle);

			onBackgroundTextureId = initTexture(gl, R.drawable.main_menu_on_bg); // ��ʼ������������
			offBackgroundTextureId = initTexture(gl, R.drawable.main_menu_off_bg);
			
			trackTextureId = initTexture(gl, R.drawable.track);
	
			
			helpDrawTop = new DrawTop(helpConeTextureId,helpCylinderTextureId,helpCircleTextureId);
			helpDrawTop.setRadius(0.2f);
			helpDrawTop.setBasicPoint(new Point(-1.15f, -2.1f));
			helpDrawTop.setAngleSpeed(3f);
			helpDrawTop.generateData();
			
			
			
			scoreDrawTop = new DrawTop(scoreConeTextureId,scoreCylinderTextureId,scoreCircleTextureId);
			scoreDrawTop.setRadius(0.3f);
			scoreDrawTop.setBasicPoint(new Point(-0.5f, -2.3f));
			scoreDrawTop.setAngleSpeed(4);
			scoreDrawTop.generateData();
	
			

			
			startDrawTop = new DrawTop(startConeTextureId,startCylinderTextureId,startCircleTextureId);
			startDrawTop.setRadius(0.4f);
			startDrawTop.setBasicPoint(new Point(0.35f, -2.4f));
			startDrawTop.setAngleSpeed(5);
			startDrawTop.generateData();
			
			
			
			selectDrawTop = new DrawTop(selectConeTextureId,selectCylinderTextureId,selectCircleTextureId);
			selectDrawTop.setRadius(0.3f);
			selectDrawTop.setBasicPoint(new Point(1.2f, -2.2f));
			selectDrawTop.setAngleSpeed(4);
			selectDrawTop.generateData();

			
			
			drawTrack = new DrawTrack(trackTextureId); // �������켣�Ķ���

			
			drawBackground = new DrawBackground(onBackgroundTextureId); // �����������Ķ���
			
			if(mainActivity.getSoundControl().isSoundOn()){
				drawBackground.setDrawableId(onBackgroundTextureId);
			}else{
				drawBackground.setDrawableId(offBackgroundTextureId);
			}
			

			gl.glDisable(GL10.GL_DITHER); // �رտ�����
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST); // �����ض�Hint��Ŀ��ģʽ������Ϊ����Ϊʹ�ÿ���ģʽ
			gl.glClearColor(0, 0, 0, 0); // ������Ļ����ɫ��ɫRGBA
			gl.glShadeModel(GL10.GL_SMOOTH); // ������ɫģ��Ϊƽ����ɫ
			gl.glEnable(GL10.GL_DEPTH_TEST); // ������Ȳ���


			new Thread() // ת�������
			{
				public void run() {
					while (true) {
						selectDrawTop.rotate();
						startDrawTop.rotate();
						scoreDrawTop.rotate();
						helpDrawTop.rotate();
						
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

//			float lx = 0; // �趨��Դ��λ��
//			float ly = (float) (7 * Math.cos(Math.toRadians(lightAngle)));
//			float lz = (float) (7 * Math.sin(Math.toRadians(lightAngle)));
//			float[] positionParamsRed = { lx, ly, lz, 0 };
//			gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, positionParamsRed, 0);
//
//			initMaterial(gl);// ��ʼ������
//
//			initLight(gl);// ����

			gl.glTranslatef(0f, 0f, -100.0f); // ��������ͼ����100�����ֵûʲô���壩����֤���Կ������ݶ���
			

			
			gl.glPushMatrix();
			gl.glRotatef(-70, 1, 0, 0);
			gl.glRotatef(-10, 0, 1, 0);
			helpDrawTop.drawSelf(gl);
			gl.glPopMatrix();
			
			
			gl.glPushMatrix();
			gl.glRotatef(-70, 1, 0, 0);
			gl.glRotatef(-5, 0, 1, 0);
			scoreDrawTop.drawSelf(gl);
			gl.glPopMatrix();
			
			
			gl.glPushMatrix();
			gl.glRotatef(-70, 1, 0, 0);
			startDrawTop.drawSelf(gl);
			gl.glPopMatrix();
			
			
			gl.glPushMatrix();
			gl.glRotatef(-70, 1, 0, 0);
			gl.glRotatef(5, 0, 1, 0);
			selectDrawTop.drawSelf(gl);
			gl.glPopMatrix();



			
			

			gl.glPushMatrix();
			gl.glTranslatef(0, 0, 1);
			drawTrack.drawSelf(gl);
			gl.glPopMatrix();

			gl.glPushMatrix(); // ������ǰ����
//			if(showFlag){
//				gl.glRotatef(showAngle, 0, 0, 1);		
//			}
			drawBackground.drawSelf(gl); // ������
			gl.glPopMatrix(); // �ظ�֮ǰ�任����

//			closeLight(gl);// �ص�

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
