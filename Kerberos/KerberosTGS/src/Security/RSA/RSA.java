package Security.RSA;

import java.math.BigInteger;

import net.sf.json.JSONObject;
import net.sf.json.util.NewBeanInstanceStrategy;

public class RSA {
	/**
	 * TGS服务器的公钥
	 */
	public static String TGSPublicKey = "65537&12212690687876111778631914058053907976672300451113723158078853698843000468416739308414181522225554504542219939793375968679427188159730219293000830899772769066746380191645968656774232302544015186785263475832891868277798832771100729783896725712096811259212894243591440731016513297774084827386817208464370668118510828455653669260208409066084619658228019359651857026389909006539430417151844000764455572849945097089283151421428135744852391646439525779749842257922838275014988726831619189559790172218242953019246646473673072924757195240042233223846998606331043164626716215112204416985916658702487045605969466575568761671927";
	
	/**
	 * 聊天应用服务器的公钥
	 */
	public static String ChatServerPublicKey = "65537&24939412851792017341433983041853385544439316891084434582068824517997516912997756104244445946338318587035793654527250842689287586037725783325035506738918998572208619541146401910294900050070690254366506474231994935174581127480000859539890011078293388464297959682561142964607014423152587470955973889962228142082872111530342973041750666450651149645148894221551259287587540100801244160277084159093706446139061973799178505817972182958748332709022690079998427857362092625678433711746599108438156823702493991805026529354100045300968836980819234474531351499937273165363552991339465793345974870738216141754799426493447310154867";
	
	
	
	/**
	 * RSA加密一组byte
	 * @param Key 加密数据时用公钥，做签名时用私钥
	 * @param src 
	 * @return 
	 */
	public static byte[] encrypt(String Key, byte[] src){
		
		BigInteger a = new BigInteger(KeyManger.getKeyMain(Key));
		BigInteger n = new BigInteger(KeyManger.getKeyN(Key));
		
		BigInteger m = new BigInteger(src);
		BigInteger c = m.modPow(a, n);
		
        return c.toByteArray();
	}
	
	/**
	 * RSA解密一组byte
	 * @param Key 解密数据时用私钥，认证签名时用公钥
	 * @param src 
	 * @return 
	 */
	public static byte[] decrypt(String Key, byte[] src){
		
		StringBuffer buffer = new StringBuffer();
		BigInteger a = new BigInteger(KeyManger.getKeyMain(Key));
		BigInteger n = new BigInteger(KeyManger.getKeyN(Key));
		
		BigInteger c = new BigInteger(src);
		BigInteger m = c.modPow(a, n);
		
		byte[] tarBytes = m.toByteArray();
		
		for(int i=0;i<tarBytes.length;i++){
			buffer.append((char)tarBytes[i]);
        }
        return buffer.toString().getBytes();
	}
	
	public static void main(String[] argv){
		JSONObject ticketTgs = new JSONObject();
		ticketTgs.put("keyCAndTgs", "2346");
		ticketTgs.put("IDC", 123);
		ticketTgs.put("ADC", "9876");
		ticketTgs.put("IDTgs", 2);
		ticketTgs.put("timeStamp", "abcde");
		ticketTgs.put("lifeTime", 1234);

		String TGSPrivateKey = "8557101429532493902296069297432525966839984080979327210872956678683348055444964967001529143851526051674302144365958534595103475598437701907847447318576766643956753870338631318477695764725592831182069652413848583110556211008269306066443957530852580573158003931606862663354720091456520366718077516710925142743668247386421510669808588015590335943907223630577185479712024698807041111648656999415659482009438403276728722609217371949987026479788277879123846309104089082329707504277965397334028538418284539552314085362446506371015899741229715542022265559066873543511768304091636770969870525365939612341502515608477750495233&12212690687876111778631914058053907976672300451113723158078853698843000468416739308414181522225554504542219939793375968679427188159730219293000830899772769066746380191645968656774232302544015186785263475832891868277798832771100729783896725712096811259212894243591440731016513297774084827386817208464370668118510828455653669260208409066084619658228019359651857026389909006539430417151844000764455572849945097089283151421428135744852391646439525779749842257922838275014988726831619189559790172218242953019246646473673072924757195240042233223846998606331043164626716215112204416985916658702487045605969466575568761671927";
       byte temp[] = RSA.encrypt(RSA.TGSPublicKey, ticketTgs.toString().getBytes());
		System.out.println(new String(temp));
		System.out.println(new String(RSA.decrypt(TGSPrivateKey, temp)));
	}
}
