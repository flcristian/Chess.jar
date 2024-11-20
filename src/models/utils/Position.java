package models.utils;

import java.io.Serializable;

public record Position(int x, int y) implements Cloneable, Serializable {
    @Override
    public Position clone() {
        return new Position(x, y);
    }
}