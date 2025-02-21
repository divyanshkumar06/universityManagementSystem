import java.util.InputMismatchException;


public class SignUp {
    private static final  String[] type={"Student","Professor"};
    public static void signup()
    {
        int choice=1;
        String userName,passWord;
        System.out.println("Enter the UserName/ Email");
        userName=Main.sc.next();
        System.out.println("Enter the Password");
        passWord=Main.sc.next();
        boolean isUpdated=DataBase.setPassword(userName,passWord,type[choice-1]);
        if(isUpdated)
        {
            System.out.println("Password Updated");
            System.out.println("Login Again to Access");
        }
        else {
            System.out.println("Something went wrong");
            System.out.println("Check the email you have entered");
            System.out.println("Contact the Admin for further details");
        }

    }
}
