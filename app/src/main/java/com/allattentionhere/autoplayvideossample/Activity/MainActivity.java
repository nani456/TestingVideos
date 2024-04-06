package com.allattentionhere.autoplayvideossample.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.allattentionhere.autoplayvideos.AAH_CustomViewHolder;
import com.allattentionhere.autoplayvideos.AAH_Utils;
import com.allattentionhere.autoplayvideossample.Adapter.MyVideosAdapter;
import com.allattentionhere.autoplayvideos.AAH_CustomRecyclerView;
import com.allattentionhere.autoplayvideossample.Model.MyModel;
import com.allattentionhere.autoplayvideossample.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.rv_home)
    AAH_CustomRecyclerView recyclerView;
    AlertDialog dialog;
    private final List<MyModel> modelList = new ArrayList<>();

    Uri loadingThumbnail;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        loadingThumbnail =  Uri.parse("android.resource://com.allattentionhere.autoplayvideossample/drawable/loading");

        Picasso p = Picasso.with(this);
      //  pd = new ProgressDialog(MainActivity.this);

        modelList.add(new MyModel("https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/80/ASSETS/asset_7378692246785011771DesignAsset_1707124198604_Ads%20offers.mp4", loadingThumbnail.toString(), "video1"));
        modelList.add(new MyModel("https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/80/ASSETS/asset_10997764465025613630DesignAsset_1707128707418_dantam%20dental%20suite-2.mp4", loadingThumbnail.toString(), "video2"));
        modelList.add(new MyModel("https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/86/ASSETS/asset_14975988839848876724DesignAsset_1711686900898_World+Kidney+Day+Campaign_Digital+Screen+-+V1+(1).mp4", loadingThumbnail.toString(),"video3"));
        modelList.add(new MyModel("https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/80/ASSETS/asset_6890123940375361769DesignAsset_1709667781043_Dantam%20Ad_01%20copy.mp4",loadingThumbnail.toString(),"video4"));
        modelList.add(new MyModel("https://adbeets-assets.s3.ap-south-1.amazonaws.com/assets/80/ASSETS/asset_7360241592401237427DesignAsset_1709667785000_Dantam%20Ad_02%20copy.mp4",loadingThumbnail.toString(), "video5"));

        MyVideosAdapter mAdapter = new MyVideosAdapter(modelList, p);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //todo before setAdapter
        recyclerView.setActivity(this);

        //optional - to play only first visible video
        recyclerView.setPlayOnlyFirstVideo(true); // false by default
        recyclerView.setCheckForMp4(false);
        if(isConnectingToInternet(MainActivity.this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            {
                downloadDialog();
        }
            else {
                requestStoragePermission();
            }

            //downloadDialog();
        }
        //optional - by default we check if url ends with ".mp4". If your urls do not end with mp4, you can set this param to false and implement your own check to see if video points to url
        //true by default

        //optional - download videos to local storage (requires "android.permission.WRITE_EXTERNAL_STORAGE" in manifest or ask in runtime)

        //extra - start downloading all videos in background before loading RecyclerView

        recyclerView.setAdapter(mAdapter);
        //call this functions when u want to start autoplay on loading async lists (eg firebase)
        recyclerView.smoothScrollBy(0,1);
        recyclerView.smoothScrollBy(0,-1);

    }

    @Override
    protected void onStop() {
        super.onStop();
        //add this code to pause videos (when app is minimised or paused)
        recyclerView.stopVideos();
    }

    public void downloadDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.title_dialog));
        builder.setCancelable(false);
        builder.setMessage(getResources().getString(R.string.text_dialog));
        builder.setPositiveButton(getResources().getString(R.string.yes_dialog), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
               // pd.show();
              //  pd.setCancelable(false);
                recyclerView.setDownloadPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()); // (Environment.getExternalStorageDirectory() + "/Video") by default
                recyclerView.setVisiblePercent(50); // percentage of View that needs to be visible to start playing

                List<String> urls = new ArrayList<>();
                for (MyModel object : modelList) {
                    if (object.getVideo_url() != null && object.getVideo_url().contains("http"))
                        urls.add(object.getVideo_url());
                }
                recyclerView.preDownload(urls);

                recyclerView.setDownloadVideos(true); // false by default
              //  pd.dismiss();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.no_dialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // finish();
            }
        });
        dialog = builder.create();
        File f1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Adbeets1.mp4");

        File f2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Adbeets2.mp4");

        File f3 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Adbeets3.mp4");

        File f4 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Adbeets4.mp4");

        File f5 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Adbeets5.mp4");
      if(f1.exists()&&f2.exists()&&f3.exists()&&f4.exists()&&f5.exists())
      {

      }
      else {
          dialog.show();
      }
    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            @SuppressLint("MissingPermission") NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    private void requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                       downloadDialog();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onBackPressed() {
        exitDialog();
       // super.onBackPressed();
    }
    public void exitDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.exit_title_dialog));
        builder.setMessage(getResources().getString(R.string.exit_text_dialog));
        builder.setPositiveButton(getResources().getString(R.string.exit_yes_dialog), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                try {
                    android.os.Process.killProcess(android.os.Process.myPid());
                } catch (Exception e) {

                }
                //  finish();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.exit_no_dialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // finish();
            }
        });
        dialog = builder.create();

        dialog.show();
    }

}
