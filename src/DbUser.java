public class DbUser {
    public String name;
    public String userName; // Can also be Email
    public String  password;
    public DbUser()
    {
        this.name="";
        this.userName="";
        this.password="";
    }
    public boolean exists()
    {
        return (!name.isEmpty() && !userName.isEmpty() && !password.isEmpty());
    }
}
