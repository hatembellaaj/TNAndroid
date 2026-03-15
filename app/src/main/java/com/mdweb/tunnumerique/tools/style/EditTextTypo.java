package com.mdweb.tunnumerique.tools.style;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

import com.mdweb.tunnumerique.R;


public class EditTextTypo extends EditText {


    private float mTypoSize = 14;
    private int mTypoStyle = 1;
    private TypeStyle typoStyle = TypeStyle.REGULAR;

    public EditTextTypo(Context context) {
        super(context);
        init();
    }

    public EditTextTypo(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TextViewTypo,
                0, 0);
        try {
            mTypoSize = a.getFloat(R.styleable.TextViewTypo_sizeTypoText, 14);
            mTypoStyle = a.getInteger(R.styleable.TextViewTypo_styleTypoText, 1);
        } finally {
            a.recycle();
        }
        init();
    }

    public EditTextTypo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        switch (mTypoStyle) {
            case 1:
                typoStyle = TypeStyle.REGULAR;
                break;


            case 2:
                typoStyle = TypeStyle.BOLD;
                break;

            case 3:
                typoStyle = TypeStyle.LIGHT;
                break;


        }
        TypeFace.setRoboto(this, mTypoSize, typoStyle);

    }
}
