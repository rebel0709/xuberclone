package com.xuber_for_services.app.Fragments;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.xuber_for_services.app.Activity.ShowInvoicePicture;
import com.xuber_for_services.app.Helper.SharedHelper;
import com.xuber_for_services.app.R;


public class AfterService extends Fragment {
    public static final String TAG = "AfterService";
    Context context;
    View rootView;
    ImageView imgAfterServiceInvoice;
    TextView lblAfterServiceInvoice;
    private int mShortAnimationDuration;
    private Animator mCurrentAnimator;

   /* LinearLayout lnrServicePhoto;
    ImageView expandedImageView;
    FrameLayout container;*/



    public AfterService() {
        // Required empty public constructor
    }


    public static AfterService newInstance() {
        AfterService fragment = new AfterService();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.after_service, container, false);
        findViewByIdAndInitialize();


        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void findViewByIdAndInitialize() {

       /* lnrServicePhoto = (LinearLayout) rootView.findViewById(R.id.lnrServicePhoto);
        expandedImageView = (ImageView) rootView.findViewById(R.id.imgZoomService);
        container = (FrameLayout) rootView.findViewById(R.id.container);*/

        imgAfterServiceInvoice = (ImageView) rootView.findViewById(R.id.imgAfterServiceInvoice);
        lblAfterServiceInvoice = (TextView) rootView.findViewById(R.id.lblAfterServiceInvoice);


        if(!SharedHelper.getKey(context, "after_comment").equalsIgnoreCase(""))
            lblAfterServiceInvoice.setText("" + SharedHelper.getKey(context, "after_comment"));
        else
            lblAfterServiceInvoice.setText("No comments");


        if(!SharedHelper.getKey(context, "after_image").equalsIgnoreCase(""))
        Picasso.with(context).load(SharedHelper.getKey(context, "after_image")).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgAfterServiceInvoice);
        else
            imgAfterServiceInvoice.setBackgroundResource(R.drawable.no_image);

        imgAfterServiceInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*zoomImageFromThumb(imgAfterServiceInvoice, SharedHelper.getKey(context, "after_image"));
                mShortAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);*/

                if(!SharedHelper.getKey(context, "after_image").equalsIgnoreCase("")) {
                    Intent intent = new Intent(context, ShowInvoicePicture.class);
                    intent.putExtra("image", "" + SharedHelper.getKey(context, "after_image"));
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(context,"After Invoice image not found!",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();

    }


 /*   private void zoomImageFromThumb(final View thumbView, String strImageURL) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        lnrServicePhoto.setVisibility(View.VISIBLE);
        // Load the high-resolution "zoomed-in" image.

        if (!strImageURL.equalsIgnoreCase("")) {
            Picasso.with(context).load(Utilities.getImageURL(strImageURL)).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(expandedImageView);
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
        container.getGlobalVisibleRect(finalBounds, globalOffset);
        container.setBackgroundColor(Color.TRANSPARENT);
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
                        expandedImageView.setImageResource(R.drawable.loading);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        lnrServicePhoto.setVisibility(View.GONE);
                        thumbView.setAlpha(1f);
//						expandedImageView.setImageResource(android.R.color.transparent);
                        expandedImageView.setImageResource(R.drawable.loading);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });

    }*/


}
