package bicis;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class Bici {

	private JFrame frame;
	private JTextField textFieldnombre;
	private JTextField textFieldEdad;
	private JTextField textFieldBanco;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Bici window = new Bici();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Bici() {
		initialize();
	}
	class ConnectionSingleton {
		private static Connection con;

		public static Connection getConnection() throws SQLException {
			String url = "jdbc:mysql://127.0.0.1:3307/bicicletas";
			String user = "alumno";
			String password = "alumno";
			if (con == null || con.isClosed()) {
				con = DriverManager.getConnection(url, user, password);
			}
			return con;
		}
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 692, 692);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textFieldnombre = new JTextField();
		textFieldnombre.setBounds(51, 468, 114, 19);
		frame.getContentPane().add(textFieldnombre);
		textFieldnombre.setColumns(10);
		
		textFieldEdad = new JTextField();
		textFieldEdad.setBounds(51, 523, 114, 19);
		frame.getContentPane().add(textFieldEdad);
		textFieldEdad.setColumns(10);
		
		textFieldBanco = new JTextField();
		textFieldBanco.setBounds(51, 578, 114, 19);
		frame.getContentPane().add(textFieldBanco);
		textFieldBanco.setColumns(10);
		
		JLabel lblNombre = new JLabel("Nombre");
		lblNombre.setBounds(61, 443, 70, 15);
		frame.getContentPane().add(lblNombre);
		
		JLabel lblEdad = new JLabel("Edad");
		lblEdad.setBounds(61, 499, 70, 15);
		frame.getContentPane().add(lblEdad);
		
		JLabel lblCuentaBancaria = new JLabel("Cuenta Bancaria");
		lblCuentaBancaria.setBounds(61, 551, 141, 15);
		frame.getContentPane().add(lblCuentaBancaria);
		
		DefaultTableModel model1 = new DefaultTableModel();
		model1.addColumn("ID");
		model1.addColumn("Nombre");
		model1.addColumn("Edad");
		model1.addColumn("Cuenta bancaria");
		
		try {
			Connection con = ConnectionSingleton.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM bicicletas.usuario;");
			while (rs.next()) {
				Object[] row = new Object[4];
				row[0] = rs.getInt("idusuario");
				row[1] = rs.getString("nombre");
				row[2] = rs.getString("edad");
				row[3] = rs.getInt("cuenta_bancaria");
				model1.addRow(row);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		DefaultTableModel model2 = new DefaultTableModel();
		model2.addColumn("Numero de bicicleta");
		model2.addColumn("ID usuario");
		
		try {
			Connection con = ConnectionSingleton.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM bicicleta");
			while (rs.next()) {
				Object[] row = new Object[2];
				row[0] = rs.getInt("idbicicleta");
				row[1] = rs.getInt("idusuario");
				model2.addRow(row);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		JTable table1 = new JTable(model1);
		table1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table1.setBounds(71, 56, 146, 74);

		JScrollPane scrollPane = new JScrollPane(table1);
		scrollPane.setBounds(41, 12, 287, 227);

		frame.getContentPane().add(scrollPane);
		
		table1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				int index = table1.getSelectedRow();
				TableModel model = table1.getModel();
				// ID NAME AGE CITY
				textFieldnombre.setText(model1.getValueAt(index, 1).toString());
				textFieldEdad.setText(model1.getValueAt(index, 2).toString());
				textFieldBanco.setText(model1.getValueAt(index, 3).toString());
			}
		});
		
		JTable table2 = new JTable(model2);
		table2.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table2.setBounds(71, 56, 146, 74);

		JScrollPane scrollPane2 = new JScrollPane(table2);
		scrollPane2.setBounds(382, 12, 287, 227);

		frame.getContentPane().add(scrollPane2);
		
		JButton btnAadirUsuario = new JButton("Añadir usuario");
		btnAadirUsuario.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				try {
					Connection con = ConnectionSingleton.getConnection();
					PreparedStatement pstmt_anadir_usuario = con.prepareStatement("INSERT INTO usuario (nombre, edad, cuenta_bancaria) VALUES(?, ?, ?)");
					pstmt_anadir_usuario.setString(1, textFieldnombre.getText());
					pstmt_anadir_usuario.setInt(2,Integer.parseInt( textFieldEdad.getText()));
					pstmt_anadir_usuario.setInt(3, Integer.parseInt(textFieldBanco.getText()));
					int rowsUpdated = pstmt_anadir_usuario.executeUpdate();
					pstmt_anadir_usuario.close();
					Statement pstmt_mostrar = con.createStatement();
					ResultSet rs_mostrar = pstmt_mostrar.executeQuery("SELECT * FROM usuario");
					model1.setRowCount(0);
					while(rs_mostrar.next()) {
						Object[] row = new Object[4];
						row[0] = rs_mostrar.getString("idusuario");
						row[1] = rs_mostrar.getString("nombre");
						row[2] = rs_mostrar.getInt("edad");
						row[3] = rs_mostrar.getInt("cuenta_bancaria");
						model1.addRow(row);
					}
					con.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
			}
		});
		btnAadirUsuario.setBounds(51, 256, 185, 25);
		frame.getContentPane().add(btnAadirUsuario);
		
		JButton btnAadirBicicleta = new JButton("Añadir bicicleta");
		btnAadirBicicleta.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Connection con = ConnectionSingleton.getConnection();
					PreparedStatement pstmt_anadir_bici = con.prepareStatement("INSERT INTO bicicleta (idbicicleta) values (null)");
					int rowsUpdated = pstmt_anadir_bici.executeUpdate();
					pstmt_anadir_bici.close();
					Statement stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery("SELECT * FROM bicicleta");
					while (rs.next()) {
						Object[] row = new Object[1];
						row[0] = rs.getString("idbicicleta");
						model2.addRow(row);
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});
		btnAadirBicicleta.setBounds(50, 303, 186, 25);
		frame.getContentPane().add(btnAadirBicicleta);
		
		JButton btnAlquilarBicicleta = new JButton("Alquilar bicicleta");
		btnAlquilarBicicleta.setBounds(51, 357, 185, 25);
		frame.getContentPane().add(btnAlquilarBicicleta);
		
		JButton btnDevolverBicicleta = new JButton("Devolver bicicleta");
		btnDevolverBicicleta.setBounds(51, 406, 185, 25);
		frame.getContentPane().add(btnDevolverBicicleta);
		
		
		
		
	}
	}

