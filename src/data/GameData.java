package data;

import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by kkyeo on 11/12/2016.
 */
public class GameData {
    private String ID;
    private String PW;
    private boolean loggedin;
    private boolean createdProfile;
    private String gameMode;
    private long gameLevel;
    private long maxLevel4Edic;
    private long maxLevel4FirstName;
    private long maxLevel4LastName;
    private long maxLevel4Shakespeare;
    private long maxPoints4EachEdic[] = new long[8]; // 각 모드별 level 최대 획득 점수.
    private long maxPoints4EachFirstName[] = new long[8];
    private long maxPoints4EachLastName[] = new long[8];
    private long maxPoints4EachShakespeare[] = new long[8];

    //Integrated Dictionary Management
    private ArrayList<String> hiddenWordsList = new ArrayList<String>(); //Grid 에 담긴 단어 목록
    private ArrayList<String> answerList = new ArrayList<String>(); //정답 맞춘 목록
    private HashSet<String> hashSet = new HashSet<String>(); // Dictionary 모든 단어 중복하지않게 목록 저장하는 곳//빠른탐색가능

    //set and reset hashSet
    public void dictionary2hashSet(){

     if (hashSet.size()>0){
         hashSet.clear(); //hashset 비우기
     }

        BufferedReader br = null;

        try {
            String sCurrentLine;
            switch (gameMode)
            {
                case "English Dictionary" :  br = new BufferedReader(new FileReader("././resources/words/English Dictionary.txt")); break;
                case "First Name" :  br = new BufferedReader(new FileReader("././resources/words/First Name.txt"));break;
                case "Last Name" :  br = new BufferedReader(new FileReader("././resources/words/Last Name.txt"));break;
                case "Shakespeare" :  br = new BufferedReader(new FileReader("././resources/words/Shakespeare.txt"));
            }
            while ((sCurrentLine = br.readLine()) != null)
                hashSet.add(sCurrentLine);


            System.out.println("Loaded dictionary for "+gameMode +", Dictionary size : "+hashSet.size());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (br != null)
                br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    public HashSet<String> getHashSet() {return hashSet;}

    public void initializeGamedata(){
        gameMode = "English Dictionary"; //가장 최근에 했던(implicitly game play 할 모드)
        loggedin = false;
        createdProfile = false;
    }

    public void loadUserInfo(String ID){

        try {
            dictionary2hashSet(); //단어 목록 구성
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader("././saved/"+ ID + ".json"));
            JSONObject jsonObject = (JSONObject)obj;

            this.ID = (String) jsonObject.get("Name");
            PW = (String) jsonObject.get("Password");
            gameMode = (String) jsonObject.get("Recent Mode");   //가장 최근에 했던(implicitly game play 할 모드)
            maxLevel4Edic = (long) jsonObject.get("Max Level on English Dictionary");
            maxLevel4FirstName = (long) jsonObject.get("Max Level on First Name");
            maxLevel4LastName = (long) jsonObject.get("Max Level on Last Name");
            maxLevel4Shakespeare = (long) jsonObject.get("Max Level on Shakespeare");
            JSONArray EachEdic = (JSONArray) jsonObject.get("Max Points of English Dictionary");
            JSONArray EachFirstName = (JSONArray) jsonObject.get("Max Points of First Name");
            JSONArray EachLastName = (JSONArray) jsonObject.get("Max Points of Last Name");
            JSONArray EachShakespeare = (JSONArray) jsonObject.get("Max Points of Shakespeare");

            setMaxPoints4EachMode(maxPoints4EachEdic, EachEdic);
            setMaxPoints4EachMode(maxPoints4EachFirstName, EachFirstName);
            setMaxPoints4EachMode(maxPoints4EachLastName, EachLastName);
            setMaxPoints4EachMode(maxPoints4EachShakespeare, EachShakespeare);
            loggedin = true;
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void renewUserInfo(){

        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader("././saved/"+ ID + ".json"));
            JSONObject jsonObject = (JSONObject)obj;
            PW = (String) jsonObject.get("Password");
            gameMode = (String) jsonObject.get("Recent Mode");   //가장 최근에 했던(implicitly game play 할 모드)
            maxLevel4Edic = (long) jsonObject.get("Max Level on English Dictionary");
            maxLevel4FirstName = (long) jsonObject.get("Max Level on First Name");
            maxLevel4LastName = (long) jsonObject.get("Max Level on Last Name");
            maxLevel4Shakespeare = (long) jsonObject.get("Max Level on Shakespeare");
            JSONArray EachEdic = (JSONArray) jsonObject.get("Max Points of English Dictionary");
            JSONArray EachFirstName = (JSONArray) jsonObject.get("Max Points of First Name");
            JSONArray EachLastName = (JSONArray) jsonObject.get("Max Points of Last Name");
            JSONArray EachShakespeare = (JSONArray) jsonObject.get("Max Points of Shakespeare");

            setMaxPoints4EachMode(maxPoints4EachEdic, EachEdic);
            setMaxPoints4EachMode(maxPoints4EachFirstName, EachFirstName);
            setMaxPoints4EachMode(maxPoints4EachLastName, EachLastName);
            setMaxPoints4EachMode(maxPoints4EachShakespeare, EachShakespeare);


        }
        catch(Exception e){
        }
    }

    public void setMaxPoints4EachMode(long[] eachMode, JSONArray jsonArray){
        for (int i =0; i <8 ; i++){
            eachMode[i] = (long)jsonArray.get(i);
        }
    }

    public long getmaxLevel4LastName() {return maxLevel4LastName;}
    public long getmaxLevel4FirstName() {
        return maxLevel4FirstName;
    }
    public long getmaxLevel4Shakespeare() {
        return maxLevel4Shakespeare;
    }
    public long getMaxLevel4Edic() {return maxLevel4Edic;}


    public String getID() {
        return ID;
    }

    public long getGameLevel() {
        return gameLevel;
    }
    public void setGameLevel(long gameLevel) {
        this.gameLevel = gameLevel;
    }
    public String getGameMode() {
        return gameMode;
    }
    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }
    public void setCreatedProfile(boolean createdProfile) {this.createdProfile = createdProfile;}
    public void setLoggedin(boolean loggedin) {
        this.loggedin = loggedin;
    }
    public Boolean getLoggedin(){
        return loggedin;
    }
    public Boolean getcreatedProfile() {
        return createdProfile;
    }

    public String getPW() {
        return PW;
    }

    public long[] getMaxPoints4EachEdic() {
        return maxPoints4EachEdic;
    }

    public long[] getMaxPoints4EachFirstName() {
        return maxPoints4EachFirstName;
    }

    public long[] getMaxPoints4EachLastName() {
        return maxPoints4EachLastName;
    }

    public long[] getMaxPoints4EachShakespeare() {
        return maxPoints4EachShakespeare;
    }

    public ArrayList<String> getAnswerList() {
        return answerList;
    }
    public ArrayList<String> getHiddenWordsList() {
        return hiddenWordsList;
    }


}
