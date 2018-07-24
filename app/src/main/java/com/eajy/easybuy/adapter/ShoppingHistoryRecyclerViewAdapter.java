package com.eajy.easybuy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.eajy.easybuy.info.OrderInfo;
import com.eajy.materialdesigndemo.R;

import java.util.List;

public class ShoppingHistoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_NORMAL_ITEM = 0;  //普通Item
    private static final int TYPE_FOOTER_ITEM = 1;  //底部FooterView

    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 1;
    //正在加载中
    public static final int LOADING_MORE = 2;
    //默认为0
    private int load_more_status = 0;

    public List<OrderInfo> list;
    private OnItemClickListener btnInfoClickListner;
    private OnItemClickListener btnConfirmClickListner;

    public ShoppingHistoryRecyclerViewAdapter(List<OrderInfo> list) {
        this.list = list;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //如果viewType是普通item返回普通的布局，否则是底部布局天降删除并返回
        if (viewType == TYPE_NORMAL_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                    .card_order, viewGroup, false);
            final NormalItmeViewHolder vh = new NormalItmeViewHolder(view);
            if (btnInfoClickListner != null) {
                vh.btnInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btnInfoClickListner.onItemClick(vh.itemView,vh.getLayoutPosition());
                    }
                });
            }
            if (btnConfirmClickListner != null) {
                vh.btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btnConfirmClickListner.onItemClick(vh.itemView,vh.getLayoutPosition());
                    }
                });
            }
            return vh;
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                    .card_order_botton, viewGroup, false);
            FooterViewHolder vh = new FooterViewHolder(view);
            return vh;
        }
    }


    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof NormalItmeViewHolder) {
            try {
                ((NormalItmeViewHolder) viewHolder).name.setText(list.get(position).getGoodName());
                ((NormalItmeViewHolder) viewHolder).date.setText(list.get(position).getDate());
                ((NormalItmeViewHolder) viewHolder).price.setText(list.get(position).getPrice());

                String name = list.get(position).getGoodName();
                switch (name){
                    case "水": ((NormalItmeViewHolder) viewHolder).img.setImageResource(R.drawable.shui); break;
                    case "沐浴露": ((NormalItmeViewHolder) viewHolder).img.setImageResource(R.drawable.muyulu); break;
                    case "盐": ((NormalItmeViewHolder) viewHolder).img.setImageResource(R.drawable.yan); break;
                    default:  ((NormalItmeViewHolder) viewHolder).img.setImageResource(R.drawable.material_design_2); break;
                }

                if (list.get(position).isArrived())
                    ((NormalItmeViewHolder) viewHolder).btnConfirm.setVisibility(View.INVISIBLE);
            }catch (Exception e){

            }
        } else if (viewHolder instanceof FooterViewHolder) {
            //处理删除按钮
        }
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
        public TextView name, date, price, btnInfo, btnConfirm;
        public ImageView img;

        public NormalItmeViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.card_order_title);
            price = (TextView) view.findViewById(R.id.card_order_price);
            date = (TextView) view.findViewById(R.id.card_order_date);
            img = (ImageView) view.findViewById(R.id.img_card_order);

            btnConfirm = (Button) view.findViewById(R.id.btn_card_order_confirm);
            btnInfo = (Button) view.findViewById(R.id.btn_card_order_info);
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

    public void setBtnInfoClickListener(OnItemClickListener listener) {
        btnInfoClickListner = listener;
    }

    public void setBtnConfirmClickListener(OnItemClickListener listener) {
        btnConfirmClickListner = listener;
    }
    public interface OnItemClickListener {
        public void onItemClick(View itemView, int pos);
    }
}
