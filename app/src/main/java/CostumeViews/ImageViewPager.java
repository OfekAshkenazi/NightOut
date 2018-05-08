package CostumeViews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rd.PageIndicatorView;
import com.squareup.picasso.Picasso;

import java.util.List;

import ofeksprojects.ofek.com.nightout.R;

/**
 * Created by Ofek on 28-Feb-18.
 */

public class ImageViewPager extends ViewPager {

    private ImagePagerAdapter adapter;
    public ImageViewPager(@NonNull Context context) {
        super(context);
    }

    public ImageViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setUrls(List<String> urls){
        setAdapter(new ImagePagerAdapter(urls,getContext()));
    }

    public void adapterNotifyDataSetChanged(){
        if (adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    private static class ImagePagerAdapter extends PagerAdapter{
        private List<String> urls;
        private Context context;

        public ImagePagerAdapter(List<String> urls, Context context) {
            this.urls = urls;
            this.context = context;
        }

        @Override
        public int getCount() {
            return urls.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            String photoUrl = urls.get(position);
            View  view = LayoutInflater.from(container.getContext()).inflate(R.layout.image_view_pager_lay,container,false);
            ImageView imageView = view.findViewById(R.id.image_ImagePagerView);
            Picasso.with(container.getContext()).load(photoUrl).fit().into(imageView);
            container.addView(view,0);
            return view;
        }
    }
}
