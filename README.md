# TagPicture 標記照片
[![build](https://img.shields.io/badge/build-1.0.0-brightgreen.svg?maxAge=2592000)](https://dl.bintray.com/t79111222/TagPicture)
[![license](https://img.shields.io/badge/license-Apache%202-blue.svg?maxAge=2592000)](https://github.com/t79111222/TagPicture/blob/master/LICENSE)



[![Tag Photo](https://play.google.com/intl/en_us/badges/images/badge_new.png)](https://play.google.com/store/apps/details?id=com.yiting.tagphoto)

![example_gif](/image/example_gif.gif)



## Import

Maven

    <dependency>
      <groupId>com.yiting</groupId>
      <artifactId>library</artifactId>
      <version>1.0.0</version>
      <type>pom</type>
    </dependency>

    
or Gradle

## 使用方法


於layout添加

    <com.yiting.tagpicture.TagPictureView
        android:id="@+id/tagpictureview"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
        
        
於Activity添加圖片

    public class MainActivity extends AppCompatActivity {
        TagPictureView tagPictureView;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            tagPictureView = (TagPictureView)findViewById(R.id.tagpictureview);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.tag);
            tagPictureView.setPicture(bitmap);
        }
    }
    
    
## 自訂義標籤編輯

實現TagPictureAdapter

    public class MyTagPictureAdapter implements TagPictureAdapter {
        private Context mContext;
        View mTagEditView;
        private EditText mEditText1;
        private EditText mEditText2;
        private EditText mEditText3;

        public DefTagPictureAdapter(Context context){
            this.mContext = context;
        }

        @Override
        public View getTagEditView(TagInfo tagInfo) {//於進行標籤編輯時，呼叫此方法，回傳編輯view
            if(null == mEditText){
                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
                mTagEditView = layoutInflater.inflate(R.layout.my_tag_edit_text, null);
                mEditText1 = (EditText) view.findViewById(R.id.tagEdittext1);
                mEditText2 = (EditText) view.findViewById(R.id.tagEdittext2);
                mEditText2 = (EditText) view.findViewById(R.id.tagEdittext3);
            }
        
            if(null != tagInfo){
                MyTagInfo myInfo = (MyTagInfo)tagInfo.customInfo;
                mEditText1.setText(myInfo.s1);
                mEditText2.setText(myInfo.s2);
                mEditText3.setText(myInfo.s3);
            }
      
            return mTagEditView;
        }

        @Override
        public String getTagStringOnEditComplete(View editView) {//回傳要顯示在標籤上的文字，若為null或字串將不會儲存顯示標籤
            String s = null;
            if(mEditText != null){
                s = mEditText1.getText().toString();
            }
            return s;
        }

        @Override
        public Object getCustomInfoOnEditComplete(View editView) {//標籤結束編輯，取得自定義資訊，存入TagInfo.customInfo中
            MyTagInfo myInfo = new MyTagInfo();
            myInfo.s1 = mEditText1.getText().toString();
            myInfo.s2 = mEditText2.getText().toString();
            myInfo.s3 = mEditText3.getText().toString();
            return myInfo;
        }

        public class MyTagInfo{
            String s1;
            String s2;
            String s3;
        }
    }


設定Adapter

    tagPictureView.setAdapter(new MyTagPictureAdapter(context));
    
## License

    Copyright 2017 Yi Ting Huang

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
