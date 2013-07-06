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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.widget.Toast;

import com.gg.game.ClassicGame;
import com.gg.game.GameFrame;
import com.gg.module.*;
import com.gg.top.*;
import com.gg.util.*;

/*		������Ϸģʽ��3D�����࣬������Ӧ����Ⱦ����		*/
public class ClassicGameSurfaceView extends GLSurfaceView {

	private MainActivity mainActivity; // ʹ�ô˽����Activity
	private Toast gameOverToast;
	private Toast difficultyToast;
	private SceneRenderer sceneRender; // ������Ⱦ��

	private boolean showFlag = false;
	private float showAngle = 0f;

	private ClassicGame classicGame; // ��ʾ������Ϸ�Ķ���

	private DrawTrack drawTrack; // ��ʾ�����켣�Ķ���

	private DrawBackground drawBackground; // ��ʾ�����Ķ���
	
	private DrawScore drawScore;
	private DrawScore drawTime;
	
//	private long startTime , endTime;
	

	public ClassicGameSurfaceView(Context context) {
		super(context);
		mainActivity = (MainActivity) context; // ���ʹ�ô˽����Activity
		gameOverToast = Toast.makeText(context, "��Ϸ��������л����", Toast.LENGTH_SHORT);
		difficultyToast = Toast.makeText(context, "�� �� �� �� �� �� �� ��", Toast.LENGTH_SHORT);
		
		sceneRender = new SceneRenderer(); // ����������Ⱦ��
		setRenderer(sceneRender); // ������Ⱦ��
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);// ������ȾģʽΪ������Ⱦ
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {

		if(e.getX()>0 && e.getX()<5/Constant.SCREEN_WIDTH && e.getY()>0 && e.getY()<Constant.SCREEN_HEIGHT/4){
			mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.MAIN_MENU);
		}
		
		
		if(showFlag==false){
			classicGame.onTouch(e); // ������Ϸ��onTouch��������������Ϣ		
			//mainActivity.getSoundControl().paisound();
		}

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
		
		private int scoreTextureId;
		private int timeTextureId;
		

		public SceneRenderer() { // ��Ⱦ���Ĺ��캯��

		}

		public void onSurfaceCreated(final GL10 gl, EGLConfig config) { // ��������ʱ���ã����ڳ�ʼ��������Դ

			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // �������������ɫ
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT); // �����ɫ����
			
			
//		    gl.glEnable(GL10.GL_BLEND);  
//		    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);  
//			
//		    gl.glEnable(GL10.GL_ALPHA_TEST);  // Enable Alpha Testing (To Make BlackTansparent)    
//		    gl.glAlphaFunc(GL10.GL_GREATER,0.1f);  // Set Alpha Testing (To Make Black Transparent) 			
			

			coneTextureId = initTexture(gl, SelectControl.getConeTextureId()); // ��ʼ��Բ׶������
			cylinderTextureId = initTexture(gl, SelectControl.getCylinderTextureId()); // ��ʼ��Բ��������
			circleTextureId = initTexture(gl, SelectControl.getCircleTextureId()); // ��ʼ��Բ������
			
			pauseTextureId = initTexture(gl, R.drawable.pause_bg);

			backgroundTextureId = initTexture(gl, R.drawable.classic_game_bg); // ��ʼ������������

			trackTextureId = initTexture(gl, R.drawable.track);
			
			scoreTextureId = initTexture(gl, R.drawable.number);
			timeTextureId = initTexture(gl, R.drawable.number);
			
			
			classicGame = new ClassicGame(gl, coneTextureId, cylinderTextureId, // ������Ϸ����
					circleTextureId, pauseTextureId);
			classicGame.setCrackFlag(mainActivity.getSettings().getBoolean("crack", false));

			drawTrack = new DrawTrack(trackTextureId); // �������켣�Ķ���

			drawBackground = new DrawBackground(backgroundTextureId); // �����������Ķ���
			
			drawScore = new DrawScore(scoreTextureId, ClassicGameSurfaceView.this);
			drawTime = new DrawScore(timeTextureId, ClassicGameSurfaceView.this);
			

