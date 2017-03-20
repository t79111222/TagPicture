package yiting.com.tagpictureexample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yiting.tagpicture.TagPictureView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TagPictureView tagPictureView = (TagPictureView)findViewById(R.id.tagpictureview);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.tag);
        tagPictureView.setPicture(icon);
    }
}
