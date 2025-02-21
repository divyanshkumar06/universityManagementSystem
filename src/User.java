abstract public class User {
    public String userName;
    public UserType userType;
    public boolean isLoggedOut;
    abstract public boolean ValidateUser();
    abstract public void DisplayOptions();
}
