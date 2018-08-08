import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.time.*;

/**
 * @author Stephen Cliffe   17237157
 * @author Daniel Dalton    17219477
 * @author Alan Finnin      17239621
 * @author William Cummins  17234956
 */

public class Main {
    private static ArrayList<Facility> facilities = new ArrayList<Facility>();
    private static ArrayList<Account> accounts = new ArrayList<Account>();
    private static ArrayList<Booking> bookings = new ArrayList<Booking>();
    private static Account currentUser;
    private static String[] timeSlots = {"09:00-10:00", "10:00-11:00", "11:00-12:00", "12:00-13:00", "13:00-14:00", "14:00-15:00", "15:00-16:00", "16:00-17:00", "17:00-18:00"};

    /**
     * The main method
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        createFiles();
        loadFileToArrayList();
        recommissionFacility();
        writeArrayListToFile();
        login();
        writeArrayListToFile();
    }

    /**
     * createFiles() checks to see if the three files for accounts, bookings and facilities exist, if
     * not then it creates them.
     *
     * @throws IOException
     */
    public static void createFiles() throws IOException {
        File file1 = new File("Accounts.txt");
        if(!file1.exists())
            file1.createNewFile();

        File file2 = new File("Facilities.txt");
        if(!file2.exists())
            file2.createNewFile();

        File file3 = new File("Bookings.txt");
        if(!file3.exists())
            file3.createNewFile();
    }

    /**
     * loadFileToArrayList() creates scanners to scan the lines of the file into an object, then adds
     * the object to its corresponding ArrayList.
     *
     * @throws IOException
     */
    public static void loadFileToArrayList() throws IOException {
        facilities.clear();
        bookings.clear();
        accounts.clear();
        File file1 = new File("Facilities.txt");
        File file2 = new File("Accounts.txt");
        File file3 = new File("Bookings.txt");
        int userID;
        int facID;
        int bookingID;
        int type;
        double price;
        int slot;
        String email;
        String facName;
        String date;
        String password;
        String state;
        boolean payment;

        Scanner inFile1 = new Scanner(file1);
        while(inFile1.hasNext()) {
            String[] fileElements;
            fileElements = (inFile1.nextLine()).split(",");
            facID = Integer.parseInt(fileElements[0]);
            facName = fileElements[1];
            price = Double.parseDouble(fileElements[2]);
            state = fileElements[3];
            Facility facility = new Facility(facID, facName, price, state);
            facilities.add(facility);
        }
        inFile1.close();

        Scanner inFile2 = new Scanner(file2);
        while(inFile2.hasNext()) {
            String[] fileElements;
            fileElements = (inFile2.nextLine()).split(",");
            userID = Integer.parseInt(fileElements[0]);
            email = fileElements[1];
            password = fileElements[2];
            type = Integer.parseInt(fileElements[3]);
            Account user = new Account(userID, email, password, type);
            accounts.add(user);
        }
        inFile2.close();

        Scanner inFile3 = new Scanner(file3);
        while(inFile3.hasNext()) {
            String[] fileElements;
            fileElements = (inFile3.nextLine()).split(",");
            bookingID = Integer.parseInt(fileElements[0]);
            facID = Integer.parseInt(fileElements[1]);
            userID = Integer.parseInt(fileElements[2]);
            date = fileElements[3];
            slot = Integer.parseInt(fileElements[4]);
            payment = Boolean.parseBoolean(fileElements[5]);
            Booking appoint = new Booking(bookingID, facID, userID, date, slot, payment);
            bookings.add(appoint);
        }
        inFile3.close();
    }

