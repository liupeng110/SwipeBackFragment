package me.yokeyword.swipebackfragment;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


public class SwipeBackFragment extends Fragment {
    private static final String SWIPEBACKFRAGMENT_STATE_SAVE_OR_HIDDEN = "SWIPEBACKFRAGMENT_STATE_SAVE_OR_HIDDEN";
    private SwipeBackLayout mSwipeBackLayout;
    private Animation mNoAnim;
    boolean mLocking = false;

    protected Activity _mActivity;

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        _mActivity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {//保存状态
            boolean isSupportHidden = savedInstanceState.getBoolean(SWIPEBACKFRAGMENT_STATE_SAVE_OR_HIDDEN);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }

        mNoAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.no_anim);//加载自定义动画
        onFragmentCreate();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SWIPEBACKFRAGMENT_STATE_SAVE_OR_HIDDEN, isHidden());
    }//保存状态

    private void onFragmentCreate() {
        mSwipeBackLayout = new SwipeBackLayout(getActivity());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSwipeBackLayout.setLayoutParams(params);
        mSwipeBackLayout.setBackgroundColor(Color.TRANSPARENT);
    }//创建一个外层布局

    protected View attachToSwipeBack(View view) {
        mSwipeBackLayout.attachToFragment(this, view);//第一个参数为fragment,第二个参数view为整个布局
        return mSwipeBackLayout;
    }

    protected View attachToSwipeBack(View view, SwipeBackLayout.EdgeLevel edgeLevel) {
        mSwipeBackLayout.attachToFragment(this, view);
        mSwipeBackLayout.setEdgeLevel(edgeLevel);
        return mSwipeBackLayout;
    }

    protected void setEdgeLevel(SwipeBackLayout.EdgeLevel edgeLevel) {
        mSwipeBackLayout.setEdgeLevel(edgeLevel);
    }

    protected void setEdgeLevel(int widthPixel) {
        mSwipeBackLayout.setEdgeLevel(widthPixel);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden && mSwipeBackLayout != null) {
            mSwipeBackLayout.hiddenFragment();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        initFragmentBackground(view);
        if (view != null) {
            view.setClickable(true);
        }
    }

    private void initFragmentBackground(View view) {
        if (view instanceof SwipeBackLayout) {
            View childView = ((SwipeBackLayout) view).getChildAt(0);
            setBackground(childView);
        } else {
            setBackground(view);
        }
    }

    private void setBackground(View view) {
        if (view != null && view.getBackground() == null) {
            int defaultBg = 0;
            if (_mActivity instanceof SwipeBackActivity) {
                defaultBg = ((SwipeBackActivity) _mActivity).getDefaultFragmentBackground();
            }
            if (defaultBg == 0) {
                int background = getWindowBackground();
                view.setBackgroundResource(background);
            } else {
                view.setBackgroundResource(defaultBg);
            }
        }
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (mLocking) {
            return mNoAnim;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    protected int getWindowBackground() {
        TypedArray a = getActivity().getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.windowBackground
        });
        int background = a.getResourceId(0, 0);
        a.recycle();
        return background;
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }

    public void setSwipeBackEnable(boolean enable) {
        mSwipeBackLayout.setEnableGesture(enable);
    }
}
