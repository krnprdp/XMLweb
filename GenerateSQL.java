

import java.io.*;
import java.util.*;


public class GenerateSQL {
    public static ArrayList GenerateSQL_EP(HashMap<String, String> inpHM) {
        File obj=new File("ParsedSQL.sql");
        if(obj!=null)obj.delete();
        Set set = inpHM.entrySet();
        ArrayList arrList = new ArrayList();
        String[] tab1 = new String[inpHM.size()];
        String[] tab2 = new String[inpHM.size()];
        String[] tab3 = new String[inpHM.size()];
        String[] tempStrArr;
        int i1 = 0,i2=0,keyat=0,ti=0;
        if (inpHM.containsKey("tableName")) {
            writeToFile("create table " + inpHM.get("tableName") + "(",1);
        }
        tab3[ti]=inpHM.get("tableName");ti++;
        for (Object aSet : set) {
            Map.Entry me = (Map.Entry) aSet;
            if (me.getValue() == "textbox") {
                tab1[i1] = String.valueOf(me.getKey());
                i1++;
            } else if (me.getValue() == "radio") {
                tab1[i1] = String.valueOf(me.getKey());
                i1++;
            } else if (me.getValue() == "select") {
                tab1[i1] = String.valueOf(me.getKey());
                i1++;
            } else if (me.getValue() == "checkboxes") {
                tab2[i2] = String.valueOf(me.getKey());
                i2++;
            } else if (me.getValue() == "multiselect") {
                tab2[i2] = String.valueOf(me.getKey());
                i2++;
            }
        }
        for (int j = 0; j < i1; j++) {
            if(j!=0){
                writeToFile(",",0);
            }
            if (Objects.equals(tab1[j], inpHM.get("key"))) {
                writeToFile(tab1[j] + " varchar(20) primary key",1);
                keyat=j;
            } else {
                writeToFile(tab1[j] + " varchar(20)",1);
            }
        }
        writeToFile(");", 1);
        arrList.add(tab1);
        for (int j = 0; j < i2; j++) {
            int x=0;
            tempStrArr = new String[inpHM.size()];;
            writeToFile("create table " + inpHM.get("tableName") + "_" + j + "(", 1);
            tab3[ti]=inpHM.get("tableName") + "_" + j;ti++;
            writeToFile(tab1[keyat]+ " varchar(20),",1);
            tempStrArr[x] = tab1[keyat];x++;
            writeToFile(tab2[j] + " varchar(20)",1);
            tempStrArr[x] = tab2[j];
            writeToFile(");",1);
            arrList.add(tempStrArr);
        }
        arrList.add(tab3);
        return arrList;
    }

    public static void writeToFile(String inp,int nlf) {
        PrintWriter fileWriter = null;
        try {

            fileWriter = new PrintWriter(new BufferedWriter(new FileWriter("ParsedSQL.sql", true)));
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
