package view;

import controlador.Servicios;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textUsuario;
	private JTextField textContraseña;
	private Servicios servicios = new Servicios();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
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
	public Login() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 736, 424);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblTitulua = new JLabel("ElorES");
		lblTitulua.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitulua.setFont(new Font("Tahoma", Font.PLAIN, 22));
		lblTitulua.setBounds(210, 73, 300, 27);
		contentPane.add(lblTitulua);
		
		textUsuario = new JTextField();
		textUsuario.setBounds(339, 125, 171, 20);
		contentPane.add(textUsuario);
		textUsuario.setColumns(10);
		
		JLabel lblUsuario = new JLabel("Usuario:");
		lblUsuario.setBounds(230, 128, 86, 14);
		contentPane.add(lblUsuario);
		
		JLabel lblContrasea = new JLabel("Contraseña:");
		lblContrasea.setBounds(230, 167, 86, 14);
		contentPane.add(lblContrasea);
		
		textContraseña = new JTextField();
		textContraseña.setColumns(10);
		textContraseña.setBounds(339, 164, 171, 20);
		contentPane.add(textContraseña);
		
		JButton btnLogin = new JButton("Log in");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Logica de autenticacion
				if(servicios.servicioLogin(textUsuario.getText(),textContraseña.getText())) {
					JOptionPane.showMessageDialog(null, "Login exitoso");
					view.Menu menuFrame = new view.Menu();
					menuFrame.setVisible(true);
					dispose();
				}else {
					JOptionPane.showMessageDialog(null, "Login fallido, usuario o contraseña incorrectos");
				}
			}
		});
		btnLogin.setBounds(294, 215, 132, 32);
		contentPane.add(btnLogin);

	}
}
