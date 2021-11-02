/*
 * Copyright (C) 2013-2020 Federico Iosue (federico@iosue.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.feio.android.omninotes;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static androidx.core.view.ViewCompat.animate;
import static it.feio.android.omninotes.BaseActivity.TRANSITION_HORIZONTAL;
import static it.feio.android.omninotes.BaseActivity.TRANSITION_VERTICAL;
import static it.feio.android.omninotes.MainActivity.FRAGMENT_DETAIL_TAG;
import static it.feio.android.omninotes.utils.ConstantsBase.ACTION_DISMISS;
import static it.feio.android.omninotes.utils.ConstantsBase.ACTION_FAB_TAKE_PHOTO;
import static it.feio.android.omninotes.utils.ConstantsBase.ACTION_MERGE;
import static it.feio.android.omninotes.utils.ConstantsBase.ACTION_NOTIFICATION_CLICK;
import static it.feio.android.omninotes.utils.ConstantsBase.ACTION_PINNED;
import static it.feio.android.omninotes.utils.ConstantsBase.ACTION_SHORTCUT;
import static it.feio.android.omninotes.utils.ConstantsBase.ACTION_SHORTCUT_WIDGET;
import static it.feio.android.omninotes.utils.ConstantsBase.ACTION_WIDGET;
import static it.feio.android.omninotes.utils.ConstantsBase.ACTION_WIDGET_SHOW_LIST;
import static it.feio.android.omninotes.utils.ConstantsBase.ACTION_WIDGET_TAKE_PHOTO;
import static it.feio.android.omninotes.utils.ConstantsBase.GALLERY_CLICKED_IMAGE;
import static it.feio.android.omninotes.utils.ConstantsBase.GALLERY_IMAGES;
import static it.feio.android.omninotes.utils.ConstantsBase.GALLERY_TITLE;
import static it.feio.android.omninotes.utils.ConstantsBase.INTENT_GOOGLE_NOW;
import static it.feio.android.omninotes.utils.ConstantsBase.INTENT_KEY;
import static it.feio.android.omninotes.utils.ConstantsBase.INTENT_NOTE;
import static it.feio.android.omninotes.utils.ConstantsBase.INTENT_WIDGET;
import static it.feio.android.omninotes.utils.ConstantsBase.MIME_TYPE_AUDIO;
import static it.feio.android.omninotes.utils.ConstantsBase.MIME_TYPE_AUDIO_EXT;
import static it.feio.android.omninotes.utils.ConstantsBase.MIME_TYPE_FILES;
import static it.feio.android.omninotes.utils.ConstantsBase.MIME_TYPE_IMAGE;
import static it.feio.android.omninotes.utils.ConstantsBase.MIME_TYPE_IMAGE_EXT;
import static it.feio.android.omninotes.utils.ConstantsBase.MIME_TYPE_SKETCH;
import static it.feio.android.omninotes.utils.ConstantsBase.MIME_TYPE_SKETCH_EXT;
import static it.feio.android.omninotes.utils.ConstantsBase.MIME_TYPE_VIDEO;
import static it.feio.android.omninotes.utils.ConstantsBase.MIME_TYPE_VIDEO_EXT;
import static it.feio.android.omninotes.utils.ConstantsBase.PREF_ATTACHMENTS_ON_BOTTOM;
import static it.feio.android.omninotes.utils.ConstantsBase.PREF_AUTO_LOCATION;
import static it.feio.android.omninotes.utils.ConstantsBase.PREF_COLORS_APP_DEFAULT;
import static it.feio.android.omninotes.utils.ConstantsBase.PREF_KEEP_CHECKED;
import static it.feio.android.omninotes.utils.ConstantsBase.PREF_KEEP_CHECKMARKS;
import static it.feio.android.omninotes.utils.ConstantsBase.PREF_PASSWORD;
import static it.feio.android.omninotes.utils.ConstantsBase.PREF_PRETTIFIED_DATES;
import static it.feio.android.omninotes.utils.ConstantsBase.PREF_WIDGET_PREFIX;
import static it.feio.android.omninotes.utils.ConstantsBase.SWIPE_MARGIN;
import static it.feio.android.omninotes.utils.ConstantsBase.SWIPE_OFFSET;
import static it.feio.android.omninotes.utils.ConstantsBase.THUMBNAIL_SIZE;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.neopixl.pixlui.components.edittext.EditText;
import com.neopixl.pixlui.components.textview.TextView;
import com.pixplicity.easyprefs.library.Prefs;
import com.pushbullet.android.extension.MessagingExtension;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Style;
import it.feio.android.checklistview.exceptions.ViewNotSupportedException;
import it.feio.android.checklistview.interfaces.CheckListChangedListener;
import it.feio.android.checklistview.models.CheckListView;
import it.feio.android.checklistview.models.CheckListViewItem;
import it.feio.android.checklistview.models.ChecklistManager;
import it.feio.android.omninotes.async.AttachmentTask;
import it.feio.android.omninotes.async.bus.NotesUpdatedEvent;
import it.feio.android.omninotes.async.bus.PushbulletReplyEvent;
import it.feio.android.omninotes.async.bus.SwitchFragmentEvent;
import it.feio.android.omninotes.async.notes.NoteProcessorDelete;
import it.feio.android.omninotes.async.notes.SaveNoteTask;
import it.feio.android.omninotes.databinding.FragmentDetailBinding;
import it.feio.android.omninotes.db.DbHelper;
import it.feio.android.omninotes.exceptions.checked.UnhandledIntentException;
import it.feio.android.omninotes.helpers.AttachmentsHelper;
import it.feio.android.omninotes.helpers.IntentHelper;
import it.feio.android.omninotes.helpers.LogDelegate;
import it.feio.android.omninotes.helpers.PermissionsHelper;
import it.feio.android.omninotes.helpers.TagOpenerHelper;
import it.feio.android.omninotes.helpers.date.DateHelper;
import it.feio.android.omninotes.helpers.date.RecurrenceHelper;
import it.feio.android.omninotes.helpers.notifications.NotificationChannels.NotificationChannelNames;
import it.feio.android.omninotes.helpers.notifications.NotificationsHelper;
import it.feio.android.omninotes.models.Attachment;
import it.feio.android.omninotes.models.Category;
import it.feio.android.omninotes.models.Note;
import it.feio.android.omninotes.models.ONStyle;
import it.feio.android.omninotes.models.Region;
import it.feio.android.omninotes.models.Tag;
import it.feio.android.omninotes.models.adapters.AttachmentAdapter;
import it.feio.android.omninotes.models.adapters.CategoryRecyclerViewAdapter;
import it.feio.android.omninotes.models.adapters.PlacesAutoCompleteAdapter;
import it.feio.android.omninotes.models.listeners.OnAttachingFileListener;
import it.feio.android.omninotes.models.listeners.OnGeoUtilResultListener;
import it.feio.android.omninotes.models.listeners.OnNoteSaved;
import it.feio.android.omninotes.models.listeners.OnReminderPickedListener;
import it.feio.android.omninotes.models.listeners.RecyclerViewItemClickSupport;
import it.feio.android.omninotes.models.views.ExpandableHeightGridView;
import it.feio.android.omninotes.utils.AlphaManager;
import it.feio.android.omninotes.utils.BitmapHelper;
import it.feio.android.omninotes.utils.ConnectionManager;
import it.feio.android.omninotes.utils.Display;
import it.feio.android.omninotes.utils.FileHelper;
import it.feio.android.omninotes.utils.FileProviderHelper;
import it.feio.android.omninotes.utils.FingerprintHelper;
import it.feio.android.omninotes.utils.GeocodeHelper;
import it.feio.android.omninotes.utils.IntentChecker;
import it.feio.android.omninotes.utils.KeyboardUtils;
import it.feio.android.omninotes.utils.PasswordHelper;
import it.feio.android.omninotes.utils.ReminderHelper;
import it.feio.android.omninotes.utils.ShortcutHelper;
import it.feio.android.omninotes.utils.StorageHelper;
import it.feio.android.omninotes.utils.StrUtil;
import it.feio.android.omninotes.utils.TagsHelper;
import it.feio.android.omninotes.utils.TextHelper;
import it.feio.android.omninotes.utils.date.DateUtils;
import it.feio.android.omninotes.utils.date.ReminderPickers;
import it.feio.android.pixlui.links.Hyperlink;
import it.feio.android.pixlui.links.InternalURLSpan;
import it.feio.android.pixlui.links.RegexPatternsConstants;
import it.feio.android.pixlui.links.TextLinkClickListener;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;

import rx.Observable;


public class DetailFragment extends BaseFragment implements OnReminderPickedListener,
        OnTouchListener,
        OnAttachingFileListener, TextWatcher, CheckListChangedListener, OnNoteSaved,
        OnGeoUtilResultListener, OnClickListener {

    private static final int TAKE_PHOTO = 1;
    private static final int TAKE_VIDEO = 2;
    private static final int SET_PASSWORD = 3;
    private static final int SKETCH = 4;
    private static final int CATEGORY = 5;
    private static final int DETAIL = 6;
    private static final int FILES = 7;

    FingerprintHelper fingerprintHelper;

    private FragmentDetailBinding binding;

    boolean goBack = false;
    private ExpandableHeightGridView mGridView;
    private View toggleChecklistView;
    private Uri attachmentUri;
    private AttachmentAdapter mAttachmentAdapter;
    private MaterialDialog attachmentDialog;
    private Note note;
    private Note noteTmp;
    private Note noteOriginal;
    // Audio recording
    private String recordName;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private boolean isRecording = false;
    private View isPlayingView = null;
    private Bitmap recordingBitmap;
    private ChecklistManager mChecklistManager;
    // Values to print result
    private String exitMessage;
    private Style exitCroutonStyle = ONStyle.CONFIRM;
    // Flag to check if after editing it will return to ListActivity or not
    // and in the last case a Toast will be shown instead than Crouton
    private boolean afterSavedReturnsToList = true;
    private boolean showKeyboard = false;
    private boolean swiping;
    private int startSwipeX;
    private boolean orientationChanged;
    private long audioRecordingTimeStart;
    private long audioRecordingTime;
    private DetailFragment mFragment;
    private Attachment sketchEdited;
    private int contentLineCounter = 1;
    private int contentCursorPosition;
    private ArrayList<String> mergedNotesIds;
    private MainActivity mainActivity;
    TextLinkClickListener textLinkClickListener = new TextLinkClickListener() {
        @Override
        public void onTextLinkClick(View view, final String clickedString, final String url) {
            new MaterialDialog.Builder(mainActivity)
                    .content(clickedString)
                    .negativeColorRes(R.color.colorPrimary)
                    .positiveText(R.string.open)
                    .negativeText(R.string.copy)
                    .onPositive((dialog, which) -> {
                        try {
                            Intent intent = TagOpenerHelper.openOrGetIntent(getContext(), url);
                            if (intent != null) {
                                mainActivity.initNotesList(intent);
                            }
                        } catch (UnhandledIntentException e) {
                            mainActivity.showMessage(R.string.no_application_can_perform_this_action,
                                    ONStyle.ALERT);
                        }
                    })
                    .onNegative((dialog, which) -> {
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                                mainActivity
                                        .getSystemService(CLIPBOARD_SERVICE);
                        android.content.ClipData clip = android.content.ClipData.newPlainText("text label",
                                clickedString);
                        clipboard.setPrimaryClip(clip);
                    }).build().show();
            View clickedView = noteTmp.isChecklist() ? toggleChecklistView : binding.contentWrapper;
            clickedView.clearFocus();
            KeyboardUtils.hideKeyboard(clickedView);
            new Handler().post(() -> {
                View clickedView1 = noteTmp.isChecklist() ? toggleChecklistView : binding.contentWrapper;
                KeyboardUtils.hideKeyboard(clickedView1);
            });
        }
    };
    private boolean activityPausing;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragment = this;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void fingerprintHelperInit(){

        fingerprintHelper=new FingerprintHelper();
        fingerprintHelper.setContext(mainActivity);
        FingerprintManager.AuthenticationCallback authenticationCallback=new FingerprintManager.AuthenticationCallback(){
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                //但多次指纹密码验证错误后，进入此方法；并且，不能短时间内调用指纹验证
//                请进行指纹验证 的提醒
//                if (imageDialog!=null){
//                    imageDialog.dismiss();
//                    imageDialog = null;
//                }
                imageDialogDismiss();
                Toast.makeText(mainActivity, errString, Toast.LENGTH_SHORT).show();
                fingerprintHelper.showAuthenticationScreen(mainActivity);
            }
            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                Toast.makeText(mainActivity, helpString, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
//                if (imageDialog!=null){
//                    imageDialog.dismiss();
//                    imageDialog = null;
//                }
                imageDialogDismiss();
//                noteAdapter.openNote();

                noteTmp.setPasswordChecked(true);
                init();
            }
            @Override
            public void onAuthenticationFailed() {
//                if (imageDialog!=null){
//                    imageDialog.dismiss();
//                    imageDialog = null;
//                }
                imageDialogDismiss();
                Toast.makeText(mainActivity, "指纹识别失败", Toast.LENGTH_SHORT).show();
            }
        };
        fingerprintHelper.setSelfCancelled(authenticationCallback);
        fingerprintHelper.permissionsInit();
    }

    void imageDialogDismiss(){
        if (imageDialog!=null){
            imageDialog.dismiss();
            imageDialog = null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().post(new SwitchFragmentEvent(SwitchFragmentEvent.Direction.CHILDREN));
    }

    @Override
    public void onStop() {
        super.onStop();
        GeocodeHelper.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        activityPausing = false;
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mainActivity.getToolbar().setNavigationOnClickListener(v -> navigateUp());

        // Force the navigation drawer to stay opened if tablet mode is on, otherwise has to stay closed
        if (NavigationDrawerFragment.isDoublePanelActive()) {
            mainActivity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        } else {
            mainActivity.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        restoreTempNoteAfterOrientationChange(savedInstanceState);

        addSketchedImageIfPresent();

        // Ensures that Detail Fragment always have the back Arrow when it's created
        EventBus.getDefault().post(new SwitchFragmentEvent(SwitchFragmentEvent.Direction.CHILDREN));
        init();

        setHasOptionsMenu(true);
        setRetainInstance(false);
//        it.feio.android.omninotes.widget.EditText search_text
//                = mainActivity.findViewById(R.id.search_text);
        search_text = mainActivity.findViewById(R.id.search_text);
//        search_text.onchang
//        EditText textChange
//        search_text.  onTextChanged();
//        search_text.onTextChanged()
//        search_text.addTextChangedListener(textwatcher);
    }

    TextWatcher textwatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        //参数名也可能是其他命名。
        @Override
        public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {

        }

        //        @Override
//        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//        }
//        https://blog.csdn.net/weixin_44614751/article/details/100597780
        @Override
        public void afterTextChanged(Editable editable) {
//            search(editable.toString());
//            搜索 会跳转 ,这样搜索文字的 的光标跳走了
//            除非了 跳转了之后 再调回来 这样可以吗
        }
    };


    android.widget.EditText search_text;
//    EditText 撤销

    private void addSketchedImageIfPresent() {
        if (mainActivity.getSketchUri() != null) {
            Attachment mAttachment = new Attachment(mainActivity.getSketchUri(), MIME_TYPE_SKETCH);
            addAttachment(mAttachment);
            mainActivity.setSketchUri(null);
            // Removes previous version of edited image
            if (sketchEdited != null) {
                noteTmp.getAttachmentsList().remove(sketchEdited);
                sketchEdited = null;
            }
        }
    }

    //  方向更改后恢复临时笔记
    private void restoreTempNoteAfterOrientationChange(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
//      保存的实例状态
            noteTmp = savedInstanceState.getParcelable("noteTmp");
            note = savedInstanceState.getParcelable("note");
            noteOriginal = savedInstanceState.getParcelable("noteOriginal");
            attachmentUri = savedInstanceState.getParcelable("attachmentUri");
            orientationChanged = savedInstanceState.getBoolean("orientationChanged");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (noteTmp != null) {
            noteTmp.setTitle(getNoteTitle());
            noteTmp.setContent(getNoteContent());
            outState.putParcelable("noteTmp", noteTmp);
            outState.putParcelable("note", note);
            outState.putParcelable("noteOriginal", noteOriginal);
            outState.putParcelable("attachmentUri", attachmentUri);
            outState.putBoolean("orientationChanged", orientationChanged);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();

        //to prevent memory leak fragment keep refernce of event but until deregister
//    为了防止内存泄漏，请保留对事件的引用，但直到取消注册为止
        EventBus.getDefault().unregister(this);

        activityPausing = true;

        // Checks "goBack" value to avoid performing a double saving
        if (!goBack) {
            saveNote(this);
        }

        if (toggleChecklistView != null) {
            KeyboardUtils.hideKeyboard(toggleChecklistView);
            binding.contentWrapper.clearFocus();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation != newConfig.orientation) {
            orientationChanged = true;
        }
    }

    private void init() {

        // Handling of Intent actions
        handleIntents();

        if (noteOriginal == null) {
            noteOriginal = getArguments().getParcelable(INTENT_NOTE);
        }

        if (note == null) {
            note = new Note(noteOriginal);
        }

        if (noteTmp == null) {
            noteTmp = new Note(note);
        }

        if (noteTmp.isLocked() && !noteTmp.isPasswordChecked()) {
            checkNoteLock(noteTmp);
            return;
        }

        initViews();
    }

//    public void startListening(FingerprintManager.CryptoObject cryptoObject,) {
//        //android studio 上，没有这个会报错
////        mainActivity
////        FragmentActivity activity = getActivity();
//        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(mainActivity, "没有指纹识别权限", Toast.LENGTH_SHORT).show();
//        }
//        manager.authenticate(cryptoObject, mCancellationSignal, 0, mSelfCancelled, null);
//    }
//
//    public void startListening(FingerprintManager.CryptoObject cryptoObject) {
//        //android studio 上，没有这个会报错
////        mainActivity
////        FragmentActivity activity = getActivity();
//        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(mainActivity, "没有指纹识别权限", Toast.LENGTH_SHORT).show();
//        }
//        manager.authenticate(cryptoObject, mCancellationSignal, 0, mSelfCancelled, null);
//    }
//
//    private void showAuthenticationScreen() {
//        Intent intent = mKeyManager.createConfirmDeviceCredentialIntent("finger", "指纹识别失败次数过多，请输入锁屏密码");
//        if (intent != null) {
//            startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
//        }
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
////            请求\u代码\u确认\u设备\u凭证
//            // Challenge completed, proceed with using cipher
//            if (resultCode == RESULT_OK) {
//                noteAdapter.openNote();
//            } else {
//                Toast.makeText(this, "密码验证失败", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    FingerprintManager.AuthenticationCallback mSelfCancelled = new FingerprintManager.AuthenticationCallback() {
//        @Override
//        public void onAuthenticationError(int errorCode, CharSequence errString) {
//            //但多次指纹密码验证错误后，进入此方法；并且，不能短时间内调用指纹验证
//            Toast.makeText(MainActivity.this, errString, Toast.LENGTH_SHORT).show();
//            showAuthenticationScreen();
//        }
//        @Override
//        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
//            //出现错误，可能是手指移动过快等问题　　　　　　　　Toast.makeText(MainActivity.this, helpString, Toast.LENGTH_SHORT).show();
//        }
//        @Override
//        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
//            //认证成功，可以进入加密文档
//            noteAdapter.openNote();
//        }
//        @Override
//        public void onAuthenticationFailed() {
//            Toast.makeText(MainActivity.this, "指纹识别失败", Toast.LENGTH_SHORT).show();
//
//        }
//    };


    /**
     * Checks note lock and password before showing note content
     * 在显示便笺内容之前检查便笺锁和密码
     */
    private void checkNoteLock(Note note) {
        // If note is locked security password will be requested
//        初始化的时候 调用，如果想要用 指纹呢，默认指纹，点击可以密码解锁
        if (note.isLocked()
                && Prefs.getString(PREF_PASSWORD, null) != null
                && !Prefs.getBoolean("settings_password_access", false)) {
            fingerprintHelper.showAuthenticationScreen();
            PasswordHelper.requestPassword(mainActivity, passwordConfirmed -> {
                switch (passwordConfirmed) {
                    case SUCCEED:
                        noteTmp.setPasswordChecked(true);
                        init();
                        break;
                    case FAIL:
                        goBack = true;
                        goHome();
                        break;
                    case RESTORE:
                        goBack = true;
                        goHome();
                        PasswordHelper.resetPassword(mainActivity);
                        break;
                }
            });
        } else {
            noteTmp.setPasswordChecked(true);
            init();
        }
    }

    private void handleIntents() {
        Intent i = mainActivity.getIntent();

        if (IntentChecker.checkAction(i, ACTION_MERGE)) {
            noteOriginal = new Note();
            note = new Note(noteOriginal);
            noteTmp = getArguments().getParcelable(INTENT_NOTE);
            if (i.getStringArrayListExtra("merged_notes") != null) {
                mergedNotesIds = i.getStringArrayListExtra("merged_notes");
            }
        }

        // Action called from home shortcut
        if (IntentChecker.checkAction(i, ACTION_SHORTCUT, ACTION_NOTIFICATION_CLICK)) {
            afterSavedReturnsToList = false;
            noteOriginal = DbHelper.getInstance().getNote(i.getLongExtra(INTENT_KEY, 0));
            // Checks if the note pointed from the shortcut has been deleted
            try {
                note = new Note(noteOriginal);
                noteTmp = new Note(noteOriginal);
            } catch (NullPointerException e) {
                mainActivity.showToast(getText(R.string.shortcut_note_deleted), Toast.LENGTH_LONG);
                mainActivity.finish();
            }
        }

        // Check if is launched from a widget
        if (IntentChecker.checkAction(i, ACTION_WIDGET, ACTION_WIDGET_TAKE_PHOTO)) {

            afterSavedReturnsToList = false;
            showKeyboard = true;

            //  with tags to set tag
            if (i.hasExtra(INTENT_WIDGET)) {
                String widgetId = i.getExtras().get(INTENT_WIDGET).toString();
                String sqlCondition = Prefs.getString(PREF_WIDGET_PREFIX + widgetId, "");
                String categoryId = TextHelper.checkIntentCategory(sqlCondition);
                if (categoryId != null) {
                    Category category;
                    try {
                        category = DbHelper.getInstance().getCategory(parseLong(categoryId));
                        noteTmp = new Note();
                        noteTmp.setCategory(category);
                    } catch (NumberFormatException e) {
                        LogDelegate.e("Category with not-numeric value!", e);
                    }
                }
            }

            // Sub-action is to take a photo
            if (IntentChecker.checkAction(i, ACTION_WIDGET_TAKE_PHOTO)) {
                takePhoto();
            }
        }

        if (IntentChecker.checkAction(i, ACTION_FAB_TAKE_PHOTO)) {
            takePhoto();
        }

        // Handles third party apps requests of sharing
        if (IntentChecker
                .checkAction(i, Intent.ACTION_SEND, Intent.ACTION_SEND_MULTIPLE, INTENT_GOOGLE_NOW)
                && i.getType() != null) {

            afterSavedReturnsToList = false;

            if (noteTmp == null) {
                noteTmp = new Note();
            }

            // Text title
            String title = i.getStringExtra(Intent.EXTRA_SUBJECT);
            if (title != null) {
                noteTmp.setTitle(title);
            }

            // Text content
            String content = i.getStringExtra(Intent.EXTRA_TEXT);
            if (content != null) {
                noteTmp.setContent(content);
            }

            importAttachments(i);

        }

        if (IntentChecker
                .checkAction(i, Intent.ACTION_MAIN, ACTION_WIDGET_SHOW_LIST, ACTION_SHORTCUT_WIDGET,
                        ACTION_WIDGET)) {
            showKeyboard = true;
        }

        i.setAction(null);
    }

    private void importAttachments(Intent i) {

        if (!i.hasExtra(Intent.EXTRA_STREAM)) {
            return;
        }

        if (i.getExtras().get(Intent.EXTRA_STREAM) instanceof Uri) {
            Uri uri = i.getParcelableExtra(Intent.EXTRA_STREAM);
            // Google Now passes Intent as text but with audio recording attached the case must be handled like this
            if (!INTENT_GOOGLE_NOW.equals(i.getAction())) {
                String name = FileHelper.getNameFromUri(mainActivity, uri);
                new AttachmentTask(this, uri, name, this).execute();
            }
        } else {
            ArrayList<Uri> uris = i.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            for (Uri uriSingle : uris) {
                String name = FileHelper.getNameFromUri(mainActivity, uriSingle);
                new AttachmentTask(this, uriSingle, name, this).execute();
            }
        }
    }

    @SuppressLint("NewApi")
    private void initViews() {
        // Sets onTouchListener to the whole activity to swipe notes
//    将onTouchListener设置为整个活动以滑动便笺
        binding.detailRoot.setOnTouchListener(this);

        // Color of tag marker if note is tagged a function is active in preferences
        setTagMarkerColor(noteTmp.getCategory());

        initViewTitle();

        initViewContent();

        initViewLocation();

        initViewAttachments();

        initViewReminder();

        initViewFooter();
    }

    private void initViewFooter() {
        String creation = DateHelper
                .getFormattedDate(noteTmp.getCreation(), Prefs.getBoolean(PREF_PRETTIFIED_DATES, true));
        binding.creation
                .append(creation.length() > 0 ? getString(R.string.creation) + " " + creation : "");
        if (binding.creation.getText().length() == 0) {
            binding.creation.setVisibility(View.GONE);
        }

        String lastModification = DateHelper
                .getFormattedDate(noteTmp.getLastModification(), Prefs.getBoolean(
                        PREF_PRETTIFIED_DATES, true));
        binding.lastModification
                .append(lastModification.length() > 0 ? getString(R.string.last_update) + " " +
                        lastModification : "");
        if (binding.lastModification.getText().length() == 0) {
            binding.lastModification.setVisibility(View.GONE);
        }
    }

    private void initViewReminder() {
        binding.fragmentDetailContent.reminderLayout.setOnClickListener(v -> {
            ReminderPickers reminderPicker = new ReminderPickers(mainActivity, mFragment);
            reminderPicker.pick(DateUtils.getPresetReminder(noteTmp.getAlarm()), noteTmp
                    .getRecurrenceRule());
        });

        binding.fragmentDetailContent.reminderLayout.setOnLongClickListener(v -> {
            MaterialDialog dialog = new MaterialDialog.Builder(mainActivity)
                    .content(R.string.remove_reminder)
                    .positiveText(R.string.ok)
                    .onPositive((dialog1, which) -> {
                        ReminderHelper.removeReminder(OmniNotes.getAppContext(), noteTmp);
                        noteTmp.setAlarm(null);
                        binding.fragmentDetailContent.reminderIcon
                                .setImageResource(R.drawable.ic_alarm_black_18dp);
                        binding.fragmentDetailContent.datetime.setText("");
                    }).build();
            dialog.show();
            return true;
        });

        // Reminder
        String reminderString = initReminder(noteTmp);
        if (!TextUtils.isEmpty(reminderString)) {
            binding.fragmentDetailContent.reminderIcon
                    .setImageResource(R.drawable.ic_alarm_add_black_18dp);
            binding.fragmentDetailContent.datetime.setText(reminderString);
        }
    }

    private void initViewLocation() {

        DetailFragment detailFragment = this;

        if (isNoteLocationValid()) {
            if (TextUtils.isEmpty(noteTmp.getAddress())) {
                //FIXME: What's this "sasd"?
                GeocodeHelper.getAddressFromCoordinates(new Location("sasd"), detailFragment);
            } else {
                binding.fragmentDetailContent.location.setText(noteTmp.getAddress());
                binding.fragmentDetailContent.location.setVisibility(View.VISIBLE);
            }
        }

        // Automatic location insertion
        if (Prefs.getBoolean(PREF_AUTO_LOCATION, false) && noteTmp.get_id() == null) {
            getLocation(detailFragment);
        }

        binding.fragmentDetailContent.location.setOnClickListener(v -> {
            String uriString = "geo:" + noteTmp.getLatitude() + ',' + noteTmp.getLongitude()
                    + "?q=" + noteTmp.getLatitude() + ',' + noteTmp.getLongitude();
            Intent locationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
            if (!IntentChecker.isAvailable(mainActivity, locationIntent, null)) {
                uriString = "http://maps.google.com/maps?q=" + noteTmp.getLatitude() + ',' + noteTmp
                        .getLongitude();
                locationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
            }
            startActivity(locationIntent);
        });
        binding.fragmentDetailContent.location.setOnLongClickListener(v -> {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(mainActivity);
            builder.content(R.string.remove_location);
            builder.positiveText(R.string.ok);
            builder.onPositive((dialog, which) -> {
                noteTmp.setLatitude("");
                noteTmp.setLongitude("");
                fade(binding.fragmentDetailContent.location, false);
            });
            MaterialDialog dialog = builder.build();
            dialog.show();
            return true;
        });
    }

    private void getLocation(OnGeoUtilResultListener onGeoUtilResultListener) {
        PermissionsHelper
                .requestPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION, R.string
                                .permission_coarse_location, binding.snackbarPlaceholder,
                        () -> GeocodeHelper.getLocation(onGeoUtilResultListener));
    }

    private void initViewAttachments() {
        // Attachments position based on preferences
        if (Prefs.getBoolean(PREF_ATTACHMENTS_ON_BOTTOM, false)) {
            binding.detailAttachmentsBelow.inflate();
        } else {
            binding.detailAttachmentsAbove.inflate();
        }
        mGridView = binding.detailRoot.findViewById(R.id.gridview);

        // Some fields can be filled by third party application and are always shown
        mAttachmentAdapter = new AttachmentAdapter(mainActivity, noteTmp.getAttachmentsList(),
                mGridView);

        // Initialzation of gridview for images
        mGridView.setAdapter(mAttachmentAdapter);
        mGridView.autoresize();

        // Click events for images in gridview (zooms image)
        mGridView.setOnItemClickListener((parent, v, position, id) -> {
            Attachment attachment = (Attachment) parent.getAdapter().getItem(position);
            Uri sharableUri = FileProviderHelper.getShareableUri(attachment);
            Intent attachmentIntent;
            if (MIME_TYPE_FILES.equals(attachment.getMime_type())) {

                attachmentIntent = new Intent(Intent.ACTION_VIEW);
                attachmentIntent.setDataAndType(sharableUri, StorageHelper.getMimeType(mainActivity,
                        sharableUri));
                attachmentIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent
                        .FLAG_GRANT_WRITE_URI_PERMISSION);
                if (IntentChecker
                        .isAvailable(mainActivity.getApplicationContext(), attachmentIntent, null)) {
                    startActivity(attachmentIntent);
                } else {
                    mainActivity.showMessage(R.string.feature_not_available_on_this_device, ONStyle.WARN);
                }

                // Media files will be opened in internal gallery
            } else if (MIME_TYPE_IMAGE.equals(attachment.getMime_type())
                    || MIME_TYPE_SKETCH.equals(attachment.getMime_type())
                    || MIME_TYPE_VIDEO.equals(attachment.getMime_type())) {
                // Title
                noteTmp.setTitle(getNoteTitle());
                noteTmp.setContent(getNoteContent());
                String title1 = TextHelper.parseTitleAndContent(mainActivity,
                        noteTmp)[0].toString();
                // Images
                int clickedImage = 0;
                ArrayList<Attachment> images = new ArrayList<>();
                for (Attachment mAttachment : noteTmp.getAttachmentsList()) {
                    if (MIME_TYPE_IMAGE.equals(mAttachment.getMime_type())
                            || MIME_TYPE_SKETCH.equals(mAttachment.getMime_type())
                            || MIME_TYPE_VIDEO.equals(mAttachment.getMime_type())) {
                        images.add(mAttachment);
                        if (mAttachment.equals(attachment)) {
                            clickedImage = images.size() - 1;
                        }
                    }
                }
                // Intent
                attachmentIntent = new Intent(mainActivity, GalleryActivity.class);
                attachmentIntent.putExtra(GALLERY_TITLE, title1);
                attachmentIntent.putParcelableArrayListExtra(GALLERY_IMAGES, images);
                attachmentIntent.putExtra(GALLERY_CLICKED_IMAGE, clickedImage);
                startActivity(attachmentIntent);

            } else if (MIME_TYPE_AUDIO.equals(attachment.getMime_type())) {
                playback(v, attachment.getUri());
            }

        });

        mGridView.setOnItemLongClickListener((parent, v, position, id) -> {
            // To avoid deleting audio attachment during playback
            if (mPlayer != null) {
                return false;
            }
            List<String> items = Arrays
                    .asList(getResources().getStringArray(R.array.attachments_actions));
            if (!MIME_TYPE_SKETCH.equals(mAttachmentAdapter.getItem(position).getMime_type())) {
                items = items.subList(0, items.size() - 1);
            }
            Attachment attachment = mAttachmentAdapter.getItem(position);
            new MaterialDialog.Builder(mainActivity)
                    .title(attachment.getName() + " (" + AttachmentsHelper.getSize(attachment) + ")")
                    .items(items.toArray(new String[items.size()]))
                    .itemsCallback((materialDialog, view, i, charSequence) ->
                            performAttachmentAction(position, i))
                    .build()
                    .show();
            return true;
        });
    }

    /**
     * Performs an action when long-click option is selected
     *
     * @param i item index
     */
    private void performAttachmentAction(int attachmentPosition, int i) {
        switch (getResources().getStringArray(R.array.attachments_actions_values)[i]) {
            case "share":
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                Attachment attachment = mAttachmentAdapter.getItem(attachmentPosition);
                shareIntent
                        .setType(StorageHelper.getMimeType(OmniNotes.getAppContext(), attachment.getUri()));
                shareIntent.putExtra(Intent.EXTRA_STREAM, FileProviderHelper.getShareableUri(attachment));
                if (IntentChecker.isAvailable(OmniNotes.getAppContext(), shareIntent, null)) {
                    startActivity(shareIntent);
                } else {
                    mainActivity.showMessage(R.string.feature_not_available_on_this_device, ONStyle.WARN);
                }
                break;
            case "delete":
                removeAttachment(attachmentPosition);
                mAttachmentAdapter.notifyDataSetChanged();
                mGridView.autoresize();
                break;
            case "delete all":
                new MaterialDialog.Builder(mainActivity)
                        .title(R.string.delete_all_attachments)
                        .positiveText(R.string.confirm)
                        .onPositive((materialDialog, dialogAction) -> removeAllAttachments())
                        .build()
                        .show();
                break;
            case "edit":
                takeSketch(mAttachmentAdapter.getItem(attachmentPosition));
                break;
            default:
                LogDelegate.w("No action available");
        }
    }

    private void initViewTitle() {
        binding.detailTitle.setText(noteTmp.getTitle());
        binding.detailTitle.gatherLinksForText();
        binding.detailTitle.setOnTextLinkClickListener(textLinkClickListener);
        // To avoid dropping here the  dragged checklist items
        binding.detailTitle.setOnDragListener((v, event) -> {
//					((View)event.getLocalState()).setVisibility(View.VISIBLE);
            return true;
        });
        //When editor action is pressed focus is moved to last character in content field
        binding.detailTitle.setOnEditorActionListener((v, actionId, event) -> {
            binding.fragmentDetailContent.detailContent.requestFocus();
            binding.fragmentDetailContent.detailContent
                    .setSelection(binding.fragmentDetailContent.detailContent.getText().length());
            return false;
        });
        requestFocus(binding.detailTitle);
    }

    int getIdxFront(String string, String find) {
        int idxFront = string.indexOf(find);
        return idxFront == -1 ? 0 : idxFront;
    }

    int getIdxBehind(String string, String find) {
        int idxBehind = string.indexOf(find);
        return idxBehind == -1 ? string.length() : idxBehind;
    }

    private static final String TAG = "DetailFragment";


    private ForegroundColorSpan getRedSpan() {
        return getColorSpan("#ff3c2a");
//        return new  ForegroundColorSpan(Color.parseColor("#ff3c2a"));
    }

    private ForegroundColorSpan getColorSpan(String colorStr) {
//        Color.RED;
//       new  BackgroundColorSpan(Color.YELLOW);
        return new ForegroundColorSpan(Color.parseColor(colorStr));
    }

    /**
     * Performs the Regex Comparison for the Pattern and adds them to listOfLinks array list
     *
     * @param links
     * @param words
     * @param pattern
     */
    private void gatherLinks2(ArrayList<Hyperlink> links,
                              String[] words, Pattern pattern) {
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
                Hyperlink spec = new Hyperlink();

                spec.textSpan = word;
                spec.span = new InternalURLSpan(spec.textSpan.toString());
                spec.start = start;
                spec.end = end;

                links.add(spec);
            }

            start = end + 1;
        }

    }


    /**
     * Collects the Links depending upon the Pattern that we supply
     * and add the links to the ArrayList of the links
     */
    public void gatherLinksForText(EditText editText, SpannableString linkableText) {
//        String text = getText().toString();
        String text = editText.getText().toString();
//        linkableText = new SpannableString(text);
        ArrayList<Hyperlink> listOfLinks = new ArrayList<>();
//        listOfLinks 这个需要使用啊 类里面是要的

        for (Pattern pattern : RegexPatternsConstants.patterns) {
//			gatherLinks(listOfLinks, linkableText, pattern);
//            editText.  gatherLinks2(listOfLinks, text.split("\\s"), pattern);
//            gatherLinks2(editText,listOfLinks, text.split("\\s"), pattern);
            gatherLinks2(listOfLinks, text.split("\\s"), pattern);
        }

        for (int i = 0; i < listOfLinks.size(); i++) {
            Hyperlink linkSpec = listOfLinks.get(i);

            // this process here makes the Clickable Links from the text
            linkableText.setSpan(linkSpec.span, linkSpec.start, linkSpec.end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // sets the text for the TextView with enabled links
        editText.setText(linkableText);
    }


    private void initViewContent() {
//    SpannableString("红颜色:红颜色");
        String content = noteTmp.getContent();

        SpannableString spanColor = new SpannableString(content);
//        SpannableString 没有颜色
//    spanColor.setSpan();
//    https://blog.csdn.net/jiangtea/article/details/54098123
//    int idxFront = content.indexOf("晚上");
//    int idxBehind = content.indexOf("下");
//    spanColor.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3c2a")),
//            idxFront==-1?0:idxFront, idxBehind==-1?content.length():idxBehind, 0);
//        https://www.jianshu.com/p/bdfcaf0cd323
//        Log.i(TAG,"设置字");
//        Log.i(TAG,"content  "+content);
//        int idxFront = getIdxFront(content, "晚上");
//        int idxBehind =getIdxBehind(content, "下");
//        Log.i(TAG,"idxFront "+idxFront);
//        Log.i(TAG,"idxBehind "+idxBehind);
////        如果前面的没有，那就是0  ，这是找到第一个啊
//        spanColor.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3c2a")),
//                idxFront,idxBehind, 0);

//    mTv1.setText(spanColor);
//    indexOf java
//————————————————
//    版权声明：本文为CSDN博主「jiangtea」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//    原文链接：https://blog.csdn.net/jiangtea/article/details/54098123
//    binding.fragmentDetailContent.detailContent.setText(noteTmp.getContent());
//        binding.fragmentDetailContent.detailContent.setText(spanColor);


        // Restore checklist
//    恢复检查表
        toggleChecklistView = binding.fragmentDetailContent.detailContent;

        binding.fragmentDetailContent.detailContent.setText(content);
//        是因为这里面 set span 所以有问题吗
//        确实是因为这个原因啊
//        setText SpannableString 之后有 gatherLinksForText
        binding.fragmentDetailContent.detailContent.setLinkableText(spanColor);
        binding.fragmentDetailContent.detailContent.gatherLinksForText();
//        有些类
//        binding.fragmentDetailContent.detailContent.set
//        gatherLinksForText(  binding.fragmentDetailContent.detailContent,spanColor);
        // TODO: 2021/10/23 测试能不能用啊
        binding.fragmentDetailContent.detailContent.setOnTextLinkClickListener(textLinkClickListener);
        // Avoids focused line goes under the keyboard
//    避免将焦点线放在键盘下
        binding.fragmentDetailContent.detailContent.addTextChangedListener(this);

        if (noteTmp.isChecklist()) {
            noteTmp.setChecklist(false);
            AlphaManager.setAlpha(toggleChecklistView, 0);
            toggleChecklist2();
        }
//        binding.fragmentDetailContent.detailContent.setOnTextLinkClickListener();
//        binding.fragmentDetailContent.detailContent.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                com.neopixl.pixlui.components.edittext.EditText editText=
//                        (com.neopixl.pixlui.components.edittext.EditText)view;
////                editText.getTextLink();
////                editText.gettextLi
//            }
//        });
    }

    /**
     * Force focus and shows soft keyboard. Only happens if it's a new note, without shared content.
     * {@link showKeyboard} is used to check if the note is created from shared content.
     */
    @SuppressWarnings("JavadocReference")
    private void requestFocus(final EditText view) {
        if (note.get_id() == null && !noteTmp.isChanged(note) && showKeyboard) {
            KeyboardUtils.showKeyboard(view);
        }
    }

    /**
     * Colors tag marker in note's title and content elements
     */
    private void setTagMarkerColor(Category tag) {

        String colorsPref = Prefs.getString("settings_colors_app", PREF_COLORS_APP_DEFAULT);

        // Checking preference
        if (!"disabled".equals(colorsPref)) {

            // Choosing target view depending on another preference
            ArrayList<View> target = new ArrayList<>();
            if ("complete".equals(colorsPref)) {
                target.add(binding.titleWrapper);
                target.add(binding.contentWrapper);
            } else {
                target.add(binding.tagMarker);
            }

            // Coloring the target
            if (tag != null && tag.getColor() != null) {
                for (View view : target) {
                    view.setBackgroundColor(parseInt(tag.getColor()));
                }
            } else {
                for (View view : target) {
                    view.setBackgroundColor(Color.parseColor("#00000000"));
                }
            }
        }
    }

    private void displayLocationDialog() {
        getLocation(new OnGeoUtilResultListenerImpl(mainActivity, mFragment, noteTmp));
    }

    @Override
    public void onLocationRetrieved(Location location) {
        if (location == null) {
            mainActivity.showMessage(R.string.location_not_found, ONStyle.ALERT);
        }
        if (location != null) {
            noteTmp.setLatitude(location.getLatitude());
            noteTmp.setLongitude(location.getLongitude());
            if (!TextUtils.isEmpty(noteTmp.getAddress())) {
                binding.fragmentDetailContent.location.setVisibility(View.VISIBLE);
                binding.fragmentDetailContent.location.setText(noteTmp.getAddress());
            } else {
                GeocodeHelper.getAddressFromCoordinates(location, mFragment);
            }
        }
    }

    @Override
    public void onLocationUnavailable() {
        mainActivity.showMessage(R.string.location_not_found, ONStyle.ALERT);
    }

    @Override
    public void onAddressResolved(String address) {
        if (TextUtils.isEmpty(address)) {
            if (!isNoteLocationValid()) {
                mainActivity.showMessage(R.string.location_not_found, ONStyle.ALERT);
                return;
            }
            address = noteTmp.getLatitude() + ", " + noteTmp.getLongitude();
        }
        if (!GeocodeHelper.areCoordinates(address)) {
            noteTmp.setAddress(address);
        }
        binding.fragmentDetailContent.location.setVisibility(View.VISIBLE);
        binding.fragmentDetailContent.location.setText(address);
        fade(binding.fragmentDetailContent.location, true);
    }

    @Override
    public void onCoordinatesResolved(Location location, String address) {
        if (location != null) {
            noteTmp.setLatitude(location.getLatitude());
            noteTmp.setLongitude(location.getLongitude());
            noteTmp.setAddress(address);
            binding.fragmentDetailContent.location.setVisibility(View.VISIBLE);
            binding.fragmentDetailContent.location.setText(address);
            fade(binding.fragmentDetailContent.location, true);
        } else {
            mainActivity.showMessage(R.string.location_not_found, ONStyle.ALERT);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.findViewById(R.id.btn_next).setOnClickListener(this);
            activity.findViewById(R.id.btn_pre).setOnClickListener(this);

            activity.findViewById(R.id.btn_search).setOnClickListener(this);
        }


    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        // Closes search view if left open in List fragment
        MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        if (searchMenuItem != null) {
            searchMenuItem.collapseActionView();
        }

        boolean newNote = noteTmp.get_id() == null;

        menu.findItem(R.id.menu_checklist_on).setVisible(!noteTmp.isChecklist());
        menu.findItem(R.id.menu_checklist_off).setVisible(noteTmp.isChecklist());
        menu.findItem(R.id.menu_checklist_moveToBottom)
                .setVisible(noteTmp.isChecklist() && mChecklistManager.getCheckedCount() > 0);
        menu.findItem(R.id.menu_lock).setVisible(!noteTmp.isLocked());
        menu.findItem(R.id.menu_unlock).setVisible(noteTmp.isLocked());
        // If note is trashed only this options will be available from menu
        if (noteTmp.isTrashed()) {
            menu.findItem(R.id.menu_untrash).setVisible(true);
            menu.findItem(R.id.menu_delete).setVisible(true);
            // Otherwise all other actions will be available
        } else {
            // Temporary removed until fixed on Oreo and following
            menu.findItem(R.id.menu_add_shortcut).setVisible(!newNote);
            menu.findItem(R.id.menu_pin_note).setVisible(!newNote);
            menu.findItem(R.id.menu_archive).setVisible(!newNote && !noteTmp.isArchived());
            menu.findItem(R.id.menu_unarchive).setVisible(!newNote && noteTmp.isArchived());
            menu.findItem(R.id.menu_trash).setVisible(!newNote);
        }
    }

    @SuppressLint("NewApi")
    private boolean goHome() {
        stopPlaying();

        // The activity has managed a shared intent from third party app and
        // performs a normal onBackPressed instead of returning back to ListActivity
        if (!afterSavedReturnsToList) {
            if (!TextUtils.isEmpty(exitMessage)) {
                mainActivity.showToast(exitMessage, Toast.LENGTH_SHORT);
            }
            mainActivity.finish();

        } else {

            if (!TextUtils.isEmpty(exitMessage) && exitCroutonStyle != null) {
                mainActivity.showMessage(exitMessage, exitCroutonStyle);
            }

            // Otherwise the result is passed to ListActivity
            if (mainActivity != null) {
                mainActivity.getSupportFragmentManager();
                mainActivity.getSupportFragmentManager().popBackStack();
                if (mainActivity.getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(true);
                    if (mainActivity.getDrawerToggle() != null) {
                        mainActivity.getDrawerToggle().setDrawerIndicatorEnabled(true);
                    }
                    EventBus.getDefault().post(new SwitchFragmentEvent(SwitchFragmentEvent.Direction.PARENT));
                }
            }
        }

        return true;
    }

    /**
     * 将焦点放在输入框中
     * 如果想要选中输入框中的文本必须要将焦点放在输入框中
     * 如果想要焦点在输入框中必须设置下面三个方法
     *
     * @param editText https://www.cnblogs.com/x_wukong/p/4450424.html
     */
    private void setEditFocus(android.widget.EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

//    List<Region> matchPatternStr(    String string ,  String  patternStr){
////        String line = "2016年10月11日我們去哪里玩近期我們近日，次年，同年等等，10月，2019年，3月4日";
//
////        String  patternStr;
////        Pattern datePattern = Pattern.compile("\\d{4}年\\d{1,2}月\\d{1,2}日|\\d{4}年\\d{1,2}月|\\d{1,2}月\\d{1,2}日|\\d{4}年|\\d{1,2}月|同年|次年|近日|近期");
//        Pattern datePattern = Pattern.compile(patternStr);
//
//        Matcher dateMatcher = datePattern.matcher(string);
//
////        int dateCount = 0;
//
////        List<int[]>list=new ArrayList<>();
//        List<Region> regions=new ArrayList<>();
//        while(dateMatcher.find()) {
////            dateMatcher.regionStart()
////            找到了 开始
////            dateMatcher.regionStart()
////            int[]
//            Region region=new Region(  dateMatcher.regionStart(),  dateMatcher.regionEnd());
//            regions.add(region);
////            String group = dateMatcher.group();
////            System.out.println(dateMatcher.group());
//
////            ++dateCount;
//
//        }
//        return regions;
////————————————————
////        版权声明：本文为CSDN博主「weixin_39999222」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
////        原文链接：https://blog.csdn.net/weixin_39999222/article/details/114782840
//    }

    void next() {
        if (regions == null) return;
        nowSearchedIdx++;
//        ||nowSearchedIdx<0
        if (nowSearchedIdx >= regions.size()) {
            Log.i(TAG, "越界");
            nowSearchedIdx = 0;
//            return;
        }
        if (regions.size() > 0) {
            android.widget.EditText detailContent = getDetailContent();
            resetSelection(detailContent, regions.get(nowSearchedIdx).getStart());
        }

//        search(true);
        // TODO: 2021/10/24  resetSelection
    }

    void pre() {
        if (regions == null) return;
        nowSearchedIdx--;
//        ||nowSearchedIdx<0
        if (nowSearchedIdx < 0) {
            Log.i(TAG, "越界");
            nowSearchedIdx = regions.size() - 1;
//            return;
        }
        if (regions.size() > 0) {
            android.widget.EditText detailContent = getDetailContent();
            resetSelection(detailContent, regions.get(nowSearchedIdx).getStart());
        }

//        search(false);
    }

    String lastSearchStr = "";
    int nowSearchedIdx = 0;

    void search(boolean next) {
        //        之前的 颜色还在
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Log.i(TAG, "没找到");
            return;
        }
        it.feio.android.omninotes.widget.EditText search_text = activity.findViewById(R.id.search_text);
        Editable searchTextText = search_text.getText();
        String searchTextTextStr = searchTextText.toString();
//        String content = noteTmp.getContent();
        it.feio.android.omninotes.widget.EditText detail_content
                = activity.findViewById(R.id.detail_content);
        Editable text = detail_content.getText();
//        text.clearSpans();
//        安卓 文字显示 行
//        text.
//        detail_content.focusSearch()
        int selectionStart = detail_content.getSelectionStart();
        int selectionEnd = detail_content.getSelectionEnd();

        Log.i(TAG, "selectionStart " + selectionStart);
        Log.i(TAG, "selectionEnd " + selectionEnd);

//        detail_content.   setSelection(selectionStart+2);
//        detail_content.getSelectionEnd()
//        int scrollX = detail_content.getScrollX();
//        int scrollY = detail_content.getScrollY();
//        int scrollX = detail_content.getscy();
//        Log.i(TAG,"scrollX "+scrollX);
//        Log.i(TAG,"scrollY "+scrollY);
//        搜索的时候 他的 scroll 也没有啊
//        这是 整个 编辑器的 他自己 的 不是他旁边的
//        Object[] spans = text.getSpans();
//        text.removeSpan(text.getSpans());
//        Log.i(TAG,"移动 之后 ");
//                detail_content.scrollTo(scrollX+10,scrollY+10);
//                detail_content.scrollBy(0,10);
//                detail_content.scrollBy(0,-20);
//                是他自己 移动吗
//        Log.i(TAG,"scrollX "+detail_content.getScrollX());
//        Log.i(TAG,"scrollY "+detail_content.getScrollY());
//        detail_content.startNestedScroll()
        String content = text.toString();
//        SpannableString spanColor = new SpannableString(content);
//        SpannableString 没有颜色
//    spanColor.setSpan();
//    https://blog.csdn.net/jiangtea/article/details/54098123
//    int idxFront = content.indexOf("晚上");
//    int idxBehind = content.indexOf("下");
//    spanColor.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3c2a")),
//            idxFront==-1?0:idxFront, idxBehind==-1?content.length():idxBehind, 0);
//        https://www.jianshu.com/p/bdfcaf0cd323
        Log.i(TAG, "设置字");
//        Log.i(TAG,"content  "+content);
//        int idxFront = getIdxFront(content, searchTextTextStr);
//        int idxBehind =searchTextTextStr.length()+idxFront;
//        Log.i(TAG,"idxFront "+idxFront);
//        Log.i(TAG,"idxBehind "+idxBehind);

        List<Region> regions = StrUtil.matchPatternStr(content, searchTextTextStr);
        Log.i(TAG, "regions " + regions);
        int idxFront = 0;
        if (next) {
            if (lastSearchStr.equals(searchTextTextStr)) {
//           如果和之前的一样 那就会去找后面的
//            if(regions.size()>nowSearchedIdx){
////                Region region = regions.get(nowSearchedIdx);
////                idxFront =region.getStart();
////                nowSearchedIdx++;
//            }
//
//            else
                if (regions.size() == nowSearchedIdx) {
                    nowSearchedIdx = 0;
//                Region region = regions.get(nowSearchedIdx);
//                idxFront =region.getStart();
//                nowSearchedIdx++;
                }

            } else {
                nowSearchedIdx = 0;
            }

        } else {
            if (lastSearchStr.equals(searchTextTextStr)) {
                if (0 == nowSearchedIdx) {
                    nowSearchedIdx = regions.size() - 1;
                }

            } else {
//                nowSearchedIdx=regions.size()-1;
                nowSearchedIdx = 0;
            }
        }
//        应该是现在 sele 开头来查找
        lastSearchStr = searchTextTextStr;
        if (regions.size() > nowSearchedIdx && 0 <= nowSearchedIdx) {
            Region region = regions.get(nowSearchedIdx);
            idxFront = region.getStart();
            if (next) {
                nowSearchedIdx++;
            } else {
                nowSearchedIdx--;
            }

        }

//        他会 回到0
//        setSelection 他会 回到0
//        如果前面的没有，那就是0  ，这是找到第一个啊
//如果查询的 词语和上次一样，才需要 变成第二个吧
        resetSelection(detail_content, idxFront);
//        detail_content.clearFocus();
//        int finalIdxFront = idxFront;
//        detail_content.post(new Runnable() {
//            @Override
//            public void run() {
//                detail_content. setSelection(finalIdxFront);
////                detail_content.setSelectionAfterHeaderView();
//            }
//        });
//
//
////        detail_content. setSelection(finalIdxFront);
////        detail_content. setSelection(idxFront);
////        这样子 setSelection 是没有用处的
////        detail_content.setFocusable();
//        setEditFocus(detail_content);
//        要重新 focus

//        Editable editable = text.getText();
//        setEditFocus(text);

//        setEditFocus(detail_content);
//        Selection.setSelection(text,idxFront,detail_content.length());

        //        detail_content.   setSelection(idxFront);
        Log.i(TAG, "设置了选中之后 ");
        Log.i(TAG, "selectionStart " + detail_content.getSelectionStart());
        Log.i(TAG, "selectionEnd " + detail_content.getSelectionEnd());

//        spanColor.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3c2a")),
//                idxFront,idxBehind, 0);
//        https://www.jianshu.com/p/10cfabf503e3
//        searchTextText.setSpan(getRedSpan(),  idxFront,idxBehind, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        searchTextText.setSpan(getRedSpan(),  idxFront,idxBehind, 0);
//        text.setSpan(getRedSpan(),  idxFront,idxBehind, 0);

//    mTv1.setText(spanColor);
//    indexOf java
//————————————————
//    版权声明：本文为CSDN博主「jiangtea」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//    原文链接：https://blog.csdn.net/jiangtea/article/details/54098123
//    binding.fragmentDetailContent.detailContent.setText(noteTmp.getContent());
//        binding.fragmentDetailContent.detailContent.setText(spanColor);


        // Restore checklist
//    恢复检查表
//        toggleChecklistView = binding.fragmentDetailContent.detailContent;

//        binding.fragmentDetailContent.detailContent.setText(content);
////        是因为这里面 set span 所以有问题吗
////        确实是因为这个原因啊
////        setText SpannableString 之后有 gatherLinksForText
//        binding.fragmentDetailContent.detailContent.setLinkableText(spanColor);
//        binding.fragmentDetailContent.detailContent.gatherLinksForText();


        SpannableString spannableString = new SpannableString(content);
//        SpannableString 没有颜色
//    spanColor.setSpan();
//    https://blog.csdn.net/jiangtea/article/details/54098123
//    int idxFront = content.indexOf("晚上");
//    int idxBehind = content.indexOf("下");
//    spanColor.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3c2a")),
//            idxFront==-1?0:idxFront, idxBehind==-1?content.length():idxBehind, 0);
//        https://www.jianshu.com/p/bdfcaf0cd323
//        Log.i(TAG,"设置字");
//        Log.i(TAG,"content  "+content);
//        int idxFront = getIdxFront(content, "晚上");
//        int idxBehind =getIdxBehind(content, "下");
//        Log.i(TAG,"idxFront "+idxFront);
//        Log.i(TAG,"idxBehind "+idxBehind);
//        如果前面的没有，那就是0  ，这是找到第一个啊
//        detail_content.set
        for (Region region : regions) {
//            spannableString.setSpan(getRedSpan(), region.getStart(),region.getEnd(), 0);
//            spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW),
//                    region.getStart(), region.getEnd(), 0);
            spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW),
                    region.getStart(), region.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            spannableString.setSpan(getRedSpan(), idxFront,idxBehind, 0);
        }


//    mTv1.setText(spanColor);
//    indexOf java
//————————————————
//    版权声明：本文为CSDN博主「jiangtea」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//    原文链接：https://blog.csdn.net/jiangtea/article/details/54098123
//    binding.fragmentDetailContent.detailContent.setText(noteTmp.getContent());
//        binding.fragmentDetailContent.detailContent.setText(spanColor);


        // Restore checklist
//    恢复检查表
//        toggleChecklistView = binding.fragmentDetailContent.detailContent;

        binding.fragmentDetailContent.detailContent.setText(content);
//        是因为这里面 set span 所以有问题吗
//        确实是因为这个原因啊
//        setText SpannableString 之后有 gatherLinksForText
        binding.fragmentDetailContent.detailContent.setLinkableText(spannableString);
        binding.fragmentDetailContent.detailContent.gatherLinksForText();
    }

    List<Region> regions;

    android.widget.EditText getDetailContent() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Log.i(TAG, "没找到");
            return null;
        }
