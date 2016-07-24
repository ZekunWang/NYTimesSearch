package com.zekunwang.nytimessearch.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zekunwang.nytimessearch.R;
import com.zekunwang.nytimessearch.models.Article;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by zwang_000 on 7/17/2016.
 */
public class ArticleArrayAdapter extends ArrayAdapter<Article> {

    public static final double RATIO = 1 / 4.5;
    double width;
    Drawable drawableModified;

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, R.layout.item_article, articles);

        // set size of each grid view item dynamically
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        width = metrics.widthPixels * RATIO;

        // popular drawable
        Drawable drawable = getContext().getResources().getDrawable(R.mipmap.ic_launcher);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        drawableModified = new BitmapDrawable(getContext().getResources(), Bitmap.createScaledBitmap(bitmap, (int) width, (int) width, false));
    }

    static class ViewHolder {
        @BindView(R.id.ivImage) ImageView ivImage;
        @BindView(R.id.tvTitle) TextView tvTitle;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get data item for position
        Article article = getItem(position);
        // check if existing view being reused
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvTitle.setText(article.getHeadLine());

        Picasso.with(getContext()).load(article.getThumbNail())
                .resize((int) width, (int) width)
                .placeholder(drawableModified)
                .centerCrop()
                .into(viewHolder.ivImage);

        return convertView;
    }
}
