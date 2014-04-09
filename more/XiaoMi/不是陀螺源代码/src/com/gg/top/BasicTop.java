package com.gg.top;

import com.gg.util.*;

/*		��ʾ���ݵĻ���ģ��		*/
public class BasicTop {
	protected Point basicPoint = new Point(0f, 0f, 0f); // Բ׶��������xyz		
	protected float radius = 0.5f; // Բ׶��Բ����Բ�뾶
	protected float coneHeight = radius; // Բ׶��
	protected float coneAngle = (float) Math.toRadians(90); // Բ׶�ǣ�Ĭ��Ϊ90��
	protected float cylinderHeight = radius*2; // Բ����

	protected float angleCount = 0f; // ������ת���ۼ��ܽǶ�
	protected float angleSpeed = 20f; // ���ٶȣ�10������15������25�ܿ죬30���Ҽ��ޣ�40�Ϳ�ʼ��������
	protected float autoAngleAccelerate = -0.05f; // 0.05���15���е�����0.06���12��ͣ������������ת���ٶȣ�Ĭ��Ϊ��ֵ
	public final static float MAX_ANGLE_SPEED = 30f; // �����ٶ�	
	
	protected float axleAngleCount = 0f; // ������ת�ۼ��ܽǶ�	
	protected float axleAngleSpeed = 0f;// ������ת���ٶȣ�0.1ͦ���������棬0.2��ͦ���ˣ��ȽϺ�����0.25�����Ǽ���
	public final static float MAX_AXLE_ANGLE_SPEED = 15f; //

	protected float axleAngle = 0f; // �������,20�Ȳ��͸�����,30�ȱ�����
	public final float DEAD_AXLE_ANGLE = 13f; // ����������ǣ�����������Ӧ������Ϣ����Ĭ��Ϊ30��,15�����е�̫����
	
	protected float xShakeDistance = 0.002f; //x�������ݶ�����ƽ�ƾ��룬�����ɸ�,��������ʱ0.005̫���ԣ�0.002�ᶯ
	protected float yShakeDistance = 0.002f; //y�������ݶ�����ƽ�ƾ��룬�����ɸ�
	
	protected float xMoveSpeed = 0.0f; // x����ƽ���ٶ�,0.005�е�����
	protected float yMoveSpeed = 0.0f; // y����ƽ���ٶ�
	public final static float X_MAX_MOVE_SPEED = 0.015f; // x�������ƽ���ٶȵľ���ֵ��0.015���м䵽�ߴ��4��
	public final static float Y_MAX_MOVE_SPEED = 0.015f; // y�������ƽ���ٶȵľ���ֵ,0.015�Ļ�2��ײ
	protected float xAutoMoveAccelerate = 0.00005f; // x��������ƽ�Ƽ��ٶȣ�ֵΪ������0.0001�ܿ��ͣ,0.00005�򻬵�һ���һ��
	protected float yAutoMoveAccelerate = 0.00005f; // y��������ƽ�Ƽ��ٶ�

	public final static int PREPARE = 0; // ��ʼ��״̬
	public final static int ROTATING = 1; // ����ת��״̬
	public final static int FALLING = 2; // ���ڵ���״̬
	public final static int END = 3; // ���µ�״̬
	protected int state = PREPARE; // ����״̬

	
	public BasicTop() { // ȱʡ״̬�µĹ��캯��

	}
	

	//ת���Ƕȵ�������ֱ���ɽ��ٶȵ��Ӷ���
	public void rotate(){
		angleCount += angleSpeed;		
		if(angleCount>360){
			angleCount -= 360;
		}
	}
	
	//���ٶ��Զ����٣����Լ���
	public void autoAngleAccelerate(){
		if(angleSpeed+autoAngleAccelerate>0){
			angleSpeed += autoAngleAccelerate;
		}else{
			angleSpeed = 0f;
		}
	}
	
	
	//������z����ת���ܽǶȣ�ֱ���ɸý��ٶȵ��Ӷ���
	public void axleRotate(){
		axleAngleCount += axleAngleSpeed;	
		if(axleAngleCount>360){
			axleAngleCount -= 360;
		}
	}
	
