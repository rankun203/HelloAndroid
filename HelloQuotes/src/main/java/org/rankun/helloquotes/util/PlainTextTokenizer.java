package org.rankun.helloquotes.util;

/**
 * Created by rankun203 on 14-4-22
 */

import android.widget.MultiAutoCompleteTextView;

/**
 * This simple Tokenizer can be used for lists where the items are
 * separated by a comma and one or more spaces.
 */
public class PlainTextTokenizer implements MultiAutoCompleteTextView.Tokenizer {
    public int findTokenStart(CharSequence text, int cursor) {
        return 0;
    }

    public int findTokenEnd(CharSequence text, int cursor) {
        return text.length();
    }

    public CharSequence terminateToken(CharSequence text) {
        int i = text.length();

        while (i > 0 && text.charAt(i - 1) == ' ') {
            i--;
        }

        return text;
    }
}