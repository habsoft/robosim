package pk.com.habsoft.robosim.filters.histogram;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import pk.com.habsoft.robosim.utils.Util;

public class SimulationBuilder extends JDialog {

	private static final long serialVersionUID = 1L;

	final static int FRAME_WIDTH = 440, FRAME_HEIGHT = 480;

	public static void main(String[] args) {
		// Sense RED, Move Right,Sense Green,Move Down
		int[][] commands = { { 0, 0 }, { 5, 1 }, { 1, 0 }, { 7, 1 } };
		SimulationBuilder frame = new SimulationBuilder(commands, 3, 9);
		System.out.println("old commands");
		Util.printArrayP(commands);
		System.out.println("new commands");
		Util.printArrayP(frame.getNewCommands());

	}

	JPanel pnlNorth;
	JComboBox<String> cmbSense, cmbMove;

	JButton btnAdd;
	JButton btnDelete;
	JButton btnDeleteAll;

	JButton btnOk, btnCancel;

	ArrayList<RobotCommands> list = new ArrayList<RobotCommands>();
	int[][] commands;
	DataModel dataModel;
	JTable table;
	int totalColors, totalMotions;
	String[] colors, motions;

	public SimulationBuilder(int[][] commands, int totalColors, int totalMotions) {
		setTitle("Simulation Builder");
		this.totalColors = totalColors;
		this.totalMotions = totalMotions;
		colors = new String[totalColors];
		motions = new String[totalMotions];
		this.commands = commands;
		for (int i = 0; i < commands.length; i = i + 2) {
			// (count,sense%totalColors,motion%totalMotions)
			list.add(new RobotCommands((i + 2) / 2, commands[i][0] % totalColors, commands[i + 1][0] % totalMotions));
		}
		System.arraycopy(HistogramFilterView.sensorNames, 0, colors, 0, totalColors);
		System.arraycopy(HistogramFilterView.btnNames, 0, motions, 0, totalMotions);
		initGUI();
	}

	public void initGUI() {

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		pnlNorth = new JPanel();
		pnlNorth.setLayout(null);

		int spacing = 5;
		int xLoc = 10;
		int yLoc = 10;
		int width = 100;
		int height = 30;

		JLabel lblSense = new JLabel("Sense");
		lblSense.setBounds(xLoc + spacing, yLoc + spacing, width - spacing, height - spacing);
		pnlNorth.add(lblSense);

		cmbSense = new JComboBox<String>(colors);
		cmbSense.setBounds(xLoc + width + spacing, yLoc + spacing, width - spacing, height - spacing);
		pnlNorth.add(cmbSense);

		JLabel lblMove = new JLabel("Move");
		lblMove.setBounds(xLoc + width * 2 + spacing, yLoc + spacing, width - spacing, height - spacing);
		pnlNorth.add(lblMove);

		cmbMove = new JComboBox<String>(motions);
		cmbMove.setBounds(xLoc + width * 3 + spacing, yLoc + spacing, width - spacing, height - spacing);
		pnlNorth.add(cmbMove);

		xLoc += 50;
		yLoc += height;
		btnAdd = new JButton("Add");
		btnAdd.setBounds(xLoc + spacing, yLoc + spacing, width - spacing, height - spacing);
		pnlNorth.add(btnAdd);

		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dataModel.insertData(new RobotCommands(table.getRowCount() + 1, cmbSense.getSelectedIndex(),
						cmbMove.getSelectedIndex()));
				table.tableChanged(new TableModelEvent(dataModel, table.getRowCount() + 1, table.getRowCount() + 1,
						TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
			}
		});

