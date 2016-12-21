package org.colorcoding.tools.btulz.shell.gui.commands;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.colorcoding.tools.btulz.shell.Environment;
import org.colorcoding.tools.btulz.shell.commands.Command;
import org.colorcoding.tools.btulz.shell.commands.CommandBuilder;
import org.colorcoding.tools.btulz.shell.commands.CommandItem;
import org.colorcoding.tools.btulz.shell.commands.ValidValue;

/**
 * 命令执行选项卡
 * 
 * @author Niuren.Zhu
 *
 */
public class CommandTab extends WorkingTab {

	private static final long serialVersionUID = -1962365855779611380L;

	public CommandTab(CommandBuilder commandBuilder) {
		super(commandBuilder.getName());
		this.setBuilder(commandBuilder);
		this.init();// 之前未赋值，再调一次
	}

	private CommandBuilder builder;

	public final CommandBuilder getBuilder() {
		return builder;
	}

	public final void setBuilder(CommandBuilder builder) {
		this.builder = builder;
	}

	private String historyFolder;

	public final String getHistoryFolder() {
		if (this.historyFolder == null) {
			this.historyFolder = Environment.getHistoryFolder() + File.separator + "commands";
		}
		return historyFolder;
	}

	public final void setHistoryFolder(String historyFolder) {
		this.historyFolder = historyFolder;
	}

	private int last_column_ipadx = 40;
	private JButton button_stop = null;
	private JButton button_run = null;
	private CommandTab that = this;
	private JLabel label_command = null;
	private Command command = null;

	@Override
	protected void init() {
		if (this.builder == null) {
			return;
		}
		this.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		int count = 0;
		gridBagConstraints.gridy = count;// 组件的纵坐标
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		gridBagConstraints.insets = new Insets(1, 1, 0, 0);
		// 名称
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.ipadx = 0;
		this.add(new JLabel(String.format("%s - %s", this.getBuilder().getName(), this.getBuilder().getDescription())),
				gridBagConstraints);
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.ipadx = last_column_ipadx;
		this.button_stop = new JButton("stop");
		this.button_stop.setEnabled(false);
		this.button_stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				that.button_stop.setEnabled(false);
				if (that.command != null) {
					that.command.destroy();
				}
				// that.label_command.setText("");
				that.button_run.setEnabled(true);
			}
		});
		this.add(this.button_stop, gridBagConstraints);
		gridBagConstraints.gridy++;
		// 内容
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.ipadx = 0;
		this.add(new JLabel("History"), gridBagConstraints);
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.ipadx = 0;
		JComboBox<ValidValue> comboBox = new JComboBox<>(this.getValueHistory());
		this.add(comboBox, gridBagConstraints);
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.ipadx = last_column_ipadx;
		this.button_run = new JButton("run");
		this.button_run.setEnabled(true);
		this.button_run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				that.button_run.setEnabled(false);
				that.command = new Command(that.getBuilder());
				that.button_stop.setEnabled(true);
			}
		});
		this.add(this.button_run, gridBagConstraints);
		gridBagConstraints.gridy++;
		// 内容项目
		for (CommandItem commandItem : this.getBuilder().getItems()) {
			this.addCommandItemLine(commandItem, gridBagConstraints);
		}
	}

	private ValidValue[] getValueHistory() {
		ArrayList<ValidValue> values = new ArrayList<>();
		File folder = new File(this.getHistoryFolder());
		if (folder.exists()) {
			for (File file : folder.listFiles()) {
				if (!file.isFile()) {
					continue;
				}
				if (file.length() == 0) {
					continue;
				}
				if (!file.getName().endsWith(".xml")) {
					continue;
				}
				if (!file.getName().startsWith(this.getBuilder().getName())) {
					continue;
				}
				values.add(new ValidValue(file.getName(), file.getName()));
			}
		}
		return values.toArray(new ValidValue[] {});
	}

	private void addCommandItemLine(CommandItem commandItem, GridBagConstraints gridBagConstraints) {
		// 添加命令内容
		JTextField textField = new JTextField();
		textField.setText(commandItem.getContent());
		textField.setEditable(false);
		textField.setToolTipText(commandItem.getDescription());
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.ipadx = 0;
		this.add(textField, gridBagConstraints);
		// 添加命令值
		JTextField textValue = null;
		commandItem.getValidValues().get();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.ipadx = 0;
		if (commandItem.getValidValues().size() > 0) {
			JComboBox<?> comboBox = new JComboBox<ValidValue>(commandItem.getValidValues().toArray());
			comboBox.setEditable(false);
			comboBox.setSelectedIndex(0);
			this.add(comboBox, gridBagConstraints);
		} else {
			textValue = new JTextField(commandItem.getValue());
			textValue.setEditable(commandItem.isEditable());
			if (commandItem.getItems().size() > 0) {
				// 存在子命令
				textValue.setEditable(false);
			}
			this.add(textValue, gridBagConstraints);
		}
		// 添加选择
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridwidth = 1;
		// gridBagConstraints.ipadx = 10;
		JCheckBox checkBox = new JCheckBox();
		checkBox.setToolTipText("selected and run it");
		checkBox.setSelected(true);
		if (!commandItem.isOptional()) {
			checkBox.setEnabled(false);
		}
		this.add(checkBox, gridBagConstraints);
		// 添加额外内容
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.ipadx = last_column_ipadx;
		JButton button = new JButton("...");
		if (commandItem.getValidValues().getClassName() != null
				&& commandItem.getValidValues().getClassName().equals(JFileChooser.class.getName())) {
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser jfc = new JFileChooser();
					jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					jfc.showDialog(new JLabel(), "Selected");
					if (button != null && jfc.getSelectedFile() != null) {
						button.setText(jfc.getSelectedFile().getPath());
					}
				}
			});
		} else {
			button.setEnabled(false);
		}
		this.add(button, gridBagConstraints);
		gridBagConstraints.gridy++;
		// 添加子项
		for (CommandItem item : commandItem.getItems()) {
			this.addCommandItemLine(item, gridBagConstraints);
		}
	}
}
