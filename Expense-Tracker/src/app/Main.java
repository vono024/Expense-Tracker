package app;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            FlatLaf.registerCustomDefaultsSource("resources");

            UIManager.setLookAndFeel(new FlatDarculaLaf());

            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));

            UIManager.put("Component.arc", 12);
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.focusWidth", 2);

        } catch (Exception e) {
            System.err.println("Не вдалося застосувати FlatLaf");
        }

        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
