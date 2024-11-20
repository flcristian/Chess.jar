package panels;

import java.util.function.Supplier;

public class BoardPanelSingleton {
    private static final Supplier<BoardPanel> instance =
            new Supplier<>() {
                private final BoardPanel singletonInstance = new BoardPanel();

                @Override
                public BoardPanel get() {
                    return singletonInstance;
                }
            };

    private BoardPanelSingleton() {}

    public static BoardPanel getInstance() {
        return instance.get();
    }
}
