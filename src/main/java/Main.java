import GUI.TUI;
import dal.IUserDAO;
import dal.UserDAO;
import dto.UserDTO;

import java.sql.*;

public class Main {

    public static void main(String[] args) {
        try {
            TUI tui = new TUI();
            while (tui.showMenu()) {}
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void readData() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com?"
                + "user=s185119&password=Hbu3DkBjyZLTF9A4jrJu7")){
            connection.setCatalog("s185119");


            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM test");

            System.out.println("Got resultset from database:");
            while (resultSet.next()){
                System.out.println(resultSet.getString(1) + ": " + resultSet.getString(2));
            }
        } catch (SQLException e) {
            //Remember to handle Exceptions gracefully! Connection might be Lost....
            e.printStackTrace();
        }
    }
    private static void insertData(String data) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com?"
                + "user=s185119&password=Hbu3DkBjyZLTF9A4jrJu7")){
            connection.setCatalog("s185119");

            PreparedStatement statement = connection.prepareStatement("INSERT INTO test(indhold) VALUES (?)");
            statement.setString(1, data);
            statement.execute();

            System.out.println("Inserted data into database");
        } catch (SQLException e) {
            //Remember to handle Exceptions gracefully! Connection might be Lost....
            e.printStackTrace();
        }
    }
    private static void deleteData(int Id) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com?"
                + "user=s185119&password=Hbu3DkBjyZLTF9A4jrJu7")){
            connection.setCatalog("s185119");

            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM test WHERE ID=" + Id);

            System.out.println("Deleted data.");
        } catch (SQLException e) {
            //Remember to handle Exceptions gracefully! Connection might be Lost....
            e.printStackTrace();
        }
    }
}