package jp.meridiani.apps.volumeprofile;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by hashiz on 2015/12/30.
 */
public class MessageText {
    private ArrayList<String> mText = new ArrayList<String>();
    private String mSeparator;

    public MessageText(String separator) {
        mSeparator = separator;
    }

    public MessageText(String separator, String text) {
        this(separator);
        this.addText(text);
    }

    public void addText(String text) {
        mText.add(text);
    }

    public void setSeparator(String separator) {
        mSeparator = separator;
    }

    public String getSeparator() {
        return mSeparator;
    }

    public String getText() {
        Iterator<String> i = mText.iterator();
        StringBuffer buf = new StringBuffer();
        if (i.hasNext()) {
            buf.append(i.next());
        }
        while (i.hasNext()) {
            buf.append(getSeparator());
            buf.append(i.next());
        }
        return buf.toString();
    }

    @Override
    public String toString() {
        return getText();
    }
}
