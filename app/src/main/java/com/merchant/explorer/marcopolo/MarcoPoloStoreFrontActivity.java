package com.merchant.explorer.marcopolo;

import android.support.v4.app.Fragment;

/**
 * Created by user on 27/12/2017.
 */

public class MarcoPoloStoreFrontActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return MarcoPoloStoreFrontFragment.newInstance();
    }
}
