package com.yiting.tagpicture;


import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tina Huang on 2017/3/17.
 */

public class TagPictureView extends FrameLayout{
	private String TAG = "TagPictureView";
	
	public static final int NO_ID = -1;
	public static final int DELETE_ID = -2;
	
	private InputMethodManager mInputMethodManager;
	private TagManager mTagManager;
	private TagBgView mTagEditView;
	private ImageView mDelete;
	private ImageView mImageView;
	private FrameLayout mTagFrameLayout;
	private Bitmap mBitmap;

	private TagViewConfig mConfig;
	private boolean isEdit = false, isOnAni = false;
	private int moveTagX,moveTagY;
	private int mWidth, mHeight, mImageW, mImageH;
	private float mImageViewScale = 1f;
	private Coordinate mEditTagCoordinateAtBitmap;
	
	private boolean mIsEnable = true;
	
	private int mEditId = NO_ID;

    private TagPictureAdapter mAdapter;
    private boolean mIsTouchOutsideEditComplete = true;
	private OnDeleteListener mOnDeleteListener;
	private OnEditListener mOnEditListener;

	public TagPictureView(Context context) {
		super(context);
		init(context);
	}
	
	public TagPictureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public TagPictureView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mTagFrameLayout.setVisibility(INVISIBLE);
		mHandler.sendEmptyMessageDelayed(0, 1000);
	}

	private Handler mHandler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			setImageSize();
			ArrayList<TagInfo> tagInfos = mTagManager.getTagInfos();
			for(TagInfo info : tagInfos){
				int tagViewWidth = getTagViewWidth(info.tagView.textView);
				setTagSeat(info.tagView, info.atBitmapCoordinate, tagViewWidth);
			}
			mTagFrameLayout.setVisibility(VISIBLE);
		};
	};

	private void init(Context context){
		mTagManager = new TagManager();
		mConfig = TagViewConfig.getTagViewConfig(context);
		mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		mImageView = new ImageView(context);
		addView(mImageView);
		mImageView.setOnTouchListener(oTouch);
		this.setOnTouchListener(oTouch);
		
		mTagFrameLayout = new FrameLayout(getContext());
		mTagFrameLayout.setDrawingCacheEnabled(true);
		addView(mTagFrameLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

    public void setAdapter(TagPictureAdapter adapter) {
        this.mAdapter = adapter;
    }

    private TagPictureAdapter getAdapter(){
        if(null == mAdapter){
            mAdapter = new DefTagPictureAdapter(getContext());
        }
        return mAdapter;
    }
	
	public void setPicture(Bitmap bitmap){
		Log.i(TAG, this.getWidth()+","+this.getHeight());
		if(null != mBitmap){
			mBitmap.recycle();
			System.gc();
		}
		mBitmap = bitmap;
		mImageView.setImageBitmap(mBitmap);
		setImageSize();
		mTagManager.clear();
		mTagFrameLayout.removeAllViews();
	}

	/**
	 *
	 * @param bitmapCoordinate
	 * @param tagString
	 * @param customInfo
	 * @return tag id
	 */
	public int addTag(Coordinate bitmapCoordinate, String tagString, Object customInfo){
		if(null == mBitmap){
			return NO_ID;
		}
		if(TextUtils.isEmpty(tagString)){//tag內容為空，不顯示
			return NO_ID;
		}
		TagView tagView = putTagView(bitmapCoordinate, tagString);
		return mTagManager.addTagInfo(bitmapCoordinate, tagString, tagView, customInfo);
	}

	/**
	 * 旋轉將會造成原有Tag被清除
	 */
	public void rotate90(){
		int w = mBitmap.getWidth();
		int h = mBitmap.getHeight();

		// Setting post rotate to 90
		Matrix mtx = new Matrix();
		mtx.postRotate(90);
		// Rotating Bitmap
		Bitmap rotatedBMP = Bitmap.createBitmap(mBitmap, 0, 0, w, h, mtx, true);

		ArrayList<TagInfo> tagInfos = mTagManager.getTagInfos();

		setPicture(rotatedBMP);//原有tag被清空了

		int newWidth = mBitmap.getWidth();
		for(TagInfo info : tagInfos){
			int newX = newWidth - info.atBitmapCoordinate.y;
			int newY = info.atBitmapCoordinate.x;
			Coordinate rotateAtBitmapCoordinate = new Coordinate(newX, newY);
			addTag(rotateAtBitmapCoordinate, info.tagString, info.customInfo);
		}
	}

	public ArrayList<TagInfo> getTagInfos(){
		return mTagManager.getTagInfos();
	}

	public void setEditCompleteOnTouchOutside(boolean editComplete){
        this.mIsTouchOutsideEditComplete = editComplete;
    }
	
	public boolean hadPicture(){
		return mBitmap != null;
	}


	public Bitmap getTagPicture() {
		Bitmap bitmap = mBitmap.copy(mBitmap.getConfig(), true);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap tagBitmap = mTagFrameLayout.getDrawingCache();
		int tagL = (mWidth - mImageW) / 2;
		int tagT = (mHeight - mImageH) / 2;
		int tagR = tagL + mImageW;
		int tagB = tagT + mImageH;
		Rect src = new Rect(tagL, tagT, tagR, tagB);
		Log.i(TAG, "src " + src.toString());
		int bitmapW = bitmap.getWidth();
		int bitmapH = bitmap.getHeight();
		Rect dst = new Rect(0, 0, bitmapW, bitmapH);
		Log.i(TAG, "dst " + dst.toString());

		canvas.drawBitmap(tagBitmap, src, dst, paint);
		return bitmap;
	}
	
	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = w;
		mHeight = h;
		setImageSize();
		moveTagX = w / 2;
		moveTagY = h / 5;
		if(null != mTagEditView){
			LayoutParams lp = (LayoutParams) mTagEditView.getLayoutParams();
			lp.topMargin = moveTagY;
			mTagEditView.setLayoutParams(lp);
		}
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		mIsEnable = enabled;
		super.setEnabled(enabled);
	}


	public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
		this.mOnDeleteListener = onDeleteListener;
	}

	public void setOnEditListener(OnEditListener onEditListener) {
		this.mOnEditListener = onEditListener;
	}

	private OnTouchListener oTouch = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(mBitmap == null || !mIsEnable){
				return false;
			}
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				if(!isOnAni){
					if(!isEdit && v == mImageView){
						Log.i(TAG, "on down, x="+event.getX() + " y="+event.getY());
						showEditView((int)event.getX(), (int)event.getY(), NO_ID);
					}else if(isEdit){
						if(mIsTouchOutsideEditComplete){
							if(null != mOnEditListener
									&& !mOnEditListener.onEditComplete(mTagEditView.getTagId())){
								return true;
							}
							dismissEditView();
						}
					}
				}
			return true;
			}
			return false;
		}
	};
	
	public void dismissEditView(){
		if(isEdit){
			int left =(mWidth - mImageW) / 2;
			int top =(mHeight - mImageH) / 2;
			isEdit = false;
			imageMove(left, top);
		}
	}
	
	public boolean isEdit(){
		return isEdit;
	}
	
	public int tagCount(){
		return mTagManager.count();
	}
	
