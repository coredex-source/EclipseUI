package dev.eclipseui.util;

/**
 * A 2D integer dimension/position record.
 * Inspired by Sodium's Dim2i for widget positioning.
 */
public record Dim2i(int x, int y, int width, int height) {
    
    /**
     * Creates a new Dim2i at origin (0, 0) with the given dimensions.
     */
    public static Dim2i ofSize(int width, int height) {
        return new Dim2i(0, 0, width, height);
    }
    
    /**
     * Creates a new Dim2i at the given position with zero size.
     */
    public static Dim2i ofPosition(int x, int y) {
        return new Dim2i(x, y, 0, 0);
    }
    
    /**
     * Returns the X coordinate of the right edge.
     */
    public int getLimitX() {
        return this.x + this.width;
    }
    
    /**
     * Returns the Y coordinate of the bottom edge.
     */
    public int getLimitY() {
        return this.y + this.height;
    }
    
    /**
     * Returns the center X coordinate.
     */
    public int getCenterX() {
        return this.x + (this.width / 2);
    }
    
    /**
     * Returns the center Y coordinate.
     */
    public int getCenterY() {
        return this.y + (this.height / 2);
    }
    
    /**
     * Checks if a point is contained within this dimension.
     */
    public boolean containsCursor(double x, double y) {
        return x >= this.x && x < this.getLimitX() && y >= this.y && y < this.getLimitY();
    }
    
    /**
     * Returns a new Dim2i offset by the given amounts.
     */
    public Dim2i withOffset(int offsetX, int offsetY) {
        return new Dim2i(this.x + offsetX, this.y + offsetY, this.width, this.height);
    }
    
    /**
     * Returns a new Dim2i with the given position.
     */
    public Dim2i withPosition(int x, int y) {
        return new Dim2i(x, y, this.width, this.height);
    }
    
    /**
     * Returns a new Dim2i with the given size.
     */
    public Dim2i withSize(int width, int height) {
        return new Dim2i(this.x, this.y, width, height);
    }
    
    /**
     * Returns a new Dim2i with padding applied (shrinks the dimension).
     */
    public Dim2i withPadding(int padding) {
        return new Dim2i(
            this.x + padding,
            this.y + padding,
            this.width - (padding * 2),
            this.height - (padding * 2)
        );
    }
    
    /**
     * Returns a new Dim2i with different padding for each side.
     */
    public Dim2i withPadding(int left, int top, int right, int bottom) {
        return new Dim2i(
            this.x + left,
            this.y + top,
            this.width - left - right,
            this.height - top - bottom
        );
    }
    
    /**
     * Checks if this dimension overlaps with another.
     */
    public boolean overlaps(Dim2i other) {
        return this.x < other.getLimitX() && this.getLimitX() > other.x
            && this.y < other.getLimitY() && this.getLimitY() > other.y;
    }
}