    /**
     * login() allows the entry of either a user or admin account. It checks if the user exists and if the username
     * and password are correct. It also checks the account type to decide whether to load the user or admin menus.
     */
    public static void login() {
        boolean successfulLogin = false, found = false;
        String emailInput, passwordInput, email, password;
        int id = -1, type = 0, index = -1;

        JTextField user = new JTextField();
        JTextField pass = new JTextField();

        Object[] login = {"Email:", user, "Password:", pass};
        int selection = JOptionPane.showConfirmDialog(null, login, "Login", JOptionPane.OK_CANCEL_OPTION, -1);
        passwordInput = pass.getText();
        emailInput = user.getText();

        if(selection != 0) {
            System.exit(0);
        } else if(passwordInput.equals("") || emailInput.equals("")) {
            System.out.println("Nothing entered!");
            login();
        }

        Account admin = new Account(0, "Admin", "bobzilla", 1);
        if(accounts.isEmpty())
            accounts.add(admin);
        else if(!accounts.get(0).getEmail().equals("Admin"))
            accounts.add(admin);

        for(int i = 0; i < accounts.size() && !found; i++) {
            id = accounts.get(i).getID();
            email = accounts.get(i).getEmail();
            password = accounts.get(i).getPassword();
            type = accounts.get(i).getType();

            if(email.matches(emailInput) && password.matches(passwordInput)) {
                found = true;
                successfulLogin = true;
                index = i;
            } else if(email.matches(emailInput) && !password.matches(passwordInput)) {
                JOptionPane.showMessageDialog(null, "Incorrect password!", "Error", JOptionPane.ERROR_MESSAGE);
                found = true;
                login();
            }
        }
        if(!found) {
            JOptionPane.showMessageDialog(null, "Email not found!", "Error", JOptionPane.ERROR_MESSAGE);
            login();
        } else if(found && type == 1 && successfulLogin) {
            currentUser = admin;
            adminMenu();
        } else if(found && type == 0 && successfulLogin) {
            currentUser = accounts.get(index);
            userMenu();
        }
    }

    /**
     * createAccount() requires you to input a string containing bot "@"  and a "." with text in between for an email,
     * it uses the password generator in the account class for its password. It then adds the account to the accounts
     * ArrayList for writing to the file later.
     */
    public static void createAccount() {
        boolean restart = false;
        String Email = JOptionPane.showInputDialog(null, "Please enter your email:\n\n", "Creating Account", -1);
        if(Email == null)
            System.exit(0);
        Email = Email.replaceAll(" ", "");
        if(!(Email.contains("@") && Email.contains(".")) || Email.equals("")) {
            JOptionPane.showMessageDialog(null, "incorrect input detected", "Warning", 2);
            createAccount();
        }
        if(Email.indexOf("@") > Email.indexOf(".")) {
            JOptionPane.showMessageDialog(null, "incorrect input detected", "Warning", 2);
            createAccount();
        }
        String emailTitle = Email.substring(0, Email.indexOf("@"));
        String emailSigniture = Email.substring(Email.indexOf("@") + 1, Email.length());
        String emailSignitureWithoutCountry = Email.substring(Email.indexOf("@"), Email.indexOf("."));
        if(emailSignitureWithoutCountry.equals(""))
            restart = true;
        if(emailSigniture.equals(""))
            restart = true;
        if(emailTitle.equals(""))
            restart = true;
        if(restart) {
            JOptionPane.showMessageDialog(null, "incorrect input detected", "Warning", 2);
            createAccount();
        }
        int index = accounts.size();
        Account newUser = new Account(index, Email, 0);
        accounts.add(newUser);
        JOptionPane.showMessageDialog(null, accounts.get(index).getPassword(), "Password", -1);
        writeArrayListToFile();
        adminMenu();
    }

