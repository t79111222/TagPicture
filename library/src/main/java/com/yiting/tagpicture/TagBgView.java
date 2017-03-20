package com.yiting.tagpicture;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Tina Huang on 2017/3/17.
 */

public class TagBgView extends RelativeLayout{
    public static final int NO_ID = -1;
    private int mId = NO_ID;
    private RelativeLayout mBody;
    private RelativeLayout mContents;
    private ImageView mFlag;
    private Integer mFlagViewMarginLeft =  null;
    private OnGlobalListener mOnGlobalListener;
    private TagViewConfig mConf;
    public TagBgView(Context context) {
        super(context);
        init(context);
    }

    public TagBgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TagBgView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mConf = TagViewConfig.getTagViewConfig(context);

        this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        mBody = new RelativeLayout(getContext());
//		mBody.setBackgroundDrawable(new RoundDrawable());
        mBody.setBackgroundResource(R.drawable.round_s_a);
        mBody.setId(R.id.tagview_body);
        LayoutParams bodyLP = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        bodyLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        bodyLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
        bodyLP.topMargin = mConf.flagViewHeight - mConf.drawPaintSize;
        this.addView(mBody, bodyLP);

        mContents = new RelativeLayout(getContext());
        LayoutParams contentsLP = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mContents.setPadding(mConf.contentsPaddingLeft, mConf.contentsPaddingTop, mConf.contentsPaddingRight, mConf.contentsPaddingBotton);
        mContents.setId(R.id.tagview_contents);
        mBody.addView(mContents, contentsLP);

        mFlag = new ImageView(getContext());
        mFlag.setImageResource(R.drawable.flag_a2);
        mFlag.setScaleType(ImageView.ScaleType.FIT_XY);
        mFlag.setId(R.id.tagview_flag);
        LayoutParams flagViewLP = new LayoutParams(mConf.flagViewWidth, mConf.flagViewHeight);
        flagViewLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        flagViewLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
        addView(mFlag, flagViewLP);

        if(mFlagViewMarginLeft != null){
            setFlagViewMarginLeft(mFlagViewMarginLeft.intValue());
        }


        ViewTreeObserver mViewTreeObserver = mContents.getViewTreeObserver();
        mViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if(mOnGlobalListener != null)
                    mOnGlobalListener.OnCallback(TagBgView.this);
            }
        });

    }

    public RelativeLayout getBody() {
        return mBody;
    }

    public RelativeLayout getContents() {
        return mContents;
    }

    public ImageView getFlagView() {
        return mFlag;
    }

    public void setFlagViewMarginLeft(int left){
        mFlagViewMarginLeft = Integer.valueOf(left);
        if(mFlag != null){
            LayoutParams flagViewLP =  (LayoutParams) mFlag.getLayoutParams();
            flagViewLP.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
            flagViewLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            flagViewLP.leftMargin = left;
            mFlag.setLayoutParams(flagViewLP);
        }
    }

    public void setFlagViewInCenter(){
        if(mFlag != null){
            LayoutParams flagViewLP = new LayoutParams(mConf.flagViewWidth, mConf.flagViewHeight);
            flagViewLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            flagViewLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mFlag.setLayoutParams(flagViewLP);
        }
    }

    public void setOnGlobalListener(OnGlobalListener mOnGlobalListener) {
        this.mOnGlobalListener = mOnGlobalListener;
    }

    public interface OnGlobalListener{
        void OnCallback(TagBgView view);
    }

    public void setTagId(int mId) {
        this.mId = mId;
    }

    public int getTagId() {
        return mId;
    }
}
