package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import modelo.Users;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Menu extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Menu frame = new Menu();
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
	public Menu() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 736, 424);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnNewButton = new JButton("Consultar alumnos");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.Alumnos frame = new view.Alumnos();
				frame.setVisible(true);
				dispose();
			}
		});
		btnNewButton.setBounds(157, 98, 131, 45);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Consultar horario");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.ViewHorario frame = new view.ViewHorario();
				frame.setVisible(true);
				dispose();
			}
		});
		btnNewButton_1.setBounds(425, 98, 131, 45);
		contentPane.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("Consultar otros horarios");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.OtrosHorarios frame = new view.OtrosHorarios();
				frame.setVisible(true);
				dispose();
			}
		});
		btnNewButton_2.setBounds(157, 241, 131, 45);
		contentPane.add(btnNewButton_2);
		
		JButton btnNewButton_3 = new JButton("Perfil");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.Perfil frame = new view.Perfil();
				frame.setVisible(true);
				dispose();
			}
		});
		btnNewButton_3.setBounds(608, 11, 102, 31);
		contentPane.add(btnNewButton_3);
		
		JButton btnNewButton_2_1 = new JButton("Gestionar reuniones");
		btnNewButton_2_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.Reuniones frame = new view.Reuniones();
				frame.setVisible(true);
				dispose();
			}
		});
		btnNewButton_2_1.setBounds(425, 241, 131, 45);
		contentPane.add(btnNewButton_2_1);
		
		JButton btnNewButton_3_1 = new JButton("Salir");
		btnNewButton_3_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnNewButton_3_1.setBounds(10, 15, 102, 31);
		contentPane.add(btnNewButton_3_1);

	}

}
