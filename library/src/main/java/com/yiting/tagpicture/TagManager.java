package com.yiting.tagpicture;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tina Huang on 2017/3/17.
 */

public class TagManager {
	private HashMap<Integer, TagInfo> mTagInfoMap = new HashMap<Integer, TagInfo>();
	private int lastId = 0;
	
	public int addTagInfo(Coordinate bitmapCoordinate, String tagString, TagView tagViw, Object customInfo){
		int id = getNewId();
		tagViw.setTagId(id);
		TagInfo tagInfo = new TagInfo(bitmapCoordinate, tagString, tagViw, customInfo);
		mTagInfoMap.put(id, tagInfo);
		return id;
	}

	public void updateTagInfo(int id, String tagString, Object customInfo){
		TagInfo tagInfo = getTagInfo(id);

		if(null == tagInfo) return;

		tagInfo.tagView.textView.setText(tagString);
		tagInfo.tagString = tagString;
		tagInfo.customInfo = customInfo;
		mTagInfoMap.put(id, tagInfo);
	}
	
	private int getNewId(){
		lastId++;
		return lastId;
	}
	
	public boolean removeTagInfo(int id){
		TagInfo remove = mTagInfoMap.remove(id);
		return remove != null ? true : false;
	}
	
	public ArrayList<TagInfo> getTagInfos(){
		return new ArrayList<>(mTagInfoMap.values());
	}
	
	public TagInfo getTagInfo(int id){
		return mTagInfoMap.get(id);
	}
	
	public void clear(){
		mTagInfoMap.clear();
	}
	
	public int count(){
		return mTagInfoMap.size();
	}
}
