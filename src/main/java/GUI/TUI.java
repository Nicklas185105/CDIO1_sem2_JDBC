package GUI;

import dal.IUserDAO;
import dal.UserDAO;
import dto.UserDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TUI {
    private UserDAO dao;
    private Scanner input;

    public TUI() throws SQLException {
        input = new Scanner(System.in);
        dao = new UserDAO("ec2-52-30-211-3.eu-west-1.compute.amazonaws.com", "s185119", "Hbu3DkBjyZLTF9A4jrJu7");
    }

    public boolean showMenu() {
        System.out.println("------------------------------");
        System.out.println("1. Opret ny bruger");
        System.out.println("2. List Brugere");
        System.out.println("3. Ret bruger");
        System.out.println("4. Slet bruger");
        System.out.println("5. Afslut program");
        System.out.println("------------------------------");

        int option = 0;
        try {
            option = Integer.parseInt(input.nextLine());
        } catch (NumberFormatException nfe) {
            System.out.println("Ukendt mulighed! Prøv igen.");
        }

        switch (option) {
            case 1: createUser(); break;
            case 2: listUsers(); break;
            case 3: updateUser(); break;
            case 4: deleteUser(); break;
            case 5: return false;
            default: break;
        }

        return true;
    }

    private void createUser() {
        UserDTO user = new UserDTO();

        System.out.println("Skriv username: ");
        user.setUserName(input.nextLine());

        System.out.println("Skriv password: ");
        user.setPassword(input.nextLine());

        System.out.println("Skriv initialer (ini): ");
        user.setIni(input.nextLine());

        System.out.println("Skriv cpr: ");
        user.setCpr(input.nextLine());

        System.out.println("Skriv roller (mellemrum sepererer rolle)");
        String[] roles = input.nextLine().split(" ");

        for (String role : roles) {
            user.addRole(role);
        }

        try {
            dao.createUser(user);
        } catch (IUserDAO.DALException e) {
            e.printStackTrace();
        }
    }
    private void listUsers() {
        try {

            List<UserDTO> dto = dao.getUserList();
            System.out.printf("Liste af brugere (%d i total): \n", dto.size());

            for (int i = 0; i < dto.size(); i++) {
                System.out.printf("%d. %s\n", i+1, dto.get(i));
            }

        } catch (IUserDAO.DALException e) {
            e.printStackTrace();
        }
    }

    private void updateUser() {
        try {
            System.out.println("Skriv bruger id: ");
            int uid = Integer.parseInt(input.nextLine());

            UserDTO user = dao.getUser(uid);

            System.out.println("Hvad ønsker du at opdaterer? ");
            System.out.println("1. Brugernavn");
            System.out.println("2. Password");
            System.out.println("3. CPR");
            System.out.println("4. Initialer");
            System.out.println("5. Rollér");

            int option = Integer.parseInt(input.nextLine());

            switch (option) {
                case 1:
                    System.out.println("Skriv nyt brugernavn: ");
                    user.setUserName(input.nextLine());
                    break;
                case 2:
                    System.out.println("Skriv nyt password: ");
                    user.setPassword(input.nextLine());
                    break;
                case 3:
                    System.out.println("Skriv nyt CPR nr: ");
                    user.setCpr(input.nextLine());
                    break;
                case 4:
                    System.out.println("Skriv nye initialer: ");
                    user.setIni(input.nextLine());
                    break;
                case 5:
                    System.out.println("Skriv nye rollér (mellemrum seperer roller)");
                    user.setRoles(Arrays.asList(input.nextLine().split(" ")));
                    break;
                default: break;
            }

            dao.updateUser(user);

        } catch (NumberFormatException nfe) {
            System.out.println("Ukendt mulighed!");
        } catch (IUserDAO.DALException de) {
            de.printStackTrace();
        }
    }
    private void deleteUser() {
        try {
            System.out.println("Skriv bruger id: ");
            int uid = Integer.parseInt(input.nextLine());
            dao.deleteUser(uid);
        } catch (NumberFormatException nfe) {
            System.out.println("Ugyldigt bruger id!");
        } catch (IUserDAO.DALException de) {
            de.printStackTrace();
        }
    }
}
