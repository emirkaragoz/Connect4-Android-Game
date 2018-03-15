package connect4.emir.connect4;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;

/**
 * Game Menu, Select Game Options
 */
public class MenuScreen extends AppCompatActivity {
    private Context context = this;
    /**
     * Size of Game Board
     */
    private int size;
    /**
     * Move Time
     */
    private int time;
    /**
     * Time Mode Option
     */
    private boolean timeMode;
    /**
     * Game Type P(PVP) or C(PVC)
     */
    private char gameType = 'X';
    /**
     * Time Screen Layout
     */
    private LinearLayout timeLayout;
    /**
     * Board Size Scan
     */
    private EditText editTextSize;
    /**
     * Time Scan
     */
    private EditText editTextSize2;

    private Button pvpButton,pvcButton;
    /**
     * start button to start game
     */
    private ImageButton startButton;
    /**
     * switch button to select time option
     */
    private Switch timeSwitch;

    /**
     * Main Method of Menu Screen
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu_screen);

        editTextSize = findViewById(R.id.editSize);
        editTextSize2 = findViewById(R.id.editSize2);
        pvpButton = findViewById(R.id.button1);
        pvcButton = findViewById(R.id.button2);
        startButton = findViewById(R.id.button3);
        timeSwitch = findViewById(R.id.switchButton);
        timeLayout = findViewById(R.id.selectTime);

        Display forScreenSize = getWindowManager().getDefaultDisplay();
        int width = forScreenSize.getWidth();
        int height = forScreenSize.getHeight();
        Drawable background = getResources().getDrawable(R.drawable.background);
        Bitmap backgroundBit = ((BitmapDrawable) background).getBitmap();
        Drawable toScaled = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(backgroundBit,width,height,true));
        findViewById(R.id.menu).setBackground(toScaled);

        pvpButton.setOnClickListener(new View.OnClickListener() {
            /**
             * PvP Button Click Listener
             * @param v
             */
            @Override
            public void onClick(View v) {
                gameType = 'P';
                pvpButton.setBackgroundResource(R.drawable.pvc_pvp_green);
                pvcButton.setBackgroundResource(R.drawable.pvp_pvc_buttons);
            }
        });

        pvcButton.setOnClickListener(new View.OnClickListener() {
            /**
             * PvC Button Click Listener
             * @param v
             */
            @Override
            public void onClick(View v) {
                gameType = 'C';
                pvcButton.setBackgroundResource(R.drawable.pvc_pvp_green);
                pvpButton.setBackgroundResource(R.drawable.pvp_pvc_buttons);
            }
        });

        editTextSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            /**
             * Game Board Size Text Listener
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {
                String temp = editTextSize.getText().toString();
                int finalSize=0;
                for(int j=temp.length()-1,k=0; j>=0 ;--j,++k)
                    finalSize+= Character.getNumericValue(temp.charAt(j))*Math.pow(10, k);
                size = finalSize;
            }
        });

        editTextSize2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            /**
             * Time Text Listener
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {
                String temp = editTextSize2.getText().toString();
                int finalTime=0;
                for(int j=temp.length()-1,k=0; j>=0 ;--j,++k)
                    finalTime+= Character.getNumericValue(temp.charAt(j))*Math.pow(10, k);
                time = finalTime;
            }
        });

        timeSwitch.setChecked(false);
        timeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /**
             * Switch Button for Time Mode
             * @param buttonView
             * @param isChecked  True or False
             */
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    timeLayout.setVisibility(View.VISIBLE);
                    timeMode = true;
                }
                else {
                    timeLayout.setVisibility(View.INVISIBLE);
                    timeMode = false;
                }
            }
        });


        startButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Start Button Click Listener
             * @param v
             */
            @Override
            public void onClick(View v) {
                if(size < 5 || size > 40) {
                    AlertDialog.Builder errorMessage = new AlertDialog.Builder(context);
                    errorMessage.setTitle("Error!");
                    errorMessage.setMessage("Please enter a value between 5 and 40 for size !");
                    errorMessage.setCancelable(true);
                    errorMessage.setPositiveButton("Okey", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    errorMessage.show();
                }
                else if (gameType == 'X'){
                    AlertDialog.Builder errorMessage = new AlertDialog.Builder(context);
                    errorMessage.setTitle("Error!");
                    errorMessage.setMessage("Please choose a game type !");
                    errorMessage.setCancelable(true);
                    errorMessage.setPositiveButton("Okey", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    errorMessage.show();
                }
                else if (time < 5 && timeMode == true){
                    AlertDialog.Builder errorMessage = new AlertDialog.Builder(context);
                    errorMessage.setTitle("Error!");
                    errorMessage.setMessage("Time can be at least 5 second !");
                    errorMessage.setCancelable(true);
                    errorMessage.setPositiveButton("Okey", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    errorMessage.show();
                }
                else{
                    Intent i = new Intent(MenuScreen.this,GameScreen.class);
                    i.putExtra("gameType",gameType);
                    i.putExtra("size",size);
                    i.putExtra("time",time);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}
