package me.michaeljiang.movesystem;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import me.michaeljiang.movesystem.movesystem.MoveSystem;
import me.michaeljiang.movesystem.movesystem.setting.ProjectSetting;

public class MainActivity extends AppCompatActivity {
    /** Called when the activity is first created. */
    private MoveSystem moveSystem;
    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        init();
//        onMessageReceiver();
    }

    private boolean setPosition(int chessType,String position){
        try{
            return moveSystem.move(chessType,position,false);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean setPosition(int chessType,int row,int col){
        try{
            return moveSystem.move(chessType,row,col,false);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private  boolean catchChess(int chessType,String position){
        try{
            return moveSystem.move(chessType,position,true);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private  boolean catchChess(int chessType,int row,int col){
        try{
            return moveSystem.move(chessType,row,col,true);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean testBowl(int number){
        if(number > 4)
            return false;
        //单点拿起放下测试
        for(int i = 0; i < number;i++){
            catchChess(ProjectSetting.CHESS_BOWL,0,i);
            setPosition(ProjectSetting.CHESS_BOWL,0,i);

            catchChess(ProjectSetting.CHESS_BOWL,1,i);
            setPosition(ProjectSetting.CHESS_BOWL,1,i);

            catchChess(ProjectSetting.CHESS_BOWL,2,i);
            setPosition(ProjectSetting.CHESS_BOWL,2,i);

            catchChess(ProjectSetting.CHESS_BOWL,3,i);
            setPosition(ProjectSetting.CHESS_BOWL,3,i);

            catchChess(ProjectSetting.CHESS_BOWL,4,i);
            setPosition(ProjectSetting.CHESS_BOWL,4,i);

            catchChess(ProjectSetting.CHESS_BOWL,5,i);
            setPosition(ProjectSetting.CHESS_BOWL,5,i);

            catchChess(ProjectSetting.CHESS_BOWL,6,i);
            setPosition(ProjectSetting.CHESS_BOWL,6,i);

            catchChess(ProjectSetting.CHESS_BOWL,7,i);
            setPosition(ProjectSetting.CHESS_BOWL,7,i);
        }
        return true;
    }

    private void testChessBoard(){
        //棋盘下子基础测试
        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"A1");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"A2");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"A2");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"A3");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"A3");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"A4");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"A4");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"A5");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"A5");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"A6");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"A6");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"A7");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"A7");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"A8");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"B1");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"B2");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"B2");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"B3");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"B3");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"B4");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"B4");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"B5");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"B5");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"B6");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"B6");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"B7");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"B7");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"B8");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"C1");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"C2");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"C2");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"C3");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"C3");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"C4");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"C4");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"C5");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"C5");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"C6");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"C6");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"C7");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"C7");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"C8");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"D1");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"D2");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"D2");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"D3");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"D3");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"D4");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"D4");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"D5");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"D5");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"D6");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"D6");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"D7");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"D7");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"D8");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"E1");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"E2");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"E2");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"E3");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"E3");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"E4");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"E4");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"E5");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"E5");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"E6");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"E6");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"E7");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"E7");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"E8");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"F1");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"F2");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"F2");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"F3");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"F3");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"F4");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"F4");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"F5");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"F5");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"F6");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"F6");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"F7");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"F7");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"F8");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"G1");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"G2");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"G2");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"G3");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"G3");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"G4");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"G4");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"G5");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"G5");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"G6");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"G6");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"G7");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"G7");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"G8");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"H1");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"H2");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"H2");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"H3");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"H3");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"H4");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"H4");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"H5");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"H5");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"H6");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"H6");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"H7");

        catchChess(ProjectSetting.INTERNATIONAL_CHESS,"H7");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"H8");
    }

    private void testPlayChess(){
        //码棋基础测试
        catchChess(ProjectSetting.CHESS_BOWL,"B2");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"A8");

        catchChess(ProjectSetting.CHESS_BOWL,"C2");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"B8");

        catchChess(ProjectSetting.CHESS_BOWL,"D2");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"C8");

        catchChess(ProjectSetting.CHESS_BOWL,"E2");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"D8");

        catchChess(ProjectSetting.CHESS_BOWL,"F2");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"E8");

        catchChess(ProjectSetting.CHESS_BOWL,"G2");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"F8");

        catchChess(ProjectSetting.CHESS_BOWL,"H2");
        setPosition(ProjectSetting.INTERNATIONAL_CHESS,"H8");
    }

    private void initChessBoard(){
        //CHESS_BOEL B1-H4
        //IntelnationChess A1-B8 G1-H8
        for(int i = 0; i < 4; i++){
            //
            catchChess(ProjectSetting.CHESS_BOWL,0,i);
            setPosition(ProjectSetting.INTERNATIONAL_CHESS,0,i);

            catchChess(ProjectSetting.CHESS_BOWL,1,i);
            setPosition(ProjectSetting.INTERNATIONAL_CHESS,0,i+4);

            //
            catchChess(ProjectSetting.CHESS_BOWL,2,i);
            setPosition(ProjectSetting.INTERNATIONAL_CHESS,1,i);

            catchChess(ProjectSetting.CHESS_BOWL,3,i);
            setPosition(ProjectSetting.INTERNATIONAL_CHESS,1,i+4);

            //
            catchChess(ProjectSetting.CHESS_BOWL,4,i);
            setPosition(ProjectSetting.INTERNATIONAL_CHESS,6,i);

            catchChess(ProjectSetting.CHESS_BOWL,5,i);
            setPosition(ProjectSetting.INTERNATIONAL_CHESS,6,i+4);

            //
            catchChess(ProjectSetting.CHESS_BOWL,6,i);
            setPosition(ProjectSetting.INTERNATIONAL_CHESS,7,i);

            catchChess(ProjectSetting.CHESS_BOWL,7,i);
            setPosition(ProjectSetting.INTERNATIONAL_CHESS,7,i+4);
        }
    }

    private void init() {
        moveSystem = new MoveSystem(this);
        moveSystem.initSystem();
        Button btn_rest = (Button)findViewById(R.id.btn_rest);
        btn_rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveSystem.reset();
            }
        });

        Button btn_next = (Button)findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveSystem.dontUse();
            }
        });

        Button btn_connect = (Button)findViewById(R.id.btn_connect);
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveSystem.connectMqtt();
                moveSystem.connectBluetooth();
            }
        });

        Button btn_disconnect = (Button)findViewById(R.id.btn_disconnect);
        btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveSystem.disconnectMqtt();
                moveSystem.disconnectBluetooth();
            }
        });

        Button btn_do = (Button)findViewById(R.id.btn_do);
        btn_do.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                initChessBoard();
