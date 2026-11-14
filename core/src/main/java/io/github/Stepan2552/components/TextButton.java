package io.github.Stepan2552.components; // Пакет компонентов UI

import com.badlogic.gdx.graphics.Color; // Импортируем класс Color
import com.badlogic.gdx.graphics.Texture; // Импортируем класс Texture
import com.badlogic.gdx.graphics.g2d.Batch; // Импортируем Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont; // Импортируем BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout; // Импортируем GlyphLayout для вычисления размеров текста

public class TextButton { // Класс текстовой кнопки TextButton
    BitmapFont font; // Шрифт текста на кнопке
    String text; // Текст надписи на кнопке
    Texture texture; // Текстура фонового изображения кнопки

    int x, y; // Координаты X и Y положения кнопки на экране
    int textX, textY; // Вычисленные координаты X и Y расположения текста по центру кнопки
    int buttonWidth, buttonHeight; // Ширина и высота кнопки
    int textWidth, textHeight; // Ширина и высота текста

    public TextButton(int x, int y, String text) { // Конструктор текстовой кнопки
        this.text = text; // Сохраняем переданный текст
        this.x = x; // Сохраняем координату X
        this.y = y; // Сохраняем координату Y

        font = new BitmapFont(); // Создаём дефолтный шрифт LibGDX
        font.getData().setScale(5f); // Масштабируем размер шрифта в 5 раз
        font.setColor(Color.WHITE); // Устанавливаем белый цвет текста

        // Рассчитываем габариты текста с помощью GlyphLayout
        GlyphLayout gl = new GlyphLayout(font, text);
        textWidth = (int) gl.width; // Ширина текста в пикселях
        textHeight = (int) gl.height; // Высота текста в пикселях

        texture = new Texture("button_bg.png"); // Загружаем фоновую текстуру кнопки из папки assets
        buttonWidth = texture.getWidth(); // Сохраняем ширину картинки кнопки
        buttonHeight = texture.getHeight(); // Сохраняем высоту картинки кнопки

        // Формула центрирования текста строго посередине кнопки по горизонтали (X) и вертикали (Y)
        textX = x + (buttonWidth - textWidth) / 2;
        textY = y + (buttonHeight + textHeight) / 2;
    }

    public void draw(Batch batch) { // Метод отрисовки кнопки и текста на ней
        batch.draw(texture, x, y, buttonWidth, buttonHeight); // Рисуем фоновую картинку кнопки
        font.draw(batch, text, textX, textY); // Рисуем отцентрованный текст поверх кнопки
    }

    public boolean isHit(int touchX, int touchY) { // Проверка клика по кнопке
        return touchX >= x && touchX <= x + buttonWidth && touchY >= y && touchY <= y + buttonHeight;
    }

    public void dispose() { // Метод очистки оперативной памяти
        font.dispose(); // Освобождаем ресурсы шрифта
        texture.dispose(); // Освобождаем ресурсы текстуры
    }
}