	//ͨ������Ǽ����ת��Ľ��ٶȣ���sin��ģ��
	//�ȱ�ʾ0��1,Ȼ����0��PI/2,����ƽ�ƺ���Sin��һ��,Ȼ��������,���������ֵ(�õ���������)
	public void axleRotateAccelerate(){
		if(axleAngleSpeed<MAX_AXLE_ANGLE_SPEED){
			axleAngleSpeed = ((float)Math.sin(axleAngle/DEAD_AXLE_ANGLE*(Math.PI/2)))*MAX_AXLE_ANGLE_SPEED;
		}
	}
	

	
	//������б�Ƕ�����ת���ٶȾ���
	//�ȱ�ʾ0��1,Ȼ����0��PI/2,����ƽ��PI����Sin������,Ȼ������,����������½Ƕ�(�õ���������)���ٳ���1.2��֤���ֵ���������Ƕ�
	public void calculateAxleAngle(){
		axleAngle = (float)(Math.sin(angleSpeed/MAX_ANGLE_SPEED*(Math.PI/2)+Math.PI)+1)*DEAD_AXLE_ANGLE*1.2f;
	}
	
	
	//ֱ���𶯵�ĳ��ֵ
	public void shake(){
		if(basicPoint.x+xShakeDistance-radius>-Constant.LOGIC_WIDTH/2 && basicPoint.x+xShakeDistance+radius<Constant.LOGIC_WIDTH){
			basicPoint.x += xShakeDistance;
		}
		if(basicPoint.y+yShakeDistance-radius>-Constant.LOGIC_HEIGHT/2 && basicPoint.y+yShakeDistance+radius<Constant.LOGIC_HEIGHT){
			basicPoint.y += yShakeDistance;
		}		
	}
	
	public void move(){
		if(basicPoint.x+xMoveSpeed-radius>-Constant.LOGIC_WIDTH/2 && basicPoint.x+xMoveSpeed+radius<Constant.LOGIC_WIDTH/2){
			basicPoint.x += xMoveSpeed;
		}
//		else{
//			xMoveSpeed = -xMoveSpeed*2/3;
//			angleSpeed = angleSpeed*4/5;
//			
//			collideFlag = true;
//		}
		
		if(basicPoint.y+yMoveSpeed-radius>-Constant.LOGIC_HEIGHT/2 && basicPoint.y+yMoveSpeed+radius<Constant.LOGIC_HEIGHT/2){
			basicPoint.y += yMoveSpeed;
		}
//		else{
//			yMoveSpeed = -yMoveSpeed*2/3;
//			angleSpeed = angleSpeed*4/5;
//			
//			collideFlag = true;
//		}
	}

	
	
	public void autoMoveAccelerate(){
		if(xMoveSpeed>0 && xMoveSpeed-xAutoMoveAccelerate>0){
			xMoveSpeed -= xAutoMoveAccelerate;
		}else if(xMoveSpeed<0 && xMoveSpeed+xAutoMoveAccelerate<0){
			xMoveSpeed += xAutoMoveAccelerate;
		}
		
		if(yMoveSpeed>0 && yMoveSpeed-yAutoMoveAccelerate>0){
			yMoveSpeed -= yAutoMoveAccelerate;
		}else if(yMoveSpeed<0 && yMoveSpeed+yAutoMoveAccelerate<0){
			yMoveSpeed += yAutoMoveAccelerate;
		}
	}
	

	
	/* һϵ��get��set���� */
	public Point getBasicPoint() {
		return basicPoint;
	}


	public void setBasicPoint(Point basicPoint) {
		this.basicPoint = basicPoint;
	}


	public float getRadius() {
		return radius;
	}


	public void setRadius(float radius) {
		this.radius = radius;
		this.coneHeight = radius;
		this.cylinderHeight = radius*2;
	}


	public float getConeHeight() {
		return coneHeight;
	}


