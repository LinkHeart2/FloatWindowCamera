package com.hjx.android.floatwindowcamera.filemanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.hjx.android.floatwindowcamera.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.hjx.android.floatwindowcamera.filemanager.FileUtil.fileFilter;
import static com.hjx.android.floatwindowcamera.filemanager.FileUtil.getFileInfosFromFileArray;


public class SDCardActivity extends baseAcitivity {
    RecyclerView rlv_sd_card;
    TextView tv_title_middle;
    TextView tv_path;
    ImageView iv_title_back;
    FloatingActionButton float_btn;
    private List<FileInfo> fileInfos = new ArrayList<>();
    private List<MultipleItem> mMultipleItems = new ArrayList<>();
    private MultipleItemQuickAdapter mAdapter;
    private File mCurrentPathFile = null;
    private File mSDCardPath = null;
    private File sDCardPath = null;
    private String path;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdcard);
        sDCardPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        rlv_sd_card = ((RecyclerView) findViewById(R.id.rlv_sd_card));
        tv_path = ((TextView) findViewById(R.id.tv_path));
        iv_title_back = ((ImageView) findViewById(R.id.iv_title_back));
        float_btn = ((FloatingActionButton) findViewById(R.id.float_btn));
        tv_title_middle = ((TextView) findViewById(R.id.tv_title_middle));
        path = getIntent().getStringExtra("path");
        tv_title_middle.setText(getIntent().getStringExtra("name"));
        mSDCardPath = new File(path);
        rlv_sd_card.setLayoutManager(new LinearLayoutManager(this));
        rlv_sd_card.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, R.drawable.divide_line));
        mAdapter = new MultipleItemQuickAdapter(this,mMultipleItems);
        rlv_sd_card.setAdapter(mAdapter);
        showFiles(mSDCardPath);
        float_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFiles(mSDCardPath);
                Toast.makeText(SDCardActivity.this,"刷新完成",Toast.LENGTH_SHORT).show();
            }
        });
        rlv_sd_card.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {

                if (adapter.getItemViewType(position) == MultipleItem.FILE) {
//                    Toast.makeText(SDCardActivity.this,"文件点击SD",Toast.LENGTH_SHORT).show();
                    MultipleItem item = (MultipleItem) adapter.getItem(position);
                    List<MultipleItem> data = adapter.getData();
                    List<String> imageList = new ArrayList<String>();
                    if (data != null &&data.size() >0){
                        for (int i = 0; i < data.size(); i++) {
                            imageList.add(data.get(i).getData().getFilePath());
                        }
                    }
                    String filePath = item.getData().getFilePath();
                    ListImgsSerializable listImgsSerializable = new ListImgsSerializable();
                    listImgsSerializable.setStringList(imageList);
//                bundle.putParcelable("Detail_DrugsBean",drugsBean );
                    openFile(filePath,listImgsSerializable,position);
                } else {
                    showFiles(new File(fileInfos.get(position).getFilePath()));
                }

            }

            @Override
            public void onItemLongClick(final BaseQuickAdapter adapter, View view, final int position) {
                MultipleItem item = (MultipleItem) adapter.getItem(position);
                String filePath = item.getData().getFilePath();
                final File file = new File(filePath);
                AlertDialog.Builder builder = new AlertDialog.Builder(SDCardActivity.this);
                builder.setTitle("提示：").setMessage("是否删除该文件？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                file.delete();
                                adapter.getData().remove(position);
//                                Toast.makeText(SDCardActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                                mAdapter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }
    /**
     * 打开一个文件
     *
     * @param filePath
     *            文件的绝对路径
     */
    private void openFile(final String filePath, ListImgsSerializable listImgsSerializable, int position)
    {
        String ext = filePath.substring(filePath.lastIndexOf('.')).toLowerCase(Locale.US);
        try
        {
            if (ext.contains("jpg") ||ext.contains("png") ||ext.contains("jpeg") ||ext.contains("gif") ||ext.contains("bmp")){
                Bundle bundle = new Bundle();
//                bundle.putParcelable("Detail_DrugsBean",drugsBean );
                bundle.putSerializable("ListImgsSerializable",listImgsSerializable );
                bundle.putInt("position",position);
                Intent iii = new Intent(SDCardActivity.this,BigPhotoViewActivity.class);
                iii.putExtras(bundle);
                startActivity(iii);
            }else {
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                String temp = ext.substring(1);
                String mime = mimeTypeMap.getMimeTypeFromExtension(temp);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                File file = new File(filePath);
                intent.setDataAndType(Uri.fromFile(file), mime);
                startActivity(intent);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "无法打开后缀名为." + ext + "的文件！",
                    Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onBackPressed() {
        if (sDCardPath.getAbsolutePath().equals(mCurrentPathFile.getAbsolutePath())) {
            finish();
        } else {
            mCurrentPathFile = mCurrentPathFile.getParentFile();
            showFiles(mCurrentPathFile);
        }
    }
    private void showFiles(File folder) {
        mMultipleItems.clear();
        tv_path.setText(folder.getAbsolutePath());
        mCurrentPathFile = folder;
        File[] files = fileFilter(folder);
        if (null == files || files.length == 0) {
            mAdapter.setEmptyView(getEmptyView());
            Log.e("files", "files::为空啦");
        } else {
            //获取文件信息
            fileInfos = getFileInfosFromFileArray(files);
            for (int i = 0; i < fileInfos.size(); i++) {
                if (fileInfos.get(i).isDirectory) {
                    mMultipleItems.add(new MultipleItem(MultipleItem.FOLD, fileInfos.get(i)));
                } else {
                    mMultipleItems.add(new MultipleItem(MultipleItem.FILE, fileInfos.get(i)));
                }

            }
            //查询本地数据库，如果之前有选择的就显示打钩
//            List<FileInfo> mList = FileDao.queryAll();
//            for (int i = 0; i < fileInfos.size(); i++) {
//                for (FileInfo fileInfo : mList) {
//                    if (fileInfo.getFileName().equals(fileInfos.get(i).getFileName())) {
//                        fileInfos.get(i).setIsCheck(true);
//                    }
//                }
//            }
        }
        mAdapter.notifyDataSetChanged();
    }
    private View getEmptyView() {
        return getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) rlv_sd_card.getParent(), false);
    }
}
