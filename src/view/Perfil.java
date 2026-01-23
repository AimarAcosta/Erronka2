package view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;

import modelo.Users;

public class Perfil extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Users usuario = controlador.Servicios.getLoggedUser();
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Perfil frame = new Perfil();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Perfil() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 736, 424);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnSalir = new JButton("Salir");
		btnSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.Menu frame = new view.Menu();
				frame.setVisible(true);
				dispose();
			}
		});
		
		
		contentPane.setLayout(null);
		btnSalir.setBounds(10, 15, 102, 31);
		contentPane.add(btnSalir);
		
		JLabel lblNombre = new JLabel("NOMBRE");
		lblNombre.setFont(new Font("Tahoma", Font.PLAIN, 23));
		lblNombre.setBounds(20, 67, 658, 37);
		contentPane.add(lblNombre);
		
		JLabel lblDNI = new JLabel("DNI");
		lblDNI.setFont(new Font("Tahoma", Font.PLAIN, 23));
		lblDNI.setBounds(20, 131, 288, 37);
		contentPane.add(lblDNI);
		
		JLabel lblDireccion = new JLabel("DIRECCION");
		lblDireccion.setFont(new Font("Tahoma", Font.PLAIN, 23));
		lblDireccion.setBounds(20, 186, 342, 80);
		contentPane.add(lblDireccion);
		
		JLabel lblTelefono = new JLabel("TELEFONO");
		lblTelefono.setFont(new Font("Tahoma", Font.PLAIN, 23));
		lblTelefono.setBounds(20, 295, 288, 37);
		contentPane.add(lblTelefono);
		
		lblNombre.setText("Nombre y apellido: " + usuario.getNombre() + " " + usuario.getApellidos());
		lblDNI.setText("DNI: " + usuario.getDni());
		lblDireccion.setText("Dirección: " + usuario.getDireccion());
		lblTelefono.setText("Teléfono: " + usuario.getTelefono1());
	}
}
