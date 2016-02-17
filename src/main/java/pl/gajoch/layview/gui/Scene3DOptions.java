package pl.gajoch.layview.gui;

public class Scene3DOptions {
    public final double tipLen, tipRadius, radius, lenScale, globalScale, FPS;
    public final HintGradient gradient1, gradient2;

    public Scene3DOptions(double tipLen, double tipRadius, double radius, double lenScale,
                          double globalScale, double FPS,
                          final HintGradient gradient1, final HintGradient gradient2) {
        this.tipLen = tipLen;
        this.tipRadius = tipRadius;
        this.radius = radius;
        this.lenScale = lenScale;
        this.globalScale = globalScale;
        this.FPS = FPS;
        this.gradient1 = gradient1;
        this.gradient2 = gradient2;
    }

    public Scene3DOptions(Scene3DOptions second) {
        this.tipLen = second.tipLen;
        this.tipRadius = second.tipRadius;
        this.radius = second.radius;
        this.lenScale = second.lenScale;
        this.globalScale = second.globalScale;
        this.FPS = second.FPS;
        this.gradient1 = new HintGradient(second.gradient1);
        this.gradient2 = new HintGradient(second.gradient2);
    }
}
