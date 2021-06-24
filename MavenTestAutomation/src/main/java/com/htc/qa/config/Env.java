package com.htc.qa.config;
import au.com.bytecode.opencsv.CSVReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.htc.qa.reporting.Log;


public class Env {

    private static Map<String,String> ps_Dic = new HashMap<>();

    static{
        InitializeEnvDataFile(Config.getConfigProperty("test.environment"));
    }

    public static String get(String key){
        key = key.toUpperCase().trim();
        if(ps_Dic.containsKey(key)){
            return ps_Dic.get(key);
        }
        else{
            return null;
        }
    }

    private static String GetWorkingDirectory(){
        String strWD = "";
        try {
            strWD = (new File(".")).getCanonicalPath();
        }
        catch (IOException e){

        }

        return strWD;
    }

    private static void InitializeEnvDataFile(String EnvName) {

        //Assume environment data file is available in the environment/environment.csv file
        String strEnvPath =  GetWorkingDirectory() + "/src/test/resources/data/environment/environment.csv";
        File fileEnv = new File(strEnvPath);

        //Check file exists
        if (!fileEnv.exists()) {
            String errorMessage = String.format("Environment data file %s missing. ", strEnvPath);
            Log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        //Read the CSV file and create the environment map
        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(strEnvPath), StandardCharsets.UTF_8))){
            int intReadRow = 0;
            String [] arrColumns = null;
            int intKeyColumn = -1;
            int intEnvCol = -1;

            while ((arrColumns = reader.readNext()) != null) {
                if (intReadRow == 0) {

                    //Validate the following keyname column and corresponding environment column available
                    for(int i=0; i < arrColumns.length; i++){

                        if(i==0)
                        {
                            if(arrColumns[i].toLowerCase().trim().equals("testenvironment")){
                                intKeyColumn = 0;
                            }
                        }
                        else {
                            if (arrColumns[i].toLowerCase().trim().equals(EnvName.toLowerCase().trim())) {
                                intEnvCol = i;
                                break;
                            }
                        }
                    }

                    //Make sure the KeyColumn is 0 and EnvCol is not -1
                    if(intKeyColumn ==-1 || intEnvCol == -1){
                        String errorMessage = "Environment data file missing key columns";
                        throw new RuntimeException(errorMessage);
                    }
                    else {
                        ps_Dic.put("TESTENVIRONMENT", arrColumns[intEnvCol]);
                    }

                }
                else {
                    ps_Dic.put(arrColumns[intKeyColumn].toUpperCase().trim(), arrColumns[intEnvCol]);
                }
                intReadRow++;
            }

        } catch (Exception e) {
            String errorMessage = String.format("Error reading CSV File: %s", strEnvPath);
            Log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }

    }
}
