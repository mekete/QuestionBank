package net.kerod.android.questionbank.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.kerod.android.questionbank.R;

import de.hdodenhof.circleimageview.CircleImageView;

public   class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.ViewHolder> {
    //CircleImageView mCimgSelectedAvatar;
    private Context mContext;
    public final int[] mAvatarId;
    public final CircleImageView[] mAvatarArray;
    private SelectCallback selectCallback;
    public   int mSavedAvatarIndex;
    public interface SelectCallback {
        void onAttempt(CircleImageView[] imageArray, int selectedIndex);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mCimgAvatar;
        public View mRootView;

        public ViewHolder(View parent) {
            super(parent);
            mRootView = parent;
            mCimgAvatar = (CircleImageView) parent.findViewById(R.id.cimg_avatar);
        }
    }

    public AvatarAdapter(Context context, int[] avatarList ,int savedAvatarIndex) {
        mContext = context;
        mSavedAvatarIndex =   savedAvatarIndex;
        mAvatarId = avatarList;
        mAvatarArray = new CircleImageView[avatarList.length];
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_avatar, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mCimgAvatar.setImageResource(mAvatarId[position]);
        mAvatarArray[position] = holder.mCimgAvatar;
        if (mSavedAvatarIndex == position) {
            holder.mCimgAvatar.setBorderColor(ContextCompat.getColor( mContext,R.color.colorAccent));
            holder.mCimgAvatar.setBorderWidth(5);
        }
        holder.mCimgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectCallback != null) {
                    selectCallback.onAttempt(mAvatarArray, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAvatarId.length;
    }

    public SelectCallback getSelectCallback() {
        return selectCallback;
    }

    public void setSelectCallback(SelectCallback selectCallback) {
        this.selectCallback = selectCallback;
    }
}
