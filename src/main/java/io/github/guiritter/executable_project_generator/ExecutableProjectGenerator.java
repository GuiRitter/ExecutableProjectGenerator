package io.github.guiritter.executable_project_generator;

import static io.github.guiritter.graphical_user_interface.LabelledComponentFactory.buildFileChooser;
import static io.github.guiritter.graphical_user_interface.LabelledComponentFactory.buildLabelledComponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import io.github.guiritter.graphical_user_interface.FileChooserResponse;

public class ExecutableProjectGenerator{

	private static final int HALF_PADDING = 5;

	private static final int FULL_PADDING = 2 * HALF_PADDING;

	private static final String JAVA_VERSION = "17";

	static {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
	}

	private static final GridBagConstraints buildGBC(int y, int topPadding, int bottomPadding) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = y;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(topPadding, FULL_PADDING, bottomPadding, FULL_PADDING);
		return gbc;
	}

	public static final void main(String args[]) {
		JFrame frame = new JFrame("Executable Project Generator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridBagLayout());

		int y = 0;
		List<JTextField> textfieldList = new LinkedList<>();

		AtomicReference<File> file = new AtomicReference<>();
		frame.getContentPane().add(buildFileChooser(
				"Select Parent Path",
				SwingConstants.CENTER,
				SwingConstants.LEFT,
				0,
				JFileChooser.DIRECTORIES_ONLY,
				(FileChooserResponse response) -> {
					if ((response.state == JFileChooser.APPROVE_OPTION)
							&& (response.selectedFile != null)) {
						file.set(response.selectedFile);
					} else {
						file.set(null);
					}
				}
		), buildGBC(y++, FULL_PADDING, HALF_PADDING));

		JTextField artifactIdField = new JTextField("executable-project-generator");
		textfieldList.add(artifactIdField);

		frame.getContentPane().add(buildLabelledComponent(
				"artifactId",
				artifactIdField,
				SwingConstants.CENTER,
				SwingConstants.LEFT,
				0
		), buildGBC(y++, HALF_PADDING, HALF_PADDING));

		JTextField humanReadableNameField = new JTextField("Executable Project Generator");
		textfieldList.add(humanReadableNameField);

		frame.getContentPane().add(buildLabelledComponent(
				"Human Readable Name",
				humanReadableNameField,
				SwingConstants.CENTER,
				SwingConstants.LEFT,
				0
		), buildGBC(y++, HALF_PADDING, HALF_PADDING));

		JTextField descriptionField = new JTextField();

		frame.getContentPane().add(buildLabelledComponent(
				"Description",
				descriptionField,
				SwingConstants.CENTER,
				SwingConstants.LEFT,
				0
		), buildGBC(y++, HALF_PADDING, HALF_PADDING));

		JTextField packageField = new JTextField("executable_project_generator");
		textfieldList.add(packageField);

		frame.getContentPane().add(buildLabelledComponent(
				"Package",
				packageField,
				SwingConstants.CENTER,
				SwingConstants.LEFT,
				0
		), buildGBC(y++, HALF_PADDING, HALF_PADDING));

		JTextField mainClassField = new JTextField("ExecutableProjectGenerator");
		textfieldList.add(mainClassField);

		frame.getContentPane().add(buildLabelledComponent(
				"Main Class",
				mainClassField,
				SwingConstants.CENTER,
				SwingConstants.LEFT,
				0
		), buildGBC(y++, HALF_PADDING, HALF_PADDING));
		
		JButton createButton = new JButton("Create");
		createButton.addActionListener((ActionEvent event) -> {
			try {
				if ((file.get() == null) || textfieldList
						.stream()
						.anyMatch(field -> field.getText().trim().isEmpty())) {
					return;
				}
			} catch (NullPointerException ex) {
				return;
			}
			file.set(file.get().toPath().resolve(mainClassField.getText()).toFile());
			file.get().mkdir();
			try {
				BufferedWriter writer = Files.newBufferedWriter(
						file.get().toPath().resolve("pom.xml"),
						StandardOpenOption.CREATE,
						StandardOpenOption.TRUNCATE_EXISTING
				);
				writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n\txsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n\t<modelVersion>4.0.0</modelVersion>\n\n\t<groupId>io.github.guiritter</groupId>\n\t<artifactId>");
				writer.write(artifactIdField.getText());
				writer.write("</artifactId>\n\t<version>1.0.0</version>\n\t<packaging>jar</packaging>\n\n\t<name>");
				writer.write(humanReadableNameField.getText());
				writer.write("</name>\n\t<description>");
				writer.write(descriptionField.getText());
				writer.write("</description>\n\t<url>https://github.com/GuiRitter/");
				writer.write(mainClassField.getText());
				writer.write("</url>\n\n\t<licenses>\n\t\t<license>\n\t\t\t<name>MIT License</name>\n\t\t\t<url>http://www.opensource.org/licenses/mit-license.php</url>\n\t\t</license>\n\t</licenses>\n\n\t<developers>\n\t\t<developer>\n\t\t\t<name>Guilherme Alan Ritter</name>\n\t\t\t<email>gui.a.ritter@gmail.com</email>\n\t\t</developer>\n\t</developers>\n\n\t<properties>\n\t\t<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n\t\t<maven.compiler.source>" + JAVA_VERSION + "</maven.compiler.source>\n\t\t<maven.compiler.target>" + JAVA_VERSION + "</maven.compiler.target>\n\t\t<exec.mainClass>io.github.guiritter.");
				writer.write(packageField.getText());
				writer.write(".");
				writer.write(mainClassField.getText());
				writer.write("</exec.mainClass>\n\t</properties>\n\n\t<repositories>\n\t\t<repository>\n\t\t\t<id>jitpack.io</id>\n\t\t\t<url>https://jitpack.io</url>\n\t\t</repository>\n\t</repositories>\n\n\t<dependencies>\n\t</dependencies>\n\n\t<build>\n\t\t<plugins>\n\n\t\t\t<plugin>\n\t\t\t\t<groupId>org.apache.maven.plugins</groupId>\n\t\t\t\t<artifactId>maven-compiler-plugin</artifactId>\n\t\t\t\t<version>3.2</version>\n\t\t\t\t<configuration>\n\t\t\t\t\t<!-- Compile java " + JAVA_VERSION + " compatible bytecode -->\n\t\t\t\t\t<source>" + JAVA_VERSION + "</source>\n\t\t\t\t\t<target>" + JAVA_VERSION + "</target>\n\t\t\t\t</configuration>\n\t\t\t</plugin>\n\n\t\t\t<plugin>\n\t\t\t\t<groupId>org.apache.maven.plugins</groupId>\n\t\t\t\t<artifactId>maven-assembly-plugin</artifactId>\n\t\t\t\t<executions>\n\t\t\t\t\t<execution>\n\t\t\t\t\t\t<phase>package</phase>\n\t\t\t\t\t\t<goals>\n\t\t\t\t\t\t\t<goal>single</goal>\n\t\t\t\t\t\t</goals>\n\t\t\t\t\t\t<configuration>\n\t\t\t\t\t\t\t<archive>\n\t\t\t\t\t\t\t\t<manifest>\n\t\t\t\t\t\t\t\t\t<mainClass>io.github.guiritter.");
				writer.write(packageField.getText());
				writer.write(".");
				writer.write(mainClassField.getText());
				writer.write("</mainClass>\n\t\t\t\t\t\t\t\t</manifest>\n\t\t\t\t\t\t\t</archive>\n\t\t\t\t\t\t\t<descriptorRefs>\n\t\t\t\t\t\t\t\t<descriptorRef>jar-with-dependencies</descriptorRef>\n\t\t\t\t\t\t\t</descriptorRefs>\n\t\t\t\t\t\t</configuration>\n\t\t\t\t\t</execution>\n\t\t\t\t</executions>\n\t\t\t</plugin>\n\n\t\t\t<plugin>\n\t\t\t\t<!-- Create sources.jar -->\n\t\t\t\t<groupId>org.apache.maven.plugins</groupId>\n\t\t\t\t<artifactId>maven-source-plugin</artifactId>\n\t\t\t\t<version>3.0.1</version>\n\t\t\t\t<executions>\n\t\t\t\t\t<execution>\n\t\t\t\t\t\t<id>attach-sources</id>\n\t\t\t\t\t\t<goals>\n\t\t\t\t\t\t\t<goal>jar</goal>\n\t\t\t\t\t\t</goals>\n\t\t\t\t\t</execution>\n\t\t\t\t</executions>\n\t\t\t</plugin>\n\n\t\t\t<plugin>\n\t\t\t\t<groupId>org.apache.maven.plugins</groupId>\n\t\t\t\t<artifactId>maven-javadoc-plugin</artifactId>\n\t\t\t\t<version>3.1.0</version>\n\t\t\t\t<executions>\n\t\t\t\t\t<execution>\n\t\t\t\t\t\t<id>attach-javadocs</id>\n\t\t\t\t\t\t<goals>\n\t\t\t\t\t\t\t<goal>jar</goal>\n\t\t\t\t\t\t</goals>\n\t\t\t\t\t</execution>\n\t\t\t\t</executions>\n\t\t\t</plugin>\n\t\t</plugins>\n\t</build>\n\n</project>");
				writer.flush();
				writer.close();

				writer = Files.newBufferedWriter(
					file.get().toPath().resolve(".gitignore"),
					StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING
				);
				writer.write("*.class\ntarget/\nbin/\n.project\n.classpath\n.settings/\n.vscode/\n\n# Package Files #\n*.jar\nhs_err_pid*\n");
				writer.flush();
				writer.close();

				file.set(file
						.get()
						.toPath()
						.resolve("src")
						.resolve("main")
						.resolve("java")
						.resolve("io")
						.resolve("github")
						.resolve("guiritter")
						.resolve(packageField.getText())
						.toFile()
				);
				file.get().mkdirs();

				writer = Files.newBufferedWriter(
						file.get().toPath().resolve(mainClassField.getText() + ".java"),
						StandardOpenOption.CREATE,
						StandardOpenOption.TRUNCATE_EXISTING
				);
				writer.write("package io.github.guiritter.");
				writer.write(packageField.getText());
				writer.write(";\n\npublic class ");
				writer.write(mainClassField.getText());
				writer.write(" {\n\n\tpublic static void main(String args[]) {\n\t\tSystem.out.println(\"Hello, World!\");\n\t}\n}\n");
				writer.flush();
				writer.close();

				JOptionPane.showMessageDialog(
						frame,
						"Successfully created project from template!"
				);
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(
						frame,
						"Failed to handle files. Check console.",
						"Error",
						JOptionPane.ERROR_MESSAGE
				);
				return;
			}
		});

		frame.getContentPane().add(createButton, buildGBC(y++, HALF_PADDING, FULL_PADDING));

		frame.setVisible(true);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}
}