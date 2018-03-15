package connect4.emir.connect4;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Random;

/**
 * Game Screen, Play Game
 */
public class GameScreen extends AppCompatActivity{

    private Context context = this;
    private int size,time;
    /**
     * Move Order
     */
    private int move=0;
    private char gameType;
    /**
     * Move History for Undo
     */
    private String history="";
    /**
     * 2d Cell Array
     */
    private Cell [][] gameCells;
    /**
     * Countdown Timer
     */
    private CountDownTimer cdt;

    /**
     * Empty Circle
     */
    Drawable emptyScaled;
    /**
     * Blue Cirle
     */
    Drawable blueScaled;
    /**
     * Red Circle
     */
    Drawable redScaled;
    /**
     * Ticked Blue Cirlce
     */
    Drawable tickedBlueScaled;
    /**
     * Ticked Red Cirle
     */
    Drawable tickedRedScaled;

    /**
     * Inner Cell Class
     */
    private class Cell{
        /**
         * . X O x o
         */
        private char cell;

        /**
         * No parameter constructer
         */
        public Cell(){
            cell = '.';
        }

        /**
         * getter
         * @return  cell
         */
        public char getCell(){
            return cell;
        }

        /**
         * setter
         * @param option cell
         */
        public void setCell(char option){
            cell = option;
        }
    }

