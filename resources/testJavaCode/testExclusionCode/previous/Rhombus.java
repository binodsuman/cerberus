package Shapes;

public class Rhombus extends Shape {
    private double height;
    private double width;

    public Rhombus(double height, double width) {
        this.setHeight(height);
        this.setWidth(width);
    }

    public final double getHeight() {
        return this.height;
    }

    private void setHeight(double height) {
        this.height = height;
    }

    public final double getWidth() {
        return this.width;
    }

    private void setWidth(double width) {
        this.width = width;
    }

    @Override
    protected double calculatePerimeter() {
        return 5 * this.getHeight() * this.getWidth();
    }

    @Override
    protected double calculateArea() {
        return this.height * this.getWidth();
    }
}