	public void setConeHeight(float coneHeight) {
		this.coneHeight = coneHeight;
	}


	public float getConeAngle() {
		return coneAngle;
	}


	public void setConeAngle(float coneAngle) {
		this.coneAngle = coneAngle;
	}


	public float getCylinderHeight() {
		return cylinderHeight;
	}


	public void setCylinderHeight(float cylinderHeight) {
		this.cylinderHeight = cylinderHeight;
	}



	public float getAxleAngle() {
		return axleAngle;
	}


	public void setAxleAngle(float axleAngle) {
		this.axleAngle = axleAngle;
	}


	public float getAxleAngleSpeed() {
		return axleAngleSpeed;
	}


	public void setAxleAngleSpeed(float axleAngleSpeed) {
		this.axleAngleSpeed = axleAngleSpeed;
	}


	public float getAxleAngleCount() {
		return axleAngleCount;
	}


	public void setAxleAngleCount(float axleAngleCount) {
		this.axleAngleCount = axleAngleCount;
	}


//	public float getAxleAngleDownSpeed() {
//		return axleAngleDownSpeed;
//	}
//
//
//	public void setAxleAngleDownSpeed(float axleAngleDownSpeed) {
//		this.axleAngleDownSpeed = axleAngleDownSpeed;
//	}



	public float getAngleSpeed() {
		return angleSpeed;
	}


	public void setAngleSpeed(float angleSpeed) {
		this.angleSpeed = angleSpeed;
	}


	public float getAngleCount() {
		return angleCount;
	}


	public void setAngleCount(float angleCount) {
		this.angleCount = angleCount;
	}


	public float getAutoAngleAccelerate() {
		return autoAngleAccelerate;
	}


	public void setAutoAngleAccelerate(float autoAngleAccelerate) {
		this.autoAngleAccelerate = autoAngleAccelerate;
	}


	public float getxMoveSpeed() {
		return xMoveSpeed;
	}


	public void setxMoveSpeed(float xMoveSpeed) {
		this.xMoveSpeed = xMoveSpeed;
	}


	public float getyMoveSpeed() {
		return yMoveSpeed;
	}


	public void setyMoveSpeed(float yMoveSpeed) {
		this.yMoveSpeed = yMoveSpeed;
	}





	public float getxAutoMoveAccelerate() {
		return xAutoMoveAccelerate;
	}


	public void setxAutoMoveAccelerate(float xAutoMoveAccelerate) {
		this.xAutoMoveAccelerate = xAutoMoveAccelerate;
	}


	public float getyAutoMoveAccelerate() {
		return yAutoMoveAccelerate;
	}


	public void setyAutoMoveAccelerate(float yAutoMoveAccelerate) {
		this.yAutoMoveAccelerate = yAutoMoveAccelerate;
	}


	public float getxShakeDistance() {
		return xShakeDistance;
	}


	public void setxShakeDistance(float xShakeDistance) {
		this.xShakeDistance = xShakeDistance;
	}


	public float getyShakeDistance() {
		return yShakeDistance;
	}


	public void setyShakeDistance(float yShakeDistance) {
		this.yShakeDistance = yShakeDistance;
	}


	public int getState() {
		return state;
	}


	public void setState(int state) {
		this.state = state;
	}




	public float getDEAD_AXLE_ANGLE() {
		return DEAD_AXLE_ANGLE;
	}





	public float getMAX_ANGLE_SPEED() {
		return MAX_ANGLE_SPEED;
	}




	public float getX_MAX_MOVE_SPEED() {
		return X_MAX_MOVE_SPEED;
	}




	public float getY_MAX_MOVE_SPEED() {
		return Y_MAX_MOVE_SPEED;
	}




	public float getMAX_AXLE_ANGLE_SPEED() {
		return MAX_AXLE_ANGLE_SPEED;
	}









	public int getPREPARE() {
		return PREPARE;
	}




	public int getROTATING() {
		return ROTATING;
	}




	public int getFALLING() {
		return FALLING;
	}




	public int getEND() {
		return END;
	}



}
