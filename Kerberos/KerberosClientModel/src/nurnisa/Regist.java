package nurnisa;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.util.Objects;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;


public class Regist extends JFrame implements ActionListener{
	
	@Serial
	private static final long serialVersionUID = 1L;
	private  JTextField jt = new JTextField("�����û���");		//�������г�ʼ���ı����ı������
	private  JPasswordField jp1=new JPasswordField(20);
	private  JPasswordField jp2=new JPasswordField(20);

	public Regist(){
		this.setResizable(false); 		//�����޸Ĵ�С
		this.getContentPane().setLayout(null);
		this.setTitle("ע��");
		this.setSize(450,350);
		
		//��������λ�ã��ǶԻ������
				Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
				this.setLocation((int)(screenSize.width-350)/2,
						(int)(screenSize.height-600)/2+45);

		JLabel back = new JLabel();
		ImageIcon icon=new ImageIcon(Objects.requireNonNull(this.getClass().getResource("ע��.jpg")));
		back.setIcon(icon);
		back.setBounds(0, -20, 450, 350);

		JLabel usr = new JLabel();
		usr.setBounds(175, 40, 80, 50);
		usr.setFont(new Font("����",Font.PLAIN,14));
		usr.setForeground(Color.BLACK);
		usr.setText("�û���:");		
		jt.setForeground(Color.gray);
		jt.setBounds(230, 50, 150, 30);
		jt.setFont(new Font("Serif",Font.PLAIN,12));

		JLabel pwd = new JLabel();
		pwd.setBounds(175, 85, 80, 50);
		pwd.setFont(new Font("����",Font.PLAIN,14));
		pwd.setForeground(Color.BLACK);
		pwd.setText("��  � ");		
		//���������
		jp1.setFont(new Font("Serif",Font.PLAIN,12));
		jp1.setBounds(230, 95, 150, 30);
		jp1.setVisible(true);

		JLabel jl = new JLabel();
		jl.setBounds(165, 130, 80, 60);
		jl.setFont(new Font("����",Font.PLAIN,14));
		jl.setForeground(Color.BLACK);
		jl.setText("����ȷ�ϣ� ");		
		jp2.setFont(new Font("Serif",Font.PLAIN,12));
		jp2.setBounds(230, 140, 150, 30);
		jp2.setVisible(true);


		JButton x = new JButton();
		x.setText("����ע��");
		x.setFont(new Font("Dialog", Font.PLAIN,12));
		x.setBounds(260,200, 90, 30);
		x.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		x.setBackground(getBackground());
		x.setBackground(Color.white);
		Border b = new LineBorder(Color.white, 2); 
		x.setBorder(b);
		x.setVisible(true);
		
		x.addActionListener(this);
		this.getContentPane().add(jt);
		this.getContentPane().add(usr);
		this.getContentPane().add(pwd);
		this.getContentPane().add(jl);
		this.getContentPane().add(jp1);	
		this.getContentPane().add(jp2);	
		this.getContentPane().add(x);

		this.getContentPane().add(back);
		this.setVisible(true);
	}
	
	

	@Override
	public void actionPerformed(ActionEvent e) {					
		String usr=jt.getText().toString();	//��ȡ�ı�������					
		String password1 =String.valueOf(jp1.getPassword());	//��ȡ���������			
		String password2 =String.valueOf(jp2.getPassword());	//��ȡ���������					
		String Content=usr+password1+password2;
					
		if(usr.equals("")||password1.equals("")||password2.equals("")){
			//System.out.println("������������Ϣ!");
			JOptionPane.showMessageDialog(null, "������������Ϣ!");
			jp1.setText(null);
	        jp2.setText(null);
			}
		else if(password1.equals(password2)){
			//�˴��Ƿ����������߼�
				
			
			JOptionPane.showMessageDialog(null, "ע��ɹ�");
			setVisible(false);			
			}	
		else{
			 JOptionPane.showMessageDialog(null, "������������벻һ�£����������룡");
	         jp1.setText(null);
	         jp2.setText(null);
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Regist();
	}
}