    /**
     * Main Method of Game Screen
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_screen);

        final ImageButton menu = findViewById(R.id.toMenu);
        final ImageButton undo = findViewById(R.id.undo);
        final TextView timeScreen = findViewById(R.id.time);


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GameScreen.this, MenuScreen.class);
                if(time != 0)
                    cdt.cancel();
                startActivity(i);
                finish();

            }
        });

        Intent s = getIntent();
        size = s.getIntExtra("size",8);
        gameType = s.getCharExtra("gameType",'P');
        time = s.getIntExtra("time",10);

        final GridLayout board = findViewById(R.id.board);
        final ScrollView sv = findViewById(R.id.scroll);

        board.setRowCount(size);
        board.setColumnCount(size);

        initCell();
        final ImageButton [][] cellButtons = new ImageButton[size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size ; ++j) {
                cellButtons[i][j] = new ImageButton(context);
            }
        }

        Display forScreenSize = getWindowManager().getDefaultDisplay();
        int width = forScreenSize.getWidth();
        int height = forScreenSize.getHeight();
        Drawable background = getResources().getDrawable(R.drawable.game_board_back);
        Bitmap backgroundBit = ((BitmapDrawable) background).getBitmap();
        Drawable toScaled = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(backgroundBit,width,height,true));
        findViewById(R.id.gameScreen).setBackground(toScaled);

        Drawable emptyButton = getResources().getDrawable(R.drawable.empty_circle);
        Bitmap emptyBit = ((BitmapDrawable) emptyButton).getBitmap();
        emptyScaled = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(emptyBit,width/5,width/5,true));

        Drawable redButton = getResources().getDrawable(R.drawable.red_circle);
        Bitmap redBit = ((BitmapDrawable) redButton).getBitmap();
        redScaled = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(redBit,width/5,width/5,true));

        Drawable blueButton = getResources().getDrawable(R.drawable.blue_circle);
        Bitmap blueBit = ((BitmapDrawable) blueButton).getBitmap();
        blueScaled = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(blueBit,width/5,width/5,true));

        Drawable tickedBlueButton = getResources().getDrawable(R.drawable.ticked_blue_circle);
        Bitmap tickedBlueBit = ((BitmapDrawable) tickedBlueButton).getBitmap();
        tickedBlueScaled = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(tickedBlueBit,width/5,width/5,true));

        Drawable tickedRedButton = getResources().getDrawable(R.drawable.ticked_red_circle);
        Bitmap tickedRedBit = ((BitmapDrawable) tickedRedButton).getBitmap();
        tickedRedScaled = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(tickedRedBit,width/5,width/5,true));



        if(time == 0){
            timeScreen.setVisibility(View.INVISIBLE);
        }
        else {
            cdt = new CountDownTimer((time * 1000), 100) {
                int tmpMove = move;
                int computerKey = 0;
                int warningKey=0;

                public void onTick(long millisUntilFinished) {
                    if (millisUntilFinished < 6000 && warningKey == 0) {
                        timeScreen.setBackgroundColor(Color.RED);
                        warningKey = 1;
                    }
                    if (tmpMove != move) {
                        this.start();
                        timeScreen.setBackgroundResource(R.drawable.game_board_back);
                        warningKey = 0;
                        tmpMove = move;
                    }
                    timeScreen.setText("" + (int)(millisUntilFinished / 1000));
                }

                public void onFinish() {
                    Random rand = new Random();
                    int tmpcol;
                    tmpcol = rand.nextInt(size);
                    while (usable(tmpcol) == 0)
                        tmpcol = rand.nextInt(size);

                    int tmprow = usable(tmpcol) - 1;
                    if (move % 2 == 0) {
                        cellButtons[tmprow][tmpcol].setBackground(blueScaled);
                        gameCells[tmprow][tmpcol].setCell('X');
                        history += tmpcol;
                        ++move;
                    } else if (move % 2 == 1 && gameType == 'P') {
                        cellButtons[tmprow][tmpcol].setBackground(redScaled);
                        gameCells[tmprow][tmpcol].setCell('O');
                        history += tmpcol;
                        ++move;
                    }
                    if (end(cellButtons)) { //oyun sonlandıysa kazandı mesajını bas ve yeniden başlat
                        cdt.cancel();
                        computerKey = 1;
                        AlertDialog.Builder errorMessage = new AlertDialog.Builder(context);
                        errorMessage.setTitle("Game Finished!");
                        if (check(cellButtons) == 1) {
                            errorMessage.setMessage("User1 is Winner !");
                        } else if (check(cellButtons) == 2) {
                            errorMessage.setMessage("User2 is Winner !");
                        } else {
                            errorMessage.setMessage("The game ended in a draw !");
                        }
                        errorMessage.setCancelable(false);
                        errorMessage.setPositiveButton("Menu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(GameScreen.this, MenuScreen.class);
                                finish();
                                startActivity(i);
                            }
                        });
                        errorMessage.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                System.exit(0);
                            }
                        });
                        errorMessage.show();
                    }else {
                        cdt.start();
                        timeScreen.setBackgroundResource(R.drawable.game_board_back);
                        warningKey = 0;
                    }
                    if (move % 2 == 1 && gameType == 'C' && computerKey == 0) {
                        tmpcol = attack3();
                        if (tmpcol < 0)
                            tmpcol = defence3();
                        if (tmpcol < 0)
                            tmpcol = defence2_1();
                        if (tmpcol < 0)
                            tmpcol = attack2();
                        if (tmpcol < 0)
                            tmpcol = defaultMove();

                        tmprow = usable(tmpcol) - 1;
                        cellButtons[tmprow][tmpcol].setBackground(redScaled);
                        gameCells[tmprow][tmpcol].setCell('O');
                        history += tmpcol;
                        ++move;
                        if (end(cellButtons)) { //oyun sonlandıysa kazandı mesajını bas ve yeniden başlat
                            cdt.cancel();
                            AlertDialog.Builder errorMessage = new AlertDialog.Builder(context);
                            errorMessage.setTitle("Game Finished!");
                            if (check(cellButtons) == 2) {
                                errorMessage.setMessage("Computer is Winner !");
                            } else {
                                errorMessage.setMessage("The game ended in a draw !");
                            }
                            errorMessage.setCancelable(false);
                            errorMessage.setPositiveButton("Menu", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(GameScreen.this, MenuScreen.class);
                                    finish();
                                    startActivity(i);
                                }
                            });
                            errorMessage.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    System.exit(0);
                                }
                            });
                            errorMessage.show();
                        }else {
                            cdt.start();
                            timeScreen.setBackgroundResource(R.drawable.game_board_back);
                            warningKey = 0;
                        }
                    }
                }
            }.start();
        }

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(history.length()>0){
                    if(gameType=='P') {
                        undo(cellButtons);
                        --move;
                    }
                    else{
                        undo(cellButtons);
                        undo(cellButtons);
                        move-=2;
                    }
                }
                else{
                    AlertDialog.Builder errorMessage = new AlertDialog.Builder(context);
                    errorMessage.setTitle("Error!");
                    errorMessage.setMessage("There is no moves were made! So you can't undo.");
                    errorMessage.setCancelable(true);
                    errorMessage.setPositiveButton("Okey", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    errorMessage.show();
                }

            }
        });

        for (int i = 0 ; i < size ; ++i){
            for (int j = 0 ; j < size ; ++j){
                final int temp_j = j;

                cellButtons[i][j].setBackground(emptyScaled);

                cellButtons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int computerEntryKey = 0;
                        if(usable(temp_j) != 0) {
                            if (move % 2 == 0) {
                                cellButtons[usable(temp_j) - 1][temp_j].setBackground(blueScaled);
                                gameCells[usable(temp_j) - 1][temp_j].setCell('X');
                                history+=temp_j;
                                ++move;
                                if (end(cellButtons)) { //oyun sonlandıysa kazandı mesajını bas ve yeniden başlat
                                    if(time!=0) {
                                        cdt.cancel();
                                    }
                                    AlertDialog.Builder errorMessage = new AlertDialog.Builder(context);
                                    errorMessage.setTitle("Game Finished!");
                                    if (check(cellButtons) == 1) {
                                        errorMessage.setMessage("User1 is Winner !");
                                    }
                                    else{
                                        errorMessage.setMessage("The game ended in a draw !");
                                    }
                                    errorMessage.setCancelable(false);
                                    errorMessage.setPositiveButton("Menu", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(GameScreen.this, MenuScreen.class);
                                            finish();
                                            startActivity(i);
                                        }
                                    });
                                    errorMessage.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                            System.exit(0);
                                        }
                                    });
                                    errorMessage.show();

                                    computerEntryKey=1;
                                }
                            } else if (move % 2 == 1 && gameType == 'P') {
                                cellButtons[usable(temp_j) - 1][temp_j].setBackground(redScaled);
                                gameCells[usable(temp_j) - 1][temp_j].setCell('O');
                                history+=temp_j;
                                ++move;
                                if (end(cellButtons)) { //oyun sonlandıysa kazandı mesajını bas ve yeniden başlat
                                    if(time!=0) {
                                        cdt.cancel();
                                    }
                                    AlertDialog.Builder errorMessage = new AlertDialog.Builder(context);
                                    errorMessage.setTitle("Game Finished!");
                                    if (check(cellButtons) == 2) {
                                        errorMessage.setMessage("User2 is Winner !");
                                    }
                                    else{
                                        errorMessage.setMessage("The game ended in a draw !");
                                    }
                                    errorMessage.setCancelable(false);
                                    errorMessage.setPositiveButton("Menu", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(GameScreen.this, MenuScreen.class);
                                            finish();
                                            startActivity(i);
                                        }
                                    });
                                    errorMessage.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                            System.exit(0);
                                        }
                                    });
                                    errorMessage.show();

                                }
                            }
                            if (move%2 == 1 && gameType == 'C' && computerEntryKey == 0){
                                int tmpcol = attack3();
                                if(tmpcol<0)
                                    tmpcol = defence3();
                                if (tmpcol<0)
                                    tmpcol = defence2_1();
                                if(tmpcol<0)
                                    tmpcol = attack2();
                                if(tmpcol<0)
                                    tmpcol = defaultMove();

                                int tmprow = usable(tmpcol)- 1;
                                cellButtons[tmprow][tmpcol].setBackground(redScaled);
                                gameCells[tmprow][tmpcol].setCell('O');
                                history+=tmpcol;
                                ++move;
                                if (end(cellButtons)) { //oyun sonlandıysa kazandı mesajını bas ve yeniden başlat
                                    if(time!=0) {
                                        cdt.cancel();
                                    }
                                    AlertDialog.Builder errorMessage = new AlertDialog.Builder(context);
                                    errorMessage.setTitle("Game Finished!");
                                    if (check(cellButtons) == 2) {
                                        errorMessage.setMessage("Computer is Winner !");
                                    }
                                    else{
                                        errorMessage.setMessage("The game ended in a draw !");
                                    }
                                    errorMessage.setCancelable(false);
                                    errorMessage.setPositiveButton("Menu", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(GameScreen.this, MenuScreen.class);
                                            finish();
                                            startActivity(i);
                                        }
                                    });
                                    errorMessage.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                            System.exit(0);
                                        }
                                    });
                                    errorMessage.show();

                                }

                            }
                        }
                    }
                });
            }
        }

        for (int i = 0 ; i < size ; ++i) {
            for (int j = 0; j < size; ++j) {
                board.addView(cellButtons[i][j]);
            }
        }

        sv.post(new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    /**
     * Undos
     * @param ibb Game Board
     */
    private void undo(ImageButton [][] ibb){
        int tmpcol = Integer.parseInt(Character.toString(history.charAt(history.length()-1)));

        for(int i=0 ; i<size ; ++i){								//undo operation
            if(gameCells[i][tmpcol].getCell()!='.'){
                gameCells[i][tmpcol].setCell('.');
                ibb[i][tmpcol].setBackground(emptyScaled);
                break;
            }
        }
        String tmphistory="";
        for(int i = 0; i < history.length()-1 ; ++i){
            tmphistory+=history.charAt(i);
        }
        history = tmphistory;
    }

