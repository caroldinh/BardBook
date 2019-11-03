package com.example.caroline.bardbook;

import android.content.ClipData;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ContextMenu.ContextMenuInfo;
import android.content.ClipboardManager;


import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<String> headData;
    private List<String> bodyData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;
    private Context appContext;
    private Context mContext;
    private String source;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, Context appContext, List<String> data, List<String> data2, String source) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.appContext = appContext;
        this.headData = data;
        this.bodyData = data2;
        this.source = source;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {
        View view = mInflater.inflate(R.layout.recyclerview_adapter, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = headData.get(position);
        String body = bodyData.get(position);
        holder.myTextView.setText(title);
        holder.textView2.setText(body);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return headData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        TextView myTextView;
        TextView textView2;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.text);
            textView2 = itemView.findViewById(R.id.body);
            itemView.setOnClickListener(this);
            if(source != "PoetPage") {
                itemView.setOnCreateContextMenuListener(this);
            }
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            if(source != "PoetPage") {
                MenuItem Copy = menu.add(Menu.NONE, 1, 1, "Copy");
                Copy.setOnMenuItemClickListener(onEditMenu);
                MenuItem Delete = menu.add(Menu.NONE, 2, 2, "Remove");
                Delete.setOnMenuItemClickListener(onEditMenu);
            }
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    BardBook book = new BardBook(appContext);
                    int position = getAdapterPosition();

                    switch (item.getItemId()) {
                        case 1:
                            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Content", headData.get(position));
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(appContext, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                            break;

                        case 2:

                            headData.remove(position);
                            notifyItemRemoved(position);
                            Log.d("RV Debugging Tag", "Removed " + getAdapterPosition());

                            switch(source){
                                case "FavPoems":
                                    book.removePoem(position);
                                    break;
                                case "SavedQuotes":
                                    book.removeLine(position);
                                    break;
                                case "FollowedPoets":
                                    book.removePoet(position);
                                    break;
                                default:
                                    break;
                            }

                            break;
                    }
                    return true;
                }
            };

        }

    // convenience method for getting data at click position
    String getItem(int id) {
        return headData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    void setLongClickListener(OnItemLongClickListener longClickListener){
        this.mLongClickListener = longClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClicked(View view, int position);
    }

}