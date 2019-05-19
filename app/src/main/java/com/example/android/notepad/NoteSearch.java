package com.example.android.notepad;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import java.io.IOException;
import java.nio.CharBuffer;

/**
 * Created by Administrator on 2018/5/25.
 */

//https://www.cnblogs.com/jeffen/p/6958235.html searchView讲解
//https://blog.csdn.net/u010376098/article/details/51461363  SearchView实时搜索初体验
//https://blog.csdn.net/douzajun/article/details/77669658  基于NotePad应用的功能扩展

public class NoteSearch extends ListActivity implements SearchView.OnQueryTextListener{
    public Cursor mCursor;
    private SimpleCursorAdapter mAdapter;
    private SearchView mSearchView;

    //数据存放的数组
    private static final String[] PROJECTION = new String[]{
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, // 1
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE,//在这里加入了修改时间的显示
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_search);
        //mSearchView = (SearchView)findViewById(R.id.search_view);
        //mListView = (ListView)findViewById();//id没法写@android:id/list
        //String action = getIntent().getAction();
        //ComponentName componentName = getIntent().getComponent();//获取该activity对应的Intent的component属性

        //Intent intent = getIntent();
        //addPreferencesFromResource(R.xml.new_preferencescreen);
        //继承activity基类的默认不显示标题栏
        Intent intent = getIntent();// Gets the intent that started this Activity.
        // If there is no data associated with the Intent, sets the data to the default URI, which
        // accesses a list of notes.
        if (intent.getData()==null){
            intent.setData(NotePad.Notes.CONTENT_URI);
        }
        getListView().setOnCreateContextMenuListener(this);

        mSearchView = (SearchView)findViewById(R.id.search_view);//注册监听器
        mSearchView.setIconifiedByDefault(false); //显示搜索的天幕，默认只有一个放大镜图标
        mSearchView.setSubmitButtonEnabled(true); //显示搜索按钮
        //mSearchView.setBackgroundColor(0x22ff00ff); //设置背景颜色
        mSearchView.setBackgroundColor(getResources().getColor(R.color.blue_green)); //设置背景颜色
        mSearchView.setOnQueryTextListener(this);
        //initDataView();
    }

    private void initDataView(){
//        String selection = NotePad.Notes.COLUMN_NAME_TITLE + " Like ? ";
//        String[] selectionArgs = { "%"+s+"%" };
//        Cursor cursor = managedQuery(
//                getIntent().getData(),            // Use the default content URI for the provider.
//                PROJECTION,                       // Return the note ID and title for each note. and modifcation date
//                selection,                        // 条件左边
//                selectionArgs,                    // 条件右边
//                NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
//        );
//        String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE ,  NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE };
//        int[] viewIDs = { android.R.id.text1 , R.id.text2 };
//        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
//                this,
//                R.layout.noteslist_item,
//                cursor,
//                dataColumns,
//                viewIDs
//        );
//        setListAdapter(adapter);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) { //Test改变的时候执行的内容
        //Text发生改变时执行的内容

//        String selection = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " LIKE '%" + s + "%' " + " OR "
//                + ContactsContract.RawContacts.SORT_KEY_PRIMARY + " LIKE '%" + s + "%' ";
//        // String[] selectionArg = { queryText };
//        mCursor = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, PROJECTION, selection, null, null);
//        mAdapter.swapCursor(mCursor); // 交换指针，展示新的数据
//        return true;


        String selection = NotePad.Notes.COLUMN_NAME_TITLE + " Like ? ";//查询条件
        String[] selectionArgs = { "%"+s+"%" };//查询条件参数，配合selection参数使用,%通配多个字符

        //查询数据库中的内容,当我们使用 SQLiteDatabase.query()方法时，就会得到Cursor对象， Cursor所指向的就是每一条数据。
        //managedQuery(Uri, String[], String, String[], String)等同于Context.getContentResolver().query()
        Cursor cursor = managedQuery(
                getIntent().getData(),            // Use the default content URI for the provider.用于ContentProvider查询的URI，从这个URI获取数据
                PROJECTION,                       // Return the note ID and title for each note. and modifcation date.用于标识uri中有哪些columns需要包含在返回的Cursor对象中
                selection,                        // 作为查询的过滤参数，也就是过滤出符合selection的数据，类似于SQL的Where语句之后的条件选择
                selectionArgs,                    // 查询条件参数，配合selection参数使用
                NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.查询结果的排序方式，按照某个columns来排序，例：String sortOrder = NotePad.Notes.COLUMN_NAME_TITLE
        );

        //一个简单的适配器，将游标中的数据映射到布局文件中的TextView控件或者ImageView控件中
        String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE ,  NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE };
        int[] viewIDs = { android.R.id.text1 , R.id.text2 };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,                   //context:上下文
                R.layout.noteslist_item,         //layout:布局文件，至少有int[]的所有视图
                cursor,                          //cursor：游标
                dataColumns,                     //from：绑定到视图的数据
                viewIDs                          //to:用来展示from数组中数据的视图
                                                 //flags：用来确定适配器行为的标志，Android3.0之后淘汰
        );
        setListAdapter(adapter);
        return true;
    }
}


