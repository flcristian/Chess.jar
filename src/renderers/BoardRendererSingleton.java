package renderers;

import java.util.function.Supplier;

public class BoardRendererSingleton {
    private static final Supplier<BoardRenderer> instance =
            new Supplier<>() {
                private final BoardRenderer singletonInstance = new BoardRenderer();

                @Override
                public BoardRenderer get() {
                    return singletonInstance;
                }
            };

    private BoardRendererSingleton() {}

    public static BoardRenderer getInstance() {
        return instance.get();
    }
}