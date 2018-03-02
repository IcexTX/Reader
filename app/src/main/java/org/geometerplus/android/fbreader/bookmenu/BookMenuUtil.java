package org.geometerplus.android.fbreader.bookmenu;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ListView;

import org.geometerplus.android.fbreader.api.FBReaderIntents;
import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.book.BookEvent;
import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.fbreader.book.BookmarkQuery;
import org.geometerplus.fbreader.book.HighlightingStyle;
import org.geometerplus.fbreader.book.IBookCollection;
import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.ui.android.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * ProjectName：FBReaderJ-master-2.5.10-geo
 * Describe：书籍目录工具类
 * Author：Icex
 * CreationTime：2018/3/1
 */

public class BookMenuUtil implements IBookCollection.Listener<Book> {

    private Activity activity;
    private View parentView;
    private HandlerUi handerUi;

    private String[] tabString;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<View> viewList;
    private ViewPagerAdapter pagerAdapter;

    //目录
    private View menuView;
    private ListView menuListView;
    private CatalogAdapter catalogAdapter;

    //书签
    private View bookMarkView;
    private ListView markListView;
    private BookMarkAdapter bookMarkAdapter;
    private static Book myBook;
    private final BookCollectionShadow myCollection = new BookCollectionShadow();
    private final Comparator<Bookmark> myComparator = new Bookmark.ByTimeComparator();
    private final Map<Integer, HighlightingStyle> myStyles = Collections.synchronizedMap(new HashMap<Integer, HighlightingStyle>());
    private final List<Bookmark> myBookmarksList = Collections.synchronizedList(new LinkedList<Bookmark>());


    private View noteView;
    private ListView noteListView;


    public BookMenuUtil(Activity activity, View parentView) {
        this.activity = activity;
        this.parentView = parentView;
        this.handerUi = new HandlerUi();
        initView();
    }

