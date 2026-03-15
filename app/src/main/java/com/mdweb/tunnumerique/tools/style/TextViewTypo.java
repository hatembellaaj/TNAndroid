package com.mdweb.tunnumerique.tools.style;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.tools.SessionManager;


public class TextViewTypo extends TextView {


    private float mTypoSize = 14;
    private int mTypoStyle = 1;
    private TypeStyle typoStyle = TypeStyle.REGULAR;
    private boolean bilingual = true;

    public TextViewTypo(Context context) {
        super(context);
        init(context);
    }

    public TextViewTypo(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TextViewTypo,
                0, 0);
        try {
            mTypoSize = a.getFloat(R.styleable.TextViewTypo_sizeTypoText, 14);
            mTypoStyle = a.getInteger(R.styleable.TextViewTypo_styleTypoText, 1);
            bilingual = a.getBoolean(R.styleable.TextViewTypo_bilingual, true);
        } finally {
            a.recycle();
        }
        init(context);
    }

    public TextViewTypo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

//    public void setTextTypoSize(int style, int size) {
//        this.mTypoStyle = style;
//        this.mTypoSize = size;
//        init(co);
//    }

    private void init(Context context) {
        switch (mTypoStyle) {
            case 1:
                typoStyle = TypeStyle.BLACK;
                break;
            case 2:
                typoStyle = TypeStyle.BLACKOBLIQUE;
                break;
            case 3:
                typoStyle = TypeStyle.BOOK;
                break;
            case 4:
                typoStyle = TypeStyle.BOOKOBLIQUE;
                break;
            case 5:
                typoStyle = TypeStyle.HEAVY;
                break;
            case 6:
                typoStyle = TypeStyle.HEAVYOBLIQUE;
                break;
            case 7:
                typoStyle = TypeStyle.LIGHT;
                break;
            case 8:
                typoStyle = TypeStyle.LIGHTOBLIQUE;
                break;
            case 9:
                typoStyle = TypeStyle.MEDIUM;
                break;
            case 10:
                typoStyle = TypeStyle.MEDIUMOBLIQUE;
                break;
            case 11:
                typoStyle = TypeStyle.ROMAN;
                break;


        }
        //test if text can be bilingual or not
        if (bilingual && SessionManager.getInstance().getCurrentLang(context).equals("ar")) {
            //if french language chose roboto style
//                TypeFace.setRoboto(this, mTypoSize, typoStyle);
            TypeFace.setArabic(this, mTypoSize);
            Log.e("TypoText", "Arabic");

        } else {
            //set roboto to text with single language
            Log.e("TypoText", "French");
            TypeFace.setRoboto(this, mTypoSize, typoStyle);
        }


    }


}