public class Booking {
    private int ID;
    private int facilityID;
    private int UserID;
    private String date;
    private int slot;
    private boolean payment;

    Booking() {
        ID = 0;
        facilityID = 0;
        UserID = 0;
        date = "";
        slot = 0;
        payment = false;
    }

    Booking(int aID, int aFacilityID, int aUserID, String aDate, int aSlot, boolean aPayment) {
        ID = aID;
        facilityID = aFacilityID;
        UserID = aUserID;
        date = aDate;
        slot = aSlot;
        payment = aPayment;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getFacilityID() {
        return facilityID;
    }

    public void setFacilityID(int facilityID) {
        this.facilityID = facilityID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public boolean isPayment() {
        return payment;
    }

    public void setPayment(boolean payment) {
        this.payment = payment;
    }

}