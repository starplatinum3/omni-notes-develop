package it.feio.android.omninotes.widget;
/*
 Copyright 2013 Neopixl - Olivier Demolliens

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this

file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under

the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF 

ANY KIND, either express or implied. See the License for the specific language governing

permissions and limitations under the License.
 */
//package com.neopixl.pixlui.components.edittext;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.android.export.AllCapsTransformationMethod;
import com.neopixl.pixlui.components.edittext.EditTextBatchListener;
import com.neopixl.pixlui.components.edittext.EditTextFocusListener;
import com.neopixl.pixlui.components.textview.FontFactory;
import com.neopixl.pixlui.intern.CustomPasswordTransformationMethod;
import com.neopixl.pixlui.intern.PixlUIContants;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.feio.android.pixlui.links.Hyperlink;
import it.feio.android.pixlui.links.InternalURLSpan;
import it.feio.android.pixlui.links.RegexPatternsConstants;
import it.feio.android.pixlui.links.TextLinkClickListener;
import it.feio.android.pixlui.links.UrlCompleter;

/**
 * Provide more possibility with EditText and enable new methods on old api
 *
 * @author Olivier Demolliens. @odemolliens Dev with Neopixl
 */
public class EditText extends com.neopixl.pixlui.components.edittext.EditText implements OnClickListener {


    private SpannableString linkableText;

    // The String Containing the Text that we have to gather links from private
    // SpannableString linkableText;
    // Populating and gathering all the links that are present in the Text
    //包含文本的字符串，我们必须从private收集链接
    //SpannableString linkableText；
    //填充并收集文本中的所有链接
    private ArrayList<Hyperlink> listOfLinks;
    private WeakReference<TextLinkClickListener> textLinkClickListenerWeakReference;

    /**
     * XML Attribute
     */
    private static final String EDITTEXT_ATTRIBUTE_FONT_NAME = "typeface";
    private static final String EDITTEXT_ATTRIBUTE_COPY_AND_PASTE = "copyandpaste";
    private static final String EDITTEXT_ATTRIBUTE_CANCEL_CLIPBOARD_CONTENT = "clearclipboardcontent";
    private static final String EDITTEXT_ATTRIBUTE_AUTO_FOCUS = "autofocus";
    private static final String EDITTEXT_OS_ATTRIBUTE_TEXT_ALL_CAPS = "textAllCaps";

    /**
     * Provider Keyboard
     */
    private static final String EDITTEXT_KEYBOARD_SENSE = "com.htc.android.htcime/.HTCIMEService";
    private static final String EDITTEXT_KEYBOARD_OLD_SAMSUNG = "com.sec.android.inputmethod.axt9/.AxT9IME";
    private static final String EDITTEXT_KEYBOARD_SAMSUNG = "com.sec.android.inputmethod/.SamsungKeypad";
    private static final String EDITTEXT_KEYBOARD_LG = "com.lge.ime/.LgeImeImpl";
    private static final String EDITTEXT_KEYBOARD_SWIFTKEY_TRIAL = "com.touchtype.swiftkey.phone.trial/com.touchtype.KeyboardService";
    private static final String EDITTEXT_KEYBOARD_SWYPE_DRAGON = "com.nuance.swype.trial/com.nuance.swype.input.IME";
    private static final String EDITTEXT_KEYBOARD_SWIFTKEY = "com.touchtype.swiftkey/com.touchtype.KeyboardService";


    /**
     * Listeners
     */
    private EditTextBatchListener listenerBatch;
    private EditTextFocusListener listenerFocus;
    private CustomTextWatcher listenerTextWatcherInternal;

    /**
     * State
     */
    private boolean mOldDeviceKeyboard;
    private boolean mOldDeviceTextAllCaps;
    private boolean mAutoFocus;
    private boolean mCustomPassWordTransformation;

