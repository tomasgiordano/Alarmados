package com.example.alarmados;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hanks.htextview.animatetext.TyperText;
import com.hanks.htextview.typer.TyperTextView;
import com.omega.animatedtext.AnimatedTextView;

import xyz.schwaab.avvylib.AvatarView;


public class splashActivity extends AppCompatActivity{

    private final int DURACION_SPLASH = 4000;
    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splashscreen);

        TyperTextView typerDivision = (TyperTextView) findViewById(R.id.typerText2);
        typerDivision.animateText("4°A");
        typerDivision.animate();

        TyperTextView typer = (TyperTextView) findViewById(R.id.typerText);
        typer.animateText("  Tomas Giordano");
        typer.animate();

        AvatarView avatarView = (AvatarView) findViewById(R.id.avatarView);
        avatarView.setAnimating(true);
        avatarView.setBorderThickness(18);
        avatarView.setHighlightBorderColor(Color.RED);
        avatarView.setHighlightBorderColorEnd(Color.GRAY);
        avatarView.setNumberOfArches(0);
        avatarView.setTotalArchesDegreeArea(80);
        avatarView.animate();

        new Handler().postDelayed(new Runnable() {
            public void run()
            {
                Intent intent = new Intent(splashActivity.this, AuthActivity.class);
                startActivity(intent);
                finish();
            }
        }, DURACION_SPLASH);
    }


}
