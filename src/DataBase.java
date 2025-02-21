import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;

public class DataBase
{
    private static final String url="jdbc:mysql://localhost:3306/oop";
    private static final String user="root";
    private static final String password="new_password";
    public static Connection connection;
    public static Statement statement;
    static {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement=connection.createStatement();
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
    public static ResultSet dispRegCourses(int student_id,int sem)
    {
        ResultSet rs=null;
        String query=String.format("select * from Course c LEFT JOIN Professor p on c.professor_id=p.professor_id JOIN Student_Course s on c.course_id = s.course_id WHERE s.student_id = %d AND c.semester=%d",student_id,sem);
        try
        {
            rs=statement.executeQuery(query);
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        finally {
            return rs;
        }
    }
    public static ResultSet getUser(String userName,String userType)
    {
        ResultSet rs=null;
        String Query="select * from "+userType+" where email='"+userName+"'";
        try {
            rs = statement.executeQuery(Query);
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        finally {
            return rs;
        }
    }
    public static boolean setPassword(String userName,String password,String type)
    {
        int rowsAffected=0;
        String Query=String.format("UPDATE "+type+" SET password='%s' WHERE email='%s' AND password IS NULL",password,userName);
        try {
            rowsAffected = statement.executeUpdate(Query);
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        finally
        {
            return rowsAffected>0;
        }
    }
    public static ResultSet getCourses(int sem)
    {
        ResultSet rs=null;
        try
        {
            String query=String.format("select * from Course c LEFT JOIN Professor p on c.professor_id=p.professor_id WHERE semester = %d",sem);
            rs=statement.executeQuery(query);
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        finally
        {
            return rs;
        }
    }
    public static boolean addCourse(int studentId,int courseId,int semester)throws SQLException,CourseNotFoundException
    {
        int sem=0;
        String preReq="";
        String Query=String.format("select semester,prerequisites from Course WHERE course_id = %d",courseId);
        ResultSet rs=null;
        boolean ans=false;
        try
        {
            rs=statement.executeQuery(Query);
            while(rs.next())
            {
                sem=rs.getInt("semester");
                preReq=rs.getString("prerequisites");
            }
            if(sem==0 && preReq.isEmpty())
            {
                throw new CourseNotFoundException("Course Not Found");
            }
            if(sem!=semester)
            {
                System.out.println("Course is not in the Current Semester");
                ans=false;
            }
            else {
                boolean p = false;
                try {
                    p = matchPrereq( preReq, studentId);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                if (p) {
                    Query = String.format("INSERT INTO Student_Course (student_id,course_id) VALUES(%d,%s)", studentId, courseId);
                    int rowsAffected = statement.executeUpdate(Query);
                    if (rowsAffected > 0) {
                        ans = true;
                    }
                }
            }
        }
        catch(SQLException e)
        {
            throw e;
        }
        finally{
            return ans;
        }
    }
    public static boolean matchPrereq(String preReq,int studentId)
    {
        String [] c=preReq.split(",");
        Arrays.sort(c);
        ArrayList<String>d=new ArrayList<>();
        ResultSet rs=null;
        boolean ans=false;
        try
        {
            String  Query=String.format("select course_id from Student_Course where student_id=%d",studentId);
            rs=statement.executeQuery(Query);
            while(rs.next())
            {
                String a=rs.getString("course_id");
                d.add(a);
            }
            String []PreReq=d.toArray(new String[d.size()]);
            Arrays.sort(PreReq);
            if(c.length>PreReq.length)
            {
                return false;
            }
            for(int i=0;i<PreReq.length;i++)
            {
                if(!PreReq[i].equals(c[i]))
                {
                    ans=false;
                    break;
                }
            }
            ans=true;
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        finally {
            return ans;
        }
    }
    public static boolean dropCourse(int student_id,String course)
    {
        boolean ans=false;
        String query=String.format("DELETE FROM Student_Course WHERE student_id=%d AND course_id=%s ",student_id,course);
        try{
            int rowsAffected=statement.executeUpdate(query);
            if(rowsAffected>0)
            {
                ans=true;
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        finally {
            return ans;
        }
    }
}
