package dal;
import java.util.List;

import dto.UserDTO;

public interface IUserDAO {
    UserDTO getUser(int userId) throws DALException;
    List<UserDTO> getUserList() throws DALException;
    void createUser(UserDTO user) throws DALException;
    void updateUser(UserDTO user) throws DALException;
    void deleteUser(int userId) throws DALException;

    public class DALException extends Exception {
        public DALException(String msg, Throwable e) {
            super(msg,e);
        }

        public DALException(String msg) {
            super(msg);
        }

    }

}
