package hostelmanagementsystem;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;

import hostelmanagement.gui.LoginFrame;

public class HostelManagementSystem {

    public static void main(String[] args) {

        // =================================================
        // SET APPLICATION LOOK AND FEEL
        // =================================================

        try {

            boolean nimbusFound = false;

            for (UIManager.LookAndFeelInfo info
                    : UIManager.getInstalledLookAndFeels()) {

                if ("Nimbus".equals(
                        info.getName())) {

                    UIManager.setLookAndFeel(
                            info.getClassName()
                    );

                    nimbusFound = true;

                    break;
                }
            }

            // Fallback to the system look and feel
            // if Nimbus is not available.

            if (!nimbusFound) {

                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "Could not set application Look & Feel."
            );
        }

        // =================================================
        // GLOBAL FONT SETTINGS
        // =================================================

        UIManager.put(
                "Label.font",
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        UIManager.put(
                "Button.font",
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        UIManager.put(
                "TextField.font",
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        UIManager.put(
                "PasswordField.font",
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        UIManager.put(
                "ComboBox.font",
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        UIManager.put(
                "Table.font",
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        UIManager.put(
                "TableHeader.font",
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );

        // =================================================
        // START APPLICATION
        // =================================================

        SwingUtilities.invokeLater(
                () -> {

                    LoginFrame loginFrame =
                            new LoginFrame();

                    loginFrame.setVisible(
                            true
                    );
                }
        );
    }
}