import javax.swing.*;
import java.awt.*;

class GAPanel extends JPanel {

    private Routine currentRoutine;
    private Routine originalRoutine;
    private Routine lastRoutine;
    private int maxX;
    private int maxY;

    public void setRoutine(Routine currentRoutine) {
        this.currentRoutine = currentRoutine;
    }

    public void setOriginalRoutine(Routine orignalRoutine) {
        this.originalRoutine = orignalRoutine.copyRoutine();
        maxX = orignalRoutine.getMaxX();
        maxY = orignalRoutine.getMaxY();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int borderSize = 10;
        int radius = 3;
        double x_scale = (double) (this.getWidth()-borderSize*2)/maxX;
        double y_scale = (double) (this.getHeight()-borderSize*2)/maxY;
        double scale = Math.min(x_scale, y_scale);

        if(lastRoutine != null){
            g.setColor(Color.red);
            for(int i=0; i<lastRoutine.getSize();i++) {
                g.fillOval((int)(lastRoutine.getX(i)*scale-radius),(int) (lastRoutine.getY(i)*scale-radius),2*radius,2*radius);
                if(i!=0) {
                    g.drawLine((int)(lastRoutine.getX(i)*scale), (int) (lastRoutine.getY(i)*scale), (int) (lastRoutine.getX(i-1)*scale), (int) (lastRoutine.getY(i-1)*scale));
                }
            }
        }
        if(currentRoutine != null) {
            g.setColor(Color.black);
            for(int i=0; i<currentRoutine.getSize();i++) {
                g.fillOval((int)(currentRoutine.getX(i)*scale-radius),(int) (currentRoutine.getY(i)*scale-radius),2*radius,2*radius);
                if(i!=0){
                    g.drawLine((int)(currentRoutine.getX(i)*scale), (int) (currentRoutine.getY(i)*scale), (int) (currentRoutine.getX(i-1)*scale), (int) (currentRoutine.getY(i-1)*scale));
                }
            }
            lastRoutine = currentRoutine;
        }
    }
}