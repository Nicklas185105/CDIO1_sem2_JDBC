package dal;

import dto.UserDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO, AutoCloseable {
    Connection connection;

    public UserDAO(String host, String username, String password) throws SQLException {
        connection = DriverManager.getConnection(String.format("jdbc:mysql://%s?user=%s&password=%s", host, username, password));
        connection.setCatalog("s185119");
    }

    @Override
    public UserDTO getUser(int userId) throws DALException {
        UserDTO userDTO = null;
        try {
            ResultSet userDao = connection.createStatement().executeQuery("SELECT * FROM UserDAO WHERE ID=" + userId + " LIMIT 1");
            ResultSet userRoles = connection.createStatement().executeQuery("SELECT * FROM UserRoles WHERE belongsTo=" + userId);

            if (userDao.next()) {
                userDTO = getUser(userDao, userRoles);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return userDTO;
    }

    @Override
    public List<UserDTO> getUserList() throws DALException {
        List<UserDTO> users = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet userDao = statement.executeQuery("SELECT * FROM UserDAO");
            while (userDao.next()) {
                int uid = userDao.getInt(1);
                ResultSet userRoles = connection.createStatement().executeQuery("SELECT * FROM UserRoles WHERE belongsTo=" + uid);

                users.add(getUser(userDao, userRoles));
            }
        } catch (SQLException e){
            throw new DALException(e.getMessage());
        }

        return users;
    }

    @Override
    public void createUser(UserDTO user) throws DALException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO UserDAO(userName, ini, cpr, pass) VALUES (?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getIni());
            preparedStatement.setString(3, user.getCpr());
            preparedStatement.setString(4, user.getPassword());

            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            if(rs.next()) {
                int last_inserted_id = rs.getInt(1);

                updateUserRoles(last_inserted_id, user);
            }
        } catch (SQLException sqlException) {
            throw new DALException(sqlException.getMessage());
        }
    }

    @Override
    public void updateUser(UserDTO user) throws DALException {
        int uid = user.getUserId();

        try {
            PreparedStatement preps = connection.prepareStatement("UPDATE UserDAO SET userName=?, ini=?, cpr=?, pass=? WHERE ID=" + uid, Statement.RETURN_GENERATED_KEYS);
            preps.setString(1, user.getUserName());
            preps.setString(2, user.getIni());
            preps.setString(3, user.getCpr());
            preps.setString(4, user.getPassword());

            preps.executeUpdate();

            connection.createStatement().execute("DELETE FROM UserRoles WHERE belongsTo=" + uid);
            updateUserRoles(uid, user);
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    @Override
    public void deleteUser(int userId) throws DALException {
        try {
            connection.createStatement().executeQuery("DELETE FROM UserDAO WHERE ID=" + userId + " LIMIT 1");
            connection.createStatement().executeQuery("DELETE FROM UserRoles WHERE belongsTo=" + userId);
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateUserRoles(int id, UserDTO user) throws SQLException {
        for (String role : user.getRoles()) {
            PreparedStatement newRole = connection.prepareStatement("INSERT INTO UserRoles (belongsTo, userRole) VALUES (?, ?)");
            newRole.setInt(1, id);
            newRole.setString(2, role);
            newRole.execute();
        }
    }

    private UserDTO getUser(ResultSet userDAOres, ResultSet userRoleres) {
        /* [userDAOres]
            userName TEXT,
            ini TEXT,
            cpr TEXT,
            pass TEXT,
        */
        /* [userRoleres]
            belongsTo INT,
            userRole TEXT,
        */
        try {
            UserDTO userDTO = new UserDTO();
            while (userRoleres.next()){
                userDTO.addRole(userRoleres.getString(3));
            }

            userDTO.setUserId(userDAOres.getInt(1));
            userDTO.setUserName(userDAOres.getString(2));
            userDTO.setIni(userDAOres.getString(3));
            userDTO.setCpr(userDAOres.getString(4));
            userDTO.setPassword(userDAOres.getString(5));

            return userDTO;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
