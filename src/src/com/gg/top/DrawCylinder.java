package com.gg.top;

import java.nio.*;
import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import com.gg.util.Constant;

/*		���ڻ�����ά�����ݣ����е�Բ�����֣�		*/
public class DrawCylinder extends BasicTop {
	int textureId; // ����id
	int vertexNumber; // ���ж��������ܺ�
	private FloatBuffer vertexBuffer; // �������껺��
	private FloatBuffer normalBuffer; // ����������
	private FloatBuffer textureBuffer; // ������
	ArrayList<Float> val = new ArrayList<Float>(); // �������б�
	ArrayList<Float> ial = new ArrayList<Float>(); // ����������б�

	float degreeSpan = 12f; // Բ�ػ�ÿһ�ݵĶ�����С
	int degreeSpanNumber = (int) (360.0f / degreeSpan); // Բ�ػ�����


	float normalconeHeight = (coneHeight * radius * radius)
			/ (coneHeight * coneHeight + radius * radius); // ������������Բ����ĸ߶�
	float normalR = (coneHeight * coneHeight * radius)
			/ (coneHeight * coneHeight + radius * radius); // ������������Բ����İ뾶


//	public float xAngle; // ��x����ת�ĽǶ�
//	public float yAngle; // ��y����ת�ĽǶ�
//	public float zAngle; // ��z����ת�ĽǶ�
	//public float zAutoAngle; // �Զ���z����ת�ĽǶ�

	public DrawCylinder(int textureId) {
		this.textureId = textureId;

		generateData();
	}
	
