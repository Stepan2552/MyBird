package io.github.Stepan2552.components; // Пакет компонентов игры

import com.badlogic.gdx.graphics.Texture; // Импортируем класс Texture для работы с изображениями
import com.badlogic.gdx.graphics.g2d.Batch; // Импортируем Batch для отрисовки графики
import io.github.Stepan2552.Main; // Импортируем главный класс Main

public class MovingBackground { // Класс MovingBackground, отвечающий за анимацию и скроллинг фоновой картинки
    Texture texture; // Текстура фонового изображения
    float texture1X; // Координата X первого экземпляра фона
    float texture2X; // Координата X второго экземпляра фона для бесшовного зацикливания
    float speed = 120f; // Динамическая скорость перемещения фона влево

    public MovingBackground() { // Дефолтный конструктор со стандартным фоном
        this("game_bg.png");
    }

    public MovingBackground(String pathToTexture) { // Универсальный конструктор с передачей пути к текстуре
        texture = new Texture(pathToTexture); // Загружаем переданную картинку фона из папки assets
        reset();
    }

    public void reset() { // Сброс позиции для устранения черного экрана на старте
        texture1X = 0; // Первый фон встает от начала экрана
        texture2X = Main.getWorldWidth(); // Второй фон встает сразу за первым за правым краем
    }

    public void setSpeed(float speed) { // Метод обновления скорости фона
        this.speed = speed;
    }

    public void move(float delta) { // Метод перемещения фона влево
        float worldWidth = Main.getWorldWidth();
        texture1X -= speed * delta; // Сдвигаем первый фон влево
        texture2X -= speed * delta; // Сдвигаем второй фон влево

        // Бесшовный перенос первой картинки за вторую при выезде за левый край
        if (texture1X <= -worldWidth) {
            texture1X = texture2X + worldWidth;
        }
        // Бесшовный перенос второй картинки за первую при выезде за левый край
        if (texture2X <= -worldWidth) {
            texture2X = texture1X + worldWidth;
        }
    }

    public void draw(Batch batch) { // Метод для двухсегментной бесшовной отрисовки фона с авто-коррекцией зазоров
        float worldWidth = Main.getWorldWidth();
        float worldHeight = Main.getWorldHeight();

        // Авто-коррекция при первом запуске: если зазор между картинками не равен ширине мира — корректно стыкуем!
        if (Math.abs(texture2X - (texture1X + worldWidth)) > 1f && Math.abs(texture1X - (texture2X + worldWidth)) > 1f) {
            texture1X = 0;
            texture2X = worldWidth;
        }

        // Рисуем первую половину фона с перекрытием +2px
        batch.draw(texture, texture1X, 0, worldWidth + 2f, worldHeight);
        // Рисуем вторую половину фона с перекрытием +2px
        batch.draw(texture, texture2X, 0, worldWidth + 2f, worldHeight);
    }

    public void dispose() { // Метод очистки оперативной памяти от текстуры фона
        texture.dispose(); // Освобождаем ресурсы текстуры
    }
}