    /**
     * Check whether the game has a winner
     * @param ibb Game Board
     * @return  0 to draw, 1 to user1,2 to user2
     */
    private int check(ImageButton [][] ibb) {   //kazananın olup olmadığını kontrol eder
        int status;

        status = horizontalCheck(ibb);
        if (status == 0) {
            status = verticalCheck(ibb);
            if (status == 0) {
                status = rightCrossCheck(ibb);
                if (status == 0) {
                    status = leftCrossCheck(ibb);
                }
            }
        }
        return status;
    }

    /**
     * Checks Horizontal Line
     * @param ibb   Game Board
     * @return  0 to draw, 1 to user1,2 to user2
     */
    private int horizontalCheck(ImageButton [][] ibb){//yatay kontrol
        int status = 0;

        for (int i = size - 1; i >= 0; --i) {
            for (int j = 0; j < size - 3; ++j) {
                if ((gameCells[i][j].getCell() == 'X' && gameCells[i][j + 1].getCell() == 'X' && gameCells[i][j + 2].getCell() == 'X' && gameCells[i][j + 3].getCell() == 'X')
                        || (gameCells[i][j].getCell() == 'x' && gameCells[i][j + 1].getCell() == 'x' && gameCells[i][j + 2].getCell() == 'x' && gameCells[i][j + 3].getCell() == 'x')) {
                    //user1 kazandıysa oluşan dörtlüyü işaretle
                    status = 1;
                    ibb[i][j].setBackground(tickedBlueScaled);
                    ibb[i][j+1].setBackground(tickedBlueScaled);
                    ibb[i][j+2].setBackground(tickedBlueScaled);
                    ibb[i][j+3].setBackground(tickedBlueScaled);
                    gameCells[i][j].setCell('x');
                    gameCells[i][j + 1].setCell('x');
                    gameCells[i][j + 2].setCell('x');
                    gameCells[i][j + 3].setCell('x');
                } else if ((gameCells[i][j].getCell() == 'O' && gameCells[i][j + 1].getCell() == 'O' && gameCells[i][j + 2].getCell() == 'O' && gameCells[i][j + 3].getCell() == 'O')
                        || (gameCells[i][j].getCell() == 'o' && gameCells[i][j + 1].getCell() == 'o' && gameCells[i][j + 2].getCell() == 'o' && gameCells[i][j + 3].getCell() == 'o')) {
                    //user2 kazandıysa oluşan dörtlüyü işaratle
                    status = 2;
                    ibb[i][j].setBackground(tickedRedScaled);
                    ibb[i][j+1].setBackground(tickedRedScaled);
                    ibb[i][j+2].setBackground(tickedRedScaled);
                    ibb[i][j+3].setBackground(tickedRedScaled);
                    gameCells[i][j].setCell('o');
                    gameCells[i][j + 1].setCell('o');
                    gameCells[i][j + 2].setCell('o');
                    gameCells[i][j + 3].setCell('o');
                }
            }
        }
        return status;
    }

