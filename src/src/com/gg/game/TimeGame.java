package com.gg.game;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import android.view.MotionEvent;
import android.widget.Toast;

import com.gg.module.*;
import com.gg.top.BasicTop;
import com.gg.top.DrawTop;
import com.gg.util.*;
import com.gg.view.R;

/* ��ʱ��Ϸģʽ�࣬��60����ͨ�����������ά����ת����÷� */
public class TimeGame extends GameFrame implements Runnable { // �̳���Ϸ����࣬ʵ����Ϸ����ʵ�ֵĺ���

	/* 3D��ͼ���õ��ı��� */
	private GL10 gl; // ��3D��������Ҫ��gl����
	private int coneTextureId; // ���ݵײ�Բ׶������
	private int cylinderTextureId; // �����м�Բ��������
	private int circleTextureId; // ���ݶ���Բ������
	private int pauseTextureId; // ��ͣʱ��ʾ��ͼƬ������

	/* ��Ӧ������Ϣ���õı��� */
	private long startTime; // ��ʼ��Ӧ������Ϣ��ʱ�̣�������������������ʱ��
	private long endTime; // ��startTime��Ӧ
	private long touchStart; // ������startTime����ʼ��Ӧ������Ϣ��ʱ�̣����ڿ�����ACTION_MOVEʱ��Ӧ������Ҫ̫��
	private boolean responseFlag; // ��ʾ������Ӧ������Ϣ�ı�־
	private Point firstPoint; // ��Ӧ������Ϣ�ĵ�һ���㣬���л�����Ϣ���ն���������߶�������
	private Point secondPoint; // ��Ӧ������Ϣ�ĵڶ�����

	/* ��Ϸ�߼��õ��ı��� */
	private Thread thread; // ��Ϸ�߼����е��߳�
	private int state; // ��ʾ��Ϸ״̬�ı���
	private double duration; // ��ʾ��Ϸ���е�ʱ��
	private double score; // ��ʾ��ϷĿǰ�ĵ÷֣�ע��÷ֲ���ֱ����ʱ��������صģ��������ݵ��µĽǶ��йأ����ݵ��ĽǶ�Խ��÷�ҲԽ�ߣ�
	private DrawTop drawTop; // 3D���ݶ���
	private Circle logicCircle; // �������ݵ��߼�Բ��ʵ���ϻ�������ײ����Ӧ����������߼�Բ�ģ��Ӷ���������������������
	private DrawBackground drawPause; // ��ͣ����ı�������

	/* ����罻���ı��� */
	private boolean collideFlag; // ��ʾ���ݣ�ʵ�������߼�Բ��������Ե��Ҫ���ⲿ��Activityʹ�ֻ���
	private boolean crackFlag; // �ٺ٣���ʾ����Crack���ܵı�־

	/* �Ƚ��ر�ı��� */
	private int currentLevel; // ��ʾ��ǰ�ؿ��ĵȼ�����Χ��1~8
	private int currentGoal; // ��ʾ��ɵ�ǰ�ȼ�����Ҫ�ĵ÷֣���ʽ��ÿ��2000��

	public TimeGame(GL10 gl, int coneTextureId, int cylinderTextureId,
			int circleTextureId, int pauseTextureId) { // ��ʼ������3D��Դ

		this.gl = gl;
		this.coneTextureId = coneTextureId;
		this.cylinderTextureId = cylinderTextureId;
		this.circleTextureId = circleTextureId;
		this.pauseTextureId = pauseTextureId;

		state = PREPARE; // ������ϷΪ׼��״̬
		init(); // ��ʼ����Ϸ�Ķ�����Դ��ע����ʱ��û��ʼ��Ϸ��
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

		drawTop = new DrawTop(coneTextureId, cylinderTextureId, circleTextureId);// �������ݶ��󣬲�������Ӧ��������ͼ
		logicCircle = new Circle(drawTop.getBasicPoint().x,
				drawTop.getBasicPoint().y, drawTop.getRadius()); // ������Ϊ���������߼�Բ����

		drawPause = new DrawBackground(pauseTextureId); // �����������󣬲�������Ӧ��������ͼ

		firstPoint = new Point(0f, 0f, 0f); // �������ص�Ķ��󣨾�����������ͺ����ҵ�ԭ���ˣ�
		secondPoint = new Point(0f, 0f, 0f); // ͬ���������ص�Ķ���
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

		state = RUN; // ����Ϸ״̬�ĳ�RUN����״̬
		drawTop.setState(RUN); // ������״̬�ĳ�RUN����״̬

		duration = 0; // ������Ϸʱ��Ϊ��
		score = 0; // ������Ϸ����Ϊ��

		thread = new Thread(this); // ������Ϸ�߼����е��̶߳���
		thread.start(); // ��㿪����Ϸ�߼��߳�
	}

