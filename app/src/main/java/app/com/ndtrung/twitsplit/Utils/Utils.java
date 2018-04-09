package app.com.ndtrung.twitsplit.Utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class Utils {
    private static final int WIDTH_INDEX = 0;
    private static final int HEIGHT_INDEX = 1;

    public static int[] getScreenSize(Context context) {
        int[] widthHeight = new int[2];
        widthHeight[WIDTH_INDEX] = 0;
        widthHeight[HEIGHT_INDEX] = 0;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        widthHeight[WIDTH_INDEX] = size.x;
        widthHeight[HEIGHT_INDEX] = size.y;

        if (!isScreenSizeRetrieved(widthHeight)) {
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            widthHeight[0] = metrics.widthPixels;
            widthHeight[1] = metrics.heightPixels;
        }

        // Last defense. Use deprecated API that was introduced in lower than API 13
        if (!isScreenSizeRetrieved(widthHeight)) {
            widthHeight[0] = display.getWidth(); // deprecated
            widthHeight[1] = display.getHeight(); // deprecated
        }

        return widthHeight;
    }

    private static boolean isScreenSizeRetrieved(int[] widthHeight) {
        return widthHeight[WIDTH_INDEX] != 0 && widthHeight[HEIGHT_INDEX] != 0;
    }

    private static String checkAddingNextString(String nextString, String currentTweet) {
        return currentTweet.isEmpty() ? nextString : (currentTweet + Constants.ONE_SPACE_CHARACTER + nextString);
    }

    // Calculate a total number of tweet parts. It's just an estimation.
    private static long calculateTotalTweet(String arr[]) {
        long numberOfCharacter = 0;
        for (int i = 0; i < arr.length; i++) {
            numberOfCharacter += arr[i].length() + ((i < (arr.length - 1)) ? 1 : 0);
        }
        return ((numberOfCharacter / Constants.MAX_CHARACTERS_NUMBER) + 1);
    }

    public static String[] putStringToStringArray(String currentArray[], String str) {
        String newArray[] = new String[currentArray.length + 1];
        for (int i = 0; i < currentArray.length; i++) {
            newArray[i] = currentArray[i];
        }
        newArray[currentArray.length] = str;
        return newArray;
    }

    public static String[] splitMessage(String arr[]) {
        String resultArr[];
        long totalTweets = calculateTotalTweet(arr); // Total number of tweet parts.
        int numberOfTweet; // current tweet part number per total.
        String tweet; // tweet content.
        int i; // index of array
        boolean isFinished = false;

        do {
            resultArr = new String[0];
            numberOfTweet = 0; //
            i = 0;
            tweet = (numberOfTweet + 1) + "/" + totalTweets;

            while (i < arr.length) {
                tweet += Constants.ONE_SPACE_CHARACTER + arr[i];
                if (tweet.length() > Constants.MAX_CHARACTERS_NUMBER) {
                    return new String[]{}; // empty
                }
                // if array does not have any elements OR current content can't add more text
                if ((i + 1) >= arr.length || checkAddingNextString(arr[i + 1], tweet).length() > Constants.MAX_CHARACTERS_NUMBER) {
                    resultArr = putStringToStringArray(resultArr, tweet);
                    numberOfTweet++;
                    tweet = (numberOfTweet + 1) + "/" + totalTweets;
                }
                i++;
            }

            // if numberOfTweet and totalTweets, is not equal. do above while loop again.
            if (numberOfTweet != totalTweets) {
                totalTweets = numberOfTweet;
            } else {
                isFinished = true;
            }
        } while (!isFinished);

        return resultArr;
    }

    public static boolean checkInvalidString(String arr[]) {
        for (String item : arr) {
            if (item.length() > Constants.MAX_CHARACTERS_NUMBER) {
                return false;
            }
        }
        return true;
    }
}
