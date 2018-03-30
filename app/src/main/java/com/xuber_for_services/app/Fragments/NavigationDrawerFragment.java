package com.xuber_for_services.app.Fragments;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.xuber_for_services.app.Helper.SharedHelper;
import com.xuber_for_services.app.Listener.NavUpdateListener;
import com.xuber_for_services.app.Models.NavMenu;
import com.xuber_for_services.app.R;
import com.xuber_for_services.app.Utils.Utilities;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * NavigationDrawerFragment used in Admi Mode.
 * usage : used to navigate between screens within the App.
 * the listener is implement.
 * Dealer Navigation Drawer
 */
public class NavigationDrawerFragment extends Fragment implements View.OnClickListener, NavUpdateListener {

    public static final String TAG = "NavDrawerFgmt";

    private CircleImageView mUserProfileImg;
    private TextView mNameTxt;
    private TextView mEmailTxt;

    private Button homeBtn, paymentBtn, couponBtn, walletBtn, historyBtn, shareBtn, logoutBtn, help_btn;

    private ImageView imgHome, imgPayment, imgCoupon, imgWallet, imgServiceHistory, imgShare, imgLogout, imgHelp;

    private RelativeLayout headerLayout;
    private ProgressBar mImgProgressBar;
    // private RelativeLayout headerLayout;

    private NavMenu mNavMenuItems = NavMenu.HOME;

    private NavDrawerFgmtListener mListener;
    private DrawerLayout mDrawerLayout;
    private View mNavigationView;

    public NavigationDrawerFragment() {
    }

    public static NavigationDrawerFragment newInstance() {
        NavigationDrawerFragment fragment = new NavigationDrawerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavDrawerFgmtListener) {
            mListener = (NavDrawerFgmtListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        findViewsById(view);
        setClickListeners();
        setBasicDetails();
        if (savedInstanceState == null)
            setBtnStates(homeBtn);
        else
            checkBtnStates();
        return view;
    }


    private void findViewsById(View view) {
        mUserProfileImg = (CircleImageView) view.findViewById(R.id.circleView);
        mNameTxt = (TextView) view.findViewById(R.id.name);


        String strName = SharedHelper.getKey(getContext(), "first_name") + " " + SharedHelper.getKey(getContext(), "last_name");
        mNameTxt.setText(strName);

        if (!SharedHelper.getKey(getContext(), "picture").equalsIgnoreCase("")) {
            Picasso.with(getContext()).load(Utilities.getImageURL(SharedHelper.getKey(getContext(), "picture")))
                    .error(R.drawable.ic_dummy_user).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.ic_dummy_user)
                    .centerCrop()
                    .fit()
                    .into(mUserProfileImg);
        }

        homeBtn = (Button) view.findViewById(R.id.home_btn);
        paymentBtn = (Button) view.findViewById(R.id.payment_btn);
        couponBtn = (Button) view.findViewById(R.id.coupon_btn);
        walletBtn = (Button) view.findViewById(R.id.wallet_btn);
        historyBtn = (Button) view.findViewById(R.id.history_btn);
        shareBtn = (Button) view.findViewById(R.id.share_btn);
        logoutBtn = (Button) view.findViewById(R.id.logout_btn);
        help_btn = (Button) view.findViewById(R.id.help_btn);
        headerLayout = (RelativeLayout) view.findViewById(R.id.navigation_header);

        imgPayment = (ImageView) view.findViewById(R.id.imgPayment);
        imgHome = (ImageView) view.findViewById(R.id.imgHome);
        imgCoupon = (ImageView) view.findViewById(R.id.imgCoupon);
        imgWallet = (ImageView) view.findViewById(R.id.imgWallet);
        imgServiceHistory = (ImageView) view.findViewById(R.id.imgServiceHistory);
        imgShare = (ImageView) view.findViewById(R.id.imgShare);
        imgLogout = (ImageView) view.findViewById(R.id.imgLogout);
        imgHelp = (ImageView) view.findViewById(R.id.imgHelp);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUserProfileImg = null;
        mNameTxt = null;
        mEmailTxt = null;
        homeBtn = null;
        paymentBtn = null;
        couponBtn = null;
        walletBtn = null;
        historyBtn = null;
        shareBtn = null;
        logoutBtn = null;
        headerLayout = null;
        help_btn = null;
        //mImgProgressBar = null;
    }