//        it.feio.android.omninotes.widget.EditText search_text = activity.findViewById(R.id.search_text);
//        Editable searchTextText= search_text.getText();
//        String searchTextTextStr = searchTextText.toString();
//        String content = noteTmp.getContent();
        it.feio.android.omninotes.widget.EditText detail_content
                = activity.findViewById(R.id.detail_content);
//        Editable text = detail_content.getText();
        return detail_content;
    }

    void resetSelection(android.widget.EditText editText, int index) {

//        他会 回到0
//        setSelection 他会 回到0
//        如果前面的没有，那就是0  ，这是找到第一个啊
//如果查询的 词语和上次一样，才需要 变成第二个吧
        editText.clearFocus();
//        int finalIdxFront = idxFront;
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.setSelection(index);
//                detail_content.setSelectionAfterHeaderView();
            }
        });


//        detail_content. setSelection(finalIdxFront);
//        detail_content. setSelection(idxFront);
//        这样子 setSelection 是没有用处的
//        detail_content.setFocusable();
        setEditFocus(editText);
    }

//    public boolean isFinger() {
//        //用来检查是否有指纹识别权限
////        这个 必须在 activity里面 check
//        if(checkCallingOrSelfPermission(Manifest.permission.USE_FINGERPRINT) ==
//                PackageManager.PERMISSION_GRANTED) {
//            if (!manager.isHardwareDetected()) {
//                Toast.makeText(this, "没有指纹识别模块", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//            if (!mKeyManager.isKeyguardSecure()) {
//                Toast.makeText(this, "没有开启锁屏密码", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//            if (!manager.hasEnrolledFingerprints()) {
//                Toast.makeText(this, "没有录入指纹", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        }
//        return true;
//    }

    void search(String searchStr) {
//        String content = noteTmp.getContent();
        it.feio.android.omninotes.widget.EditText detail_content
                = mainActivity.findViewById(R.id.detail_content);
        Editable text = detail_content.getText();
//        text.clearSpans();
//        安卓 文字显示 行
//        text.
//        detail_content.focusSearch()
        int selectionStart = detail_content.getSelectionStart();
        int selectionEnd = detail_content.getSelectionEnd();

        Log.i(TAG, "selectionStart " + selectionStart);
        Log.i(TAG, "selectionEnd " + selectionEnd);

//        detail_content.   setSelection(selectionStart+2);
//        detail_content.getSelectionEnd()
//        int scrollX = detail_content.getScrollX();
//        int scrollY = detail_content.getScrollY();
//        int scrollX = detail_content.getscy();
//        Log.i(TAG,"scrollX "+scrollX);
//        Log.i(TAG,"scrollY "+scrollY);
//        搜索的时候 他的 scroll 也没有啊
//        这是 整个 编辑器的 他自己 的 不是他旁边的
//        Object[] spans = text.getSpans();
//        text.removeSpan(text.getSpans());
//        Log.i(TAG,"移动 之后 ");
//                detail_content.scrollTo(scrollX+10,scrollY+10);
//                detail_content.scrollBy(0,10);
//                detail_content.scrollBy(0,-20);
//                是他自己 移动吗
//        Log.i(TAG,"scrollX "+detail_content.getScrollX());
//        Log.i(TAG,"scrollY "+detail_content.getScrollY());
//        detail_content.startNestedScroll()
        String content = text.toString();
//        SpannableString spanColor = new SpannableString(content);
//        SpannableString 没有颜色
//    spanColor.setSpan();
//    https://blog.csdn.net/jiangtea/article/details/54098123
//    int idxFront = content.indexOf("晚上");
//    int idxBehind = content.indexOf("下");
//    spanColor.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3c2a")),
//            idxFront==-1?0:idxFront, idxBehind==-1?content.length():idxBehind, 0);
//        https://www.jianshu.com/p/bdfcaf0cd323
        Log.i(TAG, "设置字");
//        Log.i(TAG,"content  "+content);
//        int idxFront = getIdxFront(content, searchTextTextStr);
//        int idxBehind =searchTextTextStr.length()+idxFront;
//        Log.i(TAG,"idxFront "+idxFront);
//        Log.i(TAG,"idxBehind "+idxBehind);

        regions = StrUtil.matchPatternStr(content, searchStr);
//        List<Region> regions = StrUtil.matchPatternStr(content, searchTextTextStr);
        Log.i(TAG, "regions " + regions);
        int idxFront = 0;
//        if(lastSearchStr.equals(searchTextTextStr)){
////           如果和之前的一样 那就会去找后面的
////            if(regions.size()>nowSearchedIdx){
//////                Region region = regions.get(nowSearchedIdx);
//////                idxFront =region.getStart();
//////                nowSearchedIdx++;
////            }
////
////            else
//                if(regions.size()==nowSearchedIdx){
//                nowSearchedIdx=0;
////                Region region = regions.get(nowSearchedIdx);
////                idxFront =region.getStart();
////                nowSearchedIdx++;
//            }
//
//        }else{
//            nowSearchedIdx=0;
//        }

        if (regions.size() > 0) {
            Region region = regions.get(0);
            idxFront = region.getStart();
//            nowSearchedIdx++;
        }

//        if(regions.size()>nowSearchedIdx) {
//            Region region = regions.get(nowSearchedIdx);
//            idxFront = region.getStart();
//            nowSearchedIdx++;
//        }
//        他会 回到0
//        setSelection 他会 回到0
//        如果前面的没有，那就是0  ，这是找到第一个啊
//如果查询的 词语和上次一样，才需要 变成第二个吧
        detail_content.clearFocus();
        int finalIdxFront = idxFront;
        detail_content.post(new Runnable() {
            @Override
            public void run() {
                detail_content.setSelection(finalIdxFront);
//                detail_content.setSelectionAfterHeaderView();
            }
        });


//        detail_content. setSelection(finalIdxFront);
//        detail_content. setSelection(idxFront);
//        这样子 setSelection 是没有用处的
//        detail_content.setFocusable();
        setEditFocus(detail_content);
        nowSearchedIdx = 0;
//        要重新 focus

//        Editable editable = text.getText();
//        setEditFocus(text);

//        setEditFocus(detail_content);
//        Selection.setSelection(text,idxFront,detail_content.length());

        //        detail_content.   setSelection(idxFront);
        Log.i(TAG, "设置了选中之后 ");
        Log.i(TAG, "selectionStart " + detail_content.getSelectionStart());
        Log.i(TAG, "selectionEnd " + detail_content.getSelectionEnd());

//        spanColor.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3c2a")),
//                idxFront,idxBehind, 0);
//        https://www.jianshu.com/p/10cfabf503e3
//        searchTextText.setSpan(getRedSpan(),  idxFront,idxBehind, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        searchTextText.setSpan(getRedSpan(),  idxFront,idxBehind, 0);
//        text.setSpan(getRedSpan(),  idxFront,idxBehind, 0);

//    mTv1.setText(spanColor);
//    indexOf java
//————————————————
//    版权声明：本文为CSDN博主「jiangtea」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//    原文链接：https://blog.csdn.net/jiangtea/article/details/54098123
//    binding.fragmentDetailContent.detailContent.setText(noteTmp.getContent());
//        binding.fragmentDetailContent.detailContent.setText(spanColor);


        // Restore checklist
//    恢复检查表
//        toggleChecklistView = binding.fragmentDetailContent.detailContent;

//        binding.fragmentDetailContent.detailContent.setText(content);
////        是因为这里面 set span 所以有问题吗
////        确实是因为这个原因啊
////        setText SpannableString 之后有 gatherLinksForText
//        binding.fragmentDetailContent.detailContent.setLinkableText(spanColor);
//        binding.fragmentDetailContent.detailContent.gatherLinksForText();


        SpannableString spannableString = new SpannableString(content);
//        SpannableString 没有颜色
//    spanColor.setSpan();
//    https://blog.csdn.net/jiangtea/article/details/54098123
//    int idxFront = content.indexOf("晚上");
//    int idxBehind = content.indexOf("下");
//    spanColor.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3c2a")),
//            idxFront==-1?0:idxFront, idxBehind==-1?content.length():idxBehind, 0);
//        https://www.jianshu.com/p/bdfcaf0cd323
//        Log.i(TAG,"设置字");
//        Log.i(TAG,"content  "+content);
//        int idxFront = getIdxFront(content, "晚上");
//        int idxBehind =getIdxBehind(content, "下");
//        Log.i(TAG,"idxFront "+idxFront);
//        Log.i(TAG,"idxBehind "+idxBehind);
//        如果前面的没有，那就是0  ，这是找到第一个啊
//        detail_content.set
        for (Region region : regions) {
//            spannableString.setSpan(getRedSpan(), region.getStart(),region.getEnd(), 0);
//            spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW),
//                    region.getStart(), region.getEnd(), 0);
//            https://blog.csdn.net/qq_30008547/article/details/53836908
//            BackgroundColorSpan alpha
//            Color alpha、 java
//            Color.YELLOW;
            spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW),
                    region.getStart(), region.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//            spannableString.setSpan(getRedSpan(), idxFront,idxBehind, 0);
        }
