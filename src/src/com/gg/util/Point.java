package com.gg.util;

/*		��ʾ3D�������߼���		*/
public class Point {

	public float x;
	public float y;
	public float z;
	
	public Point(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point(float x, float y){
		this(x,y,0f);
	}
}
