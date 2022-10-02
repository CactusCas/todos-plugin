
package com.todos;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

@Slf4j
class TodosPanel extends PluginPanel {
    private static final ImageIcon icon;
    private static final ImageIcon ADD_ICON;
    private static final ImageIcon ADD_HOVER_ICON;
    private static final ImageIcon REMOVE_ICON;
    private static final ImageIcon REMOVE_HOVER_ICON;

    private static final String NEWLINE = System.getProperty("line.separator");

    private final JLabel addTodo = new JLabel(ADD_ICON);
    private final JLabel removeTask = new JLabel(REMOVE_ICON);

    private final JTabbedPane tabbedPane = new JTabbedPane();

    private final JPanel todoPanel = new JPanel(new BorderLayout());
    private final JPanel donePanel = new JPanel(new BorderLayout());

    private final JPanel todoView = new JPanel();
    private final JPanel doneView = new JPanel();





    static {
        final BufferedImage todosIcon = ImageUtil.loadImageResource(TodosPlugin.class, "resources/todos_icon.png");
        final BufferedImage addIcon = ImageUtil.loadImageResource(TodosPlugin.class, "resources/add_icon.png");
        final BufferedImage removeIcon = ImageUtil.loadImageResource(TodosPlugin.class, "resources/remove_icon.png");

        icon = new ImageIcon(todosIcon);
        ADD_ICON = new ImageIcon(addIcon);
        REMOVE_ICON = new ImageIcon(removeIcon);
        ADD_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(addIcon, 0.53f));
        REMOVE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(removeIcon, 0.53f));

    }

    void init(TodosConfig config) {

        setLayout(new OverlayLayout(this));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        todoView.setLayout(new BoxLayout(todoView, BoxLayout.Y_AXIS));
        todoPanel.add(todoView, BorderLayout.CENTER);

        doneView.setLayout(new BoxLayout(doneView, BoxLayout.Y_AXIS));
        donePanel.add(doneView, BorderLayout.CENTER);

        // Setup create new option
        addTodo.setOpaque(false);
        addTodo.setAlignmentX(1.0f);
        addTodo.setAlignmentY(0.0f);

        //setting up tabs
        tabbedPane.add("Tasks", todoPanel);
        tabbedPane.add("Done", donePanel);
        tabbedPane.setAlignmentX(1.0f);
        tabbedPane.setAlignmentY(0.0f);

        add(addTodo);
        add(tabbedPane);


        addTodo.setToolTipText("Add new todo");
        addTodo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                JPanel panel = new JPanel(new BorderLayout());
                JTextArea textArea = new JTextArea(3, 2);
                panel.add(new JLabel("Start a new todo on a new line"),BorderLayout.NORTH);
                panel.add(textArea,BorderLayout.CENTER);
                Object[] options = {"Add", "Cancel"};
                switch (JOptionPane.showOptionDialog(null, panel, "Create Tasks", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                        icon, options, null)) {
                    case JOptionPane.YES_OPTION:
                        if(textArea.getText() != "") {
                            String input = textArea.getText().replaceAll(",", "");
                            String lines[] = input.split("\\r?\\n");
                            String todo = "";
                            for (String line : lines) {
                                todo = todo + line + ",false" + NEWLINE;
                                config.todoData(config.todoData() + todo);
                            }
                            createTodo(config, todo);
                            break;
                        }
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                removeTask.setIcon(ADD_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                removeTask.setIcon(ADD_ICON);
            }


        });

        createTodo(config, config.todoData());
        revalidateTasks();
    }


    void createTodo(TodosConfig config, String input) {
        if(input != ""){
            String lines[] = input.split("\\r?\\n");
            for (String line : lines) {
                String str[] = line.split(",");
                JPanel todoItem = new JPanel();
                todoItem.setLayout(new BorderLayout());

                JCheckBox todoCheckbox = new JCheckBox(str[0]);
                todoCheckbox.setPreferredSize(new Dimension(200, 26));
                todoCheckbox.setToolTipText(str[0]);
                todoCheckbox.setSelected(Boolean.parseBoolean(str[1]));

                JLabel removeTask = new JLabel(REMOVE_ICON);
                removeTask.setToolTipText("Remove todo");

                todoItem.add(todoCheckbox, BorderLayout.WEST);
                todoItem.add(removeTask, BorderLayout.EAST);

                removeTask.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        config.todoData(config.todoData().replace(todoCheckbox.getText() + "," + todoCheckbox.isSelected() + NEWLINE, ""));
                        todoItem.getParent().remove(todoItem);
                        revalidateTasks();

                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        removeTask.setIcon(REMOVE_HOVER_ICON);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        removeTask.setIcon(REMOVE_ICON);
                    }
                });

                todoCheckbox.addItemListener(e -> {
                    JCheckBox box = (JCheckBox) e.getSource();
                    if (box.isSelected()) {
                        config.todoData(config.todoData().replace(todoCheckbox.getText() + ",false" , todoCheckbox.getText() + ",true" ));
                        doneView.add(todoItem);
                        revalidateTasks();
                    } else {
                        config.todoData(config.todoData().replace(todoCheckbox.getText() + ",true", todoCheckbox.getText() + ",false" ));
                        todoView.add(todoItem);
                        revalidateTasks();
                    }
                });
                if (todoCheckbox.isSelected()) {
                    doneView.add(todoItem);
                } else {
                    todoView.add(todoItem);
                }
                revalidateTasks();
            }
        }
    }

    void revalidateTasks(){
        repaint();
        revalidate();
    }
}