    /**
     * 初始化书籍目录控件
     */
    public void initView() {
        viewList = new ArrayList<>();
        //目录
        menuView = getView(activity, R.layout.book_menu_view);
        menuListView = getWidget(menuView, R.id.listView);
        //书签
        bookMarkView = getView(activity, R.layout.book_menu_view);
        markListView = getWidget(bookMarkView, R.id.listView);
        //笔记
        noteView = getView(activity, R.layout.book_menu_view);
        noteListView = getWidget(noteView, R.id.listView);

        viewList.add(menuView);
        viewList.add(bookMarkView);
        viewList.add(noteView);

        //导航栏
        tabString = activity.getResources().getStringArray(R.array.book_menu);
        tabLayout = getWidget(parentView, R.id.tabLayout_title);
        viewPager = getWidget(parentView, R.id.viewPager);
        pagerAdapter = new ViewPagerAdapter(viewList, tabString);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * 设置目录数据
     */
    public void setDataMenu(BookMenuView callBack) {
        final FBReaderApp fbreader = (FBReaderApp) ZLApplication.Instance();
        final TOCTree root = fbreader.Model.TOCTree;
        TOCTree treeToSelect = fbreader.getCurrentTOCElement();
        catalogAdapter = new CatalogAdapter(menuListView, root, callBack);
        catalogAdapter.setSelectedItem(treeToSelect);
    }

    /**
     * 设置目录数据
     */
    public void setDataMenu() {
        TOCTree treeToSelect = ((FBReaderApp) ZLApplication.Instance()).getCurrentTOCElement();
        catalogAdapter.setSelectedItem(treeToSelect);
    }

    /**
     * 设置目录数据
     */
    public void setDataMark(BookMenuView callBack) {
        myBook = FBReaderIntents.getBookExtra(activity.getIntent(), myCollection);
        bookMarkAdapter = new BookMarkAdapter(markListView, activity);
        myCollection.bindToService(activity, new Runnable() {
            public void run() {
                myCollection.addListener(BookMenuUtil.this);
                updateStyles();
                bookMarkAdapter.setMyStyles(myStyles);
                bookMarkAdapter.setMyCollection(myCollection);
                loadBookmarks();
            }
        });
    }

    public void unBind() {
        myCollection.unbind();
    }

    /**
     * 设置目录数据
     */
    public void setDataMark() {
        myBook = FBReaderIntents.getBookExtra(activity.getIntent(), myCollection);
        loadBookmarks();
    }


    private final Object myBookmarksLock = new Object();

    /**
     * 加载本书的书签数据
     */
    private void loadBookmarks() {
        myBookmarksList.clear();
        new Thread(new Runnable() {
            public void run() {
                synchronized (myBookmarksLock) {
                    for (BookmarkQuery query = new BookmarkQuery(myBook, 50); ; query = query.next()) {
                        final List<Bookmark> thisBookBookmarks = myCollection.bookmarks(query);
                        if (thisBookBookmarks.isEmpty()) {
                            break;
                        }
                        for (Bookmark b : thisBookBookmarks) {
                            final int position = Collections.binarySearch(myBookmarksList, b, myComparator);
                            if (position < 0) {
                                myBookmarksList.add(-position - 1, b);
                            }
                        }
                        handerUi.sendEmptyMessage(1);
                    }
                }
            }
        }).start();
    }

    public class HandlerUi extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    bookMarkAdapter.addAll(myBookmarksList);
                    break;
            }
        }
    }

    private void updateBookmarks(final Book book) {
        myBook = FBReaderIntents.getBookExtra(activity.getIntent(), myCollection);
        new Thread(new Runnable() {
            public void run() {
                synchronized (myBookmarksLock) {
                    final boolean flagThisBookTab = book.getId() == myBook.getId();
                    final Map<String, Bookmark> oldBookmarks = new HashMap<>();
                    if (flagThisBookTab) {
                        for (Bookmark b : bookMarkAdapter.bookmarks()) {
                            oldBookmarks.put(b.Uid, b);
                        }
                    }
                   /* for (BookmarkQuery query = new BookmarkQuery(book, 50); ; query = query.next()) {
                        final List<Bookmark> loaded = myCollection.bookmarks(query);
                        if (loaded.isEmpty()) {
                            break;
                        }
                        for (Bookmark b : loaded) {
                            final Bookmark old = oldBookmarks.remove(b.Uid);
                            if (flagThisBookTab) {
                                bookMarkAdapter.replace(old, b);
                            }
                        }
                    }
                    if (flagThisBookTab) {
                        bookMarkAdapter.removeAll(oldBookmarks.values());
                    }*/
                }
            }
        }).start();
    }

    private void updateStyles() {
        synchronized (myStyles) {
            myStyles.clear();
            for (HighlightingStyle style : myCollection.highlightingStyles()) {
                myStyles.put(style.Id, style);
            }
        }
    }

    /**
     * 利用泛型获取布局控件
     *
     * @param activity 所在的activity
     * @param id       布局id
     * @param <E>
     * @return 实例化布局
     */
    protected final <E extends View> E getView(Activity activity, int id) {
        try {
            return (E) activity.getLayoutInflater().inflate(id, null);
        } catch (ClassCastException e) {
            throw e;
        }
    }

    /**
     * 利用泛型获取获取控件id
     *
     * @param view 布局文件
     * @param id   控件id
     * @param <E>
     * @return 实例化控件
     */
    protected final <E extends View> E getWidget(View view, int id) {
        try {
            return (E) view.findViewById(id);
        } catch (ClassCastException e) {
            throw e;
        }
    }

    @Override
    public void onBookEvent(BookEvent event, Book book) {
        switch (event) {
            default:
                break;
            case BookmarkStyleChanged:
                updateStyles();
                bookMarkAdapter.notifyDataSetChanged();
                break;
            case BookmarksUpdated:
                updateBookmarks(book);
                break;
        }
    }

    @Override
    public void onBuildEvent(IBookCollection.Status status) {

    }
}