	public void run() {
		// TODO Auto-generated method stub

		while (true) { // ��ѭ������֤��Ϸ��ʲô��Ϸ״̬�¶��ܰ���Ҫ�������У����ҵ��ô˺�����ʱ�����ǹ̶���
			long start = System.currentTimeMillis(); // ���ȼ�¼��һ��Сѭ���Ŀ�ʼʱ��

			logic(); // ��Ϸ���߼�����

			long end = System.currentTimeMillis(); // ��¼��һ��Сѭ���Ľ���ʱ��

			try {
				if (end - start < Constant.INTERVAL) { // ����߼����е�ʱ��ȹ涨��ʱ������
					thread.sleep(Constant.INTERVAL - (end - start)); // �������ǵĲ�ֵ���Ӷ���֤��ÿ���涨ʱ��������һ��Сѭ��
				}
			} catch (Exception e) { // ��Ϊsleep������Ҫ�����쳣
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onTouch(MotionEvent e) {
		// TODO Auto-generated method stub
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (state == RUN) {
				responseFlag = false;
			} else if (state == PAUSE) {
				double x = Constant.convertX(e.getX());
				double y = Constant.convertY(e.getY());
				
				if(x>-0.8 && x<-0.2 && y>0.-6 && y<0.0){
					resume();
				}else if(x<0.8 && x>0.2 && y>0.-6 && y<0.0){
					end();
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (state == RUN) {
				if (responseFlag == false) {
					if (isInLogicCircle(Constant.convertX(e.getX()),
							Constant.convertY(e.getY()))) {
						// lastTime = System.currentTimeMillis();
						startTime = System.currentTimeMillis();

						touchStart = System.currentTimeMillis();

						firstPoint.x = Constant.convertX(e.getX());
						firstPoint.y = Constant.convertY(e.getY());

						responseFlag = true;
					}
				} else {
					endTime = System.currentTimeMillis();

					if (crackFlag == true) {
						endTime = 0;
					}

					if (endTime - startTime > 90) {
						return;
					}

					if (System.currentTimeMillis() - touchStart > Constant.INTERVAL) {
						if (isInLogicCircle(Constant.convertX(e.getX()),
								Constant.convertY(e.getY()))) {
							secondPoint.x = Constant.convertX(e.getX());
							secondPoint.y = Constant.convertY(e.getY());

							responseTouch(firstPoint, secondPoint);
							// System.out.println("response");

							touchStart = System.currentTimeMillis();
							firstPoint.x = Constant.convertX(e.getX());
							firstPoint.y = Constant.convertY(e.getY());
						}

					}
				}
			}

			break;
		case MotionEvent.ACTION_UP:
			if (state == RUN) {
				firstPoint.x = 0;
				firstPoint.y = 0;
				secondPoint.x = 0;
				secondPoint.y = 0;

				responseFlag = false;
			}

			break;
		}
	}

	@Override
	public void logic() {
		// TODO Auto-generated method stub

		if (state == RUN) {
			drawTop.logic();

			updateLogicCircle();

			duration += (double) Constant.INTERVAL / 1000;
			score += (double) Constant.INTERVAL / 1000 * drawTop.getAxleAngle()
					* 10 + duration * 120 / 1000;// �������֮һ�����ɼ��ʱ�乱�׵�

		} else if (state == PAUSE) {

		}

		if (drawTop.getAxleAngle() > drawTop.getDEAD_AXLE_ANGLE()) {
			end();
		}

		if (duration >= 60) {
			end();
		}

		if (logicCircle.getCenter().x - logicCircle.getRadius() <= -Constant.LOGIC_WIDTH / 2) { // ײ����ǽ
			drawTop.getBasicPoint().x += 0.1f; // ����Ҫƽ�Ʒ�ֹ�ٴ�ײ��0.05�Ļ�ײ�껹����ײ��0.1�Ͳ����ˣ�
			drawTop.setxMoveSpeed(-drawTop.getxMoveSpeed() * 1 / 2); // ���ٶȷ��򲢼���
			drawTop.setAngleSpeed(drawTop.getAngleSpeed() * 9 / 10); // ���ٶȼ���
			collideFlag = true; // ������ײ��־���Է���Ϣ��Ӧ��
		} else if (logicCircle.getCenter().x + logicCircle.getRadius() >= Constant.LOGIC_WIDTH / 2) { // ײ����ǽ
			drawTop.getBasicPoint().x -= 0.1f;
			drawTop.setxMoveSpeed(-drawTop.getxMoveSpeed() * 1 / 2);
			drawTop.setAngleSpeed(drawTop.getAngleSpeed() * 9 / 10);
			collideFlag = true;
		}

		if (logicCircle.getCenter().y + logicCircle.getRadius() >= Constant.LOGIC_HEIGHT / 2) { // ײ����ǽ
			drawTop.getBasicPoint().y -= 0.1f;
			drawTop.setyMoveSpeed(-drawTop.getyMoveSpeed() * 1 / 2);
			drawTop.setAngleSpeed(drawTop.getAngleSpeed() * 9 / 10);
			collideFlag = true;
		} else if (logicCircle.getCenter().y - logicCircle.getRadius() <= -Constant.LOGIC_HEIGHT / 2) { // ײ����ǽ
			drawTop.getBasicPoint().y += 0.1f;
			drawTop.setyMoveSpeed(-drawTop.getyMoveSpeed() * 1 / 2);
			drawTop.setAngleSpeed(drawTop.getAngleSpeed() * 9 / 10);
			collideFlag = true;
		}

	}

	@Override
	public void draw(GL10 gl) {
		// TODO Auto-generated method stub
		if (state == RUN) {
			drawTop.drawSelf(gl);
		} else if (state == PAUSE) {
			drawPause.drawSelf(gl);
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

		state = PAUSE;
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

		state = RUN;
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub

		try {
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}

		state = END;
		drawTop.setState(END);

	}

	@Override
	public void save() {
		// TODO Auto-generated method stub

	}

	@Override
	public void load() {
		// TODO Auto-generated method stub

	}

	public void calculateGoal() {
		currentGoal = 2000 * currentLevel;
	}

	public DrawTop getDrawTop() {
		return drawTop;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void updateLogicCircle() {
		// logicCircle.getCenter().x = drawTop.getBasicPoint().x +
		// 3*drawTop.getRadius()*drawTop.getAxleVector().x;
		// logicCircle.getCenter().y = drawTop.getBasicPoint().y +
		// 3*drawTop.getRadius()*drawTop.getAxleVector().y;
		logicCircle.getCenter().x = drawTop.getBasicPoint().x + 3
				* drawTop.getRadius()
				* (float) Math.sin(drawTop.getAxleAngle() * Math.PI / 180)
				* (float) Math.cos(drawTop.getAxleAngleCount() * Math.PI / 180);
		logicCircle.getCenter().y = drawTop.getBasicPoint().y + 3
				* drawTop.getRadius()
				* (float) Math.sin(drawTop.getAxleAngle() * Math.PI / 180)
				* (float) Math.sin(drawTop.getAxleAngleCount() * Math.PI / 180);
		logicCircle.setRadius(drawTop.getRadius());

	}

	public boolean isInLogicCircle(float x, float y) {
		float distance = (float) Math.sqrt((x - logicCircle.getCenter().x)
				* (x - logicCircle.getCenter().x)
				+ (y - logicCircle.getCenter().y)
				* (y - logicCircle.getCenter().y));
		if (distance < logicCircle.getRadius() * 1.2
				&& distance > logicCircle.getRadius() / 3) {
			return true;
		} else {
			return false;
		}

	}

	public void responseTouch(Point firstPoint, Point secondPoint) {

		Line touchLine = new Line(firstPoint, secondPoint);
		Line axleLine = new Line(drawTop.getBasicPoint(),
				logicCircle.getCenter());

		float angleSpeedOffset = 0.7f; // ��ת������޴�һ�����ӵĽ��ٶȣ�1.0�Ϳ���˲����٣�0.8��������ף�0.5����ά��,0.6Ҳͦ��,0.7ͦ�õģ�
		float downAngleSpeedOffset = 0.5f; // �ĵ��������������Ľ��ٶȣ�������߻���Ŀ��Ժ�����

		float xMoveOffset = 0.04f; // �Ĵ�һ����x�᷽��ĵ������루0.06�е����,0.04�ܵ�ס�˶��Ҳ�Ծ����
		float yMoveOffset = 0.04f; // �Ĵ�һ����y�᷽��ĵ�������
		float xMoveSpeedOffset = 0.0015f; // �Ĵ�һ��x�᷽���ٶȵĸı�����0.001�����ˣ��������ξ���Ч����,0.02�ѿ��ƣ�0.0015�����ˣ�
		float yMoveSpeedOffset = 0.0015f; // �Ĵ�һ��y�᷽���ٶȵĸı���

		switch (touchLine.getDirection().getDirection()) { // �����ж��ֻ��ߵķ���
		case Direction.UP_RIGHT: // ������������Ϸ�����
			if (touchLine.directionCircle(logicCircle).getDirection() == Direction.DOWN) { // ���ж�����������߼�Բ���·�
				angleSpeedOffset = -angleSpeedOffset; // ���ٶȼ���
				xMoveSpeedOffset = -xMoveSpeedOffset; // ����һ��
				yMoveSpeedOffset = yMoveSpeedOffset; // ����һ��
				xMoveOffset = -xMoveOffset; // �����ٶȼ��ٻ������ٶ�����
				yMoveOffset = yMoveOffset; // �����ٶ����ӻ������ٶȼ���
				if (axleLine.getDirection().getDirection() == Direction.UP_LEFT) { // ��������������Ϸ���
					downAngleSpeedOffset = -downAngleSpeedOffset; // �������ĵ������ٶȼ��٣�
				} else if (axleLine.getDirection().getDirection() == Direction.DOWN_RIGHT) { // ��������������Ϸ���
					downAngleSpeedOffset = downAngleSpeedOffset; // �����������������ٶ����ӣ�
				}
			} else if (touchLine.directionCircle(logicCircle).getDirection() == Direction.UP) {
				angleSpeedOffset = angleSpeedOffset;
				xMoveSpeedOffset = xMoveSpeedOffset;
				yMoveSpeedOffset = -yMoveSpeedOffset;
				xMoveOffset = xMoveOffset;
				yMoveOffset = -yMoveOffset;
				if (axleLine.getDirection().getDirection() == Direction.DOWN_RIGHT) {
					downAngleSpeedOffset = -downAngleSpeedOffset;
				} else if (axleLine.getDirection().getDirection() == Direction.UP_LEFT) {
					downAngleSpeedOffset = downAngleSpeedOffset;
				}
			}
			break;
		case Direction.DOWN_RIGHT:
			if (touchLine.directionCircle(logicCircle).getDirection() == Direction.DOWN) {
				angleSpeedOffset = -angleSpeedOffset;
				xMoveSpeedOffset = xMoveSpeedOffset;
				yMoveSpeedOffset = yMoveSpeedOffset;
				xMoveOffset = xMoveOffset;
				yMoveOffset = yMoveOffset;
				if (axleLine.getDirection().getDirection() == Direction.UP_RIGHT) {
					downAngleSpeedOffset = -downAngleSpeedOffset;
				} else if (axleLine.getDirection().getDirection() == Direction.DOWN_LEFT) {
					downAngleSpeedOffset = downAngleSpeedOffset;
				}
			} else if (touchLine.directionCircle(logicCircle).getDirection() == Direction.UP) {
				angleSpeedOffset = angleSpeedOffset;
				xMoveSpeedOffset = -xMoveSpeedOffset;
				yMoveSpeedOffset = -yMoveSpeedOffset;
				xMoveOffset = -xMoveOffset;
				yMoveOffset = -yMoveOffset;
				if (axleLine.getDirection().getDirection() == Direction.DOWN_LEFT) {
					downAngleSpeedOffset = -downAngleSpeedOffset;
				} else if (axleLine.getDirection().getDirection() == Direction.UP_RIGHT) {
					downAngleSpeedOffset = downAngleSpeedOffset;
				}
			}
			break;
		case Direction.UP_LEFT:
			if (touchLine.directionCircle(logicCircle).getDirection() == Direction.DOWN) {
				angleSpeedOffset = angleSpeedOffset;
				xMoveSpeedOffset = xMoveSpeedOffset;
				yMoveSpeedOffset = yMoveSpeedOffset;
				xMoveOffset = xMoveOffset;
				yMoveOffset = yMoveOffset;
				if (axleLine.getDirection().getDirection() == Direction.UP_RIGHT) {
					downAngleSpeedOffset = -downAngleSpeedOffset;
				} else if (axleLine.getDirection().getDirection() == Direction.DOWN_LEFT) {
					downAngleSpeedOffset = downAngleSpeedOffset;
				}
			} else if (touchLine.directionCircle(logicCircle).getDirection() == Direction.UP) {
				angleSpeedOffset = -angleSpeedOffset;
				xMoveSpeedOffset = -xMoveSpeedOffset;
				yMoveSpeedOffset = -yMoveSpeedOffset;
				xMoveOffset = -xMoveOffset;
				yMoveOffset = -yMoveOffset;
				if (axleLine.getDirection().getDirection() == Direction.DOWN_LEFT) {
					downAngleSpeedOffset = -downAngleSpeedOffset;
				} else if (axleLine.getDirection().getDirection() == Direction.UP_RIGHT) {
					downAngleSpeedOffset = downAngleSpeedOffset;
				}
			}
			break;
		case Direction.DOWN_LEFT:
			if (touchLine.directionCircle(logicCircle).getDirection() == Direction.DOWN) {
				angleSpeedOffset = angleSpeedOffset;
				xMoveSpeedOffset = -xMoveSpeedOffset;
				yMoveSpeedOffset = yMoveSpeedOffset;
				xMoveOffset = -xMoveOffset;
				yMoveOffset = yMoveOffset;
				if (axleLine.getDirection().getDirection() == Direction.UP_LEFT) {
					downAngleSpeedOffset = -downAngleSpeedOffset;
				} else if (axleLine.getDirection().getDirection() == Direction.DOWN_RIGHT) {
					downAngleSpeedOffset = downAngleSpeedOffset;
				}
			} else if (touchLine.directionCircle(logicCircle).getDirection() == Direction.UP) {
				angleSpeedOffset = -angleSpeedOffset;
				xMoveSpeedOffset = xMoveSpeedOffset;
				yMoveSpeedOffset = -yMoveSpeedOffset;
				xMoveOffset = xMoveOffset;
				yMoveOffset = -yMoveOffset;
				if (axleLine.getDirection().getDirection() == Direction.DOWN_RIGHT) {
					downAngleSpeedOffset = -downAngleSpeedOffset;
				} else if (axleLine.getDirection().getDirection() == Direction.UP_LEFT) {
					downAngleSpeedOffset = downAngleSpeedOffset;
				}
			}
			break;
		}

		if (drawTop.getAngleSpeed() + angleSpeedOffset < BasicTop.MAX_ANGLE_SPEED) { // ����ǰ�����ƫ������������
			drawTop.setAngleSpeed(drawTop.getAngleSpeed() + angleSpeedOffset);
		}

		if (drawTop.getAngleSpeed() + downAngleSpeedOffset < BasicTop.MAX_ANGLE_SPEED) {
			drawTop.setAngleSpeed(drawTop.getAngleSpeed()
					+ downAngleSpeedOffset);
		}

		if (Math.abs(drawTop.getxMoveSpeed() + xMoveSpeedOffset) < BasicTop.X_MAX_MOVE_SPEED) {
			drawTop.setxMoveSpeed(drawTop.getxMoveSpeed() + xMoveSpeedOffset);
		}

		if (Math.abs(drawTop.getyMoveSpeed() + yMoveSpeedOffset) < BasicTop.Y_MAX_MOVE_SPEED) {
			drawTop.setyMoveSpeed(drawTop.getyMoveSpeed() + yMoveSpeedOffset);
		}

		if (drawTop.getBasicPoint().x + xMoveOffset > -Constant.LOGIC_WIDTH / 2
				&& drawTop.getBasicPoint().x + xMoveOffset < Constant.LOGIC_WIDTH / 2) {
			drawTop.setBasicPoint(new Point(drawTop.getBasicPoint().x
					+ xMoveOffset, drawTop.getBasicPoint().y));
		}

		if (drawTop.getBasicPoint().y + yMoveOffset > -Constant.LOGIC_HEIGHT / 2
				&& drawTop.getBasicPoint().y + yMoveOffset < Constant.LOGIC_HEIGHT / 2) {
			drawTop.setBasicPoint(new Point(drawTop.getBasicPoint().x, drawTop
					.getBasicPoint().y + yMoveOffset));
		}

	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public boolean isCollideFlag() {
		return collideFlag;
	}

	public void setCollideFlag(boolean collideFlag) {
		this.collideFlag = collideFlag;
	}

	public int getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}

	public int getCurrentGoal() {
		return currentGoal;
	}

	public void setCurrentGoal(int currentGoal) {
		this.currentGoal = currentGoal;
	}

	public boolean isCrackFlag() {
		return crackFlag;
	}

	public void setCrackFlag(boolean crackFlag) {
		this.crackFlag = crackFlag;
	}

}
