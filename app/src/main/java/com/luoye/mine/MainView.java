package com.luoye.mine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zyw on 2017/6/7.
 */
public class MainView extends View {
    private   Mine mine;
    private  boolean isFirst=true;//标记是否是本局第一次点击屏幕
    private Context context;
    private final int mineNum=10;//产生的雷的个数
    private  final int ROW=15;//要生成的矩阵高
    private  final int COL=8;//要生成的矩阵宽
    private   int TILE_WIDTH=50;//块大小
    private  boolean isFalse=false;
    public  MainView(Context context)
    {
        super(context);
        this.context=context;

        TILE_WIDTH=MainActivity.W/10;
        mine=new Mine((MainActivity.W-COL*TILE_WIDTH)/2,(MainActivity.H-ROW*TILE_WIDTH)/2,COL,ROW,mineNum,TILE_WIDTH);
        try {
            mine.init();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 游戏逻辑
     */
    public void logic()
    {
        int count=0;

        for (int i=0;i<mine.mapRow;i++)
        {
            for (int j=0;j<mine.mapCol;j++)
            {
                if(!mine.tile[i][j].open)
                {
                    count++;
                }
            }
        }
        //逻辑判断是否胜利
        if(count==mineNum)
        {
            new AlertDialog.Builder(context)
                    .setMessage("恭喜你，你找出了所有雷")
                    .setCancelable(false)
                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            mine.init();
                            invalidate();
                            isFirst=true;
                        }
                    })
                    .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .create()
                    .show();
        }
    }


    /**
     * 刷新View
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        mine.draw(canvas);
    }

    /**
     * 点击屏幕事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN)
        {
            int x=(int)event.getX();
            int y=(int)event.getY();
            //判断是否点在范围内
            if(x>=mine.x&&y>=mine.y&&x<=(mine.mapWidth+mine.x)&&y<=(mine.y+mine.mapHeight))
            {
                int idxX=(x-mine.x)/mine.tileWidth;
                int idxY=(y-mine.y)/mine.tileWidth;
                mine.open(new Mine.Point(idxX,idxY),isFirst);
                isFirst=false;

                if(mine.tile[idxY][idxX].value==-1)
                {
                    mine.isDrawAllMine=true;
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setMessage("很遗憾，你踩到雷了！")
                            .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mine.init();
                                    isFalse=true;
                                    isFirst=true;

                                    invalidate();
                                }
                            })
                            .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(0);
                                }
                            })
                            .create()
                            .show();
                }
                if(isFalse)
                {
                    isFalse=false;
                    invalidate();
                    return true;
                }
                logic();

                invalidate();
            }

        }
        return true;
    }
}
