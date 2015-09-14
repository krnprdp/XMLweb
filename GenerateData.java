

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.*;
import java.util.ArrayList;

public class GenerateData {
    public static void GenerateData_EP(ArrayList inpAL){
        File obj=new File("ParsedDB.php");
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
            String[] str = (String[]) inpAL.get(i);
            writeToFile("echo \"<h1>"+((String[]) inpAL.get(inpAL.size()-1))[i]+"</h1>\";\n\n",1);
            writeToFile("$query = \"SELECT * FROM "+((String[]) inpAL.get(inpAL.size()-1))[i]+"\";\n" +
                    "try {\n" +
                    "  $stmt   = $db->prepare($query);\n" +
                    "  $result = $stmt->execute();\n" +
                    "}\n" +
                    "catch (PDOException $ex) {\n" +
                    "  $response[\"success\"] = 0;\n" +
                    "  $response[\"message\"] = \"Database Error3. Please Try Again!\";\n" +
                    "  die(json_encode($response));\n" +
                    "}\n" +
                    "echo \"<table border=\\\"1\\\">\";" +
                    "while($row = $stmt->fetch())\n" +
                    "{",1);
            writeToFile("echo \"<tr>",0);
            int j=0;
            while (str[j] != null) {
                writeToFile("<td>\".$row['"+str[j]+"'].\"</td>",0);j++;
            }
            writeToFile("</tr>\";",0);
            writeToFile("\n}\n",1);
            writeToFile("echo \"</table>\";",1);
        }
        writeToFile("?>",1);
    }


    public static void writeToFile(String inp,int nlf) {
        PrintWriter fileWriter = null;
        try {
            fileWriter = new PrintWriter(new BufferedWriter(new FileWriter("ParsedDB.php", true)));
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
