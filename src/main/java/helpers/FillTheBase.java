package helpers;

import database.PostgreSQLManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

import static helpers.RandomManager.allowedSpecialCharacters;
import static helpers.RandomManager.alphaMixedCaseCharacters;
import static helpers.RandomManager.numbers;

/**
 * Created by numash on 16.02.2017.
 */
public class FillTheBase {
    private Random rand = new Random();

    public static void main(String[] args) throws IOException, SQLException {
        RandomManager rm = new RandomManager();
        int hours = 8;

        PostgreSQLManager pm = new PostgreSQLManager();
        pm.createMasterTable();
        pm.createPartitionFunction();

        for (int i=0; i<6; i++) {

            String username = rm.getRandomAlphaAndNumberString(8);
            int length = rm.getRandomNumberBetween(1, 140);
            String text = rm.getRandomString(length, alphaMixedCaseCharacters + numbers + allowedSpecialCharacters);

            String date = "2017-02-10 " + hours + ":15:12";
            hours++;

            pm.insertIntoPostsCertainDate(username, text, date);

        }
    }
}
