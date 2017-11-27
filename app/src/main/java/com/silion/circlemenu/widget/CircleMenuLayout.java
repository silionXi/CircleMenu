package com.silion.circlemenu.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.silion.circlemenu.R;

/**
 * 自定义圆形菜单
 */

public class CircleMenuLayout extends ViewGroup {
    // 圆形直径
    private int mRadius;
    // 该容器内child item的默认尺寸
    private static final float RADIO_DEFAULT_CHILD_DIMENSION = 1 / 4F;
    // 该容器的内边距，无视padding属性，如需边距请用该变量
    private static final float RADIO_PADDING_LAYOUT = 1 / 12F;
    // 该容器的内边距
    private float mPadding;
    // 布局时的开始角度
    private double mStartAngle = 0;
    // 菜单项的图标
    private int[] mItemImgs;
    // 菜单项的文本
    private String[] mItemTexts;
    // 菜单的个数
    private int mMenuItemCount;
    // 菜单布局资源Id
    private int mMenuItemLayoutId = R.layout.circle_menu_item;
    // MenuItem 的点击事件接口
    private OnMenuClickListener mOnMenuClickListener;
    private ListAdapter mAdapter;

    public CircleMenuLayout(Context context) {
        this(context, null);
    }

    public CircleMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CircleMenuLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setPadding(0, 0, 0, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 测量自身
        measureMySelf(widthMeasureSpec, heightMeasureSpec);
        // 测量菜单项
        measureChildViews();
    }

    private void measureChildViews() {
        // 获得半径
        mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight());
        // menu item数量
        int count = getChildCount();
        // menu item尺寸
        int childSize = (int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION);
        // menu item测量模式
        int childMode = MeasureSpec.EXACTLY;
        // 迭代测量
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            // 计算menu item的尺寸，以及设置好的模式，去对item进行测量
            int makeMeasureSpec = -1;
            makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize, childMode);
            child.measure(makeMeasureSpec, makeMeasureSpec);
        }
        mPadding = RADIO_PADDING_LAYOUT * mRadius;
    }

    private void measureMySelf(int widthMeasureSpec, int heightMeasureSpec) {
        int resWidth = 0;
        int resHeight = 0;
        // 根据传入的参数，分别获取测量模式和测量值
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // 如果宽或高的测量模式非精确值
        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
            // 主要设置为背景图的高度，如果未设置背景图，则设置为屏幕宽高的默认值
            resWidth = getSuggestedMinimumWidth();
            resWidth = resWidth != 0 ? resWidth : getDefaultWidth();
            resHeight = getSuggestedMinimumHeight();
            resHeight = resHeight != 0 ? resHeight : getDefaultHeight();
        } else {
            // 如果都设置为精确值，则直接取小值
            resWidth = resHeight = Math.min(width, height);
        }
        setMeasuredDimension(resWidth, resHeight);
    }

    private int getDefaultWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    private int getDefaultHeight() {
        return getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCoun = getChildCount();
        int left, top;
        // menu item的尺寸
        int itemWidth = (int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION);
        // 根据men item的个数，计算item的布局占用的角度
        float angleDelay = 360 / childCoun;
        // 遍历所有菜单项，设置它们的位置
        for (int i = 0; i < childCoun; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            // 菜单项的起始角度
            mStartAngle %= 360;
            // 计算中心点到menu item中心的距离
            float distanceFromCenter = mRadius / 2f - itemWidth / 2 - mPadding;
            // diatanceFromCenter cosa 即menu item中心点的left坐标
            left = (int) (mRadius / 2 + Math.round(distanceFromCenter * Math.cos(Math.toRadians(mStartAngle)) - 1 / 2f * itemWidth));
            // diatanceFromCenter sina 即menu item中心的纵坐标
            top = (int) (mRadius / 2 + Math.round(distanceFromCenter * Math.sin(Math.toRadians(mStartAngle)) - 1 / 2f * itemWidth));
            // 布局child View
            child.layout(left, top, left + itemWidth, top + itemWidth);
            // 叠加角度
            mStartAngle += angleDelay;
        }
    }

    /**
     * 设置菜单条目的图标和文本，至少设置其中一项
     *
     * @param images 菜单条目的图标
     * @param texts 菜单条目的文本
     */
    public void setMenuItemIconsAndTexts(int[] images, String[] texts) {
        if (images == null && texts == null) {
            throw new IllegalArgumentException("菜单项图片和文本至少设置一项");
        }

        mItemImgs = images;
        mItemTexts = texts;

        mMenuItemCount = images == null ? texts.length : images.length;
        if (images != null && texts !=null) {
            mMenuItemCount = Math.min(texts.length, images.length);
        }

        buildMenuItems();
    }

    // 设置Adapter
    public void setAdapter(ListAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    protected void onAttachedToWindow() {
        if (mAdapter != null) {
            buildMenuItems();
        }
        super.onAttachedToWindow();
    }

    /**
     * 构建菜单项
     */
    private void buildMenuItems() {
        // 根据用户设置的参数，初始化menu item
//        for (int i = 0; i < mMenuItemCount; i++) {
//            View itemView = inflateMenuView(i);
//            // 初始化菜单项
//            initMenuItem(itemView, i);
//            addView(itemView);
//        }
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View viewItem = mAdapter.getView(i, null, this);
            final int position = i;
            viewItem.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnMenuClickListener.onClick(v, position);
                }
            });
            addView(viewItem);
        }


    }

    private void initMenuItem(View itemView, int childIndex) {
        ImageView ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
        TextView tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        ivIcon.setImageResource(mItemImgs[childIndex]);
        tvTitle.setText(mItemTexts[childIndex]);
    }

    private View inflateMenuView(final int childIndex) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View itemView = inflater.inflate(mMenuItemLayoutId, this, false);
        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnMenuClickListener != null) {
                    mOnMenuClickListener.onClick(v, childIndex);
                }
            }
        });
        return itemView;
    }

    // 设置MenuItem的布局文件，必须在setMenuItemIconsAndTexts之前调用
    public void setMenuItemLayoutId(int menuItemLayoutId) {
        this.mMenuItemLayoutId = menuItemLayoutId;
    }

    // 设置MenuItem的点击事件接口
    public void setOnMenuClickListener(OnMenuClickListener listener) {
        this.mOnMenuClickListener = listener;
    }

    public interface OnMenuClickListener {
        void onClick(View v, int position);
    }
}
