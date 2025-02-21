import java.util.InputMismatchException;
import java.util.Scanner;


public class Main {
    static String[] users ={"Student","Professor","Admin"};
    public static Scanner sc=new Scanner(System.in);
    static{
        try{
            Class.forName("DataBase");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
    }
    public static void main(String[] args) {
        System.out.println("Welcome to the University Course Registration System");
        System.out.println("Please Enter your choice? ");
        System.out.println("1. Login");
        System.out.println("2. Set Password");
        int signIn=-1;
        try {
            signIn = sc.nextInt();
            if(signIn>2 || signIn<=0){
                throw new InvalidChoiceException("Invalid Choice");
            }
        }
        catch(InputMismatchException e) {
            System.out.println("Please enter a valid number");
            System.exit(0);
        }
        catch(InvalidChoiceException e) {
            System.out.println("Enter A number in the range of 1-2");
            System.exit(0);
        }
        if(signIn==2)
        {
            SignUp.signup();
            System.exit(0);
        }
        System.out.println("Please Enter your Choice: ");
        System.out.println("Login As");
        int choice;
        System.out.println("1. Student");
        System.out.println("2. Professor");
        System.out.println("3. Admin");
        System.out.println("4. Exit");
        try
        {
            choice = sc.nextInt();
            if(choice<1 ||  choice>4)
            {
                throw new InvalidChoiceException("Invalid Choice");
            }
            if(choice==4)
            {
                System.exit(0);
            }
            User user= UserFactory.getUser(users[choice-1]);
            if(user.ValidateUser())
            {
                
                while(!user.isLoggedOut)
                {
                    System.out.println("--------------------");
                    user.DisplayOptions();
                }
            }
            else {
                System.out.println("UserName or Password is incorrect");
                System.exit(0);
            }

        }
        catch(InputMismatchException e)
        {
            System.out.println("Please enter a Digit From 1-4");
            main(null);
        }
        catch(InvalidChoiceException e)
        {       
            System.out.println("Please enter a valid choice");
            main(null);
        }
        finally{
            try{
                DataBase.connection.close();
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
            sc.close();
      }
    }
}
