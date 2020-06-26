package com.gachon.priend.community.activity;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerDecoration extends RecyclerView.ItemDecoration {
    //    리사이클러뷰 아이템간의 간격 설정
    private final int divHeight;

    public RecyclerDecoration(int divHeight) {
        this.divHeight = divHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if(parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount())
            outRect.top = divHeight;


//        outRect.top = 값;
//        outRect.bottom = 값;
//        outRect.left = 값;
//        outRect.right = 값;
//        상하좌우 설정
    }
}