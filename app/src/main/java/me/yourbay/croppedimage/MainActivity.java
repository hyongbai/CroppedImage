package me.yourbay.croppedimage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MainActivity extends Activity implements View.OnClickListener {

    LinearLayout llLeft, llRight;
    private final int MAX_SAMPLE_COUNT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llLeft = (LinearLayout) findViewById(R.id.ll_left_portrit);
        llRight = (LinearLayout) findViewById(R.id.ll_right_land);
        initView(llLeft, getpx(30), true);
        initView(llRight, getpx(80), false);
        ViewGroup group = (ViewGroup) findViewById(R.id.ll);
        setClick(group);
    }

    private int getpx(int dp) {
        return (int) getResources().getDisplayMetrics().density * dp;
    }

    private void initView(LinearLayout ll, int width, boolean isPortrit) {
        float gravity = 1.0f / MAX_SAMPLE_COUNT;
        for (int i = 0; i < MAX_SAMPLE_COUNT; i++) {
            CroppedImage cv = new CroppedImage(this);
            float child_gravity = gravity * (i + 1);
            cv.setScaleType(ImageView.ScaleType.MATRIX);
            cv.setGravity(isPortrit ? 0 : child_gravity, !isPortrit ? 0 : child_gravity);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width);
            llp.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin);
            ll.addView(cv, llp);
        }
    }

    private void setClick(ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View v = group.getChildAt(i);
            if (v instanceof CroppedImage || v instanceof Button) {
                v.setOnClickListener(this);
            } else if (v instanceof ViewGroup) {
                setClick((ViewGroup) v);
            }
        }
    }

    private void setDrawables(Drawable d, ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            CroppedImage cv = (CroppedImage) group.getChildAt(i);
            cv.setImageDrawable(d);
        }
    }

    private void showImage() {
        new Thread() {
            @Override
            public void run() {
                final Drawable d = getResources().getDrawable(R.mipmap.test);
                final Drawable d_l = getResources().getDrawable(R.mipmap.test_land);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setDrawables(d, llLeft);
                                setDrawables(d_l, llRight);
                            }
                        });
                    }
                });
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button) {
            showImage();
        } else {
            modifyGravity((CroppedImage) v);
        }
    }

    private void modifyGravity(final CroppedImage cv) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Separate with : samller then 1.0f");
        builder.setView(input);
        builder.setPositiveButton(//
                android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = input.getText().toString();
                        String[] gs = s.split(":");
                        if (gs == null || gs.length != 2) {
                            Toast.makeText(input.getContext(), "Invalid !!!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        float x = Float.valueOf(gs[0]);
                        float y = Float.valueOf(gs[1]);
                        if (x >= 1 || x < 0) {
                            x = 1;
                        }
                        if (y >= 1 || y < 0) {
                            y = 1;
                        }
                        cv.resetDrawableGravity(x, y);
                    }
                })//
                .setNegativeButton(android.R.string.cancel, null).show();
    }

}
