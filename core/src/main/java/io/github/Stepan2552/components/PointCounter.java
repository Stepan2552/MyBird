package io.github.Stepan2552.components; // Пакет компонентов интерфейса

import com.badlogic.gdx.Gdx; // Импортируем Gdx для работы с хранилищем Preferences
import com.badlogic.gdx.Preferences; // Импортируем Preferences для сохранения рекорда в память
import com.badlogic.gdx.graphics.Color; // Импортируем класс Color для задания цвета шрифта
import com.badlogic.gdx.graphics.g2d.Batch; // Импортируем Batch для отрисовки текста
import com.badlogic.gdx.graphics.g2d.BitmapFont; // Импортируем BitmapFont для работы со шрифтами в LibGDX
import com.badlogic.gdx.math.MathUtils; // Импортируем утилиты математики для анимаций
import io.github.Stepan2552.Main; // Импортируем класс Main

public class PointCounter { // Класс PointCounter, отвечающий за вычисление, сохранение и отображение рекордов
    int x, y; // Координаты X и Y расположения текста очков на экране
    BitmapFont font; // Объект стандартного шрифта для отрисовки цифр и текста
    BitmapFont titleFont; // Специальный шрифт для победного золотого заголовка
    BitmapFont victoryFont; // Специальный шрифт для текста концовки
    BitmapFont starFont; // Шрифт для анимации витающих звёзд
    float victoryTimer = 0f; // Таймер для эпической анимации победного экрана

    Preferences prefs; // Объект Preferences для физического сохранения рекорда на диске/памяти устройства

    public PointCounter(int x, int y) { // Конструктор класса счетчика очков
        this.x = x; // Сохраняем горизонтальную координату X
        this.y = y; // Сохраняем вертикальную координату Y

        // Получаем объект энергонезависимой памяти приложения MyBirdPrefs
        prefs = Gdx.app.getPreferences("MyBirdPrefs");

        font = new BitmapFont(); // Создаем стандартный шрифт LibGDX
        font.getData().setScale(5f); // Масштабируем шрифт в 5 раз
        font.setColor(Color.WHITE); // Белый цвет текста

        titleFont = new BitmapFont(); // Шрифт для главного переливающегося заголовка победы
        titleFont.getData().setScale(6.5f); // Крупный заголовок (6.5x)

        victoryFont = new BitmapFont(); // Шрифт для статистики и деталей
        victoryFont.getData().setScale(4.2f); // Масштаб 4.2x
        victoryFont.setColor(Color.YELLOW); // Жёлтый цвет

        starFont = new BitmapFont(); // Шрифт для звёздного салюта
        starFont.getData().setScale(3.5f); // Размер звёзд
        starFont.setColor(Color.GOLD);
    }

    public int getHighScore() { // Метод чтения сохраненого рекорда из энергонезависимой памяти
        return prefs.getInteger("high_score", 0);
    }

    public void checkHighScore(int currentPoints) { // Метод проверки и записи нового рекорда в память
        if (currentPoints > getHighScore()) {
            prefs.putInteger("high_score", currentPoints); // Записываем значение нового рекорда
            prefs.flush(); // Физически сохраняем изменения в файл на диске/памяти
        }
    }

    public void draw(Batch batch, int countOfPoints) { // Метод для отрисовки текущего счёта и сохранённого рекорда
        checkHighScore(countOfPoints); // Проверяем и обновляем рекорд
        font.draw(batch, "Count: " + countOfPoints, x, y); // Отрисовываем текущие очки
        font.draw(batch, "Best:  " + getHighScore(), x, y - 60); // Отрисовываем несбрасываемый лучший рекорд!
    }

    public void drawVictory(Batch batch, int finalPoints) { // Метод эпической нереально крутой концовки MYBIRD
        checkHighScore(finalPoints); // Записываем 1000 очков в рекорд
        victoryTimer += 0.02f; // Обновляем таймер анимации победного экрана

        float worldW = Main.getWorldWidth();
        float worldH = Main.getWorldHeight();
        float centerX = worldW / 2f;
        float centerY = worldH / 2f;

        // Динамический анимированный перелив цвета заголовка (эффект неоновой победы)
        float r = (MathUtils.sin(victoryTimer * 5f) + 1f) / 2f;
        float g = (MathUtils.sin(victoryTimer * 5f + 2f) + 1f) / 2f;
        float b = (MathUtils.sin(victoryTimer * 5f + 4f) + 1f) / 2f;
        titleFont.setColor(r, g, b, 1f);

        // Пульсирующий размер заглавной надписи VICTORY!
        float pulse = 1f + MathUtils.sin(victoryTimer * 5f) * 0.1f;
        titleFont.getData().setScale(6f * pulse);

        // 1. Отрисовка взлетающего звёздного салюта вокруг птички
        for (int i = 0; i < 8; i++) {
            float starAngle = victoryTimer * 2f + i * (MathUtils.PI / 4f);
            float starX = centerX + MathUtils.cos(starAngle) * 320f;
            float starY = centerY + MathUtils.sin(starAngle) * 160f - 20f;
            starFont.draw(batch, "*", starX, starY);
        }

        // 2. Анимированный неоновый заголовок победы по центру
        titleFont.draw(batch, "VICTORY!", centerX - 190f * pulse, centerY + 230f);

        // 3. Главный текст легендарной концовки MYBIRD
        victoryFont.draw(batch, "SECRET ENDING UNLOCKED!", centerX - 330f, centerY + 120f);
        victoryFont.draw(batch, "LEGENDARY CHAMPION OF MYBIRD!", centerX - 420f, centerY + 40f);
        victoryFont.draw(batch, "FINAL SCORE: " + finalPoints, centerX - 240f, centerY - 50f);

        // 4. Плавно пульсирующий призыв сыграть заново
        float alpha = Math.abs(MathUtils.sin(victoryTimer * 4f));
        victoryFont.setColor(1f, 0.9f, 0.2f, alpha);
        victoryFont.draw(batch, "[ TAP TO FLY AGAIN ]", centerX - 260f, centerY - 150f);
        victoryFont.setColor(Color.YELLOW); // Возвращаем исходный цвет
    }

    public void dispose() { // Метод для очистки оперативной памяти от шрифтов
        font.dispose();
        titleFont.dispose();
        victoryFont.dispose();
        starFont.dispose();
    }
}