    private void setClickListeners() {
        homeBtn.setOnClickListener(this);
        paymentBtn.setOnClickListener(this);
        couponBtn.setOnClickListener(this);
        walletBtn.setOnClickListener(this);
        historyBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        help_btn.setOnClickListener(this);
        headerLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (isDrawerOpen())
            closeDrawer();
        if (v == homeBtn) {
            setBtnStates(homeBtn);
            mNavMenuItems = NavMenu.HOME;
        } else if (v == paymentBtn) {
            setBtnStates(paymentBtn);
            mNavMenuItems = NavMenu.PAYMENT;
        } else if (v == couponBtn) {
            setBtnStates(couponBtn);
            mNavMenuItems = NavMenu.COUPON;
        } else if (v == walletBtn) {
            setBtnStates(walletBtn);
            mNavMenuItems = NavMenu.WALLET;
        } else if (v == historyBtn) {
            setBtnStates(historyBtn);
            mNavMenuItems = NavMenu.SERVICE_HISTORY;
        } else if (v == help_btn) {
            setBtnStates(help_btn);
            mNavMenuItems = NavMenu.HELP;
        } else if (v == shareBtn) {
//          setBtnStates(shareBtn);
            mNavMenuItems = NavMenu.SHARE;
        } else if (v == logoutBtn) {
            mNavMenuItems = NavMenu.LOGOUT;
        }
        if (v == headerLayout) {
            mListener.headerClicked();
            mNavMenuItems = null;
        }
        if (mNavMenuItems != null)
            mListener.menuClicked(mNavMenuItems);

        if (v == headerLayout) {
            mListener.headerClicked();
        }

    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mNavigationView);
    }

    public void closeDrawer() {
        if (mDrawerLayout != null && isDrawerOpen()) {
            mDrawerLayout.closeDrawer(mNavigationView);
        }
    }

    public void openDrawer() {
        if (mDrawerLayout != null && !isDrawerOpen()) {
            mDrawerLayout.openDrawer(mNavigationView);
        }
    }

    public void setupDrawer(int fragmentId, final DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
        mNavigationView = getActivity().findViewById(fragmentId);
        mListener.menuClicked(NavMenu.HOME);
    }

    public void checkBtnStates() {
        if (mNavMenuItems != null) {
            if (mNavMenuItems.equals(NavMenu.HOME)) {
                setBtnStates(homeBtn);


            }
            if (mNavMenuItems.equals(NavMenu.PAYMENT)) {
                setBtnStates(paymentBtn);

            }
            if (mNavMenuItems.equals(NavMenu.COUPON)) {
                setBtnStates(couponBtn);

            }
            if (mNavMenuItems.equals(NavMenu.WALLET)) {
                setBtnStates(walletBtn);

            }
            if (mNavMenuItems.equals(NavMenu.SERVICE_HISTORY)) {
                setBtnStates(historyBtn);
            }
            if (mNavMenuItems.equals(NavMenu.HELP)) {
                setBtnStates(help_btn);
            }
            if (mNavMenuItems.equals(NavMenu.SHARE)) {
                setBtnStates(shareBtn);
            }
            if (mNavMenuItems.equals(NavMenu.LOGOUT)) {
                setBtnStates(logoutBtn);
            }
        }
    }

    private void setBtnStates(Button button) {
        homeBtn.setSelected(button == homeBtn);
        paymentBtn.setSelected(button == paymentBtn);
        couponBtn.setSelected(button == couponBtn);
        walletBtn.setSelected(button == walletBtn);
        historyBtn.setSelected(button == historyBtn);
        shareBtn.setSelected(button == shareBtn);
        logoutBtn.setSelected(button == logoutBtn);
        help_btn.setSelected(button == help_btn);
    }

    public void enableDisableDrawer(boolean isEnable) {
        if (mDrawerLayout != null) {
            int lockMode = (isEnable) ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
            mDrawerLayout.setDrawerLockMode(lockMode);
        }
        checkBtnStates();
    }

    public void setNavMenuItems(NavMenu navMenuItems) {
        this.mNavMenuItems = navMenuItems;
        checkBtnStates();
    }

    public void setBasicDetails() {
       /* String name = String.format(getContext().getString(R.string.name), SharedHelper.getKey(getContext(), "first_name"),
                SharedHelper.getKey(getContext(), "last_name"));
        mNameTxt.setText(name);
        String gender = SharedHelper.getKey(getContext(), "gender");
        if (gender != null && !gender.equalsIgnoreCase("null")) {
            if (gender.equalsIgnoreCase("male")) {
                Picasso.with(getContext()).load(SharedHelper.getKey(getContext(), "avatar"))
                        .error(R.drawable.man_user)
                        .centerCrop()
                        .fit()
                        .into(mUserProfileImg);
            } else if (gender.equalsIgnoreCase("female")) {
                Picasso.with(getContext()).load(SharedHelper.getKey(getContext(), "avatar"))
                        .error(R.drawable.woman_user)
                        .centerCrop()
                        .fit()
                        .into(mUserProfileImg);
            } else {
                Picasso.with(getContext()).load(SharedHelper.getKey(getContext(), "avatar"))
                        .error(R.drawable.man_user)
                        .centerCrop()
                        .fit()
                        .into(mUserProfileImg);
            }
        }*/
    }

    @Override
    public void onProfileUpdateReflect() {
        setBasicDetails();
    }

    public interface NavDrawerFgmtListener {
        void menuClicked(NavMenu navMenuItems);

        void headerClicked();

        void headerProfileClicked();
    }

}
