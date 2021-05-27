package Client;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import Message.Message;
import Security.DES.Des;
import Security.MD5.MD5;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

public class ClientOperation {
	static byte[] sendBuffer = new byte[8216];
	static byte[] receiveBuffer = new byte[8216];
	static DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	private final static long MAXTIME=100000; 
	
	// Դ
	private final static long AS = 1;// AS������
	private final static long TGS = 2;// TGS������
	private final static long PSERVERCHAT = 3;// Ӧ�÷�����(��Ϣ)
	private final static long PSERVERFILE = 4;// Ӧ�÷�����(�ļ�)
	
	 //����respond
	 private final static byte NEGATIVE = 0;//���ֶ�δ������
	 private final static byte SUCCESS = 1;//�����ɹ�
	 private final static byte DENY = 2;//�ܾ�����
	 private final static byte FAILTOUPLOAD = 3;//�ļ��ϴ�ʧ��
	 private final static byte FAILTODOWNLOAD = 4;//�ļ�����ʧ��
	 private final static byte USEROFFLINE = 5;//��ϢͶ�ݳɹ������û���������״̬
	 private final static byte OVERTIME = 6;//��Ϣ��ʱ
	 
	 private final static byte CONNSUCCESS = 7;//��֤�ɹ�
	 private final static byte CONNFAIL = 8;//��֤ʧ��
	 
	 public static String keyCT;//client��Tgs�ĻỰԿ
	