    /**
     * userMenu() can only be opened by users of the user type. From here you can view your account statement, bookings etc.
     */
    private static void userMenu() {
        String[] options = {"View Bookings", "View Availability", "View Account Statement"};
        String msgTitle = "User Menu", msg = "Select Option";
        String selection = (String) JOptionPane.showInputDialog(null, msg, msgTitle, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if(selection == null)
            login();
        switch(selection) {
            case "View Bookings":
                viewBookings();
                break;
            case "View Availability":
                displayAvailability();
                break;
            case "View Account Statement":
                accountStatement();
                break;
            default:
                System.out.println("Please select an option");
        }
    }

    /**
     * adminMenu() can only be opened by an account of admin type. From here you can manage facilities, manage payments,
     * create user accounts and manage bookings.
     */
    public static void adminMenu() {
        writeArrayListToFile();
        String[] options = {"Create User Account", "Manage Facilities", "Manage Payments", "Bookings"};
        String messageTitle = "Welcome Admin";
        String message = "Please choose an option...";
        String selection = (String) JOptionPane.showInputDialog(null, message, messageTitle, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if(selection == null)
            login();
        else {
            switch(selection) {
                case "Create User Account":
                    createAccount();
                    break;
                case "Manage Facilities":
                    manageFacilities();
                    break;
                case "Manage Payments":
                    paymentsMenu();
                    break;
                case "Bookings":
                    Object[] choices = {"Create Booking", "View Bookings"};
                    String msgTitle = "Bookings";
                    int n = JOptionPane.showOptionDialog(null, message, msgTitle, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, choices, choices[1]);
                    if(n == -1)
                        adminMenu();
                    else if(n == 0)
                        bookings();
                    else if(n == 1) {
                        viewBookings();
                    }
                    break;
                default:
                    System.out.println("Please select an option");
            }
        }
    }

    /**
     * manageFacilities() creates a JOptionPane drop down list with various facility options included,
     * such as addFacility(), decommissionFacility(), displayFacilities(), setFacilityPrice() and also
     * facilitates the removal of a facility.
     */
    public static void manageFacilities() {
        String[] options = {"Add Facility", "Decommission Facility", "Display Facilities", "Remove Facility", "Set Facility Price"};
        String line = "Please choose an option...";
        String selection = (String) JOptionPane.showInputDialog(null, line, "Manage Facilities", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if(selection == null)
            adminMenu();
        else {
            switch(selection) {
                case "Add Facility":
                    addFacility();
                    break;
                case "Decommission Facility":
                    decommissionFacility();
                    break;
                case "Display Facilities":
                    displayFacilities();
                    break;
                case "Remove Facility":
                    int index = selectFacility("remove");
                    facilities.remove(index);
                    manageFacilities();
                    break;
                case "Set Facility Price":
                    setFacilityPrice();
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Please select an option.");
            }
        }
    }

    /**
     * selectFacility() allows you to pick from a lost of all the facilities and return the index of that facility
     * int the array list. It gets passed a string that is used to fill in a sentence in the JOptionPane menu.
     *
     * @param purpose This fills in a blank in the sentence, "Which facility would you like to " + purpose + "?".
     * @return The index of the selected facility in the ArrayList is returned.
     */
    public static int selectFacility(String purpose) {
        int index = -1;
        try {
            String[] facNames = new String[facilities.size()];
            for(int i = 0; i < facilities.size(); i++) {
                facNames[i] = facilities.get(i).getName();
            }

            String message = "Which facility would you like to " + purpose + "?";
            String selection = (String) JOptionPane.showInputDialog(null, message, "Select Facility", JOptionPane.PLAIN_MESSAGE, null, facNames, facNames[0]);
            if(selection.equals("")) {
                selectFacility(purpose);
            } else if(selection == null) {
                System.exit(0);
            }


            for(int i = 0; i < facNames.length; i++) {
                if(selection.equals(facNames[i])) {
                    index = i;
                }
            }
            if(index == -1)
                JOptionPane.showMessageDialog(null, "Facility Not Found!", "Error", JOptionPane.ERROR_MESSAGE);

        } catch(Exception ArrayIndexOutOfBoundsException) {
            if(currentUser.getType() == 1 && facilities.size() < 1) {
                JOptionPane.showMessageDialog(null, "No facilities exist!\nPlease create a facility", "Error", JOptionPane.ERROR_MESSAGE);
                adminMenu();
            } else if(currentUser.getType() == 0 && facilities.size() < 1) {
                JOptionPane.showMessageDialog(null, "No facilities exist!\nPlease contact an admin", "Error", JOptionPane.ERROR_MESSAGE);
                userMenu();
            } else if(currentUser.getType() == 1) {
                adminMenu();
            } else {
                userMenu();
            }
        }
        return index;
    }

    /**
     * addFacility() opens a JOptionPane window that allows the input the facility name and its price per hour, it
     * uses this data to create a Facility object that is tben added to the array.
     */
    public static void addFacility() {
        JTextField facName = new JTextField();
        JTextField facPrice = new JTextField();
        Facility aFacility;
        int option;
        String state = "null";

        Object[] menu = {"Facility name:", facName, "Price:", facPrice};
        option = JOptionPane.showConfirmDialog(null, menu, "Add Facility", JOptionPane.OK_CANCEL_OPTION, -1);
        if(option == JOptionPane.CANCEL_OPTION) {
            manageFacilities();
        } else if(option == -1) {
            System.exit(0);
        }

        int ID = facilities.size();
        String name = facName.getText();
        String priceString = facPrice.getText();
        if((name.equals("")) || (priceString.equals("")))
            addFacility();

        double price = Double.parseDouble(priceString);
        aFacility = new Facility(ID, name, price, state);
        facilities.add(aFacility);
        manageFacilities();
    }

    /**
     * setFacilityPrice() uses the selectFacility() method to get the index of the facility you intend to change the
     * price for. It then takes your inputted number and sets the facility price thusly.
     */
    public static void setFacilityPrice() {
        int index = selectFacility("set the hourly price for");
        String message = "How much is this facility per hour?";
        String priceString = JOptionPane.showInputDialog(null, message, "Hourly Price", JOptionPane.QUESTION_MESSAGE);
        double price = Double.parseDouble(priceString);
        facilities.get(index).setPrice(price);
        manageFacilities();
    }

    /**
     * displayFacilities() goes through the facilities ArrayList and gets the variables for each object and adds them
     * to a string. This string then gets outputted into a JOptionPane window.
     */
    public static void displayFacilities() {
        UIManager.put("OptionPane.messageFont", new Font("Courier New", Font.PLAIN, 14));
        String facs = "";
        for(int i = 0; i < facilities.size(); i++) {
            facs += String.format("ID: %-3d", facilities.get(i).getID());
            facs += String.format(" Facility name: %-20.20s", facilities.get(i).getName());
            facs += String.format(" Price: %7.2f", facilities.get(i).getPrice());
            if(!facilities.get(i).getState().equals("null"))
                facs += String.format(" [Decommissioned]");
            facs += "\n";
        }
        JOptionPane.showMessageDialog(null, String.format(facs), "Facilities:", -1);
        UIManager.put("OptionPane.messageFont", new Font("Dialog", Font.BOLD, 12));
        manageFacilities();
    }

    /**
     * paymentsMenu() from here you can view account statements and add payments.
     */
    public static void paymentsMenu() {
        Object[] options = {"Add payment", "View account statement"};
        String message = "Please select an option...";
        String msgTitle = "Payments";
        int n = JOptionPane.showOptionDialog(null, message, msgTitle, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
        if(n == -1)
            adminMenu();
        else if(n == 0)
            addPayment();
        else if(n == 1) {
            accountStatement();
        }
    }

    /**
     * addPayment() makes you select a booking to pay and then sets its status to paid.
     */
    public static void addPayment() {
        int index = selectBooking();
        index -= 1;
        if(bookings.get(index).isPayment()) {
            JOptionPane.showMessageDialog(null, "This booking is already paid!", "Error", JOptionPane.ERROR_MESSAGE);
            addPayment();
        } else {
            Booking booking1 = bookings.get(index);
            double amtOwed = facilities.get(booking1.getFacilityID()).getPrice();
            JOptionPane.showMessageDialog(null, "A payment of \u20AC" + amtOwed + " has been made on\nbooking " + (index + 1), "Payment Complete", JOptionPane.PLAIN_MESSAGE);
            booking1.setPayment(true);
            writeArrayListToFile();
        }
    }

    /**
     * accountStatement() makes you select a user if you are an admin and uses your user ID if not, to get the unpaid
     * bookings associated with the relevant ID. It then prints out the date, facility and outstanding fees for each
     * booking.
     */
    public static void accountStatement() {
        double amtOwed = 0;
        Account userID;
        ArrayList<Booking> payments = new ArrayList<Booking>();
        if(currentUser.getType() == 1)
            userID = accounts.get(selectUser());
        else
            userID = currentUser;

        for(int i = 0; i < bookings.size(); i++) {
            if(bookings.get(i).getUserID() == userID.getID() && !bookings.get(i).isPayment())
                payments.add(bookings.get(i));
        }
        UIManager.put("OptionPane.messageFont", new Font("Courier New", Font.PLAIN, 14));
        String output = "";
        for(int i = 0; i < payments.size(); i++) {
            output += String.format("Date: %-10s", payments.get(i).getDate());
            output += String.format(" Facility name: %-20.20s", facilities.get(payments.get(i).getFacilityID()).getName());
            amtOwed += facilities.get(payments.get(i).getFacilityID()).getPrice();
            output += "\n";
        }
        output += String.format("\nTotal Outstanding: \u20AC%-8.2f \n", amtOwed);
        JOptionPane.showMessageDialog(null, String.format(output), "Account Statement for: " + userID.getEmail(), JOptionPane.PLAIN_MESSAGE);
        UIManager.put("OptionPane.messageFont", new Font("Dialog", Font.BOLD, 12));
        if(currentUser.getType() == 1) {
            adminMenu();
        } else {
            userMenu();
        }
    }

    /**
     * viewBookings() uses the current users ID if logged in as a user or prompts for a user ID if
     * logged in as admin to get their future bookings then prints the booking information out using a
     * JOptionPane dialog box. Doesn't display bookings that have passed
     */
    public static void viewBookings() {
        Account userID;
        ArrayList<Booking> userBookings = new ArrayList<Booking>();
        LocalDate systemDate = LocalDate.now();
        if(currentUser.getType() == 1)
            userID = accounts.get(selectUser());
        else
            userID = currentUser;

        for(int i = 0; i < bookings.size(); i++) {
            String date = bookings.get(i).getDate();
            String yearForLoop = date.substring(date.lastIndexOf("/") + 1, date.length());
            String tempDateForLoop = date.substring(0, date.indexOf("/"));
            String tempMonthForLoop = date.substring(date.indexOf("/") + 1, date.lastIndexOf("/"));
            int yearIntLoop = Integer.parseInt(yearForLoop);
            int dateDayLoop = Integer.parseInt(tempDateForLoop);
            int monthLoop = Integer.parseInt(tempMonthForLoop);
            LocalDate userDateLoop = LocalDate.of(yearIntLoop, monthLoop, dateDayLoop);

            if(bookings.get(i).getUserID() == userID.getID() && (userDateLoop.isAfter(systemDate) || !userDateLoop.isBefore(systemDate))) {
                userBookings.add(bookings.get(i));
            }
        }
        UIManager.put("OptionPane.messageFont", new Font("Courier New", Font.PLAIN, 14));
        String output = "";
        for(int i = 0; i < userBookings.size(); i++) {
            output += String.format("Date: %-10s", userBookings.get(i).getDate());
            output += String.format(" Facility name: %-20.20s", facilities.get(userBookings.get(i).getFacilityID()).getName());
            output += String.format(" Time: %-11s", timeSlots[userBookings.get(i).getSlot()]);
            output += "\n";
        }
        if(output.equals(""))
            output = "No future bookings found!";
        JOptionPane.showMessageDialog(null, String.format(output), "Account Statement for: " + userID.getEmail(), JOptionPane.PLAIN_MESSAGE);
        UIManager.put("OptionPane.messageFont", new Font("Dialog", Font.BOLD, 12));
        if(currentUser.getType() == 1) {
            adminMenu();
        } else {
            userMenu();
        }
    }

    /**
     * selectUSer() allows you to enter a user ID to select a user.
     *
     * @return userID, the ID of the user selected.
     */
    public static int selectUser() {
        int userID = 0;
        String pattern = "[0-9]{1,3}";
        try {
            String selection = (String) JOptionPane.showInputDialog(null, "Please enter a User ID...", "Select User", JOptionPane.PLAIN_MESSAGE);
            if(selection.equals("")) {
                JOptionPane.showMessageDialog(null, "No User ID entered\nPlease enter a valid User ID!", "Error", JOptionPane.ERROR_MESSAGE);
                accountStatement();
            }
            userID = Integer.parseInt(selection);
            if(userID >= accounts.size() || userID < 1) {
                JOptionPane.showMessageDialog(null, "This User ID does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
                selectUser();
            }
        } catch(NullPointerException e) {
            if(currentUser.getType() == 1)
                adminMenu();
            else
                userMenu();
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid User ID format\nPlease enter a valid User ID!", "Error", JOptionPane.ERROR_MESSAGE);
            accountStatement();
        }
        return userID;
    }

    /**
     * selectBooking() allows you to enter a booking ID to select the booking.
     *
     * @return bookingID, the ID of the selected booking.
     */
    public static int selectBooking() {
        int bookingID = 0;
        String pattern = "[0-9]{1,3}";
        try {
            String selection = (String) JOptionPane.showInputDialog(null, "Please enter a booking ID...", "Add Payment", JOptionPane.PLAIN_MESSAGE);
            if(selection.equals("")) {
                JOptionPane.showMessageDialog(null, "No booking ID entered\nPlease enter a valid booking ID!", "Error", JOptionPane.ERROR_MESSAGE);
                addPayment();
            }
            bookingID = Integer.parseInt(selection);
            if(bookingID >= bookings.size() || bookingID < 1) {
                JOptionPane.showMessageDialog(null, "This booking ID does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
                selectBooking();
            }
        } catch(NullPointerException e) {
            paymentsMenu();
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid User ID format\nPlease enter a valid User ID!", "Error", JOptionPane.ERROR_MESSAGE);
            paymentsMenu();
        }
        return bookingID;
    }

    /**
     * writeArrayListToFile() goes through each object ArrayList and gets the variables for each object and
     * writes them to its corresponding file.
     */
    public static void writeArrayListToFile() {
        File file1 = new File("Facilities.txt");
        File file2 = new File("Accounts.txt");
        File file3 = new File("Bookings.txt");
        int userID;
        int facID;
        int bookingID;
        int type;
        double price;
        int slot;
        String email;
        String facName;
        String date;
        String password;
        String state;
        boolean payment;

        try {
            FileWriter addFile1 = new FileWriter(file1);
            for(int i = 0; i < facilities.size(); i++) {
                facID = facilities.get(i).getID();
                facName = facilities.get(i).getName();
                price = facilities.get(i).getPrice();
                state = facilities.get(i).getState();
                addFile1.write(facID + "," + facName + "," + price + "," + state + "\r\n");
            }
            addFile1.close();

            FileWriter addFile2 = new FileWriter(file2);
            for(int i = 0; i < accounts.size(); i++) {
                userID = accounts.get(i).getID();
                email = accounts.get(i).getEmail();
                password = accounts.get(i).getPassword();
                type = accounts.get(i).getType();
                addFile2.write(userID + "," + email + "," + password + "," + type + "\r\n");
            }
            addFile2.close();

            FileWriter addFile3 = new FileWriter(file3);
            for(int i = 0; i < bookings.size(); i++) {
                userID = bookings.get(i).getUserID();
                facID = bookings.get(i).getFacilityID();
                bookingID = bookings.get(i).getID();
                date = bookings.get(i).getDate();
                slot = bookings.get(i).getSlot();
                payment = bookings.get(i).isPayment();
                addFile3.write(bookingID + "," + facID + "," + userID + "," + date + "," + slot + "," + payment + "\r\n");
            }
            addFile3.close();
        } catch(IOException ioe) {
            String message = "Error reading from file:" + ioe.getMessage();
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates a booking for a given user if no bookings are present for the selected date and time.
     */
    public static void bookings() {
        try {
            if(facilities.size() == 0) {
                JOptionPane.showMessageDialog(null, "No Facilities Present", "Warning", 2);
                addFacility();
            }
            int selection = selectFacility("book");
            String date = "";
            date = JOptionPane.showInputDialog(null, "Input what date you want the facility for\nIn the format:", "DD/MM/YYYY");
            if(date == null)
                adminMenu();
            String year = date.substring(date.lastIndexOf("/") + 1, date.length());
            String tempDate = date.substring(0, date.indexOf("/"));
            String tempMonth = date.substring(date.indexOf("/") + 1, date.lastIndexOf("/"));
            int yearInt = Integer.parseInt(year);
            int dateDay = Integer.parseInt(tempDate);
            int month = Integer.parseInt(tempMonth);
            LocalDate userDate = LocalDate.of(yearInt, month, dateDay);
            LocalDate systemDate = LocalDate.now();
            if(userDate.isBefore(systemDate)) {
                JOptionPane.showMessageDialog(null, "Invalid Date \n Date is before current date", "Warning", 2);
                bookings();
            }
            int slot = selectSlot();

            int selectedUser = selectUser();
            Booking currentBookings = new Booking(bookings.size(), selection, selectedUser, date, slot, false);
            String inputBooking = selection + " " + date + " " + slot + "\n";
            for(int i = 0; i < bookings.size(); i++) {
                int currentFacilityIDInt = bookings.get(i).getFacilityID();
                String currentFacilityID = Integer.toString(currentFacilityIDInt);
                String currentDate = bookings.get(i).getDate();
                int currentSlotInt = bookings.get(i).getSlot();
                String currentSlot = Integer.toString(currentSlotInt);
                String lineCheck = currentFacilityID + " " + currentDate + " " + currentSlot + "\n";
                if(lineCheck.equals(inputBooking)) {
                    JOptionPane.showMessageDialog(null, "Facility is already booked for this time", "Warning", 2);
                    bookings();
                }
            }
            bookings.add(currentBookings);

            JOptionPane.showMessageDialog(null, "A booking has been created for user " + selectedUser + "\nOn the " + date + ", At  " + timeSlots[slot], "Booking Created", JOptionPane.PLAIN_MESSAGE);
            adminMenu();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(null, "Bad Date Format", "Error", 2);
            bookings();
        }

    }

    /**
     * Displays a drop down menu to allow a user to select a time.
     *
     * @return Index of the selected slot
     */
    public static int selectSlot() {
        String message = "Which time would you like to book?";
        String selection = (String) JOptionPane.showInputDialog(null, message, "Select Facility", 1, null, timeSlots, timeSlots[0]);
        if(selection.equals("")) {
            JOptionPane.showMessageDialog(null, "No option selected!");
            selectSlot();
        } else if(selection.equals(null)) {
            bookings();
        }
        int index = 0;
        int x = 0;
        boolean check = true;
        while(check) {
            if(selection.equals(timeSlots[x])) {
                index = x;
                check = false;
            }
            x++;
        }
        return index;
    }

    /**
     * Checks if there are bookings after the date to be decommissioned, if there aren't it changes null to
     * the recommission date.
     */
    public static void decommissionFacility() {
        int selection = selectFacility("decommission");
        String pattern = "[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}";
        String input = (JOptionPane.showInputDialog(null, "Enter Date Here You Want To Decommission Until" + "\n" + "(format: DD/MM/YYYY)"));
        if(input.equals(""))
            decommissionFacility();
        if(input.matches(pattern)) {
            try {
                String year = input.substring(input.lastIndexOf("/") + 1, input.length());
                String tempDate = input.substring(0, input.indexOf("/"));
                String tempMonth = input.substring(input.indexOf("/") + 1, input.lastIndexOf("/"));
                int yearInt = Integer.parseInt(year);
                int dateDay = Integer.parseInt(tempDate);
                int month = Integer.parseInt(tempMonth);
                LocalDate userDate = LocalDate.of(yearInt, month, dateDay);
                LocalDate systemDate = LocalDate.now();
                if(userDate.isBefore(systemDate)) {
                    JOptionPane.showMessageDialog(null, "Invalid Date \n Date is before current date", "Warning", 2);
                    bookings();
                } else {
                    for(int i = 0; i < bookings.size(); i++) {
                        String date = bookings.get(i).getDate();
                        int selectionInArray = bookings.get(i).getFacilityID();
                        String yearForLoop = date.substring(date.lastIndexOf("/") + 1, date.length());
                        String tempDateForLoop = date.substring(0, date.indexOf("/"));
                        String tempMonthForLoop = date.substring(date.indexOf("/") + 1, date.lastIndexOf("/"));
                        int yearIntLoop = Integer.parseInt(yearForLoop);
                        int dateDayLoop = Integer.parseInt(tempDateForLoop);
                        int monthLoop = Integer.parseInt(tempMonthForLoop);
                        LocalDate userDateLoop = LocalDate.of(yearIntLoop, monthLoop, dateDayLoop);
                        if(selection == selectionInArray) {
                            if(userDate.isAfter(userDateLoop)) {
                                JOptionPane.showMessageDialog(null, "There is bookings present \n Cannot decommision", "Warning", 2);
                                decommissionFacility();
                            }
                        }
                    }
                    facilities.get(selection).setState(input);
                    manageFacilities();
                }
            } catch(Exception e) {
                JOptionPane.showMessageDialog(null, "Error: \n Bad Date", "Warning", 2);
                decommissionFacility();
            }
        }
    }

    /**
     * Checks if there are any dates in the facilities.txt that are today or before today and changes the date
     * to null if there are to recommission.
     */
    public static void recommissionFacility() {
        try {
            LocalDate systemDate = LocalDate.now();
            if(!(facilities.size() == 0)) {
                for(int i = 0; i < facilities.size(); i++) {
                    String date = facilities.get(i).getState();
                    if(!(date.equals("null"))) {
                        String yearForLoop = date.substring(date.lastIndexOf("/") + 1, date.length());
                        String tempDateForLoop = date.substring(0, date.indexOf("/"));
                        String tempMonthForLoop = date.substring(date.indexOf("/") + 1, date.lastIndexOf("/"));
                        int yearIntLoop = Integer.parseInt(yearForLoop);
                        int dateDayLoop = Integer.parseInt(tempDateForLoop);
                        int monthLoop = Integer.parseInt(tempMonthForLoop);
                        LocalDate userDateLoop = LocalDate.of(yearIntLoop, monthLoop, dateDayLoop);
                        if(systemDate.isAfter(userDateLoop)) {
                            facilities.get(i).setState("null");
                        }
                    }
                }
            }
        } catch(Exception e) {
            JOptionPane.showMessageDialog(null, "Bad date in text file", "Warning", 2);
            System.exit(0);
        }
    }

    /**
     * Displays the availability of a selected facility for the current week, showing "Free" if it's
     * available and if it's booked it shows "Book". It displays it in a text based table
     */
    public static void displayAvailability() {
        int currentFacility = selectFacility("to view availability");
        String output = "";

        String availabiltyTable[][] = new String[9][7];
        LocalDate systemDate = LocalDate.now();
        LocalDate weekDate = LocalDate.now();

        if(systemDate.getDayOfWeek() == DayOfWeek.TUESDAY) {
            weekDate = systemDate.minusDays(1);
        } else if(systemDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
            weekDate = systemDate.minusDays(2);
        } else if(systemDate.getDayOfWeek() == DayOfWeek.THURSDAY) {
            weekDate = systemDate.minusDays(3);
        } else if(systemDate.getDayOfWeek() == DayOfWeek.FRIDAY) {
            weekDate = systemDate.minusDays(4);
        } else if(systemDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            weekDate = systemDate.minusDays(5);
        } else if(systemDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            weekDate = systemDate.minusDays(6);
        }

        for(String[] row : availabiltyTable)
            Arrays.fill(row, "Free");

        for(int k = 0; k < bookings.size(); k++) {
            if(bookings.get(k).getFacilityID() == currentFacility) {
                String date = bookings.get(k).getDate();
                String yearForLoop = date.substring(date.lastIndexOf("/") + 1, date.length());
                String tempDateForLoop = date.substring(0, date.indexOf("/"));
                String tempMonthForLoop = date.substring(date.indexOf("/") + 1, date.lastIndexOf("/"));
                int yearIntLoop = Integer.parseInt(yearForLoop);
                int dateDayLoop = Integer.parseInt(tempDateForLoop);
                int monthLoop = Integer.parseInt(tempMonthForLoop);
                LocalDate userDateLoop = LocalDate.of(yearIntLoop, monthLoop, dateDayLoop);
                for(int l = 0; l < 7; l++) {
                    if(userDateLoop.equals(weekDate.plusDays(l))) {
                        availabiltyTable[bookings.get(k).getSlot()][l] = "Book";
                    }
                }
            }
        }

        output = "The availability for the week: " + weekDate + " to " + weekDate.plusDays(6);
        String titles = ("\n     Time    | Mon  | Tue  | Wed  | Thu  | Fri  | Sat  | Sun  |\n");
        output += titles;

        for(int i = 0; i < (titles.length()); i++) {
            output += "=";
        }
        output += "\n";
        char divChar = '|';
        for(int i = 0; i < 9; i++) {//Rows
            output += " " + timeSlots[i] + " " + divChar + " ";
            for(int j = 1; j < 8; j++) {//Columns
                output += availabiltyTable[i][j - 1];
                output += (" " + divChar + " ");
            }
            output += "\n";
        }
        UIManager.put("OptionPane.messageFont", new Font("Courier New", Font.PLAIN, 14));
        JOptionPane.showMessageDialog(null, output, facilities.get(currentFacility).getName() + " Availability", -1);
        UIManager.put("OptionPane.messageFont", new Font("Dialog", Font.BOLD, 12));
        if(currentUser.getType() == 1) {
            adminMenu();
        } else {
            userMenu();
        }
    }
}