//
//public class NoteSearch extends ListActivity{
//
//    private SearchView mSearchView;
//    private ListView mListView;
//    private SimpleCursorAdapter mAdapter;
//    private Cursor mCursor;
//
//    private static final String[] PROJECTION = new String[] {
//            NotePad.Notes._ID, // 0
//            NotePad.Notes.COLUMN_NAME_TITLE, // 1
//            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE,//在这里加入了修改时间的显示
//    };
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //String action = getIntent().getAction();
//        setContentView(R.layout.note_search);
//       // ComponentName componentName = getIntent().getComponent();//获取该activity对应的Intent的component属性
//        //Intent intent = getIntent();
//        //addPreferencesFromResource(R.xml.new_preferencescreen);
//        //继承activity基类的默认不显示标题栏
//
//        Intent intent = getIntent();// Gets the intent that started this Activity.
//        // If there is no data associated with the Intent, sets the data to the default URI, which
//        // accesses a list of notes.
//        if (intent.getData()==null){
//            intent.setData(NotePad.Notes.CONTENT_URI);
//        }
//        getListView().setOnCreateContextMenuListener(this);
//
//        mSearchView = (SearchView)findViewById(R.id.search_view);
//        mSearchView.setIconifiedByDefault(false); //显示搜索的天幕，默认只有一个放大镜图标
//        mSearchView.setSubmitButtonEnabled(true); //显示搜索按钮
//        mSearchView.setBackgroundColor(0x22ff00ff); //设置背景颜色
//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
//            @Override
//            String[] dataColumns = {
//                    NotePad.Notes.COLUMN_NAME_TITLE,
//                    NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE
//            } ;
//
//            // The view IDs that will display the cursor columns, initialized to the TextView in
//            // noteslist_item.xml
//            int[] viewIDs = { android.R.id.text1 ,R.id.text2};
//            public boolean onQueryTextChange(String s) {
//                SimpleCursorAdapter simpleCursorAdapter
//                        =new SimpleCursorAdapter(this,R.layout.note_search,cursor,dataColumns,viewIDs);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                return false;
//            }
//        });
//
//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                return false;
//            }
//
//            @Override
//            //文本发生变化时，执行其中的代码
//            public boolean onQueryTextChange(String s) {
//                //NoteList使用SimpleCursorAdapter来装配数据，首先查询数据库的内容，
//                // 如下代码所示，这里使用ContentProvider默认的URI。
//                String selection = NotePad.Notes.COLUMN_NAME_TITLE + " Like ? ";
//                String[] selectionArgs = { "%"+s+"%" };
//                Cursor cursor = managedQuery(
//                        getIntent().getData(),            // Use the default content URI for the provider.
//                        PROJECTION,                       // Return the note ID and title for each note.
//                        selection,                             // No where clause, return all records.
//                        selectionArgs,                             // No where clause, therefore no where column values.
//                        NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
//                );
//NoteList中原文
//                Cursor cursor = managedQuery(
//                        getIntent().getData(),            // Use the default content URI for the provider.
//                        PROJECTION,                       // Return the note ID and title for each note.
//                        null,                             // No where clause, return all records.
//                        null,                             // No where clause, therefore no where column values.
//                        NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
//                );
//                // The names of the cursor columns to display in the view, initialized to the title column
//                String[] dataColumns = {
//                        NotePad.Notes.COLUMN_NAME_TITLE,
//                        NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE
//                } ;
//                // The view IDs that will display the cursor columns, initialized to the TextView in
//                // noteslist_item.xml
//                int[] viewIDs = { android.R.id.text1 ,R.id.text2};
//
//                // Creates the backing adapter for the ListView.
//                //然后通过SimpleCursorAdapter来进行装配：
////SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.note_search,cursor,dataColumns,viewIDs);
//                SimpleCursorAdapter adapter
//                        = new SimpleCursorAdapter(
//                        this,                             // The Context for the ListView
//                        R.layout.noteslist_item,          // Points to the XML for a list item
//                        cursor,                           // The cursor to get items from
//                        dataColumns,
//                        viewIDs
//                );
//
//
//                SimpleCursorAdapter adapter = new SimpleCursorAdapter(
//                        this,
//                        R.layout.noteslist_item,
//                        cursor,
//                        dataColumns,
//                        viewIDs
//                );
//
// Sets the ListView's adapter to be the cursor adapter that was just created.
                //setListAdapter(adapter);
                //return true;
           // }
       // });

