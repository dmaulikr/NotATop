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

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.Toast;

import com.gg.game.ClassicGame;
import com.gg.game.GameFrame;
import com.gg.module.*;
import com.gg.top.*;
import com.gg.util.*;


public class SelectSurfaceView extends GLSurfaceView {

	private MainActivity mainActivity; // ʹ�ô˽����Activity
	private SceneRenderer sceneRender; // ������Ⱦ��
	
	private int nextConeTextureId;
	private int nextCylinderTextureId;
	private int nextCircleTextureId;
	private int prevConeTextureId;
	private int prevCylinderTextureId;
	private int prevCircleTextureId;
	
	private int currentConeTextureId; // Բ׶������Id
	private int currentCylinderTextureId; // Բ��������Id
	private int currentCircleTextureId; // Բ������Id
	private int hiddenConeTextureId;
	private int hiddenCylinderTextureId;
	private int hiddenCircleTextureId;

	private int[] initConeTextureIdArray; // ���ܶ�̬initTextureͼƬ��ֻ�������������ʼ��
	private int[] initCylinderTextureIdArray;
	private int[] initCircleTextureIdArray;

	private int backgroundTextureId; // ����������Id

	private DrawTop nextDrawTop;
	private DrawTop prevDrawTop;
	
	private DrawTop currentDrawTop;
	private DrawTop hiddenDrawTop;
	
	private int index;
	private int numberOfTop;

	private boolean moveLeftFlag;
	private boolean moveRightFlag;
	
	private Toast noLeftToast;
	private Toast noRightToast;
	
	private boolean moveHiddenToLeftFlag = true; // Ĭ��hiddenDrawTop���ڵ�ǰ���ݵ��Ҳ࣬�˱����hiddenDrawTop�Ƶ���෽������

	private DrawTrack drawTrack; // ��ʾ�����켣�Ķ���

	private DrawBackground drawBackground; // ��ʾ�����Ķ���
	
	private double downX;
	private double downY;
	private double upX;
	private double upY;

	
	public SelectSurfaceView(Context context) {
		super(context);
		mainActivity = (MainActivity) context; // ���ʹ�ô˽����Activity
		sceneRender = new SceneRenderer(); // ����������Ⱦ��
		setRenderer(sceneRender); // ������Ⱦ��
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);// ������ȾģʽΪ������Ⱦ
		
		index = SelectControl.getIndex();
		numberOfTop = SelectControl.getNumberOfTop();
		
