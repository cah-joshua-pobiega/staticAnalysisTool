package com.cardinalhealth.veracodewrapper;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.veracode.apiwrapper.wrappers.ResultsAPIWrapper;
import com.veracode.apiwrapper.wrappers.UploadAPIWrapper;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;


public class Main {
    private static String filePath;
    private static String appName;
    private static String busUnit;
    private static String team;
    private static Integer appID;
    private static boolean getPreResults;
    private static boolean getSResults;
    private static String autoScan = "true";


    private static Set allowedExtensions;

    static {
        allowedExtensions = new HashSet<String>();
        allowedExtensions.add("zip");
        allowedExtensions.add("tar");
        allowedExtensions.add("tar.gz");
        allowedExtensions.add("tgz");
        allowedExtensions.add("ear");
        allowedExtensions.add("war");
        allowedExtensions.add("jar");
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 4) {
            appName = args[0];
            busUnit = args[1];
            team = args[2];
            filePath = args[3];
            /*
            *   This will take in exactly 4 commands to kick off the scanning process
            *   It will first attempt to verify the filepath.
            *
            * */

            if(fileVerification(filePath)) {
                UploadAPIWrapper uploadAPIWrapper = new UploadAPIWrapper();
                uploadAPIWrapper.setUpApiCredentials("154308abbd0a2ed163707c609f77e9e2", "e302ae4b4d7f0ce1940b67ce10208a5008338af26b4fa434209f77b939b7d23b0beb47e55612f832b597eaca20d8d97385a35393b0e18b41b254195dd955ef6c");
                HashMap<String, Integer> appList;
                appList = getAppList(uploadAPIWrapper, "true");
                appID = getAppIdByName(appList, appName);
                if (appID == null){
                    System.out.println("Application does not exist, Application ID not found. Application will be " +
                            "created under current business organization with default business criticality. (Medium)");
                   appID = createApp(uploadAPIWrapper, appName, "Medium", busUnit, team);
                }
                else{
                    System.out.println("Application exists, Application ID found.");

                }
                if (uploadFile(uploadAPIWrapper, appID, filePath)){
                    Integer buildID = beginPrescan(uploadAPIWrapper, appID, null, autoScan,"true");
                    if (buildID != null){
                        getPreResults = false;
                        while (!getPreResults){
                            getPreResults = getPreScanResults(uploadAPIWrapper, appID, buildID);
                            if (getPreResults) { break;}
                            TimeUnit.MINUTES.sleep(5);
                        }

                        if (getPreResults){
                            ResultsAPIWrapper resultsAPIWrapper = new ResultsAPIWrapper();
                            resultsAPIWrapper.setUpApiCredentials("154308abbd0a2ed163707c609f77e9e2", "e302ae4b4d7f0ce1940b67ce10208a5008338af26b4fa434209f77b939b7d23b0beb47e55612f832b597eaca20d8d97385a35393b0e18b41b254195dd955ef6c");
                            getSResults = false;
                            while(!getSResults){
                                getSResults = getScanResults(resultsAPIWrapper, buildID);
                                if (getSResults) {break;}
                                TimeUnit.MINUTES.sleep(5);
                            }
                            System.out.println("Results returned.");
                        }

                    }
                }


            }


        } else {
            System.out.println(" Requires 4 arguments <appname> <business unit> <team> <file> ");
            throw new Exception("Generic error code");
        }
    }


    private static boolean fileVerification(String filePath) throws Exception {
        File path = new File(filePath);
        if(path.exists()){
            String fileName = path.getName().toLowerCase();
            String ext = FilenameUtils.getExtension(fileName);
            if(!ext.isEmpty() && allowedExtensions.contains(ext)){
                return true;
            }
            else throw new Exception("File extension is not valid: Veracode only supports '.zip', '.tar', '.tar.gz'," +
                    " '.tgz' archive types. 'WAR', 'EAR', and 'jar' extensions are also supported.");

        }
        else throw new Exception("File path '" + filePath + "' is not valid. Please check the filepath.");
    }

    private static HashMap<String, Integer> getAppList(UploadAPIWrapper uploadAPIWrapper, String include_user_info) {

        HashMap<String, Integer> appList = new HashMap<>();
        try {
            //request applist xml from veracode api
            String xml = uploadAPIWrapper.getAppList(include_user_info);
            //transform xml to JSON object since we'll be working with JSON restful APIs "Soon"™
            JSONObject json = xmlToJson(xml).getJSONObject("applist");
            //add all apps to array
            JSONArray apps = (JSONArray) json.get("app");
            //take app name and appid out of apps array for easy parsing
            for (int i = 0; i < apps.length(); i++) {
                String appName = apps.getJSONObject(i).getString("app_name");
                Integer appID = apps.getJSONObject(i).getInt("app_id");
                appList.put(appName, appID);

            }

            return appList;
        } catch (IOException e){
            e.printStackTrace();
        }

        return appList;
    }

    private static Integer createApp(UploadAPIWrapper uploadAPIWrapper, String appName, String businessCriticality,
                                   String businessUnit, String team) {
        try {
            String xml = uploadAPIWrapper.createApp(appName, null, null, businessCriticality,
                    null, businessUnit, null,null, team, null,
                    null, null, null, null, null,
                    null);
            System.out.println("Application has been created.");
            autoScan = "False";
            return xmlToJson(xml).getJSONObject("appinfo").getJSONObject("application").getInt("app_id");
        } catch (IOException e){
            e.printStackTrace();
        }
        return -1;
    }

    private static boolean uploadFile(UploadAPIWrapper uploadAPIWrapper, Integer appID, String filePath)
            throws java.io.IOException {
        //check for, and remove if existing, files already uploaded since we assume only one archive will be there at a time
        getFileList(uploadAPIWrapper, appID);
        try {
            String xml = uploadAPIWrapper.uploadFile(String.valueOf(appID), filePath);
            // for debug
            // System.out.println(xml);
            String uploadStatus = xmlToJson(xml).getJSONObject("filelist").getJSONObject("file").getString("file_status");
            if (uploadStatus.equals("Uploaded")) {
                System.out.println("Application files were uploaded for " + appName + ".");
                return true;
            } else throw new IOException("Application files were not uploaded.");
        } catch (IOException e){
            getFileList(uploadAPIWrapper, appID);
            e.printStackTrace();
        }
        return false;
    }

    private static Integer beginPrescan(UploadAPIWrapper uploadAPIWrapper, Integer appID, String sandBoxID, String autoScan, String scanNonFatalModules)
            throws java.io.IOException {
        String xml = uploadAPIWrapper.beginPreScan(String.valueOf(appID), sandBoxID, autoScan, scanNonFatalModules);
        try {
            String status = xmlToJson(xml).getJSONObject("buildinfo").getJSONObject("build").getJSONObject("analysis_unit").getString("status");
            if (status.equals("Pre-Scan Submitted")){
                Integer buildID = xmlToJson(xml).getJSONObject("buildinfo").getJSONObject("build").getInt("build_id");
                return buildID;
            }
            else throw new IOException("There was an issue starting the Prescan");
        } catch (IOException e){
            e.printStackTrace();
            return -2;
        }
    }

   private static boolean getPreScanResults(UploadAPIWrapper uploadAPIWrapper, Integer appID, Integer buildID) {
       try {
           String xml = uploadAPIWrapper.getPreScanResults(String.valueOf(appID), String.valueOf(buildID));
           JSONObject results = xmlToJson(xml);
           if (results.has("error")){
               System.out.println(results);
               return false;
           }
            else {
               System.out.println(results);
               return true;
           }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    private static Integer getAppIdByName(HashMap<String, Integer> appList, String appName){
        for (Map.Entry<String, Integer> aList : appList.entrySet()){
            if (appList.containsKey(appName)){
                return aList.getValue();
            }
        }
        return null;
    }

    private static JSONObject xmlToJson(String xml){
        return XML.toJSONObject(xml);
    }

    private static boolean getFileList(UploadAPIWrapper uploadAPIWrapper, Integer appID) throws java.io.IOException {
        String xml = uploadAPIWrapper.getFileList(String.valueOf(appID));
        JSONObject check = xmlToJson(xml).getJSONObject("filelist");
        JSONArray filelist = new JSONArray();
        if (check instanceof JSONObject){
            filelist = check.toJSONArray(check.names());
        }
        else
        filelist = xmlToJson(xml).getJSONObject("filelist").getJSONArray("file");
        JSONObject json;

        // For debug
        // System.out.println(filelist);
        try {
            for (int i = 0; i > filelist.length(); i++) {
                json = filelist.getJSONObject(i);
                if (!json.has("file_md5")) {
                    String fileName = json.getJSONObject("file").getString("file_name");
                    //System.out.println(fileName);
                    String ext = FilenameUtils.getExtension(fileName);
                    if (allowedExtensions.contains(ext)) {
                        System.out.println("Found file " + fileName + ". That may have been leftover from a previous upload." +
                                " to lower scan time, this file will be removed. Removing " + fileName + "...");
                        BigInteger fileID = xmlToJson(xml).getJSONObject("filelist").getJSONObject("file").getBigInteger("file_id");
                        removeFile(uploadAPIWrapper, appID, fileID);
                        System.out.println("Removed.");
                        return true;
                    } else System.out.println("Last build found, no files to check for...continuing");
                    return false;
                } else throw new IOException("Found last build, no files currently uploaded.");
            }}
             catch(IOException e){
                e.printStackTrace();
                return false;
            }

            return false;
    }

    private static boolean removeFile(UploadAPIWrapper uploadAPIWrapper, Integer appID, BigInteger fileID){
        try {
            uploadAPIWrapper.removeFile(String.valueOf(appID), String.valueOf(fileID));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Need to figure out if it's better to use xml response or pdf. I'm leaning xml/json
    // This has to use the ResultsAPIWrapper() instead of the UploadAPIWrapper
    private static boolean getScanResults(ResultsAPIWrapper resultsAPIWrapper, Integer buildID){
        try {
            String xml = resultsAPIWrapper.detailedReport(String.valueOf(buildID));
            JSONObject results = xmlToJson(xml);
            if (results.has("detailedreport")){
                System.out.println(results);
                return true;
            }
            else {
                System.out.println(results);
                return false;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return false;
}


}
