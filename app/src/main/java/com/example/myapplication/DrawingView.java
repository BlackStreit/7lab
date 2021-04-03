package com.example.myapplication;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.util.AttributeSet;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;

//Класс для рисования
//наследуется от View(местный канвас)
public class DrawingView extends View {

    //Стираем ли?
    private boolean erase=false;
    //Рисуен путь(линии рисования)
    private Path drawPath;
    //Рисуем и отображаем
    private Paint drawPaint, canvasPaint;
    //Устанавливаем цвет
    private int paintColor = 0xFF660000;
    //То, где рисуем
    private Canvas drawCanvas;
    //Что рисуем
    private Bitmap canvasBitmap;

    //Текущий размер, последний
    private float brushSize, lastBrushSize;

    //Перерисовка
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Холст
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        //ПУти рисования
        canvas.drawPath(drawPath, drawPaint);
    }

    //Конструктор для инициализации
    public DrawingView(Context context) {
        super(context);
    }

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }
    //Установка рисовальщина
   public void setupDrawing(){
        //Инициализация объектов
       drawPath = new Path();
       drawPaint = new Paint();
       //Установили первоначальный цвет
       drawPaint.setColor(paintColor);
       //Все это отвечает за размеры кисти
       drawPaint.setAntiAlias(true);
       drawPaint.setStyle(Paint.Style.STROKE);
       drawPaint.setStrokeJoin(Paint.Join.ROUND);
       drawPaint.setStrokeCap(Paint.Cap.ROUND);
       //Нарисовали
       canvasPaint = new Paint(Paint.DITHER_FLAG);
       //Установить новый размер
       drawPaint.setStrokeWidth(brushSize);
       //Сохранить старый
       lastBrushSize = brushSize;
   }

   //Вызывается, когда моему View присвается размер
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //Отправка рахмеров в супер-класс
        super.onSizeChanged(w, h, oldw, oldh);
        //Создать область рисования
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        //Добавить эту область на канвас
        drawCanvas = new Canvas(canvasBitmap);
    }

    //Вызывается при касании экрана
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Текущие ккординаты касания
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            //При нажатии на экран
            case MotionEvent.ACTION_DOWN:
                //Начинаем рисовку
                drawPath.moveTo(touchX, touchY);
                break;
                //При движении по жкрану
            case MotionEvent.ACTION_MOVE:
                //Рисуем
                drawPath.lineTo(touchX, touchY);
                break;
                //При прекращении касания
            case MotionEvent.ACTION_UP:
                //Прорисовать путь до конца
                drawCanvas.drawPath(drawPath, drawPaint);
                //Очистить путь
                drawPath.reset();
                break;
                //Если ничего вернуть фалс
            default:
                return false;
        }
        //Вызывает OnDraw
        invalidate();
        //Все хорошо
        return true;
    }

    //Выбор цвета
    public void setColor(String newColor){
        //Вызвать onDraw
        invalidate();
        //Переписывает цвет
        paintColor = Color.parseColor(newColor);
        //Устанливаем цвет
        drawPaint.setColor(paintColor);
    }

    //Установить размер кисти
    public void setBrushSize(float newSize){
        //Обновляем цвета
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize=pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }
    //Геттеры
    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }
    public float getLastBrushSize(){
        return lastBrushSize;
    }

    //Простой сетер
    public void setErase(boolean isErase){
        //Присвоить значение
        erase=isErase;
        //Переключаемся на стирание
        if(erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        //Либо на рисование
        else drawPaint.setXfermode(null);
    }
    //новый
    public void startNew(){
        //Очисть окно
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        //Вызвать onDraw
        invalidate();
    }

}
