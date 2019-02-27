package com.ldt.musicr.ui.main.navigate.library;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


import com.ldt.musicr.ui.main.navigate.OnClickItemListener;
import com.ldt.musicr.ui.main.navigate.feature.FeaturePlaylistAdapter;
import com.ldt.musicr.util.uitool.AutoGeneratedPlaylistBitmap;
import com.ldt.musicr.ui.widget.BounceInterpolator;
import com.ldt.musicr.util.Tool;
import com.ldt.musicr.R;
import com.ldt.musicr.loader.LastAddedLoader;
import com.ldt.musicr.loader.PlaylistSongLoader;
import com.ldt.musicr.loader.SongLoader;
import com.ldt.musicr.loader.TopTracksLoader;
import com.ldt.musicr.model.Playlist;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.util.TimberUtils;
import com.makeramen.roundedimageview.RoundedDrawable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaylistChildAdapter extends RecyclerView.Adapter<PlaylistChildAdapter.ItemHolder> {
    private static final String TAG = "PlaylistAdapter";
    public ArrayList<Playlist> mPlaylistData = new ArrayList<>();
    public FeaturePlaylistAdapter.PlaylistClickListener mListener;
    private Context mContext;
    private boolean showAuto;
    private int songCountInt;
    private long firstAlbumID=-1;

    public PlaylistChildAdapter(Context mContext, boolean showAuto) {
        this.mContext = mContext;
        this.showAuto = showAuto;
    }
    public void setOnItemClickListener(FeaturePlaylistAdapter.PlaylistClickListener listener) {
        mListener = listener;
    }

    public void unBindAdapter() {
        mListener = null;
        mContext = null;
    }
    public void setData(List<Playlist> data) {
        Log.d(TAG, "setData: count = " + data.size());
        mPlaylistData.clear();
        if(data!=null) {
            mPlaylistData.addAll(data);
            notifyDataSetChanged();

        }
    }

    public void addData(ArrayList<Playlist> data) {
        if(data!=null) {
            int posBefore = mPlaylistData.size();
            mPlaylistData.addAll(data);
            notifyItemRangeInserted(posBefore,data.size());
        }
    }

    @NotNull
    @Override
    public ItemHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_playlist_child, viewGroup,false);
        return new ItemHolder(v);
    }
    @Override
    public void onBindViewHolder(@NotNull final ItemHolder itemHolder, int i) {
        // Lấy item Playlist thứ i
        final Playlist playlist = mPlaylistData.get(i);
        // tên tương ứng     itemHolder.mTitle.setText(playlist.name);

        // lấy uri của mImage
        Log.d(TAG, "one");


        new PlaylistBitmapLoader(this,playlist,itemHolder).execute();

        itemHolder.mImage.setTag(firstAlbumID);
        itemHolder.mTitle.setText(playlist.name);
        if(TimberUtils.isLollipop()) itemHolder.mImage.setTransitionName("transition_album_art"+i);
    }

    @Override
    public int getItemCount() {
        return  mPlaylistData.size();
    }


    public List<Song> getPlaylistWithListId(int position, long id) {
        if(mContext!=null) {
            firstAlbumID = -1;
            if(showAuto) {
                switch (position) {
                    case 0: return  LastAddedLoader.getLastAddedSongs(mContext);
                    case 1:
                        TopTracksLoader recentloader = new TopTracksLoader(mContext,TopTracksLoader.QueryType.RecentSongs);
                        return SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
                    case 2:
                        TopTracksLoader topTracksLoader = new TopTracksLoader(mContext,TopTracksLoader.QueryType.TopTracks);
                        return SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
                    default:
                        return PlaylistSongLoader.getSongsInPlaylist(mContext, id);
                }
            } else PlaylistSongLoader.getSongsInPlaylist(mContext, id);
        }
        return null;
    }

    private String getPlaylistArtUri(int position, long id) {
        if (mContext != null) {
            firstAlbumID = -1;
            if (showAuto) {
                switch (position) {
                    case 0:
                        List<Song> lastAddedSongs = LastAddedLoader.getLastAddedSongs(mContext);
                        songCountInt = lastAddedSongs.size();

                        if (songCountInt != 0) {
                            firstAlbumID = lastAddedSongs.get(0).albumId;
                            return TimberUtils.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";
                    case 1:
                        TopTracksLoader recentloader = new TopTracksLoader(mContext, TopTracksLoader.QueryType.RecentSongs);
                        List<Song> recentsongs = SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
                        songCountInt = recentsongs.size();

                        if (songCountInt != 0) {
                            firstAlbumID = recentsongs.get(0).albumId;
                            return TimberUtils.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";
                    case 2:
                        TopTracksLoader topTracksLoader = new TopTracksLoader(mContext, TopTracksLoader.QueryType.TopTracks);
                        List<Song> topsongs = SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
                        songCountInt = topsongs.size();

                        if (songCountInt != 0) {
                            firstAlbumID = topsongs.get(0).albumId;
                            return TimberUtils.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";
                    default:
                        List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(mContext, id);
                        songCountInt = playlistsongs.size();

                        if (songCountInt != 0) {
                            firstAlbumID = playlistsongs.get(0).albumId;
                            return TimberUtils.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";

                }
            } else {
                List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(mContext, id);
                songCountInt = playlistsongs.size();

                if (songCountInt != 0) {
                    firstAlbumID = playlistsongs.get(0).albumId;
                    return TimberUtils.getAlbumArtUri(firstAlbumID).toString();
                } else return "nosongs";
            }
        }
        return null;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnTouchListener{
        @BindView(R.id.title) TextView mTitle;
        @BindView(R.id.image) ImageView mImage;
        @BindView(R.id.over) View view_over;
        int currentColor=0;
        ItemHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            view_over.setOnClickListener(this);
            view_over.setOnTouchListener(this);
        }
        @Override
        public void onClick(View v) {
            //Todo: Navigate to playlist detail
            final android.view.animation.Animation myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce_slow);
            BounceInterpolator interpolator = new BounceInterpolator(0.1, 30);
            myAnim.setInterpolator(interpolator);
            myAnim.setDuration(350);
            itemView.startAnimation(myAnim);

            if(mListener!=null) {
                Bitmap bitmap = null;
                Drawable d = mImage.getDrawable();
                if(d instanceof BitmapDrawable) bitmap = ((BitmapDrawable)d).getBitmap();
                else if(d instanceof RoundedDrawable) bitmap = ((RoundedDrawable)d).getSourceBitmap();
                mListener.onClickPlaylist(mPlaylistData.get(getAdapterPosition()), bitmap);
            }
               //itemView.startAnimation(myAnim);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(currentColor!=Tool.getSurfaceColor()) {
                currentColor = Tool.getSurfaceColor();
                ((RippleDrawable) view_over.getBackground()).setColor(ColorStateList.valueOf(Tool.getSurfaceColor()));
            }
            return false;
        }
    }


    private static class PlaylistBitmapLoader extends AsyncTask<Void,Void,Bitmap> {
        private PlaylistChildAdapter mAdapter;
        private ItemHolder mItemHolder;
        private Playlist mPlaylist;

        PlaylistBitmapLoader(PlaylistChildAdapter adapter, Playlist playlist, ItemHolder item) {
            mAdapter = adapter;
            mItemHolder = item;
            mPlaylist = playlist;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mItemHolder.mImage.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(Void... v) {

            List<Song> l = mAdapter.getPlaylistWithListId(mItemHolder.getAdapterPosition(),mPlaylist.id);
            return AutoGeneratedPlaylistBitmap.getBitmap(mAdapter.mContext,l,false,false);
        }
    }

}