    /**
     * Checks Vertical Line
     * @param ibb   Game Board
     * @return  0 to draw, 1 to user1,2 to user2
     */
    private int verticalCheck(ImageButton [][] ibb) {//dikey kontrol
        int status = 0;

        for (int i = 0; i < size; ++i) {
            for (int j = size - 1; j > 2; --j) {
                if ((gameCells[j][i].getCell() == 'X' && gameCells[j - 1][i].getCell() == 'X' && gameCells[j - 2][i].getCell() == 'X' && gameCells[j - 3][i].getCell() == 'X')
                        || (gameCells[j][i].getCell() == 'x' && gameCells[j - 1][i].getCell() == 'x' && gameCells[j - 2][i].getCell() == 'x' && gameCells[j - 3][i].getCell() == 'x')) {
                    //user1 kazandıysa oluşan dörtlüyü işaretle
                    status = 1;
                    ibb[j][i].setBackground(tickedBlueScaled);
                    ibb[j-1][i].setBackground(tickedBlueScaled);
                    ibb[j-2][i].setBackground(tickedBlueScaled);
                    ibb[j-3][i].setBackground(tickedBlueScaled);
                    gameCells[j][i].setCell('x');
                    gameCells[j - 1][i].setCell('x');
                    gameCells[j - 2][i].setCell('x');
                    gameCells[j - 3][i].setCell('x');
                } else if ((gameCells[j][i].getCell() == 'O' && gameCells[j - 1][i].getCell() == 'O' && gameCells[j - 2][i].getCell() == 'O' && gameCells[j - 3][i].getCell() == 'O')
                        || (gameCells[j][i].getCell() == 'o' && gameCells[j - 1][i].getCell() == 'o' && gameCells[j - 2][i].getCell() == 'o' && gameCells[j - 3][i].getCell() == 'o')) {
                    //user2 kazandıysa oluşan dörtlüyü işaretle
                    status = 2;
                    ibb[j][i].setBackground(tickedRedScaled);
                    ibb[j-1][i].setBackground(tickedRedScaled);
                    ibb[j-2][i].setBackground(tickedRedScaled);
                    ibb[j-3][i].setBackground(tickedRedScaled);
                    gameCells[j][i].setCell('o');
                    gameCells[j - 1][i].setCell('o');
                    gameCells[j - 2][i].setCell('o');
                    gameCells[j - 3][i].setCell('o');
                }
            }
        }
        return status;
    }

