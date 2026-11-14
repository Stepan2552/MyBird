package io.github.Stepan2552.characters; // Пакет персонажей и игровых объектов

import com.badlogic.gdx.graphics.Texture; // Импортируем класс Texture для работы с изображениями
import com.badlogic.gdx.graphics.g2d.Batch; // Импортируем класс Batch для пакетной отрисовки графики
import com.badlogic.gdx.graphics.g2d.TextureRegion; // Импортируем TextureRegion для продолжения ствола трубы
import io.github.Stepan2552.Main; // Импортируем класс Main
import java.util.Random; // Импортируем класс Random для генерации случайных высот прохода

public class Tube { // Класс Tube, представляющий пару труб (верхнюю и нижнюю)

    public float x; // Горизонтальная координата X расположения колонны
    public float gapY; // Вертикальная координата Y центра прохода между трубами
    public float width = 180f; // Оптимальная ширина колонны (180 пикселей)
    float naturalHeight; // Натуральная высота текстуры трубы по пропорциям 1:1 (без растягивания!)
    float gapHeight = 400f; // Высота свободного прохода между трубами (400 пикселей)
    float padding = 100f; // Минимальный отступ прохода от краев экрана
    float speed = 300f; // Динамичная скорость перемещения колонн влево
    float distanceBetweenTubes = 600f; // Постоянное идеальное расстояние между соседними колоннами

    boolean isPointReceived = false; // Флаг состояния: было ли уже начислено очко за пролет этой трубы

    Random random; // Генератор случайных чисел для высоты прохода
    Texture textureUpperTube; // Текстура верхней (перевёрнутой) трубы
    Texture textureDownTube; // Текстура нижней трубы
    TextureRegion upperBodyRegion; // Текстурный срез ствола верхней трубы
    TextureRegion downBodyRegion; // Текстурный срез ствола нижней трубы

    public Tube(int tubeCount, int tubeIdx) { // Конструктор колонны
        random = new Random(); // Инициализируем генератор случайных чисел

        float currentWorldHeight = Main.getWorldHeight(); // Получаем настоящую видимую высоту экрана

        // Расчёт случайной высоты Y центра прохода
        gapY = gapHeight / 2 + padding + random.nextInt((int) Math.max(1, currentWorldHeight - 2 * (padding + gapHeight / 2)));

        // Позиция X каждой трубы выстраивается с четким шагом
        x = Main.SCR_WIDTH + (distanceBetweenTubes * tubeIdx);

        // Загружаем изображения труб из ресурсов папки assets
        textureUpperTube = new Texture("tubes/tube_flipped.png"); // Загружаем перевернутую трубу
        textureDownTube = new Texture("tubes/tube.png"); // Загружаем обычную нижнюю трубу

        // Вычисляем натуральную высоту текстуры с сохранением пропорций 1:1
        naturalHeight = width * ((float) textureDownTube.getHeight() / textureDownTube.getWidth());

        // Вырезаем гладкие срезы ствола трубы без ободка
        int capPixels = (int) (textureDownTube.getHeight() * 0.2f);
        int bodyPixels = textureDownTube.getHeight() - capPixels;

        upperBodyRegion = new TextureRegion(textureUpperTube, 0, 0, textureUpperTube.getWidth(), bodyPixels);
        downBodyRegion = new TextureRegion(textureDownTube, 0, capPixels, textureDownTube.getWidth(), bodyPixels);
    }

    public void setSpeed(float speed) { // Метод динамического изменения скорости
        this.speed = speed;
    }

    public void move(float delta, int tubeCount) { // Метод перемещения колонны влево
        x -= speed * delta;

        if (x < -width) { // Только когда колонна ПОЛНОСТЬЮ ушла за левый край экрана
            isPointReceived = false; // Сбрасываем флаг получения очка
            x += distanceBetweenTubes * tubeCount; // Сдвигаем на полный цикл
            gapY = gapHeight / 2 + padding + random.nextInt((int) Math.max(1, Main.getWorldHeight() - 2 * (padding + gapHeight / 2)));
        }
    }

    public void draw(Batch batch) { // Метод для отрисовки пары труб на экране
        float upperY = gapY + gapHeight / 2;
        float lowerY = gapY - gapHeight / 2 - naturalHeight;

        batch.draw(textureUpperTube, x, upperY, width, naturalHeight);

        if (upperY + naturalHeight < Main.getWorldHeight() + 300) {
            float extHeight = (Main.getWorldHeight() + 300) - (upperY + naturalHeight);
            batch.draw(upperBodyRegion, x, upperY + naturalHeight, width, extHeight);
        }

        batch.draw(textureDownTube, x, lowerY, width, naturalHeight);

        if (lowerY > -300) {
            float extHeight = lowerY - (-300);
            batch.draw(downBodyRegion, x, -300, width, extHeight);
        }
    }

    public void dispose() { // Метод очистки оперативной памяти от загруженных текстур
        textureDownTube.dispose();
        textureUpperTube.dispose();
    }

    public boolean isHit(Bird bird) { // Метод проверки точного столкновения
        float paddingX = 15f;
        float paddingY = 10f;

        float birdLeft = bird.x + paddingX;
        float birdRight = bird.x + bird.width - paddingX;
        float birdBottom = bird.y + paddingY;
        float birdTop = bird.y + bird.height - paddingY;

        float lowerPipeTop = gapY - gapHeight / 2;
        float upperPipeBottom = gapY + gapHeight / 2;

        boolean inTubeX = birdRight >= x && birdLeft <= x + width;

        if (inTubeX) {
            if (birdBottom <= lowerPipeTop) return true;
            if (birdTop >= upperPipeBottom) return true;
        }

        return false;
    }

    public boolean needAddPoint(Bird bird) { // Проверка начисления балла
        return bird.x > x + width && !isPointReceived;
    }

    public void setPointReceived() { // Фиксация балла
        isPointReceived = true;
    }
}