//        得到相应的指针
// mCursor = getContentResolver().query(RawContacts.CONTENT_URI,PROJECTION,null,null,null);
//        通过传入mCursor，将搜索结果放入listview中
//        context: 上下文
//        layout: item的布局文件，其中至少要有参数to数组中的所有视图
//        c: 数据库游标，如果游标不可用，则可为空
//        from: 一个列名称列表，标志着绑定到视图的数据，如果游标不可用，则可为空
//        to: 用来展示from数组中数据的视图，如果游标不可用，则可为空
//        flags: 用来确定适配器行为的标志
//        mAdapter = new SimpleCursorAdapter(this,R.layout.note_search,mCursor,new String[]{NotePad.Notes.COLUMN_NAME_TITLE},new int[]{android.R.id.list},0);
//        mListView = (ListView)findViewById(android.R.id.list);
//        mListView.setAdapter(mAdapter);
//        mListView.setOnScrollListener(new OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int i) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
//
//            }
//        });
//        savedInstanceState;
//        mSearchView.setOnQueryTextListener(new OnQueryTextListener() {
//
//            private String TAG = getClass().getSimpleName();
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                Log.d(TAG, "onQuaryTextSubmit = " + s);
//                if (mSearchView != null) {
//                    // 得到输入管理对象
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    if (imm != null) {
//                        // 这将让键盘在所有的情况下都被隐藏，但是一般我们在点击搜索按钮后，输入法都会乖乖的自动隐藏的。
//                        imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法                    }
//                        mSearchView.clearFocus(); // 不获取焦点
//                    }
//                }
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                Log.d(TAG,"onQueryTextChange"+s);
//                String selection = RawContacts.DISPLAY_NAME_PRIMARY + "LIKE '%" + s +"%' "+"OR"+RawContacts.SORT_KEY_PRIMARY + "LIKE '%" + s +"%' ";
//                mCursor = getContentResolver().query(RawContacts.CONTENT_URI,PROJECTION,selection,null,null);
//                mAdapter.swapCursor(mCursor);//交换指针，显示新的数据
//                return true;
//            }
//        });
//    }
//}
//
//