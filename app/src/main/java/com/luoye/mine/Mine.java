package com.luoye.mine;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
/**
 * Created by zyw on 2016/7/29.
 */
public class Mine {
    public   int x;//地图的在屏幕上的坐标点
    public   int y;//地图的在屏幕上的坐标点
    public    int mapCol;//矩阵宽
    public   int mapRow;//矩阵高
    private  int mineNum ;
    public static short EMPTY=0;//空
    public static short MINE=-1;//雷
    public Tile[][] tile;//地图矩阵
    public   int tileWidth;//块宽
    private  Paint textPaint;
    private Paint bmpPaint;
    private  Paint tilePaint;
    private  Paint rectPaint;
    private  Paint minePaint;
    private Random rd=new Random();
    public  int mapWidth;//绘图区宽
    public int mapHeight;//绘图区高
    public boolean isDrawAllMine=false;//标记是否画雷
    private  int[][] dir={
            {-1,1},//左上角
            {0,1},//正上
            {1,1},//右上角
            {-1,0},//正左
            {1,0},//正右
            {-1,-1},//左下角
            {0,-1},//正下
            {1,-1}//右下角
    };//表示八个方向

    public   class Tile{
        short value;
        boolean flag;
        boolean open;
        public Tile()
        {
            this.value=0;
            this.flag=false;
            this.open=false;
        }
    }

    public static class Point{
        private int x;
        private int y;
        public Point(int x,int y)
        {
            this.x=x;
            this.y=y;
        }

        @Override
        public int hashCode() {
            // TODO Auto-generated method stub
            return 2*x+y;
        }

        @Override
        public boolean equals(Object obj) {
            // TODO Auto-generated method stub
            return this.hashCode()==((Point)(obj)).hashCode();

        }
    }//表示每个雷块


    public Mine(int x, int y, int mapCol, int mapRow, int mineNum, int tileWidth)
    {
        this.x=x;
        this.y=y;
        this.mapCol = mapCol;
        this.mapRow = mapRow;
        this.mineNum=mineNum;
        this.tileWidth=tileWidth;
        mapWidth=mapCol*tileWidth;
        mapHeight=mapRow*tileWidth;

        textPaint=new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(MainActivity.W/10);
        textPaint.setColor(Color.RED);

        bmpPaint=new Paint();
        bmpPaint.setAntiAlias(true);
        bmpPaint.setColor(Color.DKGRAY);

        tilePaint =new Paint();
        tilePaint.setAntiAlias(true);
        tilePaint.setColor(0xff1faeff);

        minePaint =new Paint();
        minePaint.setAntiAlias(true);
        minePaint.setColor(0xffff981d);

        rectPaint =new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setColor(0xff000000);
        rectPaint.setStyle(Paint.Style.STROKE);

        tile=new Tile[mapRow][mapCol];
    }
    /**
     * 初始化地图
     */
    public  void init()
    {
        for (int i = 0; i< mapRow; i++)
        {
            for (int j = 0; j< mapCol; j++)
            {
                tile[i][j]=new Tile();
                tile[i][j].value=EMPTY;
                tile[i][j].flag=false;
                tile[i][j].open=false;
                isDrawAllMine=false;
            }

        }
    }

    /**
     * 生成雷
     * @param exception 排除的位置，该位置不生成雷
     */
    public void create(Point exception)
    {
        List<Point> allPoint=new LinkedList<Point>();

        //把所有位置加入链表
        for (int i = 0; i< mapRow; i++)//y
        {
            for (int j = 0; j < mapCol; j++)//x
            {
                Point point=new Point(j,i);
                if(!point.equals(exception))
                {
                    allPoint.add(point);
                }
            }
        }

        List<Point> minePoint=new LinkedList<Point>();
        //随机产生雷
        for (int i=0; i< mineNum; i++)
        {
            int idx=rd.nextInt(allPoint.size());
            minePoint.add(allPoint.get(idx));
            allPoint.remove(idx);//取了之后，从所有集合中移除
        }

        //在矩阵中标记雷的位置
        for(Iterator<Point> it=minePoint.iterator();it.hasNext();)
        {
            Point p=it.next();
            tile[p.y][p.x].value=MINE;
        }

        //给地图添加数字
        for (int i = 0; i< mapRow; i++)//y
        {
            for (int j = 0; j< mapCol; j++)//x
            {
                short t=tile[i][j].value;
                if(t==MINE)
                {
                    for (int k=0;k<8;k++)
                    {
                        int offsetX=j+dir[k][0],offsetY=i+dir[k][1];
                        if(offsetX>=0&&offsetX< mapCol &&offsetY>=0&&offsetY< mapRow ) {
                            if (tile[offsetY][offsetX].value != -1)
                                tile[offsetY][offsetX].value += 1;
                        }
                    }
                }
            }
        }

    }


