package net.dambakk.drinkmaster.Activities;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import net.dambakk.drinkmaster.R;

/**
 * Created by chris on 30.07.2016.
 */
public class NewAdventureActivity extends AppCompatActivity {

    Button btnNext, btnCancel;

    public NewAdventureActivity() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_adventure);

        btnNext = (Button) findViewById(R.id.btnNext1);
        btnCancel = (Button) findViewById(R.id.btnCancel1);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show "are u sure"-dialog and cancel...
                finish();
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open next pane here...
            }
        });

    }

    @Override
    public void setTheme(@StyleRes int resid) {
        super.setTheme(resid);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
    }
}