    /**
     * Checks Right Cross Line
     * @param ibb   Game Board
     * @return  0 to draw, 1 to user1,2 to user2
     */
    private int rightCrossCheck(ImageButton [][] ibb) { //sağ çapraz kontrol
        int status = 0;

        for (int j = size - 1; j > 2; --j) {
            for (int i = 0; i < size - 3; ++i) {
                if ((gameCells[j][i].getCell() == 'X' && gameCells[j - 1][i + 1].getCell() == 'X' && gameCells[j - 2][i + 2].getCell() == 'X' && gameCells[j - 3][i + 3].getCell() == 'X')
                        || (gameCells[j][i].getCell() == 'x' && gameCells[j - 1][i + 1].getCell() == 'x' && gameCells[j - 2][i + 2].getCell() == 'x' && gameCells[j - 3][i + 3].getCell() == 'x')) {
                    //user1 kazandıysa oluşan dörtlüyü işaretle
                    status = 1;
                    ibb[j][i].setBackground(tickedBlueScaled);
                    ibb[j-1][i+1].setBackground(tickedBlueScaled);
                    ibb[j-2][i+2].setBackground(tickedBlueScaled);
                    ibb[j-3][i+3].setBackground(tickedBlueScaled);
                    gameCells[j][i].setCell('x');
                    gameCells[j - 1][i + 1].setCell('x');
                    gameCells[j - 2][i + 2].setCell('x');
                    gameCells[j - 3][i + 3].setCell('x');
                } else if ((gameCells[j][i].getCell() == 'O' && gameCells[j - 1][i + 1].getCell() == 'O' && gameCells[j - 2][i + 2].getCell() == 'O' && gameCells[j - 3][i + 3].getCell() == 'O')
                        || (gameCells[j][i].getCell() == 'o' && gameCells[j - 1][i + 1].getCell() == 'o' && gameCells[j - 2][i + 2].getCell() == 'o' && gameCells[j - 3][i + 3].getCell() == 'o')) {
                    //user2 kazandıysa oluşan dörtlüyü işaretle
                    status = 2;
                    ibb[j][i].setBackground(tickedRedScaled);
                    ibb[j-1][i+1].setBackground(tickedRedScaled);
                    ibb[j-2][i+2].setBackground(tickedRedScaled);
                    ibb[j-3][i+3].setBackground(tickedRedScaled);
                    gameCells[j][i].setCell('o');
                    gameCells[j - 1][i + 1].setCell('o');
                    gameCells[j - 2][i + 2].setCell('o');
                    gameCells[j - 3][i + 3].setCell('o');
                }
            }
        }
        return status;
    }

    /**
     * Checks Left Cross Line
     * @param ibb Game Board
     * @return  0 to draw, 1 to user1,2 to user2
     */
    private int leftCrossCheck(ImageButton [][] ibb) {//solçapraz kontrol
        int status = 0;

        for (int j = size - 1; j > 2; --j) {
            for (int i = size - 1; i > 2; --i) {
                if ((gameCells[j][i].getCell() == 'X' && gameCells[j - 1][i - 1].getCell() == 'X' && gameCells[j - 2][i - 2].getCell() == 'X' && gameCells[j - 3][i - 3].getCell() == 'X')
                        || (gameCells[j][i].getCell() == 'x' && gameCells[j - 1][i - 1].getCell() == 'x' && gameCells[j - 2][i - 2].getCell() == 'x' && gameCells[j - 3][i - 3].getCell() == 'x')) {
                    //user1 kazandıysa oluşan dörtlüyü işaretle
                    status = 1;
                    ibb[j][i].setBackground(tickedBlueScaled);
                    ibb[j-1][i-1].setBackground(tickedBlueScaled);
                    ibb[j-2][i-2].setBackground(tickedBlueScaled);
                    ibb[j-3][i-3].setBackground(tickedBlueScaled);
                    gameCells[j][i].setCell('x');
                    gameCells[j - 1][i - 1].setCell('x');
                    gameCells[j - 2][i - 2].setCell('x');
                    gameCells[j - 3][i - 3].setCell('x');
                } else if ((gameCells[j][i].getCell() == 'O' && gameCells[j - 1][i - 1].getCell() == 'O' && gameCells[j - 2][i - 2].getCell() == 'O' && gameCells[j - 3][i - 3].getCell() == 'O')
                        || (gameCells[j][i].getCell() == 'o' && gameCells[j - 1][i - 1].getCell() == 'o' && gameCells[j - 2][i - 2].getCell() == 'o' && gameCells[j - 3][i - 3].getCell() == 'o')) {
                    //user2 kazandıysa oluşan dörtlüyü işaretle
                    status = 2;
                    ibb[j][i].setBackground(tickedRedScaled);
                    ibb[j-1][i-1].setBackground(tickedRedScaled);
                    ibb[j-2][i-2].setBackground(tickedRedScaled);
                    ibb[j-3][i-3].setBackground(tickedRedScaled);
                    gameCells[j][i].setCell('o');
                    gameCells[j - 1][i - 1].setCell('o');
                    gameCells[j - 2][i - 2].setCell('o');
                    gameCells[j - 3][i - 3].setCell('o');
                }
            }
        }
        return status;
    }

