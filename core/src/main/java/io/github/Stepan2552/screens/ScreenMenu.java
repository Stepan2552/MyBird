package io.github.Stepan2552.screens; // Пакет экранов игры

import com.badlogic.gdx.Gdx; // Импортируем Gdx
import com.badlogic.gdx.Preferences; // Импортируем Preferences для считывания рекорда
import com.badlogic.gdx.Screen; // Импортируем интерфейс Screen
import com.badlogic.gdx.graphics.Color; // Импортируем Color
import com.badlogic.gdx.graphics.g2d.BitmapFont; // Импортируем BitmapFont
import com.badlogic.gdx.math.Vector3; // Импортируем Vector3 для отслеживания координат нажатия
import com.badlogic.gdx.utils.ScreenUtils; // Импортируем ScreenUtils
import io.github.Stepan2552.Main; // Импортируем главный класс Main
import io.github.Stepan2552.components.MovingBackground; // Импортируем MovingBackground
import io.github.Stepan2552.components.TextButton; // Импортируем TextButton

public class ScreenMenu implements Screen { // Главный экран меню игры
    Main myGdxGame; // Ссылка на главный класс управления игрой Main
    MovingBackground background; // Объект анимированного фонового изображения по ТЗ курса
    TextButton buttonStart; // Кнопка старта игры buttonStart
    TextButton buttonExit; // Кнопка выхода из игры buttonExit
    BitmapFont recordFont; // Шрифт для показа несбрасываемого рекорда в меню
    Preferences prefs; // Доступ к памяти рекордов

    public ScreenMenu(Main myGdxGame) { // Конструктор экрана меню
        this.myGdxGame = myGdxGame; // Сохраняем ссылку на класс Main
        buttonStart = new TextButton(100, 400, "Start"); // Кнопка начала игры buttonStart
        buttonExit = new TextButton(100, 250, "Exit"); // Кнопка выхода из игры buttonExit
        background = new MovingBackground("restart_bg.png"); // Загружаем фон меню с файлом restart_bg.png по ТЗ урока

        prefs = Gdx.app.getPreferences("MyBirdPrefs"); // Инициализируем доступ к памяти рекордов
        recordFont = new BitmapFont(); // Создаём шрифт рекорда
        recordFont.getData().setScale(5f); // Масштаб 5x
        recordFont.setColor(Color.YELLOW); // Золотой жёлтый цвет рекорда
    }

    @Override
    public void show() { // Вызывается при каждом открытии экрана меню
        background.reset(); // Сбрасываем позиции фона при показе меню
    }

    @Override
    public void render(float delta) { // Главный игровой цикл экрана меню
        float clampedDelta = Math.min(delta, 0.033f); // Фильтр от лагов
        background.move(clampedDelta); // Включаем плавное движение фона в меню!

        if (Gdx.input.justTouched()) { // Проверяем клик или касание пальцем по экрану
            // Преобразуем координаты клика в координаты мира через camera.unproject
            Vector3 touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            int touchX = (int) touch.x;
            int touchY = (int) touch.y;

            // Если нажата кнопка buttonStart — запускаем основной экран игры
            if (buttonStart.isHit(touchX, touchY)) {
                myGdxGame.setScreen(myGdxGame.screenGame); // Переключаемся на ScreenGame
            }
            // Если нажата кнопка buttonExit — корректно закрываем приложение
            else if (buttonExit.isHit(touchX, touchY)) {
                Gdx.app.exit(); // Завершение работы игры по ТЗ урока
            }
        }

        ScreenUtils.clear(0, 0, 0, 1); // Очищаем экран перед началом отрисовки

        myGdxGame.viewport.apply(); // Применяем параметры 2D-камеры и Viewport
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined); // Настраиваем матрицу отрисовки

        myGdxGame.batch.begin(); // Начинаем пакетную отрисовку
        background.draw(myGdxGame.batch); // Отрисовываем движущийся фон
        buttonStart.draw(myGdxGame.batch); // Отрисовываем кнопку начала игры
        buttonExit.draw(myGdxGame.batch); // Отрисовываем кнопку выхода из игры

        // Отрисовываем лучший сохранённый рекорд игрока в меню!
        int bestRecord = prefs.getInteger("high_score", 0);
        recordFont.draw(myGdxGame.batch, "Best: " + bestRecord, Main.getWorldWidth() - 400f, Main.getWorldHeight() - 60f);

        myGdxGame.batch.end(); // Завершаем пакетную отрисовку
    }

    @Override
    public void resize(int width, int height) { // Вызывается при изменении размера окна или первом запуске
        myGdxGame.viewport.update(width, height, true); // Обновляем Viewport
        background.reset(); // Сбрасываем фон под настоящую ширину обновленного Viewport!
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() { // Очистка оперативной памяти
        background.dispose(); // Освобождаем ресурсы текстуры фона
        buttonStart.dispose(); // Освобождаем ресурсы кнопки старта
        buttonExit.dispose(); // Освобождаем ресурсы кнопки выхода
        recordFont.dispose(); // Освобождаем ресурсы шрифта рекорда
    }
}
