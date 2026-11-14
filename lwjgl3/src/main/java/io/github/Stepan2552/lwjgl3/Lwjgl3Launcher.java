package io.github.Stepan2552.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.Stepan2552.Main;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // Handles macOS & Windows startup
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("MyBird");

        // Включаем VSync для предотвращения тиринга (разрывов кадра)
        configuration.useVsync(true);

        // Автоматическая адаптация FPS под герцовку любого монитора (60Hz, 120Hz, 144Hz, 165Hz, 240Hz+)
        int monitorHz = Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate;
        configuration.setForegroundFPS(monitorHz > 0 ? monitorHz + 1 : 60);

        configuration.setWindowedMode(Main.SCR_WIDTH, Main.SCR_HEIGHT);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        return configuration;
    }
}
