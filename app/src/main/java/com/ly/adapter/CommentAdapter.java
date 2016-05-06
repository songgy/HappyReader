package com.ly.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ly.entity.Comment;
import com.ly.entity.User;
import com.ly.happyreader.R;
import com.ly.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ly on 2016/4/27.
 */
public class CommentAdapter extends BaseAdapter {
    Context context;
    List<Comment> data = new ArrayList<>();
    LayoutInflater inflater;

    public CommentAdapter(Context context, List<Comment> data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder viewHolder =null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.commentadapter_listview_item,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_portrait = (ImageView) convertView.findViewById(R.id.iv_portrait);
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            viewHolder.tv_nickName = (TextView) convertView.findViewById(R.id.tv_nickName);
            viewHolder.tv_story_time = (TextView) convertView.findViewById(R.id.tv_story_time);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Comment comment = data.get(position);
        User user=comment.getUser();
        if(!TextUtils.isEmpty(user.getPortrait())){
            Picasso.with(context).load(Constants.PORTRAIT_PATH+"/"+user.getPortrait()).into(viewHolder.iv_portrait);
        }
        viewHolder.tv_content.setText(comment.getContent());
        viewHolder.tv_nickName.setText(user.getNickName());
        viewHolder.tv_story_time.setText(comment.getComment_time());

        return convertView;
    }

    static class ViewHolder {
        ImageView iv_portrait;
        TextView tv_content;
        TextView tv_nickName;
        TextView tv_story_time;
    }
}