    /**
     * Checks whether game is over
     * @param ibb
     * @return
     */
    private boolean end(ImageButton [][] ibb){
        if(check(ibb)!=0 || fillCheck()==true)
            return true;
        else
            return false;
    }

    /**
     *  Counts usable cells number on selected column
     * @param col column number
     * @return number of usable cells of selected column
     */
    private int usable(int col){    //bir kolonda kaç adet oynanabilir cell var onu sayıyor
        int sum=0;
        for(int i = 0 ; i<size ;++i){
            if(gameCells[i][col].getCell() == '.')
                ++sum;
        }
        return sum;
    }

    /**
     *  Checks whether selected cell coordination is legal
     * @param r row
     * @param c column
     * @return  True or False
     */
    private boolean isLegal(int r, int c){
        boolean status=false;

        if(r < size && r >= 0 && c < size && c >= 0){
            if(gameCells[r][c].getCell()=='.'){
                if(r != size-1 && r+1<=size-1 && gameCells[r+1][c].getCell()!='.')
                    status = true;
                else if(r==size-1)
                    status = true;
            }
        }
        return status;
    }

    /**
     * A part of AI
     * @return  column number
     */
    private int defaultMove(){
        int status=-1;

        for (int i = 0; i < size ; ++i) {
            for (int j = size - 1; j >= 0 ; --j) {
                if (gameCells[j][i].getCell() == 'X') {
                    if (isLegal(j,i+1)){
                        status=i+1;
                        return status;
                    }
                    else if (isLegal(j,i-1)){
                        status=i-1;
                        return status;
                    }
                    else if (isLegal(j-1,i)){
                        status=i;
                        return status;
                    }
                }
            }
        }
        return status;
    }

