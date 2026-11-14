package io.github.Stepan2552.screens; // Пакет игровых экранов

import com.badlogic.gdx.Gdx; // Импортируем класс Gdx для получения пользовательского ввода и системных данных
import com.badlogic.gdx.Screen; // Импортируем интерфейс Screen для создания экрана игры
import com.badlogic.gdx.graphics.Texture; // Импортируем класс Texture для работы с изображениями
import com.badlogic.gdx.math.MathUtils; // Импортируем утилиты математики для плавного парения птички
import com.badlogic.gdx.utils.ScreenUtils; // Импортируем утилиты для работы с экраном (очистка)
import io.github.Stepan2552.Main; // Импортируем класс Main
import io.github.Stepan2552.characters.Bird; // Импортируем класс Bird из пакета characters
import io.github.Stepan2552.characters.Tube; // Импортируем класс Tube из пакета characters
import io.github.Stepan2552.components.MovingBackground; // Импортируем класс MovingBackground из компонентов
import io.github.Stepan2552.components.PointCounter; // Импортируем класс PointCounter из компонентов

public class ScreenGame implements Screen { // Класс экрана игры, реализующий интерфейс Screen
    Main myGdxGame; // Храним ссылку на главный класс Main для доступа к камере и batch
    Texture birdTexture; // Базовая текстура птички
    MovingBackground background; // Объект движущегося фонового изображения
    Bird bird; // Объект персонажа птички

    float baseBgSpeed = 120f; // Базовая скорость прокрутки фона
    float currentBgSpeed = 120f; // Текущая динамическая скорость фона

    float baseTubeSpeed = 300f; // Базовая начальная скорость движения колонн

    int tubeCount = 3; // Общее количество создаваемых колонн на экране
    Tube[] tubes; // Массив объектов колонн (труб)
    boolean isGameOver = false; // Флаг состояния игры: true — врезались (проигрыш), false — играем
    boolean isVictory = false; // Флаг состояния победы: true — активирована секретная концовка (1000+ очков)
    float victoryAnimTimer = 0f; // Таймер анимации птички в концовке

    int gamePoints = 0; // Старт с 0 очков (нормальный игровой процесс)
    PointCounter pointCounter; // Объект для отображения счетчика очков на экране

    final int pointCounterMarginTop = 60; // Отступ счетчика от верхнего края экрана
    final int pointCounterMarginRight = 400; // Отступ счетчика от правого края экрана

    public ScreenGame(Main myGdxGame) { // Конструктор экрана игры
        this.myGdxGame = myGdxGame; // Сохраняем ссылку на объект Main
        birdTexture = new Texture("bird0.png"); // Загружаем базовую картинку птички из папки assets
        background = new MovingBackground("game_bg.png"); // Инициализируем объект фона с передачей пути к картинке

        // Инициализируем объект отображения очков с отступами от верхнего правого угла
        pointCounter = new PointCounter(Main.SCR_WIDTH - pointCounterMarginRight, Main.SCR_HEIGHT - pointCounterMarginTop);

        initGame(); // Вызываем инициализацию объектов игры
    }

    void initGame() { // Метод старта/перезапуска игры
        bird = new Bird(100, Main.getWorldHeight() / 2f, birdTexture, 300); // Создаём птичку по центру
        initTubes(); // Создаем массив из 3 колонн

        background.reset(); // Сбрасываем позиции фона для полного отсутствия черных промежутков при старте
        gamePoints = 0; // Сбрасываем набранные очки в 0
        currentBgSpeed = baseBgSpeed; // Сбрасываем скорость фона до базовой
        background.setSpeed(currentBgSpeed); // Устанавливаем базовую скорость фона
        isGameOver = false; // Сбрасываем флаг проигрыша
        isVictory = false; // Сбрасываем флаг победы
        victoryAnimTimer = 0f; // Сбрасываем таймер победы
    }

    void initTubes() { // Метод инициализации массива колонн
        tubes = new Tube[tubeCount]; // Выделяем память под массив из tubeCount элементов
        for (int i = 0; i < tubeCount; i++) { // Проходимся циклом по всем элементам массива
            tubes[i] = new Tube(tubeCount, i); // Создаем каждую трубу, передавая общее кол-во и её текущий индекс
        }
    }

    @Override // Переопределяем метод отображения
    public void show() { // Вызывается 1 раз при переключении на этот экран
        initGame(); // Инициализируем новую игру при каждом переключении на этот экран
    }

