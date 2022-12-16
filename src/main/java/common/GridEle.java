package common;

public class GridEle {
    protected Vec2d pos;

    public GridEle(Vec2d pos) {
        this.pos = pos;
    }

    public Vec2d getPos() {
        return pos;
    }

    public void setPos(Vec2d pos) {
        this.pos = pos;
    }
}
