package com.yiting.tagpicture;

import android.content.Context;
import android.graphics.Color;

/**
 * Created by Tina Huang on 2017/3/17.
 */

public class TagViewConfig {
	public static int drawPaintSize;
//	public int textSize = 20;
//	public int editMaxLengh = 10;
	public int lineColor = Color.WHITE;
	public int backgroundColor = Color.BLACK;
	public int textColor = Color.WHITE;
	public static int flagViewWidth;
	public static int flagViewHeight;
	public static int deleteWidth;
	public static int deleteHeight;
	public static int deletePadding;
	public int contentsPaddingLeft = 0;
	public int contentsPaddingRight = 0;
	public int contentsPaddingTop = 0;
	public int contentsPaddingBotton = 0;
	private static TagViewConfig mTagViewConfig;
	public TagViewConfig() {
		
	}
	
	public static TagViewConfig getTagViewConfig(Context context){
		if(mTagViewConfig == null){
			mTagViewConfig = new TagViewConfig();
			float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
			flagViewWidth = (int)(8f * scale);
			flagViewHeight = (int)(5f * scale);
			drawPaintSize = (int)(1f * scale);
			deleteWidth = (int)(25f * scale);
			deletePadding = (int)(5f * scale);
			deleteHeight = deleteWidth;
		}
		return mTagViewConfig;
	}
	
}
