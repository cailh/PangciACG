package com.sine_x.material_wecenter.controller.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.nispok.snackbar.Snackbar;
import com.sine_x.material_wecenter.Client;
import com.sine_x.material_wecenter.Config;
import com.sine_x.material_wecenter.R;
import com.sine_x.material_wecenter.models.PublishAnswer;
import com.sine_x.material_wecenter.models.Response;
import com.sine_x.material_wecenter.models.Result2;

public class PostAnswerActivity extends AppCompatActivity {

    private int questionID;
    private String questionTitle;
    private EditText editContent;
    private ImageButton publish;
    private final int resultCode=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_answer);

        //获取intent
        Intent mIntent=getIntent();
        questionID=mIntent.getIntExtra("questionID", -1);
        questionTitle=mIntent.getStringExtra("questionTitle");

        //init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_publishAnswer);
        setSupportActionBar(toolbar);
        setTitle("评论 "+questionTitle);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);

        //init editContent publish按钮
        editContent=(EditText) findViewById(R.id.editText_answerContent);

        publish=(ImageButton) findViewById(R.id.imageButton_publishAnswer);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editContent.getText().toString();
                if(!text.isEmpty()){
                    new PublishTask().execute();
                }
            }
        });
    }

    //异步发布答案
    private class PublishTask extends AsyncTask<Void, Void, Void> {

        String content;
        Response<PublishAnswer> result2;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            content = editContent.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Client client = Client.getInstance();
            result2 = client.publishAnswer(questionID, content);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (result2 == null) // 未知错误
                Snackbar.with(PostAnswerActivity.this).text("未知错误").show(PostAnswerActivity.this);
            else if (result2.getErrno() == 1){ // 发布成功
                PostAnswerActivity.this.setResult(resultCode);//设置activity结束返回值
                PostAnswerActivity.this.finish();
            }

            else                // 显示错误
                Snackbar.with(PostAnswerActivity.this).text(result2.getErr()).show(PostAnswerActivity.this);
        }
    }
}
