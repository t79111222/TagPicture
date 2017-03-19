package com.yiting.tagpicture;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Tina Huang on 2017/3/17.
 */

public class TagView extends TagBgView {

    public TextView textView;

    public TagView(Context context) {
        super(context);
        addTextView();
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextView();
    }

    public TagView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addTextView();
    }

    private void addTextView(){
        textView = new TextView(getContext());
        textView.setTextColor(TagViewConfig.getTagViewConfig(getContext()).textColor);
        textView.setId(R.id.tagview_text);
        getContents().addView(textView);
    }
}
