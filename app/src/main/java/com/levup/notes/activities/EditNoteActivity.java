package com.levup.notes.activities;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.levup.notes.R;
import com.levup.notes.adapters.NotesFragmentPagerAdapter;
import com.levup.notes.db.NotesContract;
import com.levup.notes.model.Note;
import com.levup.notes.util.DateUtil;
import com.tjeannin.provigen.ProviGenBaseContract;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.levup.notes.db.NotesContract.TABLE_NAME;

public class EditNoteActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String SHARE_TYPE = "text/plain";

//    @BindView(R.id.titleEditText)
//    protected EditText mTitleEditText;
//    @BindView(R.id.contentEditText)
//    protected EditText mContentEditText;
    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.view_pager)
    protected ViewPager mViewPager;

    private long mId = -1;

    private NotesFragmentPagerAdapter mViewPagerAdapter = null;

    private String mOriginalTitle = "";
    private String mOriginalText = "";

    public static Intent newInstance(Context context) {
        return new Intent(context, EditNoteActivity.class);
    }

    public static Intent newInstance(Context context, long id) {
        Intent intent = newInstance(context);
        intent.putExtra(ProviGenBaseContract._ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        ButterKnife.bind(this);
        checkIntentByExtraId();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPagerAdapter = new NotesFragmentPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mViewPagerAdapter);
    }

    private void checkIntentByExtraId() {
        Intent intent = getIntent();
        if(!intent.hasExtra(ProviGenBaseContract._ID)) return;
        mId = intent.getLongExtra(ProviGenBaseContract._ID, mId);
        if(mId == -1) return;
        getLoaderManager().initLoader(R.id.edit_note_loader, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
        //        safetyFinish(() -> finish());
                break;
            }
            case R.id.action_share: {
       //         share();
                break;
            }
            case R.id.action_delete: {
                deleteNote();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void safetyFinish(Object o) {
    }

    private void deleteNote() {
        if(isNoteUpdatable()) {
            getContentResolver().delete(
                    Uri.withAppendedPath(NotesContract.CONTENT_URI, String.valueOf(mId)),
                    null,
                    null);
        }
        finish();
    }

//    private void share() {
//        Intent shareIntent = new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND);
//        shareIntent.putExtra(Intent.EXTRA_TEXT, prepareNoteForSharing());
//        shareIntent.setType(SHARE_TYPE);
//        startActivity(shareIntent);
//    }

//    private String prepareNoteForSharing() {
//        return getString(
//                R.string.sharing_template,
//                mTitleEditText.getText(),
//                mContentEditText.getText());
//    }

//    @OnClick(R.id.saveBtn)
//    public void onSaveBtnClick() {
//        save();
//        finish();
//    }

//    private void insertNote() {
//        ContentValues values = new ContentValues();
//        values.put(NotesContract.TITLE_COLUMN, mTitleEditText.getText().toString());
//        values.put(NotesContract.TEXT_COLUMN, mContentEditText.getText().toString());
//        values.put(NotesContract.TIME_COLUMN, DateUtil.formatCurrentDate());
//       getContentResolver().insert(NotesContract.CONTENT_URI, values);
//     //   getContentResolver().insert(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + TABLE_NAME)), values);
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                this,
              //  Uri.withAppendedPath(NotesContract.CONTENT_URI, String.valueOf(mId)),
                NotesContract.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || !cursor.moveToFirst()) return;
        List<Note>  dataSource = new ArrayList<>();
        do{
            dataSource.add(new Note(cursor));
        } while (cursor.moveToNext());
        mViewPagerAdapter.setDataSource(dataSource);
//        if (cursor == null || !cursor.moveToFirst()) return;
//        Note note = new Note(cursor);
//        mTitleEditText.setText(note.getTitle());
//        mContentEditText.setText(note.getText());
//        mOriginalTitle = note.getTitle();
//        mOriginalText = note.getText();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

//    @Override
//    public void onBackPressed() {
//        safetyFinish(() -> EditNoteActivity.super.onBackPressed());
//    }
//
//    private void safetyFinish(Runnable finish) {
//        if(mOriginalTitle.equals(mTitleEditText.getText().toString())
//                && mOriginalText.equals(mContentEditText.getText().toString())) {
//            finish.run();
//            return;
//        }
//        showDoYouSureAlert(finish);
//    }

    private void save() {
        if(isNoteUpdatable()) {
            updateNote();
        } else {
      //      insertNote();
        }
    }

    private boolean isNoteUpdatable() {
        return mId != -1;
    }

    private void showDoYouSureAlert(final Runnable finish) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.do_you_sure_alert_title);
        builder.setMessage(R.string.do_yout_sure_alert_do_you_want_to_save_change);
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
            save();
            finish.run();
        });
        builder.setNeutralButton(android.R.string.cancel, ((dialogInterface, i) -> dialogInterface.dismiss()));
        builder.setNegativeButton(android.R.string.no, (dialogInterface, i) -> finish.run());
        builder.show();
    }

    private void updateNote() {
//        final ContentValues values = new ContentValues();
//        values.put(NotesContract.TITLE_COLUMN, mTitleEditText.getText().toString());
//        values.put(NotesContract.TEXT_COLUMN, mContentEditText.getText().toString());
//        values.put(NotesContract.TIME_COLUMN, DateUtil.formatCurrentDate());
//        getContentResolver().update(
//                Uri.withAppendedPath(NotesContract.CONTENT_URI, String.valueOf(mId)),
//                values,
//                null,
//                null);
    }

}