    /**
     * initializes the gameCells
     */
    private void initCell(){
        gameCells = new Cell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                gameCells[i][j] = new Cell();
            }
        }
    }

    /**
     * checks whether game board is filled
     * @return
     */
    private boolean fillCheck(){
        boolean status=true;

        for(int i=0 ; i<size ; ++i){
            for(int j=0; j<size ; ++j){
                if(gameCells[i][j].getCell()=='.')
                    return false;
            }
        }
        return status;
    }

    /**
     * triple to quadruple
     * @return a column number
     */
    private int attack3() {
        int status;
        if (attack3Vertical() > 0) {
            status = attack3Vertical();
            return status;
        } else if (attack3Horizontal() > 0) {
            status = attack3Horizontal();
            return status;
        } else if (attack3RightCross() > 0) {
            status = attack3RightCross();
            return status;
        } else if (attack3LeftCross() > 0) {
            status = attack3LeftCross();
            return status;
        } else {
            status = -1;
        }

        return status;
    }

    /**
     * triple to quadruple on Horizontal Line
     * @return a column number
     */
    private int attack3Horizontal(){    //yatay 3lü atak fonksiyonu
        int status = -1;

        for (int i = size - 1; i >= 0; --i) {
            for (int j = 0; j < size - 2; ++j) {
                if (gameCells[i][j].getCell() == 'O' && gameCells[i][j + 1].getCell() == 'O' && gameCells[i][j + 2].getCell() == 'O') {
                    if (isLegal(i, j - 1)) {
                        status = j - 1;
                    } else if (isLegal(i, j + 3)) {
                        status = j + 3;
                    }
                }
            }
        }
        return status;
    }

    /**
     * triple to quadruple on Vertical Line
     * @return a column number
     */
    private int attack3Vertical() { //dikey 3lü atak fonksiyonu
        int status = -1;

        for (int i = 0; i < size; ++i) {
            for (int j = size - 1; j > 2; --j) {
                if (gameCells[j][i].getCell() == 'O' && gameCells[j - 1][i].getCell() == 'O' && gameCells[j - 2][i].getCell() == 'O') {
                    if (isLegal(j - 3, i)) {
                        status = i;
                    }
                }
            }
        }
        return status;
    }

    /**
     * triple to quadruple on Right Cross Line
     * @return a column number
     */
    private int attack3RightCross() {   //sağ çapraz 3lü atak fonksiyonu
        int status = -1;

        for (int j = size - 1; j > 2; --j) {
            for (int i = 0; i < size - 3; ++i) {
                if (gameCells[j][i].getCell() == 'O' && gameCells[j - 1][i + 1].getCell() == 'O' && gameCells[j - 2][i + 2].getCell() == 'O') {
                    if (isLegal(j - 3, i + 3)) {
                        status = i + 3;
                    } else if (isLegal(j + 1, i - 1)) {
                        status = i - 1;
                    }
                }
            }
        }
        return status;
    }

    /**
     * triple to quadruple on Left Cross Line
     * @return a column number
     */
    private int attack3LeftCross() {    //solçapraz üçlü atak fonksiyonu
        int status = -1;

        for (int j = size - 1; j > 2; --j) {
            for (int i = size - 1; i > 2; --i) {
                if (gameCells[j][i].getCell() == 'O' && gameCells[j - 1][i - 1].getCell() == 'O' && gameCells[j - 2][i - 2].getCell() == 'O') {
                    if (isLegal(j - 3, i - 3)) {
                        status = i - 3;
                    } else if (isLegal(j + 1, i + 1)) {
                        status = i + 1;
                    }
                }
            }
        }
        return status;
    }

    /**
     * blocks a triple combination
     * @return a column number
     */
    private int defence3() {
        int status;
        if (defence3Vertical() > 0) {
            status = defence3Vertical();
            return status;
        } else if (defence3Horizontal() > 0) {
            status = defence3Horizontal();
            return status;
        } else if (defence3RightCross() > 0) {
            status = defence3RightCross();
            return status;
        } else if (defence3LeftCross() > 0) {
            status = defence3LeftCross();
            return status;
        } else {
            status = -1;
        }

        return status;
    }

    /**
     * blocks a triple combination on Horizontal Line
     * @return a column number
     */
    private int defence3Horizontal(){   //yatay 3lü defans fonksiyonu
        int status = -1;

        for (int i = size - 1; i >= 0; --i) {
            for (int j = 0; j < size - 2; ++j) {
                if (gameCells[i][j].getCell() == 'X' && gameCells[i][j + 1].getCell() == 'X' && gameCells[i][j + 2].getCell() == 'X') {
                    if (isLegal(i, j - 1)) {
                        status = j - 1;
                        return status;
                    } else if (isLegal(i, j + 3)) {
                        status = j + 3;
                        return status;
                    }
                }
            }
        }
        return status;
    }

    /**
     * blocks a triple combination on Vertical Line
     * @return a column number
     */
    private int defence3Vertical() {    //dikey üçlü defans fonksiyonu
        int status = -1;

        for (int i = 0; i < size; ++i) {
            for (int j = size - 1; j > 2; --j) {
                if (gameCells[j][i].getCell() == 'X' && gameCells[j - 1][i].getCell() == 'X' && gameCells[j - 2][i].getCell() == 'X') {
                    if (isLegal(j - 3, i)) {
                        status = i;
                        return status;
                    }
                }
            }
        }
        return status;
    }

    /**
     * blocks a triple combination on Right Cross Line
     * @return a column number
     */
    private int defence3RightCross() {  // sağ çapraz üçlü defans fonksiyonu
        int status = -1;

        for (int j = size - 1; j > 2; --j) {
            for (int i = 0; i < size - 3; ++i) {
                if (gameCells[j][i].getCell() == 'X' && gameCells[j - 1][i + 1].getCell() == 'X' && gameCells[j - 2][i + 2].getCell() == 'X') {
                    if (isLegal(j - 3, i + 3)) {
                        status = i + 3;
                        return status;
                    } else if (isLegal(j + 1, i - 1)) {
                        status = i - 1;
                        return status;
                    }
                }
            }
        }
        return status;
    }

    /**
     * blocks a triple combination on Left Cross Line
     * @return a column number
     */
    private int defence3LeftCross() {   //sol çapraz üçlü defans fonksiyonu
        int status = -1;

        for (int j = size - 1; j > 2; --j) {
            for (int i = size - 1; i > 2; --i) {
                if (gameCells[j][i].getCell() == 'X' && gameCells[j - 1][i - 1].getCell() == 'X' && gameCells[j - 2][i - 2].getCell() == 'X') {
                    if (isLegal(j - 3, i - 3)) {
                        status = i - 3;
                        return status;
                    } else if (isLegal(j + 1, i + 1)) {
                        status = i + 1;
                        return status;
                    }
                }
            }
        }
        return status;
    }

    /**
     * blocks XX.X or X.XX
     * @return a column number
     */
    private int defence2_1() {
        int status;
        if (defence2_1Horizontal() > 0) {
            status = defence2_1Horizontal();
            return status;
        } else if (defence2_1RightCross() > 0) {
            status = defence2_1RightCross();
            return status;
        } else if (defence2_1LeftCross() > 0) {
            status = defence2_1LeftCross();
            return status;
        } else {
            status = -1;
        }

        return status;
    }

    /**
     * blocks XX.X or X.XX on Horizontal Line
     * @return a column number
     */
    private int defence2_1Horizontal() {
        int status = -1;

        for(int i=size-1 ; i>=0 ; --i) {
            for (int j = 0; j < size - 3; ++j) {
                if (gameCells[i][j].getCell() == 'X' && gameCells[i][j + 1].getCell() == 'X' && gameCells[i][j + 3].getCell() == 'X' && isLegal(i, j + 2)) {
                    status = j + 2;
                    return status;
                } else if (gameCells[i][j].getCell() == 'X' && gameCells[i][j + 2].getCell() == 'X' && gameCells[i][j + 3].getCell() == 'X' && isLegal(i, j + 1)) {
                    status = j + 1;
                    return status;
                }
            }
        }
        return status;
    }

    /**
     * blocks XX.X or X.XX on Right Cross Line
     * @return a column number
     */
    private int defence2_1RightCross() {
        int status = -1;
        
        for(int j=size-1 ; j>2 ; --j) {
            for (int i = 0; i < size - 3; ++i) {
                if (gameCells[j][i].getCell() == 'X' && gameCells[j - 1][i + 1].getCell() == 'X' && gameCells[j - 3][i + 3].getCell() == 'X' && isLegal(j - 2, i + 2)) {
                    status = i + 2;
                    return status;
                } else if (gameCells[j][i].getCell() == 'X' && gameCells[j - 2][i + 2].getCell() == 'X' && gameCells[j - 3][i + 3].getCell() == 'X' && isLegal(j - 1, i + 1)) {
                    status = i + 1;
                    return status;
                }
            }
        }
        return status;
    }

    /**
     * blocks XX.X or X.XX on Left Cross Line
     * @return a column number
     */
    private int defence2_1LeftCross() {
        int status = -1;

        for(int j=size-1 ; j>2 ; --j)
            for(int i=size-1 ; i>2 ; --i){
                if(gameCells[j][i].getCell()=='X' && gameCells[j-1][i-1].getCell()=='X' && gameCells[j-3][i-3].getCell()=='X' && isLegal(j-2,i-2)){
                    status=i-2;
                    return status;
                }
                else if(gameCells[j][i].getCell()=='X' && gameCells[j-2][i-2].getCell()=='X' && gameCells[j-3][i-3].getCell()=='X' && isLegal(j-1,i-1)){
                    status=i-1;
                    return status;
                }
            }
        return status;
    }

    /**
     * double to triple
     * @return a column number
     */
    private int attack2() {
        int status;
        if (attack2Horizontal() > 0) {
            status = attack2Horizontal();
            return status;
        } else if (attack2Vertical() > 0) {
            status = attack2Vertical();
            return status;
        } else if (attack2RightCross() > 0) {
            status = attack2RightCross();
            return status;
        } else if (attack2LeftCross() > 0) {
            status = attack2LeftCross();
            return status;
        } else {
            status = -1;
        }

        return status;
    }

    /**
     * double to triple on Horizontal Line
     * @return a column number
     */
    private int attack2Horizontal() {
        int status = -1;

        for(int i=size-1 ; i>=0 ; --i)
            for(int j=0 ; j<size-1 ; ++j){
                if(gameCells[i][j].getCell()=='O' && gameCells[i][j+1].getCell()=='O'){
                    if(isLegal(i,j-1)){
                        status=j-1;
                        return status;
                    }
                    else if(isLegal(i,j+2)){
                        status=j+2;
                        return status;
                    }
                }
            }
        return status;
    }

    /**
     * double to triple on Vertical Line
     * @return a column number
     */
    private int attack2Vertical() {
        int status=-1;

        for(int i=0 ; i<size ; ++i)
            for(int j=size-1 ; j>1 ; --j){
                if(gameCells[j][i].getCell()=='O' && gameCells[j-1][i].getCell()=='O'){
                    if(isLegal(j-2,i)){
                        status=i;
                        return status;
                    }
                }
            }
        return status;
    }

    /**
     * double to triple on Right Cross Line
     * @return a column number
     */
    private int attack2RightCross() {
        int status=-1;
        
        for(int j=size-1 ; j>1 ; --j)
            for(int i=0 ; i<size-2 ; ++i){
                if(gameCells[j][i].getCell()=='O' && gameCells[j-1][i+1].getCell()=='O'){
                    if(isLegal(j-2,i+2)){
                        status=i+2;
                        return status;
                    }
                }
            }
        return status;
    }

    /**
     * double to triple on Left Cross Line
     * @return a column number
     */
    private int attack2LeftCross() {
        int status=-1;

        for(int j=size-1 ; j>1 ; --j)
            for(int i=size-1 ; i>1 ; --i){
                if(gameCells[j][i].getCell()=='O' && gameCells[j-1][i-1].getCell()=='O'){
                    if(isLegal(j-2,i-2)){
                        status=i-2;
                        return status;
                    }
                }
            }
        return status;
    }
}
