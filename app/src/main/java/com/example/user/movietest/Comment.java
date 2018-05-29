package com.example.user.movietest;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Comment extends Activity {

    MyAdapter itemAdapter;
    String account = "test456";
    RecyclerView recyclerView;
    private final int EACH_COUNT=10;
    private JSONArray jsonArray;
    private int num=0,load = 0;
    private String dmname;
    private String GET_URL = "https://lifego04.000webhostapp.com/dmcontent.php";
    private String COMMENT_URL = "https://lifego04.000webhostapp.com/dm_comment.php";
    private EditText comment_et;
    private ImageButton comment_btn;
    DividerItemDecoration d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dm_comment);

        dmname = getIntent().getStringExtra("dmname");

        comment_et = (EditText) findViewById(R.id.comment_et);
        comment_btn = (ImageButton) findViewById(R.id.comment_btn);

        d = new DividerItemDecoration(Comment.this, DividerItemDecoration.VERTICAL);

        comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comment_et.getText().toString().equals("")||comment_et.getText().toString()==null){
                    Toast.makeText(Comment.this, "給個評論吧", Toast.LENGTH_SHORT).show();
                }else if(comment_et.getText().toString().contains("'")||comment_et.getText().toString().equals(";")){
                    Toast.makeText(Comment.this, "包含非法字元", Toast.LENGTH_SHORT).show();
                }else if(comment_et.getText().toString().length()>=50) {
                    Toast.makeText(Comment.this, "50字內", Toast.LENGTH_SHORT).show();
                }else{
                    DMComment.uploadComment(COMMENT_URL,account,"mv",dmname,comment_et.getText().toString());
                    comment_et.setText("");
                    Toast.makeText(Comment.this, "發送成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

        new CommentRunTask().execute();
    }
    private class CommentRunTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //loading.setVisibility(View.VISIBLE);
                }
            });
        }
        @Override
        protected String doInBackground(String... params) {
            String result = DownloadImg.executeQuery(GET_URL, "SELECT * FROM dm_comment WHERE dmname='" + dmname + "' AND dmtype = '"+getIntent().getStringExtra("type")+"' " +
                    "ORDER BY comment_time DESC");
            return result;
        }
        @Override
        protected void onPostExecute(final String result) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // if(this == null)
                    //  return;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Comment.this, "長按檢舉", Toast.LENGTH_SHORT).show();
                            try {
                                jsonArray = new JSONArray(result);
                                num = jsonArray.length();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            recyclerView = (RecyclerView) findViewById(R.id.dmcomment_rv);

                            if(num>0){
                                // listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, false, true));
                                final ArrayList<HashMap<String,String>> list=new ArrayList<HashMap<String,String>>();
                                itemAdapter = null;
                                itemAdapter = new MyAdapter(list);
                                recyclerView.setLayoutManager(new LinearLayoutManager(Comment.this));
                                recyclerView.setAdapter(itemAdapter);
                                // recyclerView.setNestedScrollingEnabled(false);
                                recyclerView.removeItemDecoration(d);
                                recyclerView.addItemDecoration(d);

                                loadMoreData(load);
                                load=1;
                                new LoadMoreForRecyclerView(recyclerView, new LoadMoreForRecyclerView.LoadMoreListener() {
                                    @Override
                                    public void loadListener() {
                                        if((10 * load)<num){
                                            loadMoreData(load);
                                            load+=1;
                                            itemAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });

                            }else{}
                            //loading.setVisibility(View.GONE);
                        }
                    });
                }
            }).start();

        }
    }
    private void loadMoreData(int load){
        int count = itemAdapter.getItemCount();

        if(num>0){
            try {
                for (int i = 0 +(10 * load); i < EACH_COUNT * (load + 1); i++) {
                    if (count+i-(10 * load)<num) {

                        HashMap<String, String> item = new HashMap<String, String>();
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        String user_name = jsonData.getString("account");
                        String comment = jsonData.getString("user_comment");
                        String time = jsonData.getString("comment_time");

                        item.put("user_name",user_name);
                        item.put("time",time);
                        item.put("comment",comment);
                        itemAdapter.addItem(item);
                    }
                    //
                }
            }catch(Exception e) {
                Log.e("log_tag", e.toString());
            }
        }
    }
    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

        ArrayList<HashMap<String,String>> list=new ArrayList<HashMap<String,String>>();

        public MyAdapter(ArrayList<HashMap<String,String>> newlist) {
            // TODO 自动生成的构造函数存根
            list = newlist;
        }
        public void addItem(HashMap<String, String> item) {
            list.add(item);
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dmcomment_rv,parent,false);
            MyAdapter.ViewHolder viewHolder = new MyAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, final int position) {

            holder.name.setText(list.get(position).get("user_name"));
            holder.comment.setText(list.get(position).get("comment"));
            holder.time.setText(list.get(position).get("time"));
            if(list.get(position).get("user_name").equals(account)){
                holder.edit.setVisibility(View.VISIBLE);
                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(Comment.this)
                                .setTitle("確認編輯")
                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AlertDialog.Builder editDialog = new AlertDialog.Builder(Comment.this);
                                        editDialog.setTitle("編輯");

                                        final EditText editText = new EditText(Comment.this);
                                        editText.setText(list.get(position).get("comment"));
                                        editDialog.setView(editText);

                                        editDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                            // do something when the button is clicked
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                if(editText.getText().toString()!=null&&!editText.getText().toString().equals("")) {
                                                    if (editText.getText().toString().contains("'") || editText.getText().toString().contains(";") ||
                                                            editText.getText().toString().length() >= 50) {
                                                        Toast.makeText(Comment.this, "包含非法字元(50字)", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        DMComment.updateComment(GET_URL, account, getIntent().getStringExtra("type"), dmname, editText.getText().toString(),list.get(position).get("time"));
                                                        Toast.makeText(Comment.this, "已送出", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });
                                        editDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            // do something when the button is clicked
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                //...
                                            }
                                        });
                                        editDialog.show();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setNeutralButton("刪除", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DMComment.deleteComment(GET_URL, account, getIntent().getStringExtra("type"), dmname, list.get(position).get("comment") ,list.get(position).get("time"));
                                        Toast.makeText(Comment.this, "已送出", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .show();
                    }
                });
            }else{
                holder.edit.setVisibility(View.GONE);
                holder.ll.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                String result = DownloadImg.executeQuery(GET_URL, "SELECT count(account) as cum FROM dm_report WHERE dmname='" + dmname + "' AND dmtype = '"+getIntent().getStringExtra("type")+"' " +
                                        "AND account='"+list.get(position).get("user_name")+"' AND user_comment='"+list.get(position).get("comment")+"' AND comment_time='"
                                        +list.get(position).get("time")+"'");
                                if(result.contains("cum"+"\":\""+"0")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new AlertDialog.Builder(Comment.this)
                                                    .setTitle("確認檢舉")
                                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            AlertDialog.Builder editDialog = new AlertDialog.Builder(Comment.this);
                                                            editDialog.setTitle("請簡述原因");

                                                            final EditText editText = new EditText(Comment.this);
                                                            editText.setText("廣告或帶有惡意訊息");
                                                            editDialog.setView(editText);

                                                            editDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                                // do something when the button is clicked
                                                                public void onClick(DialogInterface arg0, int arg1) {
                                                                    if (editText.getText().toString() != null && !editText.getText().toString().equals("")) {
                                                                        if (editText.getText().toString().contains("'") || editText.getText().toString().contains(";") ||
                                                                                editText.getText().toString().length() >= 50) {
                                                                            Toast.makeText(Comment.this, "包含非法字元(50字)", Toast.LENGTH_SHORT).show();
                                                                        } else {
                                                                            DMComment.uploadReport(GET_URL, list.get(position).get("user_name"),getIntent().getStringExtra("type"), dmname, list.get(position).get("comment"),list.get(position).get("time") ,editText.getText().toString());
                                                                            Toast.makeText(Comment.this, "已送出", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                            editDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                                // do something when the button is clicked
                                                                public void onClick(DialogInterface arg0, int arg1) {
                                                                    //...
                                                                }
                                                            });
                                                            editDialog.show();
                                                        }
                                                    })
                                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    })
                                                    .show();
                                        }
                                    });
                                }
                            }
                        }).start();

                        return true;
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return list==null?0:list.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView name,comment,time;
            public ImageButton edit;
            public LinearLayout ll;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                comment = (TextView) itemView.findViewById(R.id.comment);
                time = (TextView) itemView.findViewById(R.id.time);
                edit = (ImageButton) itemView.findViewById(R.id.edit);
                ll= (LinearLayout) itemView.findViewById(R.id.ll);
            }
        }
    }
}
