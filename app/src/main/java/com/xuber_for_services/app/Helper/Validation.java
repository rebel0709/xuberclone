package com.xuber_for_services.app.Helper;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by RAJKUMAR on 07-04-2017.
 */

public class Validation {

    public static boolean isEmail(TextView textView, String errMsg) {
        boolean result = android.util.Patterns.EMAIL_ADDRESS.matcher(
                textView.getText().toString()).matches();
        if (!result) {
            textView.setError(errMsg);
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isEmpty(EditText editText, String errMsg) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            editText.setError(errMsg);
            return true;
        }
        return false;
    }

    public static boolean checkConfirmPassword(EditText passwordTxtInput, EditText confirmPasswordTxtInput, String msg) {
        String password = passwordTxtInput.getText().toString().trim();
        String confirmPassword = confirmPasswordTxtInput.getText().toString().trim();
        if (!password.equals(confirmPassword)) {
            //errView.setVisibility(View.VISIBLE);
            confirmPasswordTxtInput.setError(msg);
            return false;
        }
        return true;
    }

    private boolean confirmPassword(EditText etPassword, EditText etConfirmPassword, String msg) {
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        if (confirmPassword.equals(password))
            return true;
        else {
            etConfirmPassword.setError(msg);
            etPassword.setError(msg);
            return false;
        }
    }

    public static boolean checkTextLength(EditText editText, String errMsg) {
        boolean result = editText.getText().toString().trim().length() < 6;
        if (result) {
            editText.setError(errMsg);
            return false;
        }
        return true;
    }

    public static boolean checkPasswordPattern(EditText passwordTxtInput, String msg) {
        String password = passwordTxtInput.getText().toString().trim();
        String pwdPattern = "((?=.*\\d)(?=.*[-^!&@#$]).{8,15})";
        Pattern pattern = Pattern.compile(pwdPattern);
        //Log.e("pattern", pattern + "");
        Matcher matcher = pattern.matcher(password);
        if (!matcher.matches()) {
            //Log.e("the pattern matches", "true password");
            passwordTxtInput.setError(msg);
            return false;
        }
        return true;
    }
}
