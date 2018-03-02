package org.geometerplus.android.fbreader.bookmenu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.geometerplus.android.fbreader.ZLTreeAdapter;
import org.geometerplus.android.util.ViewUtil;
import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.tree.ZLTree;
import org.geometerplus.zlibrary.ui.android.R;

/**
 * ProjectName：FBReaderJ-master-2.5.10-geo
 * Describe：目录设配器
 * Author：Icex
 * CreationTime：2018/3/1
 */

public class CatalogAdapter extends ZLTreeAdapter {

    private ZLTree<?> selectedItem;
    private BookMenuView callBack;

    public CatalogAdapter(ListView listView, TOCTree root, BookMenuView callBack) {
        super(listView, root);
        this.callBack = callBack;
    }

    /**
     * 设置选择的章节目录
     */
    public void setSelectedItem(ZLTree<?> item) {
        this.selectedItem = item;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = (convertView != null) ? convertView :
                LayoutInflater.from(parent.getContext()).inflate(R.layout.toc_tree_item, parent, false);
        final TOCTree tree = (TOCTree) getItem(position);
        ViewUtil.findTextView(view, R.id.tv_title).setText(tree.getText());
        ViewUtil.findTextView(view, R.id.tv_title).setTextColor(
                tree == selectedItem ? 0xFFF4942D : 0xFF505050);
        return view;
    }

    void openBookText(TOCTree tree) {
        final TOCTree.Reference reference = tree.getReference();
        if (reference != null) {
            setSelectedItem(tree);
            final FBReaderApp fbreader = (FBReaderApp) ZLApplication.Instance();
            fbreader.addInvisibleBookmark();
            fbreader.BookTextView.gotoPosition(reference.ParagraphIndex, 0, 0);
            fbreader.showBookTextView();
            fbreader.storePosition();
            if (callBack != null) {
                callBack.closeDrawers();
            }
        }
    }

    @Override
    protected boolean runTreeItem(ZLTree<?> tree) {
        if (super.runTreeItem(tree)) {
            return true;
        }
        openBookText((TOCTree) tree);
        return true;
    }

}