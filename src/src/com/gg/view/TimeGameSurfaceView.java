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
import com.gg.game.TimeGame;
import com.gg.module.*;
import com.gg.top.*;
import com.gg.util.*;

/*		������Ϸģʽ��3D�����࣬������Ӧ����Ⱦ����		*/
public class TimeGameSurfaceView extends GLSurfaceView {

	private MainActivity mainActivity; // ʹ�ô˽����Activity

	private SceneRenderer sceneRender; // ������Ⱦ��

	private TimeGame timeGame; // ��ʾ������Ϸ�Ķ���

	private DrawTrack drawTrack; // ��ʾ�����켣�Ķ���

	private DrawBackground drawBackground; // ��ʾ�����Ķ���
	
	private DrawScore drawScore;
	
	private DrawScore drawTime;
	
	private Toast nextLevelToast;
	private Toast notNextLevelToast;
	
	
	

	public TimeGameSurfaceView(Context context) {
		super(context);
		mainActivity = (MainActivity) context; // ���ʹ�ô˽����Activity

		nextLevelToast = Toast.makeText(context, "��ϲ������һ��", Toast.LENGTH_SHORT);
		notNextLevelToast = Toast.makeText(context, "���ź���սʧ��", Toast.LENGTH_SHORT);
		
		sceneRender = new SceneRenderer(); // ����������Ⱦ��
		setRenderer(sceneRender); // ������Ⱦ��
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);// ������ȾģʽΪ������Ⱦ
		
		Toast.makeText(context, "�ؿ�"+mainActivity.getSettings().getInt("currentLevel", 1)+"   ����Ŀ�꣺"+mainActivity.getSettings().getInt("currentLevel", 1)*2000, Toast.LENGTH_LONG).show();
	

	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {

		timeGame.onTouch(e); // ������Ϸ��onTouch��������������Ϣ
		
		//mainActivity.getSoundControl().paisound();


		drawTrack.onTouchEvent(e); // ��Ӧ������Ϣ�����켣

		requestRender(); // ǿ���ػ滭��

		return true;
	}

	private class SceneRenderer implements GLSurfaceView.Renderer { // ��ʾ��ǰ3D�������Ⱦ��

		private int lightAngle = 90;// �Ƶĵ�ǰ�Ƕ�

		private int coneTextureId; // Բ׶������Id
		private int cylinderTextureId; // Բ��������Id
		private int circleTextureId; // Բ������Id
		
		private int pauseTextureId;
		
		private int trackTextureId;

		private int backgroundTextureId; // ����������Id
		
		private int backgroundImages[];
		
		private int scoreTextureId;
		
		private int timeTextureId;
		

		public SceneRenderer() { // ��Ⱦ���Ĺ��캯��

		}

		public void onSurfaceCreated(final GL10 gl, EGLConfig config) { // ��������ʱ���ã����ڳ�ʼ��������Դ

			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // �������������ɫ
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT); // �����ɫ����
			


			coneTextureId = initTexture(gl, SelectControl.getConeTextureId()); // ��ʼ��Բ׶������
			cylinderTextureId = initTexture(gl, SelectControl.getCylinderTextureId()); // ��ʼ��Բ��������
			circleTextureId = initTexture(gl, SelectControl.getCircleTextureId()); // ��ʼ��Բ������
			
			pauseTextureId = initTexture(gl, R.drawable.pause_bg);
			
			
			timeGame = new TimeGame(gl, coneTextureId, cylinderTextureId, // ������Ϸ����
					circleTextureId, pauseTextureId);
			timeGame.setCrackFlag(mainActivity.getSettings().getBoolean("crack", false));
			
			timeGame.setCurrentLevel(mainActivity.getSettings().getInt("currentLevel", 1));
			timeGame.calculateGoal();

			
			backgroundImages = new int[]{ 0, R.drawable.time_game_one_bg, R.drawable.time_game_two_bg, 
					R.drawable.time_game_three_bg, R.drawable.time_game_four_bg, R.drawable.time_game_five_bg,
					R.drawable.time_game_six_bg, R.drawable.time_game_seven_bg, R.drawable.time_game_eight_bg};		
			
			backgroundTextureId = initTexture(gl, backgroundImages[timeGame.getCurrentLevel()]); // ��ʼ������������
			
			trackTextureId = initTexture(gl, R.drawable.track);
			
			scoreTextureId = initTexture(gl, R.drawable.number);
			
