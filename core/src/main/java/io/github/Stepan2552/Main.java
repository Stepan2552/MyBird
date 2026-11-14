package io.github.Stepan2552; // Главный пакет проекта

import com.badlogic.gdx.Game; // Импортируем базовый класс Game из фреймворка libGDX
import com.badlogic.gdx.graphics.OrthographicCamera; // Импортируем виртуальную 2D-камеру
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Импортируем инструмент для отрисовки 2D-картинок
import com.badlogic.gdx.utils.viewport.ExtendViewport; // Импортируем ExtendViewport для заполнения 100% экрана
import com.badlogic.gdx.utils.viewport.Viewport; // Импортируем интерфейс Viewport
import io.github.Stepan2552.screens.ScreenGame; // Импортируем экран игры из пакета screens
import io.github.Stepan2552.screens.ScreenMenu; // Импортируем экран главного меню из пакета screens
import io.github.Stepan2552.screens.ScreenRestart; // Импортируем экран рестарта из пакета screens

public class Main extends Game { // Главный класс игры, наследуемый от Game (умеет переключать экраны)

    public static final int SCR_WIDTH = 1280; // Базовая ширина виртуального экрана (1280 пикселей)
    public static final int SCR_HEIGHT = 720; // Базовая высота виртуального экрана (720 пикселей)

    public static Main instance; // Ссылка на текущий рабочий экземпляр класса Main

    public SpriteBatch batch; // Объявляем инструмент для рисования всех картинок на экране
    public OrthographicCamera camera; // Объявляем виртуальную 2D-камеру
    public Viewport viewport; // Объявляем Viewport для плавной растяжки на весь физический экран
    public ScreenGame screenGame; // Объявляем объект главного игрового экрана
    public ScreenRestart screenRestart; // Объявляем объект экрана рестарта
    public ScreenMenu screenMenu; // Объявляем объект экрана главного меню

    public static float getWorldWidth() { // Метод получения реальной ширины текущего экрана без стенок
        return (instance != null && instance.viewport != null) ? instance.viewport.getWorldWidth() : SCR_WIDTH;
    }

    public static float getWorldHeight() { // Метод получения реальной высоты текущего экрана без стенок
        return (instance != null && instance.viewport != null) ? instance.viewport.getWorldHeight() : SCR_HEIGHT;
    }

    @Override // Переопределяем метод базового класса Game
    public void create() { // Этот метод вызывается 1 раз при запуске приложения
        instance = this; // Сохраняем экземпляр для доступа к реальным размерам мира
        batch = new SpriteBatch(); // Инициализируем инструмент отрисовки
        camera = new OrthographicCamera(); // Инициализируем виртуальную камеру

        // ExtendViewport растягивает камеру на 100% реального экрана смартфона или окна без рамок и невидимых стенок
        viewport = new ExtendViewport(SCR_WIDTH, SCR_HEIGHT, camera);
        viewport.apply(); // Применяем параметры viewport к камере

        screenGame = new ScreenGame(this); // Создаём главный игровой экран
        screenRestart = new ScreenRestart(this); // Создаём экран рестарта
        screenMenu = new ScreenMenu(this); // Создаём экран главного меню

        setScreen(screenMenu); // Устанавливаем и активируем стартовый экран главного меню!
    }

    @Override // Переопределяем метод очистки ресурсов
    public void dispose() { // Вызывается при закрытии игры для очистки оперативной памяти
        batch.dispose(); // Освобождаем память от объекта SpriteBatch
    }
}