//        设置了 BackgroundColorSpan 之后，selection 会不见


//    mTv1.setText(spanColor);
//    indexOf java
//————————————————
//    版权声明：本文为CSDN博主「jiangtea」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//    原文链接：https://blog.csdn.net/jiangtea/article/details/54098123
//    binding.fragmentDetailContent.detailContent.setText(noteTmp.getContent());
//        binding.fragmentDetailContent.detailContent.setText(spanColor);


        // Restore checklist
//    恢复检查表
//        toggleChecklistView = binding.fragmentDetailContent.detailContent;

        binding.fragmentDetailContent.detailContent.setText(content);
//        是因为这里面 set span 所以有问题吗
//        确实是因为这个原因啊
//        setText SpannableString 之后有 gatherLinksForText
        binding.fragmentDetailContent.detailContent.setLinkableText(spannableString);
        binding.fragmentDetailContent.detailContent.gatherLinksForText();
    }

    void showSearchToolToggle(){
        LinearLayout search_tool= mainActivity.findViewById(R.id.search_tool);
        if(search_tool.getVisibility()!=View.VISIBLE){
            search_tool.setVisibility(View.VISIBLE);
        }else{
            search_tool.setVisibility(View.GONE);
        }
    }
    void search() {
//        FragmentActivity activity1 = getActivity();
//        getActivity().findViewById( R.id.search_tool);
//        之前的 颜色还在
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Log.i(TAG, "没找到");
            return;
        }