			timeTextureId = initTexture(gl, R.drawable.number);
			
			
			

			

			drawTrack = new DrawTrack(trackTextureId); // �������켣�Ķ���

			drawBackground = new DrawBackground(backgroundTextureId); // �����������Ķ���
			
			drawScore = new DrawScore(scoreTextureId, TimeGameSurfaceView.this);
			
			drawTime = new DrawScore(timeTextureId, TimeGameSurfaceView.this);
			

			gl.glDisable(GL10.GL_DITHER); // �رտ�����
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST); // �����ض�Hint��Ŀ��ģʽ������Ϊ����Ϊʹ�ÿ���ģʽ
			gl.glClearColor(0, 0, 0, 0); // ������Ļ����ɫ��ɫRGBA
			gl.glShadeModel(GL10.GL_SMOOTH); // ������ɫģ��Ϊƽ����ɫ
			gl.glEnable(GL10.GL_DEPTH_TEST); // ������Ȳ���
			
		    gl.glEnable(GL10.GL_ALPHA_TEST);  // Enable Alpha Testing (To Make BlackTansparent)  
		    
		    gl.glAlphaFunc(GL10.GL_GREATER,0.1f);  // Set Alpha Testing (To Make Black Transparent)

			
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
			
			
			if(mainActivity.isPressMenu()){
				timeGame.setState(GameFrame.PAUSE);
				mainActivity.setPressMenu(false);
			}
			
			if(timeGame.isCollideFlag()){
				mainActivity.getHandler().sendEmptyMessage(MainActivity.VIBRATE_MESSAGE);
				timeGame.setCollideFlag(false);
			}
			
			if(timeGame.getState()==GameFrame.RUN){
				gl.glTranslatef(0f, 0f, -100.0f); // ��������ͼ����100�����ֵûʲô���壩����֤���Կ������ݶ���
		
				
				gl.glPushMatrix(); // ������ǰ����
	//			if(showFlag){
	//				gl.glRotatef(showAngle, 0, 0, 1);		
	//			}
				drawBackground.drawSelf(gl); // ������
				gl.glPopMatrix(); // �ظ�֮ǰ�任����
				
				
				gl.glPushMatrix(); // ��ͼǰ�������						
				timeGame.draw(gl); // ������Ϸ�Ļ�ͼ����
				gl.glPopMatrix(); // ��ͼ��ָ�����
	
				
				gl.glPushMatrix();
				drawTrack.drawSelf(gl);
				gl.glPopMatrix();			

				
				gl.glPushMatrix();
				gl.glTranslatef(-1.5f, 0.6f, 3);
				drawScore.setScore((int)timeGame.getScore());
				drawScore.drawSelf(gl);
				gl.glPopMatrix();
				
				
				gl.glPushMatrix();
				gl.glTranslatef(1.3f, 0.6f, 3);
				drawTime.setScore(60-(int)timeGame.getDuration());
				drawTime.drawSelf(gl);
				gl.glPopMatrix();


			}else if(timeGame.getState()==GameFrame.END){
				//Toast.makeText(MainActivity.this, "��Ϸ��������л����", Toast.LENGTH_SHORT);

				
				SharedPreferences settings = mainActivity.getSettings();
				SharedPreferences.Editor editor = settings.edit();
				
				if(timeGame.getScore()>timeGame.getCurrentGoal() ){ // �����ǰ�÷ֱ�Ŀ�����Ҫ��
					
				    if(settings.getInt("currentLevel", 1)>=settings.getInt("level", 1) && settings.getInt("level", 1)<8){ // �����ǰlevel�����level���������ֵС��8
					    editor.putInt("level", settings.getInt("currentLevel", 1)+1);
					    editor.commit();							 
				    }
				    
				    nextLevelToast.show();
				    
				}else{
					notNextLevelToast.show();
				}
				

				//mainActivity.updateScore((int)timeGame.getScore());
				mainActivity.setCurrScore((int)timeGame.getScore());

				
				mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.END);

//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.END);
			}else if(timeGame.getState()==GameFrame.PAUSE){
				gl.glTranslatef(0f, 0f, -100.0f); // ��������ͼ����100�����ֵûʲô���壩����֤���Կ������ݶ���		
				gl.glPushMatrix(); // ��ͼǰ�������					
				timeGame.draw(gl); // ������Ϸ�Ļ�ͼ����
				gl.glPopMatrix(); // ��ͼ��ָ�����
			}
			
			
			

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

			timeGame.start();
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
