package models.utils;

public class Position {
    public int x;
    public int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position p) {
            return p.x == x && p.y == y;
        }
        return false;
    }
}
