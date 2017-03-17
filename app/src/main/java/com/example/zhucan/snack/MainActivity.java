package com.example.zhucan.snack;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.example.zhucan.snack.util.Coord;
import com.example.zhucan.snack.util.Queue;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
   //屏幕的长宽
    private int width;
    private int height;
    //一节的长度
    private int size;
    //横向屏幕分为20个列
    private int numcolumn=20;
    //自定义的双向链表存放蛇身体的所有坐标
    private Queue snackQueue = new Queue();
    //获取后的全部蛇身坐标list
    private List<Coord> resultQueue;
    //横向和纵向的每一次位移距离
    private int Xspeed=0;
    private int Yspeed=1;
    //touchEvent的距离差来判断是往哪个方向移动
    private float downX,downY;
    private float upX,upY;
    //判断是否结束的旗标
    private boolean isLose=false;
    //蛇头的现坐标
    private int Xnow,Ynow;
    //蛇头的位移的下一个像素点
    private float X,Y;
    //死亡后的显示字
    private String LOSE="you are loser!";
    //定义的虫子初始坐标
    private int wormX=10;
    private int wormY=15;
    //游戏分数
    private int score;
    //每次运动的时间间隔
    private int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //创建自定义view
        final SnackView view = new SnackView(this);
        //通过windowManager
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
       //获取屏幕长宽
        width = metrics.widthPixels;
        height = metrics.heightPixels;

        setContentView(view);

        //设置分数和时间的初值
        score=0;
        time=500;

        //设置定时器开自动让蛇动起来
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //获取头坐标
                Xnow = snackQueue.getFirst().getX();
                Ynow = snackQueue.getFirst().getY();
                X = (Xnow + Xspeed) * size;
                Y = (Ynow + Yspeed) * size;

                //判断是否撞墙(就是碰到屏幕四周)以及是否咬到尾巴
                if (X < 0 || X >=width || Y < 0 || Y >= height - 80 ||eat(snackQueue)) {
                    //如果死亡设置旗标为true
                    isLose = true;
                    //判断是否蛇头吃到虫子
                } else if (Xnow+Xspeed==wormX&&Ynow+Yspeed==wormY){
                    //蛇长加1
                    snackQueue.insert(Xnow + Xspeed, Ynow + Yspeed);
                    //重新随机虫子的坐标
                    wormX=(int)(Math.random()*numcolumn);
                    wormY=(int)(Math.random()*(30));
                    //分数增加
                    score++;
                }else {
                    snackQueue.insert(Xnow + Xspeed, Ynow + Yspeed);
                    snackQueue.deleteEnd();

                }
                //更新view绘制
                view.postInvalidate();
            }
        }, 0, time);

        //这个定时器的主要作用就是慢慢加快蛇的速度
        final Timer timer1=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (time>300){
                    time--;
                }
            }
        },0,2000);
    }

    //判断是否咬尾
        public boolean eat(Queue queue){
            List<Coord> list=queue.showAll();
            Coord now=queue.getFirst();
            Coord current;
            for (int i=1;i<list.size();i++){
                current= list.get(i);
                if (current.getX()==now.getX()&&current.getY()==now.getY()){
                    return true;
                }
            }
            return false;
        }


//自定义的蛇view
    public class SnackView extends View {
        Paint paint;

        public SnackView(Context context) {
            super(context);
           //初始的蛇位置
            snackQueue.insert(1, 1);
            snackQueue.insert(1, 2);
            snackQueue.insert(1, 3);
            snackQueue.insert(1, 4);

            paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
        }

        protected void onDraw(Canvas canvas) {
            int x, y;
            //获取蛇所有坐标
            resultQueue = snackQueue.showAll();
            canvas.drawColor(Color.WHITE);
            //判断是否死亡
            if (isLose){
            //死亡显示的界面
            paint.setTextSize(50);
                paint.setColor(Color.RED);
            canvas.drawText(LOSE,width/2-100,300,paint);
                canvas.drawText("your score is  "+score,width/2-120,400,paint);

        }
            else {
                //绘制蛇和虫子
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(wormX*size+size/2,wormY*size+size/2,size/2,paint);
                 paint.setStyle(Paint.Style.STROKE);
                for (int i = 0; i < resultQueue.size(); i++) {
                    x = resultQueue.get(i).getX();
                    y = resultQueue.get(i).getY();
                    canvas.drawRect(x * size, y * size, (x + 1) * size, (y + 1) * size, paint);
                }
            }}

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int wightSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            //设置size的值
            size = wightSize /numcolumn;
            setMeasuredDimension(wightSize, heightSize);
        }
        @Override
        public boolean onTouchEvent(MotionEvent event){
            //判断滑屏的方向来动态的改变蛇方向
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    downX=event.getX();
                    downY=event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    upX=event.getX();
                    upY=event.getY();
                    float x=upX-downX;
                    float y=upY-downY;
                    if(Math.abs(x)>Math.abs(y)){
                        if (x>0){
                            //右移
                            if(Xspeed==0){
                                Yspeed=0;
                                Xspeed=1;
                            }
                        }else {
                            //左移
                            if(Xspeed==0){
                                Yspeed=0;
                                Xspeed=-1;
                            }
                        }

                    }else {
                        if (y>0){
                            //下移
                            if (Yspeed==0){
                                Xspeed=0;
                                Yspeed=1;
                            }
                        }else {
                            //上移
                            if (Yspeed==0){
                                Xspeed=0;
                                Yspeed=-1;
                            }
                        }
                    }

            }
            return true;
        }




    }

}
