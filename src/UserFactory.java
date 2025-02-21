public class UserFactory {
    public static User getUser(String user){
        User u=null;
        user=user.toLowerCase();
        if(user.equals("student"))
        {
            u=new Student();
        }
        else if(user.equals("professor"))
        {
            u=new Professor();
        }
        else {
            u=new Admin();
        }
        return u;
    }
}
