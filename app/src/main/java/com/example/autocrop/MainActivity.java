package com.example.autocrop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.view.ViewGroup.LayoutParams;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.Scalar;


import java.util.ArrayList;
import java.util.List;

import static android.graphics.Bitmap.createScaledBitmap;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private Bitmap srcpic;
    private ImageView imageView;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    System.out.println("OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.result);
        takePicture();
    }



    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            System.out.println("Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            System.out.println("OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void takePicture() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        srcpic = (Bitmap) data.getExtras().get("data");
        Bitmap dstpic = Bitmap.createBitmap(1500, 2000, Bitmap.Config.ARGB_8888);

        Mat srcmat = new Mat();
        Mat graymat = new Mat();
        Mat blurmat = new Mat();


        Utils.bitmapToMat(srcpic, srcmat);


        Mat smallmat = new Mat();
        Imgproc.resize(srcmat, smallmat,new Size(500, 600));//调用Imgproc的Resize方法，进行图片缩放



        Imgproc.cvtColor(smallmat, graymat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat

        Imgproc.GaussianBlur(graymat,blurmat,new Size(5,5),0);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        Mat edges = new Mat();
        Imgproc.Canny(blurmat,edges,100,200);

        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        //Imgproc.drawContours(smallmat, contours, -1, new Scalar(255),3);

        double maxArea = 0;
        double area;
        for(int i = 0; i < contours.size(); i++)
        {
            area = Imgproc.contourArea(contours[i]);
        }
        for i in range(len(contours)):
        area = cv2.contourArea(contours[i])
        if area > maxArea:
        c = i
        maxArea = area

        cnt = contours[c]
        epsilon = 0.01*cv2.arcLength(cnt,True)
        approx = cv2.approxPolyDP(cnt,epsilon,True)

        Mat dstmat = new Mat();
        Imgproc.resize(smallmat, dstmat,new Size(1500, 2000));//调用Imgproc的Resize方法，进行图片缩放
        Utils.matToBitmap(dstmat, dstpic); //convert mat to bitmap
        imageView.setImageBitmap(dstpic);


    }
}
