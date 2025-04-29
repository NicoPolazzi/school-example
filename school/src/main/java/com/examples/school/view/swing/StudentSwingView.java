package com.examples.school.view.swing;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import com.examples.school.controller.SchoolController;
import com.examples.school.model.Student;
import com.examples.school.view.StudentView;

public class StudentSwingView extends JFrame implements StudentView {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtId;
    private JLabel lblName;
    private JTextField txtName;
    private JButton btnAdd;
    private JScrollPane scrollPane;
    private JButton btnDeleteSelected;
    private JLabel lblErrorMessage;

    private JList<Student> listStudents;
    private DefaultListModel<Student> listStudentsModel;
    private SchoolController schoolController;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(() -> {
	    try {
		StudentSwingView frame = new StudentSwingView();
		frame.setVisible(true);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	});
    }

    // Used for testing purpose
    DefaultListModel<Student> getListStudentsModel() {
	return listStudentsModel;
    }

    /**
     * Create the frame.
     */
    public StudentSwingView() {

	setTitle("Student View\n");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(100, 100, 926, 629);
	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

	setContentPane(contentPane);
	GridBagLayout gbl_contentPane = new GridBagLayout();
	gbl_contentPane.columnWidths = new int[] { 34, 875, 0 };
	gbl_contentPane.rowHeights = new int[] { 21, 21, 27, 453, 0, 0, 0 };
	gbl_contentPane.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
	gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
	contentPane.setLayout(gbl_contentPane);

	JLabel lblId = new JLabel("id");
	GridBagConstraints gbc_lblId = new GridBagConstraints();
	gbc_lblId.anchor = GridBagConstraints.EAST;
	gbc_lblId.insets = new Insets(0, 0, 5, 5);
	gbc_lblId.gridx = 0;
	gbc_lblId.gridy = 0;
	contentPane.add(lblId, gbc_lblId);

	txtId = new JTextField();
	KeyAdapter btnAddEnabler = new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		btnAdd.setEnabled(!txtId.getText().trim().isEmpty() && !txtName.getText().trim().isEmpty());
	    }
	};
	txtId.setName("idTextBox");
	GridBagConstraints gbc_textField = new GridBagConstraints();
	gbc_textField.anchor = GridBagConstraints.NORTH;
	gbc_textField.fill = GridBagConstraints.HORIZONTAL;
	gbc_textField.insets = new Insets(0, 0, 5, 0);
	gbc_textField.gridx = 1;
	gbc_textField.gridy = 0;
	contentPane.add(txtId, gbc_textField);
	txtId.setColumns(10);

	lblName = new JLabel("name");
	GridBagConstraints gbc_lblName = new GridBagConstraints();
	gbc_lblName.anchor = GridBagConstraints.EAST;
	gbc_lblName.insets = new Insets(0, 0, 5, 5);
	gbc_lblName.gridx = 0;
	gbc_lblName.gridy = 1;
	contentPane.add(lblName, gbc_lblName);

	btnAdd = new JButton("Add");
	btnAdd.setEnabled(false);
	btnAdd.addActionListener(e -> schoolController.newStudent(new Student(txtId.getText(), txtName.getText())));
	txtId.addKeyListener(btnAddEnabler);
	txtName = new JTextField();
	txtName.addKeyListener(btnAddEnabler);
	txtName.setName("nameTextBox");
	GridBagConstraints gbc_textField_1 = new GridBagConstraints();
	gbc_textField_1.anchor = GridBagConstraints.NORTH;
	gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
	gbc_textField_1.insets = new Insets(0, 0, 5, 0);
	gbc_textField_1.gridx = 1;
	gbc_textField_1.gridy = 1;
	contentPane.add(txtName, gbc_textField_1);
	txtName.setColumns(10);
	GridBagConstraints gbc_btnAdd = new GridBagConstraints();
	gbc_btnAdd.anchor = GridBagConstraints.NORTH;
	gbc_btnAdd.insets = new Insets(0, 0, 5, 0);
	gbc_btnAdd.gridx = 1;
	gbc_btnAdd.gridy = 2;
	contentPane.add(btnAdd, gbc_btnAdd);

	scrollPane = new JScrollPane();
	GridBagConstraints gbc_scrollPane = new GridBagConstraints();
	gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
	gbc_scrollPane.fill = GridBagConstraints.BOTH;
	gbc_scrollPane.gridwidth = 2;
	gbc_scrollPane.gridx = 0;
	gbc_scrollPane.gridy = 3;
	contentPane.add(scrollPane, gbc_scrollPane);

	listStudentsModel = new DefaultListModel<>();
	listStudents = new JList<>(listStudentsModel);
	listStudents.addListSelectionListener(e -> btnDeleteSelected.setEnabled(listStudents.getSelectedIndex() != -1));
	scrollPane.setViewportView(listStudents);
	listStudents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	listStudents.setName("studentList");

	btnDeleteSelected = new JButton("Delete Selected");
	btnDeleteSelected.addActionListener(e -> schoolController.deleteStudent(listStudents.getSelectedValue()));
	btnDeleteSelected.setEnabled(false);
	GridBagConstraints gbc_btnDeleteSelected = new GridBagConstraints();
	gbc_btnDeleteSelected.insets = new Insets(0, 0, 5, 0);
	gbc_btnDeleteSelected.gridwidth = 2;
	gbc_btnDeleteSelected.gridx = 0;
	gbc_btnDeleteSelected.gridy = 4;
	contentPane.add(btnDeleteSelected, gbc_btnDeleteSelected);

	lblErrorMessage = new JLabel(" ");
	lblErrorMessage.setName("errorMessageLabel");
	GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
	gbc_lblNewLabel.gridwidth = 2;
	gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
	gbc_lblNewLabel.gridx = 0;
	gbc_lblNewLabel.gridy = 5;
	contentPane.add(lblErrorMessage, gbc_lblNewLabel);
    }

    @Override
    public void showAllStudents(List<Student> students) {
	students.stream().forEach(listStudentsModel::addElement);

    }

    @Override
    public void showError(String message, Student student) {
	lblErrorMessage.setText(message + ": " + student);
    }

    @Override
    public void studentAdded(Student student) {
	listStudentsModel.addElement(student);
	resetErrorLabel();
    }

    @Override
    public void studentRemoved(Student student) {
	listStudentsModel.removeElement(student);
	resetErrorLabel();
    }

    private void resetErrorLabel() {
	lblErrorMessage.setText(" ");
    }

    public void setSchoolController(SchoolController schoolController) {
	this.schoolController = schoolController;
    }

}
