import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

//making custom cell renderer for the Table
class ProgressRenderer extends DefaultTableCellRenderer {
    private final JProgressBar bar;

    public ProgressRenderer() {
        super();
        bar = new JProgressBar(0, 100);//min and max value
        bar.setBackground(Color.decode("#161a1d"));
        bar.setUI(new FancyProgressBar());//creating custom look for progress bar
        bar.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    }

    public Component getTableCellRendererComponent(JTable table, Object value
            , boolean isSelected, boolean hasFocus, int row, int column) {
        Integer i = (Integer) value; //column type progress bar is Integer
        String text = "Done";
        if (i < 0) {
            text = "Error";
        } else if (i < 100) {
            bar.setValue(i);
            return bar;
        }
        //if there is no more processing
        Component tableCellRendererComponent = super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
        tableCellRendererComponent.setFont(new Font("", Font.BOLD, 18));
        tableCellRendererComponent.setForeground(text.equals("Error") ? Color.red : Color.decode("#49C628"));
        tableCellRendererComponent.setBackground(Color.decode("#161a1d"));
        ((DefaultTableCellRenderer) tableCellRendererComponent).setOpaque(true);
        ((DefaultTableCellRenderer) tableCellRendererComponent).setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        return this;
    }
}