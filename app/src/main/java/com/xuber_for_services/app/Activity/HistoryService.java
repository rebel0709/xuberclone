package com.xuber_for_services.app.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.xuber_for_services.app.R;
import com.xuber_for_services.app.Utils.Utilities;


public class HistoryService extends AppCompatActivity implements View.OnClickListener {

    ImageView imgBeforeService;
    ImageView imgAfterService;
    TextView lblBeforeService, lblAfterService;

    String phone;
    String email;
    Activity activity;
    ImageView backArrow;

    String before_comment = "", before_image = "";
    String after_comment = "", after_image = "";
    private int mShortAnimationDuration;
    private Animator mCurrentAnimator;
    LinearLayout lnrServicePhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setContentView(R.layout.activity_history_service);
        findviewById();
        setOnClickListener();

    }

    private void findviewById() {
        imgBeforeService = (ImageView) findViewById(R.id.imgBeforeService);
        imgAfterService = (ImageView) findViewById(R.id.imgAfterService);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        lblBeforeService = (TextView) findViewById(R.id.lblBeforeService);
        lblAfterService = (TextView) findViewById(R.id.lblAfterService);

        lnrServicePhoto = (LinearLayout) findViewById(R.id.lnrServicePhoto);


        before_comment = getIntent().getExtras().getString("before_comment");
        after_comment = getIntent().getExtras().getString("after_comment");
        before_image = getIntent().getExtras().getString("before_image");
        after_image = getIntent().getExtras().getString("after_image");


        //Before Part

        if (before_comment.equalsIgnoreCase("") || before_comment.equalsIgnoreCase("null"))
            lblBeforeService.setText("No comments found!");
        else
            lblBeforeService.setText("" + before_comment);

        if (!before_image.equalsIgnoreCase(""))
            Picasso.with(activity).load(Utilities.getImageURL(before_image)).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgBeforeService);
        else
            imgBeforeService.setBackgroundResource(R.drawable.no_image);


        //After Part

        if (after_comment.equalsIgnoreCase("") || after_comment.equalsIgnoreCase("null"))
            lblAfterService.setText("No comments found!");
        else
            lblAfterService.setText("" + after_comment);

        if (!after_image.equalsIgnoreCase(""))
            Picasso.with(activity).load(Utilities.getImageURL(after_image)).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgAfterService);
        else
            imgAfterService.setBackgroundResource(R.drawable.no_image);


    }

    private void setOnClickListener() {
        imgBeforeService.setOnClickListener(this);
        imgAfterService.setOnClickListener(this);
        backArrow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == imgBeforeService) {
            if (before_image != null && !before_image.equalsIgnoreCase("null") && before_image.length() > 0) {
                Intent intent = new Intent(HistoryService.this, ShowInvoicePicture.class);
                intent.putExtra("image", Utilities.getImageURL(before_image));
                startActivity(intent);
            }
        }

        if (v == imgAfterService) {
            if (after_image != null && !after_image.equalsIgnoreCase("null") && after_image.length() > 0) {
                Intent intent = new Intent(HistoryService.this, ShowInvoicePicture.class);
                intent.putExtra("image", Utilities.getImageURL(after_image));
                startActivity(intent);
            }
        }

        if (v == backArrow) {
            finish();
        }
    }


    private void zoomImageFromThumb(final View thumbView, String strImageURL) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        lnrServicePhoto.setVisibility(View.VISIBLE);
        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(R.id.imgZoomService);

        if (!strImageURL.equalsIgnoreCase("")) {
            Picasso.with(activity).load(Utilities.getImageURL(strImageURL)).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.no_image).error(R.drawable.no_image).into(expandedImageView);
        }

        // Calculate the starting and ending bounds for the zoomed-in image. This step
        // involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail, and the
        // final bounds are the global visible rectangle of the container view. Also
        // set the container view's offset as the origin for the bounds, since that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
        findViewById(R.id.container).setBackgroundColor(Color.TRANSPARENT);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);


        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
        // "center crop" technique. This prevents undesirable stretching during the animation.
        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation begins,
        // it will position the zoomed-in view in the place of the thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);


        // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
        // the zoomed-in view (the default is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;


        // Upon clicking the zoomed-in image, it should zoom back_letter down to the original bounds
        // and show the thumbnail instead of the expanded image.
        final float startScaleFinal = startScale;

        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel, back_letter to their
                // original values.
                AnimatorSet set = new AnimatorSet();
                set
                        .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        lnrServicePhoto.setVisibility(View.GONE);
                        thumbView.setAlpha(1f);
                        expandedImageView.setImageResource(R.drawable.placeholder);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        lnrServicePhoto.setVisibility(View.GONE);
                        thumbView.setAlpha(1f);
//						expandedImageView.setImageResource(android.R.color.transparent);
                        expandedImageView.setImageResource(R.drawable.placeholder);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });

    }

}
