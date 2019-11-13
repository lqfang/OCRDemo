package com.xinhuo.ocrdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xinhuo.ocrdemo.R;
import com.xinhuo.ocrdemo.entity.KeyBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MyWordsBeanAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private Context context;
    private String data;
    private int num;

    public MyWordsBeanAdapter(Context context) {
        this.context = context;
    }

    public void setDatas(int count, String data) {
        this.num = count;
        this.data = data;
        notifyDataSetChanged();
    }

    //创建新View，被LayoutManager所调用
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_item, viewGroup, false);
        return new MyAdapter.ViewHolder(view);
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder viewHolder, final int position) {
        //解析数据
        JSONObject jsonObject = null;
        Map<String, String> map = new HashMap<>();
        try {
            jsonObject = new JSONObject(data);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = jsonObject.optString(key);
                viewHolder.mTextView.setText(key + " ：" + value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        for(String key : map.keySet()){
//            Log.e("tag", " ===Key====>:" + key);
//            String value = map.get(key);
//            viewHolder.mTextView.setText(key + " ：" + value);
//        }
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            Log.e("tag", " ===Key====>:" + entry.getKey());
//            Log.e("tag", " ===Value====>:" + entry.getValue());
//            viewHolder.mTextView.setText(entry.getKey() + " ：" + entry.getValue());
//        }
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return num;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.tv_txt);
        }

    }
}