	//client��װ���ģ������͸�AS
	public static byte[] clientToAS(long IDC,long IDTgs){

		JSONObject clientToAs = new JSONObject();
		clientToAs.put("IDC", IDC);
		clientToAs.put("IDTgs", IDTgs);
		Date time =new Date();
		String timeString=format.format(time);
		clientToAs.put("timeStamp", timeString);
		
		Message.setContent(sendBuffer, clientToAs.toString().getBytes());
		Message.setTargetID(sendBuffer, AS);
		Message.setSourceID(sendBuffer, IDC);
		Message.setType(sendBuffer, (byte)0);
		Message.setMethod(sendBuffer, (byte)0);
		Message.setRespond(sendBuffer, (byte)0);
		
		ConnManger cm = new ConnManger("as");
		SocketConn conn = cm.getConn();
		conn.send(sendBuffer);
		conn.receive(receiveBuffer);
		try {
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return receiveBuffer;
	}
	
	//client����AS�յ��ı��Ľ��н���,���ɹ�����Tgs���ͱ���
	public static byte[] clientToTgs(long IDC, String pwd, long IDPServer,byte receiveBuffer[]) throws IOException, Exception{
		//�����ݿ��л�ȡIDC��passwd������MD5�õ�����Կ��
		String passwdOfClient= pwd;
		//����MD5�㷨����keyClient,������ܺ���client��passwd��Ӧ��MD5�⿪
		String keyClient = MD5.getStringMD5(passwdOfClient);;//��keyClient��ASToClient����
		
		ConnManger cm = new ConnManger("tgs");
		SocketConn conn = cm.getConn();
		Date receiveTime=new Date();
		byte respond =Message.getRespond(receiveBuffer);
		byte type =Message.getType(receiveBuffer);
		byte method = Message.getMethod(receiveBuffer);// ������method
		
		//�ж�respond�Ļ�Ӧ
		if (respond!=(byte)1) {
			byte[] erro={(byte)respond};
			return erro;
		}
		
		byte[] contentEncipher = Message.getContent(receiveBuffer);
		//��keyClient��content����
		Des.setKey(keyClient);
		byte[] content=Des.decrypt(contentEncipher);
		
		// ����content�е�json
		String json = new String(content);
		JSONTokener jsonTokener = new JSONTokener(json);
		JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();// ��json����ת��ΪjsonObject����
		String keyCAndTgs = jsonObject.getString("keyCAndTgs");
		keyCT=keyCAndTgs;
		long IDTgs = jsonObject.getLong("IDTgs");
		String timeStamp = jsonObject.getString("timeStamp");
		long lifeTime = jsonObject.getLong("lifeTime");
		String ticketTgsEncipher = jsonObject.getString("ticketTgsEncipher");
		
		//�ж��Ƿ�ʱ
		try {
			Date timeStampDate = format.parse(timeStamp);
			long s = receiveTime.getTime() - timeStampDate.getTime();
			if (s>lifeTime) {
				Message.setRespond(sendBuffer, OVERTIME);
				Message.setTargetID(sendBuffer, IDC);
				Message.setSourceID(sendBuffer, AS);
				conn.send(sendBuffer);
				byte[] erro={(byte)OVERTIME};
				return erro;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//��AuthenticatorC��װ��json
		JSONObject AuthenticatorC=new JSONObject();
		AuthenticatorC.put("IDC", IDC);
		String ADC="MACC";//C��mac��ַ
		AuthenticatorC.put("ADC", ADC);
		Date sendTime=new Date();
		String sendTimeString=format.format(sendTime);//ʱ���
		AuthenticatorC.put("timeStamp", sendTimeString);

		//��keyCAndTgs��AuthenticatorC���ܣ�DES�����õ�String����AuthenticatorEncipher
		Des.setKey(keyCAndTgs);
		byte[] temp = Des.encrypt(AuthenticatorC.toString().getBytes());
		String AuthenticatorEncipher = new BASE64Encoder().encodeBuffer(temp);
		
		//��Client���͸�tgs��content��װ��json
		JSONObject ClientToTgs = new JSONObject();
		ClientToTgs.put("IDPServer", IDPServer);
		ClientToTgs.put("ticketTgsEncipher", ticketTgsEncipher);
		ClientToTgs.put("AuthenticatorEncipher", AuthenticatorEncipher);
		
		// �����ݷ�װ�ڱ�����
		Message.setContent(sendBuffer, ClientToTgs.toString().getBytes());
		//���÷���
		Message.setRespond(sendBuffer, SUCCESS);
		Message.setTargetID(sendBuffer, TGS);
		Message.setSourceID(sendBuffer, IDC);
		Message.setType(sendBuffer, type);
		Message.setMethod(sendBuffer, method);
		
		conn.send(sendBuffer);
		conn.receive(receiveBuffer);
		try {
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return receiveBuffer;	
	}
	
	//client����Tgs�յ��ı��Ľ��н���,���ɹ�����PServer���ͱ���
	public static byte clientToPSever(long IDC,byte receiveBuffer[]) throws Exception{		
		Date receiveTime=new Date();
		byte respond =Message.getRespond(receiveBuffer);
		byte type =Message.getType(receiveBuffer);
		byte method = Message.getMethod(receiveBuffer);// ������method
		
		//�ж�respond�Ļ�Ӧ
		if (respond!=(byte)1) {
			return respond;
		}
		
		byte[] temp = Message.getContent(receiveBuffer);
		//��client��Tgs�ĻỰԿ��content����
		Des.setKey(keyCT);
		byte[]temp2= Des.decrypt(temp);
		String content=new String(temp2);
		
		// ����content�е�json
		String json = new String(content);
		JSONTokener jsonTokener = new JSONTokener(json);
		JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();// ��json����ת��ΪjsonObject����
//		String keyCAndPServer = jsonObject.getString("keyCAndPServer");
		long IDPServer = jsonObject.getLong("IDPServer");
		String timeStamp = jsonObject.getString("timeStamp");
		String ticketPServerEncipher = jsonObject.getString("ticketPServerEncipher");
		
		SocketConn conn;
		if (IDPServer == PSERVERCHAT) {
			ConnManger cm = new ConnManger("CHATSERVER");
			conn = cm.getConn();
		} else {
			ConnManger cm = new ConnManger("FILESERVER");
			conn = cm.getConn();
		}
		
		//�ж��Ƿ�ʱ
		try {
			Date timeStampDate = format.parse(timeStamp);
			long s = receiveTime.getTime() - timeStampDate.getTime();
			if (s>MAXTIME) {
				Message.setRespond(sendBuffer, OVERTIME);
				Message.setTargetID(sendBuffer, TGS);
				Message.setSourceID(sendBuffer, IDC);
				conn.send(sendBuffer);
				return OVERTIME;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//��AuthenticatorC��װ��json
		JSONObject AuthenticatorC=new JSONObject();
		AuthenticatorC.put("IDC", IDC);
		String ADC="MACC";//C��mac��ַ
		AuthenticatorC.put("ADC", ADC);
		Date sendTime=new Date();
		AuthenticatorC.put("timeStamp", sendTime);
		
		//����client��PServer�ĻỰԿ
		String str =  "asdhfafsfjhfaksfkooiij";
		//���ɶ�����keyCAndTgs
		String keyCAndPServer=MD5.getStringMD5(str);
		
		//��client��PServer�ĻỰԿ��AuthenticatorC���м���
		Des.setKey(keyCAndPServer);
		byte[] temp1=Des.encrypt(AuthenticatorC.toString().getBytes());
		String AuthenticatorCEncipher = new BASE64Encoder().encodeBuffer(temp1);
		
		//��Client���͸�PServer��content��װ��json
		JSONObject ClientToPServer = new JSONObject();
		ClientToPServer.put("ticketPServerEncipher", ticketPServerEncipher);
		ClientToPServer.put("AuthenticatorCEncipher", AuthenticatorCEncipher);
		
		// �����ݷ�װ�ڱ�����
		Message.setContent(sendBuffer, ClientToPServer.toString().getBytes());
		//���÷���
		Message.setRespond(sendBuffer, SUCCESS);
		Message.setTargetID(sendBuffer, IDPServer);
		Message.setSourceID(sendBuffer, IDC);
		Message.setType(sendBuffer, type);
		Message.setMethod(sendBuffer, method);
		
		conn.send(sendBuffer);
		
		conn.receive(receiveBuffer);
		
		
		byte[] contentPSEncipher = Message.getContent(receiveBuffer);
		//��client��PServer�ĻỰԿ��contentPS����
		Des.setKey(keyCAndPServer);
		byte[] contentPS=Des.decrypt(contentPSEncipher);
		// ����content�е�json
		String json1 = new String(contentPS);
		JSONTokener jsonTokener1 = new JSONTokener(json1);
		JSONObject jsonObject2 = (JSONObject) jsonTokener1.nextValue();// ��json����ת��ΪjsonObject����
		String timeStampFromPS = jsonObject2.getString("timeStamp");

		if (timeStampFromPS.equals(sendTime.toString()+1)) {
			return CONNFAIL;
		}
		return CONNSUCCESS;
	}

	private static class BASE64Encoder {

		public String encodeBuffer(byte[] temp) {
			return null;
		}}

}

