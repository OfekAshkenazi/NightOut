package CostumeViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.rd.PageIndicatorView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ofeksprojects.ofek.com.nightout.R;

/**
 * Created by Ofek on 06-Mar-18.
 */

public class ImageRecyclerView extends RecyclerView {
    public ImageRecyclerView(Context context) {
        super(context);
    }

    public ImageRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public void setPhotos(@Nullable List<String> data, @Nullable final PageIndicatorView indicator){
        assert data != null;
        Log.e("ImageRecyclerView","photos list size: " + data.size());
        Adapter adapter = new Adapter(data,indicator);
        setLayoutManager(new GridLayoutManager(getContext(),1,GridLayoutManager.HORIZONTAL,false));
        setAdapter(adapter);
        SnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(this);
    }
    private class Adapter extends BaseQuickAdapter<String,BaseViewHolder>{

        private final PageIndicatorView indicator;

        public Adapter(@Nullable List<String> data, @Nullable PageIndicatorView indicator) {
            super(R.layout.image_view_pager_lay,data);
            if (indicator!=null&&data!=null){
                indicator.setCount(data.size());
            }
            this.indicator = indicator;
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            ImageView imageView = helper.getView(R.id.image_ImagePagerView);
            Picasso.with(imageView.getContext()).load(item).fit().into(imageView);

            if (indicator != null){
                indicator.onPageSelected(helper.getLayoutPosition());
            }
        }
    }
}
