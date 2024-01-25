package vaapps.photoslideshow.photovideomaker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class CustomSongsListAdapter extends RecyclerView.Adapter<CustomSongsListAdapter.CustomViewHolder> {

    Context context;
    List<String> tuneslisted;
    CustomSongsListAdapter.MyInterface myInterface;
    public int mSelectedItem = -1;
    private int checkedPosition = 0;

    public CustomSongsListAdapter(Context context, List<String> tuneslist, CustomSongsListAdapter.MyInterface myInterface) {
        this.context = context;
        this.tuneslisted = tuneslist;
        this.myInterface = myInterface;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customtunes_listview, parent, false);
        return new CustomSongsListAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        holder.Username.setText(tuneslisted.get(position));
        holder.UsernameRadioButton.setChecked(position == mSelectedItem);

    }

    @Override
    public int getItemCount() {
        return tuneslisted.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        RadioButton UsernameRadioButton;
        TextView Username;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            UsernameRadioButton = itemView.findViewById(R.id.usernameradiobutton);
            Username = itemView.findViewById(R.id.usernametxt);
            View.OnClickListener l = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myInterface.USERNAME_TAKER_FUNCTION(getAdapterPosition());
                    mSelectedItem = getAdapterPosition();
                    notifyItemRangeChanged(0, tuneslisted.size());
                }
            };
            itemView.setOnClickListener(l);
            UsernameRadioButton.setOnClickListener(l);


        }

        }


    public interface MyInterface {
        void USERNAME_TAKER_FUNCTION(int Val);

    }
}
