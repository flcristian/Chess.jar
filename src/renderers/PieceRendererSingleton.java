package renderers;

import java.util.function.Supplier;

public class PieceRendererSingleton {
    private static final Supplier<PieceRenderer> instance =
            new Supplier<>() {
                private final PieceRenderer singletonInstance = new PieceRenderer();

                @Override
                public PieceRenderer get() {
                    return singletonInstance;
                }
            };

    private PieceRendererSingleton() {}

    public static PieceRenderer getInstance() {
        return instance.get();
    }
}
