package com.ly.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ly.entity.Story;
import com.ly.entity.User;
import com.ly.happyreader.R;
import com.ly.utils.Constants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;

/**
 *碎碎念适配器
 */
public class StoryAdapter extends BaseAdapter {
    Context context;
    List<Story> dataList = new ArrayList<>();
    LayoutInflater inflater;

    public StoryAdapter(Context context, List<Story> dataList) {
        this.context = context;
        this.dataList = dataList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder =null;
        if(viewHolder == null){
            convertView = inflater.inflate(R.layout.fragment_story_listview_item,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_sex = (ImageView) convertView.findViewById(R.id.iv_sex);
            viewHolder.iv_story_img = (ImageView) convertView.findViewById(R.id.iv_story_img);
            viewHolder.tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            viewHolder.tv_nickName = (TextView) convertView.findViewById(R.id.tv_nickName);
            viewHolder.tv_story_time = (TextView) convertView.findViewById(R.id.tv_story_time);
            viewHolder.tv_like = (TextView) convertView.findViewById(R.id.tv_like);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Story story = dataList.get(position);
        User user = story.getUser();
        Log.e("info",story+" ===");
        //UTL图片配置
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
        //如果图片不存在，则控件不显示


        if(!TextUtils.isEmpty(story.getStory_img())){
            imageLoader.displayImage(Constants.IMG_PATH+"/"+story.getStory_img(),viewHolder.iv_story_img,options);
        }else {
            viewHolder.iv_story_img.setVisibility(View.GONE);
        }
        viewHolder.tv_title.setText(story.getTitle());
        viewHolder.tv_content.setText(story.getContent());
        viewHolder.tv_nickName.setText(user.getNickName());
        //0代表男
        if(user.getSex().endsWith("0")){
            viewHolder.iv_sex.setImageResource(R.drawable.registermale2x);
        }else {
            viewHolder.iv_sex.setImageResource(R.drawable.registerfemale2x);
        }
        viewHolder.tv_story_time.setText(story.getStory_time());
        viewHolder.tv_like.setText("喜欢"+story.getLike_count());
        viewHolder.tv_comment.setText("评论"+story.getComment_count());
        return convertView;
    }
    static  class ViewHolder{
        ImageView iv_story_img;
        ImageView iv_sex;
        TextView tv_title;
        TextView tv_content;
        TextView tv_nickName;
        TextView tv_story_time;
        TextView tv_like;
        TextView tv_comment;
    }
}
