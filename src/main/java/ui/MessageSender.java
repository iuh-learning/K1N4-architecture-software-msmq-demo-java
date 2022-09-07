package ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.WindowEvent;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import utils.ContextForConnect;

public class MessageSender extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lbSend;
	private JTextArea inputMessage;
	private JButton btnSend;
	private Connection con;
	private Session session;
	private MessageProducer producer;

	public MessageSender() throws Exception {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 651, 260);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		lbSend = new JLabel("Send Message");
		lbSend.setForeground(new Color(64, 0, 128));
		lbSend.setHorizontalAlignment(SwingConstants.CENTER);
		lbSend.setFont(new Font("Tahoma", Font.BOLD, 15));
		lbSend.setBackground(new Color(128, 128, 192));
		lbSend.setBounds(0, 0, 631, 45);
		contentPane.add(lbSend);

		inputMessage = new JTextArea();
		inputMessage.setForeground(new Color(128, 128, 128));
		inputMessage.setFont(new Font("Monospaced", Font.PLAIN, 14));
		inputMessage.setBounds(40, 45, 560, 94);
		contentPane.add(inputMessage);

		btnSend = new JButton("Send");
		btnSend.setForeground(new Color(255, 128, 0));
		btnSend.setBackground(new Color(64, 0, 128));
		btnSend.setFont(new Font("Tahoma", Font.BOLD, 13));
		btnSend.setBounds(500, 163, 100, 35);// x,y,w,h
		btnSend.addActionListener((e) -> {
			try {
				String xml = inputMessage.getText().trim();
				Message msg = session.createTextMessage(xml);
				producer.send(msg);
				inputMessage.setText("");
				System.out.println("Sent...");
			} catch (JMSException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		contentPane.add(btnSend);

		// closing form
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					// shutdown connection
					session.close();
					con.close();
					System.out.println("Sender disconnect");
					System.exit(0);
				} catch (JMSException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		// handle send message by jms
		configSender();
	}

	public void configSender() throws Exception {
		// create context
		Context ctx = ContextForConnect.getContext();

		// lookup JMS connection factory
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");

		// lookup destination. (If not exist-->ActiveMQ create once)
		Destination destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");

		// get connection using credential
		con = factory.createConnection("admin", "admin");

		// connect to MOM
		con.start();

		// create session
		session = con.createSession(/* transaction */false, /* ACK */Session.AUTO_ACKNOWLEDGE);

		// create producer
		producer = session.createProducer(destination);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				new MessageSender().setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
