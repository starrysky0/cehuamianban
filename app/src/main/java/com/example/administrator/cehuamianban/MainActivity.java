package com.example.administrator.cehuamianban;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.cehuamianban.ui.DragDayout;
import com.example.administrator.cehuamianban.ui.MyLinearLayout;
import com.nineoldandroids.view.ViewHelper;

import java.util.Random;

import static com.example.administrator.cehuamianban.R.id.dl;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        MyLinearLayout ll_main = (MyLinearLayout) findViewById(R.id.ll_main);
        final ImageView iv_header = (ImageView) findViewById(R.id.iv_head);
        final ListView lv_left = (ListView) findViewById(R.id.lv_left);
        RecyclerView lv_main = (RecyclerView) findViewById(R.id.lv_main);
        final DragDayout dragDayout = (DragDayout) findViewById(dl);
        dragDayout.setOnDragDayoutListener(new DragDayout.OnDragDayoutListener() {
            public static final String TAG ="dfdsg" ;

            @Override
            public void onClose() {
                utils.showToast(getApplicationContext(), "onClose");
                // iv_header.setTranslationX();
                ObjectAnimator animator = ObjectAnimator.ofFloat(iv_header, "translationX", 15f);
                animator.setDuration(500);
                animator.setInterpolator(new CycleInterpolator(4));
                animator.start();
            }

            @Override
            public void onOpen() {
                utils.showToast(getApplicationContext(), "onOpen");
                lv_left.smoothScrollToPosition(new Random().nextInt(50));
                Log.i(TAG, "onOpen: ");
            }

            @Override
            public void onDraging(float percent) {
                utils.showToast(getApplicationContext(), "onDraging" + percent);
                ViewHelper.setAlpha(iv_header, 1 - percent);
            }
        });
        MyRecyclerAdapter myRecyclerAdapter = new MyRecyclerAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        lv_main.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        lv_main.setAdapter(myRecyclerAdapter );

        lv_main.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                RecyclerView.FOCUSABLES_TOUCH_MODE
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        lv_left.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, Cheeses.sCheeseStrings) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setTextColor(Color.WHITE);
                return view;

            }
        });
        iv_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dragDayout.open(true);
            }
        });

        ll_main.setDragLayout(dragDayout);


    }
    class MyRecyclerAdapter extends RecyclerView.Adapter {

        private String[] mList= Cheeses.NAMES;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.mTextView.setTextSize(new Random().nextInt(30));
            viewHolder.mTextView.setText(mList[position]);

        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.length;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }
    }
    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }
    }
    class DividerItemDecoration extends RecyclerView.ItemDecoration{

    }
}