		btnDelete = new JButton("Delete");
		btnDelete.setBounds(xLoc + width + spacing, yLoc + spacing, width - spacing, height - spacing);
		pnlNorth.add(btnDelete);

		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = table.getSelectedRow();
				if (index < 0) {
					if (dataModel.delete(dataModel.getRowCount() - 1)) {
						table.tableChanged(new TableModelEvent(dataModel, table.getRowCount(), table.getRowCount(),
								TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
					}
				} else {
					if (dataModel.delete(index)) {
						table.tableChanged(new TableModelEvent(dataModel, table.getRowCount(), table.getRowCount(),
								TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
					}
				}
				resetSerialNumbers();
			}
		});

		btnDeleteAll = new JButton("Delete All");
		btnDeleteAll.setBounds(xLoc + width * 2 + spacing, yLoc + spacing, width - spacing, height - spacing);
		pnlNorth.add(btnDeleteAll);

		btnDeleteAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				while (dataModel.getRowCount() > 0) {
					dataModel.delete(0);
				}
				table.revalidate();
			}
		});

		xLoc += 50;
		yLoc += height;
		btnOk = new JButton("Ok");
		btnOk.setBounds(xLoc + spacing, yLoc + spacing, width - spacing, height - spacing);
		pnlNorth.add(btnOk);
		btnOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int[][] newCommands = new int[dataModel.getRowCount() * 2][2];
				for (int i = 0; i < dataModel.getRowCount(); i++) {
					RobotCommands cmd = dataModel.m_vector.get(i);
					newCommands[i * 2][0] = cmd.getSense();
					newCommands[i * 2][1] = HistogramSimulator.SENSE;
					newCommands[i * 2 + 1][0] = cmd.getMove();
					newCommands[i * 2 + 1][1] = HistogramSimulator.MOVE;
				}
				commands = newCommands;
				dispose();
			}
		});

		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(xLoc + width + spacing, yLoc + spacing, width - spacing, height - spacing);
		pnlNorth.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		// ///////////////////////////////////////////////////////////////////////

		dataModel = new DataModel(this);
		table = new JTable();
		table.setModel(dataModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		table.setRowHeight(25);

		// for (int k = 0; k < DataModel.m_columns.length; k++) {
		// TableCellRenderer renderer;
		//
		// DefaultTableCellRenderer textRenderer = new
		// DefaultTableCellRenderer();
		// textRenderer.setHorizontalAlignment(DataModel.m_columns[k].m_alignment);
		// renderer = textRenderer;
		// TableCellEditor editor;
		// JTextField text;
		// editor = new DefaultCellEditor(text = new JTextField());
		// text.setEditable(false);
		// }

		JTableHeader header = table.getTableHeader();
		header.setUpdateTableInRealTime(false);
		header.setReorderingAllowed(false);

		TableCellEditor myCellEditor = new MyComboBoxEditor(colors);
		TableColumn sensor = table.getColumnModel().getColumn(1);
		sensor.setCellEditor(myCellEditor);
		sensor.setCellRenderer(new MyComboBoxRenderer(colors));

		myCellEditor = new MyComboBoxEditor(motions);
		TableColumn motion = table.getColumnModel().getColumn(2);
		motion.setCellEditor(myCellEditor);
		motion.setCellRenderer(new MyComboBoxRenderer(motions));

		// ///////////////////////////////////////////////////////////////////////

		xLoc = 10;
		yLoc += height;
		JScrollPane scrollPane = new JScrollPane(table);
		pnlNorth.add(scrollPane);
		scrollPane.setBounds(xLoc + spacing, yLoc + spacing, 400, 300);

		// Adding panel in Jframe
		add(pnlNorth);

		for (int i = 0; i < list.size(); i++) {
			dataModel.insertData(list.get(i));
		}

		setModalityType(ModalityType.APPLICATION_MODAL);

		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) size.getWidth();
		height = (int) size.getHeight();

		setLocation(width / 2 - FRAME_WIDTH / 2, height / 2 - FRAME_HEIGHT / 2);
		setSize(FRAME_WIDTH + 20, FRAME_HEIGHT + 10);
		setMinimumSize(new Dimension(FRAME_WIDTH + 20, FRAME_HEIGHT + 10));
		setVisible(true);

	}

	void resetSerialNumbers() {
		for (int i = 0; i < dataModel.getRowCount(); i++) {
			RobotCommands cmd = dataModel.m_vector.get(i);
			cmd.setCount(i + 1);
		}
	}

	public int[][] getNewCommands() {
		return this.commands;
	}

	private class MyComboBoxRenderer extends JComboBox<Object> implements TableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MyComboBoxRenderer(String[] items) {
			super(items);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				super.setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				setBackground(table.getBackground());
			}

			// Select the current value
			setSelectedItem(value);
			return this;
		}
	}

	private class MyComboBoxEditor extends DefaultCellEditor {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MyComboBoxEditor(String[] items) {
			super(new JComboBox<Object>(items));
		}
	}
}

class DataModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;
	static final PersonTableColumn m_columns[] = { new PersonTableColumn("S #", 20, JLabel.LEFT),
			new PersonTableColumn("Sense", 50, JLabel.LEFT), new PersonTableColumn("Move", 50, JLabel.LEFT) };

	public static final int COUNT = 0;
	public static final int SENSE = 1;
	public static final int MOVE = 2;

	protected SimulationBuilder m_parent;
	protected Vector<RobotCommands> m_vector;

	public DataModel(SimulationBuilder parent) {
		m_parent = parent;
		m_vector = new Vector<RobotCommands>();
	}

	public void insertData(RobotCommands r) {
		m_vector.addElement(new RobotCommands(r.getCount(), r.getSense(), r.getMove()));
	}

	@Override
	public int getRowCount() {
		return m_vector == null ? 0 : m_vector.size();
	}

	@Override
	public int getColumnCount() {
		return m_columns.length;
	}

	@Override
	public String getColumnName(int column) {
		return m_columns[column].m_title;
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return column == SENSE | column == MOVE;
	}

	@Override
	public Object getValueAt(int nRow, int nCol) {
		if (nRow < 0 || nRow >= getRowCount()) {
			return "";
		}
		RobotCommands row = m_vector.elementAt(nRow);
		switch (nCol) {
		case COUNT:
			return row.getCount();
		case SENSE:
			return m_parent.colors[row.getSense()];
		case MOVE:
			return m_parent.motions[row.getMove()];
		}
		return "";
	}

	@Override
	public void setValueAt(Object value, int nRow, int nCol) {
		if (nRow < 0 || nRow >= getRowCount()) {
			return;
		}
		RobotCommands row = m_vector.elementAt(nRow);

		switch (nCol) {
		case COUNT:
			row.setCount(Integer.parseInt(value.toString()));
			break;
		case SENSE:
			row.setSense(value.toString());
			break;
		case MOVE:
			row.setMove(value.toString());
			break;
		}
	}

	public boolean delete(int row) {
		if (row < 0 || row >= m_vector.size()) {
			return false;
		}
		m_vector.remove(row);
		return true;
	}
}

class PersonTableColumn {

	String m_title;
	int m_width;
	int m_alignment;

	public PersonTableColumn(String title, int width, int alignment) {
		m_title = title;
		m_width = width;
		m_alignment = alignment;
	}
}

class RobotCommands {

	private int count = 0;
	private int sense;
	private int move;

	public RobotCommands(int count, int sense, int move) {
		super();
		this.count = count;
		setSense(sense);
		setMove(move);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getSense() {
		return sense;
	}

	public void setSense(int sense) {
		this.sense = sense;
	}

	public void setSense(String sense) {
		for (int i = 0; i < HistogramFilterView.sensorNames.length; i++) {
			if (HistogramFilterView.sensorNames[i].equals(sense.trim())) {
				this.sense = i;
				break;
			}
		}
	}

	public int getMove() {
		return move;
	}

	public void setMove(int move) {
		this.move = move;
	}

	public void setMove(String move) {
		for (int i = 0; i < HistogramFilterView.btnNames.length; i++) {
			if (HistogramFilterView.btnNames[i].equals(move.trim())) {
				this.move = i;
				break;
			}
		}
	}

	@Override
	public String toString() {
		return "PersonInfoDTO [count=" + count + ", sense=" + sense + ", move=" + move + "]";
	}

}