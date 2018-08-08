import java.util.*;

public class Account {
    private int     ID;
    private String  email;
    private String  password;
    private int     type;

    Account(int aID, String aEmail, String aPassword, int aType){
        ID 			= aID;
        email 		= aEmail;
        password    = aPassword;
        type 		= aType;
    }

    Account(int aID, String aEmail, int aType){
        ID 			= aID;
        email 		= aEmail;
        password    = generatePassword();
        type 		= aType;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static String generatePassword(){
        String characters = "aA1bB2cC3dD4eE5fF6gG8hH9iI0jJkKlLmMnNoOpP&qQrRsStT_uUvV-wWxX+yYzZ";
        String password = "";
        for (int i = 0; i < 8; i++) {
            password = password + " ";
        }
        Random random = new Random();
        for (int i = 0; i < password.length(); i++) {
            if (i == 0) {
                password = String.valueOf(characters.charAt(random.nextInt(65))) + password.substring(1, password.length());
            } else {
                password = password.substring(0, i) + String.valueOf(characters.charAt(random.nextInt(65))) + password.substring(i + 1, password.length());
            }
        }
        return password;
    }
}