# 期中大实验NotPad
## 实验要求
**基本要求：**

1.搜索功能

2.时间戳功能

**附加功能**

1.UI美化

2.文本字体更改

## 功能介绍

**1.搜索功能**

在onOptionsItemSelected的switch (item.getItemId())中添加对应menu_search的case：
```javascript
case R.id.menu_search:
    startActivity(new Intent(Intent.ACTION_SEARCH,getIntent().getData()));
    return true;
```
在其中加入隐式Intent的跳转
startActivity(new Intent(Intent.ACTION_SEARCH,getIntent().getData()));
然后在AndroidManifest.xml中加入相应的声名
```javascript
<activity
    android:name=".NoteSearch"
    android:label="NoteSearch"
    >

    <intent-filter>
        <action android:name="android.intent.action.NoteSearch" />
        <action android:name="android.intent.action.SEARCH" />
        <action android:name="android.intent.action.SEARCH_LONG_PRESS" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:mimeType="vnd.android.cursor.dir/vnd.google.note" />
    </intent-filter>
</activity>
```
之后就可以跳转到相应的搜索界面


实现

```javascript
 @Override
    public boolean onQueryTextChange(String s) { 
        String selection = NotePad.Notes.COLUMN_NAME_TITLE + " Like ? ";
        String[] selectionArgs = { "%"+s+"%" };//查询条件参数，配合selection参数使用,%通配多个字符
        Cursor cursor = managedQuery(
                getIntent().getData(),            // Use the default content URI for the provider.用于ContentProvider查询的URI，从这个URI获取数据
                PROJECTION,                       // Return the note ID and title for each note. and modifcation date.用于标识uri中有哪些columns需要包含在返回的Cursor对象中
                selection,                        // 作为查询的过滤参数，也就是过滤出符合selection的数据，类似于SQL的Where语句之后的条件选择
                selectionArgs,                    // 查询条件参数，配合selection参数使用
                NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.查询结果的排序方式，按照某个columns来排序，例：String sortOrder = NotePad.Notes.COLUMN_NAME_TITLE
        );

        String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE ,  NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE };
        int[] viewIDs = { android.R.id.text1 , R.id.text2 };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,                   
                R.layout.noteslist_item,       
                cursor,                       
                dataColumns,                    
                viewIDs                        
        );
        setListAdapter(adapter);
        return true;
    }
}
```


这里实现数据的查询，首先进行数据库的查询

```javascript
String selection = NotePad.Notes.COLUMN_NAME_TITLE + " Like ? ";
String[] selectionArgs = { "%"+s+"%" };
Cursor cursor = managedQuery(
        getIntent().getData(),            // Use the default content URI for the provider.用于ContentProvider查询的URI，从这个URI获取数据
        PROJECTION,                       // Return the note ID and title for each note. and modifcation date.用于标识uri中有哪些columns需要包含在返回的Cursor对象中
        selection,                        // 作为查询的过滤参数，也就是过滤出符合selection的数据，类似于SQL的Where语句之后的条件选择
        selectionArgs,                    // 查询条件参数，配合selection参数使用
        NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.查询结果的排序方式，按照某个columns来排序，例：String sortOrder = NotePad.Notes.COLUMN_NAME_TITLE
);
```

把数据映射到ListView中

```javascript
String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE ,  NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE };
int[] viewIDs = { android.R.id.text1 , R.id.text2 };
SimpleCursorAdapter adapter = new SimpleCursorAdapter(
        this,                   
        R.layout.noteslist_item,         
        cursor,                         
        dataColumns,                    
        viewIDs                          
);
setListAdapter(adapter);
return true;
```
**实现截图：**

