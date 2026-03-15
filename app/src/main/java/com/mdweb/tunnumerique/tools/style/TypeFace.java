package com.mdweb.tunnumerique.tools.style;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class TypeFace {

    /**
     * Sets the action bar title type face.
     *
     * @param textView the new action bar title type face
     */
    public static void setActionBarTitleTypeFace(TextView textView) {

        setRoboto(textView, 0, TypeStyle.REGULAR);

    }

    /**
     * Sets the Style Roboto.
     * if the text size equal to 0 then will be use the declared size into xml file
     *
     * @param textView  the text view
     * @param textSize  the text size
     * @param typeStyle the type style
     */
    public static void setRoboto(TextView textView, float textSize,
                                 TypeStyle typeStyle) {

        Typeface typeface = Typeface.createFromAsset(textView.getContext()
                .getAssets(), "Roboto/Roboto-Regular.ttf");
        if (textSize != 0)
            textView.setTextSize(textSize);
        if (typeStyle != null) {
            if (typeStyle == TypeStyle.BOLD)
                typeface = Typeface.createFromAsset(textView.getContext()
                        .getAssets(), "Roboto/Roboto-Bold.ttf");
            else if (typeStyle == TypeStyle.REGULAR)
                typeface = Typeface.createFromAsset(textView.getContext()
                        .getAssets(), "Roboto/Roboto-Regular.ttf");
            else if (typeStyle == TypeStyle.LIGHT)
                typeface = Typeface.createFromAsset(textView.getContext()
                        .getAssets(), "Roboto/Roboto-Light.ttf");
            else if (typeStyle == TypeStyle.MEDIUM)
                typeface = Typeface.createFromAsset(textView.getContext()
                        .getAssets(), "Roboto/Roboto-Medium.ttf");
            else if (typeStyle == TypeStyle.BOOK)
                typeface = Typeface.createFromAsset(textView.getContext()
                        .getAssets(), "Roboto/AvenirLTStd-Medium.otf");
            textView.setTypeface(typeface);

        } else
            textView.setTypeface(typeface);
        textView.setPaintFlags(textView.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

    }

    /**
     * Sets the Style Roboto.
     *
     * @param textView  the text view
     * @param textSize  the text size
     * @param typeStyle the type style
     */
    public static void setRoboto(Button textView, float textSize,
                                 TypeStyle typeStyle) {

        Typeface typeface = Typeface.createFromAsset(textView.getContext()
                .getAssets(), "Roboto/Roboto-Regular.ttf");
        if (textSize != 0)
            textView.setTextSize(textSize);
        if (typeStyle != null) {
            if (typeStyle == TypeStyle.BOLD)
                typeface = Typeface.createFromAsset(textView.getContext()
                        .getAssets(), "Roboto/Roboto-Bold.ttf");
            else if (typeStyle == TypeStyle.REGULAR)
                typeface = Typeface.createFromAsset(textView.getContext()
                        .getAssets(), "Roboto/Roboto-Regular.ttf");
            else if (typeStyle == TypeStyle.LIGHT)
                typeface = Typeface.createFromAsset(textView.getContext()
                        .getAssets(), "Roboto/Roboto-Light.ttf");
            textView.setTypeface(typeface);
        } else
            textView.setTypeface(typeface, Typeface.NORMAL);
        textView.setPaintFlags(textView.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

    }




    /**
     * Sets the Style Roboto.
     *
     * @param textView  the text view
     * @param textSize  the text size
     * @param typeStyle the type style
     */
    public static void setRoboto(EditText textView, float textSize,
                                 TypeStyle typeStyle) {

        Typeface typeface = Typeface.createFromAsset(textView.getContext()
                .getAssets(), "Roboto/Roboto-Regular.ttf");
        if (textSize != 0)
            textView.setTextSize(textSize);
        if (typeStyle != null) {
            if (typeStyle == TypeStyle.BOLD)
                typeface = Typeface.createFromAsset(textView.getContext()
                        .getAssets(), "Roboto/Roboto-Bold.ttf");
            else if (typeStyle == TypeStyle.REGULAR)
                typeface = Typeface.createFromAsset(textView.getContext()
                        .getAssets(), "Roboto/Roboto-Regular.ttf");
            else if (typeStyle == TypeStyle.LIGHT)
                typeface = Typeface.createFromAsset(textView.getContext()
                        .getAssets(), "Roboto/Roboto-Light.ttf");
            textView.setTypeface(typeface);
        } else
            textView.setTypeface(typeface, Typeface.NORMAL);
        textView.setPaintFlags(textView.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

    }






    /**
     * Sets the Style Roboto.
     * if the text size equal to 0 then will be use the declared size into xml file
     *
     * @param textView the text view
     * @param textSize the text size
     */
    public static void setArabic(TextView textView, float textSize) {

        Typeface typeface = Typeface.createFromAsset(textView.getContext()
                .getAssets(), "fonts/din-next_-ar-regular.otf");
        if (textSize != 0)
            textView.setTextSize(textSize);

        textView.setTypeface(typeface);
        textView.setPaintFlags(textView.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

    }


}
