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
import java.util.Iterator;
import java.util.List;

public class MyWordsBeanAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    /**
     * words_result_num : 1
     * words_result : [{"locale":{"right_bottom":[2227,1591],"left_top":[635,685]},"words":" TinMEal"}]
     */

    private Context context;
    private int num;
    private List<KeyBean> list = new ArrayList<>();

    public MyWordsBeanAdapter(Context context) {
        this.context = context;
    }

    public void setDatas(int count, List<KeyBean> data) {
        this.num = count;
        this.list = data;
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
        String key = list.get(position).getKey();
        String value = list.get(position).getValue();
        viewHolder.mTextView.setText(key + " ：" + value);
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
