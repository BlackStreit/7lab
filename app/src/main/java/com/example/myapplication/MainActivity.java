package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Toast;

//Интерфейс отслеживает названия
public class MainActivity extends AppCompatActivity implements OnClickListener {

    //Создаем переменную класса
    private DrawingView drawView;
    //Создаем все кнопки : кнопка цвета, кнопка кисти, стерка, новый, сохранить
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Передать id для рисования
        drawView = (DrawingView)findViewById(R.id.drawing);
        //Лейаут, отвещающий за кнопки цвета
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        //Найти первую кнопку
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        //Показать что выбрана текущая кнопка
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        //Установить значения из ресурсов
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        //Создать кнопку
        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        //Добавить кнопку
        drawBtn.setOnClickListener(this);
        //Установить средний размер по умолчанию
        drawView.setBrushSize(mediumBrush);
        //Стерка
        eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);
        //Новый
        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);
        //Сохранить
        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
    }

    //Размеры кнопок(кисти)
    private float smallBrush, mediumBrush, largeBrush;

    //Тут мы выбираем цвета
    public void paintClicked(View view){
        //Вытащить размер кисти до стирания
        drawView.setBrushSize(drawView.getLastBrushSize());
        drawView.setErase(false);
        if(view!=currPaint){
            //Найти тег кнопки
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            //Передать цвет кноки
            drawView.setColor(color);
            //Отобразить текущую кнопку
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
        }
    }

    @Override
    public void onClick(View view) {
        //Если выбрали рисовать
        if(view.getId()==R.id.draw_btn){
            //Меню выбора
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setContentView(R.layout.brush_chooser);
            //Создать кнопки и дбавить слушателей
            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(v -> {
                //Считать все размеры
                drawView.setBrushSize(smallBrush);
                drawView.setLastBrushSize(smallBrush);
                //Показать что рисуем
                drawView.setErase(false);
                brushDialog.dismiss();
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(v -> {
                drawView.setBrushSize(mediumBrush);
                drawView.setLastBrushSize(mediumBrush);
                drawView.setErase(false);
                brushDialog.dismiss();
            });

            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(v -> {
                drawView.setBrushSize(largeBrush);
                drawView.setLastBrushSize(largeBrush);
                drawView.setErase(false);
                brushDialog.dismiss();
            });
            //Отобразить выбор
            brushDialog.show();
        }
        //Если выбрана стерка
        else if(view.getId()==R.id.erase_btn){
            //Отобразить меню выбора
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setContentView(R.layout.brush_chooser);
            //Тоже самое чтои выше, только для стерки
            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(v -> {
                //Передать что стираем
                drawView.setErase(true);
                //Передать что малым
                drawView.setBrushSize(smallBrush);
                brushDialog.dismiss();
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(v -> {
                drawView.setErase(true);
                drawView.setBrushSize(mediumBrush);
                brushDialog.dismiss();
            });
            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(v -> {
                drawView.setErase(true);
                drawView.setBrushSize(largeBrush);
                brushDialog.dismiss();
            });
            brushDialog.show();
        }
        //Если новый
        else if(view.getId()==R.id.new_btn){
            //Спросить хочет ли пользователь начать новый
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("Новый рисунок");
            newDialog.setMessage("Хотите начать новый рисунок(текущий будет утерян)?");
            //Если да
            newDialog.setPositiveButton("Да", (dialog, which) -> {
                drawView.startNew();
                dialog.dismiss();
            });
            //Если нет
            newDialog.setNegativeButton("Закрыть", (dialog, which) -> dialog.cancel());
            newDialog.show();
        }
        //Сохранялся
        else if(view.getId()==R.id.save_btn){
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            //Хотим ли сохранять?
            saveDialog.setTitle("Сохранение рисунка");
            saveDialog.setMessage("Хотите сохранить рисунок в галерее?");
            //Если да
            saveDialog.setPositiveButton("Да", (dialog, which) -> {
                //Дейтсвие в потоке
                Runnable runnable = () -> {
                    //Кеш рисунка
                    drawView.setDrawingCacheEnabled(true);
                    //Избегание исключений и успешное сохранение
                    drawView.post(() -> {
                        String imgSaved = MediaStore.Images.Media.insertImage(
                                getContentResolver(), drawView.getDrawingCache(),
                                UUID.randomUUID().toString()+".png", "drawing");
                        //Пост сообщение
                        if(imgSaved!=null){
                            Toast savedToast = Toast.makeText(getApplicationContext(),
                                    "Картинка сохранена! ^_^", Toast.LENGTH_SHORT);
                            savedToast.show();
                        }
                        else{
                            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                    "Что-то пошло не так Q_Q", Toast.LENGTH_SHORT);
                            unsavedToast.show();
                        }
                        //Уничтожить кеш рисунка
                        drawView.destroyDrawingCache();
                    });
                };
                // Определяем объект Thread - новый поток
                Thread thread = new Thread(runnable);
                // Запускаем поток
                thread.start();
            });
            //Если не хотим сохранять
            saveDialog.setNegativeButton("Закрыть", (dialog, which) -> dialog.cancel());
            saveDialog.show();
        }
    }

}