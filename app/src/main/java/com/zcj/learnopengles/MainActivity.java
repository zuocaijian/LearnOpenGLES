package com.zcj.learnopengles;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zcj.test1.Test1Activity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String GLOBAL_TAG = "zcjLog";

    private RecyclerView mRv;
    private List<Class> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createData();

        mRv = findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false));
        mRv.setAdapter(new Adapter());
        mRv.addItemDecoration(new RecyclerView.ItemDecoration() {

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            RectF rectF = new RectF();
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getBaseContext().getResources().getDisplayMetrics());

            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDraw(c, parent, state);
                rectF.set(0, 0, parent.getWidth(), padding);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.parseColor("#88f9aa"));
                c.drawRect(rectF, paint);
            }

            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.set(0, padding, 0, padding);
            }
        });
    }

    private void createData() {
        mData.clear();
        mData.add(Test1Activity.class);
    }

    private class ClickListener implements View.OnClickListener {
        private RecyclerView.ViewHolder mVh;

        public ClickListener(RecyclerView.ViewHolder vh) {
            this.mVh = vh;
        }

        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, mData.get(mVh.getAdapterPosition())));
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        public ClickListener mClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mClickListener = new ClickListener(this);
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, parent.getContext().getResources().getDisplayMetrics());
            tv.setPadding(0, padding, 0, padding);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(16);
            tv.setBackgroundColor(Color.parseColor("#aaaaaa"));
            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(tv);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.itemView.setOnClickListener(holder.mClickListener);
            ((TextView) holder.itemView).setText(mData.get(position).getSimpleName());
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }
}