    @Override // Переопределяем метод отрисовки каждого кадра
    public void render(float delta) { // Главный игровой цикл (delta — время между кадрами в секундах)
        float clampedDelta = Math.min(delta, 0.033f); // Фильтр от микролагов системы

        if (Gdx.input.justTouched()) { // Проверяем, произошел ли клик мыши или касание пальцем по экрану
            if (isGameOver || isVictory) { // Если игра окончена или достигнута победа
                initGame(); // Повторный клик заново перезапускает игру
            } else {
                bird.onClick(); // В нормальной игре вызываем прыжок у птички
            }
        }

        if (!isGameOver && !isVictory) { // Если игра продолжается (нет проигрыша и нет победы) — обновляем физику
            bird.fly(clampedDelta); // Обновляем позицию птички с учетом времени clampedDelta

            // Высокий предел скорости (уровень 15 = 1050 px/s): очень быстрая, драйвовая и динамичная скорость в конце!
            int speedLevel = Math.min(gamePoints / 10, 15);
            float currentTubeSpeed = baseTubeSpeed + (speedLevel * 50f);
            currentBgSpeed = baseBgSpeed + (speedLevel * 20f);

            background.setSpeed(currentBgSpeed);
            for (Tube tube : tubes) {
                tube.setSpeed(currentTubeSpeed);
            }

            background.move(clampedDelta);

            for (Tube tube : tubes) { // В цикле проходимся по всем колоннам
                tube.move(clampedDelta, tubeCount); // Двигаем каждую колонну влево
            }

            // Проверяем коллизии и преодоление колонн
            for (Tube tube : tubes) {
                if (tube.isHit(bird)) { // Если птичка задела трубу
                    System.out.println("hit"); // Печатаем сообщении в лог
                    isGameOver = true; // Фиксируем проигрыш
                } else if (tube.needAddPoint(bird)) { // Если птичка перелетела трубу и балл еще не начислен
                    gamePoints += 1; // Прибавляем 1 балл
                    tube.setPointReceived(); // Фиксируем получение балла за эту трубу
                    System.out.println("Points: " + gamePoints); // Выводим текущие очки в консоль

                    if (gamePoints >= 1000) {
                        isVictory = true; // Активируем секретную концовку
                    }
                }
            }

            // Проверяем вылет птички за пределы экрана
            if (!bird.isInField()) {
                isGameOver = true; // Переключаем игру в режим проигрыша
            }

            // Передаем итоговые очки и переключаемся на экран рестарта ScreenRestart при проигрыше
            if (isGameOver) {
                myGdxGame.screenRestart.gamePoints = gamePoints; // Передаём очко во второй экран
                myGdxGame.setScreen(myGdxGame.screenRestart); // Смена экрана
            }
        }

        if (isVictory) { // Эпическая победная анимация парения птички в центре экрана
            victoryAnimTimer += clampedDelta;
            bird.x = Main.getWorldWidth() / 2f - bird.width / 2f; // Помещаем птичку по центру X
            bird.y = Main.getWorldHeight() / 2f - 30f + MathUtils.sin(victoryAnimTimer * 4f) * 20f; // Волнообразное парение
        }

        ScreenUtils.clear(0, 0, 0, 1); // Очищаем буфер экрана перед началом отрисовки

        myGdxGame.viewport.apply(); // Применяем актуальные пропорции мира из ExtendViewport
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined); // Передаем параметры камеры в инструмент отрисовки

        myGdxGame.batch.begin(); // Начинаем пакетную отрисовку графики

        // 1. Отрисовываем движущийся фон
        background.draw(myGdxGame.batch);

        // 2. Отрисовываем колонны только если игра продолжается
        if (!isVictory) {
            for (Tube tube : tubes) {
                tube.draw(myGdxGame.batch);
            }
        }

        // 3. Отрисовываем объект птички
        bird.draw(myGdxGame.batch, isGameOver || isVictory);

        if (isVictory) {
            // Отрисовываем эпическую неоновую секретную концовку MYBIRD со звёздами!
            pointCounter.drawVictory(myGdxGame.batch, gamePoints);
        } else {
            // Отрисовываем дефолтный счетчик
            pointCounter.draw(myGdxGame.batch, gamePoints);
        }

        myGdxGame.batch.end(); // Завершаем пакетную отрисовку графики
    }

    @Override // Переопределяем метод изменения размеров
    public void resize(int width, int height) { // Вызывается при изменении размеров окна или ориентации экрана
        myGdxGame.viewport.update(width, height, true); // Обновляем Viewport на всю ширину и высоту
    }

    @Override // Переопределяем метод паузы
    public void pause() { // Вызывается при сворачивании игры

    }

    @Override // Переопределяем метод возобновления
    public void resume() { // Вызывается при разворачивании игры

    }

    @Override // Переопределяем метод скрытия
    public void hide() { // Вызывается при переключении на другой экран

    }

    @Override // Переопределяем метод очистки
    public void dispose() { // Вызывается при уничтожении данного экрана
        bird.dispose(); // Вызываем очистку ресурсов объекта птички
        background.dispose(); // Вызываем очистку ресурсов движущегося фона
        pointCounter.dispose(); // Вызываем очистку ресурсов шрифта счетчика очков
        for (Tube tube : tubes) { // Проходимся по всем колоннам
            tube.dispose(); // Освобождаем ресурсы текстур каждой колонны
        }
    }
}