//       LinearLayout search_tool= activity.findViewById(R.id.search_tool);
//        if(search_tool.getVisibility()!=View.VISIBLE){
//            search_tool.setVisibility(View.VISIBLE);
//        }
//        activity.findViewById(R.id.search_tool).setVisibility(View.VISIBLE);
        it.feio.android.omninotes.widget.EditText search_text =
                activity.findViewById(R.id.search_text);
        Editable searchTextText = search_text.getText();
        String searchTextTextStr = searchTextText.toString();
        search(searchTextTextStr);
//
//        String content = noteTmp.getContent();
//        it.feio.android.omninotes.widget.EditText detail_content
//                = activity.findViewById(R.id.detail_content);
//        Editable text = detail_content.getText();
////        text.clearSpans();
////        安卓 文字显示 行
////        text.
////        detail_content.focusSearch()
//        int selectionStart = detail_content.getSelectionStart();
//        int selectionEnd = detail_content.getSelectionEnd();
//
//        Log.i(TAG,"selectionStart "+selectionStart);
//        Log.i(TAG,"selectionEnd "+selectionEnd);
//
////        detail_content.   setSelection(selectionStart+2);
////        detail_content.getSelectionEnd()
////        int scrollX = detail_content.getScrollX();
////        int scrollY = detail_content.getScrollY();
////        int scrollX = detail_content.getscy();
////        Log.i(TAG,"scrollX "+scrollX);
////        Log.i(TAG,"scrollY "+scrollY);
////        搜索的时候 他的 scroll 也没有啊
////        这是 整个 编辑器的 他自己 的 不是他旁边的
////        Object[] spans = text.getSpans();
////        text.removeSpan(text.getSpans());
////        Log.i(TAG,"移动 之后 ");
////                detail_content.scrollTo(scrollX+10,scrollY+10);
////                detail_content.scrollBy(0,10);
////                detail_content.scrollBy(0,-20);
////                是他自己 移动吗
////        Log.i(TAG,"scrollX "+detail_content.getScrollX());
////        Log.i(TAG,"scrollY "+detail_content.getScrollY());
////        detail_content.startNestedScroll()
//        String content = text.toString();
////        SpannableString spanColor = new SpannableString(content);
////        SpannableString 没有颜色
////    spanColor.setSpan();
////    https://blog.csdn.net/jiangtea/article/details/54098123
////    int idxFront = content.indexOf("晚上");
////    int idxBehind = content.indexOf("下");
////    spanColor.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3c2a")),
////            idxFront==-1?0:idxFront, idxBehind==-1?content.length():idxBehind, 0);
////        https://www.jianshu.com/p/bdfcaf0cd323
//        Log.i(TAG,"设置字");
////        Log.i(TAG,"content  "+content);
////        int idxFront = getIdxFront(content, searchTextTextStr);
////        int idxBehind =searchTextTextStr.length()+idxFront;
////        Log.i(TAG,"idxFront "+idxFront);
////        Log.i(TAG,"idxBehind "+idxBehind);
//
//        regions = StrUtil.matchPatternStr(content, searchTextTextStr);
////        List<Region> regions = StrUtil.matchPatternStr(content, searchTextTextStr);
//        Log.i(TAG,"regions "+regions);
//        int idxFront=0;
////        if(lastSearchStr.equals(searchTextTextStr)){
//////           如果和之前的一样 那就会去找后面的
//////            if(regions.size()>nowSearchedIdx){
////////                Region region = regions.get(nowSearchedIdx);
////////                idxFront =region.getStart();
////////                nowSearchedIdx++;
//////            }
//////
//////            else
////                if(regions.size()==nowSearchedIdx){
////                nowSearchedIdx=0;
//////                Region region = regions.get(nowSearchedIdx);
//////                idxFront =region.getStart();
//////                nowSearchedIdx++;
////            }
////
////        }else{
////            nowSearchedIdx=0;
////        }
//
//        if(regions.size()>0){
//            Region region = regions.get(0);
//            idxFront = region.getStart();
////            nowSearchedIdx++;
//        }
//
////        if(regions.size()>nowSearchedIdx) {
////            Region region = regions.get(nowSearchedIdx);
////            idxFront = region.getStart();
////            nowSearchedIdx++;
////        }
////        他会 回到0
////        setSelection 他会 回到0
////        如果前面的没有，那就是0  ，这是找到第一个啊
////如果查询的 词语和上次一样，才需要 变成第二个吧
//        detail_content.clearFocus();
//        int finalIdxFront = idxFront;
//        detail_content.post(new Runnable() {
//            @Override
//            public void run() {
//                detail_content. setSelection(finalIdxFront);
////                detail_content.setSelectionAfterHeaderView();
//            }
//        });
//
//
////        detail_content. setSelection(finalIdxFront);
////        detail_content. setSelection(idxFront);
////        这样子 setSelection 是没有用处的
////        detail_content.setFocusable();
//        setEditFocus(detail_content);
//        nowSearchedIdx=0;
////        要重新 focus
//
////        Editable editable = text.getText();
////        setEditFocus(text);
//
////        setEditFocus(detail_content);
////        Selection.setSelection(text,idxFront,detail_content.length());
//
//        //        detail_content.   setSelection(idxFront);
//        Log.i(TAG,"设置了选中之后 ");
//        Log.i(TAG,"selectionStart "+ detail_content.getSelectionStart());
//        Log.i(TAG,"selectionEnd "+ detail_content.getSelectionEnd());
//
////        spanColor.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3c2a")),
////                idxFront,idxBehind, 0);
////        https://www.jianshu.com/p/10cfabf503e3
////        searchTextText.setSpan(getRedSpan(),  idxFront,idxBehind, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
////        searchTextText.setSpan(getRedSpan(),  idxFront,idxBehind, 0);
////        text.setSpan(getRedSpan(),  idxFront,idxBehind, 0);
//
////    mTv1.setText(spanColor);
////    indexOf java
////————————————————
////    版权声明：本文为CSDN博主「jiangtea」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
////    原文链接：https://blog.csdn.net/jiangtea/article/details/54098123
////    binding.fragmentDetailContent.detailContent.setText(noteTmp.getContent());
////        binding.fragmentDetailContent.detailContent.setText(spanColor);
//
//
//        // Restore checklist
////    恢复检查表
////        toggleChecklistView = binding.fragmentDetailContent.detailContent;
//
////        binding.fragmentDetailContent.detailContent.setText(content);
//////        是因为这里面 set span 所以有问题吗
//////        确实是因为这个原因啊
//////        setText SpannableString 之后有 gatherLinksForText
////        binding.fragmentDetailContent.detailContent.setLinkableText(spanColor);
////        binding.fragmentDetailContent.detailContent.gatherLinksForText();
//
//
//
//        SpannableString spannableString = new SpannableString(content);
////        SpannableString 没有颜色
////    spanColor.setSpan();
////    https://blog.csdn.net/jiangtea/article/details/54098123
////    int idxFront = content.indexOf("晚上");
////    int idxBehind = content.indexOf("下");
////    spanColor.setSpan(new ForegroundColorSpan(Color.parseColor("#ff3c2a")),
////            idxFront==-1?0:idxFront, idxBehind==-1?content.length():idxBehind, 0);
////        https://www.jianshu.com/p/bdfcaf0cd323
////        Log.i(TAG,"设置字");
////        Log.i(TAG,"content  "+content);
////        int idxFront = getIdxFront(content, "晚上");
////        int idxBehind =getIdxBehind(content, "下");
////        Log.i(TAG,"idxFront "+idxFront);
////        Log.i(TAG,"idxBehind "+idxBehind);
////        如果前面的没有，那就是0  ，这是找到第一个啊
////        detail_content.set
//        for (Region region : regions) {
////            spannableString.setSpan(getRedSpan(), region.getStart(),region.getEnd(), 0);
//            spannableString.setSpan( new  BackgroundColorSpan(Color.YELLOW),
//                    region.getStart(),region.getEnd(), 0);
////            spannableString.setSpan(getRedSpan(), idxFront,idxBehind, 0);
//        }
////        设置了 BackgroundColorSpan 之后，selection 会不见
//
//
////    mTv1.setText(spanColor);
////    indexOf java
////————————————————
////    版权声明：本文为CSDN博主「jiangtea」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
////    原文链接：https://blog.csdn.net/jiangtea/article/details/54098123
////    binding.fragmentDetailContent.detailContent.setText(noteTmp.getContent());
////        binding.fragmentDetailContent.detailContent.setText(spanColor);
//
//
//        // Restore checklist
////    恢复检查表
////        toggleChecklistView = binding.fragmentDetailContent.detailContent;
//
//        binding.fragmentDetailContent.detailContent.setText(content);
////        是因为这里面 set span 所以有问题吗
////        确实是因为这个原因啊
////        setText SpannableString 之后有 gatherLinksForText
//        binding.fragmentDetailContent.detailContent.setLinkableText(spannableString);
//        binding.fragmentDetailContent.detailContent.gatherLinksForText();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (isOptionsItemFastClick()) {
            return true;
        }

        switch (item.getItemId()) {

            case R.id.menu_search:
//                search();
//                search_tool。
                showSearchToolToggle();
                break;
            case R.id.menu_attachment:
                showAttachmentsPopup();
                break;
            case R.id.menu_tag:
                addTags();
                break;
            case R.id.menu_category:
                categorizeNote();
                break;
            case R.id.menu_share:
                shareNote();
                break;
            case R.id.menu_checklist_on:
            case R.id.menu_checklist_off:
                toggleChecklist();
                break;
            case R.id.menu_checklist_moveToBottom:
                moveCheckedItemsToBottom();
                break;
            case R.id.menu_lock:
            case R.id.menu_unlock:
                lockNote();
                break;
            case R.id.menu_pin_note:
                pinNote();
                break;
            case R.id.menu_add_shortcut:
                addShortcut();
                break;
            case R.id.menu_archive:
                archiveNote(true);
                break;
            case R.id.menu_unarchive:
                archiveNote(false);
                break;
            case R.id.menu_trash:
                trashNote(true);
                break;
            case R.id.menu_untrash:
                trashNote(false);
                break;
            case R.id.menu_discard_changes:
                discard();
                break;
            case R.id.menu_delete:
                deleteNote();
                break;
            case R.id.menu_note_info:
                showNoteInfo();
                break;
            default:
                LogDelegate.w("Invalid menu option selected");
        }

        ((OmniNotes) getActivity().getApplication()).getAnalyticsHelper()
                .trackActionFromResourceId(getActivity(),
                        item.getItemId());

        return super.onOptionsItemSelected(item);
    }

    private void showNoteInfo() {
        noteTmp.setTitle(getNoteTitle());
        noteTmp.setContent(getNoteContent());
        Intent intent = new Intent(getContext(), NoteInfosActivity.class);
        intent.putExtra(INTENT_NOTE, (android.os.Parcelable) noteTmp);
        startActivity(intent);

    }

    private void navigateUp() {
        afterSavedReturnsToList = true;
        saveAndExit(this);
    }

    private void toggleChecklist() {
        // In case checklist is active a prompt will ask about many options
        // to decide hot to convert back to simple text
        if (Boolean.FALSE.equals(noteTmp.isChecklist())) {
            toggleChecklist2();
            return;
        }

        // If checklist is active but no items are checked the conversion in done automatically
        // without prompting user
        if (mChecklistManager.getCheckedCount() == 0) {
            toggleChecklist2(true, false);
            return;
        }

        // Inflate the popup_layout.XML
        LayoutInflater inflater = (LayoutInflater) mainActivity
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.dialog_remove_checklist_layout,
                getView().findViewById(R.id.layout_root));

        // Retrieves options checkboxes and initialize their values
        final CheckBox keepChecked = layout.findViewById(R.id.checklist_keep_checked);
        final CheckBox keepCheckmarks = layout.findViewById(R.id.checklist_keep_checkmarks);
        keepChecked.setChecked(Prefs.getBoolean(PREF_KEEP_CHECKED, true));
        keepCheckmarks.setChecked(Prefs.getBoolean(PREF_KEEP_CHECKMARKS, true));

        new MaterialDialog.Builder(mainActivity)
                .customView(layout, false)
                .positiveText(R.string.ok)
                .onPositive((dialog, which) -> {
                    Prefs.edit()
                            .putBoolean(PREF_KEEP_CHECKED, keepChecked.isChecked())
                            .putBoolean(PREF_KEEP_CHECKMARKS, keepCheckmarks.isChecked())
                            .apply();
                    toggleChecklist2();
                }).build().show();
    }

    private void toggleChecklist2() {
        boolean keepChecked = Prefs.getBoolean(PREF_KEEP_CHECKED, true);
        boolean showChecks = Prefs.getBoolean(PREF_KEEP_CHECKMARKS, true);
        toggleChecklist2(keepChecked, showChecks);
    }

    private void toggleChecklist2(final boolean keepChecked, final boolean showChecks) {
        mChecklistManager = mChecklistManager == null ? new ChecklistManager(mainActivity) : mChecklistManager;
        int checkedItemsBehavior = Integer
                .parseInt(Prefs.getString("settings_checked_items_behavior", String.valueOf
                        (it.feio.android.checklistview.Settings.CHECKED_HOLD)));
        mChecklistManager
                .showCheckMarks(showChecks)
                .newEntryHint(getString(R.string.checklist_item_hint))
                .keepChecked(keepChecked)
                .undoBarContainerView(binding.contentWrapper)
                .moveCheckedOnBottom(checkedItemsBehavior);

        // Links parsing options
        mChecklistManager.setOnTextLinkClickListener(textLinkClickListener);
        mChecklistManager.addTextChangedListener(mFragment);
        mChecklistManager.setCheckListChangedListener(mFragment);

        // Switches the views
        View newView = null;
        try {
            newView = mChecklistManager.convert(toggleChecklistView);
        } catch (ViewNotSupportedException e) {
            LogDelegate.e("Error switching checklist view", e);
        }

        // Switches the views
        if (newView != null) {
            mChecklistManager.replaceViews(toggleChecklistView, newView);
            toggleChecklistView = newView;
            animate(toggleChecklistView).alpha(1).scaleXBy(0).scaleX(1).scaleYBy(0).scaleY(1);
            noteTmp.setChecklist(!noteTmp.isChecklist());
        }
    }

    private void moveCheckedItemsToBottom() {
        if (Boolean.TRUE.equals(noteTmp.isChecklist())) {
            mChecklistManager.moveCheckedToBottom();
        }
    }

    private void categorizeNote() {

        String currentCategory =
                noteTmp.getCategory() != null ? String.valueOf(noteTmp.getCategory().getId()) : null;
        final List<Category> categories = Observable.from(DbHelper.getInstance().getCategories())
                .map(category -> {
                    if (String.valueOf(category.getId()).equals(currentCategory)) {
                        category.setCount(category.getCount() + 1);
                    }
                    return category;
                }).toList().toBlocking().single();

        final MaterialDialog dialog = new MaterialDialog.Builder(mainActivity)
                .title(R.string.categorize_as)
                .adapter(new CategoryRecyclerViewAdapter(mainActivity, categories), null)
                .positiveText(R.string.add_category)
                .positiveColorRes(R.color.colorPrimary)
                .negativeText(R.string.remove_category)
                .negativeColorRes(R.color.colorAccent)
                .onPositive((dialog1, which) -> {
                    Intent intent = new Intent(mainActivity, CategoryActivity.class);
                    intent.putExtra("noHome", true);
                    startActivityForResult(intent, CATEGORY);
                })
                .onNegative((dialog12, which) -> {
                    noteTmp.setCategory(null);
                    setTagMarkerColor(null);
                }).build();

        RecyclerViewItemClickSupport.addTo(dialog.getRecyclerView())
                .setOnItemClickListener((recyclerView, position, v) -> {
                    noteTmp.setCategory(categories.get(position));
                    setTagMarkerColor(categories.get(position));
                    dialog.dismiss();
                });

        dialog.show();

    }

    private void showAttachmentsPopup() {
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.attachment_dialog, null);

        attachmentDialog = new MaterialDialog.Builder(mainActivity)
                .autoDismiss(false)
                .customView(layout, false)
                .build();
        attachmentDialog.show();

        // Camera
        android.widget.TextView cameraSelection = layout.findViewById(R.id.camera);
        cameraSelection.setOnClickListener(new AttachmentOnClickListener());
        // Audio recording
        android.widget.TextView recordingSelection = layout.findViewById(R.id.recording);
        toggleAudioRecordingStop(recordingSelection);
        recordingSelection.setOnClickListener(new AttachmentOnClickListener());
        // Video recording
        android.widget.TextView videoSelection = layout.findViewById(R.id.video);
        videoSelection.setOnClickListener(new AttachmentOnClickListener());
        // Files
        android.widget.TextView filesSelection = layout.findViewById(R.id.files);
        filesSelection.setOnClickListener(new AttachmentOnClickListener());
        // Sketch
        android.widget.TextView sketchSelection = layout.findViewById(R.id.sketch);
        sketchSelection.setOnClickListener(new AttachmentOnClickListener());
        // Location
        android.widget.TextView locationSelection = layout.findViewById(R.id.location);
        locationSelection.setOnClickListener(new AttachmentOnClickListener());
        // Time
        android.widget.TextView timeStampSelection = layout.findViewById(R.id.timestamp);
        timeStampSelection.setOnClickListener(new AttachmentOnClickListener());
        // Desktop note with PushBullet
        android.widget.TextView pushbulletSelection = layout.findViewById(R.id.pushbullet);
        pushbulletSelection.setVisibility(View.VISIBLE);
        pushbulletSelection.setOnClickListener(new AttachmentOnClickListener());
    }

    private void takePhoto() {
        // Checks for camera app available
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (!IntentChecker
                .isAvailable(mainActivity, intent, new String[]{PackageManager.FEATURE_CAMERA})) {
            mainActivity.showMessage(R.string.feature_not_available_on_this_device, ONStyle.ALERT);

            return;
        }
        // Checks for created file validity
        File f = StorageHelper.createNewAttachmentFile(mainActivity, MIME_TYPE_IMAGE_EXT);
        if (f == null) {
            mainActivity.showMessage(R.string.error, ONStyle.ALERT);
            return;
        }
        attachmentUri = FileProviderHelper.getFileProvider(f);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, attachmentUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void takeVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (!IntentChecker
                .isAvailable(mainActivity, takeVideoIntent, new String[]{PackageManager.FEATURE_CAMERA})) {
            mainActivity.showMessage(R.string.feature_not_available_on_this_device, ONStyle.ALERT);

            return;
        }
        // File is stored in custom ON folder to speedup the attachment
        File f = StorageHelper.createNewAttachmentFile(mainActivity, MIME_TYPE_VIDEO_EXT);
        if (f == null) {
            mainActivity.showMessage(R.string.error, ONStyle.ALERT);
            return;
        }
        attachmentUri = FileProviderHelper.getFileProvider(f);
        takeVideoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, attachmentUri);
        String maxVideoSizeStr = "".equals(Prefs.getString("settings_max_video_size",
                "")) ? "0" : Prefs.getString("settings_max_video_size", "");
        long maxVideoSize = parseLong(maxVideoSizeStr) * 1024L * 1024L;
        takeVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, maxVideoSize);
        startActivityForResult(takeVideoIntent, TAKE_VIDEO);
    }

    private void takeSketch(Attachment attachment) {

        File f = StorageHelper.createNewAttachmentFile(mainActivity, MIME_TYPE_SKETCH_EXT);
        if (f == null) {
            mainActivity.showMessage(R.string.error, ONStyle.ALERT);
            return;
        }
        attachmentUri = Uri.fromFile(f);

        // Forces portrait orientation to this fragment only
        mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Fragments replacing
        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        mainActivity.animateTransition(transaction, TRANSITION_HORIZONTAL);
        SketchFragment mSketchFragment = new SketchFragment();
        Bundle b = new Bundle();
        b.putParcelable(MediaStore.EXTRA_OUTPUT, attachmentUri);
        if (attachment != null) {
            b.putParcelable("base", attachment.getUri());
        }
        mSketchFragment.setArguments(b);
        transaction.replace(R.id.fragment_container, mSketchFragment, mainActivity.FRAGMENT_SKETCH_TAG)
                .addToBackStack(FRAGMENT_DETAIL_TAG).commit();
    }

    private void addTimestamp() {
        Editable editable = binding.fragmentDetailContent.detailContent.getText();
        int position = binding.fragmentDetailContent.detailContent.getSelectionStart();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        String dateStamp = dateFormat.format(new Date().getTime()) + " ";
        if (noteTmp.isChecklist()) {
            if (mChecklistManager.getFocusedItemView() != null) {
                editable = mChecklistManager.getFocusedItemView().getEditText().getEditableText();
                position = mChecklistManager.getFocusedItemView().getEditText().getSelectionStart();
            } else {
                ((CheckListView) toggleChecklistView)
                        .addItem(dateStamp, false, mChecklistManager.getCount());
            }
        }
        String leadSpace = position == 0 ? "" : " ";
        dateStamp = leadSpace + dateStamp;
        editable.insert(position, dateStamp);
        Selection.setSelection(editable, position + dateStamp.length());
    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Fetch uri from activities, store into adapter and refresh adapter
        Attachment attachment;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO:
                    attachment = new Attachment(attachmentUri, MIME_TYPE_IMAGE);
                    addAttachment(attachment);
                    mAttachmentAdapter.notifyDataSetChanged();
                    mGridView.autoresize();
                    break;
                case TAKE_VIDEO:
                    attachment = new Attachment(attachmentUri, MIME_TYPE_VIDEO);
                    addAttachment(attachment);
                    mAttachmentAdapter.notifyDataSetChanged();
                    mGridView.autoresize();
                    break;
                case FILES:
                    onActivityResultManageReceivedFiles(intent);
                    break;
                case SET_PASSWORD:
                    noteTmp.setPasswordChecked(true);
                    lockUnlock();
                    break;
                case SKETCH:
                    attachment = new Attachment(attachmentUri, MIME_TYPE_SKETCH);
                    addAttachment(attachment);
                    mAttachmentAdapter.notifyDataSetChanged();
                    mGridView.autoresize();
                    break;
                case CATEGORY:
                    mainActivity.showMessage(R.string.category_saved, ONStyle.CONFIRM);
                    Category category = intent.getParcelableExtra("category");
                    noteTmp.setCategory(category);
                    setTagMarkerColor(category);
                    break;
                case DETAIL:
                    mainActivity.showMessage(R.string.note_updated, ONStyle.CONFIRM);
                    break;
                default:
                    LogDelegate.e("Wrong element choosen: " + requestCode);
            }
        }
    }

    private void onActivityResultManageReceivedFiles(Intent intent) {
        List<Uri> uris = new ArrayList<>();
        if (intent.getClipData() != null) {
            for (int i = 0; i < intent.getClipData().getItemCount(); i++) {
                uris.add(intent.getClipData().getItemAt(i).getUri());
            }
        } else {
            uris.add(intent.getData());
        }
        for (Uri uri : uris) {
            String name = FileHelper.getNameFromUri(mainActivity, uri);
            new AttachmentTask(this, uri, name, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /**
     * Discards changes done to the note and eventually delete new attachments
     */
    private void discard() {
        new MaterialDialog.Builder(mainActivity)
                .content(R.string.undo_changes_note_confirmation)
                .positiveText(R.string.ok)
                .onPositive((dialog, which) -> {
                    if (!noteTmp.getAttachmentsList().equals(note.getAttachmentsList())) {
                        for (Attachment newAttachment : noteTmp.getAttachmentsList()) {
                            if (!note.getAttachmentsList().contains(newAttachment)) {
                                StorageHelper.delete(mainActivity, newAttachment.getUri().getPath());
                            }
                        }
                    }

                    goBack = true;

                    if (noteTmp.equals(noteOriginal)) {
                        goHome();
                    }

                    if (noteOriginal.get_id() != null) {
                        new SaveNoteTask(mFragment, false)
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, noteOriginal);
                        BaseActivity.notifyAppWidgets(mainActivity);
                    } else {
                        goHome();
                    }
                }).build().show();
    }

    @SuppressLint("NewApi")
    private void archiveNote(boolean archive) {
        // Simply go back if is a new note
        if (noteTmp.get_id() == null) {
            goHome();
            return;
        }

        noteTmp.setArchived(archive);
        goBack = true;
        exitMessage = archive ? getString(R.string.note_archived) : getString(R.string.note_unarchived);
        exitCroutonStyle = archive ? ONStyle.WARN : ONStyle.INFO;
        saveNote(this);
    }

    @SuppressLint("NewApi")
    private void trashNote(boolean trash) {
        // Simply go back if is a new note
        if (noteTmp.get_id() == null) {
            goHome();
            return;
        }

        noteTmp.setTrashed(trash);
        goBack = true;
        exitMessage = trash ? getString(R.string.note_trashed) : getString(R.string.note_untrashed);
        exitCroutonStyle = trash ? ONStyle.WARN : ONStyle.INFO;
        if (trash) {
            ShortcutHelper.removeShortcut(OmniNotes.getAppContext(), noteTmp);
            ReminderHelper.removeReminder(OmniNotes.getAppContext(), noteTmp);
        } else {
            ReminderHelper.addReminder(OmniNotes.getAppContext(), note);
        }
        saveNote(this);
    }

    private void deleteNote() {
        new MaterialDialog.Builder(mainActivity)
                .content(R.string.delete_note_confirmation)
                .positiveText(R.string.ok)
                .onPositive((dialog, which) -> {
                    mainActivity.deleteNote(noteTmp);
                    LogDelegate.d("Deleted note with ID '" + noteTmp.get_id() + "'");
                    mainActivity.showMessage(R.string.note_deleted, ONStyle.ALERT);
                    goHome();
                }).build().show();
    }

    public void saveAndExit(OnNoteSaved mOnNoteSaved) {
        if (isAdded()) {
            exitMessage = getString(R.string.note_updated);
            exitCroutonStyle = ONStyle.CONFIRM;
            goBack = true;
            saveNote(mOnNoteSaved);
        }
    }

    /**
     * Save new notes, modify them or archive
     */
    void saveNote(OnNoteSaved mOnNoteSaved) {

        // Changed fields
        noteTmp.setTitle(getNoteTitle());
        noteTmp.setContent(getNoteContent());

        // Check if some text or attachments of any type have been inserted or is an empty note
        if (goBack && TextUtils.isEmpty(noteTmp.getTitle()) && TextUtils.isEmpty(noteTmp.getContent())
                && noteTmp.getAttachmentsList().isEmpty()) {
            LogDelegate.d("Empty note not saved");
            exitMessage = getString(R.string.empty_note_not_saved);
            exitCroutonStyle = ONStyle.INFO;
            goHome();
            return;
        }

        if (saveNotNeeded()) {
            exitMessage = "";
            if (goBack) {
                goHome();
            }
            return;
        }

        noteTmp.setAttachmentsListOld(note.getAttachmentsList());

        new SaveNoteTask(mOnNoteSaved, lastModificationUpdatedNeeded()).executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR, noteTmp);
    }

    /**
     * Checks if nothing is changed to avoid committing if possible (check)
     */
    private boolean saveNotNeeded() {
        if (noteTmp.get_id() == null && Prefs.getBoolean(PREF_AUTO_LOCATION, false)) {
            note.setLatitude(noteTmp.getLatitude());
            note.setLongitude(noteTmp.getLongitude());
        }
        return !noteTmp.isChanged(note) || (noteTmp.isLocked() && !noteTmp.isPasswordChecked());
    }

    /**
     * Checks if only tag, archive or trash status have been changed and then force to not update last
     * modification date*
     */
    private boolean lastModificationUpdatedNeeded() {
        note.setCategory(noteTmp.getCategory());
        note.setArchived(noteTmp.isArchived());
        note.setTrashed(noteTmp.isTrashed());
        note.setLocked(noteTmp.isLocked());
        return noteTmp.isChanged(note);
    }

    @Override
    public void onNoteSaved(Note noteSaved) {
        if (!activityPausing) {
            EventBus.getDefault().post(new NotesUpdatedEvent(Collections.singletonList(noteSaved)));
            deleteMergedNotes(mergedNotesIds);
            if (noteTmp.getAlarm() != null && !noteTmp.getAlarm().equals(note.getAlarm())) {
                ReminderHelper.showReminderMessage(String.valueOf(noteTmp.getAlarm()));
            }
        }
        note = new Note(noteSaved);
        if (goBack) {
            goHome();
        }
    }

    private void deleteMergedNotes(List<String> mergedNotesIds) {
        ArrayList<Note> notesToDelete = new ArrayList<>();
        if (mergedNotesIds != null) {
            for (String mergedNoteId : mergedNotesIds) {
                Note noteToDelete = new Note();
                noteToDelete.set_id(Long.valueOf(mergedNoteId));
                notesToDelete.add(noteToDelete);
            }
            new NoteProcessorDelete(notesToDelete).process();
        }
    }

    private String getNoteTitle() {
        if (!TextUtils.isEmpty(binding.detailTitle.getText())) {
            return binding.detailTitle.getText().toString();
        } else {
            return "";
        }
    }

    private String getNoteContent() {
        String contentText = "";
        if (!noteTmp.isChecklist()) {
            // Due to checklist library introduction the returned EditText class is no more a
            // com.neopixl.pixlui.components.edittext.EditText but a standard android.widget.EditText
            View contentView = binding.detailRoot.findViewById(R.id.detail_content);
            if (contentView instanceof EditText) {
                contentText = ((EditText) contentView).getText().toString();
            } else if (contentView instanceof android.widget.EditText) {
                contentText = ((android.widget.EditText) contentView).getText().toString();
            }
        } else {
            if (mChecklistManager != null) {
                mChecklistManager.keepChecked(true).showCheckMarks(true);
                contentText = mChecklistManager.getText();
            }
        }
        return contentText;
    }

    /**
     * Updates share intent
     */
    private void shareNote() {
        Note sharedNote = new Note(noteTmp);
        sharedNote.setTitle(getNoteTitle());
        sharedNote.setContent(getNoteContent());
        mainActivity.shareNote(sharedNote);
    }

    /**
     * Notes locking with security password to avoid viewing, editing or deleting from unauthorized
     */
    private void lockNote() {
        LogDelegate.d("Locking or unlocking note " + note.get_id());

        // If security password is not set yes will be set right now
        if (Prefs.getString(PREF_PASSWORD, null) == null) {
            Intent passwordIntent = new Intent(mainActivity, PasswordActivity.class);
            startActivityForResult(passwordIntent, SET_PASSWORD);
            return;
        }

        // If password has already been inserted will not be asked again
        if (noteTmp.isPasswordChecked() || Prefs.getBoolean("settings_password_access", false)) {
            lockUnlock();
            return;
        }

        // Password will be requested here
        PasswordHelper.requestPassword(mainActivity, passwordConfirmed -> {
            switch (passwordConfirmed) {
                case SUCCEED:
                    lockUnlock();
                    break;
                default:
                    break;
            }
        });
    }

    private void lockUnlock() {
        // Empty password has been set
        if (Prefs.getString(PREF_PASSWORD, null) == null) {
            mainActivity.showMessage(R.string.password_not_set, ONStyle.WARN);
            return;
        }
        mainActivity.showMessage(R.string.save_note_to_lock_it, ONStyle.INFO);
        mainActivity.supportInvalidateOptionsMenu();
        noteTmp.setLocked(!noteTmp.isLocked());
        noteTmp.setPasswordChecked(true);
    }

    /**
     * Used to set actual reminder state when initializing a note to be edited
     */
    private String initReminder(Note note) {
        if (noteTmp.getAlarm() == null) {
            return "";
        }
        long reminder = parseLong(note.getAlarm());
        String rrule = note.getRecurrenceRule();
        if (!TextUtils.isEmpty(rrule)) {
            return RecurrenceHelper.getNoteRecurrentReminderText(reminder, rrule);
        } else {
            return RecurrenceHelper.getNoteReminderText(reminder);
        }
    }

    /**
     * Audio recordings playback
     */
    private void playback(View v, Uri uri) {
        // Some recording is playing right now
        if (mPlayer != null && mPlayer.isPlaying()) {
            if (isPlayingView != v) {
                // If the audio actually played is NOT the one from the click view the last one is played
                stopPlaying();
                isPlayingView = v;
                startPlaying(uri);
                replacePlayingAudioBitmap(v);
            } else {
                // Otherwise just stops playing
                stopPlaying();
            }
        } else {
            // If nothing is playing audio just plays
            isPlayingView = v;
            startPlaying(uri);
            replacePlayingAudioBitmap(v);
        }
    }

    private void replacePlayingAudioBitmap(View v) {
        Drawable d = ((ImageView) v.findViewById(R.id.gridview_item_picture)).getDrawable();
        if (BitmapDrawable.class.isAssignableFrom(d.getClass())) {
            recordingBitmap = ((BitmapDrawable) d).getBitmap();
        } else {
            recordingBitmap = ((BitmapDrawable) d.getCurrent()).getBitmap();
        }
        ((ImageView) v.findViewById(R.id.gridview_item_picture)).setImageBitmap(ThumbnailUtils
                .extractThumbnail(BitmapFactory.decodeResource(mainActivity.getResources(),
                        R.drawable.stop), THUMBNAIL_SIZE, THUMBNAIL_SIZE));
    }

    private void startPlaying(Uri uri) {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
        try {
            mPlayer.setDataSource(mainActivity, uri);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(mp -> {
                mPlayer = null;
                if (isPlayingView != null) {
                    ((ImageView) isPlayingView.findViewById(R.id.gridview_item_picture)).setImageBitmap
                            (recordingBitmap);
                    recordingBitmap = null;
                    isPlayingView = null;
                }
            });
        } catch (IOException e) {
            LogDelegate.e("prepare() failed", e);
            mainActivity.showMessage(R.string.error, ONStyle.ALERT);
        }
    }

    private void stopPlaying() {
        if (mPlayer != null) {
            if (isPlayingView != null) {
                ((ImageView) isPlayingView.findViewById(R.id.gridview_item_picture))
                        .setImageBitmap(recordingBitmap);
            }
            isPlayingView = null;
            recordingBitmap = null;
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void startRecording(View v) {
        PermissionsHelper.requestPermission(getActivity(), Manifest.permission.RECORD_AUDIO,
                R.string.permission_audio_recording, binding.snackbarPlaceholder, () -> {

                    isRecording = true;
                    toggleAudioRecordingStop(v);

                    File f = StorageHelper.createNewAttachmentFile(mainActivity, MIME_TYPE_AUDIO_EXT);
                    if (f == null) {
                        mainActivity.showMessage(R.string.error, ONStyle.ALERT);
                        return;
                    }
                    if (mRecorder == null) {
                        mRecorder = new MediaRecorder();
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                        mRecorder.setAudioEncodingBitRate(96000);
                        mRecorder.setAudioSamplingRate(44100);
                    }
                    recordName = f.getAbsolutePath();
                    mRecorder.setOutputFile(recordName);

                    try {
                        audioRecordingTimeStart = Calendar.getInstance().getTimeInMillis();
                        mRecorder.prepare();
                        mRecorder.start();
                    } catch (IOException | IllegalStateException e) {
                        LogDelegate.e("prepare() failed", e);
                        mainActivity.showMessage(R.string.error, ONStyle.ALERT);
                    }
                });
    }

    private void toggleAudioRecordingStop(View v) {
        if (isRecording) {
            ((android.widget.TextView) v).setText(getString(R.string.stop));
            ((android.widget.TextView) v).setTextColor(Color.parseColor("#ff0000"));
        }
    }

    private void stopRecording() {
        isRecording = false;
        if (mRecorder != null) {
            mRecorder.stop();
            audioRecordingTime = Calendar.getInstance().getTimeInMillis() - audioRecordingTimeStart;
            mRecorder.release();
            mRecorder = null;
        }
    }

    private void fade(final View v, boolean fadeIn) {

        int anim = R.animator.fade_out_support;
        int visibilityTemp = View.GONE;

        if (fadeIn) {
            anim = R.animator.fade_in_support;
            visibilityTemp = View.VISIBLE;
        }

        final int visibility = visibilityTemp;

        // Checks if user has left the app
        if (mainActivity != null) {
            Animation mAnimation = AnimationUtils.loadAnimation(mainActivity, anim);
            mAnimation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // Nothing to do
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // Nothing to do
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    v.setVisibility(visibility);
                }
            });
            v.startAnimation(mAnimation);
        }
    }

    /**
     * Pin note as ongoing notifications
     */
    private void pinNote() {
        PendingIntent notifyIntent = IntentHelper
                .getNotePendingIntent(getContext(), SnoozeActivity.class, ACTION_PINNED, note);

        Spanned[] titleAndContent = TextHelper.parseTitleAndContent(getContext(), note);
        String pinnedTitle = titleAndContent[0].toString();
        String pinnedContent = titleAndContent[1].toString();

        NotificationsHelper notificationsHelper = new NotificationsHelper(getContext());
        notificationsHelper
                .createOngoingNotification(NotificationChannelNames.PINNED, R.drawable.ic_stat_notification,
                        pinnedTitle, notifyIntent).setMessage(pinnedContent);

        List<Attachment> attachments = note.getAttachmentsList();
        if (!attachments.isEmpty() && !attachments.get(0).getMime_type().equals(MIME_TYPE_FILES)) {
            Bitmap notificationIcon = BitmapHelper
                    .getBitmapFromAttachment(getContext(), note.getAttachmentsList().get(0), 128,
                            128);
            notificationsHelper.setLargeIcon(notificationIcon);
        }

        PendingIntent unpinIntent = IntentHelper
                .getNotePendingIntent(getContext(), SnoozeActivity.class, ACTION_DISMISS, note);
        notificationsHelper.getBuilder()
                .addAction(R.drawable.ic_material_reminder_time_light, "FATTO", unpinIntent);

        notificationsHelper.show(note.get_id());
    }

    /**
     * Adding shortcut on Home screen
     */
    private void addShortcut() {
        ShortcutHelper.addShortcut(OmniNotes.getAppContext(), noteTmp);
        mainActivity.showMessage(R.string.shortcut_added, ONStyle.INFO);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                LogDelegate.v("MotionEvent.ACTION_DOWN");
                int w;

                Point displaySize = Display.getUsableSize(mainActivity);
                w = displaySize.x;

                if (x < SWIPE_MARGIN || x > w - SWIPE_MARGIN) {
                    swiping = true;
                    startSwipeX = x;
                }

                break;

            case MotionEvent.ACTION_UP:
                LogDelegate.v("MotionEvent.ACTION_UP");
                if (swiping) {
                    swiping = false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (swiping) {
                    LogDelegate.v("MotionEvent.ACTION_MOVE at position " + x + ", " + y);
                    if (Math.abs(x - startSwipeX) > SWIPE_OFFSET) {
                        swiping = false;
                        FragmentTransaction transaction = mainActivity.getSupportFragmentManager()
                                .beginTransaction();
                        mainActivity.animateTransition(transaction, TRANSITION_VERTICAL);
                        DetailFragment mDetailFragment = new DetailFragment();
                        Bundle b = new Bundle();
                        b.putParcelable(INTENT_NOTE, new Note());
                        mDetailFragment.setArguments(b);
                        transaction.replace(R.id.fragment_container, mDetailFragment, FRAGMENT_DETAIL_TAG)
                                .addToBackStack(
                                        FRAGMENT_DETAIL_TAG).commit();
                    }
                }
                break;

            default:
                LogDelegate.e("Wrong element choosen: " + event.getAction());
        }

        return true;
    }

    @Override
    public void onAttachingFileErrorOccurred(Attachment mAttachment) {
        mainActivity.showMessage(R.string.error_saving_attachments, ONStyle.ALERT);
        if (noteTmp.getAttachmentsList().contains(mAttachment)) {
            removeAttachment(mAttachment);
            mAttachmentAdapter.notifyDataSetChanged();
            mGridView.autoresize();
        }
    }

    private void addAttachment(Attachment attachment) {
        noteTmp.addAttachment(attachment);
    }

    private void removeAttachment(Attachment mAttachment) {
        noteTmp.removeAttachment(mAttachment);
    }

    private void removeAttachment(int position) {
        noteTmp.removeAttachment(noteTmp.getAttachmentsList().get(position));
    }

    private void removeAllAttachments() {
        noteTmp.setAttachmentsList(new ArrayList<>());
        mAttachmentAdapter = new AttachmentAdapter(mainActivity, new ArrayList<>(), mGridView);
        mGridView.invalidateViews();
        mGridView.setAdapter(mAttachmentAdapter);
    }

    @Override
    public void onAttachingFileFinished(Attachment mAttachment) {
        addAttachment(mAttachment);
        mAttachmentAdapter.notifyDataSetChanged();
        mGridView.autoresize();
    }

    @Override
    public void onReminderPicked(long reminder) {
        noteTmp.setAlarm(reminder);
        if (mFragment.isAdded()) {
            binding.fragmentDetailContent.reminderIcon.setImageResource(R.drawable.ic_alarm_black_18dp);
            binding.fragmentDetailContent.datetime
                    .setText(RecurrenceHelper.getNoteReminderText(reminder));
        }
    }

    @Override
    public void onRecurrenceReminderPicked(String recurrenceRule) {
        noteTmp.setRecurrenceRule(recurrenceRule);
        if (!TextUtils.isEmpty(recurrenceRule)) {
            LogDelegate.d("Recurrent reminder set: " + recurrenceRule);
            binding.fragmentDetailContent.datetime.setText(RecurrenceHelper
                    .getNoteRecurrentReminderText(Long.parseLong(noteTmp.getAlarm()), recurrenceRule));
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        scrollContent();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Nothing to do
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Nothing to do
    }

    @Override
    public void onCheckListChanged() {
        scrollContent();
    }

    private void scrollContent() {
        if (noteTmp.isChecklist()) {
            if (mChecklistManager.getCount() > contentLineCounter) {
                binding.contentWrapper.scrollBy(0, 60);
            }
            contentLineCounter = mChecklistManager.getCount();
        } else {
            if (binding.fragmentDetailContent.detailContent.getLineCount() > contentLineCounter) {
                binding.contentWrapper.scrollBy(0, 60);
            }
            contentLineCounter = binding.fragmentDetailContent.detailContent.getLineCount();
        }
    }

    /**
     * Add previously created tags to content
     */
    private void addTags() {
        contentCursorPosition = getCursorIndex();

        final List<Tag> tags = TagsHelper.getAllTags();

        if (tags.isEmpty()) {
            mainActivity.showMessage(R.string.no_tags_created, ONStyle.WARN);
            return;
        }

        final Note currentNote = new Note();
        currentNote.setTitle(getNoteTitle());
        currentNote.setContent(getNoteContent());
        Integer[] preselectedTags = TagsHelper.getPreselectedTagsArray(currentNote, tags);

        // Dialog and events creation
        MaterialDialog dialog = new MaterialDialog.Builder(mainActivity)
                .title(R.string.select_tags)
                .positiveText(R.string.ok)
                .items(TagsHelper.getTagsArray(tags))
                .itemsCallbackMultiChoice(preselectedTags, (dialog1, which, text) -> {
                    dialog1.dismiss();
                    tagNote(tags, which, currentNote);
                    return false;
                }).build();
        dialog.show();
    }

    private void tagNote(List<Tag> tags, Integer[] selectedTags, Note note) {
        Pair<String, List<Tag>> taggingResult = TagsHelper.addTagToNote(tags, selectedTags, note);

        StringBuilder sb;
        if (Boolean.TRUE.equals(noteTmp.isChecklist())) {
            CheckListViewItem mCheckListViewItem = mChecklistManager.getFocusedItemView();
            if (mCheckListViewItem != null) {
                sb = new StringBuilder(mCheckListViewItem.getText());
                sb.insert(contentCursorPosition, " " + taggingResult.first + " ");
                mCheckListViewItem.setText(sb.toString());
                mCheckListViewItem.getEditText()
                        .setSelection(contentCursorPosition + taggingResult.first.length()
                                + 1);
            } else {
                binding.detailTitle.append(" " + taggingResult.first);
            }
        } else {
            sb = new StringBuilder(getNoteContent());
            if (binding.fragmentDetailContent.detailContent.hasFocus()) {
                sb.insert(contentCursorPosition, " " + taggingResult.first + " ");
                binding.fragmentDetailContent.detailContent.setText(sb.toString());
                binding.fragmentDetailContent.detailContent
                        .setSelection(contentCursorPosition + taggingResult.first.length() + 1);
            } else {
                if (getNoteContent().trim().length() > 0) {
                    sb.append(System.getProperty("line.separator"))
                            .append(System.getProperty("line.separator"));
                }
                sb.append(taggingResult.first);
                binding.fragmentDetailContent.detailContent.setText(sb.toString());
            }
        }

        eventuallyRemoveDeselectedTags(taggingResult.second);
    }

    private void eventuallyRemoveDeselectedTags(List<Tag> tagsToRemove) {
        if (CollectionUtils.isEmpty(tagsToRemove)) {
            return;
        }

        boolean currentlyChecklist = Boolean.TRUE.equals(noteTmp.isChecklist());
        if (currentlyChecklist) {
            toggleChecklist2(true, true);
        }

        String titleWithoutTags = TagsHelper.removeTags(getNoteTitle(), tagsToRemove);
        binding.detailTitle.setText(titleWithoutTags);
        String contentWithoutTags = TagsHelper.removeTags(getNoteContent(), tagsToRemove);
        binding.fragmentDetailContent.detailContent.setText(contentWithoutTags);

        if (currentlyChecklist) {
            toggleChecklist2();
        }
    }

    private int getCursorIndex() {
        if (Boolean.TRUE.equals(noteTmp.isChecklist())) {
            CheckListViewItem mCheckListViewItem = mChecklistManager.getFocusedItemView();
            return mCheckListViewItem != null ? mCheckListViewItem.getEditText().getSelectionStart() : 0;
        } else {
            return binding.fragmentDetailContent.detailContent.getSelectionStart();
        }
    }

    /**
     * Used to check currently opened note from activity to avoid opening multiple times the same one
     */
    public Note getCurrentNote() {
        return note;
    }

    private boolean isNoteLocationValid() {
        return noteTmp.getLatitude() != null
                && noteTmp.getLatitude() != 0
                && noteTmp.getLongitude() != null
                && noteTmp.getLongitude() != 0;
    }

    public void startGetContentAction() {
        Intent filesIntent;
        filesIntent = new Intent(Intent.ACTION_GET_CONTENT);
        filesIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        filesIntent.addCategory(Intent.CATEGORY_OPENABLE);
        filesIntent.setType("*/*");
        startActivityForResult(filesIntent, FILES);
    }

    private void askReadExternalStoragePermission() {
        PermissionsHelper.requestPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE,
                R.string.permission_external_storage_detail_attachment,
                binding.snackbarPlaceholder, this::startGetContentAction);
    }

    public void onEventMainThread(PushbulletReplyEvent pushbulletReplyEvent) {
        String text =
                getNoteContent() + System.getProperty("line.separator") + pushbulletReplyEvent.getMessage();
        binding.fragmentDetailContent.detailContent.setText(text);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_next:
                next();

                break;
            case R.id.btn_pre:
                pre();

                break;
            case R.id.btn_search:
                search();
                break;


        }
    }

    private static class OnGeoUtilResultListenerImpl implements OnGeoUtilResultListener {

        private final WeakReference<MainActivity> mainActivityWeakReference;
        private final WeakReference<DetailFragment> detailFragmentWeakReference;
        private final WeakReference<Note> noteTmpWeakReference;

        OnGeoUtilResultListenerImpl(MainActivity activity, DetailFragment mFragment, Note noteTmp) {
            mainActivityWeakReference = new WeakReference<>(activity);
            detailFragmentWeakReference = new WeakReference<>(mFragment);
            noteTmpWeakReference = new WeakReference<>(noteTmp);
        }

        @Override
        public void onAddressResolved(String address) {
            // Nothing to do
        }

        @Override
        public void onCoordinatesResolved(Location location, String address) {
            // Nothing to do
        }

        @Override
        public void onLocationUnavailable() {
            mainActivityWeakReference.get().showMessage(R.string.location_not_found, ONStyle.ALERT);
        }

        @Override
        public void onLocationRetrieved(Location location) {

            if (!checkWeakReferences()) {
                return;
            }

            if (location == null) {
                return;
            }
            if (!ConnectionManager.internetAvailable(mainActivityWeakReference.get())) {
                noteTmpWeakReference.get().setLatitude(location.getLatitude());
                noteTmpWeakReference.get().setLongitude(location.getLongitude());
                onAddressResolved("");
                return;
            }
            LayoutInflater inflater = mainActivityWeakReference.get().getLayoutInflater();
            View v = inflater.inflate(R.layout.dialog_location, null);
            final AutoCompleteTextView autoCompView = v.findViewById(R.id
                    .auto_complete_location);
            autoCompView.setHint(mainActivityWeakReference.get().getString(R.string.search_location));
            autoCompView
                    .setAdapter(new PlacesAutoCompleteAdapter(mainActivityWeakReference.get(), R.layout
                            .simple_text_layout));
            final MaterialDialog dialog = new MaterialDialog.Builder(mainActivityWeakReference.get())
                    .customView(autoCompView, false)
                    .positiveText(R.string.use_current_location)
                    .onPositive((dialog1, which) -> {
                        if (TextUtils.isEmpty(autoCompView.getText().toString())) {
                            noteTmpWeakReference.get().setLatitude(location.getLatitude());
                            noteTmpWeakReference.get().setLongitude(location.getLongitude());
                            GeocodeHelper.getAddressFromCoordinates(location, detailFragmentWeakReference.get());
                        } else {
                            GeocodeHelper.getCoordinatesFromAddress(autoCompView.getText().toString(),
                                    detailFragmentWeakReference.get());
                        }
                    })
                    .build();
            autoCompView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Nothing to do
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() != 0) {
                        dialog
                                .setActionButton(DialogAction.POSITIVE, mainActivityWeakReference.get().getString(R
                                        .string.confirm));
                    } else {
                        dialog
                                .setActionButton(DialogAction.POSITIVE, mainActivityWeakReference.get().getString(R
                                        .string
                                        .use_current_location));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Nothing to do
                }
            });
            dialog.show();
        }

        private boolean checkWeakReferences() {
            return mainActivityWeakReference.get() != null && !mainActivityWeakReference.get()
                    .isFinishing()
                    && detailFragmentWeakReference.get() != null && noteTmpWeakReference.get() != null;
        }
    }

    /**
     * Manages clicks on attachment dialog
     */
    @SuppressLint("InlinedApi")
    private class AttachmentOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                // Photo from camera
                case R.id.camera:
                    takePhoto();
                    break;
                case R.id.recording:
                    if (!isRecording) {
                        startRecording(v);
                    } else {
                        stopRecording();
                        Attachment attachment = new Attachment(Uri.fromFile(new File(recordName)),
                                MIME_TYPE_AUDIO);
                        attachment.setLength(audioRecordingTime);
                        addAttachment(attachment);
                        mAttachmentAdapter.notifyDataSetChanged();
                        mGridView.autoresize();
                    }
                    break;
                case R.id.video:
                    takeVideo();
                    break;
                case R.id.files:
                    if (ContextCompat
                            .checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) {
                        startGetContentAction();
                    } else {
                        askReadExternalStoragePermission();
                    }
                    break;
                case R.id.sketch:
                    takeSketch(null);
                    break;
                case R.id.location:
                    displayLocationDialog();
                    break;
                case R.id.timestamp:
                    addTimestamp();
                    break;
                case R.id.pushbullet:
                    MessagingExtension.mirrorMessage(mainActivity, getString(R.string.app_name),
                            getString(R.string.pushbullet),
                            getNoteContent(), BitmapFactory.decodeResource(getResources(),
                                    R.drawable.ic_stat_literal_icon),
                            null, 0);
                    break;
                default:
                    LogDelegate.e("Wrong element choosen: " + v.getId());
            }
            if (!isRecording) {
                attachmentDialog.dismiss();
            }
        }
    }
}



