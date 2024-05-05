import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Objects;

public class ButtonRenderer extends JButton implements TableCellRenderer {

    private int n;

    public void setN(int n) {
        this.n = n;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        if (row < n)
            setIcon(new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/Images/klose.png"))));
        else
            setIcon(null);
        setBackground(Color.decode("#bfc1c3"));
        setContentAreaFilled(true);
        setBorderPainted(false);
        return this;
    }
}