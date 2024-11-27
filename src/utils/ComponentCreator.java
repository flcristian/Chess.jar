package utils;

import constants.Globals;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class ComponentCreator {
    private ComponentCreator() {}

    public static JButton createStyledButton(String text, Color baseColor, Font font) {
        JButton button = new JButton(text);
        button.setFont(font.deriveFont(Font.BOLD, 36f));
        button.setBackground(baseColor);
        button.setForeground(Globals.COLOR_DARK_GOLD);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(new LineBorder(baseColor.darker(), 5, true) {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getLineColor());
                g2.setStroke(new BasicStroke(getThickness()));
                g2.drawRoundRect(x, y, width - 1, height - 1, 30, 30);
                g2.dispose();
            }
        });

        button.setUI(new BasicButtonUI() {
            @Override
            protected void installDefaults(AbstractButton b) {
                super.installDefaults(b);
                b.setOpaque(false);
            }
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 30, 30);
                super.paint(g2, c);
                g2.dispose();
            }

            @Override
            protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                FontMetrics fm = g.getFontMetrics();
                int mnemonicIndex = b.getDisplayedMnemonicIndex();

                g.setColor(new Color(0, 0, 0, 70));
                paintStringWithMnemonic(g, text, mnemonicIndex,
                        textRect.x + 3, textRect.y + fm.getAscent() + 3);

                g.setColor(new Color(0, 0, 0, 30));
                paintStringWithMnemonic(g, text, mnemonicIndex,
                        textRect.x + 4, textRect.y + fm.getAscent() + 4);

                // Draw main text
                g.setColor(model.isEnabled() ? b.getForeground() : UIManager.getColor("Button.disabledText"));
                paintStringWithMnemonic(g, text, mnemonicIndex,
                        textRect.x, textRect.y + fm.getAscent());
            }

            private void paintStringWithMnemonic(Graphics g, String text, int mnemonicIndex, int x, int y) {
                if (mnemonicIndex >= 0 && mnemonicIndex < text.length()) {
                    g.drawString(text.substring(0, mnemonicIndex), x, y);
                    g.drawString(text.substring(mnemonicIndex, mnemonicIndex + 1), x + g.getFontMetrics().stringWidth(text.substring(0, mnemonicIndex)), y);
                    g.drawString(text.substring(mnemonicIndex + 1), x + g.getFontMetrics().stringWidth(text.substring(0, mnemonicIndex + 1)), y);
                } else {
                    g.drawString(text, x, y);
                }
            }

            @Override
            protected void paintButtonPressed(Graphics g, AbstractButton b) {
                // Do nothing to prevent the "pressed" visual effect
            }
        });

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBorder(new LineBorder(Globals.COLOR_DARK_GOLD, 5, true) {
                    @Override
                    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(getLineColor());
                        g2.setStroke(new BasicStroke(getThickness()));
                        g2.drawRoundRect(x, y, width - 1, height - 1, 30, 30);
                        g2.dispose();
                    }
                });
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBorder(new LineBorder(baseColor.darker(), 5, true) {
                    @Override
                    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(getLineColor());
                        g2.setStroke(new BasicStroke(getThickness()));
                        g2.drawRoundRect(x, y, width - 1, height - 1, 30, 30);
                        g2.dispose();
                    }
                });
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                int darkerRed = Math.max(baseColor.getRed() - 8, 0);
                int darkerGreen = Math.max(baseColor.getGreen() - 8, 0);
                int darkerBlue = Math.max(baseColor.getBlue() - 8, 0);
                button.setBackground(new Color(darkerRed, darkerGreen, darkerBlue));
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });
        return button;
    }

    public static JLabel createShadowedLabel(String text, Font font, Color foregroundColor) {
        return new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                FontMetrics fm = g2d.getFontMetrics(font);
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() - textHeight) / 2 + fm.getAscent();

                g2d.setFont(font);

                // Draw shadows
                g2d.setColor(new Color(0, 0, 0, 70));
                g2d.drawString(getText(), x + 3, y + 3);
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.drawString(getText(), x + 2, y + 2);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawString(getText(), x + 1, y + 1);

                // Draw main text
                g2d.setColor(foregroundColor);
                g2d.drawString(getText(), x, y);

                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                FontMetrics fm = getFontMetrics(font);
                int width = fm.stringWidth(getText()) + 10; // Add some padding
                int height = fm.getHeight() + 10; // Add some padding
                return new Dimension(width, height);
            }
        };
    }
}