	public void generateData(){
		val.clear();
		ial.clear();
		
		for (float circle_degree = 360.0f; circle_degree > 0.0f; circle_degree -= degreeSpan)// ѭ����
		{

//			// ��Բ׶�ĵ�ͷ�����
//			for (int j = 0; j < coneCol; j++) // ѭ����
//			{
//				float currentR = j * spanConeRadius;// ��ǰ�����Բ�뾶
//				float currentconeHeight = j * spanConeHeight;// ��ǰ����ĸ߶�
//
//				float x1 = (float) (currentR * Math.cos(Math
//						.toRadians(circle_degree)));
//				float y1 = (float) (currentR * Math.sin(Math
//						.toRadians(circle_degree)));
//				float z1 = currentconeHeight;
//
//				float a1 = (float) (normalR * Math.cos(Math
//						.toRadians(circle_degree)));
//				float b1 = (float) (normalR * Math.sin(Math
//						.toRadians(circle_degree)));
//				float c1 = normalconeHeight;
//				float l1 = getVectorLength(a1, b1, c1);// ģ��
//				a1 = a1 / l1;// ���������
//				b1 = b1 / l1;
//				c1 = c1 / l1;
//
//				float x2 = (float) ((currentR + spanConeRadius) * Math.cos(Math
//						.toRadians(circle_degree)));
//				float y2 = (float) ((currentR + spanConeRadius) * Math.sin(Math
//						.toRadians(circle_degree)));
//				float z2 = currentconeHeight + spanConeHeight;
//
//				float a2 = (float) (normalR * Math.cos(Math
//						.toRadians(circle_degree)));
//				float b2 = (float) (normalR * Math.sin(Math
//						.toRadians(circle_degree)));
//				float c2 = normalconeHeight;
//
//				float l2 = getVectorLength(a2, b2, c2);// ģ��
//				a2 = a2 / l2;// ���������
//				b2 = b2 / l2;
//				c2 = c2 / l2;
//
//				float x3 = (float) ((currentR + spanConeRadius) * Math.cos(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float y3 = (float) ((currentR + spanConeRadius) * Math.sin(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float z3 = currentconeHeight + spanConeHeight;
//
//				float a3 = (float) (normalR * Math.cos(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float b3 = (float) (normalR * Math.sin(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float c3 = normalconeHeight;
//
//				float l3 = getVectorLength(a3, b3, c3);// ģ��
//				a3 = a3 / l3;// ���������
//				b3 = b3 / l3;
//				c3 = c3 / l3;
//
//				float x4 = (float) ((currentR) * Math.cos(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float y4 = (float) ((currentR) * Math.sin(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float z4 = currentconeHeight;
//
//				float a4 = (float) (normalR * Math.cos(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float b4 = (float) (normalR * Math.sin(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float c4 = normalconeHeight;
//
//				float l4 = getVectorLength(a4, b4, c4);// ģ��
//				a4 = a4 / l4;// ���������
//				b4 = b4 / l4;
//				c4 = c4 / l4;
//
//				val.add(x1);
//				val.add(y1);
//				val.add(z1);// ���������Σ���6�����������
//				val.add(x2);
//				val.add(y2);
//				val.add(z2);
//				val.add(x4);
//				val.add(y4);
//				val.add(z4);
//
//				val.add(x2);
//				val.add(y2);
//				val.add(z2);
//				val.add(x3);
//				val.add(y3);
//				val.add(z3);
//				val.add(x4);
//				val.add(y4);
//				val.add(z4);
//
//				ial.add(a1);
//				ial.add(b1);
//				ial.add(c1);// �����Ӧ�ķ�����
//				ial.add(a2);
//				ial.add(b2);
//				ial.add(c2);
//				ial.add(a4);
//				ial.add(b4);
//				ial.add(c4);
//
//				ial.add(a2);
//				ial.add(b2);
//				ial.add(c2);
//				ial.add(a3);
//				ial.add(b3);
//				ial.add(c3);
//				ial.add(a4);
//				ial.add(b4);
//				ial.add(c4);
//			}

			// ��Բ���ĵ�ͷ�����
			for (int j = 0; j < 1; j++)// ѭ����
			{
				float x1 = (float) (radius * Math.sin(Math
						.toRadians(circle_degree)));
				float y1 = (float) (radius * Math.cos(Math
						.toRadians(circle_degree)));
				float z1 = (float) (j * cylinderHeight + coneHeight);

				float a1 = (float) (normalR * Math.sin(Math
						.toRadians(circle_degree)));
				float b1 = (float) (normalR * Math.cos(Math
						.toRadians(circle_degree)));
				float c1 = 0;
				float l1 = getVectorLength(a1, b1, c1);// ģ��
				a1 = a1 / l1;// ���������
				b1 = b1 / l1;
				c1 = c1 / l1;

				float x2 = (float) (radius * Math.sin(Math
						.toRadians(circle_degree - degreeSpan)));
				float y2 = (float) (radius * Math.cos(Math
						.toRadians(circle_degree - degreeSpan)));// ��Ϊ������
				float z2 = (float) (j * cylinderHeight + coneHeight);

				float a2 = (float) (normalR * Math.sin(Math
						.toRadians(circle_degree - degreeSpan)));
				float b2 = (float) (normalR * Math.cos(Math
						.toRadians(circle_degree - degreeSpan)));
				float c2 = 0;
				float l2 = getVectorLength(a1, b1, c1);// ģ��
				a2 = a2 / l2;// ���������
				b2 = b2 / l2;
				c2 = c2 / l2;

				float x3 = (float) (radius * Math.sin(Math
						.toRadians(circle_degree - degreeSpan)));
				float y3 = (float) (radius * Math.cos(Math
						.toRadians(circle_degree - degreeSpan)));
				float z3 = (float) ((j + 1) * cylinderHeight + coneHeight);

				float a3 = (float) (normalR * Math.sin(Math
						.toRadians(circle_degree - degreeSpan)));
				float b3 = (float) (normalR * Math.cos(Math
						.toRadians(circle_degree - degreeSpan)));
				float c3 = 0;
				float l3 = getVectorLength(a1, b1, c1);// ģ��
				a3 = a3 / l3;// ���������
				b3 = b3 / l3;
				c3 = c3 / l3;

				float x4 = (float) (radius * Math.sin(Math
						.toRadians(circle_degree)));
				float y4 = (float) (radius * Math.cos(Math
						.toRadians(circle_degree)));
				float z4 = (float) ((j + 1) * cylinderHeight + coneHeight);

				float a4 = (float) (normalR * Math.sin(Math
						.toRadians(circle_degree)));
				float b4 = (float) (normalR * Math.cos(Math
						.toRadians(circle_degree)));
				float c4 = 0;
				float l4 = getVectorLength(a1, b1, c1);// ģ��
				a4 = a4 / l4;// ���������
				b4 = b4 / l4;
				c4 = c4 / l4;

				val.add(x1);
				val.add(y1);
				val.add(z1);// �����������Σ�����������
				val.add(x2);
				val.add(y2);
				val.add(z2);
				val.add(x4);
				val.add(y4);
				val.add(z4);

				val.add(x2);
				val.add(y2);
				val.add(z2);
				val.add(x3);
				val.add(y3);
				val.add(z3);
				val.add(x4);
				val.add(y4);
				val.add(z4);

				ial.add(x1);
				ial.add(y1);
				ial.add(z1);
				ial.add(x2);
				ial.add(y2);
				ial.add(z2);
				ial.add(x4);
				ial.add(y4);
				ial.add(z4);

				ial.add(x2);
				ial.add(y2);
				ial.add(z2);
				ial.add(x3);
				ial.add(y3);
				ial.add(z3);
				ial.add(x4);
				ial.add(y4);
				ial.add(z4);
			}

//			// ��Բ�ĵ�ͷ�����
//			float x1 = (float) ((radius) * Math.cos(Math
//					.toRadians(circle_degree)));
//			float y1 = (float) ((radius) * Math.sin(Math
//					.toRadians(circle_degree)));
//			float z1 = (float) (cylinderHeight + coneHeight);
//
//			float a1 = 0;
//			float b1 = 0;
//			float c1 = 1;
//			float l1 = getVectorLength(a1, b1, c1);// ģ��
//			a1 = a1 / l1;// ���������
//			b1 = b1 / l1;
//			c1 = c1 / l1;
//
//			float x2 = (float) ((radius) * Math.cos(Math
//					.toRadians(circle_degree + degreeSpan)));
//			float y2 = (float) ((radius) * Math.sin(Math
//					.toRadians(circle_degree + degreeSpan)));
//			float z2 = (float) (cylinderHeight + coneHeight);
//
//			float a2 = 0;
//			float b2 = 0;
//			float c2 = 1;
//			float l2 = getVectorLength(a2, b2, c2);// ģ��
//			a2 = a2 / l2;// ���������
//			b2 = b2 / l2;
//			c2 = c2 / l2;
//
//			val.add(x1);
//			val.add(y1);
//			val.add(z1);
//			val.add(x2);
//			val.add(y2);
//			val.add(z2);
//			val.add(0f);
//			val.add(0f);
//			val.add(cylinderHeight + coneHeight);
//
//			ial.add(x1);
//			ial.add(y1);
//			ial.add(z1);
//			ial.add(x2);
//			ial.add(y2);
//			ial.add(z2);
//			ial.add(0f);
//			ial.add(0f);
//			ial.add(1f);

		}

		vertexNumber = val.size() / 3;// ȷ����������

		// ����
		float[] vertexs = new float[vertexNumber * 3];
		for (int i = 0; i < vertexNumber * 3; i++) {
			vertexs[i] = val.get(i);
		}
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertexs.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertexs);
		vertexBuffer.position(0);

