package ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.util.Date;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import utils.ContextForConnect;

public class MessageReceiver extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblReceiveMessage;
	private JTextArea txaMessages;
	private Date date;
	private Connection con;
	private Session session;

	public MessageReceiver() throws Exception {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 692, 365);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		lblReceiveMessage = new JLabel("Receive Message");
		lblReceiveMessage.setHorizontalAlignment(SwingConstants.CENTER);
		lblReceiveMessage.setForeground(new Color(64, 0, 128));
		lblReceiveMessage.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblReceiveMessage.setBackground(new Color(128, 128, 192));
		lblReceiveMessage.setBounds(10, 0, 631, 45);
		contentPane.add(lblReceiveMessage);

		txaMessages = new JTextArea();
		txaMessages.setForeground(Color.GRAY);
		txaMessages.setFont(new Font("Monospaced", Font.PLAIN, 14));
		txaMessages.setBounds(50, 45, 577, 243);
		txaMessages.setEditable(false);
		contentPane.add(txaMessages);
		
		//handle receive messages
		configReceiver();
		
		//closing form
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					session.close();
					con.close();
					System.out.println("Reciver disconnect!");
				} catch (JMSException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
	}
	
	private void configReceiver() throws Exception{
		//create context
		Context ctx = ContextForConnect.getContext();
		
		// lookup JMS connection factory
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		
		// lookup destination
		Destination destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");
		
		// create connection
		con = factory.createConnection("admin", "admin");
		
		//connect to MOM
		con.start();
		
		//create session
		session = con.createSession(/* transaction */false, /* ACK */Session.CLIENT_ACKNOWLEDGE);
		
		//create consumer
		MessageConsumer receiver = session.createConsumer(destination);
		
		// blocked-method for receiving message - sync
		// receiver.receive();
		// receiver listen on queue, if queue has message then notify - async
		System.out.println("Receiver start!");
		receiver.setMessageListener(new MessageListener() {

			//message into queue, method below will run
			public void onMessage(Message msg) {
				try {
					if (msg instanceof TextMessage) {
						TextMessage tm = (TextMessage) msg;
						String txt = tm.getText();
						date = new Date();
						date.getTime();
						txaMessages.append(txt + "\t" + date + "\n");

						msg.acknowledge();// send signal acknowledge
					} else if (msg instanceof ObjectMessage) {
						ObjectMessage om = (ObjectMessage) msg;
						System.out.println(om);
					}
					// others message type....
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				new MessageReceiver().setVisible(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
}
