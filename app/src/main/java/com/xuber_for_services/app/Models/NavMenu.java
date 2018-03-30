package com.xuber_for_services.app.Models;

import com.xuber_for_services.app.R;

public enum NavMenu {
    HOME(R.string.menu_home),
    PAYMENT(R.string.menu_payment),
    COUPON(R.string.menu_coupon),
    WALLET(R.string.menu_wallet),
    SERVICE_HISTORY(R.string.menu_wallet),
    HELP(R.string.help),
    SHARE(R.string.menu_share),
    LOGOUT(R.string.menu_logout);

    private int stringId;

    NavMenu(int stringId) {
        this.stringId = stringId;
    }

    public int getStringId() {
        return stringId;
    }

}
