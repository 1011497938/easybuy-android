package com.eajy.materialdesigndemo.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.eajy.materialdesigndemo.R;
import com.eajy.materialdesigndemo.info.ThingsInfo;

import java.util.List;

public class ThingsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_NORMAL_ITEM = 0;  //普通Item
    private static final int TYPE_FOOTER_ITEM = 1;  //底部FooterView

    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 1;
    //正在加载中
    public static final int LOADING_MORE = 2;
    //默认为0
    private int load_more_status = 0;

    public List<ThingsInfo> list;
    public List<Boolean> isChoosen;

    private OnCheckClickListener checkListener;    //复选框
    private OnBtnClickListener btn_clickListener, card_clickListener;  //管理按钮,卡片

    public Context context;

    public ThingsRecyclerViewAdapter(Context context, List<ThingsInfo> list) {
        this.context = context;
        this.list = list;
    }

    ViewGroup viewGroup;
    //创建新View，被LayoutManager所调用
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        this.viewGroup = viewGroup;
        //如果viewType是普通item返回普通的布局，否则是底部布局天降删除并返回
        if (viewType == TYPE_NORMAL_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                    .card_thing, viewGroup, false);
            final NormalItmeViewHolder vh = new NormalItmeViewHolder(view);
            if (checkListener != null) {
                vh.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                            checkListener.onCheckClick(vh.itemView,vh.getLayoutPosition(), isChecked);
                    }
                });
            }
            if(card_clickListener!=null){
                vh.card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        card_clickListener.onBtnClick(vh.itemView,vh.getLayoutPosition());
                    }
                });
            }
            return vh;
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                    .card_thing_botton, viewGroup, false);
            final FooterViewHolder vh = new FooterViewHolder(view);
            if (btn_clickListener!=null){
                vh.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btn_clickListener.onBtnClick(vh.itemView,vh.getLayoutPosition());
                    }
                });
            }
            return vh;
        }
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof NormalItmeViewHolder) {
            try {
//                Log.e("WTF","name " + list.get(position).getName());
                ((NormalItmeViewHolder) viewHolder).name.setText(list.get(position).getName());
                ((NormalItmeViewHolder) viewHolder).pos.setText(list.get(position).getPos());

                ((NormalItmeViewHolder) viewHolder).state.setText( list.get(position).getRemain() + "%");
                if(list.get(position).getRemain()<20)
                    ((NormalItmeViewHolder) viewHolder).state.setTextColor(context.getResources().getColor(R.color.red_primary));
                else
                    ((NormalItmeViewHolder) viewHolder).state.setTextColor(context.getResources().getColor(R.color.black_overlay));

                ((NormalItmeViewHolder) viewHolder).cb.setChecked(list.get(position).isChoosen());

            }catch (Exception e){

            }
        } else if (viewHolder instanceof FooterViewHolder) {
            //处理删除按钮
        }
    }

    public void clearAllSelected(){
//        RecyclerView.LayoutManager manager =

    }
    @Override
    public int getItemCount() {
        return list.size()+1;
    }

    public int getItemViewType(int position) {
        // 如果position+1等于整个布局所有数总和就是底部布局
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER_ITEM;
        } else {
            return TYPE_NORMAL_ITEM;
        }
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class NormalItmeViewHolder extends RecyclerView.ViewHolder {
        public TextView name, state, pos;
        public ImageView iv;
        public CheckBox cb;
        public CardView card;

        public NormalItmeViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.card_things_name);
            state = (TextView) view.findViewById(R.id.card_things_state);
            pos = (TextView) view.findViewById(R.id.card_things_pos);
            cb = (CheckBox) view.findViewById(R.id.checkbox_choose);
            card = (CardView)view.findViewById(R.id.card_thing);
        }
    }
    /**
     * 底部FooterView布局
     */
    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public Button btn;

        public FooterViewHolder(View view) {
            super(view);
            btn = (Button) view.findViewById(R.id.btn_things_delete);
        }
    }
    public void setMoreStatus(int status){
        load_more_status=status;
        notifyDataSetChanged();
    }

    public void setOnCheckClickListener(OnCheckClickListener listener) {
        checkListener = listener;
    }

    public interface OnCheckClickListener {
        public void onCheckClick(View itemView, int pos, boolean isChecked);
    }

    public void setOnBtnClickListener(OnBtnClickListener listener) {
        btn_clickListener = listener;
    }
    public void setOnCardClickListener(OnBtnClickListener listener) {
        card_clickListener = listener;
    }
    public interface OnBtnClickListener {
        public void onBtnClick(View itemView, int pos);
    }
}
