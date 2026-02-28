package DAL.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/phone_shop?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("Không tìm thấy MySQL Driver!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối database!");
            e.printStackTrace();
        }
        return null;
    }
 
}