		noLeftToast = Toast.makeText(context, "�Ѿ��ǵ�һ����", Toast.LENGTH_SHORT);
		noRightToast = Toast.makeText(context, "�Ѿ������һ����", Toast.LENGTH_SHORT);
		
		
		initConeTextureIdArray = new int[numberOfTop]; // ���ܶ�̬initTextureͼƬ��ֻ�������������ʼ��
		initCylinderTextureIdArray = new int[numberOfTop];
		initCircleTextureIdArray = new int[numberOfTop];
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) { // Ӧ���Ƕ�㴥����bug
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
			
			if(moveLeftFlag==false && moveRightFlag==false){ // ��������ƶ�����Ӧ��

				if(downX<-1.0 && downY>0.3 && upX<-1.0 && upY>0.3){
					mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.MAIN_MENU);
				}else if(downX>1.0 && downY>0.3 && upX>1.0 && upY>0.3){
					mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.CLASSIC_GAME);
				}				
				
				
				
				
				if(downX>-1.5 && downX<-0.9 && downY>-0.7 && downY<0.0){ // ���½ǵġ���һ������ť����������
					if(upX>-1.5 && upX<-0.9 && upY>-0.7 && upY<0.0){ // ͬʱƥ����ָ�ƿ�ʱ��λ�ã���ֹ��
						if(index>0){ // ���������һ�����ݿ�ѡ
							moveRightFlag = true; // ��������ƽ�Ʊ�־��ʵ�����ݵ�ƽ��
						}else{
							noLeftToast.show(); // û������ʾ��ʾ��Ϣ
						}
					}
				}else if(downX<1.5 && downX>0.9 && downY>-0.7 && downY<0.0){ // ���½ǵġ���һ������ť����������
					if(upX<1.5 && upX>0.9 && upY>-0.7 && upY<0.0){
						if(index<numberOfTop-1){
							moveLeftFlag = true;
						}else{
							noRightToast.show();
						}
					}
				}
				
				if(downX>-0.9 && downX<0.9){ // ��ָ�һ�����������
					if(upX-downX>0.5){
						if(index>0){
							moveRightFlag = true;
						}else{
							noLeftToast.show();
						}
					}else if(downX-upX>0.5){ // ��ָ�󻮣���������
						if(index<numberOfTop-1){
							moveLeftFlag = true;
						}else{
							noRightToast.show();
						}
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
		
		private int trackTextureId;

		public SceneRenderer() { // ��Ⱦ���Ĺ��캯��

		}

		public void onSurfaceCreated(final GL10 gl, EGLConfig config) { // ��������ʱ���ã����ڳ�ʼ��������Դ

			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // �������������ɫ
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT); // �����ɫ����

			nextConeTextureId = initTexture(gl, R.drawable.next_cone);
			nextCylinderTextureId = initTexture(gl, R.drawable.next_cylinder);
			nextCircleTextureId = initTexture(gl, R.drawable.next_circle);
			prevConeTextureId = initTexture(gl, R.drawable.prev_cone);
			prevCylinderTextureId = initTexture(gl, R.drawable.prev_cylinder);
			prevCircleTextureId = initTexture(gl, R.drawable.prev_circle);

			
			
			initConeTextureIdArray[0] = initTexture(gl, R.drawable.cone0);
			initConeTextureIdArray[1] = initTexture(gl, R.drawable.cone1);
			initConeTextureIdArray[2] = initTexture(gl, R.drawable.cone2);
			initConeTextureIdArray[3] = initTexture(gl, R.drawable.cone3);
			initConeTextureIdArray[4] = initTexture(gl, R.drawable.cone4);
			
			initCylinderTextureIdArray[0] = initTexture(gl, R.drawable.cylinder0);
			initCylinderTextureIdArray[1] = initTexture(gl, R.drawable.cylinder1);
			initCylinderTextureIdArray[2] = initTexture(gl, R.drawable.cylinder2);
			initCylinderTextureIdArray[3] = initTexture(gl, R.drawable.cylinder3);
			initCylinderTextureIdArray[4] = initTexture(gl, R.drawable.cylinder4);
			
			initCircleTextureIdArray[0] = initTexture(gl, R.drawable.circle0);
			initCircleTextureIdArray[1] = initTexture(gl, R.drawable.circle1);
			initCircleTextureIdArray[2] = initTexture(gl, R.drawable.circle2);
			initCircleTextureIdArray[3] = initTexture(gl, R.drawable.circle3);
			initCircleTextureIdArray[4] = initTexture(gl, R.drawable.circle4);
			
			
			
			currentConeTextureId = initTexture(gl, SelectControl.getConeTextureId());
			currentCylinderTextureId = initTexture(gl, SelectControl.getCylinderTextureId());
			currentCircleTextureId = initTexture(gl, SelectControl.getCircleTextureId());
			
			SelectControl.next();
			hiddenConeTextureId = initTexture(gl, SelectControl.getConeTextureId());
			hiddenCylinderTextureId = initTexture(gl, SelectControl.getCylinderTextureId());
			hiddenCircleTextureId = initTexture(gl, SelectControl.getCircleTextureId());
			SelectControl.prev();
			
			

			backgroundTextureId = initTexture(gl, R.drawable.select_bg); // ��ʼ������������
			
			trackTextureId = initTexture(gl, R.drawable.track);


			prevDrawTop = new DrawTop(prevConeTextureId,prevCylinderTextureId,prevCircleTextureId);
			prevDrawTop.setRadius(0.2f);
			prevDrawTop.setBasicPoint(new Point(-1.2f, -2.0f));
			prevDrawTop.setAngleSpeed(5);
			prevDrawTop.generateData();
			
			
			nextDrawTop = new DrawTop(nextConeTextureId,nextCylinderTextureId,nextCircleTextureId);
			nextDrawTop.setRadius(0.2f);
			nextDrawTop.setBasicPoint(new Point(1.2f, -2.0f));
			nextDrawTop.setAngleSpeed(5);
			nextDrawTop.generateData();

			
			
			currentDrawTop = new DrawTop(currentConeTextureId,currentCylinderTextureId,currentCircleTextureId);
			currentDrawTop.setRadius(0.55f);
			currentDrawTop.setBasicPoint(new Point(0.0f, -1.65f));
			currentDrawTop.setAngleSpeed(3);
			currentDrawTop.generateData();
			
			hiddenDrawTop = new DrawTop(hiddenConeTextureId,hiddenCylinderTextureId,hiddenCircleTextureId);
			hiddenDrawTop.setRadius(0.55f);
			hiddenDrawTop.setBasicPoint(new Point(2.5f, -1.65f));
			hiddenDrawTop.setAngleSpeed(3);
			hiddenDrawTop.generateData();
	

			drawTrack = new DrawTrack(trackTextureId); // �������켣�Ķ���

			drawBackground = new DrawBackground(backgroundTextureId); // �����������Ķ���

			gl.glDisable(GL10.GL_DITHER); // �رտ�����
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST); // �����ض�Hint��Ŀ��ģʽ������Ϊ����Ϊʹ�ÿ���ģʽ
			gl.glClearColor(0, 0, 0, 0); // ������Ļ����ɫ��ɫRGBA
			gl.glShadeModel(GL10.GL_SMOOTH); // ������ɫģ��Ϊƽ����ɫ
			gl.glEnable(GL10.GL_DEPTH_TEST); // ������Ȳ���


			new Thread()
			{
				public void run() {
					while (true) {
						nextDrawTop.rotate();
						prevDrawTop.rotate();
							
						currentDrawTop.rotate();
						hiddenDrawTop.rotate();
						
						if(moveLeftFlag){
							if(hiddenDrawTop.getBasicPoint().x>=0f){
								currentDrawTop.setBasicPoint(new Point(currentDrawTop.getBasicPoint().x-0.1f, currentDrawTop.getBasicPoint().y));
								hiddenDrawTop.setBasicPoint(new Point(hiddenDrawTop.getBasicPoint().x-0.1f, hiddenDrawTop.getBasicPoint().y));
							}else{
								moveLeftFlag = false;
								
								SelectControl.next();
								index++;
								
								currentDrawTop.setConeTextureId(initConeTextureIdArray[index]);
								currentDrawTop.setCylinderTextureId(initCylinderTextureIdArray[index]);
								currentDrawTop.setCircleTextureId(initCircleTextureIdArray[index]);
								//currentDrawTop.generateData();
								currentDrawTop.setBasicPoint(new Point(0f, currentDrawTop.getBasicPoint().y));
								
								if(index<numberOfTop-1){ // ��ֹ�Ѿ������ұ��˻�ΪhiddenDrawTop�������ֵ
									index++;
									hiddenDrawTop.setConeTextureId(initConeTextureIdArray[index]);
									hiddenDrawTop.setCylinderTextureId(initCylinderTextureIdArray[index]);
									hiddenDrawTop.setCircleTextureId(initCircleTextureIdArray[index]);
									//hiddenDrawTop.generateData();
									index--;
								}
								hiddenDrawTop.setBasicPoint(new Point(2.5f, hiddenDrawTop.getBasicPoint().y));
								
							}
							
						}
						
						if(moveRightFlag){
							if(moveHiddenToLeftFlag==true){

								if(index>0){ // ��ֹ���
									index--;
									hiddenDrawTop.setConeTextureId(initConeTextureIdArray[index]);
									hiddenDrawTop.setCylinderTextureId(initCylinderTextureIdArray[index]);
									hiddenDrawTop.setCircleTextureId(initCircleTextureIdArray[index]);
									//hiddenDrawTop.generateData();
									index++;
								}
								hiddenDrawTop.setBasicPoint(new Point(-2.5f, hiddenDrawTop.getBasicPoint().y));		
								
								moveHiddenToLeftFlag = false;
							}
							
							if(hiddenDrawTop.getBasicPoint().x<=0f){
								currentDrawTop.setBasicPoint(new Point(currentDrawTop.getBasicPoint().x+0.1f, currentDrawTop.getBasicPoint().y));
								hiddenDrawTop.setBasicPoint(new Point(hiddenDrawTop.getBasicPoint().x+0.1f, hiddenDrawTop.getBasicPoint().y));
							}else{
								moveRightFlag = false;
								moveHiddenToLeftFlag = true;
								
								SelectControl.prev();
								index--;
								
								currentDrawTop.setConeTextureId(initConeTextureIdArray[index]);
								currentDrawTop.setCylinderTextureId(initCylinderTextureIdArray[index]);
								currentDrawTop.setCircleTextureId(initCircleTextureIdArray[index]);
								//currentDrawTop.generateData();
								currentDrawTop.setBasicPoint(new Point(0f, currentDrawTop.getBasicPoint().y));
								
								if(index<numberOfTop-1){
									index++;
									hiddenDrawTop.setConeTextureId(initConeTextureIdArray[index]);
									hiddenDrawTop.setCylinderTextureId(initCylinderTextureIdArray[index]);
									hiddenDrawTop.setCircleTextureId(initCircleTextureIdArray[index]);
									//hiddenDrawTop.generateData();
									index--;
								}
								hiddenDrawTop.setBasicPoint(new Point(2.5f, hiddenDrawTop.getBasicPoint().y));
							}							
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
			gl.glTranslatef(0, 0, -1);
			gl.glRotatef(-70, 1, 0, 0);
			nextDrawTop.drawSelf(gl);
			gl.glPopMatrix();
			
			gl.glPushMatrix();
			gl.glTranslatef(0, 0, -1);
			gl.glRotatef(-70, 1, 0, 0);
			prevDrawTop.drawSelf(gl);
			gl.glPopMatrix();

			gl.glPushMatrix();
			gl.glRotatef(-60, 1, 0, 0);
			currentDrawTop.drawSelf(gl);
			gl.glPopMatrix();
			
			gl.glPushMatrix();
			gl.glRotatef(-60, 1, 0, 0);
			hiddenDrawTop.drawSelf(gl);
			gl.glPopMatrix();
			

			
			

			gl.glPushMatrix();
			drawTrack.drawSelf(gl);
			gl.glPopMatrix();

			gl.glPushMatrix(); // ������ǰ����
//			gl.glTranslatef(0, 0, -1);
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