    private InputMethodManager mImm;

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new CustomInputConnection(
                super.onCreateInputConnection(outAttrs), true, this);
    }

    public EditText(Context context) {
        super(context);
        editTextVersion();

        setOnClickListener(this);
        setLinkTextColor(getTextColors());
    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        editTextVersion();
        setCustomFont(context, attrs);
        setDisableCopyAndPaste(context, attrs);
        setCancelClipboard(context, attrs);
        setAutoFocus(context, attrs);
//        旧设备文本是否全部大写
//        is Old Device Text All Caps
        if (isOldDeviceTextAllCaps()) {
            setAllCaps(context, attrs);
        }

        setOnClickListener(this);
        setLinkTextColor(getCurrentTextColor());
    }

    public EditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        editTextVersion();
        setCustomFont(context, attrs);
        setDisableCopyAndPaste(context, attrs);
        setCancelClipboard(context, attrs);
        setAutoFocus(context,attrs);
        if (isOldDeviceTextAllCaps()) {
            setAllCaps(context, attrs);
        }

        setOnClickListener(this);
        setLinkTextColor(getCurrentTextColor());
    }

    /**
     * Define what version of code we need to use
     */
    private void editTextVersion() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            setOldDeviceKeyboard(true);
            setOldDeviceTextAllCaps(true);

        } else {
            setOldDeviceKeyboard(true);
            setOldDeviceTextAllCaps(false);
        }

        //Custom internal listener
        setListenerTextWatcherInternal(new CustomTextWatcher(this));
        addTextChangedListener(getListenerTextWatcherInternal());

        //System service
        mImm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        //Autofocus disabled by default
        mAutoFocus = false;
        mCustomPassWordTransformation = false;
    }

    /**
     * XML methods
     *
     * @param ctx
     * @param attrs
     */
    private void setCustomFont(Context ctx, AttributeSet attrs) {
        String typefaceName = attrs.getAttributeValue(
                PixlUIContants.SCHEMA_URL, EDITTEXT_ATTRIBUTE_FONT_NAME);

        if (typefaceName != null) {
//			setPaintFlags(this.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG
//					| Paint.LINEAR_TEXT_FLAG);
            setCustomFont(ctx, typefaceName);
        }
    }

    /**
     * XML methods
     *
     * @param ctx
     * @param attrs
     */
    private void setDisableCopyAndPaste(Context ctx, AttributeSet attrs) {
        boolean disableCopyAndPaste = attrs.getAttributeBooleanValue(
                PixlUIContants.SCHEMA_URL, EDITTEXT_ATTRIBUTE_COPY_AND_PASTE,
                true);

        if (!disableCopyAndPaste && !isInEditMode()) {
            disableCopyAndPaste();
        }
    }

    /**
     * XML methods
     *
     * @param ctx
     * @param attrs
     */
    private void setCancelClipboard(Context ctx, AttributeSet attrs) {
        boolean cancelClipboard = attrs.getAttributeBooleanValue(
                PixlUIContants.SCHEMA_URL,
                EDITTEXT_ATTRIBUTE_CANCEL_CLIPBOARD_CONTENT, false);

        if (cancelClipboard && !isInEditMode()) {
            cancelClipBoardContent();
        }
    }

    /**
     * XML methods
     *
     * @param ctx
     * @param attrs
     */
    private void setAllCaps(Context ctx, AttributeSet attrs) {

        if(!isInEditMode()){
            int indexSize = attrs.getAttributeCount();

            boolean allCaps = false;

            for (int i = 0; i < indexSize; i++) {
                if (attrs.getAttributeName(i).equals(
                        EDITTEXT_OS_ATTRIBUTE_TEXT_ALL_CAPS)) {
                    allCaps = attrs.getAttributeBooleanValue(i, false);
                    break;
                }
            }

            if (allCaps && !isInEditMode()) {
                setAllCaps(allCaps);
            }
        }
    }

    /**
     * Enable autofocus mode (for keyboard).
     *
     * @param ctx
     * @param attrs
     */
    private void setAutoFocus(Context ctx, AttributeSet attrs) {

        if(!isInEditMode()){
            int indexSize = attrs.getAttributeCount();

            boolean autoFocus = false;

            for (int i = 0; i < indexSize; i++) {
                if (attrs.getAttributeName(i).equals(
                        EDITTEXT_ATTRIBUTE_AUTO_FOCUS)) {
                    autoFocus = attrs.getAttributeBooleanValue(i, false);
                    break;
                }
            }

            if (autoFocus && !isInEditMode()) {
                setAutoFocus(autoFocus);
            }
        }
    }

    /**
     * Use this method to uppercase all char in text.
     *
     * @param allCaps
     */
    @SuppressLint("NewApi")
    @Override
    public void setAllCaps(boolean allCaps) {

        //FIXME: if user input new char, it's generate a crash on Paint methods
        if (this.isOldDeviceTextAllCaps()) {
            if (allCaps) {
                setTransformationMethod(new AllCapsTransformationMethod(
                        getContext()));
            } else {
                setTransformationMethod(null);
            }
        } else {
            super.setAllCaps(allCaps);
        }
    }

    /**
     * Use this method to set a custom font in your code (/assets/fonts/) a
     *
     * @param ctx
     * @param Font
     *            Name, don't forget to add file extension
     * @return
     */
    public boolean setCustomFont(Context ctx, String font) {
        Typeface tf = FontFactory.getInstance(ctx).getFont(font);
        if (tf != null) {
            setTypeface(tf);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Disable cut/copy/paste
     */
    @SuppressLint("NewApi")
    public void disableCopyAndPaste() {
        if (android.os.Build.VERSION.SDK_INT < 11) {
            this.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
                public void onCreateContextMenu(ContextMenu menu, View v,
                                                ContextMenuInfo menuInfo) {
                    menu.clear();
                }
            });
        } else {
            this.setCustomSelectionActionModeCallback(new Callback() {

                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public void onDestroyActionMode(ActionMode mode) {
                }

                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public boolean onActionItemClicked(ActionMode mode,
                                                   MenuItem item) {
                    return false;
                }
            });
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    /*
     * Cancel clipboard content
     */
    public void cancelClipBoardContent() {

        if (android.os.Build.VERSION.SDK_INT < 11) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getContext()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                clipboard.setText("");
            }
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null && clipboard.getPrimaryClip() != null
                    && clipboard.getPrimaryClip().getItemCount() > 0) {
                android.content.ClipData clip = android.content.ClipData
                        .newPlainText("", "");
                clipboard.setPrimaryClip(clip);
            }
        }
    }

    /**
     * Used to intercept the focus
     */
    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        EditTextFocusListener listener = getFocusListener();
        if (listener != null) {
            if (focused) {
                listener.requestFocus(this);
            } else {
                listener.loseFocus(this);
            }
        }

        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if(mAutoFocus){
            if(focused){
                mImm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT | InputMethodManager.SHOW_FORCED);
            }else{
                mImm.hideSoftInputFromWindow(this.getWindowToken(), 0);
            }
            mImm.toggleSoftInput(0, 0);
        }

        // Checks link clicked
        if (focused && textLinkClickListenerWeakReference != null) {
            onClick(this);
        }
    }

    /**
     * Force show keyboard
     */
    public void showKeyboard() {
        this.requestFocus();


        Context context = getContext();
        if(Activity.class.isInstance(context)) {
            ((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        //mImm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT | InputMethodManager.SHOW_FORCED);
        //mImm.toggleSoftInput(0, 0);

        // Trick used to create a fake touch event on the editText
        MotionEvent event = MotionEvent.obtain(0, SystemClock.uptimeMillis(),
                MotionEvent.ACTION_DOWN | MotionEvent.ACTION_UP, this.getMeasuredWidth(), 0, 0);
        this.onTouchEvent(event);
        event.recycle();
    }

    /**
     * Force hide keyboard
     */
    public void hideKeyboard() {
        hideKeyboard(true);
    }

    /**
     * Force hide keyboard optionally without loosing focus
     */
    public void hideKeyboard(boolean clearFocus) {
        if (clearFocus) {
            this.clearFocus();
        }
        Context context = getContext();
        if(Activity.class.isInstance(context)) {
            ((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        //mImm.hideSoftInputFromWindow(this.getWindowToken(), 0);
        //mImm.toggleSoftInput(0, 0);
    }

    /**
     * Get keyboard name (usefull to know if user used a custom keyboard, like
     * HackerKeyboard or SenseKeyboard or...)
     *
     * @return keyboard name + package
     *
     */
    private String getKeyboardName() {
        return Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);
    }

    /**
     * Return if cellphone use a sense keyboard (HTC)
     *
     * @return
     */
    public boolean isASenseKeyboard() {
        if (getKeyboardName().equals(EDITTEXT_KEYBOARD_SENSE)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return if cellphone use a Swype + Dragon
     *
     * @return
     */
    public boolean isASwypeDragonKeyboard() {
        if (getKeyboardName().equals(EDITTEXT_KEYBOARD_SWYPE_DRAGON)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return if cellphone use a SwiftKeyTrial
     *
     * @return
     */
    public boolean isASwiftKeyTrialKeyboard() {
        if (getKeyboardName().equals(EDITTEXT_KEYBOARD_SWIFTKEY_TRIAL)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return if cellphone use a SwiftKey
     *
     * @return
     */
    public boolean isASwiftKeyKeyboard() {
        if (getKeyboardName().equals(EDITTEXT_KEYBOARD_SWIFTKEY)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return if cellphone use a old samsung keyboard
     *
     * @return
     */
    public boolean isAOldSamsungKeyboard() {
        if (getKeyboardName().equals(EDITTEXT_KEYBOARD_OLD_SAMSUNG)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return if cellphone use a samsung keyboard
     *
     * @return
     */
    public boolean isASamsungKeyboard() {
        if (getKeyboardName().equals(EDITTEXT_KEYBOARD_SAMSUNG)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return if cellphone use a lg keyboard
     *
     * @return
     */
    public boolean isALGKeyboard() {
        if (getKeyboardName().equals(EDITTEXT_KEYBOARD_LG)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Test if user use a custom keyboard
     *
     * @return
     */
    public boolean isUsingCustomInputMethod() {
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> mInputMethodProperties = imm
                .getEnabledInputMethodList();
        final int N = mInputMethodProperties.size();
        for (int i = 0; i < N; i++) {
            InputMethodInfo imi = mInputMethodProperties.get(i);
            if (imi.getId().equals(
                    Settings.Secure.getString(
                            getContext().getContentResolver(),
                            Settings.Secure.DEFAULT_INPUT_METHOD))) {
                if ((imi.getServiceInfo().applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private class CustomTextWatcher implements TextWatcher{

        private EditText mEdittext;
        private String previousText;

        public CustomTextWatcher(EditText editText) {
            super();
            setEdittext(editText);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

            EditTextBatchListener listener = getEdittext()
                    .getBatchListener();

            if(listener!=null && count == 1){
                listener.addNewChar(getEdittext());
            }

//			// Catching space pressed		
//			if (previousText != null) {
//				CharSequence c = s.toString().replaceAll(previousText.toString(), "");		
//				if (" ".equals(c)) {
//					Log.d("PixlUI", "space inserted");
//					gatherLinksForText();
//				}
//			}
//			previousText = s.toString();			

            updateLinksSpan(start, count - before);
        }

        public EditText getEdittext() {
            return mEdittext;
        }

        public void setEdittext(EditText mEdittext) {
            this.mEdittext = mEdittext;
        }
    }

    private class CustomInputConnection extends InputConnectionWrapper {

        private int mLastLength;
        private EditText mEdittext;
        private KeyEvent mKeyEvent;

        public CustomInputConnection(InputConnection target, boolean mutable,
                                     EditText editText) {
            super(target, mutable);
            setEdittext(editText);
        }

        @Override
        public boolean beginBatchEdit() {
            mLastLength = length();
            mKeyEvent = null;
            return super.beginBatchEdit();
        }

        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            //WorkAround for Samsung/HTC keyboard
            try {
                return super.commitText(text, newCursorPosition);
            } catch (IndexOutOfBoundsException e) {
                return true;
            }
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            mKeyEvent = event;

            EditTextBatchListener listener = getEdittext()
                    .getBatchListener();

            if (event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                String text = getEdittext().getText().toString();

                if (listener != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (text.length() == 0) {
                        listener.deleteKeyboardButton(getEdittext(), true);
                    } else {
                        listener.deleteKeyboardButton(getEdittext(), false);
                    }
                }
            }
            return super.sendKeyEvent(event);
        }

        @Override
        public boolean endBatchEdit() {

            final int newLength = length();

            EditTextBatchListener listener = getEdittext().getBatchListener();

            if (listener != null /*&& !getEdittext().isOldDeviceKeyboard()*/) {
                if (newLength <= mLastLength) {
                    if (mLastLength - newLength == 1) {

                        if (mKeyEvent == null) {
                            listener.deleteKeyboardButton(getEdittext(), false);
                        }/* else {
							char unicodeChar = (char) mKeyEvent
									.getUnicodeChar();
							String text = getEdittext().getText().toString();
							text = text + unicodeChar;
							getEdittext().setText(text);
							listener.addNewChar(getEdittext());
						}*/

                    } else if (mLastLength == 0 && newLength == 0) {

                        if (mKeyEvent == null) {
                            listener.deleteKeyboardButton(getEdittext(), true);
                        }/* else {
							char unicodeChar = (char) mKeyEvent
									.getUnicodeChar();
							String text = getEdittext().getText().toString();
							text = text + unicodeChar;
							getEdittext().setText(text);
							listener.addNewChar(getEdittext());
						}*/

                    } else {
						/*if (mKeyEvent != null) {
							char unicodeChar = (char) mKeyEvent
									.getUnicodeChar();
							String text = getEdittext().getText().toString();
							text = text + unicodeChar;
							getEdittext().setText(text);
							listener.addNewChar(getEdittext());
						}*/
                    }
                } /*else {
					listener.addNewChar(getEdittext());
				}*/
            }
            return super.endBatchEdit();
        }

        public EditText getEdittext() {
            return mEdittext;
        }

        public void setEdittext(EditText mEdittext) {
            this.mEdittext = mEdittext;
        }
    }

    public boolean isOldDeviceTextAllCaps() {
        return mOldDeviceTextAllCaps;
    }

    public void setOldDeviceTextAllCaps(boolean mOldDeviceTextAllCaps) {
        this.mOldDeviceTextAllCaps = mOldDeviceTextAllCaps;
    }

    public boolean isOldDeviceKeyboard() {
        return mOldDeviceKeyboard;
    }

    public void setOldDeviceKeyboard(boolean mOldDevice) {
        this.mOldDeviceKeyboard = mOldDevice;
    }

    private EditTextBatchListener getBatchListener() {
        return listenerBatch;
    }

    public void setBatchListener(EditTextBatchListener listener) {
        this.listenerBatch = listener;
    }

    private EditTextFocusListener getFocusListener() {
        return listenerFocus;
    }

    public void setFocusListener(EditTextFocusListener listenerFocus) {
        this.listenerFocus = listenerFocus;
    }

//    @Override
//    public CustomTextWatcher getListenerTextWatcherInternal() {
//        return listenerTextWatcherInternal;
//    }

    public void setListenerTextWatcherInternal(
            CustomTextWatcher listenerTextWatcherInternal) {
        this.listenerTextWatcherInternal = listenerTextWatcherInternal;
    }

    public boolean isAutoFocus() {
        return mAutoFocus;
    }

    public void setAutoFocus(boolean mAutoFocus) {
        this.mAutoFocus = mAutoFocus;
    }

    public boolean isCustomPassWordTransformation() {
        return mCustomPassWordTransformation;
    }

    public void setCustomPassWordTransformation(
            boolean mCustomPassWordTransformation) {
        this.mCustomPassWordTransformation = mCustomPassWordTransformation;
        if(this.mCustomPassWordTransformation){
            this.setTransformationMethod(new CustomPasswordTransformationMethod());
        }
    }


    /**
     * Collects the Links depending upon the Pattern that we supply
     * and add the links to the ArrayList of the links
     * 根据我们提供的模式收集链接
     * *并将链接添加到链接的ArrayList中
     */
    public void gatherLinksForText() {
        String text = getText().toString();
//        linkableText = new SpannableString(text);
        listOfLinks = new ArrayList<>();

        for (Pattern pattern : RegexPatternsConstants.patterns) {
//			gatherLinks(listOfLinks, linkableText, pattern);
            gatherLinks2(listOfLinks, text.split("\\s"), pattern);
        }

        for (int i = 0; i < listOfLinks.size(); i++) {
            Hyperlink linkSpec = listOfLinks.get(i);

            // this process here makes the Clickable Links from the text
            //这个过程可以从文本中创建可点击的链接
            //链接点击的事件是
            linkableText.setSpan(linkSpec.span, linkSpec.start, linkSpec.end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // sets the text for the TextView with enabled links
        //为启用链接的TextView设置文本
        setText(linkableText);
    }


    /**
     * Performs the Regex Comparison for the Pattern and adds them to listOfLinks array list
     * @param links
     * @param s
     * @param pattern
     */
    private final void gatherLinks(ArrayList<Hyperlink> links, Spannable s, Pattern pattern) {
        // Matcher matching the pattern
        Matcher m = pattern.matcher(s);

        while (m.find()) {
            int start = m.start();
            int end = m.end();

            // Hyperlink is basically used like a structure for storing the
            // information about where the link was found.
            Hyperlink spec = new Hyperlink();

            spec.textSpan = s.subSequence(start, end);
            spec.span = new InternalURLSpan(spec.textSpan.toString());
            spec.start = start;
            spec.end = end;

            links.add(spec);
        }
    }


    /**
     * Performs the Regex Comparison for the Pattern and adds them to listOfLinks array list
     * *对模式执行正则表达式比较，并将它们添加到listOfLinks数组列表中
     * @param links
     * @param words
     * @param pattern
     */
    private final void gatherLinks2(ArrayList<Hyperlink> links, String[] words, Pattern pattern) {
        // Matcher matching the pattern
        Matcher m;

        int start = 0;
        int end = 0;
        for (String word : words) {
            m = pattern.matcher(word);
            end = start + word.length();

            if (m.matches()) {

                // Hyperlink is basically used like a structure for storing the
                // information about where the link was found.
                //超级链接基本上是一种存储数据的结构
                //有关找到链接的位置的信息。
                Hyperlink spec = new Hyperlink();

                spec.textSpan = word;
                //https://blog.csdn.net/danpincheng0204/article/details/106778955
                spec.span = new InternalURLSpan(spec.textSpan.toString());
                //spec.span.onClick(this);
                spec.start = start;
                spec.end = end;

                links.add(spec);
            }

            start = end + 1;
        }

    }


    /**
     * Updates all the parsed links start/end index on new text insertion (onTextChanged callback is used)
     * @param start Start of text modification
     * @param count Characters inserted
     */
    private void updateLinksSpan(int start, int count) {
        if (listOfLinks != null) {
            for (Hyperlink link : listOfLinks) {
                if (link.start > start) {
                    link.start += count;
                    link.end += count;
                }
            }
        }
    }


    // Sets the Listener for later click propagation purpose
    public void setOnTextLinkClickListener(TextLinkClickListener newListener) {
        this.textLinkClickListenerWeakReference = new WeakReference<>(newListener);
    }

    // Retrieves the Listener for links click propagation purpose
    public TextLinkClickListener getOnTextLinkClickListener() {
        if (textLinkClickListenerWeakReference != null) {
            return this.textLinkClickListenerWeakReference.get();
        }
        return null;
    }


    @Override
    public void onClick(View v) {
        if (textLinkClickListenerWeakReference != null) {
            int cursorPosition = getSelectionStart();
            Log.v("PixlUI", "onClick() on position " + cursorPosition);
            for (Hyperlink link : listOfLinks) {
                if (cursorPosition > link.start && cursorPosition < link.end) {
                    this.textLinkClickListenerWeakReference.get().onTextLinkClick(v, link.textSpan.toString(),
                            UrlCompleter.complete
                                    (link.textSpan.toString()));
                }
            }
        }
    }


    public SpannableString getLinkableText() {
        return linkableText;
    }

    public void setLinkableText(SpannableString linkableText) {
        this.linkableText = linkableText;
    }


}