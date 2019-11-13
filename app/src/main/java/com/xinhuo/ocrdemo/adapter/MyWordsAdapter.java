package com.xinhuo.ocrdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xinhuo.ocrdemo.R;
import com.xinhuo.ocrdemo.entity.Words;

import java.util.List;

public class MyWordsAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private Context context;
    private Words data = new Words();

    public MyWordsAdapter(Context context) {
        this.context = context;
    }

    public void setDatas(Words data) {
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
//        String key = data.getWords_result().get(position).getKey();
//        String value = data.getWords_result().get(position).getValue();
//        viewHolder.mTextView.setText(key + " ：" + value);
        switch (position) {
            case 0:
                viewHolder.mTextView.setText("公司代码   " + data.getWords_result().get公司代码());
                break;
            case 1:
                viewHolder.mTextView.setText("集装箱编号   " + data.getWords_result().get集装箱编号());
                break;
            case 2:
                viewHolder.mTextView.setText("校验码识别   " + data.getWords_result().get校验码识别());
                break;
            case 3:
                viewHolder.mTextView.setText("校验码计算   " + data.getWords_result().get校验码计算());
                break;
            case 4:
                viewHolder.mTextView.setText("其他   " + data.getWords_result().get其他());
                break;

        }
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return data.getWords_result_num();
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
