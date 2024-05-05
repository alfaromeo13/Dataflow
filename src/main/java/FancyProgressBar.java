import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.geom.*;

public class FancyProgressBar extends BasicProgressBarUI {

    @Override
    protected Dimension getPreferredInnerVertical() {
        return new Dimension(20, 146);
    }

    @Override
    protected Dimension getPreferredInnerHorizontal() {
        return new Dimension(146, 20);
    }

    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int strokeWidth = 5;
        g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(progressBar.getBackground());
        g2d.setBackground(progressBar.getBackground());

        int width = progressBar.getWidth();
        int height = progressBar.getHeight();

        RoundRectangle2D outline = new RoundRectangle2D.Double((strokeWidth / 2), (strokeWidth / 2),
                width - strokeWidth, height - strokeWidth,
                height, height);

        g2d.draw(outline);

        int iInnerHeight = height - (strokeWidth * 4);
        int iInnerWidth = width - (strokeWidth * 4);

        double dProgress = progressBar.getPercentComplete();
        if (dProgress < 0) {
            dProgress = 0;
        } else if (dProgress > 1) {
            dProgress = 1;
        }

        iInnerWidth = (int) Math.round(iInnerWidth * dProgress);

        int x = strokeWidth * 2;
        int y = strokeWidth * 2;

        Point2D start = new Point2D.Double(x, y);
        Point2D end = new Point2D.Double(x, y + iInnerHeight);

        float[] dist = {0.0f, 1.0f};

        Color[] colors = {Color.decode("#70F570"), Color.decode("#49C628")};
        LinearGradientPaint p = new LinearGradientPaint(start, end, dist, colors);

        g2d.setPaint(p);

        RoundRectangle2D fill = new RoundRectangle2D.Double(strokeWidth * 2, strokeWidth * 2,
                iInnerWidth, iInnerHeight, iInnerHeight, iInnerHeight);

        g2d.fill(fill);
        g2d.dispose();
    }

    @Override
    protected void paintIndeterminate(Graphics g, JComponent c) {
        super.paintIndeterminate(g, c);
    }
}