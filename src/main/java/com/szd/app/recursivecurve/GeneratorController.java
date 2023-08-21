package com.szd.app.recursivecurve;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import static com.szd.app.recursivecurve.CurveType.KOCH_SNOWFLAKE;

public class GeneratorController {
    @FXML
    private Canvas canvas;
    @FXML
    private ChoiceBox<String> curveChoice;
    @FXML
    private ChoiceBox<String> depthValueChoice;
    @FXML
    private TextField angleValue;
    @FXML
    private Integer canvasWidth;
    double padding = 20;
    double beginX = padding, beginY = padding;
    GraphicsContext graphicsContext;

    @FXML
    protected void onCurveChosen() {
        angleValue.setDisable(!curveChoice.getValue().equals(KOCH_SNOWFLAKE.toString()));
    }

    @FXML
    protected void onGenerating() {
        graphicsContext = canvas.getGraphicsContext2D();
        // 重新绘制前需擦除画布上的原内容
        graphicsContext.clearRect(0, 0, canvasWidth, canvasWidth);

        CurveType curveType = CurveType.valueOf(curveChoice.getValue().toUpperCase());
        int depth = Integer.parseInt(depthValueChoice.getValue());
        double canvasLimit = canvasWidth - 2 * padding;
        double dx, angle, lineLength;
        switch (curveType) {
            case HILBERT -> {
                // Hilbert曲线每递归多一层，坐标方向上的长度就被切分为2^(n+1)-1份
                dx = canvasLimit / (GeneratorController.fastExponentiation(2, depth + 1) - 1);
                beginX = beginY = padding;
                hilbertCurve(depth, dx, 0);
                // hilbertCurveWithoutRecursion(depth, dx, 0);
            }
            case SIERPINSKI -> {
                // Sierpinski曲线每递归多一层，坐标方向上的长度就被切分为2^(n+2)-2份
                dx = canvasLimit / (GeneratorController.fastExponentiation(2, depth + 2) - 2);
                beginX = dx + padding;
                beginY = padding;
                sierpinskiCurve(depth, dx, dx);
            }
            case KOCH_SNOWFLAKE -> {
                    /*
                     角度输入时使用角度角表示，但计算时需用弧度角表示，输入值最好在[0, pi/2]区间范围内
                     设传入角为a，l为单位绘线的长度，即绘制时每段线段的长度，三条基本边组成的等边三角形对应的同心圆半径为r，则其与总的画布宽度h的关系为
                     l * sina + r * cos(pi/3) + r = h
                     ((2 * sqrt(3)) / 3 * (1 + cos(a))) * l = r
                     最终可以算出来l的值为
                     l = h / (sin(a) + sqrt(3) * (1 + cos(a)))
                     确定了l即可开始进行绘制，最原始的图形总的边长度为
                     2 * l * (1 + cos(a))
                    */
                angle = Float.parseFloat(angleValue.getText()) * (Math.PI / 180);
                dx = canvasLimit / (Math.sin(angle) + Math.sqrt(3) * (1 + Math.cos(angle)));
                lineLength = 2 * dx * (1 + Math.cos(angle));
                // 确定起始坐标
                beginX = (canvasLimit - lineLength) / 2 + padding;
                beginY = dx * Math.sin(angle) + padding;
                kochCurve(depth, 0, angle, lineLength);
                    /*
                     画布的x坐标与标准坐标系方向一致，y坐标方向相反，这里对初始角度的计算都是相对角度0而言的，而不是上一次绘线的角度
                     三条基线应构成等边三角形
                     */
                kochCurve(depth, Math.PI * 2 / 3, angle, lineLength);
                kochCurve(depth, -Math.PI * 2 / 3, angle, lineLength);
            }
        }
    }

    /**
     * Hilbert曲线绘制，初始方向为横向，即dx=单位长度，dy=0
     *
     * @param depth 递归深度
     * @param dx    横坐标增量
     * @param dy    纵坐标增量
     */
    public void hilbertCurve(int depth, double dx, double dy) {
        if (depth > 0) hilbertCurve(depth - 1, dy, dx);
        drawRelatives(dx, dy);
        if (depth > 0) hilbertCurve(depth - 1, dx, dy);
        drawRelatives(dy, dx);
        if (depth > 0) hilbertCurve(depth - 1, dx, dy);
        drawRelatives(-dx, -dy);
        if (depth > 0) hilbertCurve(depth - 1, -dy, -dx);
    }

