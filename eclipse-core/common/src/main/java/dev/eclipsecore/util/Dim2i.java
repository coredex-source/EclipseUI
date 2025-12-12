package dev.eclipsecore.util;

/**
 * A 2D integer dimension/position record.
 */
public record Dim2i(int x, int y, int width, int height) {
    
    public int getLimitX() {
        return x + width;
    }
    
    public int getLimitY() {
        return y + height;
    }
    
    public int getCenterX() {
        return x + width / 2;
    }
    
    public int getCenterY() {
        return y + height / 2;
    }
    
    public boolean containsCursor(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < getLimitX() && mouseY >= y && mouseY < getLimitY();
    }
    
    public Dim2i withX(int newX) {
        return new Dim2i(newX, y, width, height);
    }
    
    public Dim2i withY(int newY) {
        return new Dim2i(x, newY, width, height);
    }
    
    public Dim2i withWidth(int newWidth) {
        return new Dim2i(x, y, newWidth, height);
    }
    
    public Dim2i withHeight(int newHeight) {
        return new Dim2i(x, y, width, newHeight);
    }
    
    public Dim2i offset(int dx, int dy) {
        return new Dim2i(x + dx, y + dy, width, height);
    }
    
    public Dim2i inset(int amount) {
        return new Dim2i(x + amount, y + amount, width - amount * 2, height - amount * 2);
    }
}
