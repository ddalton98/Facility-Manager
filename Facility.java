public class Facility {
    private int     ID;
    private String  name;
    private double  price;
    private String  state;

    Facility(){
        ID     = 0;
        name   = "";
        price  = 0.0;
        state  = "";
    }

    Facility(int aID, String aName, double aPrice){
        ID 		= aID;
        name 	= aName;
        price 	= aPrice;
    }

    Facility(int aID, String aName, double aPrice, String aState){
        ID 		= aID;
        name 	= aName;
        price 	= aPrice;
        state 	= aState;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}