    /**
     * 打开某个位置
     * @param op
     * @param isFirst 标记是否是第一次打开
     */

    public void open(Point op,boolean isFirst)
    {
        if(isFirst)
        {
            create(op);
        }

        tile[op.y][op.x].open=true;
        if( tile[op.y][op.x].value==-1)
            return;
        else if( tile[op.y][op.x].value>0)//点中数字块
        {
            return;
        }

        //广度优先遍历用队列
        Queue<Point> qu=new LinkedList<Point>();
        //加入第一个点
        qu.offer(new Point(op.x,op.y));

        //朝8个方向遍历
        for (int i=0;i<8;i++)
        {
            int offsetX=op.x+dir[i][0],offsetY=op.y+dir[i][1];
            //判断越界和是否已访问
            boolean isCan=offsetX>=0&&offsetX< mapCol &&offsetY>=0&&offsetY< mapRow;
            if(isCan)
            {
                if(tile[offsetY][offsetX].value==0 &&!tile[offsetY][offsetX].open) {
                    qu.offer(new Point(offsetX, offsetY));
                }
                else if(tile[offsetY][offsetX].value>0)
                {
                    tile[offsetY][offsetX].open=true;
                }
            }

        }

        while(qu.size()!=0)
        {
            Point p=qu.poll();
            tile[p.y][p.x].open=true;
            for (int i=0;i<8;i++)
            {
                int offsetX=p.x+dir[i][0],offsetY=p.y+dir[i][1];
                //判断越界和是否已访问
                boolean isCan=offsetX>=0&&offsetX< mapCol &&offsetY>=0&&offsetY< mapRow;
                if(isCan)
                {
                    if( tile[offsetY][offsetX].value==0&&!tile[offsetY][offsetX].open) {
                        qu.offer(new Point(offsetX, offsetY));
                    }
                    else if(tile[offsetY][offsetX].value>0)
                    {
                        tile[offsetY][offsetX].open=true;
                    }
                }

            }
        }

    }

    /**
     * 绘制地图
     * @param canvas
     */
    public  void draw(Canvas canvas)
    {


        for (int i = 0; i< mapRow; i++)
        {
            for (int j = 0; j< mapCol; j++)
            {
                Tile t=tile[i][j];
                if(t.open){
                    if(t.value>0)
                    {
                        canvas.drawText(t.value+"",x+j*tileWidth,y+i*tileWidth+tileWidth,textPaint);
                    }

                }else
                {
                    //标记，备用
                    if(t.flag)
                    {

                    }else
                    {
                        //画矩形方块
                        RectF reactF=new RectF(x+j*tileWidth,y+i*tileWidth,x+j*tileWidth+tileWidth,y+i*tileWidth+tileWidth);
                        canvas.drawRoundRect(reactF,0,0, tilePaint);
                    }
                }
                //是否画出所有雷
                if( isDrawAllMine&&tile[i][j].value==-1) {
                    canvas.drawCircle((x + j * tileWidth) + tileWidth / 2, (y + i * tileWidth) + tileWidth / 2, tileWidth / 2, bmpPaint);
                }
            }
        }

        //画边框
        canvas.drawRect(x,y,x+mapWidth,y+mapHeight, rectPaint);
        //画横线
        for (int i = 0; i< mapRow; i++) {
            canvas.drawLine(x,y+i*tileWidth,x+mapWidth,y+i*tileWidth, rectPaint);
        }
        //画竖线
        for (int i = 0;i < mapCol; i++) {
            canvas.drawLine(x+i*tileWidth,y,x+i*tileWidth,y+mapHeight, rectPaint);
        }

    }

}
