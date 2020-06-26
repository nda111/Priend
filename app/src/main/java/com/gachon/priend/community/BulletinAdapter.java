package com.gachon.priend.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.priend.R;
import com.gachon.priend.community.database.BulletinData;

import java.util.ArrayList;

public class BulletinAdapter extends RecyclerView.Adapter<BulletinAdapter.ItemViewHolder> {
    // adapter에 들어갈 list 입니다.
    private ArrayList<BulletinData> listData = new ArrayList<>();
    private ItemSelectionListener itemSelectionListener = null;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bulletin_list_bulletin, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    public ItemSelectionListener getItemSelectionListener() {
        return itemSelectionListener;
    }

    public void setItemSelectionListener(ItemSelectionListener itemSelectionListener) {
        this.itemSelectionListener = itemSelectionListener;
    }

    public void addItem(BulletinData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView bulletinList;
        private TextView bulletinContent;
        private ImageView icon;

        ItemViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            bulletinList = itemView.findViewById(R.id.row_bulletin_textview_bulletinList);
            bulletinContent = itemView.findViewById(R.id.row_bulletin_textview_bulletinContent);
            icon = itemView.findViewById(R.id.row_bulletin_image_view_bulletin_icon);
        }

        void onBind(final BulletinData data) {
            bulletinList.setText(data.getTitle());
            bulletinContent.setText(data.getContent());
            icon.setImageResource(data.getResId());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemSelectionListener != null) {
                        itemSelectionListener.onItemSelected(data);
                    }
                }
            });
        }
    }

    public interface ItemSelectionListener {

        void onItemSelected(@NonNull BulletinData bulletinData);
    }
}