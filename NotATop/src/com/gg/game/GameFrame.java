package com.gg.game;

import javax.microedition.khronos.opengles.GL10;
import android.view.MotionEvent;

/*		��Ϸ�Ŀ�ܣ�����һ����Ϸ������ʵ�ֵĺ���		*/
abstract public class GameFrame {
	
	public final static int PREPARE = 0;
	public final static int RUN = 1;
	public final static int PAUSE = 2;
	public final static int END = 3;

	abstract public void init();// ��ʼ����Ϸ��Դ

	abstract public void start();// ��Ϸ��ʼ����ʼ����ϷԪ��״̬

	abstract public void pause();// ��Ϸ��ͣ��ֹͣһ�ж���

	abstract public void resume();// ��Ϸ�ָ�������һ�ж���

	abstract public void end();// ��Ϸ�������ͷ���Դ

	abstract public void save();// ������Ϸ����¼��ϷԪ��״̬

	abstract public void load();// ������Ϸ���ָ���ϷԪ��״̬

	abstract public void onTouch(MotionEvent e);// ��Ӧ������Ϣ

	abstract public void logic();// ��Ϸ�߼����֣�ÿ���̶�ʱ�����һ��

	abstract public void draw(GL10 gl);// ����OpenGL��ͼ
}
