package io.github.Stepan2552.screens; // Пакет экранов игры

import com.badlogic.gdx.Gdx; // Импортируем Gdx
import com.badlogic.gdx.Screen; // Импортируем интерфейс Screen
import com.badlogic.gdx.math.Vector3; // Импортируем Vector3 для считывания точных координат клика
import com.badlogic.gdx.utils.ScreenUtils; // Импортируем ScreenUtils
import io.github.Stepan2552.Main; // Импортируем главный класс Main
import io.github.Stepan2552.components.MovingBackground; // Импортируем MovingBackground
import io.github.Stepan2552.components.PointCounter; // Импортируем PointCounter
import io.github.Stepan2552.components.TextButton; // Импортируем TextButton

public class ScreenRestart implements Screen { // Экран рестарта/проигрыша игры
    Main myGdxGame; // Ссылка на главный класс Main
    MovingBackground background; // Фон экрана рестарта
    TextButton buttonRestart; // Кнопка перезапуска игры buttonRestart
    TextButton buttonMenu; // Кнопка перехода в главное меню buttonMenu по ТЗ урока
    PointCounter pointCounter; // Объект для красивого вывода набранных очков
    public int gamePoints; // Переменная финального количества набранных очков

    public ScreenRestart(Main myGdxGame) { // Конструктор экрана рестарта
        this.myGdxGame = myGdxGame; // Сохраняем ссылку на Main
        buttonRestart = new TextButton(100, 350, "Restart"); // Инициализируем текстовую кнопку "Restart"
        buttonMenu = new TextButton(100, 200, "Menu"); // Инициализируем кнопку выхода в меню "Menu" по ТЗ урока
        background = new MovingBackground("restart_bg.png"); // Загружаем фон экрана рестарта из assets
        pointCounter = new PointCounter(750, 530); // Инициализируем вывод очков в точных координатах урока (750, 530)
    }

    @Override
    public void show() { // Вызывается при показе экрана
        background.reset(); // Сбрасываем координаты фона при открытии экрана
    }

    @Override
    public void render(float delta) { // Главный цикл экрана рестарта
        if (Gdx.input.justTouched()) { // Проверяем прикосновение к экрану
            // Преобразуем экранные координаты касания в систему координат игрового мира через camera.unproject
            Vector3 touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            int touchX = (int) touch.x;
            int touchY = (int) touch.y;

            // Проверяем нажатие на кнопку "Restart"
            if (buttonRestart.isHit(touchX, touchY)) {
                myGdxGame.setScreen(myGdxGame.screenGame); // Переключаемся на главный игровой экран
            }
            // Проверяем нажатие на кнопку "Menu"
            else if (buttonMenu.isHit(touchX, touchY)) {
                myGdxGame.setScreen(myGdxGame.screenMenu); // Переключаемся на экран главного меню по ТЗ урока
            }
        }

        ScreenUtils.clear(0, 0, 0, 1); // Очищаем буфер экрана перед началом отрисовки

        myGdxGame.viewport.apply(); // Применяем актуальные пропорции мира из ExtendViewport
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined); // Настраиваем матрицу проекции камеры

        myGdxGame.batch.begin(); // Начинаем пакетную отрисовку графики
        background.draw(myGdxGame.batch); // Отрисовываем фон экрана рестарта
        buttonRestart.draw(myGdxGame.batch); // Отрисовываем кнопку "Restart"
        buttonMenu.draw(myGdxGame.batch); // Отрисовываем кнопку "Menu"
        pointCounter.draw(myGdxGame.batch, gamePoints); // Отрисовываем итоговое количество очков
        myGdxGame.batch.end(); // Завершаем пакетную отрисовку графики
    }

    @Override
    public void resize(int width, int height) { // Ресайз экрана
        myGdxGame.viewport.update(width, height, true); // Обновляем Viewport
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() { // Метод очистки оперативной памяти
        background.dispose(); // Освобождаем ресурсы фона
        buttonRestart.dispose(); // Освобождаем ресурсы кнопки рестарта
        buttonMenu.dispose(); // Освобождаем ресурсы кнопки меню
        pointCounter.dispose(); // Освобождаем ресурсы счетчика очков
    }
}
