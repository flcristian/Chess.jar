package controllers;

import java.util.function.Supplier;

public class PieceControllerSingleton {
    private static final Supplier<PieceController> instance =
            new Supplier<>() {
                private final PieceController singletonInstance = new PieceController();

                @Override
                public PieceController get() {
                    return singletonInstance;
                }
            };

    private PieceControllerSingleton() {}

    public static PieceController getInstance() {
        return instance.get();
    }
}
