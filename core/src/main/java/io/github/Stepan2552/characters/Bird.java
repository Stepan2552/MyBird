package io.github.Stepan2552.characters; // Пакет персонажей игры

import com.badlogic.gdx.graphics.Texture; // Импорт класса для работы с текстурами (картинками)
import com.badlogic.gdx.graphics.g2d.Batch; // Импорт класса для пакета отрисовки 2D-графики
import com.badlogic.gdx.math.MathUtils; // Импорт математических утилит для плавного наклона
import io.github.Stepan2552.Main; // Импортируем главный класс Main

public class Bird { // Объявление класса Bird (Птичка)

    int frameCounter; // Счётчик для замедления смены кадров анимации
    public float x, y; // Координаты X и Y положения птички на экране
    public float width, height; // Ширина и высота птички для отрисовки

    float velocity = 0; // Вертикальная скорость птички (положительная = летит вверх, отрицательная = падает)
    final float gravity = -1100f; // Умеренная гравитация для отзывчивого полёта
    final float jumpImpulse = 400f; // Энергичный импульс прыжка при клике
    float rotation = 0f; // Текущий угол наклона носа птички в градусах

    Texture texture; // Основная текстура птички
    Texture[] framesArray; // Массив текстур для кадров анимации полёта

    public Bird(float x, float y, Texture texture, float speed) { // Главный конструктор класса Bird
        this.x = x; // Сохраняем начальную координату X
        this.y = y; // Сохраняем начальную координату Y
        this.texture = texture; // Сохраняем переданную текстуру

        // Уменьшаем размер птички до 65% от оригинала для отличного маневрирования
        this.width = texture.getWidth() * 0.65f; // Уменьшенная ширина птички (65%)
        this.height = texture.getHeight() * 0.65f; // Уменьшенная высота птички (65%)

        // Создаем массив из 4 кадров для зацикленной анимации махов крыльев
        framesArray = new Texture[]{
            new Texture("bird0.png"), // Кадр 0: крылья вверху
            new Texture("bird1.png"), // Кадр 1: крылья посередине
            new Texture("bird2.png"), // Кадр 2: крылья внизу
            new Texture("bird1.png")  // Кадр 3: крылья посередине
        };
    }

    public void fly(float delta) { // Метод физики полёта с адаптацией под герцовку монитора (60-240Hz)
        float clampedDelta = Math.min(delta, 0.033f); // Фильтр от микролагов системы

        velocity += gravity * clampedDelta; // Гравитация плавно уменьшает скорость (тянет вниз)
        y += velocity * clampedDelta; // Изменяем высотную координату Y на величину текущей скорости

        // Ультра-плавная сглаженная интерполяция наклона носа птички под герцовку экрана
        float targetRotation = MathUtils.clamp(velocity * 0.08f, -60f, 25f);
        rotation = MathUtils.lerp(rotation, targetRotation, Math.min(1f, 16f * clampedDelta));

        // Не даем птичке улететь выше верхнего края экрана (ограничиваем потолком)
        if (y > Main.SCR_HEIGHT - height) {
            y = Main.SCR_HEIGHT - height; // Упираемся в потолок экрана
            velocity = 0; // Сбрасываем вертикальную скорость на потолке
        }
    }

    public void draw(Batch batch, boolean isGameOver) { // Метод отрисовки с идеальным плавным вращением
        int frameMultiplier = 10; // Множитель для замедления смены кадров
        Texture currentFrame = framesArray[frameCounter / frameMultiplier]; // Выбираем текущий кадр из массива

        // Отрисовываем птичку с вращением вокруг её собственного центра (width / 2f, height / 2f)
        batch.draw(currentFrame, x, y, width / 2f, height / 2f, width, height, 1f, 1f, rotation, 0, 0, currentFrame.getWidth(), currentFrame.getHeight(), false, false);

        if (!isGameOver) { // Переключаем кадры анимации крыльев ТОЛЬКО если игра активна
            if (++frameCounter >= framesArray.length * frameMultiplier) { // Увеличиваем счётчик
                frameCounter = 0; // Сбрасываем счётчик в 0 для зацикливания анимации
            }
        }
    }

    public void dispose() { // Метод очистки оперативной памяти
        texture.dispose(); // Освобождаем память от основной текстуры
        if (framesArray != null) { // Если массив кадров анимации создан
            for (Texture t : framesArray) { // В цикле удаляем кадры из памяти
                t.dispose(); // Освобождаем память каждой текстуры
            }
        }
    }

    public void onClick() { // Метод, вызываемый при клике игрока
        velocity = jumpImpulse; // Даем энергичный импульс вверх
    }

    public boolean isInField() { // Метод проверки, находится ли птичка в пределах игрового поля
        if (y <= 0) return false; // Игрок проигрывает только если птичка упала на дно (землю)
        return true; // Птичка находится на поле
    }
}