//	public boolean dismissEditViewNoAni(){
//		if(isEdit){
//			int left =(mWidth - mImageW) / 2;
//			int top =(mHeight - mImageH) / 2;
//			isEdit = false;
//			if(mEditText != null){
//				mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
//			}
//			mTagEditView.setVisibility(INVISIBLE);
//			setImageSeat(left, top);
//			editToSave();
//			mTagFrameLayout.setVisibility(VISIBLE);
//			return true;
//		}
//		return false;
//	}
	
	private void showEditView(int x , int y, int id){
		Coordinate imageClick = new Coordinate(x, y);
		mEditTagCoordinateAtBitmap = transformBitmapCoordinate(imageClick);
		isEdit = true;
		Coordinate imageViewCoordinate = getImageViewMoveCoordinate(imageClick);
		mEditId = id;
		imageMove(imageViewCoordinate.x, imageViewCoordinate.y);
		//EditView將於圖片移動動畫結束顯示
	}
	
	private void setEditTagView(int id){
		if(mTagEditView == null){
			initEditTagView();
		}

        TagInfo info = null;
        if(id != NO_ID){
            info = mTagManager.getTagInfo(id);
        }

        View tagEditView = getAdapter().getTagEditView(info);

        mTagEditView.getContents().removeAllViews();
        mTagEditView.getContents().addView(tagEditView);
        mTagEditView.setVisibility(VISIBLE);
		mTagEditView.setTagId(id);

		if(null != mOnEditListener){
			mOnEditListener.onEdit(id);
		}
	}
	
	
	
	private void imageMove(final int left, final int top){
		 Animation am = new TranslateAnimation(0, left - mImageView.getLeft(), 0, top - mImageView.getTop());
		 am.setDuration(200);
		 am.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				if(!isEdit){
                    //TODO: 確認keyboard是否有縮下去
                    View focusedChild  = getFocusedChild();
					if(focusedChild != null){
						mInputMethodManager.hideSoftInputFromWindow(focusedChild.getWindowToken(), 0);
					}
					mTagEditView.setVisibility(INVISIBLE);
				}else{
					mTagFrameLayout.setVisibility(INVISIBLE);
				}
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				mImageView.clearAnimation();
				
				setImageSeat(left, top);
				if(isEdit){
                    setEditTagView(mEditId);
				}else{
					editToSave();
					mTagFrameLayout.setVisibility(VISIBLE);
				}
				isOnAni = false;
			}
		});
		 isOnAni = true;
		 mImageView.startAnimation(am);

	}
	
	private void editToSave(){
		int id = mTagEditView.getTagId();
		
		if(id == DELETE_ID){
			return;
		}

        TagPictureAdapter adapter = getAdapter();
        View editView = mTagEditView.getContents();

		String tagS = adapter.getTagStringOnEditComplete(editView);
        if(TextUtils.isEmpty(tagS)){//tag內容為空，不顯示
            if(id != NO_ID){
                removeTag(id);
            }
            return;
        }

        Object customInfo = adapter.getCustomInfoOnEditComplete(editView);
        if(id == NO_ID){
			addTag(mEditTagCoordinateAtBitmap, tagS, customInfo);
        }else{
            mTagManager.updateTagInfo(id, tagS, customInfo);
        }
	}
	
	public void deleteTag(int id){
		TagInfo info = mTagManager.getTagInfo(id);
		if(isEdit && id == mTagEditView.getTagId()){
			deleteEditingTag();
			return;
		}
		removeTag(id);
		if(null != mOnDeleteListener) mOnDeleteListener.onDeleted(info);
	}

	private void removeTag(int id){
		TagInfo info = mTagManager.getTagInfo(id);
		mTagFrameLayout.removeView(info.tagView);
		mTagManager.removeTagInfo(id);
	}


	public void deleteEditingTag(){
		if(isEdit){
			int id = mTagEditView.getTagId();
			TagInfo info = mTagManager.getTagInfo(id);
			if (id != NO_ID) {
				removeTag(id);
			}
			mTagEditView.setTagId(DELETE_ID);
			dismissEditView();
			if(null != mOnDeleteListener) mOnDeleteListener.onDeleted(info);
		}
	}
	
	private void setImageSeat(int left, int top){
		LayoutParams lp = (LayoutParams) mImageView.getLayoutParams();
		lp.leftMargin = left;
		lp.topMargin = top;
		mImageView.setLayoutParams(lp);
	}
	
	public void setImageSize(){
		if(mBitmap == null || mWidth == 0 || mHeight == 0){
			return;
		}
		int bitmapW = mBitmap.getWidth();
		int bitmapH = mBitmap.getHeight();
		float scaleW = (float)mWidth / (float)bitmapW;
		float scaleH = (float)mHeight / (float)bitmapH;
		mImageViewScale = scaleW < scaleH ? scaleW : scaleH;
		LayoutParams lp = (LayoutParams) mImageView.getLayoutParams();
		mImageW = (int) ((float)bitmapW * mImageViewScale);
		mImageH = (int) ((float)bitmapH * mImageViewScale);
		Log.i(TAG, "mImageW:"+mImageW+" mImageH"+mImageH);
		lp.width = mImageW;
		lp.height = mImageH;
		lp.leftMargin = (mWidth - lp.width) / 2;
		lp.topMargin = (mHeight - lp.height) / 2;
		mImageView.setLayoutParams(lp);
	}
	

	private void initEditTagView(){
		mTagEditView = new TagView(getContext());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp.topMargin = moveTagY;
		this.addView(mTagEditView, lp);
		
		mDelete = new ImageView(getContext());
		mDelete.setImageResource(R.drawable.deleted);
		mDelete.setPadding(mConfig.deletePadding, mConfig.deletePadding, mConfig.deletePadding, mConfig.deletePadding);
		RelativeLayout.LayoutParams deleteLP = new RelativeLayout.LayoutParams(mConfig.deleteWidth, mConfig.deleteHeight);
		deleteLP.addRule(RelativeLayout.ALIGN_TOP, mTagEditView.getBody().getId());
		deleteLP.addRule(RelativeLayout.ALIGN_RIGHT, mTagEditView.getBody().getId());
		deleteLP.setMargins(0, -mConfig.deletePadding, -(mConfig.deleteWidth / 2), 0);
		mTagEditView.addView(mDelete, deleteLP);
		mDelete.setOnClickListener(deleteOnClick);
	}
	
	
	private OnClickListener deleteOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = mTagEditView.getTagId();
			Log.i(TAG, "delete onClick id =" + id);
			if(null != mOnDeleteListener && !mOnDeleteListener.onClickTagDelete(id)){
				return; //can delete
			}
			if (id != NO_ID) {
				removeTag(id);
			}
			mTagEditView.setTagId(DELETE_ID);
			dismissEditView();
		}
	};
	
	private Coordinate getImageViewMoveCoordinate(Coordinate imageClickCoordinate){
		int moveL = moveTagX - imageClickCoordinate.x;
		int moveT = moveTagY - imageClickCoordinate.y;
		Log.i(TAG, "moveL : "+moveTagX+" - "+imageClickCoordinate.x+" = "+moveL);
		Log.i(TAG, "moveT : "+moveTagY+" - "+imageClickCoordinate.y+" = "+moveT);
//		int left = mImageView.getLeft() + moveL;
//		int top = mImageView.getTop() + moveT;
		return new Coordinate(moveL, moveT);
	}

	
	private TagView putTagView(Coordinate mEditTagCoordinateAtBitmap,
                               String tagS) {
        TagView tagView = new TagView(getContext());
		tagView.textView.setText(tagS);
		int tagViewWidth = getTagViewWidth(tagView.textView);
		mTagFrameLayout.addView(tagView);
		setTagSeat(tagView, mEditTagCoordinateAtBitmap, tagViewWidth);
		tagView.setOnClickListener(tagOnClick);
		tagView.setOnGlobalListener(tagOnGlobal);
        return tagView;
	}
	
	
	private void setTagSeat(TagBgView tagView, Coordinate coordinateAtBitmap, int tagViewWidth) {
		Coordinate coordinateAtTagPictureView = transformThisViewCoordinate(
				transformImageViewCoordinate(coordinateAtBitmap));
		int halfFlagViewWidth = mConfig.flagViewWidth / 2;
		
		Integer flagLeft = null;
		//tag view will set outside
		if(coordinateAtTagPictureView.x < tagViewWidth / 2){
			flagLeft = Integer.valueOf(coordinateAtTagPictureView.x - halfFlagViewWidth);
		}else if(coordinateAtTagPictureView.x > (mWidth - tagViewWidth / 2)){
			flagLeft = Integer.valueOf(tagViewWidth - (mWidth - coordinateAtTagPictureView.x) - halfFlagViewWidth);
		}
		
		int tagViewLeft;
		if(flagLeft != null){
			tagView.setFlagViewMarginLeft(flagLeft.intValue());
			tagViewLeft = coordinateAtTagPictureView.x - flagLeft.intValue() - halfFlagViewWidth;
		}else{
			tagViewLeft = coordinateAtTagPictureView.x - tagViewWidth / 2;
		}

		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.leftMargin = tagViewLeft;
		lp.topMargin = coordinateAtTagPictureView.y;
		tagView.setLayoutParams(lp);
	}
	
	private OnClickListener tagOnClick = new OnClickListener() {
	
		@Override
		public void onClick(View v) {
			int id = ((TagView) v).getTagId();
			Log.i(TAG, "Tag onClick id =" + id);
			TagInfo info = mTagManager.getTagInfo(id);
			if (info != null) {
				Coordinate bitmapC = info.atBitmapCoordinate;
				Coordinate imageC = transformImageViewCoordinate(bitmapC);
				showEditView(imageC.x, imageC.y, id);
			}
		}
	};
	
	private TagBgView.OnGlobalListener tagOnGlobal = new TagBgView.OnGlobalListener() {
		
		@Override
		public void OnCallback(TagBgView view) {
			if (view.getWidth() <= 0) {
				return;
			}
			int width = view.getContents().getWidth()
					+ mConfig.contentsPaddingLeft
					+ mConfig.contentsPaddingRight;
			if(view.getTag() instanceof Integer){
				Integer w = (Integer)view.getTag();
				if(width == w.intValue()){
					return;
				}
			}
			int id =  view.getTagId();
			Log.i(TAG, "Tag OnGlobal id =" + id);
			TagInfo info = mTagManager.getTagInfo(id);
			if (info != null) {
				Coordinate bitmapC = info.atBitmapCoordinate;
				setTagSeat(view, bitmapC, width);
				view.setTag(width);
			}

		}
	};
	
	private Coordinate transformBitmapCoordinate(Coordinate imageViewCoordinate){
		return new Coordinate((int)((float)imageViewCoordinate.x / mImageViewScale)
				, (int)((float)imageViewCoordinate.y / mImageViewScale));
	}
	
	private Coordinate transformImageViewCoordinate(Coordinate bitmapCoordinate){
		return new Coordinate((int)((float)bitmapCoordinate.x * mImageViewScale)
				, (int)((float)bitmapCoordinate.y * mImageViewScale));
	}
	
	private Coordinate transformThisViewCoordinate(Coordinate imageViewCoordinate){
		return new Coordinate(imageViewCoordinate.x + (mWidth - mImageW) / 2
				, imageViewCoordinate.y + (mHeight - mImageH) / 2);
	}
	
	private int getTagViewWidth(TextView textView){
		int textViewWidth = (int)textView.getPaint().measureText(textView.getText().toString());
		int tagViwWidth = textViewWidth + mConfig.contentsPaddingLeft + mConfig.contentsPaddingRight;
		return tagViwWidth;
	}


	public interface OnDeleteListener{

		/**
		 * Called when delete button has been clicked.
		 * @param tagId A tag id that want delete. New tag id is {@link #NO_ID}
		 * @return True if can delete the tag, false otherwise.
		 */
		boolean onClickTagDelete(int tagId);

		/**
		 * Called when a tag has been deleted.
		 * @param info The tag information that was deleted. The TagInfo is null,when deleted new tag.
		 */
		void onDeleted(TagInfo info);

	}

	public interface OnEditListener{

		/**
		 * Called when tag do editing.
		 * @param tagId A tag id that tag was editing. New tag id is {@link #NO_ID}
		 */
		void onEdit(int tagId);

		/**
		 * Called when the editing tag has been clicked tag outside.
		 * @param tagId A tag id that tag was editing. New tag id is {@link #NO_ID}
		 * @return True if can finish edit, false otherwise.
		 */
		boolean onEditComplete(int tagId);

	}


}
