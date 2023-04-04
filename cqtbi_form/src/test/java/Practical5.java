import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Practical5 {

    Frame frame = new Frame("basic");
    Button ok = new Button("determine");

    TextField tf = new TextField(50);
    Choice colorChooser = new Choice();
    CheckboxGroup cbg = new CheckboxGroup();
    Checkbox male = new Checkbox("man",cbg,true);
    Checkbox female = new Checkbox("woman",cbg,false);
    Checkbox married = new Checkbox("Whether you are married?",false);
    TextArea ta = new TextArea(5,20);
    List colorList = new List(6,false);
    MenuBar menuBar = new MenuBar();

    Menu fileMenu = new Menu("file");
    Menu editMenu = new Menu("edit");
    Menu formatMenu = new Menu("format");
    MenuItem creat = new MenuItem("newly built", new MenuShortcut(KeyEvent.VK_N, false));
    MenuItem open = new MenuItem("Open", new MenuShortcut(KeyEvent.VK_O, false));
    MenuItem save = new MenuItem("Save", new MenuShortcut(KeyEvent.VK_S, false));
    MenuItem autoWrap = new MenuItem("word wrap");
    MenuItem copy = new MenuItem("copy");
    MenuItem paste = new MenuItem("stickup");
    MenuItem comment = new MenuItem("exegesis");
    MenuItem cancelComent = new MenuItem("Uncomment");

    public void init(){

        Panel bottom = new Panel();

        tf.addTextListener(new TextListener() {
            @Override
            public void textValueChanged(TextEvent textEvent) {
                System.out.println("文本框的内容" + tf.getText());
            }
        });

        bottom.add(tf);

        bottom.add(ok);
        frame.add(bottom, BorderLayout.SOUTH);

        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ta.append("您确认了输入信息：" + tf.getText() + "\n");
            }
        });

        colorChooser.add("red");
        colorChooser.add("green");
        colorChooser.add("blue");
        Panel checkPanelss = new Panel();

        colorChooser.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                String cmd = itemEvent.getItem().toString();
                ta.append("颜色列表选择框选中了：" + cmd + "\n");
                for (int i = 0; i < colorList.getItemCount(); i++) {
                    if(cmd.equals(colorList.getItem(i).toString())){
                        colorList.select(i);
                        break;
                    }
                }
            }
        });
        checkPanelss.add(colorChooser);

        male.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange()==1){
                    ta.append("我是男生!\n");
                }
            }
        });
        checkPanelss.add(male);

        female.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange()==1){
                    ta.append("我是女生!\n");
                }
            }
        });
        checkPanelss.add(female);

        married.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange()==1){
                    ta.append("本人已婚，请您自重!\n");
                }else{
                    ta.append("本人单身，欢迎来撩!\n");
                }
            }
        });
        checkPanelss.add(married);

        Box topLeft = Box.createVerticalBox();
        topLeft.add(ta);
        topLeft.add(checkPanelss);

        Box top = Box.createHorizontalBox();
        top.add(topLeft);

        colorList.add("red");
        colorList.add("green");
        colorList.add("blue");
        top.add(colorList);

        colorList.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                int ind = Integer.parseInt(itemEvent.getItem().toString());

                String cmd = colorList.getItem(ind);
                ta.append("颜色列表选择框选中了：" + cmd + "\n");

                colorChooser.select(cmd);
            }
        });

        frame.add(top);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.pack();
        frame.setVisible(true);

        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ta.append("您点击了菜单项：" + actionEvent.getActionCommand() + "\n");
            }
        };

        comment.addActionListener(listener);
        formatMenu.add(comment);
        cancelComent.addActionListener(listener);
        formatMenu.add(cancelComent);
        fileMenu.add(creat);
        fileMenu.addSeparator();
        fileMenu.add(open);
        fileMenu.add(save);
        autoWrap.addActionListener(listener);
        creat.addActionListener(listener);
        open.addActionListener(listener);
        save.addActionListener(listener);
        copy.addActionListener(listener);
        paste.addActionListener(listener);

        editMenu.add(formatMenu);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        editMenu.add(autoWrap);
        editMenu.add(copy);
        editMenu.add(paste);
        editMenu.addSeparator();
        frame.setMenuBar(menuBar);
        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

    }

    public static void main(String[] args) {

        new Practical5().init();
    }
}


