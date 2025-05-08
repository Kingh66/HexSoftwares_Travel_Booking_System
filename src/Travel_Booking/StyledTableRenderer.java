/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Travel_Booking;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author sizwe
 */
public class StyledTableRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, 
            isSelected, hasFocus, row, column);
        
        // Set consistent styling
        c.setForeground(Color.BLACK);
        c.setBackground(new Color(255, 255, 255, 30));
        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Add padding
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        return c;
    }
}
