package encryptdecrypt;
import java.io.*;
import java.util.Locale;
import java.util.Scanner;
public class Main {
    private static final String[] commands = {"-mode", "-key", "-data", "-in", "-out", "-alg"};
    private static boolean encryptMode = true;
    private static int key = 0;
    private static String message = "";
    private static String outName = "";
    private static String inName = "";
    private static int isCommand(String[] args, String command) {
        int index = -1;
        for (int i =0; i < args.length; i++) {
            if (args[i].equals(command)) {
                index = i;
            }
        }
        return index;
    }

    private static void printOut(String str) {
        for (char ch: str.toCharArray()) {
            printOut(ch);
        }
    }

    private static void printOut(char ch) {
        if (outName.isEmpty()) {
            System.out.print(ch);
        } else {
            File file = new File(outName);
            try (FileWriter writer = new FileWriter(file, true)){
                writer.append(ch);
            } catch (IOException e) {
                System.out.println("Error");
            }
        }
    }
    
    public static void main(String[] args) {
        Crypt crypt = null;
        for (int i = 0; i < commands.length; i++) {
            int index = isCommand(args, commands[i]);
            if (index >= 0) {
                switch (i) {
                    case 0: {
                        encryptMode = args[index + 1].equals("enc");
                        break;
                    }
                    case 1: {
                        key = Integer.parseInt(args[index + 1]);
                        break;
                    }
                    case 2: {
                        message = args[index + 1];
                        break;
                    }
                    case 3: {
                        if (message.isEmpty()) {
                            inName = args[index + 1];
                        }
                        break;
                    }
                    case 4:{
                        outName = args[index + 1];
                        File file = new File(outName);
                        try (FileWriter writer = new FileWriter(file)){
                        } catch (IOException e) {
                            System.out.println("Error");
                        }
                        break;
                    }
                    case 5:{
                        if ("shift".equals(args[index + 1].toLowerCase())) {
                            crypt = new ShiftCrypt(key);
                        } else if ("unicode".equals(args[index + 1].toLowerCase())) {
                            crypt = new UnicodeCrypt(key);
                        } else {
                            System.out.println("Unknown crypt algorithm");
                        }
                        break;
                    }
                }
            }
        }
        if (crypt == null) {
            crypt = new ShiftCrypt(key);
        }
        if (encryptMode) {
            if (inName.isEmpty()) {
                for (int i = 0; i < message.length(); i++) {
                    printOut(crypt.encrypt(message.charAt(i)));
                }
            } else {
                try (Scanner scanner = new Scanner(new File(inName))) {
                    while (scanner.hasNext()) {
                        message = scanner.nextLine();
                        for (int i = 0; i < message.length(); i++) {
                            printOut(crypt.encrypt(message.charAt(i)));
                        }
                    }
                }  catch (IOException e) {
                    printOut("Error");
                }
            }
        } else {
            if (inName.isEmpty()) {
                for (int i = 0; i < message.length(); i++) {
                    printOut(crypt.decrypt(message.charAt(i)));
                }
            } else {
                File file = new File(inName);
                try (FileReader reader = new FileReader(file)) {
                    while (reader.ready()) {
                        printOut(crypt.decrypt((char)reader.read()));
                    }
                }  catch (IOException e) {
                    printOut("Error");
                }
            }
        }
        printOut("\n");

    }
}
interface Crypt {
    char encrypt(char ch);
    char decrypt(char ch);
}

class ShiftCrypt implements Crypt {
    private String cryptWord = "abcdefghijklmnopqrstuvwxyz";
    private int key;
    public ShiftCrypt(int key) {
        this.key = key;
    }
    @Override
    public char encrypt(char ch) {
        int index = cryptWord.indexOf(ch);
        char result = ch;
        if (index >= 0) {
            result = cryptWord.charAt((index + key) % cryptWord.length());
        }
        return result;
    }

    @Override
    public char decrypt(char ch) {
        int index = cryptWord.indexOf(ch);
        char result = ch;
        if (index >= 0) {
            result = cryptWord.charAt((cryptWord.length() + index - key) % cryptWord.length());
        }
        return result;
    }
}

class UnicodeCrypt implements Crypt {
    private int key;
    public UnicodeCrypt(int key) {
        this.key = key;
    }
    @Override
    public char encrypt(char ch) {
        return (char) (ch + key);
    }

    @Override
    public char decrypt(char ch) {
        return (char) (ch - key);
    }
}
