package com.gachon.priend.calendar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gachon.priend.R;
import com.gachon.priend.data.datetime.Date;

import java.util.Map;
import java.util.TreeMap;

/**
 * A view that shows simple chart
 *
 * @author 유근혁
 * @since May 23rd 2020
 */
public class SimpleChartView extends View {

    private TreeMap<Date, Double> values = new TreeMap<Date, Double>();

    private int graphColor;

    private final Paint Paint = new Paint();
    private final Path Path = new Path();
    private final RectF Bounds = new RectF();
    private final Matrix Matrix = new Matrix();
    private final float[] Point = new float[2];

    public SimpleChartView(Context context) {
        super(context);
        setGraphColor(getContext().getResources().getColor(R.color.primaryColor, getContext().getTheme()));
    }

    public SimpleChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setGraphColor(getContext().getResources().getColor(R.color.primaryColor, getContext().getTheme()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (this.values != null) {
            final int StrokeColor = graphColor | 0xFF000000;
            final int FillColor = StrokeColor & 0x40FFFFFF; // 25% opacity

            Paint.setAntiAlias(true);

            if (values.size() == 1) {
                Paint.setStyle(android.graphics.Paint.Style.FILL);
                Paint.setColor(StrokeColor);
                canvas.drawOval(getWidth() / 2F - 2, getHeight() / 2F - 2, 4, 4, Paint);
            } else if (values.size() > 1) {

                Path.reset();
                Path.moveTo(0, 0);

                final long xMin = values.firstKey().toMillis();
                final long xMax = values.lastKey().toMillis();
                final float firstY = values.firstEntry().getValue().floatValue();
                float yMin = Float.MAX_VALUE;
                float yMax = Float.MIN_VALUE;

                // draw lines for value points
                Path.moveTo(xMin, firstY);
                for (Map.Entry<Date, Double> entry : values.entrySet()) {
                    final long key = entry.getKey().toMillis();
                    final float val = entry.getValue().floatValue();

                    if (val < yMin) {
                        yMin = val;
                    }
                    if (val > yMax) {
                        yMax = val;
                    }

                    Path.lineTo(key, val);
                }

                // calculate canvas bounds
                Bounds.top = getPaddingTop();
                Bounds.bottom = getHeight() - getPaddingBottom();
                Bounds.left = getPaddingLeft();
                Bounds.right = getWidth() - getPaddingRight();

                // fit the value line into canvas
                Matrix.reset();
                Matrix.preScale(xMax - xMin, yMax - yMin);
                Matrix.postScale(Bounds.width(), Bounds.height() * 0.3F);
                Matrix.preTranslate(xMin, yMin);
                Matrix.postTranslate(Bounds.left, Bounds.right);
                Path.transform(Matrix);

                // fit the first point on the line into canvas
                Point[0] = xMin;
                Point[1] = firstY;
                Matrix.mapPoints(Point);

                // draw right, bottom, left line
                Path.lineTo(Bounds.right, Bounds.bottom);
                Path.lineTo(Bounds.left, Bounds.bottom);
                Path.lineTo(Point[0], Point[1]);

                // fill path
                Paint.setStyle(android.graphics.Paint.Style.FILL);
                Paint.setColor(FillColor);
                canvas.drawPath(Path, Paint);

                // draw path
                Paint.setStyle(android.graphics.Paint.Style.STROKE);
                Paint.setColor(StrokeColor);
                canvas.drawPath(Path, Paint);
            }
        }
    }

    /**
     * Set the theme color of the chart
     *
     * @param color The theme color
     */
    public void setGraphColor(int color) {
        this.graphColor = color;
    }

    /**
     * Get the theme color of the chart
     *
     * @return The theme color
     */
    public int getGraphColor() {
        return graphColor;
    }

    /**
     * Get the whole value set of the chart in {TreeMap<Long, Double>} type
     *
     * @return The set of value
     */
    public TreeMap<Date, Double> getValues() {
        return values;
    }

    /**
     * Get one value corresponding given key
     *
     * @param key The horizontal axis value
     * @return The vertical axis value
     */
    public Double getValue(@NonNull Date key) {
        return values.get(key);
    }

    /**
     * Set the entire value of weight associated with date
     *
     * @param values The TreeMap instance
     */
    public void setValues(TreeMap<Date, Double> values) {
        this.values = values;

        invalidate();
    }

    /**
     * Set the value that corresponds the given key. Insert value if occupied, update, otherwise.
     *
     * @param key   The key
     * @param value The value
     */
    public void insertOrUpdateValue(@NonNull Date key, double value) {
        values.put(key, value);

        invalidate();
    }

    /**
     * Remove the value that corresponds the given key.
     *
     * @param key The key
     * @return True if success, false otherwise
     */
    public boolean removeValue(@NonNull Date key) {
        Double result = values.remove(key);

        if (result == null) {
            return false;
        } else {
            invalidate();

            return true;
        }
    }
}
