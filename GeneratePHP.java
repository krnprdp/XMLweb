
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.*;
import java.util.ArrayList;


public class GeneratePHP {
    public static void GeneratePHP_EP(ArrayList inpAL) {
        File obj=new File("ParsedPHP.php");
        if(obj!=null)obj.delete();
        
        Properties prop = new Properties();
		try {
			InputStream is = new FileInputStream("config.properties");
			prop.load(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        String username = prop.getProperty("username");
        String password = prop.getProperty("password");
		String host = prop.getProperty("host");
		String dbname = prop.getProperty("dbname");
        
        writeToFile("<?php \n" +
                "    $username = \""+username+"\"; \n" +
                "    $password = \""+password+"\"; \n" +
                "    $host = \""+host+"\"; \n" +
                "    $dbname = \""+dbname+"\"; \n" +
                "    $options = array(PDO::MYSQL_ATTR_INIT_COMMAND => 'SET NAMES utf8'); \n" +
                "    try \n" +
                "    { \n" +
                "        $db = new PDO(\"mysql:host={$host};dbname={$dbname};charset=utf8\", $username, $password, $options); \n" +
                "    } \n" +
                "    catch(PDOException $ex) \n" +
                "    { \n" +
                "        die(\"Failed to connect to the database: \" . $ex->getMessage()); \n" +
                "    } \n" +
                "    $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION); \n" +
                "    $db->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC); \n" +
                "    if(function_exists('get_magic_quotes_gpc') && get_magic_quotes_gpc()) \n" +
                "    { \n" +
                "        function undo_magic_quotes_gpc(&$array) \n" +
                "        { \n" +
                "            foreach($array as &$value) \n" +
                "            { \n" +
                "                if(is_array($value)) \n" +
                "                { \n" +
                "                    undo_magic_quotes_gpc($value); \n" +
                "                } \n" +
                "                else \n" +
                "                { \n" +
                "                    $value = stripslashes($value); \n" +
                "                } \n" +
                "            } \n" +
                "        } \n" +
                "     \n" +
                "        undo_magic_quotes_gpc($_POST); \n" +
                "        undo_magic_quotes_gpc($_GET); \n" +
                "        undo_magic_quotes_gpc($_COOKIE); \n" +
                "    } \n" +
                "    header('Content-Type: text/html; charset=utf-8');\n",1);

        for (int i = 0; i < inpAL.size()-1; i++) {
            writeToFile("",1);
            int flg=0;
            String[] str = (String[]) inpAL.get(i);
            if(i>0){
                flg=1;
                writeToFile("foreach($_POST['"+str[1]+"'] as $selected){",1);
            }
            writeToFile("$query = \"INSERT INTO "+ ((String[]) inpAL.get(inpAL.size()-1))[i] +"(",0);
            printStrArr(str, 0);
            writeToFile(")VALUES ( ", 0);
            printStrArr(str, 1);
            writeToFile(")\";", 1);

            writeToFile("$query_params = array(",1);
            int j=0;
            while (str[j] != null) {
                if(j!=0){writeToFile(",\n",0);}
                if(flg==1 && j>0)
                    writeToFile("':"+str[j]+"' => $selected",0);
                else
                    writeToFile("':"+str[j]+"' => $_POST['"+str[j]+"']",0);
                j++;
            }
            writeToFile(");",1);
            writeToFile("try {\n" +
                    "\t\t$stmt   = $db->prepare($query);\n" +
                    "\t\t$result = $stmt->execute($query_params);\n" +
                    "\t}\n" +
                    "\tcatch (PDOException $ex) {\n" +
                    "\t}",1);

             if(i!=0){
        writeToFile("}",1);
    }
        }
       
        writeToFile("?>",1);
        writeToFile("<div ><h1>Successfully Entered<h1></div>",1);
    }

    public static void printStrArr(String[] str,int nc){
        int j=0;
        while (str[j] != null) {
            if(j!=0){writeToFile(",",0);}
            if(nc==1){writeToFile(":",0);}
            writeToFile(str[j],0);j++;
        }
    }

    public static void writeToFile(String inp,int nlf) {
        PrintWriter fileWriter = null;
        try {

            fileWriter = new PrintWriter(new BufferedWriter(new FileWriter("ParsedPHP.php", true)));
            if(nlf==0)
                fileWriter.print(inp);
            else
                fileWriter.println(inp);
        } catch (IOException E) {
            E.printStackTrace();
        }

        if (fileWriter != null) {
            fileWriter.close();
        }

    }
}
