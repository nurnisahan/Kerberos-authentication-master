package DBManger;

import java.sql.*;

public class DataConn {

	static Statement stmt = null;
	static ResultSet rs = null;
	static Connection conn = null;
	static String ip = "localhost:3306/test";

	static void OpenConn() {
		stmt = null;
		rs = null;
		try {
			//Class.forName("Connection");
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://" + ip + "?user=root&password=1999329";
			conn = DriverManager.getConnection(url);
			// System.out.println("���ݿ����ӳɹ�");
		} catch (Exception e) {
			System.err.println("���ݿ�����ʧ�� " + e.getMessage());
		}
	}

	public static ResultSet executeQuery(String sql) {
		stmt = null;
		rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			System.err.println("��ѯ����" + e.getMessage());
		}
		return rs;
	}

	public static int executeUpdate(String sql) {
		stmt = null;
		rs = null;
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.executeUpdate(sql);
			// conn.commit();
		} catch (SQLException e) {
			System.err.println("��������" + e.getMessage());
			return 0;
		}
		return 1;
	}

	static void CloseConn() {
		try {
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			System.err.println("�����ͷ�");
		}
	}
}