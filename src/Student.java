import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;

public class Student extends User{
    private String name;

    private int semester;
    private int student_id;
    public Student()
    {
        this.userType=UserType.STUDENT;
        this.isLoggedOut=false;
    }
    @Override
    public boolean ValidateUser()
    {
        String password;
        System.out.println("Login Details: ");
        System.out.println("Enter your UserName:  ");
        this.userName=Main.sc.next();
        System.out.println("Enter your Password:  ");
        password=Main.sc.next();
        ResultSet rs=DataBase.getUser(this.userName,"Student");
        String dbPass="";
        try {
            while (rs.next()) {
                this.name = rs.getString("name");
                dbPass = rs.getString("password");
                this.semester=rs.getInt("semester");
                this.student_id=rs.getInt("student_id");
            }
        }catch(SQLException e) {
            System.out.println(e.getMessage());        }
        finally{
            return dbPass.equals(password);
        }
    }
    @Override
    public void DisplayOptions()
    {
        System.out.println("Choose from the below Options");
        System.out.println("1. View All courses");
        System.out.println("2. View Registered Courses");
        System.out.println("3. Add a course");
        System.out.println("4. Drop a course");
        System.out.println("5. View Schedule");
        System.out.println("6. Submit Complaint");
        System.out.println("7. View complaint Status");
        System.out.println("8. View CGPA and SGPA");
        System.out.println("9. Logout");
        int choice=Main.sc.nextInt();
        if(choice==1)
        {
            displayAllCourses();
        }
        else if(choice==3)
        {
            addCourse();
        }
        else if(choice==2)
        {
            displayRegCourse();
        }
        else if(choice==4)
        {
            dropCourse();
        }
        else if(choice==9   ){
            isLoggedOut=true;
        }
        else if(choice==5) {
            viewSchedule();
        }
        else if(choice==6)
        {
            submitComplaint();
        }
        else if(choice==7)
        {
            viewComplaintProgress();
        }
        else if(choice==8)
        {
            result();
        }
        else {
            System.out.println("Please enter a valid option");
        }

    }
    public void dropCourse()
    {
        displayRegCourse();
        System.out.println("Enter the Course ID from above to Drop Course");
        try {
            String choice = Main.sc.next();
            boolean flag=DataBase.dropCourse(this.student_id,choice);
            if(flag)
            {
                System.out.println("Course Dropped");
            }
            else{
                System.out.println("Course Not Dropped");
            }
        }
        catch(InputMismatchException e) {
            System.out.println(e.getMessage());
        }


    }
    public void displayRegCourse()
    {
        ResultSet rs=DataBase.dispRegCourses(this.student_id,this.semester);
        try {
            String ls="| %-10s | %-25s | %-19s | %-7s | %-23s | %-14s |%n";
            System.out.printf("+------------+--------------------------------------------------+---------------------+---------+-------------------------+----------------+%n");
            System.out.printf(ls,"CourseId","Title","Professor","Credits","Prerequisites","Timing");
            while (rs.next()) {
                String course_id=rs.getString("course_id");
                String title=rs.getString("title");
                String professor=rs.getString("name");
                String credits=rs.getString("credits");
                String prerequisites=rs.getString("prerequisites");
                String timing=rs.getString("timing");
                System.out.printf(ls,course_id,title,professor,credits,prerequisites,timing);
            }
            System.out.printf("+------------+--------------------------------------------------+---------------------+---------+-------------------------+----------------+%n");
        }catch(SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    public void addCourse()
    {
        System.out.println("Enter the Course ID to Add");
        int course_id=Main.sc.nextInt();
        // Add Course
        try {
            boolean add = DataBase.addCourse(this.student_id, course_id,this.semester);
            if (add) {
                System.out.println("Successfully added course");
            } else {
                System.out.println("Failed to add course");
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    public void displayAllCourses()
    {
        ResultSet rs=DataBase.getCourses(this.semester);

        try {
            String ls="| %-10s | %-25s | %-19s | %-7s | %-23s | %-14s |%n";
            System.out.printf("+------------+--------------------------------------------------+---------------------+---------+-------------------------+----------------+%n");
            System.out.printf(ls,"CourseId","Title","Professor","Credits","Prerequisites","Timing");
            System.out.printf("+------------+--------------------------------------------------+---------------------+---------+-------------------------+----------------+%n");
            while (rs.next()) {
                String course_id=rs.getString("course_id");
                String title=rs.getString("title");
                String professor=rs.getString("name");
                String credits=rs.getString("credits");
                String prerequisites=rs.getString("prerequisites");
                String timing=rs.getString("timing");
                System.out.printf(ls,course_id,title,professor,credits,prerequisites,timing);
            }
            System.out.printf("+------------+--------------------------------------------------+---------------------+---------+-------------------------+----------------+%n");
        }catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void viewSchedule(){
        try{
            String query=String.format("SELECT sc.course_id as Course_id, c.title as Title,c.timing as Timing,p.name as Professor_Name FROM Student_Course as sc JOIN Course as c ON sc.course_id=c.course_id JOIN Professor as p ON c.professor_id=p.professor_id WHERE sc.student_id=%d AND c.semester=%d",this.student_id,this.semester);
            ResultSet rs= DataBase.statement.executeQuery(query);
            System.out.printf("%-20s %-30s %-15s %-20s%n", "Course", "Title", "Timings", "Professor Name");
            System.out.println("--------------------------------------------------------------------------------------------");

            while (rs.next()) {
                String course = rs.getString("Course_id");
                String title = rs.getString("Title");
                String timings = rs.getString("Timing");
                String proName = rs.getString("Professor_Name");

                System.out.printf("%-20s %-30s %-15s %-20s%n", course, title, timings, proName);
            }
            System.out.println("--------------------------------------------------------------------------------------------");

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    public void submitComplaint()
    {
        try{

            System.out.println("Enter your complaint");
            Scanner sc=new Scanner(System.in);
            String des = sc.nextLine();
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
            String query=String.format("INSERT INTO Complaint (description,status,date,student_id) VALUES ('%s' ,'Pending','%s',%d )",des,currentDate,this.student_id);
            int rowsAffected= DataBase.statement.executeUpdate(query);
            if(rowsAffected>0) {
                System.out.println("Your complaint is registered successfully.");
                System.out.println("It will be resolved as soon as possible");
            }
            else{
                System.out.println("There exits an Error.Please try again");
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    public void viewComplaintProgress()
    {
        try{
            String query=String.format("SELECT complaint_id,status,date,resolution_details FROM Complaint WHERE student_id=%d",this.student_id);

            ResultSet rs= DataBase.statement.executeQuery(query);
                System.out.printf("+--------------------+-----------------+----------+--------------------------------+%n");
                String S="| %-18s | %-15s | %-8s | %-30s |%n";
                System.out.printf(S,"ComplaintId","Date","Status","ResolutionDetails");
                System.out.printf("+--------------------+-----------------+----------+--------------------------------+%n");
            while(rs.next()){
                int comp=rs.getInt("complaint_id");
                String s=rs.getString("status");
                java.sql.Date d= rs.getDate("date");
                String res = Objects.requireNonNullElse(rs.getString("resolution_details"), "-");
                System.out.printf(S,comp,d,s,res);
          }
            System.out.printf("+--------------------+-----------------+----------+--------------------------------+%n");

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void result()
    {
        float []sg=new float[9];
        int []noOfcourse=new int[9];
        float []cg=new float[9];
        //Find out How many Grades in Current Semester are assigned and how many coursed are registed
        ResultSet rs=null;
        int lastSem=0;
        int currRegister=0,currGrade=0;
        try
        {
            String Query=String.format("select COUNT(*) as count FROM Student_Course s JOIN Course c on s.course_id=c.course_id WHERE s.student_id=%d AND c.semester=%d",this.student_id,this.semester);
            rs=DataBase.statement.executeQuery(Query);
            while(rs.next())
            {
                currRegister=rs.getInt("count");
            }
            Query=String.format("select COUNT(*) as count FROM Grade g JOIN Course c on g.course_id=c.course_id WHERE g.student_id=%d AND c.semester=%d",this.student_id,this.semester);
            rs=DataBase.statement.executeQuery(Query);
            while(rs.next())
            {
                currGrade=rs.getInt("count");
            }
            if(currRegister==currGrade)
            {
                Query=String.format("select * from  Grade g JOIN Course c on g.course_id =c.course_id WHERE c.semester<=%d AND g.student_id=%d ",this.semester,this.student_id);
                rs=DataBase.statement.executeQuery(Query);
                while(rs.next())
                {
                    int grade=rs.getInt("grade");
                    int credit=rs.getInt("credits");
                    int sem=rs.getInt("semester");
                    int tGrage=grade*credit;
                    sg[sem]+=tGrage;
                    noOfcourse[sem]++;
                }
                lastSem=this.semester;
            }
            else
            {
                Query=String.format("select * from  Grade g JOIN Course c on g.course_id =c.course_id WHERE c.semester<%d AND g.student_id=%d ",this.semester,this.student_id);
                rs=DataBase.statement.executeQuery(Query);
                while(rs.next())
                {
                    int grade=rs.getInt("grade");
                    int credit=rs.getInt("credits");
                    int sem=rs.getInt("semester");
                    int tGrage=grade*credit;
                    sg[sem]+=tGrage;
                    noOfcourse[sem]++;
                }
                lastSem=this.semester-1;
            }
            for(int i=1;i<=lastSem;++i)
            {
                sg[i]=sg[i]/noOfcourse[i];
                if(i==1)
                {
                    cg[i]=sg[i];
                }
                else {
                    float total=0.0f;
                    for(int j=1;j<=i;j++)
                    {
                        total+=sg[j];
                    }
                    cg[i]=total/i;
                }
            }
            //print each sem sg and cg
            String format="| %-10s | %-10s | %-10s |%n";
            System.out.printf("+------------+------------+------------+%n");
            System.out.printf(format,"SGPA", "CGPA", "Semester");
            System.out.printf("+------------+------------+------------+%n");
            for(int i=1;i<=lastSem;++i)
            {
                System.out.printf(format,sg[i],cg[i],i);
            }
            System.out.printf("+------------+------------+------------+%n");
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
}

