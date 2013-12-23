package com.gg.module;

import javax.microedition.khronos.opengles.GL10;

import com.gg.util.TextureRect;
import com.gg.view.ClassicGameSurfaceView;
import com.gg.view.TimeGameSurfaceView;

public class DrawScore {

	ClassicGameSurfaceView mv;
	TimeGameSurfaceView tv;
	TextureRect[] numbers=new TextureRect[10];
	
	private int score;
	private float numberSize = 0.2f;
	private float adjaceney = 0.20f; // �־�
	
	public DrawScore(int texId,ClassicGameSurfaceView mv)
	{
		this.mv=mv;
		
		//����0-9ʮ�����ֵ��������
		for(int i=0;i<10;i++)
		{
			numbers[i]=new TextureRect
            (
            		
            		numberSize,numberSize,0,
            		texId,
           		 new float[]//�趨��������
		             {
		           	  0.1f*i,0, 0.1f*i,1, 0.1f*(i+1),1,
		           	  0.1f*(i+1),1, 0.1f*(i+1),0,  0.1f*i,0
		             }
             ); 
		}
	}
	
	public DrawScore(int texId,TimeGameSurfaceView mv) {
		// TODO Auto-generated constructor stub
		
		this.tv = mv;
		
		//����0-9ʮ�����ֵ��������
		for(int i=0;i<10;i++)
		{
			numbers[i]=new TextureRect
            (
            		
            		numberSize,numberSize,0,
            		texId,
           		 new float[]//�趨��������
		             {
		           	  0.1f*i,0, 0.1f*i,1, 0.1f*(i+1),1,
		           	  0.1f*(i+1),1, 0.1f*(i+1),0,  0.1f*i,0
		             }
             ); 
		}
	}

	public void drawSelf(GL10 gl)//��ϰģʽ
	{		
		String scoreStr=score+"";
		for(int i=0;i<scoreStr.length();i++)
		{//���÷��е�ÿ�������ַ�����
			char c=scoreStr.charAt(i);
			gl.glPushMatrix();
			gl.glTranslatef(i*adjaceney, 0, 0);
			numbers[c-'0'].drawSelf(gl);
			gl.glPopMatrix();
		}
		//System.out.println("score = "+scoreStr);
	}

	

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	
	
}
