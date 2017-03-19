package com.yiting.tagpicture;

/**
 * Created by Tina Huang on 2017/3/17.
 */

public class TagInfo {
	public Coordinate atBitmapCoordinate;
	public TagView tagView;
	public String tagString;
	public Object customInfo;
	
	public TagInfo(Coordinate bitmapCoordinate, String tagString, TagView tagView, Object customInfo){
		this.atBitmapCoordinate = bitmapCoordinate;
		this.tagString = tagString;
		this.tagView = tagView;
		this.customInfo = customInfo;
	}
}