    /**
     * Sierpinski曲线绘制，通过调用向四个不同方向绘制的递归子程序组合实现
     * 四个方向的基本图形分别为
     * 右 ╲__╱ 左 ╱‾‾╲ 下和上的方向图形分别为右、左图形顺时针旋转pi/2而得
     *
     * @param depth 递归深度
     * @param dx    横坐标增量
     * @param dy    纵坐标增量
     */
    public void sierpinskiCurve(int depth, double dx, double dy) {
        sierpinskiRight(depth, dx, dy);
        drawRelatives(dx, dy);
        sierpinskiDown(depth, dx, dy);
        drawRelatives(-dx, dy);
        sierpinskiLeft(depth, dx, dy);
        drawRelatives(-dx, -dy);
        sierpinskiUp(depth, dx, dy);
        drawRelatives(dx, -dy);
    }

    private void sierpinskiRight(int depth, double dx, double dy) {
        if (depth > 0) {
            depth--;

            sierpinskiRight(depth, dx, dy);
            drawRelatives(dx, dy);
            sierpinskiDown(depth, dx, dy);
            drawRelatives(2 * dx, 0);
            sierpinskiUp(depth, dx, dy);
            drawRelatives(dx, -dy);
            sierpinskiRight(depth, dx, dy);
        }
    }

    private void sierpinskiLeft(int depth, double dx, double dy) {
        if (depth > 0) {
            depth--;

            sierpinskiLeft(depth, dx, dy);
            drawRelatives(-dx, -dy);
            sierpinskiUp(depth, dx, dy);
            drawRelatives(-2 * dx, 0);
            sierpinskiDown(depth, dx, dy);
            drawRelatives(-dx, dy);
            sierpinskiLeft(depth, dx, dy);
        }
    }

    private void sierpinskiDown(int depth, double dx, double dy) {
        if (depth > 0) {
            depth--;

            sierpinskiDown(depth, dx, dy);
            drawRelatives(-dx, dy);
            sierpinskiLeft(depth, dx, dy);
            drawRelatives(0, 2 * dy);
            sierpinskiRight(depth, dx, dy);
            drawRelatives(dx, dy);
            sierpinskiDown(depth, dx, dy);
        }
    }

    private void sierpinskiUp(int depth, double dx, double dy) {
        if (depth > 0) {
            depth--;

            sierpinskiUp(depth, dx, dy);
            drawRelatives(dx, -dy);
            sierpinskiRight(depth, dx, dy);
            drawRelatives(0, -2 * dy);
            sierpinskiLeft(depth, dx, dy);
            drawRelatives(-dx, -dy);
            sierpinskiUp(depth, dx, dy);
        }
    }

    /**
     * 任意旋转角的科赫曲线绘制
     *
     * @param depth     递归深度
     * @param direction 科赫曲线的初始绘制方向
     * @param angle     旋转角
     * @param length    初始线段长度
     */
    public void kochCurve(int depth, double direction, double angle, double length) {
        if (depth == 0) {
            drawRelativesWithAngle(direction, length);
        } else {
            double newLength = length / (2 * (1 + Math.cos(angle)));
            kochCurve(depth - 1, direction, angle, newLength);
            kochCurve(depth - 1, direction - angle, angle, newLength);
            kochCurve(depth - 1, direction + angle, angle, newLength);
            kochCurve(depth - 1, direction, angle, newLength);
        }
    }

    /**
     * 根据横纵坐标偏移量绘制直线
     *
     * @param dx 横坐标增量
     * @param dy 纵坐标增量
     */
    public void drawRelatives(double dx, double dy) {
        graphicsContext.strokeLine(beginX, beginY, beginX + dx, beginY + dy);
        beginX += dx;
        beginY += dy;
    }

    /**
     * 根据特定长度向特定方向绘制直线
     *
     * @param angle  方向角
     * @param length 直线长度(用以计算横纵坐标)
     */
    public void drawRelativesWithAngle(double angle, double length) {
        double dx = length * Math.cos(angle);
        double dy = length * Math.sin(angle);
        graphicsContext.strokeLine(beginX, beginY, beginX + dx, beginY + dy);
        beginX += dx;
        beginY += dy;
    }

    /**
     * 快速求幂算法
     *
     * @param base     底数
     * @param exponent 指数且不大于62
     */
    public static long fastExponentiation(int base, int exponent) {
        long result = 1L;
        long currentBase = base;
        int currentExponent = exponent;
        while (currentExponent != 0) {
            if (currentExponent % 2 == 1) {
                result *= currentBase;
            }
            currentBase *= currentBase;
            currentExponent /= 2;
        }

        return result;
    }

}