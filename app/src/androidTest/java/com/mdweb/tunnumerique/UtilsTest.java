package com.mdweb.tunnumerique;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.mdweb.tunnumerique.tools.Utils;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UtilsTest {
    @Test
    public void TestcheckSurvey() throws Exception {
        Context appContext = InstrumentationRegistry.getContext();
        Utils utils = new Utils(appContext);
        utils.checkSurvey();
    }
}
