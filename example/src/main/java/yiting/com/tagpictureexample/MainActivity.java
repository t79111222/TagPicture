package yiting.com.tagpictureexample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yiting.tagpicture.TagInfo;
import com.yiting.tagpicture.TagPictureView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TagPictureView tagPictureView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tagPictureView = (TagPictureView)findViewById(R.id.tagpictureview);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.tag);
        tagPictureView.setPicture(icon);

        ArrayList<TagInfo>  tagInfos = (ArrayList<TagInfo> )getLastCustomNonConfigurationInstance();
        if(null != tagInfos ) {
            for (TagInfo info : tagInfos) {
                tagPictureView.addTag(info.atBitmapCoordinate, info.tagString, info.customInfo);
            }
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return tagPictureView.getTagInfos();
    }
}
