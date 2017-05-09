package com.yumi.android.sdk.ads.beans;

public class ClickArea
{

	private int showAreaWidth;
	private int showAreaHeight;
	private float clickX;
	private float clickY;

	public ClickArea(int showAreaWidth, int showAreaHeight, float clickX, float clickY)
	{
		this.showAreaWidth = showAreaWidth;
		this.showAreaHeight = showAreaHeight;
		this.clickX = clickX;
		this.clickY = clickY;
	}

	public int getShowAreaWidth()
	{
		return showAreaWidth;
	}

	public int getShowAreaHeight()
	{
		return showAreaHeight;
	}

	public float getClickX()
	{
		return clickX;
	}

	public float getClickY()
	{
		return clickY;
	}
	
	


}
