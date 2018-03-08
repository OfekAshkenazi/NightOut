package CostumeViews;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

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
    public void setPhotos(@Nullable List<String> data){
        Adapter adapter = new Adapter(data);
        setLayoutManager(new GridLayoutManager(getContext(),1,GridLayoutManager.HORIZONTAL,false));
        setAdapter(adapter);
    }

    private class Adapter extends BaseQuickAdapter<String,BaseViewHolder>{

        public Adapter(@Nullable List<String> data) {
            super(R.layout.image_view_pager_lay,data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            ImageView imageView = helper.getView(R.id.image_ImagePagerView);
            Picasso.with(imageView.getContext()).load(item).fit().into(imageView);
        }
    }
}
