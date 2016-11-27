/**
 * Created by Andrew on 10/11/16.
 */
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;

import java.applet.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;

public class FormApplet extends Applet {
    private TextField textField;
    private TextArea output;
    private TextArea textArea;
    private Checkbox checkbox;
    private Choice choice = new Choice();
    private List list = new List();
    private Button button = new Button("Drop Table");

    public void init() {
        textField = new TextField();
        textArea = new TextArea(5, 20);
        checkbox = new Checkbox("Checked?");
        choice = new Choice();
        list = new List();
        button = new Button("Drop Table");
        output = new TextArea(5, 20);
    }

    public void start() {
        Panel textPanel = new Panel();
        Label textLabel = new Label("Type whatever you want:");
        textField.setColumns(20);
        textPanel.add(textLabel);
        textPanel.add(textField);
        textPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        Panel textAreaPanel = new Panel();
        Label textAreaLabel = new Label("Type a lot of text here:");
        textAreaPanel.add(textAreaLabel);
        textAreaPanel.add(textArea);
        textAreaPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        Panel checkboxPanel = new Panel();
        checkboxPanel.add(checkbox);
        checkboxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        Panel choicePanel = new Panel();
        Label choiceLabel = new Label("Make your choice!");
        choice.add("Blow up KPI Sicorski");
        choice.add("Get 1,000,000 Bellarusian rubles (without devalvation)");
        choice.add("Meet aliens (they will kill you)");
        choice.add("Have a date with Scrallet Yohanson");
        choicePanel.add(choiceLabel);
        choicePanel.add(choice);
        checkboxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        Panel listPanel = new Panel();
        list.add("1st");
        list.add("2nd");
        list.add("3rd");
        list.add("4th");
        list.add("FIVTh");
        list.add("pun intended");
        listPanel.add(list);
        listPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        Panel buttonPanel = new Panel();
        buttonPanel.add(button);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        output.setEditable(false);

        this.add(textPanel);
        this.add(textAreaPanel);
        this.add(checkboxPanel);
        this.add(choicePanel);
        this.add(listPanel);
        this.add(buttonPanel);
        this.add(output);

        this.setLayout(new FlowLayout(FlowLayout.LEADING));
//        this.setLayout(new GridLayout(10, 1));

        this.setSize(500, 500);

        button.addActionListener((e) -> {
            doStuff();
        });
    }

    public void doStuff() {
        System.out.print("IT'S ALIVE");
        String outputText = "";

        outputText += textField.getText() + "\n";
        outputText += textArea.getText() + "\n";
        outputText += checkbox.getState() + "\n";
        outputText += choice.getSelectedItem() + "\n";
        outputText += list.getSelectedItem() == null ? "" : list.getSelectedItem() + "\n";

        output.setText(outputText);
    }

    public void paint(Graphics g) {

    }
}
