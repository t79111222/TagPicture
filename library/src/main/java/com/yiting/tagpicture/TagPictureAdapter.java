package com.yiting.tagpicture;

import android.view.View;

/**
 * Created by Tina Huang on 2017/3/17.
 */

public interface TagPictureAdapter {


    View getTagEditView(TagInfo tagInfo);

    String getTagStringOnEditComplete(View editView);

    /**
     * Called when tag edit finish, gets a custom information that save into TagInfo.
     * @param editView
     * @return custom object
     */
    Object getCustomInfoOnEditComplete(View editView);

}