		// ������
		float[] normals = new float[vertexNumber * 3];
		for (int i = 0; i < vertexNumber * 3; i++) {
			normals[i] = ial.get(i);
		}
		ByteBuffer ibb = ByteBuffer.allocateDirect(normals.length * 4);
		ibb.order(ByteOrder.nativeOrder());
		normalBuffer = ibb.asFloatBuffer();
		normalBuffer.put(normals);
		normalBuffer.position(0);

		// ����
		float[] textures = generateTexCoor(degreeSpanNumber, 1);
		ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		textureBuffer = tbb.asFloatBuffer();
		textureBuffer.put(textures);
		textureBuffer.position(0);
	}


	public void drawSelf(GL10 gl) {


		gl.glTranslatef(basicPoint.x, basicPoint.y, 0);
		

		gl.glRotatef(-axleAngle, (float)Math.sin(axleAngleCount*Math.PI/180), -(float)Math.cos(axleAngleCount*Math.PI/180), 0);

		gl.glRotatef(-angleCount, 0, 0, 1);
		


		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);// �򿪶��㻺��
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);// ָ�����㻺��

		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);// �򿪷���������
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);// ָ������������

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vertexNumber);// ����ͼ��

		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);// �رջ���
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
	}

	// ��������񻯣��󳤶�
	public float getVectorLength(float x, float y, float z) {
		float pingfang = x * x + y * y + z * z;
		float length = (float) Math.sqrt(pingfang);
		return length;
	}

	   //�Զ��з����������������ķ���
    public float[] generateTexCoor(int bh, int bw)
    {
    	float[] result=new float[bh*6*2*bw]; 
    	float REPEAT=0;
    	float sizeh=1.0f/bh;//����
    	float sizew = 1.0f/bw;
    	int c=0;
    	for(int j=0;j<bw;j++)
    	{
    		for(int i=0;i<bh;i++){
    			//ÿ����һ�����Σ������������ι��ɣ��������㣬12����������
    			float t=i*sizeh;
    			float s = j*sizew;
    			
    			result[c++]=s;
    			result[c++]=t;
    		
    			result[c++]=s;
    			result[c++]=t+sizeh; 
    			
    			result[c++]=s+sizew+REPEAT;
    			result[c++]=t;
    			   			
    			result[c++]=s;
    			result[c++]=t+sizeh;
    			
    			result[c++]=s+sizew+REPEAT;
    			result[c++]=t+sizeh;   
    			
    			result[c++]=s+sizew+REPEAT;
    			result[c++]=t;
    		}
    	}
    	return result;
    }
	
	public int getTextureId() {
		return textureId;
	}

	public void setTextureId(int textureId) {
		this.textureId = textureId;
	}

	
	
}