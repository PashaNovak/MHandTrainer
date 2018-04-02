package pavelnovak.com.mhandtrainer;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class PresetValueButton extends RelativeLayout implements RadioCheckable {
    private TextView mValueTextView, mUnitTextView;

    public static final int DEFAULT_TEXT_COLOR = Color.TRANSPARENT;

    private String mValue;
    private String mUnit;
    private int mValueTextColor;
    private int mUnitTextColor;
    private int mPressedTextColor;

    private Drawable mInitialBackgroundDrawable;
    private OnClickListener mOnClickListener;
    private OnTouchListener mOnTouchListener;
    private boolean mChecked;
    private ArrayList<OnCheckedChangeListener> mOnCheckedChangeListeners = new ArrayList<>();

    public PresetValueButton(Context context) {
        super(context);
        setupView();
    }

    public PresetValueButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(attrs);
        setupView();
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public PresetValueButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttributes(attrs);
        setupView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PresetValueButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        parseAttributes(attrs);
        setupView();
    }

    private void parseAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.PresetValueButton, 0, 0);
        Resources resources = getContext().getResources();
        try {
            mValue = a.getString(R.styleable.PresetValueButton_presetButtonValueText);
            mUnit = a.getString(R.styleable.PresetValueButton_presetButtonUnitText);
            mValueTextColor = a.getColor(R.styleable.PresetValueButton_presetButtonValueTextColor, resources.getColor(R.color.black));
            mPressedTextColor = a.getColor(R.styleable.PresetValueButton_presetButtonPressedTextColor, Color.WHITE);
            mUnitTextColor = a.getColor(R.styleable.PresetValueButton_presetButtonUnitTextColor, resources.getColor(R.color.gray));
        } finally {
            a.recycle();
        }
    }

    // Template method
    private void setupView() {
        inflateView();
        bindView();
        setCustomTouchListener();
    }

    protected void inflateView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.custom_preset_button, this, true);
        mValueTextView = (TextView) findViewById(R.id.text_view_value);
        mUnitTextView = (TextView) findViewById(R.id.text_view_unit);
        mInitialBackgroundDrawable = getBackground();
    }

    protected void bindView() {
        if (mUnitTextColor != DEFAULT_TEXT_COLOR) {
            mUnitTextView.setTextColor(mUnitTextColor);
        }
        if (mValueTextColor != DEFAULT_TEXT_COLOR) {
            mValueTextView.setTextColor(mValueTextColor);
        }
        mUnitTextView.setText(mUnit);
        mValueTextView.setText(mValue);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mOnClickListener = l;
    }

    protected void setCustomTouchListener() {
        super.setOnTouchListener(new TouchListener());
    }

    @Override
    public void setOnTouchListener(OnTouchListener onTouchListener) {
        mOnTouchListener = onTouchListener;
    }

    public OnTouchListener getOnTouchListener() {
        return mOnTouchListener;
    }

    private void onTouchDown(MotionEvent motionEvent) {
        setChecked(true);
    }

    private void onTouchUp(MotionEvent motionEvent) {
        // Handle user defined click listeners
        if (mOnClickListener != null) {
            mOnClickListener.onClick(this);
        }
    }

    public void setCheckedState() {
        setBackgroundResource(R.drawable.background_shape_preset_button_pressed);
        mValueTextView.setTextColor(mPressedTextColor);
        mUnitTextView.setTextColor(mPressedTextColor);
    }

    public void setNormalState() {
        setBackgroundDrawable(mInitialBackgroundDrawable);
        mValueTextView.setTextColor(mValueTextColor);
        mUnitTextView.setTextColor(mUnitTextColor);
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public String getUnit() {
        return mUnit;
    }

    public void setUnit(String unit) {
        mUnit = unit;
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            if (!mOnCheckedChangeListeners.isEmpty()) {
                for (int i = 0; i < mOnCheckedChangeListeners.size(); i++) {
                    mOnCheckedChangeListeners.get(i).onCheckedChanged(this, mChecked);
                }
            }
            if (mChecked) {
                setCheckedState();
            } else {
                setNormalState();
            }
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public void addOnCheckChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListeners.add(onCheckedChangeListener);
    }

    @Override
    public void removeOnCheckChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListeners.remove(onCheckedChangeListener);
    }

    private final class TouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onTouchDown(event);
                    break;
                case MotionEvent.ACTION_UP:
                    onTouchUp(event);
                    break;
            }
            if (mOnTouchListener != null) {
                mOnTouchListener.onTouch(v, event);
            }
            return true;
        }
    }
}
