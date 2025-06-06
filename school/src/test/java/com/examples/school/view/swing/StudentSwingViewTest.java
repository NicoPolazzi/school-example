package com.examples.school.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.examples.school.controller.SchoolController;
import com.examples.school.model.Student;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@RunWith(GUITestRunner.class)
public class StudentSwingViewTest extends AssertJSwingJUnitTestCase {
    private static final int TIMEOUT = 5000;

    @Mock
    private SchoolController schoolController;

    private AutoCloseable closeable;

    private FrameFixture window;
    private StudentSwingView studentSwingView;

    @Override
    protected void onSetUp() {
	closeable = MockitoAnnotations.openMocks(this);
	GuiActionRunner.execute(() -> {
	    studentSwingView = new StudentSwingView();
	    studentSwingView.setSchoolController(schoolController);
	    return studentSwingView;
	});
	window = new FrameFixture(robot(), studentSwingView);
	window.show();
    }

    @Override
    protected void onTearDown() throws Exception {
	closeable.close();
    }

    @Test
    @GUITest
    public void testControlsInitialStates() {
	window.label(JLabelMatcher.withText("id"));
	window.textBox("idTextBox").requireEnabled();
	window.label(JLabelMatcher.withText("name"));
	window.textBox("nameTextBox").requireEnabled();
	window.button(JButtonMatcher.withText("Add")).requireDisabled();
	window.list("studentList");
	window.button(JButtonMatcher.withText("Delete Selected")).requireDisabled();
	window.label("errorMessageLabel").requireText(" ");
    }

    @Test
    public void testWhenIdAndNameAreNonEmptyThenAddButtonShouldBeEnabled() {
	window.textBox("idTextBox").enterText("1");
	window.textBox("nameTextBox").enterText("test");
	window.button(JButtonMatcher.withText("Add")).requireEnabled();
    }

    @Test
    public void testWhenEitherIdOrNameAreBlankThenAddButtonShouldBeDisabled() {
	JTextComponentFixture idTextBox = window.textBox("idTextBox");
	JTextComponentFixture nameTextBox = window.textBox("nameTextBox");

	idTextBox.enterText("1");
	nameTextBox.enterText(" ");
	window.button(JButtonMatcher.withText("Add")).requireDisabled();

	idTextBox.setText("");
	nameTextBox.setText("");

	idTextBox.enterText(" ");
	nameTextBox.enterText("test");
	window.button(JButtonMatcher.withText("Add")).requireDisabled();

    }

    @Test
    public void testDeleteButtonShouldBeEnabledOnlyWhenAStudentIsSelected() {
	GuiActionRunner.execute(() -> studentSwingView.getListStudentsModel().addElement(new Student("1", "test")));
	window.list("studentList").selectItem(0);
	JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Delete Selected"));
	deleteButton.requireEnabled();
	window.list("studentList").clearSelection();
	deleteButton.requireDisabled();

    }

    @Test
    public void testShowAllStudentsShouldAddStudentDescriptionsToTheList() {
	Student student1 = new Student("1", "test1");
	Student student2 = new Student("2", "test2");
	studentSwingView.showAllStudents(Arrays.asList(student1, student2));
	String[] listContents = window.list().contents();
	assertThat(listContents).containsExactly(student1.toString(), student2.toString());
    }

    @Test
    public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
	var student = new Student("1", "test1");
	studentSwingView.showError("error message", student);
	window.label("errorMessageLabel").requireText("error message: " + student);
    }

    @Test
    public void testStudentAddedShouldAddTheStudentToTheListAndResetTheErrorLabel() {
	var student = new Student("1", "test1");
	studentSwingView.studentAdded(student);
	String[] listContents = window.list().contents();
	assertThat(listContents).containsExactly(student.toString());
	window.label("errorMessageLabel").requireText(" ");
    }

    @Test
    public void testStudentRemovedShouldRemoveTheStudentFromTheListAndResetTheErrorLabel() {
	var student1 = new Student("1", "test1");
	var student2 = new Student("2", "test2");
	GuiActionRunner.execute(() -> {
	    DefaultListModel<Student> listStudentModel = studentSwingView.getListStudentsModel();
	    listStudentModel.addElement(student1);
	    listStudentModel.addElement(student2);
	});

	studentSwingView.studentRemoved(new Student("1", "test1"));

	String[] listContents = window.list().contents();
	assertThat(listContents).containsExactly(student2.toString());
	window.label("errorMessageLabel").requireText(" ");
    }

    @Test
    public void testAddButtonShouldDelegateToSchoolControllerNewStudent() {
	window.textBox("idTextBox").enterText("1");
	window.textBox("nameTextBox").enterText("test");
	window.button(JButtonMatcher.withText("Add")).click();
	verify(schoolController, timeout(TIMEOUT)).newStudent(new Student("1", "test"));
    }

    @Test
    public void testDeleteButtonShouldDelegateToSchoolControllerDeleteStudent() {
	var student1 = new Student("1", "test1");
	var student2 = new Student("2", "test2");
	GuiActionRunner.execute(() -> {
	    DefaultListModel<Student> listStudentModel = studentSwingView.getListStudentsModel();
	    listStudentModel.addElement(student1);
	    listStudentModel.addElement(student2);
	});

	window.list("studentList").selectItem(1);
	window.button(JButtonMatcher.withText("Delete Selected")).click();
	verify(schoolController, timeout(TIMEOUT)).deleteStudent(student2);

    }
}
