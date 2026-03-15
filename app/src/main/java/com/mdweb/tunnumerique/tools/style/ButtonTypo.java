package com.mdweb.tunnumerique.tools.style;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;

import com.mdweb.tunnumerique.R;


public class ButtonTypo extends Button {

    private float mTypoSize = 14;
    private int mTypoStyle = 1;
    private TypeStyle typoStyle = TypeStyle.REGULAR;

    public ButtonTypo(Context context) {
        super(context);
        init();

    }

    public ButtonTypo(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ButtonTypo,
                0, 0);
        try {
            mTypoSize = a.getFloat(R.styleable.ButtonTypo_sizeTypo, 14);
            mTypoStyle = a.getInteger(R.styleable.ButtonTypo_styleTypo, 1);
        } finally {
            a.recycle();
        }


        init();
    }

    public ButtonTypo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }
    private void init(){


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
            //chose roboto style
            TypeFace.setRoboto(this, mTypoSize, typoStyle);


    }


}
