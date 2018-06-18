/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doanspammail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tra Tran
 */
public class Processing {

    public static String ReadFile(String FileName) throws FileNotFoundException {
        String str = new String();
        FileInputStream fis = new FileInputStream(FileName);
        Scanner scanner = new Scanner(fis);
        while (scanner.hasNextLine()) {
            str = str + "\r\n" + scanner.nextLine();
        }
        scanner.close();
        return str;
    }

    public static boolean isNumeric(String s) {
        return java.util.regex.Pattern.matches("\\d+", s);
    }

    //Tách từ trong văn bản
    public static ArrayList<String> SplitText(String text) {
        ArrayList<String> l = new ArrayList<String>();
        text = text.replaceAll("\r", "").replaceAll("\n", "");

        String[] array = text.trim().split("\\W+");
        //String[] array = text.trim().split("\\d+");

        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                char[] sc = array[i].toCharArray();
                String temp = "";

                //Chuẩn hóa về chữ thường
                if (array[i].length() == 0) {

                } else {
                    for (int j = 0; j < array[i].length(); j++) {
                        int ascii = sc[j];

                        if (ascii >= 65 && ascii <= 90) {
                            temp += (char) (ascii += 32);
                        } else {
                            temp += (char) ascii;
                        }
                    }
                    if (isNumeric(temp) == false) {
                        l.add(temp);
                    }
                }
            }
            //array[i].toUpperCase();
            //l.add(array[i]);
        }
        return l;
    }
    //Xóa các từ trùng lặp trong listWord

    public static ArrayList<String> RemoveDuplicate(ArrayList<String> listWord) {
        //ArrayList<String> l = new ArrayList<String>();
        //List<String> al = new ArrayList<>();

        Set<String> hs = new HashSet<>();
        hs.addAll(listWord);
        listWord.clear();
        listWord.addAll(hs);

        return listWord;
    }

    public static double probabilityOfHam(ArrayList<String> listWordHam, ArrayList<String> listWordFull, String word) {

        double k = 0;
        for (int i = 0; i < listWordHam.size(); i++) {
            if (listWordHam.get(i).contains(word)) // moi lan x xuat hien trong 1 thu rac thi k++
            {
                k++;
            }
        }
        return (k + 1) / (listWordHam.size() + listWordFull.size());
    }

    public static double probabilityOfSpam(ArrayList<String> listWordSpam, ArrayList<String> listWordFull, String word) {

        double k = 0;
        for (int i = 0; i < listWordSpam.size(); i++) {
            if (listWordSpam.get(i).contains(word)) // moi lan x xuat hien trong 1 thu rac thi k++
            {
                k++;
            }
        }
        return (k + 1) / (listWordSpam.size() + listWordFull.size());
    }

    public static int check(ArrayList<String> listWordInput, ArrayList<String> listWordFull,
            ArrayList<String> listWordHam, ArrayList<String> listWordSpam, double numFileHam, double numFileSpam) {
        //listWordFull = p.RemoveDuplicate(listWordFull);
        double pHam = ((double) numFileHam / (double) (numFileHam + numFileSpam));
        double pSpam = ((double) numFileSpam / (double) (numFileHam + numFileSpam));
        double h = pHam;
        double s = pSpam;
        for (String item : listWordInput) {
            h = (double) (1000 * h * probabilityOfHam(listWordHam, listWordFull, item));
            s = (double) (1000 * s * probabilityOfSpam(listWordSpam, listWordFull, item));
        }
        if (h >= s) {
            //return "Không phải thư rác!" + "\r\n" + Double.toString(h);
            //return "Không phải thư rác!";
            return 0;
        } else {
            //return "Là thư rác" + "\r\n" + Double.toString(s);
            //return "Là thư rác" + "\r\n";
            return 1;
        }
    }

    public static double precision(String dir, String ss, ArrayList<String> listWordFull,
            ArrayList<String> listWordHam, ArrayList<String> listWordSpam, double numFileHam, double numFileSpam) {

        ArrayList<String> listWordTemp = new ArrayList<String>();
        File folder = new File(dir);
        File[] listOfFiles = folder.listFiles();
        String s = "";
        double tp = 0;
        double fp = 0;
        if (ss == "Ham") {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    try {
                        listWordTemp = SplitText(s + ReadFile(dir + "\\" + file.getName()));
                        if (check(listWordTemp, listWordFull, listWordHam, listWordSpam, numFileHam, numFileSpam) == 0) {
                            tp++;
                        }
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(SpamMail.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } if (ss == "Spam") {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    try {
                        listWordTemp = SplitText(s + ReadFile(dir + "\\" + file.getName()));
                        if (check(listWordTemp, listWordFull, listWordHam, listWordSpam, numFileHam, numFileSpam) == 1) {
                            tp++;
                        }
                        //listWord = p.SplitText(text);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(SpamMail.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return tp;
    }

    public static ArrayList<String> stopwordRemoval(ArrayList<String> Stopword, ArrayList<String> Input) {
        ArrayList<String> l = new ArrayList<>();
        for (String item : Input) {
            if (Stopword.contains(item) == true) {
                l.add(item);
            }
        }
        return l;
    }

    public static void WriteFile(String path, ArrayList<String> l) throws IOException {
        File file = new File(path);
        FileWriter writer = new FileWriter(file);
        int i = 0;
        for (String s : l) {
            writer.write(s + "\r\n");
            i++;
        }
        writer.write(Integer.toString(i));
        writer.close();
    }

    public static ArrayList<String> loadData(String path) {
        ArrayList<String> l = new ArrayList<String>();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        int i = 0;
        String text = "";
        for (File file : listOfFiles) {
            if (file.isFile()) {
                try {
                    i++;//Số file trong thư mục Ham
                    text += ReadFile(path + "\\" + file.getName());
                    //listWord = p.SplitText(text);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(SpamMail.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        l = SplitText(text);
        return l;
    }

    public static int countFile(String path) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        int i = 0;
        String text = "";
        for (File file : listOfFiles) {
            if (file.isFile()) {
                i++;//Số file trong thư mục Ham                   
            }
        }
        return i;
    }

    public static String lemmatization(String word) throws FileNotFoundException {
        ArrayList<String> as = new ArrayList<String>();
        String s = word;
        FileInputStream fis = new FileInputStream(".\\lemmatization-database.txt");
        Scanner scanner = new Scanner(fis);
        //int flag = 0;
        while (scanner.hasNextLine()) {
            //str = str + "\r\n" + scanner.nextLine();
            //as.add(scanner.nextLine());
            String[] array = scanner.nextLine().trim().split("\\s*[^a-zA-Z']+\\s*");
            for (int i = 0; i < array.length; i++) {
                if (array[i].equals(word)) {
                    //s = array[0];
                    //flag++;
                    return array[0];
                }
            }
        }
        scanner.close();
        //return false;
        return s;
    }
}
