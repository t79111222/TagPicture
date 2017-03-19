package com.yiting.tagpicture;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


/**
 * Created by Tina Huang on 2017/3/17.
 */

public class DefTagPictureAdapter implements TagPictureAdapter {

    private Context mContext;
    private EditText mEditText;

    public DefTagPictureAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public View getTagEditView(TagInfo tagInfo) {
        if(null == mEditText){
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
            View view = layoutInflater.inflate(R.layout.tag_edit_text, null);
            mEditText = (EditText) view.findViewById(R.id.tagEdittext);
            mEditText.setTextColor(TagViewConfig.getTagViewConfig(mContext).textColor);
        }

        mEditText.requestFocus();
        mEditText.setFocusableInTouchMode(true);
        mEditText.setText(null == tagInfo ? "" : tagInfo.tagString);

        return mEditText;
    }

    @Override
    public String getTagStringOnEditComplete(View editView) {
        String s = null;
        if(mEditText != null){
            s = mEditText.getText().toString();
        }
        return s;
    }

    @Override
    public Object getCustomInfoOnEditComplete(View editView) {
        return null;
    }

}
