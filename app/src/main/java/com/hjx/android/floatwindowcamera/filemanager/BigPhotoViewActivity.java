package com.hjx.android.floatwindowcamera.filemanager;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.hjx.android.floatwindowcamera.R;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

public class BigPhotoViewActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private List<String> mList;
//    private DragPhotoView[] mPhotoViews;
    private PhotoView[] mPhotoViews;


    private Bundle mBundle;
    private int positionImg;
    private ListImgsSerializable listImgsSerializable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_big_photo_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.background_main));
        }
        mViewPager = (ViewPager) findViewById(R.id.viewpager);


        mList = new ArrayList<>();
        mBundle = getIntent().getExtras();
        if (mBundle != null) {
//            drugsBean = mBundle.getParcelable("Detail_DrugsBean");
            listImgsSerializable = (ListImgsSerializable)mBundle.getSerializable("ListImgsSerializable");
            positionImg = mBundle.getInt("position");
            mList = listImgsSerializable.getStringList();
//            if (drugsBean.getImages() != null && drugsBean.getImages().size() != 0) {
//                for (Map.Entry<String, String> entry : drugsBean.getImages().entrySet()) {
////                    imageBeenList.add(new ImageBean(entry.getValue()));
//                    mList.add(new ImageBean(entry.getValue()).getImgRes());
//                }

//                adapter = new DrugRecordImageAdapter(imageBeen);
//                adapter = new ImageDrugDetailAdapter(imageBeenList,this);
//                rl.setAdapter(adapter);
//            }
        }

        mPhotoViews = new PhotoView[mList.size()];
//        mPhotoViews = new DragPhotoView[mList.size()];
//
        for (int i = 0; i < mPhotoViews.length; i++) {
            mPhotoViews[i]  = (PhotoView) View.inflate(this, R.layout.item_viewpager_two, null);
//            mPhotoViews[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
//            mPhotoViews[i] = (DragPhotoView) View.inflate(this, R.layout.item_viewpager, null);
//            LoadImgUtil.setNoFitImg(this, R.mipmap.ic_default_img, mList.get(i).toString(), mPhotoViews[i]);
            Glide.with(this).load(mList.get(i).toString()).fitCenter().into(mPhotoViews[i]);
//            mPhotoViews[i].setOnTapListener(new DragPhotoView.OnTapListener() {
//                @Override
//                public void onTap(DragPhotoView view) {
//                    finishWithAnimation();
//                }
//            });

//            mPhotoViews[i].setOnExitListener(new DragPhotoView.OnExitListener() {
//                @Override
//                public void onExit(DragPhotoView view, float x, float y, float w, float h) {
//                    performExitAnimation(view, x, y, w, h);
//                }
//            });
        }

        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mList.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
//                DragPhotoView iv = new DragPhotoView(BigPhotoViewActivity.this);
//                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                Picasso.with(BigPhotoViewActivity.this).load(mList.get(position)).into(iv);
//                container.addView(iv);
//                iv.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(BigPhotoViewActivity.this, "position:" + position, Toast.LENGTH_SHORT).show();
//                    }
//                });
//                return iv;
                container.addView(mPhotoViews[position]);
                return mPhotoViews[position];
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
//                container.removeView((View) object);
                container.removeView(mPhotoViews[position]);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
        mViewPager.setCurrentItem(positionImg);
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
