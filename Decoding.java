import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Decoding {
    static final String SIMBOLS = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя.,\":-!? ";
    static String pathSource = "TextAfterEncryption.txt";
    static String pathDestination = "TextAfterEncryptionAndDecryption.txt";

    public static void decoding(int key) {
        try (RandomAccessFile raf = new RandomAccessFile(pathSource, "rw");
             FileChannel fc = raf.getChannel()) {
            ByteBuffer bb = ByteBuffer.allocate((int) fc.size());

            fc.read(bb);
            bb.flip();

            Charset charset = StandardCharsets.UTF_8;
            CharBuffer ch = charset.decode(bb);

            StringBuilder output = new StringBuilder();

            for (int i = 0; i < ch.length(); i++) {
                char c = ch.charAt(i);
                if (SIMBOLS.indexOf(c) != -1) {
                    int newIndex = (SIMBOLS.indexOf(c) - key + SIMBOLS.length()) % SIMBOLS.length();
                    output.append(SIMBOLS.charAt(newIndex));
                } else {
                    output.append(c);
                }
            }

            Path outputPath = Paths.get(pathDestination);
            Files.createFile(outputPath);
            Files.writeString(outputPath, output);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void brutForce() {
        try (RandomAccessFile raf = new RandomAccessFile(pathSource, "rw");
             FileChannel fc = raf.getChannel()) {
            ByteBuffer bb = ByteBuffer.allocate((int) fc.size());

            fc.read(bb);
            bb.flip();

            Charset charset = StandardCharsets.UTF_8;
            CharBuffer ch = charset.decode(bb);

            for (int key = 1; key <= 20; key++) {
                StringBuilder output = new StringBuilder();
                for (int i = 0; i < ch.length(); i++) {
                    char c = ch.charAt(i);
                    if (SIMBOLS.indexOf(c) != -1) {
                        int newIndex = (SIMBOLS.indexOf(c) - key + SIMBOLS.length()) % SIMBOLS.length();
                        output.append(SIMBOLS.charAt(newIndex));
                    } else {
                        output.append(c);
                    }
                }
                if (isDecryptionValid(String.valueOf(output))) {
                    Path outputPath = Paths.get(pathDestination);
                    Files.createFile(outputPath);
                    Files.writeString(outputPath, output);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isDecryptionValid(String output) {
        String[] searchWordsAndSigns = {" в ", " на ", " без ", " до ", " для ", " за ", " через ", " над ", " по ", " из ", " у ", " около ",
                " под ", " о ", " про ", " к ", " перед ", " при ", " с ", " между ", ", ", ". ", "! ", "? "};
        int minWords = 20;
        int count = 0;
        for (String wordAndSigns : searchWordsAndSigns) {
            int index = 0;
            while (index != -1) {
                index = output.indexOf(wordAndSigns, index);
                if (index == -1) {
                    break;
                }
                count++;
                index += wordAndSigns.length();
            }
        }
        if (count >= minWords) {
            return true;
        }
        return false;
    }
}