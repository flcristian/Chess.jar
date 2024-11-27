package constants;

import java.awt.*;

public class Globals {
    private Globals() {}

    public static final int SIZE_BOARD_PANEL = 1024;
    public static final int SIZE_TILE = SIZE_BOARD_PANEL / 8;
    public static final int SIZE_PIECE = 64;

    // COLORS
    public static final Color COLOR_WHITE_TILE = new Color(203, 208, 215);
    public static final Color COLOR_BLACK_TILE = new Color(38, 46, 56);
    public static final Color COLOR_LIGHT_GOLD = new Color(255, 193, 7);
    public static final Color COLOR_DARK_GOLD = new Color(162, 120, 55);
    public static final Color COLOR_DARK_GRAY = new Color(24, 24, 24);
    public static final Color COLOR_WHITE_PIECE = Color.WHITE;
    public static final Color COLOR_BLACK_PIECE = Color.BLACK;

    // FONT PATHS
    public static final String FONT_PATH = "src/assets/fonts/Quintessential-Regular.ttf";

    // TEXTURE PATHS
    public static final String TEXTURE_WOOD_PATH = "src/assets/textures/wood.png";
}