			gl.glDisable(GL10.GL_DITHER); // �رտ�����
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST); // �����ض�Hint��Ŀ��ģʽ������Ϊ����Ϊʹ�ÿ���ģʽ
			gl.glClearColor(0, 0, 0, 0); // ������Ļ����ɫ��ɫRGBA
			gl.glShadeModel(GL10.GL_SMOOTH); // ������ɫģ��Ϊƽ����ɫ
			gl.glEnable(GL10.GL_DEPTH_TEST); // ������Ȳ���
			
		    gl.glEnable(GL10.GL_ALPHA_TEST);  // Enable Alpha Testing (To Make BlackTansparent)  
		    
		    gl.glAlphaFunc(GL10.GL_GREATER,0.1f);  // Set Alpha Testing (To Make Black Transparent)

			showFlag = true;
			new Thread() // ת�������
			{
				public void run() {
					while (showFlag) {
						if (showAngle < 365f) {
							showAngle += 2.5f;
							try {
								sleep(30);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							showFlag = false;

							classicGame.getDrawTop().setAngleSpeed(classicGame.getDrawTop().getAngleSpeed()*1.2f);		
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
			
			
			if(mainActivity.isPressMenu()){
				classicGame.setState(GameFrame.PAUSE);
				mainActivity.setPressMenu(false);
			}
			
			if(classicGame.isCollideFlag()){
				mainActivity.getHandler().sendEmptyMessage(MainActivity.VIBRATE_MESSAGE);
				classicGame.setCollideFlag(false);
			}
			
			if(classicGame.isDifficultyFlag()){
				difficultyToast.show();
				classicGame.setDifficultyFlag(false);
			}
			
			if(classicGame.getState()==GameFrame.RUN){
				gl.glTranslatef(0f, 0f, -100.0f); // ��������ͼ����100�����ֵûʲô���壩����֤���Կ������ݶ���
		
				
				gl.glPushMatrix(); // ������ǰ����
				drawBackground.drawSelf(gl); // ������
				gl.glPopMatrix(); // �ظ�֮ǰ�任����
				
				
				gl.glPushMatrix(); // ��ͼǰ�������		
				if(showFlag){
					gl.glRotatef(365/10-showAngle/10, (float)Math.cos(showAngle/20), (float)Math.sin(showAngle/20), 0);
					gl.glTranslatef((360-showAngle)/180*(float)Math.cos(showAngle/45), (365-showAngle)/180*(float)Math.sin(showAngle/45), 0);
				}				
				classicGame.draw(gl); // ������Ϸ�Ļ�ͼ����
				gl.glPopMatrix(); // ��ͼ��ָ�����
	
				
				gl.glPushMatrix();
				if(showFlag==false){
					drawTrack.drawSelf(gl);
				}
				gl.glPopMatrix();

				
				gl.glPushMatrix();
				gl.glTranslatef(-1.5f, 0.6f, 3);
				drawScore.setScore((int)classicGame.getScore());
				drawScore.drawSelf(gl);
				gl.glPopMatrix();
				
				gl.glPushMatrix();
				gl.glTranslatef(1.2f, 0.6f, 3);
				drawTime.setScore((int)classicGame.getDuration());
				drawTime.drawSelf(gl);
				gl.glPopMatrix();
				

			}else if(classicGame.getState()==GameFrame.END){

				mainActivity.setCurrScore((int)classicGame.getScore());
				
				mainActivity.goToOverView();
				
				classicGame.setState(GameFrame.PREPARE); // ��ֹ���д�����ݿ�
				
			}else if(classicGame.getState()==GameFrame.PAUSE){
				gl.glTranslatef(0f, 0f, -100.0f); // ��������ͼ����100�����ֵûʲô���壩����֤���Կ������ݶ���		
				gl.glPushMatrix(); // ��ͼǰ�������					
				classicGame.draw(gl); // ������Ϸ�Ļ�ͼ����
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

			classicGame.start();
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