![Alt](https://github.com/512lgw/NotPad/blob/master/2.png)

**2.时间戳功能**

创建时显示的时间的实现

```javascript
Long now = Long.valueOf(System.currentTimeMillis());
if (values.containsKey(NotePad.Notes.COLUMN_NAME_CREATE_DATE) == false) {
    values.put(NotePad.Notes.COLUMN_NAME_CREATE_DATE, now);
}
if (values.containsKey(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE) == false) {
    values.put(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, now);
}
```

实现时间的显示，

```javascript
<TextView
    android:id="@+id/text2"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:textAppearance="?android:attr/textAppearanceLarge"
    android:textColor="@color/yellow"
    android:textSize="20sp"
    />
 ```  
    
装配的时候需要装配相应的日期，所以dataColumns,viewIDs这两个参数需要加入时间

```javascript
String[] dataColumns = {
        NotePad.Notes.COLUMN_NAME_TITLE,
        NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE
} ;
int[] viewIDs = { android.R.id.text1 ,R.id.text2};
 ``` 

然后通过SimpleCursorAdapter来进行装配

```javascript
SimpleCursorAdapter adapter
    = new SimpleCursorAdapter(
              this,                             // The Context for the ListView
              R.layout.noteslist_item,          // Points to the XML for a list item
              cursor,                           // The cursor to get items from
              dataColumns,
              viewIDs
      );
```
时间就会显示在相应的条目上

**实现截图：**

![Alt](https://github.com/512lgw/NotPad/blob/master/1.png)

**3.UI美化：**

修改一下条目显示的颜色，以及鼠标点击之后的颜色

```javascript
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_focused="true" android:drawable="@color/orange"/>
    <item android:state_pressed="true" android:drawable="@color/yellow" />
    <item android:drawable="@color/light_blue"/>
</selector>
```

**实现截图**
![Alt](https://github.com/512lgw/NotPad/blob/master/1.png#pic_center=50x50)

**4.文本内容字体可修改：**

基于之前的实验完成的此项功能，对notepad中的文本继续字体大小，颜色的修改

添加对应的菜单

```javascript
<item
    android:id="@+id/font_size"
    android:title="@string/font_size">
    <!--子菜单-->
    <menu>
        <!--定义一组单选菜单项-->
        <group>
            <!--定义多个菜单项-->
            <item
                android:id="@+id/font_10"
                android:title="@string/font10"
                />

            <item
                android:id="@+id/font_16"
                android:title="@string/font16" />
            <item
                android:id="@+id/font_20"
                android:title="@string/font20" />
        </group>
    </menu>
</item>

<item
    android:title="@string/font_color"
    android:id="@+id/font_color"
    >
    <menu>
        <!--定义一组普通菜单项-->
        <group>
            <!--定义两个菜单项-->
            <item
                android:id="@+id/red_font"
                android:title="@string/red_title" />
            <item
                android:title="@string/black_title"
                android:id="@+id/black_font"/>
        </group>
    </menu>
</item>
```javascript



case来相应事件

```javascript
case R.id.font_10:

    mText.setTextSize(20);
    Toast toast =Toast.makeText(NoteEditor.this,"修改成功",Toast.LENGTH_SHORT);
    toast.show();
    finish();
    break;

case R.id.font_16:
    mText.setTextSize(32);
    Toast toast2 =Toast.makeText(NoteEditor.this,"修改成功",Toast.LENGTH_SHORT);
    toast2.show();
    finish();
    break;
case R.id.font_20:
    mText.setTextSize(40);
    Toast toast3 =Toast.makeText(NoteEditor.this,"修改成功",Toast.LENGTH_SHORT);
    toast3.show();
    finish();
    break;
case R.id.red_font:
    mText.setTextColor(Color.RED);
    Toast toast4 =Toast.makeText(NoteEditor.this,"修改成功",Toast.LENGTH_SHORT);
    toast4.show();
    finish();
    break;
case R.id.black_font:
    mText.setTextColor(Color.BLACK);
    Toast toast5 =Toast.makeText(NoteEditor.this,"修改成功",Toast.LENGTH_SHORT);
    toast5.show();
    finish();
    break;
```

在数据库中加入相应的参数来保存字体大小和颜色的信息

```javascript
db.execSQL("CREATE TABLE " + NotePad.Notes.TABLE_NAME + " ("
        + NotePad.Notes._ID + " INTEGER PRIMARY KEY,"
        + NotePad.Notes.COLUMN_NAME_TITLE + " TEXT,"
        + NotePad.Notes.COLUMN_NAME_NOTE + " TEXT,"
        + NotePad.Notes.COLUMN_NAME_CREATE_DATE + " INTEGER,"
        + NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE + " INTEGER"
        + NotePad.Notes.COLUMN_NAME_TEXT_COLOR + "INTEGER" //新增加的修改字体颜色
        + NotePad.Notes.COLUMN_NAME_TEXT_SIZE + "INTEGER" //新增加的修改字体大小
        + ");");
```
**实现截图**

![Alt](https://github.com/512lgw/NotPad/blob/master/3.png#pic_center=60x60)

![Alt](https://github.com/512lgw/NotPad/blob/master/4.png#pic_center=60x60)
