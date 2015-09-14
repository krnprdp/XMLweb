



import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        HashMap<String,String> sqlHM;
        ArrayList sqlAL;
        sqlHM= GenerateHTML.GenerateHTML_EP(args[0]);
        sqlAL=GenerateSQL.GenerateSQL_EP(sqlHM);
        GeneratePHP.GeneratePHP_EP(sqlAL);
        GenerateData.GenerateData_EP(sqlAL);
    }
}