//                testPlayChess();
//                testBowl(4);
//                testChessBoard();
                initChessBoard();
            }
        });

        Button btn_A1 = (Button) findViewById(R.id.btn_A1);
        Button btn_A2 = (Button) findViewById(R.id.btn_A2);
        Button btn_A3 = (Button) findViewById(R.id.btn_A3);
        Button btn_A4 = (Button) findViewById(R.id.btn_A4);
        Button btn_A5 = (Button) findViewById(R.id.btn_A5);
        Button btn_A6 = (Button) findViewById(R.id.btn_A6);
        Button btn_A7 = (Button) findViewById(R.id.btn_A7);
        Button btn_A8 = (Button) findViewById(R.id.btn_A8);
        Button btn_B1 = (Button) findViewById(R.id.btn_B1);
        Button btn_B2 = (Button) findViewById(R.id.btn_B2);
        Button btn_B3 = (Button) findViewById(R.id.btn_B3);
        Button btn_B4 = (Button) findViewById(R.id.btn_B4);
        Button btn_B5 = (Button) findViewById(R.id.btn_B5);
        Button btn_B6 = (Button) findViewById(R.id.btn_B6);
        Button btn_B7 = (Button) findViewById(R.id.btn_B7);
        Button btn_B8 = (Button) findViewById(R.id.btn_B8);
        Button btn_C1 = (Button) findViewById(R.id.btn_C1);
        Button btn_C2 = (Button) findViewById(R.id.btn_C2);
        Button btn_C3 = (Button) findViewById(R.id.btn_C3);
        Button btn_C4 = (Button) findViewById(R.id.btn_C4);
        Button btn_C5 = (Button) findViewById(R.id.btn_C5);
        Button btn_C6 = (Button) findViewById(R.id.btn_C6);
        Button btn_C7 = (Button) findViewById(R.id.btn_C7);
        Button btn_C8 = (Button) findViewById(R.id.btn_C8);
        Button btn_D1 = (Button) findViewById(R.id.btn_D1);
        Button btn_D2 = (Button) findViewById(R.id.btn_D2);
        Button btn_D3 = (Button) findViewById(R.id.btn_D3);
        Button btn_D4 = (Button) findViewById(R.id.btn_D4);
        Button btn_D5 = (Button) findViewById(R.id.btn_D5);
        Button btn_D6 = (Button) findViewById(R.id.btn_D6);
        Button btn_D7 = (Button) findViewById(R.id.btn_D7);
        Button btn_D8 = (Button) findViewById(R.id.btn_D8);
        Button btn_E1 = (Button) findViewById(R.id.btn_E1);
        Button btn_E2 = (Button) findViewById(R.id.btn_E2);
        Button btn_E3 = (Button) findViewById(R.id.btn_E3);
        Button btn_E4 = (Button) findViewById(R.id.btn_E4);
        Button btn_E5 = (Button) findViewById(R.id.btn_E5);
        Button btn_E6 = (Button) findViewById(R.id.btn_E6);
        Button btn_E7 = (Button) findViewById(R.id.btn_E7);
        Button btn_E8 = (Button) findViewById(R.id.btn_E8);
        Button btn_F1 = (Button) findViewById(R.id.btn_F1);
        Button btn_F2 = (Button) findViewById(R.id.btn_F2);
        Button btn_F3 = (Button) findViewById(R.id.btn_F3);
        Button btn_F4 = (Button) findViewById(R.id.btn_F4);
        Button btn_F5 = (Button) findViewById(R.id.btn_F5);
        Button btn_F6 = (Button) findViewById(R.id.btn_F6);
        Button btn_F7 = (Button) findViewById(R.id.btn_F7);
        Button btn_F8 = (Button) findViewById(R.id.btn_F8);
        Button btn_G1 = (Button) findViewById(R.id.btn_G1);
        Button btn_G2 = (Button) findViewById(R.id.btn_G2);
        Button btn_G3 = (Button) findViewById(R.id.btn_G3);
        Button btn_G4 = (Button) findViewById(R.id.btn_G4);
        Button btn_G5 = (Button) findViewById(R.id.btn_G5);
        Button btn_G6 = (Button) findViewById(R.id.btn_G6);
        Button btn_G7 = (Button) findViewById(R.id.btn_G7);
        Button btn_G8 = (Button) findViewById(R.id.btn_G8);
        Button btn_H1 = (Button) findViewById(R.id.btn_H1);
        Button btn_H2 = (Button) findViewById(R.id.btn_H2);
        Button btn_H3 = (Button) findViewById(R.id.btn_H3);
        Button btn_H4 = (Button) findViewById(R.id.btn_H4);
        Button btn_H5 = (Button) findViewById(R.id.btn_H5);
        Button btn_H6 = (Button) findViewById(R.id.btn_H6);
        Button btn_H7 = (Button) findViewById(R.id.btn_H7);
        Button btn_H8 = (Button) findViewById(R.id.btn_H8);

        btn_A1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPosition(ProjectSetting.INTERNATIONAL_CHESS,"A1");
            }
        });
        btn_A2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"A2");
            }
        });
        btn_A3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"A3");
            }
        });
        btn_A4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"A4");
            }
        });
        btn_A5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"A5");
            }
        });
        btn_A6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"A6");
            }
        });
        btn_A7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"A7");
            }
        });
        btn_A8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"A8");
                setPosition(ProjectSetting.INTERNATIONAL_CHESS,"A7");
            }
        });
        btn_B1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"B1");
            }
        });
        btn_B2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"B2");
            }
        });
        btn_B3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"B3");
            }
        });
        btn_B4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"B4");
            }
        });
        btn_B5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"B5");
            }
        });
        btn_B6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"B6");
            }
        });
        btn_B7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"B7");
            }
        });
        btn_B8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"B8");
                setPosition(ProjectSetting.INTERNATIONAL_CHESS,"B7");
            }
        });
        btn_C1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"C1");
            }
        });
        btn_C2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"C2");
            }
        });
        btn_C3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"C3");
            }
        });
        btn_C4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"C4");
            }
        });
        btn_C5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"C5");
            }
        });
        btn_C6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"C6");
            }
        });
        btn_C7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"C7");
            }
        });
        btn_C8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"C8");
                setPosition(ProjectSetting.INTERNATIONAL_CHESS,"C7");
            }
        });
        btn_D1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"D1");
            }
        });
        btn_D2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"D2");
            }
        });
        btn_D3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"D3");
            }
        });
        btn_D4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"D4");
            }
        });
        btn_D5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"D5");
            }
        });
        btn_D6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"D6");
            }
        });
        btn_D7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"D7");
            }
        });
        btn_D8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"D8");
                setPosition(ProjectSetting.INTERNATIONAL_CHESS,"D7");
            }
        });
        btn_E1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"E1");
            }
        });
        btn_E2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"E2");
            }
        });
        btn_E3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"E3");
            }
        });
        btn_E4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"E4");
            }
        });
        btn_E5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"E5");
            }
        });
        btn_E6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"E6");
            }
        });
        btn_E7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"E7");
            }
        });
        btn_E8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"E8");
                setPosition(ProjectSetting.INTERNATIONAL_CHESS,"E7");
            }
        });
        btn_F1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"F1");
            }
        });
        btn_F2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"F2");
            }
        });
        btn_F3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"F3");
            }
        });
        btn_F4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"F4");
            }
        });
        btn_F5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"F5");
            }
        });
        btn_F6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"F6");
            }
        });
        btn_F7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"F7");
            }
        });
        btn_F8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"F8");
                setPosition(ProjectSetting.INTERNATIONAL_CHESS,"F7");
            }
        });
        btn_G1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"G1");
            }
        });
        btn_G2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"G2");
            }
        });
        btn_G3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"G3");
            }
        });
        btn_G4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"G4");
            }
        });
        btn_G5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"G5");
            }
        });
        btn_G6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"G6");
            }
        });
        btn_G7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"G7");
            }
        });
        btn_G8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"G8");
                setPosition(ProjectSetting.INTERNATIONAL_CHESS,"G7");
            }
        });
        btn_H1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"H1");
            }
        });
        btn_H2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"H2");
            }
        });
        btn_H3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"H3");
            }
        });
        btn_H4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"H4");
            }
        });
        btn_H5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"H5");
            }
        });
        btn_H6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"H6");
            }
        });
        btn_H7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"H7");
            }
        });
        btn_H8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchChess(ProjectSetting.INTERNATIONAL_CHESS,"H8");
                setPosition(ProjectSetting.INTERNATIONAL_CHESS,"H7");
            }
        });
    }

}
